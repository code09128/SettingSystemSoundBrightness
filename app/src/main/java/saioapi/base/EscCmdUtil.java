package saioapi.base;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import saioapi.OnEventListener;
import saioapi.base.XacCmdPrint.PrintWidth;
import saioapi.comm.Com;

class EscCmdUtil
{
    private final static String TAG = "EscCmdUtil";
    
    private final static byte ESC   = 0x1B;
    private final static byte GS    = 0x1D;
    private final static byte CR    = 0x0D;
    private final static byte LF    = 0x0A;
    private final static byte TAB   = 0x09;
    private final static byte DC4   = 0x14;
    private final static byte SO    = 0x1E;
    private final static byte CAN   = 0x18;
    //
    private int mMaxCmdLen = 1152;  //0x0480: default TP_48; For TP_72, the max length should be 1728 (0x06C0: 72x24) 
    private PrintWidth mPageWidth = PrintWidth.TP_48;
    private int mLeftMargin = 0;    //unit is font width
    private int mRightMargin = -1;  //unit is font width, -1 means the most right edge
    //
    private LineDecor mLD = null;   //current LineDecor
    private SegDecor mSD = null;    //current SegDecor
    private List<byte[]> mXacCmdList = null;
    //
    private boolean mIsDebug = false;
    
    EscCmdUtil()
    {
        mSD = new SegDecor();
        reset();
        
        //
        // Get the width of printer head, it will spend about 0.5 second
        //
        new QueryTpHeadThread().start();
    }
    
