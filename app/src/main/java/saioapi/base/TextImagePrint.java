package saioapi.base;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.Collection;
//import android.util.Log;

/**
 * This class provides text and image printing features for SAIO specific thermal printer.
 */
public class TextImagePrint extends XacCmdPrint
{
    //private final static String TAG = "TextImagePrint";
    
    /** Specify that print NORMAL text. */
    public final static int STYLE_DEFAULT      = 0x00000000;
    
    /** Specify that print bold text. */
    public final static int STYLE_BOLD         = 0x00000001;
    
    /** Specify that print italic text. */
    public final static int STYLE_ITALIC       = 0x00000002;
    
    /** Specify that print text with underline. */
    public final static int STYLE_UNDERLINE    = 0x00000004;
    
    /** Specify that print text with double height. */
    public final static int STYLE_DOUBLEHEIGHT = 0x00000008;
    
    /** Specify that the maximum of rolling dot-lines. */
    public final static int MAX_ROLL_DOTLINES = 128;
    
    private boolean _isAntiAlias = true;
    private boolean _isDither = false;
    //
    private float _fSkew = -0.25f;
    
    /**
     * Create a new TextToXacCmdPrint with default settings.
     */
    public TextImagePrint()
    {
        _isAntiAlias = true;
        _isDither = false;
    }
    
    /**
     * Print text with the specified font size and default settings. The text will be rendered and converted to 
     * print commands, and then pushed into print queue. Not be printed immediately. Use the method 
     * {@link #start(int) start} to print commands in queue.
     * @param text The text to be printed.
     * @param size Specify the font size.
     * @return Return the result that render and convert the text to print commands.
     */
    public boolean printText(String text, float size)
    {
        return printText(text, size, Typeface.DEFAULT);
    }
    
    /**
     * Print text with the specified font size and android.graphics.Typeface. The text will be rendered and 
     * converted to print commands, and then pushed into print queue. Not be printed immediately. Use the method 
     * {@link #start(int) start} to print commands in queue.
     * @param text The text to be printed.
     * @param size Specify the font size.
     * @param tf Specify the android.graphics.Typeface that can be android system or external one.
     * @return Return the result that render and convert the text to print commands.
     */
    public boolean printText(String text, float size, Typeface tf)
    {
        return printText(text, size, tf, STYLE_DEFAULT, Align.LEFT);
    }
    
