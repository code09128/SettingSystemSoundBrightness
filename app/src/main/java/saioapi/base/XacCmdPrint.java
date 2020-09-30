package saioapi.base;

//import java.io.BufferedWriter;
//import java.io.FileWriter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

//import android.graphics.BitmapFactory;

/**
 * This class provides text and image printing features for SAIO specific thermal printer.
 */
public class XacCmdPrint extends Print
{
    private final static String TAG = "XacCmdPrint";
    
    /**
     * PrintWidth specifies the print head width of SAIO specific thermal printer. <br />
     * Default is {@link PrintWidth#TP_72}.
     */
    public enum PrintWidth
    {
        /** Specify the print width is 48 x 8 dots. */
        TP_48   (48),
        
        /** Specify the print width is 72 x 8 dots. */
        TP_72   (72),
        
        /** The default print width is {@link #TP_72}. */
        DEFAULT (72);
        
        private PrintWidth(int nativeInt)
        {
            this.nativeInt = nativeInt;
        }
        
        final int nativeInt;
        
        /**
         * Return number of dots per print line.
         * @return Return number of dots per print line.
         */
        public int intValue()
        {
            return nativeInt << 3;
        }
        
    }
    
    /**
     * ColorMode specifies print black text with white background or white text with black background. <br />
     * Now the setting is not supported for inverting the image. <br />
     * Default is {@link ColorMode#BLACK_FONT_WHITE_BG}. <br />
     * SAIO specific thermal printer can only print monochrome content.
     */
    public enum ColorMode
    {
        /** Specify that print black text with white background. */
        BLACK_FONT_WHITE_BG (0),
        
        /** Specify that print white text with black background. */
        WHITE_FONT_BLACK_BG (1),
        
        /** The default color mode is {@link ColorMode#BLACK_FONT_WHITE_BG}. */
        DEFAULT             (0);
        
        private ColorMode(int nativeInt)
        {
            this.nativeInt = nativeInt;
        }
        
        final int nativeInt;
    }
    
    /**
     * Align specifies how to align the text or image to the paper. <br />
     * Default is {@link Align#LEFT}.
     */
    public enum Align
    {
        /** Specify that print text or image to the center of paper. */
        CENTER  (0),
        
        /** Specify that print text or image to the left of paper. */
        LEFT    (1),
        
        /** Specify that print text or image to the right of paper. */
        RIGHT   (2),
        
        /** The default alignment is {@link Align#LEFT}. */
        DEFAULT (1);
        
        private Align(int nativeInt)
        {
            this.nativeInt = nativeInt;
        }
        
        final int nativeInt;
    }
    
    /**
     * CutMode specifies how to cut the paper. <br />
     * Default is {@link CutMode#FULLY}. <br />
     * Not all SAIO specific thermal printers support the cutter.
     */
    public enum CutMode
    {
        /** Specify that cut the paper fully. */
        FULLY   (0),
        
        /** Specify that cut the paper partially. */
        PARTIAL (1),
        
        /** Default is {@link CutMode#FULLY}. */
        DEFAULT (0);
        
        private CutMode(int nativeInt)
        {
            this.nativeInt = nativeInt;
        }
        
        final int nativeInt;
    }
    
    /** Specify that the maximum of rolling dot-lines. */
    public final static int MAX_ROLL_DOTLINES = 128;
    
    private static boolean _isDebug = false;
    //
    private ArrayList<byte[]> _cmds = null;
    //
    private PrintWidth _printWidth = PrintWidth.DEFAULT;
    private ColorMode _colorMode = ColorMode.DEFAULT;
    
    /**
     * Create a new XacCmdPrint with default settings.
     */
    public XacCmdPrint()
    {
        _cmds = new ArrayList<byte[]>();
        _printWidth = PrintWidth.DEFAULT;
        _colorMode = ColorMode.DEFAULT;
    }
    
    /**
     * Return the print width.
     * @return The print width.
     */
    public PrintWidth getPrintWidth()
    {
        return _printWidth;
    }
    
