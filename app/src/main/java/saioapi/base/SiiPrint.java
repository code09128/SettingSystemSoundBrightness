//
//	Saio API, SiiPrint class
//
package saioapi.base;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class provides control for SAIO specific thermal printer.
 */
public class SiiPrint
{
    private static final String TAG = "SiiPrint";
    
    //
    //  Constants (Error code and Events)
    //
    /** The device is no longer available. The device is in use by another. */
    public static final int ERR_NOT_READY      = SimplePrint.ERR_NOT_READY;
    
    /** The device does not exist. */
    public static final int ERR_NOT_EXIST      = SimplePrint.ERR_NOT_EXIST;
    
    /** The thermal printer device has not been opened. */
    public static final int ERR_NOT_OPEN       = SimplePrint.ERR_NOT_OPEN;
    
    /** The inputted parameters are not valid. */
    public static final int ERR_INVALID_PARAM  = SimplePrint.ERR_INVALID_PARAM;
    
    /** The method is not supported for the given device. */
    public static final int ERR_NOT_SUPPORT    = SimplePrint.ERR_NOT_SUPPORT;
    
    /** The printer is busy to receive new print job now. */
    public static final int ERR_DEV_BUSY       = SimplePrint.ERR_DEV_BUSY;
    
    /** Unable to get method reference of the class listener. */
    public static final int ERR_NO_LISTENER    = SimplePrint.ERR_NO_LISTENER;
    
    /** Some unexpected internal error occurred. */
    public static final int ERR_IO_FAIL        = SimplePrint.ERR_IO_FAIL;
    
    /** Indicates to get detail error code by using {@link #lastError} method. */
    public static final int ERR_OPERATION      = SimplePrint.ERR_OPERATION;
    //
    private static final byte[] CMD_HW_RESET = {(byte)0x12, (byte)0x040};
    //
    private static int MAX_PRINTER_DEV = 10;
    private static int MAX_PRINTER_OPEN = 1;
    private static int mInterval = 100;
    private static boolean _isDebug = !true;
    //
    private SimplePrint mSimplePrint = null;
    private int mErr = 0;
    private int mOpenedDev = 0;
    private int[] mHandle = null;
    private byte[][] mAsb = null;
    private ArrayList<Byte>[] mRspDataList = null;
    private PrintReadThread[] mPrintReadThread = null;
    private SiiPrint.AutoStatusCallback mAsbCb = null;

    /**
     * Transfer bitmap to escape raster bit image command.
     * @param bmp The image to print.
     * @param mode The mode of printing raster format dot images. Please refer to ESC command set document for details.
     * <table border=1>
     *   <thead><tr><th>value</th><th>Mode</th><th>Vertical Dot Density</th><th>Horizontal Dot Density</th></tr></thead>
     *   <tbody>
     *     <tr><td>0, 48</td><td>Normal mode</td><td>203dpi</td><td>203dpi</td></tr>
     *     <tr><td>1, 49</td><td>Double width mode</td><td>203dpi</td><td>101dpi</td></tr>
     *     <tr><td>2, 50</td><td>Double height mode</td><td>101dpi</td><td>203dpi</td></tr>
     *     <tr><td>3, 51</td><td>Double height and width mode</td><td>101dpi</td><td>101dpi</td></tr>
     *   </tbody>
     * </table>
     * @return Escape raster bit image command.
     */
    final static public byte[] bmpToRasterBitImgCmd(Bitmap bmp, int mode)
    {
        if(null == bmp)
        {
            Log.e(TAG, "Bitmap is null");
            return null;
        }

        if((mode < 0 || 3 < mode) && (mode < 48 || 51 < mode))
        {
            Log.e(TAG, "Invalid mode: " + mode);
            return null;
        }

        long t = 0L;
        int w = bmp.getWidth() >> 3;
        if(bmp.getWidth() % 8 != 0)
            w++;
        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        if(_isDebug) Log.v(TAG, "bmp_size=" + bmp.getWidth() + "x" + bmp.getHeight());

        t = System.currentTimeMillis();
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        if(_isDebug) Log.v(TAG, "Spend " + (System.currentTimeMillis() - t) + " ms to get bmp pixels");

        //ARGB pixels to Monochrome bytes
        t = System.currentTimeMillis();
        byte[] raw = XacCmdPrint._pixelsARGB2Mono(pixels, bmp.getWidth(), bmp.getHeight(), w, XacCmdPrint.Align.DEFAULT, false);
        if(_isDebug) Log.v(TAG, "Spend " + (System.currentTimeMillis() - t) + " ms to convert pixels from ARGB to Mono");

        t = System.currentTimeMillis();
        //
        //max width of image is (256 * 256 + 256) * 8 = 526336 dots, so don't care about if the width is out of bound
        //max height of image is (16 * 256 + 256) = 4352 dots, only mind the height
        int copy_size = raw.length;
        int copy_h = bmp.getHeight();
        if(bmp.getHeight() >= 4352)
        {
            copy_size = bmp.getHeight() * 4352;
            copy_h = 4351;
        }
        byte[] cmd = new byte[copy_size + 8];
        cmd[0] = 0x1D;
        cmd[1] = 0x76;
        cmd[2] = 0x30;
        cmd[3] = (byte)mode;
        cmd[4] = (byte)(w & 0xFF);
        cmd[5] = (byte)(w >> 8);
        cmd[6] = (byte)(copy_h & 0xFF);
        cmd[7] = (byte)(copy_h >> 8);
        System.arraycopy(raw, 0, cmd, 8, copy_size);
        if(_isDebug) Log.v(TAG, "Spend " + (System.currentTimeMillis() - t) + " ms to copy raster bit image to cmd");
        return cmd;
    }