    synchronized List<byte[]> transform(byte[] cmd, int len)
    {
        if(null == cmd || 0 == len)
            return null;
        if(ESC == cmd[0] && (cmd.length < len || len < 2))
            return null;
        
        switch(cmd[0])
        {
            case ESC:
            {
                switch(cmd[1])
                {
                    case 'E':
                        mSD.isBold = true;
                        _dumpSD(mSD);
                        return null;
                    case 'F':
                        mSD.isBold = false;
                        _dumpSD(mSD);
                        return null;
                    case '4':
                        mSD.isItalic = true;
                        _dumpSD(mSD);
                        return null;
                    case '5':
                        mSD.isItalic = false;
                        _dumpSD(mSD);
                        return null;
                    //
                    // Set UnderLine
                    //
                    case '-':
                        if(len < 3 || cmd.length < 3)
                        {
                            Log.w(TAG, "Invalid command (" + _cmdToStr(cmd, len)  + ")");
                            break;
                        }
                        if(0 == cmd[2])
                            mSD.isUnderLine = false;
                        else if(1 == cmd[2])
                            mSD.isUnderLine = true;
                        else
                            Log.w(TAG, "Invalid underline parameter (" + cmd[2] + ")");
                        _dumpSD(mSD);
                        return null;
                    //
                    // Set Double Width
                    //
                    case 'W':
                        if(len < 3 || cmd.length < 3)
                        {
                            Log.w(TAG, "Invalid command (" + _cmdToStr(cmd, len)  + ")");
                            break;
                        }
                        if(0 == cmd[2])
                            mSD.isDoubleWidth = false;
                        else if(1 == cmd[2])
                            mSD.isDoubleWidth = true;
                        else
                            Log.w(TAG, "Invalid double width parameter (" + cmd[2] + ")");
                        _dumpSD(mSD);
                        return null;
                    //
                    // Set Double Height
                    //
                    case 'w':
                        if(len < 3 || cmd.length < 3)
                        {
                            Log.w(TAG, "Invalid command (" + _cmdToStr(cmd, len)  + ")");
                            break;
                        }
                        if(0 == cmd[2])
                            mSD.isDoubleHeight = false;
                        else if(1 == cmd[2])
                            mSD.isDoubleHeight = true;
                        else
                            Log.w(TAG, "Invalid double height parameter (" + cmd[2] + ")");
                        _dumpSD(mSD);
                        return null;
                    //
                    // Select Character Font
                    //
                    case 'M':
                        if(len < 3 || cmd.length < 3)
                        {
                            Log.w(TAG, "Invalid command (" + _cmdToStr(cmd, len)  + ")");
                            break;
                        }
                        if(0 == cmd[2])
                            mSD.fontInfo = FontInfo.SIZE_20;
                        else if(1 == cmd[2])
                            mSD.fontInfo = FontInfo.SIZE_15;
                        else
                            Log.w(TAG, "Invalid font size parameter (" + cmd[2] + ")");
                        _dumpSD(mSD);
                        return null;
                    //
                    // Set right margin
                    //
                    case 'Q':
                        if(len < 3 || cmd.length < 3)
                        {
                            Log.w(TAG, "Invalid command (" + _cmdToStr(cmd, len)  + ")");
                            break;
                        }
                        if(1 <= cmd[2] && cmd[2] <= (mPageWidth.intValue() / mSD.fontInfo.width()))
                        {
                            mRightMargin = cmd[2];
                            if(mLeftMargin >= mRightMargin)
                                mLeftMargin = mRightMargin - 1;
                            mLD.fontInfo = mSD.fontInfo;
                        }
                        else
                            Log.w(TAG, "Invalid right margin (" + cmd[2] + ")");
                        _dumpLD(mLD);
                        return null;
                    //
                    // Set left margin
                    //
                    case 'l':
                        if(len < 3 || cmd.length < 3)
                        {
                            Log.w(TAG, "Invalid command (" + _cmdToStr(cmd, len)  + ")");
                            break;
                        }
                        if(0 <= cmd[2] && cmd[2] < (mPageWidth.intValue() / mSD.fontInfo.width()))
                        {
                            mLeftMargin = cmd[2];
                            if(mRightMargin != -1 && mLeftMargin >= mRightMargin)
                                mRightMargin = mLeftMargin + 1;
                            mLD.fontInfo = mSD.fontInfo;
                        }
                        else
                            Log.w(TAG, "Invalid left margin (" + cmd[2] + ")");
                        _dumpLD(mLD);
                        return null;
                    //
                    // Advance print position vertically
                    //
                    case 'J':
                        if(mLD.size() > 0)
                            _lineDecorToXacGCmd();
                        mXacCmdList.add(cmd);
                        break;
                    //
                    // Select bit image
                    //
                    case '*':
                        if(mLD.size() > 0)
                            _lineDecorToXacGCmd();
                        _escGCmdToXacGCmd(cmd, len);
                        break;
                    //
                    // Initialize printer
                    //
                    case '@':
                        List<byte[]> l = null;
                        //
                        //flush, list needs to be swapped if flush. The list will be reset if reset() called
                        if(mLD.size() > 0)
                            _lineDecorToXacGCmd();
                        l = flush();
                        reset();
                        _dumpLD(mLD);
                        _dumpSD(mSD);
                        return l;
                    //
                    // Unsupported Command
                    //
                    default:
                        Log.w(TAG, "Unsupported ESC/P Command (" + _cmdToStr(cmd, len) + ")");
                        return null;
                }
                break;
            }//ESC
            //
            // XAC commands
            //
            case GS:
                //
                //Most XAC command will be handled in EscCmdPrint
                switch(cmd[1])
                {
                    //
                    // Not handle here
                    //
                    case CAN:
                    case 'a':
                        break;
                    //
                    // Print raster bit image
                    //
                    case 'v':
                        if(len < 8)
                            Log.w(TAG, "Unsupported ESC/P command. (" + _cmdToStr(cmd, len) + ")");
                        else
                        {
                            if(mLD.size() > 0)
                                _lineDecorToXacGCmd();
                            _xacPrintRasterImageCmdToXacGCmd(cmd, len);
                        }
                        break;
                    //
                    // Cut
                    //
                    case 'o':
                        if(mLD.size() > 0)
                            _lineDecorToXacGCmd();
                        mXacCmdList.add(cmd);
                        break;
                    default:
                        Log.w(TAG, "Unsupported ESC/P command. (" + _cmdToStr(cmd, len) + ")");
                        return null;
                }
                break;
            case DC4:
            case TAB:
            case SO:
                Log.w(TAG, "Unsupported ESC/P command. (" + _cmdToStr(cmd, len) + ")");
                break;
            case CR:
            case LF:
            default:
                if(mIsDebug) Log.d(TAG, "Suppose this command is text only. ("+ _cmdToStr(cmd, len) + ")");
                //
                StringTokenizer st = new StringTokenizer(new String(cmd), "\r\n", true);
                while(st.hasMoreTokens())
                {
                    String s = st.nextToken();
                    if("\r".equals(s) || "\n".equals(s))
                    {
                        _lineDecorToXacGCmd();
                    }
                    else
                    {
                        _strToLD(s);
                    }
                }
                break;
        }
        
        return flush();
    }
    
