//
//  Saio API, SCR class
//
package saioapi.reader;

import saioapi.OnEventListener;

/**
 * This class is deprecated since version 170308_r1.
 * This class provide services between IC card reader (hardware device driver and 
 * protocol stack) and applications that are smart-card-aware. All methods here is 
 * defined with respect to standard EMV level 1 or ISO T=0/T=1 protocol.
 * 
 * The class offer both asynchronous and synchronous methods accessing the SCR to 
 * meet the different needs for application programming. Where the synchronous 
 * method is pretty straightforward, occurs while you wait; processing cannot 
 * continue until the request is complete. 
 * 
 * In contrast, asynchronous method run in the API background thread and do not 
 * block the application processing, which improves performance cause SCR operation 
 * and application processing can run at the same time. With asynchronous functions, 
 * as soon as the given request has been queued, it returns right away that allows 
 * the application processing is continues while request is being performed and no 
 * matter it is successful or not, an event is sent by API to notify user program 
 * once the I/O is done. Any event following an asynchronous request must be 
 * considered as the completion of operation that effectively provides stop-and-wait 
 * control on per request basis.
 */

public class Scr
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
    
    /** The reader slot is busy to receive new request now. */
    public static final int ERR_DEV_BUSY        = 0x0000E00A;

    /** Signals the {@link #powerOn} or {@link #warmReset} operation is completed 
     * successfully and the card is powered. The ATR has been received 
     * and can be read by the {@link #getData} then. */
    public static final int EVENT_RESET_DONE    = 0x00000001;
    
    /** Signals the <b>sendData</b> operation is completed successfully and a 
     * card response can be read by {@link #getData} now. */
    public static final int EVENT_DATA_READY    = 0x00000002;
    
    /** Indicates the card protocol ({@link #setProtocol}) has just been negotiated 
     * successfully. */
    public static final int EVENT_NEGOTIATED    = 0x00000003;
    
    /** Indicates the card not present or was removed. */
    public static final int EVENT_REMOVED       = 0x00000004;
    
    /** Unexpected data received from the card or the operation mode is 
     * not supported. */
    public static final int EVENT_PROTO_ERR     = 0x00000005;
    
    /** Indicates the card not respond and timeout occurs. */
    public static final int EVENT_NO_RESPONSE   = 0x00000006;
    
    /** Indicates the SCR operation has been cancelled. */
    public static final int EVENT_CANCEL        = 0x00000007;
    
    /** ICC slot, smart card reader slot#1. */
    public static final int SCR_ICC_SLOT1       = 0x00000000;
    
    /** ICC slot, smart card reader slot#2. */
    public static final int SCR_ICC_SLOT2       = 0x00000001;

    /** ICC slot, smart card reader slot#3. */
    public static final int SCR_ICC_SLOT3       = 0x00000002;

    /** SAM#1 service. */
    public static final int SCR_SAM1            = 0x00000003;
    
    /** SAM#2 service. */
    public static final int SCR_SAM2            = 0x00000004;
    
    /** SAM#3 service. */
    public static final int SCR_SAM3            = 0x00000005;
    
    /** SAM#4 service. */
    public static final int SCR_SAM4            = 0x00000006;
    
    /** SAM#5 service. */
    public static final int SCR_SAM5            = 0x00000007;
    
    /** SAM#6 service. */
    public static final int SCR_SAM6            = 0x00000008;
    
    /** SAM#7 service. */
    public static final int SCR_SAM7            = 0x00000009;
    
    /** SAM#8 service. */
    public static final int SCR_SAM8            = 0x0000000A;
    
    /** for syncOperation. */
    public static final int SYNC_SET_PROTOCOL   = 0x00000000;
    
    /** for syncOperation. */
    public static final int SYNC_COLD_RESET     = 0x00000001;
    
    /** for syncOperation. */
    public static final int SYNC_WARM_RESET     = 0x00000002;
    
    /** for syncOperation. */
    public static final int SYNC_EXCHANGE       = 0x00000003;

    /**
     * The listener that receives notifications when an SCR event is triggered.
     */
    private OnEventListener mOnEventListener = null;
    
    //
    //    Methods
    //
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * Get the registered Listener that handle the SCR event.
     * @return The callback to be invoked with a SCR event is triggered, 
     *         or null id no callback has been set.
     */
    public final OnEventListener getOnEventListener()
    {
        return mOnEventListener;
    }
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * Register a callback to be invoked when a SCR event is triggered.
     * @param listener The callback that will be invoked.
     */
    public void setOnEventListener(OnEventListener listener)
    {
        mOnEventListener = listener;
    }
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method results in the IC card is connected to service exclusively. 
     * @param slot Logical ID of the selected IC card or SAM service defined in 
     *          below class constants: <br/>
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>IC Card slot and SAM service</th></tr></thead>
     *   <tbody>
     *     <tr><td>SCR_ICC_SLOT1</td><td>ICC slot, smart card reader slot#1</td></tr>
     *     <tr><td>SCR_ICC_SLOT2</td><td>ICC slot, smart card reader slot#2</td></tr>
     *     <tr><td>SCR_ICC_SLOT3</td><td>ICC slot, smart card reader slot#3</td></tr>
     *     <tr><td>SCR_SAM1</td><td>SAM#1 service</td></tr>
     *     <tr><td>SCR_SAM2</td><td>SAM#2 service</td></tr>
     *     <tr><td>SCR_SAM3</td><td>SAM#3 service</td></tr>
     *     <tr><td>SCR_SAM4</td><td>SAM#4 service</td></tr>
     *     <tr><td>SCR_SAM5</td><td>SAM#5 service</td></tr>
     *     <tr><td>SCR_SAM6</td><td>SAM#6 service</td></tr>
     *     <tr><td>SCR_SAM7</td><td>SAM#7 service</td></tr>
     *     <tr><td>SCR_SAM8</td><td>SAM#8 service</td></tr>
     *   </tbody>
     * </table>
     * @return Upon successful completion, returns the service handle identifying
     *           the associated service that subsequent method will reference this 
     *           value, Otherwise a {@link #ERR_OPERATION} is returned and the method 
     *           {@link #lastError} can be used to indicate the error.
     */
    public native int open(short slot);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * The method results in the open IC card service to be closed for use. If the other 
     * SCR operation request is not completed, it must wait for all done or cancel the 
     * pending operation before close.
     * @param handle The service handle identifying the opened SCR service on given slot.
     * @return Return zero if the function succeeds else nonzero error code defined in 
     *          class constants.
     */
    public native int close(int handle);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method retrieves the current protocol and parameters used by the card in 
     * selected reader slot.
     * The format of the 4 bytes returned PPS is defined as:<br/>
     * <table border=1>
     *   <thead><tr><th>Data Element</th><th>Format</th><th>Length</th><th>Description</th></tr></thead>
     *   <tbody>
     *     <tr><td>Type</td><td>Binary</td><td>1</td><td>IC Card Type<br/>
     *                                                  0x00 = IC Smart Card and SAM<br/>
     *                                                  0x01 = IC Memory Card<br/>
     *                                                  0x02 = High Speed PSAM *</td></tr>
     *     <tr><td>Protocol</td><td>Binary</td><td>1</td><td>The ICC protocol<br/>
     *                                                  For IC smart card:<br/>
     *                                                  0x00 = “T=0” protocol<br/>
     *                                                  0x01 = “T=1” protocol<br/>
     *                                                  For IC memory card:<br/>
     *                                                  0x00 = SLE 4418, 4428<br/>
     *                                                  0x01 = SLE 4432, 4442<br/>
     *                                                  0x02 = Philips I2C 4K – 64K<br/>
     *                                                  0x03 = Philips I2C 2K</td></tr>
     *     <tr><td>FI</td><td>Binary</td><td>1</td><td>ICC Clock Rate conversion factor<br/>
     *                                                  Index into the table 7 in ISO/IEC 7816-3:1997<br/>
     *                                                  selecting a clock rate conversion factor</td></tr>
     *     <tr><td>DI</td><td>Binary</td><td>1</td><td>Data bit rate adjustment factor<br/>
     *                                                  Index into the table 8 in ISO/IEC 7816-3:1997<br/>
     *                                                  selecting a baud rate conversion factor</td></tr>
     *   </tbody>
     * </table>
     * *Note: some special High speed PSAM card will require higher preset baud rate to 
     * retrieve the ATR data
     * @param handle The service handle identifying the opened SCR service on given slot.
     * @param pps The data array to receive protocol and parameters data
     * @return Return zero if the function succeeds else nonzero error code defined in 
     *          class constants.
     */
    public native int getProtocol(int handle, byte[] pps);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method programs the protocol and parameters to use with the card in selected 
     * reader slot. It is asynchronous and will results in an event sent to the registered 
     * {@link #listener} method to indicate the completion of the operation.
     * 1. For IC Memory cards, the application has to select the right protocol with this 
     *      method before calling the {@link #powerOn} or {@link #warmReset}, otherwise, 
     *      may result in a {@link #EVENT_PROTO_ERR} event. 
     * 2. For normal IC Smart cards, SCR power on and reset normally negotiate the protocol 
     *      (as the protocol is usually defined by the ATR) automatically. In case where the
     *      application wants to change the protocol, this function is called and will have
     *      an effect on the next IC smart exchange.
     * 3. Some high speed PSAM card requires higher data bit to retrieve the ATR data; the 
     *      application should apply this function to preset the right FI/DI parameters 
     *      before calling the power-on/reset request then.
     * @param handle The service handle identifying the opened SCR service on given slot.
     * @param pps The 4 bytes data array containing protocol and parameters data defined 
     *          in above table of {@link #getProtocol} method
     * @return Return zero if the function succeeds else nonzero error code defined in 
     *          class constants.
     */
    public native int setProtocol(int handle, byte[] pps);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method returns current status of a given card slot, which includes if there is 
     * a card in the slot and its power status etc.:<br/>
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>IC Card slot and SAM service</th></tr></thead>
     *   <tbody>
     *     <tr><td>BIT</td><td>Status</td><td>BIT=0</td><td>BIT=1</td></tr>
     *     <tr><td>0</td><td>Card is in the reader slot</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>1</td><td>Card slot power state is on</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>2-31</td><td>Unused</td><td></td><td></td></tr>
     *   </tbody>
     * </table>
     * @param handle The service handle identifying the opened SCR service on given slot.
     * @return If successful, the reader slot status is returned, otherwise a {@link #ERR_OPERATION} 
     *          is returned and the method {@link #lastError} can be used to indicate the error.
     */
    public native int status(int handle);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method applies power to the selected smart card reader slot, performing a cold 
     * reset. Calling this method when the slot power is off will normally reset the card, 
     * resulting in the transmission of an ATR by the card in the slot. 
     * 
     * <p>Remarks: <br />
     * The request is asynchronous and results in an event sent to the registered listener 
     * method upon the completion of the SCR operation. On success, the Answer to Reset 
     * (ATR) can be retrieved using the {@link #getData} function. 
     * @param handle The service handle identifying the opened SCR service on given slot.
     * @param voltage Indicates the power supply (0-> Auto, 1-> 1.8V, 2-> 3V, 3-> 5V)
     * @return Return zero if the function succeeds else nonzero error code defined in 
     *          class constants.
     */
    public native int powerOn(int handle, short voltage);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method removes power from the selected smart card reader slot.
     * @param handle The service handle identifying the opened SCR service on given slot.
     * @return Return zero if the function succeeds else nonzero error code defined in 
     *          class constants.
     */
    public native int powerOff(int handle);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method performs a warm reset on selected reader slot. With the IC Smart card, 
     * this will normally result in the transmission of an ATR by the card in the slot.
     * 
     * <p>Remarks: <br />
     * The request is asynchronous and results in an event sent to the registered {@link #listener} 
     * method upon the completion of the SCR operation. On success, the reset data (ATR) 
     * can be retrieved.
     * @param handle The service handle identifying the opened SCR service on given slot.
     * @return Return zero if the function succeeds else nonzero error code defined in 
     *          class constants.
     */
    public native int warmReset(int handle);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method sends data to smart card reader. In the context of the IC Memory cards, 
     * the format of the data is card dependent. In the context of standard EMV or ISO 
     * T=0/T=1 IC Smart cards, the format of the data must respect the APDU format defined 
     * by the ISO 7816-4 and EMV specifications.
     * 
     * <p>Remarks: <br />
     * This method result in an event sent to the registered {@link #listener} method that 
     * indicate the completion of the IC exchange. On success, the response data can be 
     * retrieved using the {@link #getData} method.
     * @param handle The service handle identifying the opened SCR service on given slot.
     * @param buffer The data array to be written to the card.
     * @param len Number of bytes in buffer to be transmitted.
     * @return Return zero if the function succeeds else nonzero error code defined in
     *          class constants.
     */
    public native int exchange(int handle, byte[] buffer, int len);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method retrieves the number of bytes within the current SCR slot data buffer 
     * received from the card.
     * @param handle The service handle identifying the opened SCR service on given slot.
     * @return If successful, the number of bytes in reader slot buffer, otherwise a 
     *          {@link #ERR_OPERATION} is returned and the method {@link #lastError} can 
     *          be used to indicate the error.
     */
    public native int getDataLength(int handle);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method retrieves the current SCR slot data buffer. The contents of the slot 
     * data buffer depend on the operation for whose completion event the method is invoked. 
     * For a cold or warm reset operation, for instance, the data buffer may contain either 
     * the card's Answer to Reset (from an IC Smart card) or the first few bytes of the card's 
     * memory (from an IC Memory card). For a card command {@link #exchange} operation, 
     * the data buffer will contain the response from the card.
     * @param handle The service handle identifying the opened SCR service on given slot.
     * @param buffer The data array to receive the data.
     * @return Upon successful, the number of bytes copied into the data array is returned, 
     *          otherwise a {@link #ERR_OPERATION} is returned and the method {@link #lastError} 
     *          can be used to indicate the error.
     */
    public native int getData(int handle, byte[] buffer);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * Retrieves the last error occurs on the SCR operation.
     * @return Return zero if there is no error else nonzero error code defined in 
     *          class constants.
     */
    public native int lastError();
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method sends a given service request (such as set protocol, cold/warm reset or 
     * exchange) to the card, wait for the operation complete and expects to receive data 
     * back (if any) from the card synchronously.
     * The available service requests (defined in class constants) and corresponding input, 
     * output are defined as: <br/>
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>IC Card slot and SAM service</th></tr></thead>
     *   <tbody>
     *     <tr><td>Request</td><td>Sent Data</td><td>Received Data</td></tr>
     *     <tr><td>SYNC_SET_PROTOCOL</td><td>A 4 bytes data containing protocol and parameters 
     *              data defined in above table in {@link #getProtocol} method</td><td>NULL</td></tr>
     *     <tr><td>SYNC_COLD_RESET</td><td>A 2 bytes data containing voltage information defined 
     *              in {@link #powerOn} method</td><td>Answer to reset (ATR)</td></tr>
     *     <tr><td>SYNC_WARM_RESET</td><td>NULL</td><td>Answer to reset (ATR)</td></tr>
     *     <tr><td>SYNC_EXCHANGE</td><td>Data to be written to the card</td><td>Response data 
     *              from the card</td></tr>
     *   </tbody>
     * </table>
     * <p>Remarks: <br />
     * This function cannot be called within {@link #listener} method.
     * @param handle The service handle identifying the opened SCR service on given slot.
     * @param request Defines the service request:
     * @param SendBuffer The data array containing the parameter for service request or data 
     *          to be transmitted to the card, it is ignored in the card warn reset case.
     * @param SendLen Number of bytes in buffer to be transmitted.
     * @param RecvBuffer The data array to receive any data returned from the card.
     * @return If successful, returns the number of bytes in the actual received data array, 
     *          otherwise a {@link #ERR_OPERATION} is returned and the method {@link #lastError} 
     *          can be used to indicate the error.
     */
    public native int syncOperation(int handle, int request, byte[] SendBuffer, int SendLen, byte[] RecvBuffer);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This function causes all pending SCR request to be stopped.
     * @param handle The service handle identifying the opened SCR service on given slot.
     * @return Return zero if the function succeeds else nonzero error code defined in
     *          class constants.
     */
    public native int cancel(int handle);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * The method get called when the class received a notification event of the 
     * given SCR reader slot and the register method has been enabled.
     * @param handle The service handle identifying the opened SCR device.
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