    /**
     * The auto status callback handler.
     */
    public class AutoStatusCallback
    {
        /**
         * The callback function that will be called when ASB changes.
         */
        public void onStatusChanged(byte[] status)
        {

        }
    }

    @SuppressWarnings("unchecked")
    public SiiPrint()
    {
        mErr = 0;
        mOpenedDev = 0;
        mSimplePrint = new SimplePrint();

        mHandle = new int[MAX_PRINTER_DEV];
        Arrays.fill(mHandle, 0);
        //
        mAsb =  new byte[MAX_PRINTER_DEV][4];
        for(int i = 0; i < MAX_PRINTER_DEV; i++)
            Arrays.fill(mAsb[i], (byte)0);
        //
        mRspDataList = new ArrayList[MAX_PRINTER_DEV];
        Arrays.fill(mRspDataList, null);
        //
        mAsbCb = new SiiPrint.AutoStatusCallback();
        //
        mPrintReadThread = new PrintReadThread[MAX_PRINTER_DEV];
        Arrays.fill(mPrintReadThread, null);
    }

    /**
     * This method creates a handle of the printer service. It always implements an exclusive open.
     * @param dev Logical ID of the printer service to open.
     * @return Upon successful completion, the service handle is returned to identify
     *          a printer service that subsequent method will reference this value, Otherwise the {@link #ERR_OPERATION}
     *          is returned and the method {@link #lastError} can be used to indicate the error code defined in
     *          class constants or in Linux ERRNO.
     */
    synchronized public int open(short dev)
    {
        if(MAX_PRINTER_DEV <= dev || 0 > dev)
        {
            mErr = ERR_INVALID_PARAM;
            return ERR_OPERATION;
        }
        if(MAX_PRINTER_OPEN <= mOpenedDev)
        {
            mErr = ERR_DEV_BUSY;
            return ERR_OPERATION;
        }
        if(0 != mHandle[dev])
        {
            mErr = ERR_DEV_BUSY;
            return ERR_OPERATION;
        }

        int ret = mSimplePrint.open(dev);
        if(ERR_OPERATION != ret)
        {
            mHandle[dev] = ret;

            //read out remainder data (ASB ...) from the input buffer
            _flush(ret);

            Arrays.fill(mAsb[dev], (byte)0x0);
            mRspDataList[dev] = new ArrayList<Byte>();
            (mPrintReadThread[dev] = new PrintReadThread(dev, ret)).start();
            mOpenedDev++;

            return ret;
        }
        else
        {
            mErr = ret;
            return ERR_OPERATION;
        }
    }

    /**
     * The method tries to close an opened printer service.
     * @param handle The service handle identifying the opened printer device.
     * @return zero if the function succeeds else nonzero error code defined in class constants.
     */
    synchronized public int close(int handle)
    {
        int dev = _getDevIdByHandle(handle);
        if(-1 == dev)
            return ERR_NOT_OPEN;

        //add a delay to read out asb
        try{
            Thread.sleep(100);}catch(Exception e){}

        if(null != mPrintReadThread[dev])
            mPrintReadThread[dev].doStop();

        //read out remainder data (ASB ...) from the input buffer
        _flush(handle);

        int ret = mSimplePrint.close(handle);
        //
        mPrintReadThread[dev] = null;
        mHandle[dev] = 0;
        Arrays.fill(mAsb[dev], (byte)0x0);
        mRspDataList[dev] = null;
        mOpenedDev--;

        return ret;
    }

    /**
     * Retrieves the last error occurs on the printer operation.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public int lastError()
    {
        return mErr;
    }

    /**
     * Retrieves the latest ASB.
     * @param handle The service handle identifying the opened printer device.
     * @return status. For details about ASB response, refer to the product technical reference. When disconnection
     *          from the printer is detected, the ASB value is returned as {0, 0, 0, 0}.
     */
    public byte[] getAutoStatus(int handle)
    {
        int dev = _getDevIdByHandle(handle);
        if(-1 == dev || null == mAsb[dev])
            return new byte[]{0, 0, 0, 0};

        byte[] st = new byte[4];
        synchronized(mAsb[dev])
        {
            System.arraycopy(mAsb[dev], 0, st, 0, mAsb[dev].length);
        }
        return st;
    }