    /**
     * Set the print with.
     * @param printWidth The printWidth must be {@link PrintWidth#TP_48}, {@link PrintWidth#TP_72}, or 
     *          {@link PrintWidth#DEFAULT}.
     */
    public void setPrintWidth(PrintWidth printWidth)
    {
        if(PrintWidth.TP_48 == printWidth || PrintWidth.TP_72 == printWidth || PrintWidth.DEFAULT == printWidth)
            _printWidth = printWidth;
    }
    
    /**
     * Return the color mode.
     * @return The color mode.
     */
    public ColorMode getColorMode()
    {
        return _colorMode;
    }
    
    /**
     * Set the color mode.
     * @param colorMode The colorMode must be {@link ColorMode#BLACK_FONT_WHITE_BG}, {@link ColorMode#WHITE_FONT_BLACK_BG}, 
     *          or {@link ColorMode#DEFAULT}.
     */
    public void setColorMode(ColorMode colorMode)
    {
        if(ColorMode.BLACK_FONT_WHITE_BG == colorMode || ColorMode.WHITE_FONT_BLACK_BG == colorMode || ColorMode.DEFAULT == colorMode)
            _colorMode = colorMode;
    }
    
    /**
     * Clean the print queue.
     */
    public void emptyQueue()
    {
        _cmds.clear();
    }
    
    /**
     * Clean the print queue.
     */
    protected boolean enqueue(Collection<byte[]> cmds)
    {
        return _cmds.addAll(cmds);
    }
    
    /**
     * Start to print the commands in queue.
     * @param handle The service handle identifying the opened printer device.
     * @return Return zero if the function succeeds else nonzero error code defined in class constants.
     */
    public int start(int handle)
    {
        int size = _cmds.size();
        byte[] cmd = null;
        int err = 0;
        
        Log.v(TAG, "Start printing. Total " + size + " command(s).");
        
        //Consume the command queue
        for(int i = 0; i < size; i++)
        {
            cmd = _cmds.get(i);
            err = print(handle, cmd, cmd.length);
            if(err != 0)
                return err;
        }
        
        return 0;
    }
    
    /**
     * Feed the paper forward. It will execute immediately, not push the command into the queue.
     * @param handle The service handle identifying the opened printer device.
     * @param dotlines How many dot-lines to feed forward. The value should be 1 ~ {@link #MAX_ROLL_DOTLINES}.
     * @return Return zero if the function succeeds else nonzero error code defined in class constants.
     */
    public int roll(int handle, int dotlines)
    {
        if(dotlines <= 0 || MAX_ROLL_DOTLINES < dotlines)
        {
            Log.e(TAG, "invalied number of dotlines (" + dotlines + ")");
            return ERR_OPERATION;
        }
        
        byte[] cmd = new byte[]{0x1B, 0x4A, (byte)dotlines};
        return print(handle, cmd, cmd.length);
    }
    
    //00h: fully, 01h: partial
    /**
     * Cut the paper with the specified cutting mode. It will execute immediately, not push the command into the queue.
     * @param handle The service handle identifying the opened printer device.
     * @param mode Cut the paper fully or partially. The mode should be {@link CutMode#FULLY}, {@link CutMode#FULLY} , 
     *          or {@link CutMode#DEFAULT}.
     * @return Return zero if the function succeeds else nonzero error code defined in class constants.
     */
    public int cut(int handle, CutMode mode)
    {
        CutMode m = mode;
        if(CutMode.FULLY != mode && CutMode.PARTIAL != mode && CutMode.DEFAULT != mode)
        {
            Log.w(TAG, "Unknown cutting mode (" + mode + "), cut the paper as mode CUT_DEFAULT");
            m = CutMode.DEFAULT;
        }
        
        byte[] cmd = new byte[]{0x1D, 0x6F, (byte)m.nativeInt};
        return print(handle, cmd, cmd.length);
    }
    
//    public boolean printImage(String path, int align)
//    {
//        Bitmap bmp = BitmapFactory.decodeFile(path);
//        
//        return printImage(bmp, align);
//    }
//    
    /**
     * Print image with the specified alignment. The image will be converted to print commands, and then pushed
     * into print queue. Not be printed immediately. Use the method {@link #start(int) start} to print commands 
     * in queue.
     * @param bmp The android.graphics.Bitmap can be colored. And the print quality will be better if the bitmap is more opaque.
     * @param align Specify the alignment of image.
     * @return Return the result that convert the image to print commands.
     */
    public boolean printImage(Bitmap bmp, Align align)
    {
        return _cmds.addAll(bmpToCmds(bmp, align, (_colorMode == ColorMode.WHITE_FONT_BLACK_BG), _printWidth));
    }
    