    public void reset()
    {
        mLeftMargin = 0;
        mRightMargin = -1;
        mLD = new LineDecor();
        mSD = new SegDecor();
        mXacCmdList = new ArrayList<byte[]>();
    }
    
    synchronized private List<byte[]> flush()
    {
        List<byte[]> l = mXacCmdList;
        mXacCmdList = new ArrayList<byte[]>();
        
        if(mIsDebug) Log.d(TAG, "flush " + l.size() + " cmd(s)\n\n");
        
        return l;
    }
    
    private void _strToLD(String s)
    {
        String tmp = s;
        
        if(mIsDebug) Log.d(TAG, "_strToLD(\"" + s + "\"): ");
        _dumpLD(mLD);
        
        while(null != tmp && 0 < tmp.length())
        {
            int cpos = 0;
            if(0 < mLD.size())
            {
                Seg lastSeg = mLD.get(mLD.size() - 1);
                cpos = lastSeg.startX + lastSeg.length;
            }
            int nReminderMarginChars = -1;
            if(-1 != mRightMargin)
            {
                nReminderMarginChars = mRightMargin - mLeftMargin;
                for(int i = 0; i < mLD.size(); i++)
                {
                    Seg seg = mLD.get(i);
                    if(seg.sd.isDoubleWidth)
                        nReminderMarginChars = nReminderMarginChars - (seg.content.length() << 1);
                    else
                        nReminderMarginChars = nReminderMarginChars - seg.content.length();
                }
            }
            int nReminderLineChars = (mPageWidth.intValue() - mLeftMargin * mLD.fontInfo.width() - cpos) / mSD.fontInfo.width();
            if(-1 != nReminderMarginChars && nReminderMarginChars < nReminderLineChars)
                nReminderLineChars = nReminderMarginChars;
            if(mSD.isDoubleWidth)
                nReminderLineChars = nReminderLineChars >> 1;
            int nCopiedChars = tmp.length();
            if(nReminderLineChars < nCopiedChars)
                nCopiedChars = nReminderLineChars;
            
            // For the case that the space between LM and RM is not enough to set any character, at last draw a character.
            // Or it will loop infinitely.
            boolean doLineDecorToXacGCmd = false;
            if(0 == mLD.size() && 0 == nCopiedChars && 0 < tmp.length())
            {
                nCopiedChars = 1;
                doLineDecorToXacGCmd = true;
            }
            
            //Log.d(TAG, "\tnReminderMarginChars=" + nReminderMarginChars + ", nReminderLineChars=" + nReminderLineChars + ", nCopiedChars=" + nCopiedChars);
            
            if(0 == nCopiedChars)
            {
                _lineDecorToXacGCmd();
            }
            else
            {
                Seg seg = new Seg();
                seg.copyDecorFrom(mSD);
                seg.startX = cpos;
                seg.length = nCopiedChars * seg.sd.getFontWidth();
                seg.content = tmp.substring(0, nCopiedChars);
                mLD.add(seg);
                //Log.d(TAG, "\tnew Seg(): content='"+seg.content.toString()+ "' startX=" + seg.startX + " length=" + seg.length);
                
                tmp = tmp.substring(nCopiedChars);
                
                if(doLineDecorToXacGCmd)
                    _lineDecorToXacGCmd();
            }
        }
    }
    
