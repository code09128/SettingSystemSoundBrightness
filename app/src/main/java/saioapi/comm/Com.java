//  Saio API, COM class
package saioapi.comm;
import saioapi.OnEventListener;
import saioapi.base.Misc;
import saioapi.util.Sys;

/**
 * This class defines common generic functionalities to be implemented by communication  services. It enumerates the list of the generic 
 * communication methods to be used by an application to communicate with a the device once a connection has been established. 
 * 
 * The XAC Peripheral Devices (XPD) are well supported too, in which this class isolates an application from details of the attached 
 * peripheral device by implementing all the related protocol stuff and work with the associated communication devices to provide 
 * end-to-end connectivity between device and high-level application.
 */
public class Com {

    static
    {
        System.loadLibrary("SaioBase");
    }
    
    //
    //  Constants (Error code and Events)
    //
    /** Indicates to get detail error code by using {@link #lastError} method. */
    public static final int ERR_OPERATION       = 0xFFFFFFFF;
    
    /** The device is no longer available. The device is in use by another. */
    public static final int ERR_NOT_READY       = 0x0000E000;
    
    /** The device does not exist. */
    public static final int ERR_NOT_EXIST       = 0x0000E002;
    
    /** The communication device has not been opened. */
    public static final int ERR_NOT_OPEN        = 0x0000E004;
    
    /** The inputted parameters are not valid. */
    public static final int ERR_INVALID_PARAM   = 0x0000E006;
    
    /** Unable to get method reference of the class listener. */
    public static final int ERR_NO_LISTENER     = 0x0000E008;
    
    /** An I/O error occurred while making a service operation. */
    public static final int ERR_IO_ERROR        = 0x0000E00A;
    
    /** The specified connection has not been established. */
    public static final int ERR_NO_CONNECTED    = 0x0000E00C;
    
    /** Fails to establish a XPD connection due to synchronization error. */
    public static final int ERR_XPD_SYNC        = 0x0000E00E;
    
    /** A communication timeout occurs on XPD connection. */
    public static final int ERR_XPD_TIMEOUT     = 0x0000E010;
    
    /** XPD Device is busy and unable to process the command now. */
    public static final int ERR_XPD_BUSY        = 0x0000E012;
    
    /** Indicates new data comes from incoming stream. */
    public static final int EVENT_DATA_READY    = 0x00000001;
    
    /** Signal the End Of Transmission event is received (VNG only). */
    public static final int EVENT_EOT           = 0x00000002;
    
    /** Signals the connection with host has been lost. */
    public static final int EVENT_DISCONNECT    = 0x00000003;
    
    /** Signals the device just gets connection with host. */
    public static final int EVENT_CONNECT       = 0x00000004;
    
    /** Raw communication. */
    public static final int PROTOCOL_RAW_DATA    = 0x00000000;
    
    /** XAC Venus device. */
    public static final int PROTOCOL_XAC_VENUS   = 0x00000001;

    /** XAC Cradle RFID contact-less reader. */
    public static final int PROTOCOL_XAC_CRADLE  = 0x00000002;

    /** XAC Check reader/Scanner device . */
    public static final int PROTOCOL_XAC_CHK     = 0x00000003;

    /** XAC legacy VNG device. */
    public static final int PROTOCOL_XAC_VNG     = 0x00000004;
    
    /** XAC VISA3 chip type contactless reader device. */
    public static final int PROTOCOL_XAC_VISA3   = 0x00000005;
    
    /** The id for communication device at USB 0. */
    public static final int DEVICE_USB0          = 0x00000000;
    
    /** The id for communication device at USB 1. */
    public static final int DEVICE_USB1          = 0x00000001;
    
    /** The id for communication device at USB 2. */
    public static final int DEVICE_USB2          = 0x00000002;
    
    /** The id for communication device at USB 3. */
    public static final int DEVICE_USB3          = 0x00000003;
    