    //Added to support ROAPI
    public Collection<byte[]> obtainImageCommandCollection(Bitmap bmp, Align align) {
        return bmpToCmds(bmp, align, (_colorMode == ColorMode.WHITE_FONT_BLACK_BG), _printWidth);
    }

    private static int nCommandHeader = 8;
    
    private static byte[] newGraphicCommand(PrintWidth printWidth, int len)
    {
        nCommandHeader = 8;
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
    
    protected static Collection<byte[]> bmpToCmds(Bitmap bmp, Align align, boolean invert, PrintWidth printWidth)
    {
        int h = 0;
        if(null != bmp)
        {
            h = bmp.getHeight();
        }
        
        return (Collection<byte[]>)bmpToCmds(bmp, align, invert, printWidth, h);
    }
    
    static int[] _pixels = null;
    protected static Collection<byte[]> bmpToCmds(Bitmap bmp, Align align, boolean invert, PrintWidth printWidth, int height)
    {
        if(null == bmp)
            return null;
        
        ArrayList<byte[]> cmds = null;
        long t = 0L;
        int nMaxCmdLen = 1152;//0x0480: default for TP_48 (48x24)
        if(PrintWidth.TP_72 == printWidth)
            nMaxCmdLen = 1728;//0x06C0: for TP_72 (72x24)
        if(_isDebug)
            Log.d(TAG, "nMaxCmdLen=" + nMaxCmdLen);
        
        if(height > bmp.getHeight())
        {
            Log.w(TAG, "The parameter height (" + height + ") is larger than the height of transfer bitmap (" + bmp.getHeight() + ")!");
            height = bmp.getHeight();
        }
        if(null == _pixels || (bmp.getWidth() * height > _pixels.length))
        {
            _pixels = new int[bmp.getWidth() * height];
        }
        if(_isDebug) Log.v(TAG, "bmp_size=" + bmp.getWidth() + "x" + height);
        
        t = System.currentTimeMillis();
        bmp.getPixels(_pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), height);
        if(_isDebug) Log.v(TAG, "Spend " + (System.currentTimeMillis() - t) + " ms to get bmp pixels");
        
        //ARGB pixels to Monochrome bytes
        t = System.currentTimeMillis();
        byte[] raw = _pixelsARGB2Mono(_pixels, bmp.getWidth(), height, printWidth.nativeInt, align, invert);
        if(_isDebug) Log.v(TAG, "Spend " + (System.currentTimeMillis() - t) + " ms to convert pixels from ARGB to Mono");
        
        if(null != raw)
        {
            cmds = new ArrayList<byte[]>();
            int nb = 0;
            byte[] cmd = null;
            int nCmdLen = 0;
            
            //split monochrome bytes to print commands
            t = System.currentTimeMillis();
            while(true)
            {
                nb = cmds.size() * nMaxCmdLen;
                if(nb >= raw.length)
                    break;
                
                cmd = null;
                if((raw.length - nb) >= nMaxCmdLen)
                {
                    cmd = newGraphicCommand(printWidth, nMaxCmdLen);
                    nCmdLen = nMaxCmdLen;
                    System.arraycopy(raw, nb, cmd, nCommandHeader, nCmdLen);
                }
                //reminder data
                else
                {
                    int r = raw.length % nMaxCmdLen;
                    cmd = newGraphicCommand(printWidth, r);
                    nCmdLen = ((r % printWidth.nativeInt) == 0)? r : r - (r % printWidth.nativeInt) + printWidth.nativeInt;
                    System.arraycopy(raw, nb, cmd, nCommandHeader, r);
                }
                
                cmds.add(cmd);
            }
            if(_isDebug) Log.v(TAG, "Spend " + (System.currentTimeMillis() - t) + " ms to sparate " + cmds.size() + " cmd(s)");

            /*
            //save cmds as pattern
            StringBuffer sb = new StringBuffer();
            for(int i = 0; i < cmds.size(); i++)
            {
                cmd = cmds.get(i);
                for(int j = 0; j < cmd.length; j++)
                {
                    sb.append(String.format("%02X ", cmd[j]));
                    if(j % 72 == 4)
                        sb.append(String.format("\n", cmd[j]));
                }
                sb.append("\n\n");
            }
            try{
                BufferedWriter writer = new BufferedWriter(new FileWriter("/storage/sdcard0/cmd-" + 
                            String.format("%04d", (System.currentTimeMillis() % 10000)) + ".txt"));
                writer.write(sb.toString());
                writer.close();
            }catch(Exception e){
                Log.e(TAG, e.getMessage());
            }
            //*/
        }
        else
            return null;
        
        return cmds;
    }
    