    Bitmap mBmp = null;
    private void _lineDecorToXacGCmd()
    {
        if(null == mLD)
            return;
        
        int height = 0;
        int baseline = 0;
        for(int i = 0, th = 0, tb = 0; i < mLD.size(); i++)
        {
            Seg s = mLD.get(i);
            th = s.sd.getHeight();
            tb = s.sd.getBaseline();
            if(th > height)
            {
                height = th;
                baseline = tb;
            }
            
            if(height == FontInfo.SIZE_40.height())
                break;
        }
        if(0 == height)
        {
            height = mLD.fontInfo.height();
            baseline = mLD.fontInfo.baseline();
        }
        
        if(null == mBmp)
        {
            mBmp = Bitmap.createBitmap(mPageWidth.intValue(), FontInfo.SIZE_40.height(), Bitmap.Config.ARGB_8888);
        }
        
        Canvas c = new Canvas(mBmp);
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setDither(false);
        
        //clean the bitmap
        p.setColor(Color.WHITE);
        c.drawRect(0, 0, mPageWidth.intValue(), height, p);
        
        if(mIsDebug) Log.d(TAG, "\n_lineDecorToXacGCmd(): h=" + height + " baseline=" + baseline);
        _dumpLD(mLD);
        
        for(int i = 0; i < mLD.size(); i++)
        {
            Seg s = mLD.get(i);
            _dumpSeg(s);
            //
            p.setTypeface(s.sd.fontInfo.tf);
            
            //font size and font scaleX will determine double_width and/or double_height
            p.setTextSize(s.sd.getSize());
            p.setTextScaleX(s.sd.getScaleX());
            
            p.setFakeBoldText(s.sd.isBold);
            p.setTextSkewX(s.sd.isItalic? -0.25f : 0f);
            p.setUnderlineText(s.sd.isUnderLine);
            p.setTextAlign(Paint.Align.LEFT);
            p.setColor(Color.BLACK);
            //
            c.drawText(s.content, mLeftMargin * mLD.fontInfo.width() + s.startX, baseline, p);
        }
        
        mXacCmdList.addAll(XacCmdPrint.bmpToCmds(mBmp, XacCmdPrint.Align.LEFT, false, mPageWidth, height));
        
        FontInfo fi = mLD.fontInfo;
        mLD = new LineDecor();
        mLD.fontInfo = fi;
    }
    
    private static byte[] _newGraphicCommand(PrintWidth printWidth, int len)
    {
        int nCommandHeader = 8;
        byte[] cmd = new byte[len + nCommandHeader];
        
        cmd[0] = 0x1B;
        cmd[1] = 0x2A;
        cmd[2] = (byte)(len & 0xFF);
        cmd[3] = (byte)(len >> 8);
        cmd[4] = 0x00;
        cmd[5] = 0x00;
        cmd[6] = 0x00;
        cmd[7] = (byte)printWidth.nativeInt;
        
        return cmd;
    }
    
    private void _escGCmdToXacGCmd(byte[] cmd, int len)
    {
        int idx = 5;
        int l = (cmd[3] & 0xFF) + ((cmd[4] & 0xFF) << 8);
        if(l + 5 > len)
        {
            Log.w(TAG, "Invalid command Length (" + _cmdToStr(cmd, len) + ")");
            return;
        }
        
        for(; idx + mMaxCmdLen <= l; idx = idx + mMaxCmdLen)
        {
            byte[] c = _newGraphicCommand(mPageWidth, mMaxCmdLen);
            System.arraycopy(cmd, idx, c, 8, mMaxCmdLen);
            mXacCmdList.add(c);
        }
        //reminder
        if(idx < l - 1)
        {
            byte[] c = _newGraphicCommand(mPageWidth, l - idx);
            System.arraycopy(cmd, idx, c, 8, l - idx);
            mXacCmdList.add(c);
        }
    }
    
    private void _xacPrintRasterImageCmdToXacGCmd(byte[] cmd, int len)
    {
        int w = (cmd[4] & 0xFF) + ((cmd[5] & 0xFF) << 8);
        int h = (cmd[6] & 0xFF) + ((cmd[7] & 0xFF) << 8);
        if(w * h + 8 > len)
        {
            Log.w(TAG, "Invalid command Length (" + _cmdToStr(cmd, len) + ")");
            return;
        }
        
        int maxHeightOfCmd = mMaxCmdLen / mPageWidth.nativeInt;
        int leftMarginByte = ((int)(mLeftMargin + 0.5) * mLD.fontInfo.width()) >> 3;
        int rightMarginByte = 0;
        if(-1 != mRightMargin)
        {
            rightMarginByte = ((mRightMargin * mLD.fontInfo.width()) < mPageWidth.intValue())?
                    ((mPageWidth.intValue() - (((int)(mRightMargin - 0.5)) * mLD.fontInfo.width())) >> 3) : 0;
        }
        int copyedImgBytes = mPageWidth.nativeInt - leftMarginByte - rightMarginByte;
        if(w < copyedImgBytes)
            copyedImgBytes = w;
        
        if(mIsDebug)
        {
            StringBuffer sb = new StringBuffer();
            sb.append("_xacPrintRasterImageCmdToXacGCmd(): w=").append(w).append(" h=").append(h)
                    .append(" LMB=").append(leftMarginByte).append(" RMB=").append(rightMarginByte)
                    .append(" CB=").append(copyedImgBytes).append(" CMD_H=").append(maxHeightOfCmd)
                    .append(" LM=").append(mLeftMargin).append(" RM=").append(mRightMargin).append(" MW=").append(mLD.fontInfo.width());
            Log.d(TAG, sb.toString());
        }
        
        int j = 0;
        for(; (j + maxHeightOfCmd) < h; j = j + maxHeightOfCmd)
        {
            byte[] c = _newGraphicCommand(mPageWidth, mMaxCmdLen);
            for(int i = 0, sh = 8 + j * w, dh = 8 + leftMarginByte; i < maxHeightOfCmd; i++, sh = sh + w, dh = dh + mPageWidth.nativeInt)
                System.arraycopy(cmd, sh, c, dh, copyedImgBytes);
            mXacCmdList.add(c);
        }
        //reminder
        if(j < h - 1)
        {
            byte[] c = _newGraphicCommand(mPageWidth, (h - j) * mPageWidth.nativeInt);
            for(int i = j, sh = 8 + j * w, dh = 8 + leftMarginByte; i < h; i++, sh = sh + w, dh = dh + mPageWidth.nativeInt)
                System.arraycopy(cmd, sh, c, dh, copyedImgBytes);
            mXacCmdList.add(c);
        }
    }
    