    /** The id for communication device at USB 4. */
    public static final int DEVICE_USB4          = 0x00000009;
    
    /** The id for communication device at USB 5. */
    public static final int DEVICE_USB5          = 0x0000000A;
    
    /** The id for communication device at USB 6. */
    public static final int DEVICE_USB6          = 0x0000000B;
    
    /** The id for communication device at USB 7. */
    public static final int DEVICE_USB7          = 0x0000000C;
    
    /** The id for communication device at USB 8. */
    public static final int DEVICE_USB8          = 0x0000000D;
    
    /** The id for communication device at USB 9. */
    public static final int DEVICE_USB9          = 0x0000000E;
    
    /** The id for communication device at USB 10. */
    public static final int DEVICE_USB10         = 0x0000000F;
    
    /** The id for communication device at COM 0. */
    public static final int DEVICE_COM0          = 0x00000004;
    
    /** The id for communication device at COM 1. */
    public static final int DEVICE_COM1          = 0x00000005;
    
    /** The id for communication device at COM 2. */
    public static final int DEVICE_COM2          = 0x00000006;
    
    /** The id for communication device at COM 3. */
    public static final int DEVICE_COM3          = 0x00000007;
    
    /** The id for communication device at COM 4. */
    public static final int DEVICE_COM4          = 0x00000008;
    
    /** The id for extension board USB 0. */
    public static final int DEVICE_EXT_USB0      = 0x00000010;
    
    /** The id for extension board USB 1. */
    public static final int DEVICE_EXT_USB1      = 0x00000011;
    
    /** The id for printer at USB 0. */
    public static final int DEVICE_PRINTER0      = 0x00000012;
    
    /** The id for printer at USB 1. */
    public static final int DEVICE_PRINTER1      = 0x00000013;
    
    /** The id for printer at USB 2. */
    public static final int DEVICE_PRINTER2      = 0x00000014;
    
    /** The id for printer at USB 3. */
    public static final int DEVICE_PRINTER3      = 0x00000015;
    
    /** The id for printer at USB 4. */
    public static final int DEVICE_PRINTER4      = 0x00000016;
    
    /** The id for gadget serial port 0. */
    public static final int DEVICE_GS0           = 0x00000017;

    /** The id for communication device at ttyACM0. */
    public static final int DEVICE_ACM0           = 0x00000018;

    /** The id for communication device at ttyACM1. */
    public static final int DEVICE_ACM1           = 0x00000019;

    /** The id for communication device at ttyACM2. */
    public static final int DEVICE_ACM2           = 0x0000001A;

    /** The id for communication device at ttyACM3. */
    public static final int DEVICE_ACM3           = 0x0000001B;

    /** The id for communication device at ttyACM4. */
    public static final int DEVICE_ACM4           = 0x0000001C;

    /** The id for communication device at ttyACM5. */
    public static final int DEVICE_ACM5           = 0x0000001D;

    /** The id for communication device at ttyACM6. */
    public static final int DEVICE_ACM6           = 0x0000001E;

    /** The id for communication device at ttyACM7. */
    public static final int DEVICE_ACM7           = 0x0000001F;

    /** The id for communication device at ttyVSP0. */
    public static final int DEVICE_VSP0           = 0x00000020;

    /** The id for communication device at ttyVSP1. */
    public static final int DEVICE_VSP1           = 0x00000021;

    /** The id for communication device at ttyVSP2. */
    public static final int DEVICE_VSP2           = 0x00000022;

    /** The id for communication device at ttyVSP3. */
    public static final int DEVICE_VSP3           = 0x00000023;

    /** The id for extension board USB 2. */
    public static final int DEVICE_EXT_USB2      = 0x00000025;

    /** The id for extension board USB 3. */
    public static final int DEVICE_EXT_USB3      = 0x00000026;

    /** The id for extension board USB 4. */
    public static final int DEVICE_EXT_USB4      = 0x00000027;

    /** The id for extension board USB 5. */
    public static final int DEVICE_EXT_USB5      = 0x00000028;

