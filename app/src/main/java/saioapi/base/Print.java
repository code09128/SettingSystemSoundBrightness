//
//	Saio API, Printer class
//
package saioapi.base;

import saioapi.OnEventListener;

/**
 * This class provides control for SAIO specific thermal printer.
 */
public class Print implements PrintInterface
{
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
    
    /** Send the printer into pending status which will wait for the further action, 
     * either {@link #resume} or {@link #cancel}. */
    public static final short PRINT_JOB_CANCEL   = 0x00000000;
    
    /** Discard the remaining printer job automatically. */
    public static final short PRINT_JOB_PENDING  = 0x00000001;
    
    private boolean mbUseOldApi = false;
    private PrintHandler mPrintHandler = null;
    
    /**
     * The listener that receives notifications when an printer event is triggered.
     */
    private OnEventListener mOnEventListener = null;
    
    public Print()
    {
        mPrintHandler = new PrintHandler(this);
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
        if(mbUseOldApi)
            return native_open(dev);
        
        return mPrintHandler.attach(dev);
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
        if(mbUseOldApi)
            return native_close(handle);
        
        return mPrintHandler.detach();
    }
    
//    /**
//     * The method transfers the data to the printer spooler for printing.
//     * <p>Remarks:<br />
//     * This method might not result in the actual printing immediately, it queues 
//     * the data into printer spooler which will decides when start to print; either 
//     * the spooler reach the threshold value or {@link #close} method is issued.
//     * @param handle The service handle identifying the opened printer device.
//     * @param data The data array to be sent to the printer, which may contains 
//     *          text and escape sequence, a printer command always begins with escape 
//     *          character to drive the printer to perform a certain function. Please 
//     *          refer to Annex of this document for detail about the support list 
//     *          and their syntax. Now supports escape sequence print only.
//     * @return Return zero if the function succeeds else nonzero error code defined in class constants.
//     */
//    public int print(int handle, byte[] data)
//    {
//        return print(handle, data, data.length);
//    }
    
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
     * @return Return zero if the function succeeds else nonzero error code defined in class constants.
     */
    public int print(int handle, byte[] data, int len)
    {
        if(mbUseOldApi)
            return native_print(handle, data, len);
        
        return mPrintHandler.print(data, len);
    }
    
    /**
     * This method retrieves the current printer status. Which is defined as below table:
     * <table border=1>
     *   <thead><tr><th>BIT</th><th>Function</th><th>BIT=0</th><th>BIT=1</th></tr></thead>
     *   <tbody>
     *     <tr><td>0</td><td>Head temperature</td><td>Ok</td><td>Too high or too low</td></tr>
     *     <tr><td>1</td><td>Printer buffer full</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>2</td><td>Paper out</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>3</td><td>Power supply</td><td>Ok</td><td></td></tr>
     *     <tr><td>4</td><td>Printer is in use</td><td>Ready</td><td>Printing in progress</td></tr>
     *     <tr><td>5</td><td>Printer device online</td><td>Offline</td><td>Online</td></tr>
     *     <tr><td>6</td><td>Print job is pending</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>7</td><td>Cover Detect</td><td>Close</td><td>Open</td></tr>
     *     <tr><td>8 - 31</td><td>Unused</td><td></td><td></td></tr>
     *   </tbody>
     * </table>
     * @param handle The service handle identifying the opened printer device.
     * @return If successful, the printer status is returned, otherwise a {@link #ERR_OPERATION} 
     *          is returned and the method {@link #lastError} can be used to 
     *          indicate the error.
     */
    public int status(int handle)
    {
        if(mbUseOldApi)
            return native_status(handle);
        
        return mPrintHandler.status();
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
    public int manage(int handle,short ctrl)
    {
        if(mbUseOldApi)
            return native_manage(handle, ctrl);
        
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
        if(mbUseOldApi)
            return native_resume(handle);
        
        return mPrintHandler.resume();
    }
    
    /**
     * This method discards the currently remaining pending print job.
     * @param handle The service handle identifying the opened printer device.
     * @return Return zero if the function succeeds else nonzero error code defined in class constants.
     */
    public int cancel(int handle)
    {
        if(mbUseOldApi)
            return native_cancel(handle);
        
        return mPrintHandler.cancel();
    }
    
    /**
     * Retrieves the last error occurs on the printer operation.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public int lastError()
    {
        if(mbUseOldApi)
            native_lastError();
        
        return mPrintHandler.lastError();
    }
    
    /**
     * The method get called when the class received a notification event of the 
     * printer and the register method has been enabled.
     * @param handle The service handle identifying the opened printer device.
     * @param event Indicates the event defined in class constants.
     */
    public void listener(int handle, int event)
    {
        // 
        //	Call your real function to handle event here
        //
        if (null != mOnEventListener)
        {
            mOnEventListener.onEvent(handle, event);
        }
    }
    
    protected void finalize()
    {
        mPrintHandler = null;
    }
    
    private native int native_open(short dev);
    private native int native_close(int handle);
    private native int native_print(int handle, byte[] data, int len);
    private native int native_status(int handle);
    private native int native_manage(int handle,short ctrl);
    private native int native_resume(int handle);
    private native int native_cancel(int handle);
    private native int native_lastError();
}