    static String _cmdToStr(byte[] cmd, int len)
    {
        if(null == cmd)
            return null;
        
        StringBuffer sb = new StringBuffer();
        sb.append("cmd(").append(Integer.toString(len)).append(":").append(Integer.toString(cmd.length)).append("){ ");
        for(int i= 0; i < len && i < cmd.length; i++)
        {
            if(i % 32 == 0 && i > 0)
                sb.append("\n\t\t");
            sb.append(String.format("%02X ", cmd[i]));
        }
        return sb.append("}").toString();
    }
    
    private void _dumpLD(LineDecor ld)
    {
        if(!mIsDebug)
            return;
        
        StringBuffer sb = new StringBuffer();
        
        if(null == ld)
        {
            sb.append("_dumpLD(): LineDecor is null.");
        }
        else
        {
            sb.append("<LineDecor");
            sb.append(" size=").append(ld.size());
            sb.append(" PW=").append(mPageWidth.nativeInt);
            sb.append(" LM=").append(mLeftMargin);
            sb.append(" RM=").append(mRightMargin);
            sb.append(" MW=").append(mLD.fontInfo.width());
            sb.append(" />");
        }
        
        Log.d(TAG, sb.toString());
    }
    
    private void _dumpSeg(Seg s)
    {
        if(!mIsDebug)
            return;
        
        StringBuffer sb = new StringBuffer();
        
        if(null == s)
        {
            sb.append("_dumpSeg(): Seg is null.");
        }
        else
        {
            sb.append("\t<Seg").append(" x=").append(s.startX).append(" len=").append(s.length).append(">");
            sb.append("\n\t\t").append(_descSD(s.sd));
            sb.append("\n\t\t").append("<content>").append(s.content).append("</content>");
            sb.append("\n\t</Seg>");
        }
        
        Log.d(TAG, sb.toString());
    }
    
    private void _dumpSD(SegDecor sd)
    {
        if(!mIsDebug)
            return;
        
        if(null == sd)
            Log.d(TAG, "_dumpSD(): SegDecor is null.");
        else
            Log.d(TAG, _descSD(sd));
    }
    
    private String _descSD(SegDecor sd)
    {
        StringBuffer sb = new StringBuffer();
        
        if(null == sd)
        {
            sb.append("_descSD(): SegDecor is null.");
        }
        else
        {
            sb.append("<SegDecor");
            sb.append(" font=").append(sd.fontInfo.width());
            sb.append(" B=").append(sd.isBold);
            sb.append(" I=").append(sd.isItalic);
            sb.append(" UL=").append(sd.isUnderLine);
            sb.append(" DW=").append(sd.isDoubleWidth);
            sb.append(" DH=").append(sd.isDoubleHeight);
            sb.append(" />");
        }
        
        return sb.toString();
    }
    
    class QueryTpHeadThread extends Thread
    {
        Com mCom = null;
        int mHandle = 0;
        String mVersion = null;
        