    /** The device id for spi-interface printer (for SC20-series products with built-in printer only). */
    public static final int DEVICE_PRINTER_SPI0  = 0x00000029;
    
    /** The id for qualcomm smd11 (Shared Memory Driver). */
    public static final int DEVICE_SMD11         = 0x0000002A;    

    /** The control flag specifies that default number of bits per byte is CS8 */
    public static final int CFLAG_CSIZE_DEFAULT   = 0x00000000;

    /** The control flag specifies 5 bits per byte. This feature is not supported for DEVICE_COM0, DEVICE_COM1, DEVICE_COM2, DEVICE_COM3 of iMX6 series products. */
    public static final int CFLAG_CS5             = 0x00000001;

    /** The control flag specifies 6 bits per byte. This feature is not supported for DEVICE_COM0, DEVICE_COM1, DEVICE_COM2, DEVICE_COM3 of iMX6 series products. */
    public static final int CFLAG_CS6             = 0x00000010;

    /** The control flag specifies 7 bits per byte. */
    public static final int CFLAG_CS7             = 0x00000020;

    /** The control flag specifies 8 bits per byte. */
    public static final int CFLAG_CS8             = 0x00000030;

    /** The control flag specifies sending two stop bits rather than one.*/
    public static final int CFLAG_CSTOPB          = 0x00000040;

    /** The control flag specifies that Hang up the modem connection when the last process with the port open closes it */
    public static final int CFLAG_HUPCL           = 0x00000400;

    /** The control flag specifies enable parity generation and detection. */
    public static final int CFLAG_PARENB          = 0x00000100;

    /** The control flag specifies that use odd parity rather than even if CFLAG_PARENB is set. */
    public static final int CFLAG_PARODD          = 0x00000200;

    /** The control flag specifies that enable RTS/CTS (hardware) flow control. */
    public static final int CFLAG_CRTSCTS         = 0x80000000;

    /**
     * The listener that receives notifications when an COMM event is triggered.
     */
    private OnEventListener mOnEventListener = null;
    
    //
    //    Methods
    //
    
    /**
     * Get the registered Listener that handle the COM event.
     * @return The callback to be invoked with a COM event is triggered, 
     *         or null id no callback has been set.
     */
    public final OnEventListener getOnEventListener()
    {
        return mOnEventListener;
    }
    
    /**
     * Register a callback to be invoked when a COM event is triggered.
     * @param listener The callback that will be invoked.
     */
    public void setOnEventListener(OnEventListener listener)
    {
        mOnEventListener = listener;
    }
    
    /**
     * This method gets Build-In Epp device id.
     * @return Return Build-In Epp device id.
     */
    public static int getBuiltInEppDevId(){
        int epp_id;

        if (
                Sys.getCpuFamily() == Sys.CPU_FAMILY_QCOM ||
                Sys.getCpuFamily() == Sys.CPU_FAMILY_QCOM_SC20)
            epp_id = DEVICE_COM1;
        else if (Sys.getCpuFamily() == Sys.CPU_FAMILY_ALLWINNER)
            epp_id = DEVICE_COM3;
        else//CPU_FAMILY_IMX6
            epp_id = DEVICE_COM2;

        return epp_id;
    }

    /**
     * This method gets Build-In printer device id.
     * @return Return Build-In printer device id.
     */
    public static int getBuiltInPrinterDevId(){
        int printer_id;

        if (Sys.getCpuFamily() == Sys.CPU_FAMILY_IMX6 )
            if (getProductName().contains("200I"))
                printer_id = ERR_OPERATION;
            else
                printer_id = DEVICE_PRINTER0;
        else if (Sys.getCpuFamily() == Sys.CPU_FAMILY_ALLWINNER)
            if (
                    getProductName().contains("T305")||
                    getProductName().contains("AP-10")||
                    getProductName().contains("AM-10")||
                    getProductName().contains("SUD") //7/12
            )
                printer_id = ERR_OPERATION;
            else
                printer_id = DEVICE_PRINTER0;
        else if (Sys.getCpuFamily() == Sys.CPU_FAMILY_QCOM_SC20)
            printer_id = DEVICE_PRINTER_SPI0;
        else //Sys.CPU_FAMILY_QCOM
            printer_id = ERR_OPERATION;

        return printer_id;
    }

