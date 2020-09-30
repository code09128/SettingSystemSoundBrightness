//
//	Saio API, SimplePrint class
//
package saioapi.base;

/**
 * This class provides control for SAIO specific thermal printer.
 */
public class SimplePrint
{
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
    
    private PrintHandler mPrintHandler = null;
    
    public SimplePrint()
    {
        mPrintHandler = new PrintHandler(null);
    }
    
    //
    //    Methods
    //
    
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
        return mPrintHandler.native_popen(dev);
    }
    
    /**
     * The method tries to close an opened printer service.
     * @param handle The service handle identifying the opened printer device.
     * @return Zero if the function succeeds else nonzero error code defined in class constants.
     */
    public int close(int handle)
    {
        return mPrintHandler.native_pclose(handle);
    }
    
    /**
     * Retrieves the last error occurs on the printer operation.
     * @return Zero if there is no error else nonzero error code defined in class constants.
     */
    public int lastError()
    {
        return mPrintHandler.native_lastError();
    }
    
    /**
     * The method gets the data from the printer.
     * @param handle The service handle identifying the opened printer device.
     * @param data The data array read into.
     * @param len The number of bytes that attempts to read up.
     * @return On success, the number of bytes written is returned (zero indicates nothing was written). On error, 
     *          {@link #ERR_OPERATION} is returned, and the method {@link #lastError} can be used to indicate the 
     *          error code defined in class constants or in Linux ERRNO.
     */
    public int read(int handle, byte[] data, int len)
    {
        return mPrintHandler.native_read(handle, data, len);
    }
    
    /**
     * The method transfers the data to the printer.
     * @param handle The service handle identifying the opened printer device.
     * @param data The data array to be sent.
     * @param len The number of bytes in buffer to be printed.
     * @return On success, the number of bytes written is returned (zero indicates nothing was written). On error, 
     *          {@link #ERR_OPERATION} is returned, and the method {@link #lastError} can be used to indicate the 
     *          error code defined in class constants or in Linux ERRNO.
     */
    public int write(int handle, byte[] data, int len)
    {
        return mPrintHandler.native_write(handle, data, len);
    }
    
    protected void finalize()
    {
        mPrintHandler = null;
    }
    
}