    /**
     * Registers the callback function that will be called when ASB changes.
     * @param callback The registered callback function.
     */
    public void setAutoStatusCallback(SiiPrint.AutoStatusCallback callback)
    {
        mAsbCb = callback;
    }
    
    /**
     * The method transfers the data to the printer.
     * @param handle The service handle identifying the opened printer device.
     * @param data The data array to be sent to the printer.
     * @param len Number of bytes in buffer to be printed.
     * @return On success, the number of bytes written is returned (zero indicates nothing was written). On error, 
     *          {@link #ERR_OPERATION} is returned, and the method {@link #lastError} can be used to indicate the 
     *          error code defined in class constants or in Linux ERRNO.
     */
    public int print(int handle, byte[] data, int len)
    {
        int ret = mSimplePrint.write(handle, data, len);
        
        if(-1 == ret)
        {
            mErr = mSimplePrint.lastError();
        }
        else
        {
            if(len != ret)
            {
                if(_isDebug)
                    Log.w(TAG, "Cmd is not written fully.(" + len + " -> " + ret + ")");
            }
        }
        
        return ret;
    }
    
    /**
     * Retrieves response data from the printer.
     * @param handle The service handle identifying the opened printer device.
     * @param nRead Number of response data (bytes) that expects to be read up.
     * @param data The data array to read up.
     * @return zero if the function succeeds else nonzero error code defined in class constants.
     */
    public int getData(int handle, int nRead, byte[] data)
    {
        if(null == data || nRead > data.length)
            return ERR_INVALID_PARAM;
        
        int dev = _getDevIdByHandle(handle);
        if(-1 == dev)
            return ERR_NOT_EXIST;
        
        int sleep = 3000;
        for(int i = 0; i < sleep; i = i + mInterval)
        {
            if(null == mRspDataList[dev])
                return ERR_NOT_EXIST;
            
            synchronized(mRspDataList[dev])
            {
                if(nRead <= mRspDataList[dev].size())
                {
                    for(int j = 0; j < nRead; j++)
                    {
                        data[j] = mRspDataList[dev].get(0);
                        mRspDataList[dev].remove(0);
                    }
                    
                    if(_isDebug)
                    {
                        StringBuffer sb = new StringBuffer();
                        sb.append("getData(").append(nRead).append("): ");
                        
                        for(int j = 0; j < nRead; j++)
                        {
                            if(j % 32 == 0)
                                sb.append("\n\t\t");
                            sb.append(String.format("%02X ", data[j]));
                        }
                        sb.append("\n\t\tRemaining data: ").append(mRspDataList[dev].size()).append(" byte(s).\n");
                        
                        Log.d(TAG, sb.toString());
                    }
                    
                    return 0;
                }
            }
            
            try{
                Thread.sleep(mInterval);
            }catch(InterruptedException e){}
        }
        
        Log.w(TAG, "Timeout to getData(nRead): " + mRspDataList[dev].size());
        return ERR_NOT_EXIST;
    }
    
    /**
     * Drop the received respond data.
     * @param handle The service handle identifying the opened printer device.
     */
    public void resetRsp(int handle)
    {
        int dev = _getDevIdByHandle(handle);
        if(-1 == dev && null == mRspDataList[dev])
            return;
        
        synchronized(mRspDataList[dev])
        {
            mRspDataList[dev].clear();
        }
    }

    /**
     * Convert ROM version ID to a readable version string
     * @param rsp the 1 byte response that got from requesting ROM version ID
     * @return ROM version string
     */
    public static String getRomVerString(byte rsp)
    {
        return String.format("1.%d", rsp);
    }
    
    /**
     * Resets the printer (with using printer commands). The connection of the service will disconnect.
     * A constant waiting time may occur when hardware-reset succeed.
     * @param handle The service handle identifying the opened printer device.
     * @return On success, zero is returned. On error, {@link #ERR_OPERATION} is returned, and the method {@link #lastError} 
     *          can be used to indicate the error code defined in class constants or in Linux ERRNO.
     */
    public int reset(int handle)
    {
        int dev = _getDevIdByHandle(handle);
        if(-1 == dev)
        {
            mErr = ERR_NOT_EXIST;
            return ERR_OPERATION;
        }
        
        int ret = mSimplePrint.write(handle, CMD_HW_RESET, CMD_HW_RESET.length);
        if(0 == ret)
        {
            if(null != mPrintReadThread[dev])
                mPrintReadThread[dev].doStop();
            mPrintReadThread = null;
            //
            synchronized(mAsb[dev])
            {
                Arrays.fill(mAsb[dev], (byte)0x0);
            }
            //
            synchronized(mRspDataList[dev])
            {
                mRspDataList[dev].clear();
            }
            return 0;
        }
        else
        {
            mErr = mSimplePrint.lastError();
            return ERR_OPERATION;
        }
    }
    