    /**
     * This method gets card reader device id.
     * @return Return card reader device id.
     */
    public static int getCardReaderDevId(){
        int reader_id;

        if (getProductName().contains("T305"))
            reader_id = DEVICE_COM2;
        else
            reader_id = ERR_OPERATION;

        return reader_id;
    }

    /**
     * This method creates a handle of the communication service. It always implements an exclusive open.
     * @param dev Logical ID of the selected communication device where the connection to be established, the following predefined values 
     *        defines available ID.
     * <table border=1>
     *   <thead><tr><th>Values</th><th>Status</th></tr></thead>
     *   <tbody>
     *     <tr><td>0</td><td>XAC USB communication device</td></tr>
     *     <tr><td>1 to 9</td><td>COM1 to COM9 serial communication device</td></tr>
     *     <tr><td>other</td><td>Unused</td></tr>
     *   </tbody>
     * </table>
     * @return Upon successful completion, the service handle is returned to identify a communication that subsequent method will 
     *         reference this value, Otherwise a {@link #ERR_OPERATION} is returned and the method {@link #lastError} can be used to 
     *         indicate the error.
     */
    public native int open(short dev);
    
    /**
     * This method tries to close an opened communication service. If there is some operation is not completed, it will wait for all 
     * done or the app may issue {@link #cancel} method to stop all pending operation before close.
     * @param handle The service handle identifying the opened communication device.
     * @return Return zero if the function succeeds else nonzero error code defined in class constants.
     */
    public native int close(int handle);
    
    /**
     * This method establishes a connection to device over a specified communication device (serial, USB etc.) with a given protocol. 
     * @param handle The service handle identifying the opened communication device.
     * @param baud Specifies the baud rate at which the serial communication device operation. It is an actual baud rate value. Ignored 
     *         by USB device.
     * @param data_size The number of bits in the bytes transmitted and received. Ignored by USB device.
     * @param stop_bit The number of stop bits to be used. Ignored by USB device.
     * @param parity Specifies the parity scheme to be used. Ignored by USB device.
     * @param protocol The communication protocol family defined in below class constants:
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>Protocols</th></tr></thead>
     *   <tbody>
     *     <tr><td>PROTOCOL_RAW_DATAPROTOCOL_RAW_DATA</td><td>Raw communication</td></tr>
     *     <tr><td>PROTOCOL_XAC_VENUS</td><td>XAC Venus device</td></tr>
     *     <tr><td>PROTOCOL_XAC_CRADLE</td><td>XAC Cradle RFID contact-less reader</td></tr>
     *     <tr><td>PROTOCOL_XAC_CHK</td><td>XAC Check reader/Scanner device </td></tr>
     *     <tr><td>PROTOCOL_XAC_VNG</td><td>XAC legacy VNG device</td></tr>
     *     <tr><td>PROTOCOL_XAC_VISA3</td><td>XAC VISA3 chip type contactless reader device</td></tr>
     *   </tbody>
     * </table>
     * @param extra The data array containing extra connect information, ignored by now.
     * @return Return zero if the function succeeds else nonzero error code defined in class constants.
     */
    public native int connect(int handle, int baud, byte data_size, byte stop_bit, byte parity, int protocol, byte[] extra);