    /**
     * Print text with the specified settings. The text will be rendered and converted to print commands, and then 
     * pushed into print queue. Not be printed immediately. Use the method {@link #start(int) start} to print 
     * commands in queue.
     * @param text The text to be printed.
     * @param size Specify the font size.
     * @param style Specify the font style.
     * @param align Specify the alignment of text.
     * @return Return the result that render and convert the text to print commands.
     */
    public boolean printText(String text, float size, int style, Align align)
    {
        return printText(text, size, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), style, align);
    }
    
    /**
     * Print text with the specified settings and android system typeface. The text will be rendered and converted 
     * to print commands, and then pushed into print queue. Not be printed immediately. Use the method 
     * {@link #start(int) start} to print commands in queue.
     * @param text The text to be printed.
     * @param size Specify the font size.
     * @param familyName Specify the name of android system typeface.
     * @param style Specify the font style.
     * @param align Specify the alignment of text.
     * @return Return the result that render and convert the text to print commands.
     */
    public boolean printText(String text, float size, String familyName, int style, Align align)
    {
        Typeface tf = Typeface.create(familyName, Typeface.NORMAL);
        if(null == tf)
            return false;
        
        return printText(text, size, tf, style, align);
    }
    
    /**
     * Print text with the specified settings and android.graphics.Typeface. The text will be rendered and converted 
     * to print commands, and then pushed into print queue. Not be printed immediately. Use the method 
     * {@link #start(int) start} to print commands in queue.
     * @param text The text to be printed.
     * @param size Specify the font size.
     * @param tf Specify the android.graphics.Typeface that can be android system or external one.
     * @param style Specify the font style.
     * @param align Specify the alignment of text.
     * @return Return the result that render and convert the text to print commands.
     */
    public boolean printText(String text, float size, Typeface tf, int style, Align align)
    {
        Paint p = new Paint();
        p.setTypeface(tf);
        if(0 < size)
            p.setTextSize(size);
        p.setFakeBoldText((style & STYLE_BOLD) == STYLE_BOLD);
        p.setTextSkewX(((style & STYLE_ITALIC) == STYLE_ITALIC)? _fSkew : 0f);
        p.setUnderlineText((style & STYLE_UNDERLINE) == STYLE_UNDERLINE);
        p.setAntiAlias(_isAntiAlias);
        p.setDither(_isDither);
        if(Align.LEFT == align)
            p.setTextAlign(Paint.Align.LEFT);
        else if(Align.CENTER == align)
            p.setTextAlign(Paint.Align.CENTER);
        else if(Align.RIGHT == align)
            p.setTextAlign(Paint.Align.RIGHT);
        else
            p.setTextAlign(Paint.Align.LEFT);
        
        return enqueue(_textToCmds(text, tf, p, ((style & STYLE_DOUBLEHEIGHT) == STYLE_DOUBLEHEIGHT), align, getColorMode(), getPrintWidth()));
    }

    //Added to support ROAP
    public Collection<byte[]> obtainTextCommandCollection(String text, float size)
    {
        return obtainTextCommandCollection(text, size, Typeface.DEFAULT);
    }

    public Collection<byte[]> obtainTextCommandCollection(String text, float size, Typeface tf)
    {
        return obtainTextCommandCollection(text, size, tf, STYLE_DEFAULT, Align.LEFT);
    }

    public Collection<byte[]> obtainTextCommandCollection(String text, float size, int style, Align align)
    {
        return obtainTextCommandCollection(text, size, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), style, align);
    }

    public Collection<byte[]> obtainTextCommandCollection(String text, float size, String familyName, int style, Align align)
    {
        Typeface tf = Typeface.create(familyName, Typeface.NORMAL);
        if (null == tf)
            return null;

        return obtainTextCommandCollection(text, size, tf, style, align);
    }

    public Collection<byte[]> obtainTextCommandCollection(String text, float size, Typeface tf, int style, Align align)
    {
        Paint p = new Paint();
        p.setTypeface(tf);
        if(0 < size)
            p.setTextSize(size);
        p.setFakeBoldText((style & STYLE_BOLD) == STYLE_BOLD);
        p.setTextSkewX(((style & STYLE_ITALIC) == STYLE_ITALIC)? _fSkew : 0f);
        p.setUnderlineText((style & STYLE_UNDERLINE) == STYLE_UNDERLINE);
        p.setAntiAlias(_isAntiAlias);
        p.setDither(_isDither);
        if(Align.LEFT == align)
            p.setTextAlign(Paint.Align.LEFT);
        else if(Align.CENTER == align)
            p.setTextAlign(Paint.Align.CENTER);
        else if(Align.RIGHT == align)
            p.setTextAlign(Paint.Align.RIGHT);
        else
            p.setTextAlign(Paint.Align.LEFT);

        return _textToCmds(text, tf, p, ((style & STYLE_DOUBLEHEIGHT) == STYLE_DOUBLEHEIGHT), align, getColorMode(), getPrintWidth());
    }

    static Bitmap _bmp = null;
    private static Collection<byte[]> _textToCmds(String text, Typeface tf, Paint p, boolean doubleHeight,
                                                  Align align, ColorMode colorMode, PrintWidth printWidth)
    {
        //width
        int width = printWidth.intValue(); //round
        
        //y
        float baseline = (int) (-p.ascent() + 0.5f); //ascent() is negative
        
        //TODO: double the height of text, instead of space; 
        //  may implement by Paint.setTextScaleX(), or scale the Bitmap, or Canvas.scale()
        //height
        int height = 0;
        if(doubleHeight)
            height = (int)(baseline + baseline);
        else
            height = (int)(baseline + p.descent() + 0.5f);
        
        //x, align
        int x = 0; //left
        if(Align.CENTER == align) //center
            x = width >> 1;
        else if(Align.RIGHT == align) //right
            x = width - 1;
        
        //render it!
        if(null == _bmp || (height > _bmp.getHeight()))
        {
            if(null != _bmp)
                _bmp.recycle();
            _bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(_bmp);
        if(ColorMode.BLACK_FONT_WHITE_BG == colorMode || ColorMode.DEFAULT == colorMode)
        {
            p.setColor(Color.WHITE);
            canvas.drawRect(0, 0, width, height, p);
            p.setColor(Color.BLACK);
            canvas.drawText(text, x, baseline, p);
        }
        else
        {
            p.setColor(Color.BLACK);
            canvas.drawRect(0, 0, width, height, p);
            p.setColor(Color.WHITE);
            canvas.drawText(text, x, baseline, p);
        }
        
        return bmpToCmds(_bmp, XacCmdPrint.Align.LEFT, false, printWidth, height);
    }
    
}