        @Override
        public void run()
        {
            //open
            mCom = new Com();
            mHandle = mCom.open((short) Com.DEVICE_USB0);
            if(mHandle == Com.ERR_OPERATION)
            {
                Log.e(TAG, "Failed to query TP_Head (" + String.format("0x%08X", mCom.lastError()) + ")");
                return;
            }
            if(mIsDebug) Log.i(TAG, "Printer handle: " + String.format("0x%08X", mHandle & 0xFFFFFFFF));
            
            //setup
            mCom.connect(mHandle, 115200, (byte)0, (byte)0, (byte)0, (byte) Com.PROTOCOL_XAC_VNG, null);
            mCom.setOnEventListener(mOnEventListener);
            
            //query
            byte[] sndCmd = {'R', '0'};
            //byte[] sndCmd = {'X', '1', 'B'};
            mCom.write(mHandle, sndCmd, sndCmd.length, 3000);
            
            //close - close Com in Listener
        }
        
        protected OnEventListener mOnEventListener = new OnEventListener()
        {
            @Override
            public void onEvent(int handle, int event)
            {
                switch(event)
                {
                    case Com.EVENT_CONNECT:
                        if(mIsDebug) Log.v(TAG, "Received event: EVENT_CONNECT");
                        break;
                    case Com.EVENT_DISCONNECT:
                        if(mIsDebug) Log.v(TAG, "Received event: EVENT_DISCONNECT");
                        break;
                    case Com.EVENT_EOT:
                        Log.w(TAG, "Received event: EVENT_EOT");
                        break;
                    case Com.EVENT_DATA_READY:
                        if(mIsDebug) Log.v(TAG, "Received event: EVENT_DATA_READY");
                        //
                        byte[] inBuffer = new byte[256];
                        int result = mCom.read(mHandle, inBuffer, inBuffer.length, 3000);
                        if(result == Com.ERR_OPERATION)
                        {
                            Log.e(TAG, "Read data error!");
                            break;
                        }
                        
                        if(mIsDebug)
                        {
                            StringBuffer sb = new StringBuffer();
                            sb.append("read data => ");
                            for(int i = 0; i < result; i++)
                            {
                                if(i % 32 == 0)
                                    sb.append("\n\t");
                                sb.append(String.format("%02X ", inBuffer[i] & 0xFF));
                            }
                            Log.v(TAG, sb.toString());
                        }
                        
                        //status
                        if((inBuffer[0] == 'X') && (inBuffer[1] == '1') && (inBuffer[2] == 'B'))
                        {
                            if(mIsDebug)
                            {
                                StringBuffer sb = new StringBuffer();
                                sb.append("Version: ").append(new String(inBuffer, 3, 8)).append(" \n")
                                        .append("Customer: ").append(new String(inBuffer, 11, 4)).append(" \n")
                                        .append("Model: ").append(new String(inBuffer, 15, 16));
                                Log.d(TAG, sb.toString());
                            }
                            
                            mVersion = new String(inBuffer, 3, 8);
                            _checkTpHead();
                        }
                        else if((inBuffer[0] == 'R') && (inBuffer[1] == '0'))
                        {
                            if(mIsDebug)
                            {
                                StringBuffer sb = new StringBuffer();
                                sb.append("Model: ").append(new String(inBuffer, 2, 16)).append(" \n")
                                        .append("Version: ").append(new String(inBuffer, 18, 8)).append(" \n")
                                        .append("Build: ").append(new String(inBuffer, 26, 8)).append(" \n")
                                        .append("SN: ").append(new String(inBuffer, 34, 16)).append(" \n")
                                        .append("CID: ").append(new String(inBuffer, 50, 4));
                                Log.d(TAG, sb.toString());
                            }
                            
                            mVersion = new String(inBuffer, 18, 8);
                            _checkTpHead();
                        }
                        else
                        {
                            Log.w(TAG, "Unknown VNG return");
                        }
                        
                        break;
                    default:
                        Log.w(TAG, "Received event: Unknown (" + event + ")");
                        break;
                }
                
                //close
                mCom.cancel(mHandle);
                mCom.close(mHandle);
                mHandle = 0;
            }
        };
        
        private void _checkTpHead()
        {
            if(null != mVersion)
            {
                if(mVersion.startsWith("231001"))// E200WU
                    mPageWidth = PrintWidth.TP_72;
                else if(mVersion.startsWith("231004"))// E200I
                    mPageWidth = PrintWidth.TP_72;
                else if(mVersion.startsWith("231003"))// E200T
                    mPageWidth = PrintWidth.TP_48;
                else
                    mPageWidth = PrintWidth.TP_48;
                
                if(PrintWidth.TP_72 == mPageWidth)
                {
                    mMaxCmdLen = 1728; //72x24
                }
                
                Log.i(TAG, "TP_Head detected: " + mPageWidth);
            }
        }
    }
    
}