    /**
     * This method establishes a connection to device over a specified communication device (serial, USB etc.) with a given protocol. 
     * @param handle The service handle identifying the opened communication device.
     * @param baud Specifies the baud rate at which the serial communication device operation. It is an actual baud rate value. Ignored 
     *         by USB device.
     * @param data_size The number of bits in the bytes transmitted and received. Ignored by USB device.
     * @param stop_bit The number of stop bits to be used. Ignored by USB device.
     * @param parity Specifies the parity scheme to be used. Ignored by USB device.
     * @param protocol The communication protocol family defined in below class constants:
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>Protocols</th></tr></thead>
     *   <tbody>
     *     <tr><td>PROTOCOL_RAW_DATAPROTOCOL_RAW_DATA</td><td>Raw communication</td></tr>
     *     <tr><td>PROTOCOL_XAC_VENUS</td><td>XAC Venus device</td></tr>
     *     <tr><td>PROTOCOL_XAC_CRADLE</td><td>XAC Cradle RFID contact-less reader</td></tr>
     *     <tr><td>PROTOCOL_XAC_CHK</td><td>XAC Check reader/Scanner device </td></tr>
     *     <tr><td>PROTOCOL_XAC_VNG</td><td>XAC legacy VNG device</td></tr>
     *     <tr><td>PROTOCOL_XAC_VISA3</td><td>XAC VISA3 chip type contactless reader device</td></tr>
     *   </tbody>
     * </table>
     * @param extra The data array containing extra connect information, ignored by now.
     * @return Return zero if the function succeeds else nonzero error code defined in class constants.
     */
    public int connect(int handle, int baud, int data_size, int stop_bit, int parity, int protocol, byte[] extra)
    {
         return connect2(handle,baud, data_size, stop_bit, parity, 0, protocol, extra);
    }
    
    /**
     * This method establishes a connection to device over a specified communication device (serial, USB etc.) with a given protocol. 
     * @param handle The service handle identifying the opened communication device.
     * @param baud Specifies the baud rate at which the serial communication device operation. It is an actual baud rate value. Ignored 
     *         by USB device.
     * @param data_size The number of bits in the bytes transmitted and received. Ignored by USB device.
     * @param stop_bit The number of stop bits to be used. Ignored by USB device.
     * @param parity Specifies the parity scheme to be used. Ignored by USB device.
     * @param flow_control Specifies the flow control. Now only support HW flow control {@link #CFLAG_CRTSCTS}, default is 0. Ignored by non-serial device.
     * @param protocol The communication protocol family defined in below class constants:
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>Protocols</th></tr></thead>
     *   <tbody>
     *     <tr><td>PROTOCOL_RAW_DATAPROTOCOL_RAW_DATA</td><td>Raw communication</td></tr>
     *     <tr><td>PROTOCOL_XAC_VENUS</td><td>XAC Venus device</td></tr>
     *     <tr><td>PROTOCOL_XAC_CRADLE</td><td>XAC Cradle RFID contact-less reader</td></tr>
     *     <tr><td>PROTOCOL_XAC_CHK</td><td>XAC Check reader/Scanner device </td></tr>
     *     <tr><td>PROTOCOL_XAC_VNG</td><td>XAC legacy VNG device</td></tr>
     *     <tr><td>PROTOCOL_XAC_VISA3</td><td>XAC VISA3 chip type contactless reader device</td></tr>
     *   </tbody>
     * </table>
     * @param extra The data array containing extra connect information, ignored by now.
     * @return Return zero if the function succeeds else nonzero error code defined in class constants.
     */
    public int connect(int handle, int baud, int data_size, int stop_bit, int parity, int flow_control, int protocol, byte[] extra)
    {
         return connect2(handle,baud, data_size, stop_bit, parity, flow_control, protocol, extra);
    }
    
    /**
     * Read data from the open input stream, the thread will be blocked until something is received before timer has expired. 
     * @param handle The service handle identifying the opened communication device.
     * @param data The byte array to receive data from to the device.
     * @param len The number of byte to be read from the communication device.
     * @param timeout The maximum time (in milliseconds) to finish the read operation, if the timeout is set to zero the timer will never 
     *        expire else, the timeout will begin when the request begins to be processed.
     * @return If successful, the number of bytes actually read is returned, otherwise a {@link #ERR_OPERATION} is returned and the method 
     *          {@link #lastError} can be used to indicate the error.
     */
    public native int read(int handle, byte[] data, int len, int timeout);
    