    private int _getDevIdByHandle(int handle)
    {
        if(0 == handle)
            return -1;
        
        for(int i = 0; i < MAX_PRINTER_DEV; i++)
        {
            if(mHandle[i] == handle)
                return i;
        }
        
        return -1;
    }
    
    //Just flush input buffer
    private void _flush(int handle)
    {
        byte[] tmp = new byte[256];
        int read = 0;
        
        try{
            Thread.sleep(100);}catch(Exception e){}
        while((read = mSimplePrint.read(handle, tmp, tmp.length)) > 0)
        {
            Log.d(TAG, "Read out remainder data from input buffer: (" + read +")");
            if(_isDebug)
            {
                StringBuffer sb = new StringBuffer();
                for(int i = 0; i < read; i++)
                    sb.append(String.format(" %02X", tmp[i]));
                Log.d(TAG, "\t\t" + sb.toString());
            }
            try{
                Thread.sleep(100);}catch(Exception e){}
        }
    }
    
    //filter ASB, and store remainders to mRspDataList
    byte[] mTmpAsb = {0, 0, 0, 0};
    byte mTmpAsbRead = -1;
    private void _filterAsbAndStoreRspData(int dev, byte[] data, int size)
    {
        if(null == mRspDataList[dev])
            return;
        
        synchronized(mRspDataList[dev])
        {
            for(int i = 0; i < size; i++)
            {
                if(mTmpAsbRead == -1)
                {
                    if((data[i] & 0x10) == 0x10)
                    {
                        mTmpAsbRead = 0;
                        mTmpAsb[mTmpAsbRead] = data[i];
                    }
                    else
                    {
                        mRspDataList[dev].add(data[i]);
                    }
                }
                else
                {
                    mTmpAsbRead++;
                    mTmpAsb[mTmpAsbRead] = data[i];
                    if(mTmpAsbRead == 3)
                    {
                        synchronized(mAsb[dev])
                        {
                            System.arraycopy(mTmpAsb, 0, mAsb[dev], 0, mAsb[dev].length);
                        }
                        mTmpAsbRead = -1;
                        
                        //if(_isDebug)
                            Log.v(TAG, String.format("ASB {%02X, %02X, %02X, %02X}", mAsb[dev][0], mAsb[dev][1], mAsb[dev][2], mAsb[dev][3]));
                        
                        if(null != mAsbCb)
                        {
                            byte[] asb = new byte[4];
                            System.arraycopy(mAsb[dev], 0, asb, 0, mAsb[dev].length);
                            
                            mAsbCb.onStatusChanged(asb);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Read responded data, filter ASB, and then store remainders to mRspDataList
     */
    private class PrintReadThread extends Thread
    {
        private boolean _doStop = false;
        private int _handle = 0;
        private int _dev = 0;
        
        public PrintReadThread(int dev, int handle)
        {
            _handle = handle;
            _dev = dev;
        }
        
        @Override
        public void run()
        {
            while(true)
            {
                if(_doStop || 0 == mHandle[_dev])
                    break;
                
                //!!!DO NOT move out of while(true){...}, data appending to list may be overwriting if new data read at the same time!!!
                byte[] data = new byte[256];
                
                int ret = mSimplePrint.read(_handle, data, data.length);
                if(-1 == ret)
                {
                    mErr = mSimplePrint.lastError();//break immediately if error?
                    _doStop = true;
                    //if(_isDebug)
                        Log.e(TAG, String.format("Read error: 0x%08X, errno: 0x%08X", ret, mErr));
                }
                else if(ret > 0)
                {
                    if(_isDebug)
                    {
                        StringBuffer sb = new StringBuffer();
                        sb.append("Read: ").append(ret).append(" byte(s)");
                        
                        for(int i = 0; i < ret;  i++)
                        {
                            if(i % 32 == 0)
                                sb.append("\n\t");
                            sb.append(String.format("%02X ", data[i]));
                        }
                        sb.append("\n");
                        
                        Log.d(TAG, sb.toString());
                    }
                    
                    _filterAsbAndStoreRspData(_dev, data, ret);
                }
                
                try{
                    Thread.sleep(mInterval);
                }catch(InterruptedException e){}
            }
            
            Log.v(TAG, "SiiPrint.PrintReadThread ends.");
        }
        
        void doStop()
        {
            _doStop = true;
        }
        
    };
    
}