class FontInfo
{
    static final FontInfo SIZE_20;   //12x24
    static final FontInfo SIZE_15;   //9x18
    static final FontInfo SIZE_40;   //24x48
    static final FontInfo SIZE_30;   //18x34
    static final FontInfo DEFAULT;   //12x24
    //
    Typeface tf = Typeface.MONOSPACE;
    private int size;  //size of Typeface
    private int w = 12;
    private int h = 24;
    private int baseline = 19;
    
    static
    {
        SIZE_20 = new FontInfo();
        SIZE_20.size = 20;
        SIZE_20.w = 12;
        SIZE_20.h = 24;
        SIZE_20.baseline = 19;
        //
        SIZE_15 = new FontInfo();
        SIZE_15.size = 15;
        SIZE_15.w = 9;
        SIZE_15.h = 17;
        SIZE_15.baseline = 13;
        //
        SIZE_40 = new FontInfo();
        SIZE_40.size = 40;
        SIZE_40.w = 24;
        SIZE_40.h = 48;
        SIZE_40.baseline = 38;
        //
        SIZE_30 = new FontInfo();
        SIZE_30.size = 30;
        SIZE_30.w = 18;
        SIZE_30.h = 34;
        SIZE_30.baseline = 27;
        //
        DEFAULT = SIZE_20;
    }
    
    int size()
    {
        return size;
    }
    
    int width()
    {
        return w;
    }
    
    int height()
    {
        return h;
    }
    
    int baseline()
    {
        return baseline;
    }
    
}

@SuppressWarnings("serial")
class LineDecor extends ArrayList<Seg>
{
    FontInfo fontInfo;
    
    public LineDecor()
    {
        super();
        fontInfo = FontInfo.DEFAULT;
    }
}

class Seg
{
    SegDecor sd = null;
    String content = null;
    //
    int startX = 0;
    int length = 0;
    
    public Seg()
    {
        sd = new SegDecor();
    }
    
    public boolean copyDecorFrom(SegDecor sd)
    {
        //This one should not be null.
        if(null == sd)
            return false;
        
        this.sd.copyDecorFrom(sd);
        
        return true;
    }
    
}

class SegDecor
{
    //style
    FontInfo fontInfo = FontInfo.DEFAULT;
    boolean isBold = false;
    boolean isItalic = false;
    boolean isUnderLine = false;
    boolean isDoubleWidth = false;
    boolean isDoubleHeight = false;
    private float scaleX = 1.0f; 
    
    public SegDecor()
    {
        reset();
    }
    
    public void reset()
    {
        fontInfo = FontInfo.DEFAULT;
        isBold = false;
        isItalic = false;
        isUnderLine = false;
        isDoubleWidth = false;
        isDoubleHeight = false;
        scaleX = 1.0f;
    }
    
    public boolean copyDecorFrom(SegDecor sd)
    {
        if(null == sd)
            return false;
        
        fontInfo = sd.fontInfo;
        isBold = sd.isBold;
        isItalic = sd.isItalic;
        isUnderLine = sd.isUnderLine;
        isDoubleWidth = sd.isDoubleWidth;
        isDoubleHeight = sd.isDoubleHeight;
        scaleX = sd.getScaleX();
        
        return true;
    }
    
    public int getHeight()
    {
        return getFontInfo().height();
    }
    
    public int getBaseline()
    {
        return getFontInfo().baseline();
    }
    
    public int getSize()
    {
        return getFontInfo().size();
    }
    
    public int getFontWidth()
    {
        return (int)(getFontInfo().width() * getScaleX());
        //return (isDoubleWidth)? (fontInfo.width() << 1) : fontInfo.width();
    }
    
    private FontInfo getFontInfo()
    {
        if(isDoubleHeight)
        {
            if(fontInfo == FontInfo.SIZE_20)
                return FontInfo.SIZE_40;
            else
                return FontInfo.SIZE_30;
        }
        else
        {
            return fontInfo;
        }
    }
    
    public float getScaleX()
    {
        if(isDoubleHeight)
        {
            if(isDoubleWidth)
                scaleX = 1.0f;
            else
                scaleX = 0.5f;
        }
        else
        {
            if(isDoubleWidth)
                scaleX = 2.0f;
            else
                scaleX = 1.0f;
        }
        
        return scaleX;
    }
    
}
