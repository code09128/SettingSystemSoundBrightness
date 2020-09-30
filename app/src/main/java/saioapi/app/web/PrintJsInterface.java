package saioapi.app.web;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import saioapi.OnEventListener;
import saioapi.base.Print;
import saioapi.base.TextImagePrint;
import saioapi.base.XacCmdPrint;
import saioapi.base.XacCmdPrint.PrintWidth;

final public class PrintJsInterface implements SaioJsInterface
{
    final static public String ALIAS = "PrintJsInterface";
    //
    final static private String TAG = ALIAS;
    final static public int ERR_DECODE_OOM              = 0x0000EE00;
    final static public int ERR_DECODE_IO_FAIL          = 0x0000EE01;
    final static public int ERR_DECODE_MALFORMED_URL    = 0x0000EE02;
    
    final static private String URI_HTTP_PREFIX ="http";
    
    private JsCallback mCallback = null;
    private Context mContext = null;
    //
    private TextImagePrint mPrint = null;
    private int mPrintHandle = Print.ERR_OPERATION;
    private short mDevId = 0;
    private Boolean mDoPrint = false;
    private boolean mIsClosing = false;
    private int mPrintWidth = XacCmdPrint.PrintWidth.DEFAULT.intValue() >> 3;
    private Handler mHandler = new Handler();
    private int mEvent = 0;
    private String mJsListener = null;
    private boolean mIsHeadTemperatureAbnormal = false;
    private boolean mIsBufferFull = false;
    private boolean mIsPaperOut = false;
    private boolean mIsCoverOpened = false;
    private boolean mIsPowerSupplyOk = false;
    private boolean mIsPrinting = false;
    private boolean mIsPrinterOnline = false;
    private boolean mIsJobPending = false;
    private PrintJobThread mJob = null;
    private boolean mDebug = true;
    
    public PrintJsInterface(Context c)
    {
        mContext = c;
        mPrint = new TextImagePrint();
        mPrint.setOnEventListener(mOnEventListener);
        mPrintHandle = Print.ERR_OPERATION;
    }
    
    public void onDestory(){
        close();
    }
    
    @JavascriptInterface
    public int open()
    {
        synchronized(mPrint)
        {
            if(Print.ERR_OPERATION == mPrintHandle)
            {
                mPrintHandle = mPrint.open(mDevId);
                Log.i(TAG, "open(dev=" + mDevId + "): handle=" + String.format("0x%08X", mPrintHandle));
                
                //open error
                if(Print.ERR_OPERATION == mPrintHandle)
                {
                    Log.e(TAG, "\t=> lastError(): " + String.format("0x%08X", mPrint.lastError()));
                    return Print.ERR_OPERATION;
                }
                else
                {
                    Log.i(TAG, "\t=> successed");
                    return 0;
                }
            }
            else
            {
                Log.i(TAG, "already opened");
                return 0;
            }
        }
    }
    