    /**
     * This method is writing data from the specified byte array to the opened output stream before the timer is expired. It is asynchronous 
     * and results in an event sent to the {@link #listener} method.
     * Note: data queued in system buffer could not be flushed away when flow control is enabled
     *
     * @param handle The service handle identifying the opened communication device.
     * @param data The data to be written to the output device.
     * @param len The number of byte to be written to the communication device.
     * @param timeout The maximum time (in milliseconds) to finish the write operation, if the timeout is set to zero the timer will never 
     *         expire else, the timeout will begin when the request begins to be processed.
     * @return Upon successful completion, the number of bytes which were written is return, Otherwise a {@link #ERR_OPERATION} is returned and 
     *          the method {@link #lastError} can be used to indicate the error.
     */
    public native int write(int handle, byte[] data, int len, int timeout);
    
    /**
     * Retrieves the last error occurs on communication operation.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public native int lastError();
    
    /**
     * This function causes all pending read and write operation to be stopped for given communication device.
     * @param handle The service handle identifying the opened communication device.
     * @return Return zero if the function succeeds else nonzero error code defined in class constants.
     */
    public native int cancel(int handle);
    
    /**
     * Get the status of the given communication device.
     * <table border=1>
     *   <thead><tr><th>BIT</th><th>Status</th><th>BIT=0</th><th>BIT=1</th></tr></thead>
     *   <tbody>
     *     <tr><td>0</td><td>Communication device connection status</td><td>Offline</td><td>Online</td></tr>
     *     <tr><td>1</td><td>Incoming data is available</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>2-31</td><td>Unused</td><td></td><td></td></tr>
     *   </tbody>
     * </table>
     * @param handle The service handle identifying the opened communication device.
     * @return If successful, the device status is returned, otherwise a {@link #ERR_OPERATION} is returned and the method {@link #lastError}
     *         can be used to indicate the error.
     */
    public native int status(int handle);
    
    /**
     * Get CTS signal
     * @return If successful, 0 or 1 is returned, otherwise a {@link #ERR_OPERATION} is returned and the method {@link #lastError}
     *         can be used to indicate the error.
     */
    public int getCts(int handle)
    {
        return _getCts(handle);
    }
    
    /**
     * Set RTS signal (only worked when HW flow control is not enabled)
     * @param rts 1 to enable RTS; 0 to disable RTS
     * @return If successful, 0 is returned, otherwise a {@link #ERR_OPERATION} is returned and the method {@link #lastError}
     *         can be used to indicate the error.
     */
    public int setRts(int handle, int rts)
    {
        return _setRts(handle, rts);
    }

    private static String getProductName(){
        byte[] info = new byte[20];
        Misc.getSystemInfo(Misc.INFO_PRODUCT, info);
        int len = info.length;
        for (int i = 0; i < info.length; i++) {
            if (info[i] == 0) {
                len = i;
                break;
            }
        }
        String mProdInfo = new String(info);
        mProdInfo = mProdInfo.substring(0, len);
        return mProdInfo;
    }
    
    /**
     * The method get called when the class received a notification event of the given communication device and the register method has 
     * been enabled.
     * @param handle The service handle identifying the opened communication device.
     * @param event Indicates the event defined in class constants.
     */
    public void listener(int handle, int event)
    {
        // 
        //  Call your real function to handle event here
        //
        if (null != mOnEventListener)
        {
            mOnEventListener.onEvent(handle, event);
        }
    }
    
    private native int connect2(int handle, int baud, int data_size, int stop_bit, int parity, int flow_control, int protocol, byte[] extra);
    private native int _getCts(int handle);
    private native int _setRts(int handle, int rts);
    
}
