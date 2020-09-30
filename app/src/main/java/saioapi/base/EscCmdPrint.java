package saioapi.base;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.List;

import saioapi.OnEventListener;

/**
 * This class provides control for SAIO specific thermal printer.
 */
public class EscCmdPrint implements PrintInterface
{
    private final static String TAG = "EscCmdPrint";
    
    static
    {
        //
        //	Load the corresponding library
        //
        System.loadLibrary("SaioBase");
    }
    
    //
    //	Constants (Error code and Events)
    //
    /** The device is no longer available. The device is in use by another. */
    public static final int ERR_NOT_READY      = 0x0000E000;
    
    /** The device does not exist. */
    public static final int ERR_NOT_EXIST      = 0x0000E002;
    
    /** The thermal printer device has not been opened. */
    public static final int ERR_NOT_OPEN       = 0x0000E004;
    
    /** The inputted parameters are not valid. */
    public static final int ERR_INVALID_PARAM  = 0x0000E006;
    
    /** The method is not supported for the given device. */
    public static final int ERR_NOT_SUPPORT    = 0x0000E008;
    
    /** The printer is busy to receive new print job now. */
    public static final int ERR_DEV_BUSY       = 0x0000E00A;
    
    /** Unable to get method reference of the class listener. */
    public static final int ERR_NO_LISTENER    = 0x0000E00C;
    
    /** Some unexpected internal error occurred. */
    public static final int ERR_IO_FAIL        = 0x0000E00E;
    
    /** Indicates to get detail error code by using {@link #lastError} method. */
    public static final int ERR_OPERATION      = 0xFFFFFFFF;
    
    /** Signals the completion of the printer. */
    public static final int EVENT_STATUS_DONE  = 0x00000000;
    
    /** Signals the printer status changed. */
    public static final int EVENT_STATUS_CHG   = 0x00000001;
    
//    /** Send the printer into pending status which will wait for the further action, 
//     * either {@link #resume} or {@link #cancel}. */
//    private static final short PRINT_JOB_CANCEL   = 0x00000000;
//    
    /** Discard the remaining printer job automatically. */
    private static final short PRINT_JOB_PENDING  = 0x00000001;
    
    private final static byte GS  = 0x1D;
    private final static byte CAN = 0x18;
    
    private PrintHandler mPrintHandler = null;
    private EscCmdUtil mCmdUtil = null;
    private boolean mDoCallback = true;
    private int mStatus = EscCmdPrint.ERR_OPERATION;
    
    /**
     * The listener that receives notifications when an printer event is triggered.
     */
    private OnEventListener mOnEventListener = null;
    
    /**
     * Transfer bitmap to escape raster bit image command.
     * @param bmp The image to print.
     * @return Escape raster bit image command.
     */
    final static public byte[] bmpToRasterBitImgCmd(Bitmap bmp)
    {
        if(null == bmp)
            return null;
        
        boolean _debug = !true;
        long t = 0L;
        int w = bmp.getWidth() >> 3;
        if(bmp.getWidth() % 8 != 0)
            w++;
        int[] pixels = new int[bmp.getWidth() * bmp.getHeight()];
        if(_debug) Log.v(TAG, "bmp_size=" + bmp.getWidth() + "x" + bmp.getHeight());
        
        t = System.currentTimeMillis();
        bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
        if(_debug) Log.v(TAG, "Spend " + (System.currentTimeMillis() - t) + " ms to get bmp pixels");
        
        //ARGB pixels to Monochrome bytes
        t = System.currentTimeMillis();
        byte[] raw = XacCmdPrint._pixelsARGB2Mono(pixels, bmp.getWidth(), bmp.getHeight(), w, XacCmdPrint.Align.DEFAULT, false);
        if(_debug) Log.v(TAG, "Spend " + (System.currentTimeMillis() - t) + " ms to convert pixels from ARGB to Mono");
        
        t = System.currentTimeMillis();
        byte[] cmd = new byte[raw.length + 8];
        cmd[0] = 0x1D;
        cmd[1] = 0x76;
        cmd[2] = 0x30;
        cmd[3] = 0x00;
        cmd[4] = (byte)(w & 0xFF);
        cmd[5] = (byte)(w >> 8);
        cmd[6] = (byte)(bmp.getHeight() & 0xFF);
        cmd[7] = (byte)(bmp.getHeight() >> 8);
        System.arraycopy(raw, 0, cmd, 8, raw.length);
        if(_debug) Log.v(TAG, "Spend " + (System.currentTimeMillis() - t) + " ms to copy raster bit image to cmd");
        
        return cmd;
    }
    
