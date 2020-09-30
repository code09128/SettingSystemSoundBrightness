//
//  Saio API, RFID class
//
package saioapi.reader;

import saioapi.OnEventListener;


/**
 * This class is deprecated since version 170308_r1.
 * This class defines the RFID contact-less reader APIs to be used by the applications.
 */
public class Rfid
{
    static
    {
        System.loadLibrary("SaioBase");
        System.loadLibrary("SaioReader");
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
    
    /** The card transporter device has not been opened. */
    public static final int ERR_NOT_OPEN        = 0x0000E004;
    
    /** The inputted parameters are not valid. */
    public static final int ERR_INVALID_PARAM   = 0x0000E006;
    
    /** Unable to get method reference of the class listener. */
    public static final int ERR_NO_LISTENER     = 0x0000E008;
    
   /** The method is not supported for the given device. */
    public static final int ERR_NOT_SUPPORT     = 0x0000E00A;
    
    /** A card detected, the RFID scanning can be enabled only after the card 
    * has been removed from the operation volume */
    public static final int ERR_CARD_EXIST      = 0x0000E00C;
    
    
    /** Signals the user has scanned/tap the card and the read card is successful 
    * and data available and can be read by {@link #getData}. */
    public static final int EVENT_DATA_READY    = 0x00000001;
    
    /** Signals the notification message arrived from the RFID reader and can be 
    * read by {@link #getData}. */
    public static final int EVENT_MSG_READY     = 0x00000002;
    
    /** The read data timeout occurred. no card data available and the reader was 
    * disabled. */
    public static final int EVENT_TIMEOUT       = 0x00000003;
    
    /** Signals there is at least one error detected and reader was disabled. */
    public static final int EVENT_READ_ERROR    = 0x00000004;
    
    /** Indicates the card read operation has been cancelled. */
    public static final int EVENT_CANCEL        = 0x00000005;
    
    /** The transaction has finished and the RFID reader was disabled. */
    public static final int EVENT_RFID_FINISH   = 0x00000006;

    /** A purchase transaction of goods. */
    public static final int PURCHASE_FOR_GOODS      = 0x00000000;
    
    /** A purchase transaction for service. */
    public static final int PURCHASE_FOR_SERVICE    = 0x00000001;
    
    /** A refund transaction. */
    public static final int REFUND_TRANSACTION      = 0x00000002;
    
    /** The data read from the card. */
    public static final int RFID_CARD_DATA          = 0x00000000;
    
    /** The data from the RFID reader module. */
    public static final int RFID_MESSAGE_DATA       = 0x00000001;
    
    /**
     * The listener that receives notifications when an RFID event is triggered.
     */
    private OnEventListener mOnEventListener = null;
    
    //
    //    Methods
    //
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * Get the registered Listener that handle the RFID event.
     * @return The callback to be invoked with a RFID event is triggered, 
     *         or null id no callback has been set.
     */
    public final OnEventListener getOnEventListener()
    {
        return mOnEventListener;
    }
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * Register a callback to be invoked when a RFID event is triggered.
     * @param listener The callback that will be invoked.
     */
    public void setOnEventListener(OnEventListener listener)
    {
        mOnEventListener = listener;
    }
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method creates a handle of the RFID service. It always implements an 
     * exclusive open.
     * @param dev Logical ID of the RFID service to open, it is ignored by now and 
     *          should be zero.
     * @return  Upon successful completion, the service handle is returned to 
     *          identify a RFID service that subsequent method will reference 
     *          this value, Otherwise a {@link #ERR_OPERATION} is returned and the 
     *          method {@link #lastError} can be used to indicate the error.
     */
    public native int open(short dev);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * The method results in the opened RFID service to be closed for use.
     * @param handle The service handle identifying the opened RFID device.
     * @return Return zero if the function succeeds else nonzero error code defined 
     *          in class constants.
     */
    public native int close(int handle);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method configures the payment transaction information of coming RFID scan 
     * and enable the RFID reader to obtain the data read from the RFID device by  
     * scanning the card. 
     * <br />The transaction type:
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>Transaction Types</th></tr></thead>
     *   <tbody>
     *     <tr><td>PURCHASE_FOR_GOODS</td><td>A purchase transaction of goods</td></tr>
     *     <tr><td>PURCHASE_FOR_SERVICE</td><td>A purchase transaction for service</td></tr>
     *     <tr><td>REFUND_TRANSACTION</td><td>A refund transaction</td></tr>
     *   </tbody>
     * </table>
     * <p>Remarks: <br />
     * This method is asynchronous and results in the events that is sent to the {@link #listener} 
     * method. On success, the associated data and message can be retrieved by using the {@link #getData} 
     * method upon the event arrived
     * @param handle The service handle identifying the opened RFID device.
     * @param type Specifies the transaction type, it could be one of below pre-defined class constants.
     * @param amount A null terminated string formatting an amount as money for 
     *          any currency, it may be empty, in which case the last amount 
     *          (if it exists) or default value (if there is no previous transaction) 
     *          will be used by the RFID module.
     * @param timeout The maximum pending time (in milliseconds) of the operation, if 
     *          the timeout is set to zero the timer will never expire and enable is 
     *          always on, else the timeout will begin when the request begins to be 
     *          processed
     * @param other The data array containing the additional amount information, 
     *          it is ignored by now.
     * @return Return zero if the function succeeds else nonzero error code 
     *          defined in class constants.
     */
    public native int transaction(int handle, byte type, String amount, int timeout, byte[] other);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This function resets the RFID reader service and stops the pending operation 
     * in case there is any.
     * @param handle The service handle identifying the opened RFID device.
     * @return Return zero if there is no error else nonzero error code defined 
     *          in class constants.
     */
    public native int reset(int handle);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method provides generic interface for carrying out the specific RFID 
     * sub command to customize or retrieve the parameters data that governs the 
     * operations of the RFID reader; which might include some opaque information 
     * and designed for advanced user only. For detail information about the parameter 
     * format, please refer to the associated RFID configure commands manuals.
     * 
     * <p>Remarks:<br />
     * This method manipulates RFID wrapped sub-command only, the application should 
     * prepare the outgoing command and proper incoming data array size (that should 
     * be large enough to ensure that the largest possible output data will fit 
     * in the buffer) to receive the associated response respecting to the definition 
     * on related RFID configure command.
     * @param handle The service handle identifying the opened RFID device.
     * @param cmd The data array that contains the command sent to the RFID reader.
     * @param cmdLen The size, in bytes, of the command message above.
     * @param resp The data array that receives the associated response from the RFID reader.
     * @return If successful, returns the number of bytes in the actual response 
     *          data array, otherwise a {@link #ERR_OPERATION} is returned and the 
     *          method {@link #lastError} can be used to indicate the error.
     */
    public native int config(int handle, byte[] cmd, int cmdLen, byte[] resp);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method returns the length (byte count) of either the card data or notification 
     * message from the RFID reader module. 
     * <br />The data types:
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>Data Types</th></tr></thead>
     *   <tbody>
     *     <tr><td>RFID_CARD_DATA</td><td>The data read from the card</td></tr>
     *     <tr><td>RFID_MESSAGE_DATA</td><td>The data from the RFID reader module</td></tr>
     *   </tbody>
     * </table>
     * @param handle The service handle identifying the opened RFID device.
     * @param type Specifies the data type, it could be one of below pre-defined class constants.
     * @return If successful, the number of bytes in card data array, otherwise 
     *          a {@link #ERR_OPERATION} is returned and the method {@link #lastError} 
     *          can be used to indicate the error.
     */
    public native int getDataLength(int handle, byte type);

    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method retrieves a buffer containing the card data or notification message. 
     * @param handle The service handle identifying the opened RFID device.
     * @param type Specifies the data type defined in the table of above {@link #getDataLength} method
     * @param buffer The byte array to receive data.
     * @return Upon successful, the number of bytes copied into the data array is 
     *          returned, otherwise a {@link #ERR_OPERATION} is returned and the 
     *          method {@link #lastError} can be used to indicate the error.
     */
    public native int getData(int handle, byte type, byte[] buffer);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * Retrieves the last error occurs on the RFID operation.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public native int lastError();
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * The method get called when the class received a notification event of the 
     * given RFID device and the register method has been enabled.
     * @param handle The service handle identifying the opened RFID device.
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
    
}