    @JavascriptInterface
    public void close()
    {
        if(Print.ERR_OPERATION != mPrintHandle)
        {
            if(mIsClosing)
            {
                Log.v(TAG, "\t=> still closing ...\n\t(please reset the paper or cancel jobs if still not closed!)");
                return;
            }
            else
            {
                Log.v(TAG, "Closing ...");
                mIsClosing = true;
            }
            
            //
            // The priority of AsyncTask() is too low.
            // It's not easy to get the CPU resource to execute doInBackground() to close the printer.
            // So change to use the thread.
            //
            new Thread()
            {
                int h = 0;
                int ret = 0;
                
                @Override
                public void run()
                {
                    
                    synchronized(mPrint)
                    {
                        h = mPrintHandle;//just used for log
                        if(0 == (ret = mPrint.close(mPrintHandle)))
                            mPrintHandle = Print.ERR_OPERATION;
                    }
                    
                    mHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            Log.i(TAG, "close(handle=" + String.format("0x%08X", h) + "): " + String.format("0x%08X", ret));
                            if(0 == ret)
                            {
                                //callback to web: it's closed, but onEvent() will call status() again!
                                //mOnEventListener.onEvent(mPrintHandle, Print.EVENT_STATUS_CHG);
                            }
                            mIsClosing = false;
                        }
                    });
                }
            }.start();
        }
        else
        {
            Log.v(TAG, "Already closed");
        }
    }
    
    @JavascriptInterface
    public void setOnEventListener(String listener)
    {
        mJsListener = listener;
    }
    
    @JavascriptInterface
    public int lastError()
    {
        int errno = mPrint.lastError();
        if(mDebug)
            Log.d(TAG, String.format("lastError(): %08X", errno));
        return errno;
    }
    
    @JavascriptInterface
    public int manage(int ctrl)
    {
        int ret = mPrint.manage(mPrintHandle, (short)ctrl);
        if(mDebug)
            Log.d(TAG, String.format("manage(%d): %08X", ctrl, ret));
        
        return ret;
    }
    
    @JavascriptInterface
    public int resume()
    {
        int ret = mPrint.resume(mPrintHandle);
        if(mDebug)
            Log.d(TAG, String.format("resume(): %08X", ret));
        
        return ret;
    }
    
    @JavascriptInterface
    public int cancel()
    {
        if(mJob != null)
        {
            mJob.cancel();
        }
        
        int ret = mPrint.cancel(mPrintHandle);
        if(mDebug)
            Log.d(TAG, String.format("cancel(): %08X", ret));
        
        return ret;
    }
    
    @JavascriptInterface
    public int status()
    {
        int status = mPrint.status(mPrintHandle);
        _updatePrinterStatus(status);
        
        return status;
    }
    
    //return ERRNO directly
    @JavascriptInterface
    public int start()
    {
        synchronized(mDoPrint)
        {
            if(mDoPrint)
            {
                Log.v(TAG, "Previous job is not finished.");
                return Print.ERR_DEV_BUSY;
            }
            
            (mJob = new PrintJobThread()).start();
            return 0;
        }
    }
    
    @JavascriptInterface
    public void empty()
    {
        mPrint.emptyQueue();
    }
    
    Bitmap mBmp = null;
    
    //return ERRNO directly
    @JavascriptInterface
    synchronized public int printImage(String uri, String align)
    {
        int nAlign = Integer.valueOf(align);
        
        long t = System.currentTimeMillis();
        int ret = _loadImage(uri);
        if(mDebug)
        {
            Log.d(TAG, "Spend " + (System.currentTimeMillis() - t) + " ms to load image.");
            t = System.currentTimeMillis();
        }
        
        if(null == mBmp)
        {
            Log.e(TAG, "Failed to decode image - " + uri);
            return ret;
        }
        
        ret = mPrint.printImage(mBmp, XacCmdPrint.Align.values()[nAlign])? 0 : Print.ERR_OPERATION;
        if(mDebug)
        {
            Log.d(TAG, "Spend " + (System.currentTimeMillis() - t) + " ms to convert bmp to cmds.");
            Log.d(TAG, String.format("printImage(\"%s\", %d): ", uri, nAlign) + ret);
        }
        return ret;
    }
    
    //return ERRNO directly
    @JavascriptInterface
    public int printText(String text, String size, String typeface, String style, String align)
    {
        if(Print.ERR_OPERATION == mPrintHandle)
            return Print.ERR_NOT_OPEN;
        
        int nSize = Integer.valueOf(size);
        int nStyle = Integer.valueOf(style);
        int nAlign = Integer.valueOf(align);
        
        int ret = mPrint.printText(text, nSize, typeface, nStyle, XacCmdPrint.Align.values()[nAlign])? 0 : Print.ERR_OPERATION;
        if(mDebug)
            Log.d(TAG, String.format("printText(\"%s\", %d, Typeface=%s, %08X, %d): ", text, nSize, typeface, nStyle, nAlign) + ret);
        return ret;
    }
    
    @JavascriptInterface
    public int cut(String mode)
    {
        int m = Integer.valueOf(mode);
        
        int ret = mPrint.cut(mPrintHandle, XacCmdPrint.CutMode.values()[m]);
        if(mDebug)
            Log.d(TAG, String.format("cut(%d): %08X", m, ret));
        
        return ret;
    }
    
    @JavascriptInterface
    public int roll(String dotlines)
    {
        int d = Integer.valueOf(dotlines);
        
        int ret = mPrint.roll(mPrintHandle, d);
        if(mDebug)
            Log.d(TAG, String.format("cut(%d): %08X", d, ret));
        
        return ret;
    }
    
    @JavascriptInterface
    public int getPrintWidth()
    {
        return mPrint.getPrintWidth().intValue() >> 3;
    }
    
    @JavascriptInterface
    public void setPrintWidth(String width)
    {
        int w = Integer.valueOf(width);
        for(PrintWidth e: PrintWidth.values())
        {
            if((e.intValue() >> 3) == w)
            {
                mPrintWidth = w;
                mPrint.setPrintWidth(e);
                if(mDebug)
                    Log.d(TAG, String.format("setPrintWidth(%d)", mPrintWidth));
                return;
            }
        }
    }
    
    @JavascriptInterface
    public int getColorMode()
    {
        return mPrint.getColorMode().ordinal();
    }
    
    @JavascriptInterface
    public void setColorMode(String mode)
    {
        int m = Integer.valueOf(mode);
        if(mDebug)
            Log.d(TAG, String.format("setColorMode(%d)", m));
        mPrint.setColorMode(XacCmdPrint.ColorMode.values()[m]);
    }
    
    public void setCallback(JsCallback cb)
    {
        mCallback = cb;
    }
    
    private OnEventListener mOnEventListener = new OnEventListener()
    {
        @Override
        public void onEvent(int handle, int event)
        {
            /*
            int st = mPrint.status(mPrintHandle);
            String tmpStr = null;
            tmpStr = "listener(): handle=" + String.format("0x%08X",
            mPrintHandle) + ", event=" +
            String.format("0x%08X", event) + ", status=" +
            String.format("0x%08X", st);
            Log.d(TAG, tmpStr);
            
            // status error
            if (Print.ERR_OPERATION == st)
            {
                tmpStr = "\tlastError(): " + String.format("0x%08X",
                mPrint.lastError());
                Log.d(TAG, tmpStr);
            }
            //*/
            
            if(null != mCallback)
            {
                mEvent = event;
                mHandler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                mCallback.onLoadUrl("javascript:" + mJsListener + "('" + mEvent + "')");
                            }
                        });
            }
        }
    };
    
    private class PrintJobThread extends Thread
    {
        Boolean mDoStop = false;
        
        @Override
        public void run()
        {
            if(Print.ERR_OPERATION == mPrintHandle)
                return;
            
//            if(!mIsPrinterOnline)
//            {
//                _showMsg("\nPrinter is offline!\n");
//                return;
//            }
//            
            Log.v(TAG, "\nStart printing ...");
            mPrint.start(mPrintHandle);
            
            while(true)
            {
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException e){}
                //
                int st = mPrint.status(mPrintHandle);
                _updatePrinterStatus(st);
                
                if(!mIsPrinting || mDoStop)
                    break;
                
                Log.v(TAG, "Still printing ...");
            }
        }
        
        public void cancel()
        {
            synchronized(mDoStop)
            {
                mDoStop = true;
            }
        }
        
    };
    
    private synchronized void _updatePrinterStatus(int status)
    {
        if(Print.ERR_OPERATION == status)
            return;
        
        mIsHeadTemperatureAbnormal = ((status & 0x01) == 1);
        mIsPowerSupplyOk           = ((status & 0x08) >> 3 == 0);
        boolean isBufferFull       = ((status & 0x02) >> 1 == 1);
        boolean isPaperOut         = ((status & 0x04) >> 2 == 1);
        boolean isPrinting         = ((status & 0x10) >> 4 == 1);
        boolean isPrinterOnline    = ((status & 0x20) >> 5 == 1);
        boolean isJobPending       = ((status & 0x40) >> 6 == 1);
        boolean isCoverOpened      = ((status & 0x80) >> 7 == 1);
        //
        if(mIsHeadTemperatureAbnormal)
            Log.v(TAG, "Head temperature is too high or low.");
        //
        if(mIsBufferFull != isBufferFull)
        {
            mIsBufferFull = isBufferFull;
            if(mIsBufferFull)
                //_showMsg("\tPrinter buffer is full.");
                Log.d(TAG, "Printer buffer is full.");
        }
        //
        if(mIsPaperOut != isPaperOut)
        {
            mIsPaperOut = isPaperOut;
            if(mIsPaperOut)
                Log.v(TAG, "Out of paper.");
            else
                Log.v(TAG, "paper in.");
        }
        //
        if(mIsCoverOpened != isCoverOpened)
        {
            mIsCoverOpened = isCoverOpened;
            if(mIsCoverOpened)
                Log.v(TAG, "Cover is opened.");
            else
                Log.v(TAG, "Cover is closed.");
        }
        //
        if(!mIsPowerSupplyOk)
            Log.v(TAG, "Power supply is abnormal.");
        //
        if(mIsPrinting != isPrinting)
        {
            mIsPrinting = isPrinting;
            if(mIsPrinting)
                Log.v(TAG, "Start ...");
            else
                Log.v(TAG, "Stop ...");
        }
        //
        if(isPrinterOnline != mIsPrinterOnline)
        {
            mIsPrinterOnline = isPrinterOnline;
            if(mIsPrinterOnline)
                Log.v(TAG, "Printer is online.");
            else
                Log.v(TAG, "Printer is offline.");
        }
        //
        if(mIsJobPending != isJobPending)
        {
            mIsJobPending = isJobPending;
            if(isJobPending)
                Log.v(TAG, "Print job is pending.");
            else
                Log.v(TAG, "Print job is resumed.");
        }
    }
    
    private int _loadImage(String strPath)
    {
        //TODO: get exif rotation
        int m_nExifOrientation = 0;
        int mTPHeadWidth  = mPrintWidth << 3;
        int mTPHeadHeight = 72000;
        BitmapFactory.Options m_BitmapOpt = new BitmapFactory.Options();
        int ret = 0;
        
        Log.d(TAG, "Loading image: " + strPath + " (orientation: " + m_nExifOrientation + ")");
        
        boolean failToDecode = false;
        try
        {
            if(strPath.startsWith(URI_HTTP_PREFIX))
            {
                URL imageURL = new URL(strPath);
                HttpURLConnection connection = null;
                InputStream input = null;
                
                //1. Get width and height of the image
                connection = (HttpURLConnection)imageURL.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
                m_BitmapOpt.inSampleSize = 1;
                m_BitmapOpt.inJustDecodeBounds = true;
                mBmp = BitmapFactory.decodeStream(input, null, m_BitmapOpt);
                connection.disconnect();
                m_BitmapOpt.inJustDecodeBounds = false;
                
                /*
                //2. Re-scale during reading image
                m_BitmapOpt.inSampleSize = (int)Math.ceil(Math.max(
                        m_BitmapOpt.outWidth/(float)mTPHeadWidth, 
                        m_BitmapOpt.outHeight/(float)mTPHeadHeight));
                Log.d(TAG, "*** " + m_BitmapOpt.outWidth + "x" + m_BitmapOpt.outHeight + " (" + 
                        (m_BitmapOpt.outWidth/(float)mTPHeadWidth) + " : "  + 
                        (m_BitmapOpt.outHeight/(float)mTPHeadHeight) + ") => " + 
                        m_BitmapOpt.inSampleSize);
                //*/
                
                //3. Decode it!
                connection = (HttpURLConnection)imageURL.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
                mBmp = BitmapFactory.decodeStream(input, null, m_BitmapOpt);
                connection.disconnect();
                m_BitmapOpt.inSampleSize = 1;
            }
            else
            {
                //1. Get width and height of the image
                m_BitmapOpt.inSampleSize = 1;
                m_BitmapOpt.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(strPath, m_BitmapOpt);
                m_BitmapOpt.inJustDecodeBounds = false;
                
                /*
                //2. Re-scale during reading image
                m_BitmapOpt.inSampleSize = (int)Math.ceil(Math.max(
                        m_BitmapOpt.outWidth/(float)mTPHeadWidth, 
                        m_BitmapOpt.outHeight/(float)mTPHeadHeight));
                Log.d(TAG, "*** " + m_BitmapOpt.outWidth + "x" + m_BitmapOpt.outHeight + " (" + 
                        (m_BitmapOpt.outWidth/(float)mTPHeadWidth) + " : "  + 
                        (m_BitmapOpt.outHeight/(float)mTPHeadHeight) + ") => " + 
                        m_BitmapOpt.inSampleSize);
                //*/
                
                //3. Decode it!
                mBmp = BitmapFactory.decodeFile(strPath, m_BitmapOpt);
                m_BitmapOpt.inSampleSize = 1;
            }
            
            if(null == mBmp)
            {
                Log.e(TAG, "Cannot decode file: " + strPath);
                failToDecode = true;
            }
        }
        catch(OutOfMemoryError e)
        {
            Log.e(TAG, "Load image error (OutOfMemoryError): " + e.getMessage());
            //e.printStackTrace();
            ret = ERR_DECODE_OOM;
            failToDecode = true;
            
        }
        catch (MalformedURLException e)
        {
            Log.e(TAG, "Load image error (MalformedURLException): " + e.getMessage());
            //e.printStackTrace();
            ret = ERR_DECODE_MALFORMED_URL;
            failToDecode = true;
        }
        catch (IOException e)
        {
            Log.e(TAG, "Load image error (IOException): " + e.getMessage());
            //e.printStackTrace();
            ret = ERR_DECODE_IO_FAIL;
            failToDecode = true;
        }
        
        if(failToDecode)
        {
            if(null != mBmp)
            {
                if(!mBmp.isRecycled())
                {
                    mBmp.recycle();
                }
                mBmp = null;
                System.gc();
            }
            
            return ret;
        }
        
        /*
        //TODO: rotate it by exif orientation
        
        //scale it to fit
        int nWidth = mBmp.getWidth();
        int nHeight = mBmp.getHeight();
        if(nWidth > mTPHeadWidth || nHeight > mTPHeadHeight)
        {
            float fScale = Math.max(nWidth/(float)mTPHeadWidth, nHeight/(float)mTPHeadHeight);
            nWidth /= fScale;
            nHeight /= fScale;
            
            try
            {
                Bitmap bmpResize = Bitmap.createScaledBitmap(mBmp, nWidth, nHeight, true);
                mBmp.recycle();
                mBmp = null;
                mBmp = bmpResize;
                System.gc();
            }
            catch(OutOfMemoryError e)
            {
                Log.e(TAG, "**** SW Decode error on _loadImage (out of memory) ****");
                if(null != mBmp)
                {
                    if(!mBmp.isRecycled())
                    {
                        mBmp.recycle();
                    }
                    mBmp = null;
                    System.gc();
                }
                
                return ERR_DECODE_OOM;
            }
        }
        //*/
        
        return ret;
    }
    
}