    static byte[] _raw = null;
    final protected static byte[] _pixelsARGB2Mono(int[] pixels, int width, int height, int monoWidth, Align align, boolean invert)
    {
        if(null == pixels)
            return null;
        
        //Log.d(TAG, "_pixelsARGB2Mono: pixels.len=" + pixels.length + ", w=" + width + ", h=" + height + 
        //      ", pwidth=" + monoWidth + ", align=" + align + ", invert=" + invert);
        if(null == _raw || (monoWidth * height != _raw.length))
        {
            _raw = new byte[monoWidth * height];
        }
        
        //Arrays.fill(raw, (byte)0x0);
        int maxMonoWidth = monoWidth << 3;
        int k = 0;
        byte m = 0;//mono pixel
        
        if(Align.LEFT == align || Align.DEFAULT == align)
        {
            for(int j = 0, pl = 0, rl = 0; j < height; j++, pl = pl + width, rl = rl + monoWidth)
            {
                //if(j < 35)Log.d(TAG, "==============j="+j);//DEBUG
                for(int i = 0, p = 0; i < monoWidth; i++)
                {
                    k = i << 3;
                    if(k + 8 <= width)
                        p = 8;
                    else if(k > width)
                        p = 0;
                    else
                        p = width - k;
                    
                    m = 0;
                    //ARGB pixel 2 monochrome bit
                    for(int r = 0, pr = pl + k; r < p; r++)
                    {
                        m = (byte)((m << 1) + _argb2mono(pixels[pr + r], invert));
                        
                        /*
                        //dump partial data for debug
                        if(j < 30 && i < 10)
                            Log.d(TAG, "Argb2Mono: " + String.format("%08X", pixels[pr + r]) + " => " + String.format("%08X", m));
                        //*/
                    }
                    
                    //case: width of image is smaller width of print
                    if(p == 0)
                        m = 0;
                    else if(p < 8)
                        m = (byte)(m << (8 - p));
                    
                    _raw[rl + i] = m;
                }
            }
        }
        else if(Align.CENTER == align)
        {
            //image is larger than or equals to paper
            if(width >= maxMonoWidth)
            {
                for(int j = 0, pl = (width - maxMonoWidth) >> 1, rl = 0; j < height; j++, pl = pl + width, rl = rl + monoWidth)
                {
                    for(int i = 0; i < monoWidth; i++)
                    {
                        k = i << 3;
                        
                        m = 0;
                        //ARGB pixel 2 monochrome bit
                        for(int r = 0, pr = pl + k; r < 8; r++)
                        {
                            m = (byte)((m << 1) + _argb2mono(pixels[pr + r], invert));
                        }
                        
                        _raw[rl + i] = m;
                    }
                }
            }
            //image is smaller than paper
            else
            {
                int emptyWidth = (maxMonoWidth - width) >> 1;
                for(int j = 0, pl = 0, rl = 0; j < height; j++, pl = pl + width, rl = rl + monoWidth)
                {
                    for(int i = 0, preByteEmpty = 0, postByteEmpty = 0, ps = 0; i < monoWidth; i++)
                    {
                        k = i << 3;
                        if(k + 8 <= emptyWidth)
                        {
                            preByteEmpty = 8;
                            ps = 0;
                            postByteEmpty = 0;
                        }
                        else if(k <= emptyWidth) //equals to if(k + 8 <= emptyWidth + 8)
                        {
                            preByteEmpty = emptyWidth - k;
                            ps = - preByteEmpty;
                            
                            //image width is small than 8
                            postByteEmpty = 8 - preByteEmpty - width;
                            if(postByteEmpty < 0)
                                postByteEmpty = 0;
                        }
                        else if(k + 8 <= emptyWidth + width)
                        {
                            preByteEmpty = 0;
                            ps = ps + 8;
                            postByteEmpty = 0;
                        }
                        else if(k <= emptyWidth + width) //equals to if(k + 8 <= emptyWidth + width + 8)
                        {
                            preByteEmpty = 0;
                            ps = ps + 8;
                            postByteEmpty = k + 8 - emptyWidth - width;
                        }
                        else
                        {
                            preByteEmpty = 0;
                            //ps = ps + 8;
                            postByteEmpty = 8;
                        }
                        
                        m = 0;
                        //pre-empty
                        if(preByteEmpty == 8)
                            m = 0;
                        else if(preByteEmpty > 0)
                            m = (byte)(m << preByteEmpty);
                        
                        //ARGB pixel 2 monochrome bit
                        for(int r = preByteEmpty, pr = pl + ps; r < 8 - postByteEmpty; r++)
                        {
                            m = (byte)((m << 1) + _argb2mono(pixels[pr + r], invert));
                        }
                        
                        //post-empty
                        if(postByteEmpty == 8)
                            m = 0;
                        else if(postByteEmpty > 0)
                            m = (byte)(m << postByteEmpty);
                        
                        _raw[rl + i] = m;
                    }
                }
            }
        }
        else if(Align.RIGHT == align)
        {
            //image is larger than or equals to paper
            if(width >= maxMonoWidth)
            {
                for(int j = 0, pl = width - maxMonoWidth, rl = 0; j < height; j++, pl = pl + width, rl = rl + monoWidth)
                {
                    for(int i = 0; i < monoWidth; i++)
                    {
                        k = i << 3;
                        
                        m = 0;
                        //ARGB pixel 2 monochrome bit
                        for(int r = 0, pr = pl + k; r < 8; r++)
                        {
                            m = (byte)((m << 1) + _argb2mono(pixels[pr + r], invert));
                        }
                        
                        _raw[rl + i] = m;
                    }
                }
            }
            //image is smaller than paper
            else
            {
                for(int j = 0, pl = 0, rl = 0; j < height; j++, pl = pl + width, rl = rl + monoWidth)
                {
                    for(int i = 0, byteEmpty = 0, ps = 0; i < monoWidth; i++)
                    {
                        k = i << 3;
                        if(k + 8 + width <= maxMonoWidth)
                            byteEmpty = 8;
                        else if(k + width <= maxMonoWidth) //equals to if(k + 8 + width <= maxMonoWidth + 8)
                        {
                            byteEmpty = maxMonoWidth - width - k;
                            ps = - byteEmpty;
                        }
                        else
                        {
                            byteEmpty = 0;
                            ps = ps + 8;
                        }
                        
                        m = 0;
                        //fill empty
                        if(byteEmpty == 8)
                            m = 0;
                        else if(byteEmpty > 0)
                            m = (byte)(m << byteEmpty);
                        
                        //fill image
                        for(int r = byteEmpty, pr = pl + ps; r < 8; r++)
                        {
                            m = (byte)((m << 1) + _argb2mono(pixels[pr + r], invert));
                        }
                        
                        _raw[rl + i] = m;
                    }
                }
            }
        }
        
        return _raw;
    }
    
    private static int _argb2mono(int pixel, boolean invert)
    {
        int mono = 0;
        //
        int a = Color.alpha(pixel);
        int b = Color.blue(pixel);
        int g = Color.green(pixel);
        int r = Color.red(pixel);
        //
        if(
//TODO：check the colour with alpha
//            //absolutely black, it's only suitable for black background and white word
//            pixel == 0x00000000 || 
            
            //adjust alpha to edge printing. 0(fully transparent) to 255 (completely opaque)
//            (a >= 0x80 && (int)(0.299 * r + 0.587 * g + 0.114 * b) <= 0x80)
            (a >= 0x80 && (299 * r + 587 * g + 114 * b) <= 128000)
        )
        {
            if(invert)
                mono = 0;
            else
                mono = 1;
        }
        else
        {
            if(invert)
                mono = 1;
            else
                mono = 0;
        }
        
        return mono;
    }
    
}