    public EscCmdPrint()
    {
        mPrintHandler = new PrintHandler(this);
        mCmdUtil = new EscCmdUtil();
    }
    
    //
    //    Methods
    //
    
    /**
     * Get the registered Listener that handles the printer event.
     * @return The callback to be invoked with a printer event is triggered, 
     *         or null id no callback has been set.
     */
    public final OnEventListener getOnEventListener()
    {
        return mOnEventListener;
    }
    
    /**
     * Register a callback to be invoked when a printer event is triggered.
     * @param listener The callback that will be invoked.
     */
    public void setOnEventListener(OnEventListener listener)
    {
        mOnEventListener = listener;
    }
    
    /**
     * This method creates a handle of the printer service. It always implements an exclusive open.
     * @param dev Logical ID of the printer service to open.
     * @return Upon successful completion, the service handle is returned to identify 
     *          a printer service that subsequent method will reference this value, 
     *          Otherwise the {@link #ERR_OPERATION} is returned and the method 
     *          {@link #lastError} can be used to indicate the error.
     */
    public int open(short dev)
    {
        int ret = mPrintHandler.attach(dev);
        
        if(EscCmdPrint.ERR_OPERATION != ret)
        {
            manage(ret, EscCmdPrint.PRINT_JOB_PENDING);
        }
        
        return ret;
    }
    
    /**
     * The method tries to close an opened printer service. If there is some print 
     * job is not completed, it will wait for all done or the app may issue {@link #cancel} 
     * method to stop all pending operation before close.
     * @param handle The service handle identifying the opened printer device.
     * @return Return zero if the function succeeds else nonzero error code defined in class constants.
     */
    public int close(int handle)
    {
        return mPrintHandler.detach();
    }
    
    /**
     * The method transfers the data to the printer spooler for printing.
     * <p>Remarks:<br />
     * This method might not result in the actual printing immediately, it queues 
     * the data into printer spooler which will decides when start to print; either 
     * the spooler reach the threshold value or {@link #close} method is issued.
     * @param handle The service handle identifying the opened printer device.
     * @param data The data array to be sent to the printer, which may contains 
     *          text and escape sequence, a printer command always begins with escape 
     *          character to drive the printer to perform a certain function. Please 
     *          refer to Annex of this document for detail about the support list 
     *          and their syntax. Now supports escape sequence print only.
     * @param len Number of bytes in buffer to be printed.
     */
    public synchronized void print(int handle, byte[] data, int len)
    {
        if(2 <= len && 2 <= data.length)
        {
            //Log.d(TAG, "print() input_cmd => " + EscCmdUtil._cmdToStr(data, len));
            switch(data[0])
            {
                case GS:
                    switch(data[1])
                    {
                        //
                        // Status
                        //
                        case 'r':
                            if(!mDoCallback)
                                mStatus = mPrintHandler.status();
                            return;
                        //
                        // Cut
                        //
                        case 'o':
                            break;
                        //
                        // Enable/Disable Automatic Status Back (ASB)
                        //
                        case 'a':
                            if(len < 3 || data.length < 3)
                                Log.w(TAG, "Invalid command format (" + EscCmdUtil._cmdToStr(data, len)  + ")");
                            //
                            if(0 == data[2])
                            {
                                mDoCallback = false;
                                mStatus = 0;
                            }
                            else if(1 == data[2])
                                mDoCallback = true;
                            else
                                Log.w(TAG, "Invalid callback parameter (" + data[2]  + ")");
                            return;
                        //
                        // Cancel
                        //
                        case CAN:
                            cancel(handle);
                            return;
                        //
                        // Print raster bit image, bypass it!!!
                        //
                        case 'v':
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
        
        List<byte[]> l = mCmdUtil.transform(data, len);
        if(null == l)
            return;
        
        for(int i = 0; i < l.size(); i++)
        {
            byte[] cmd = l.get(i);
            mPrintHandler.print(cmd, cmd.length);
            //Log.d(TAG, EscCmdUtil._cmdToStr(cmd, cmd.length));
        }
        return;
    }
    
    /**
     * This method retrieves the printer status. If Auto Status Back has been enabled, the current status is 
     * returned. Else the status that last status command queried is returned (The returned value {0x00, 0x00} 
     * means no status command queried the status. Which is defined as below table:
     * <table border=1>
     *   <thead><tr><th>BIT</th><th>Function</th><th>BIT=0</th><th>BIT=1</th></tr></thead>
     *   <tbody>
     *     <tr><td>0</td><td>Head temperature</td><td>Ok</td><td>Too high or too low</td></tr>
     *     <tr><td>1</td><td>Power supply</td><td>Ok</td><td></td></tr>
     *     <tr><td>2</td><td>Printer buffer full</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>3</td><td>Paper out</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>4</td><td>Printer is in use</td><td>Ready</td><td>Printing in progress</td></tr>
     *     <tr><td>5</td><td>Cutter is in use</td><td>Ready</td><td>Action in progress</td></tr>
     *     <tr><td>6</td><td>RFU</td><td>TBD</td><td>TBD</td></tr>
     *     <tr><td>7</td><td>Busy</td><td>No (Pending)</td><td>Yes</td></tr>
     *     <tr><td>8</td><td>Cover Detect</td><td>Close</td><td>Open</td></tr>
     *     <tr><td>9 - 15</td><td>Unused</td><td></td><td></td></tr>
     *   </tbody>
     * </table>
     * @param handle The service handle identifying the opened printer device.
     * @return If successful, the printer status is returned, otherwise a null 
     *          is returned and the method {@link #lastError} can be used to 
     *          indicate the error.
     */
    public byte[] status(int handle)
    {
        if(mDoCallback)
            mStatus = mPrintHandler.status();
        
        if(EscCmdPrint.ERR_OPERATION == mStatus)
            return null;
        
        byte[] st = new byte[2];
        if((mStatus & 0x01) == 0x01) st[1] = (byte)(st[1] | 0x01);
        if((mStatus & 0x02) == 0x02) st[1] = (byte)(st[1] | 0x04);
        if((mStatus & 0x04) == 0x04) st[1] = (byte)(st[1] | 0x08);
        if((mStatus & 0x08) == 0x08) st[1] = (byte)(st[1] | 0x02);
        if((mStatus & 0x10) == 0x10) st[1] = (byte)(st[1] | 0x10);
        if((mStatus & 0x40) == 0x40)
            st[1] = (byte)(st[1] | 0x00);
        else
            st[1] = (byte)(st[1] | 0x80);
        
        if((mStatus & 0x80) == 0x80) st[0] = (byte)(st[0] | 0x01);
        
        return st;
    }
    
    /**
     * This function manages the thermal printer general behaviors when the printer 
     * was in pause status (out of paper and offline), the setting will take effect 
     * on next print job and be reset to default after system reboot.
     * <br />The behaviors control code:
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>Printer behaviors</th></tr></thead>
     *   <tbody>
     *     <tr><td>PRINT_JOB_PENDING</td><td>Send the printer into pending status which will wait for the further action, either resume or cancel</td></tr>
     *     <tr><td>PRINT_JOB_CANCEL</td><td>Discard the remaining printer job automatically</td></tr>
     *   </tbody>
     * </table>
     * @param handle The service handle identifying the opened printer device.
     * @param ctrl The behaviors control code to be used with printer on pause status.
     * @return Return zero if the function succeeds else nonzero error code defined in class constants.
     */
    private int manage(int handle,short ctrl)
    {
        return mPrintHandler.manage(ctrl);
    }
    
    /**
     * This method resumes the pending print job after the SAIO thermal printer 
     * has been stopped with paper out or printer device offline.
     * @param handle The service handle identifying the opened printer device.
     * @return Return zero if the function succeeds else nonzero error code defined in class constants.
     */
    public int resume(int handle)
    {
        return mPrintHandler.resume();
    }
    
    /**
     * This method discards the currently remaining pending print job.
     * @param handle The service handle identifying the opened printer device.
     * @return Return zero if the function succeeds else nonzero error code defined in class constants.
     */
    public int cancel(int handle)
    {
        return mPrintHandler.cancel();
    }
    
    /**
     * Retrieves the last error occurs on the printer operation.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public int lastError()
    {
        return mPrintHandler.lastError();
    }
    
    /**
     * The method get called when the class received a notification event of the printer, and both the register 
     *      method and Auto Status Back have been enabled. Auto Status Back has been enabled default.
     * @param handle The service handle identifying the opened printer device.
     * @param event Indicates the event defined in class constants.
     */
    public void listener(int handle, int event)
    {
        //
        //	Call your real function to handle event here
        //
        if (mDoCallback && null != mOnEventListener)
        {
            mOnEventListener.onEvent(handle, event);
        }
    }
    
    protected void finalize()
    {
        mPrintHandler = null;
    }
    
}
