package saioapi.cmd;

import com.xac.cmdmanager.CmdAPI;

/**
 * This class defines the Command Manager APIs to be used by the applications.Please Reference "VNG Saturn Command Reference Manual" document. 
 */
public class CommandManager {
    //For selectICCSlot
    public static final int ICC_SLOT      = 0x00000000;
    public static final int ICC_SLOT_SAM1 = 0x00000001;
    public static final int ICC_SLOT_SAM2 = 0x00000002;
    public static final int ICC_SLOT_SAM3 = 0x00000003;
    public static final int ICC_SLOT_SAM4 = 0x00000004;
    public static final int ICC_SLOT_SAM5 = 0x00000005;

    //For setICCmode
    public static final int ICC_MODE_EMV             = 0x00000000;
    public static final int ICC_MODE_ISO7816         = 0x00000001;
    public static final int ICC_MODE_SLE4418_SLE4428 = 0x00000002;
    public static final int ICC_MODE_SLE4442_SLE4432 = 0x00000003;

    //For PowerOnandResetSCR
    public static final int ICC_VOLT_18             = 0x00000002;//1.8V
    public static final int ICC_VOLT_20             = 0x00000003;//2V
    public static final int ICC_VOLT_30             = 0x00000005;//3V
    public static final int ICC_EMV_COLD_WARM_RESET = 0x00000000;
    public static final int ICC_7816_COLD_RESET     = 0x00000000;
    public static final int ICC_7816_WARM_RESET     = 0x00000001;
    public static final int ICC_MEMORY_CARD_RESET   = 0x00000000;

    /** The command indicates not defined. */
    public static final int CMD_UNKNOWN                = 0x00000000;

    /** The command indicates keypad notification. */
    public static final int CMD_KEYPAD_NOTIFICATION     = 0x00000001;

    /** The command indicates touch sreen diagnostic. */
    public static final int CMD_TOUCH_SCREEN_KEYPAD_LAYOUT= 0x00000002;

    /** The command indicates request PIN entry. */
    public static final int CMD_REQ_ONLINE_PINENTRY      = 0x00000003;

    /** The command indicates request Offline PIN Entry*/
    public static final int CMD_REQ_OFFLINE_PINENTRY     = 0x00000004;

    /** The command indicates APDU response packet.*/
    public static final int CMD_APDU_RESPONSE_PACKET     = 0x00000005;

    /** The command indicates ATR response packet.*/
    public static final int CMD_ATR_RESPONSE_PACKET      = 0x00000006;
    
    /**
     * Register a callback to be invoked when a Command is triggered.
     * @param listener The callback that will be invoked.
     */
    public static void setOnCommandListener(OnCommandListener listener){
        CmdAPI.setOnCommandListener(listener);
    }

    /**
    * This method creates a handle of XAC VNG platform device and establishes a connection. It always implements an exclusive open. Please make sure XAC VNG platform device is closed before using the method.
    * @return Upon successful completion, the service handle is returned to identify a communication that subsequent method will reference this value, Otherwise Class Com error code will be returned.
     */
    public static int openCom(){
        return CmdAPI.openCom();
    }

    /**
    *  The method results in the open XAC VNG platform device to be closed for use. 
    * @return Return zero if the function succeeds else nonzero error code defined in class constants.Please refer to Com.close().
     */
    public static void closeCom(){
        CmdAPI.closeCom();
    }

    /**
    * This method gets a value indicating the opened or closed status of XAC VNG platform device. The method tracks whether the device is open for use by the Cmd API, not whether the device is open by any application.
    * @return true if the serial port is open; otherwise, false.The default is false.
     */
    public static boolean isComOpened(){
        return CmdAPI.isComOpened();
    }

    /**
     * The method set touch screen keypad layout.
     * @param x1,y1,x2,y2 Indicates numeric keypad layout, where "x1, y1" are the coordinates of the upper-left corner of the rectangle 
     *                    and "x2, y2" are the coordinates of the lower-right corner of the rectangle. x1,y1,(x2-x1) and (y2-y1) should be integers between 0 and 9999. 
     * @param fx1,fx1,fx2,fy2 Indicates function key layout. where "fx1, fy1" are the coordinates of the upper-left corner of the rectangle 
     *                    and "fx2, fy2" are the coordinates of the lower-right corner of the rectangle. fx1,fy1,(fx2-fx1) and (fy2-fy1) should be integers between 0 and 9999.<br/>
     * @return Upon successful completion, the number of bytes which were communication service written is return, Otherwise Class Com error code will be returned.
     */
    public static int setTouchScreenKeypadLayout(int x1,int y1,int x2,int y2,int fx1,int fy1,int fx2,int fy2){
        return CmdAPI.setTouchScreenKeypadLayout(x1,y1,x2,y2,fx1,fy1,fx2,fy2);
    }

    /**
     * The method set request PIN entry.
     * @param key_index PIN encryption key index.(1 byte) If the length greater than 1 byte,set first byte only.
     * @param format PIN Block Format.(1 byte) If the length greater than 1 byte,set first byte only.
     * @param min_length Minimum acceptable PIN length.The Sting should be a number between 0 and 12. If the string number greater than 12 will be set 12 and If the value less than 0 will be set 0.
     * @param max_length Maximum acceptable PIN length.The Sting should be the number between 0 and 12. If the string number greater than 12 will be set 12 and If the value less than 0 will be set 0.
     * @param account Cardholder account number.
     * @param timeout_value Timeout value in seconds, range 000-255. If the value greater than 255 will be set 255 and If the value less than 0 will be set 0.
     * @return Upon successful completion, the number of bytes which were communication service written is return, Otherwise Class Com error code will be returned.
     */
    public static int setReqOnlinePinEntry(String key_index, String format, String min_length, String max_length, String account, int timeout_value){
        return CmdAPI.setReqOnlinePinEntry(key_index, format, min_length, max_length, account,timeout_value);
    }

    /**
     * The method set request offline plaintext PIN entry.
     * @param min_length Minimum acceptable PIN length. The Sting should be a number between 0 and 99. If the string number greater than 99 will be set 99 and If the value less than 0 will be set 0.
     * @param max_length Maximum acceptable PIN length. The Sting should be a number between 0 and 99. If the string number greater than 99 will be set 99 and If the value less than 0 will be set 0.
     * @param timeout_value Timeout value in seconds, range 000-255. If the value greater than 255 will be set 255 and If the value less than 0 will be set 0.
     * @return Upon successful completion, the number of bytes which were communication service written is return, Otherwise Class Com error code will be returned.
     */
    public static int setReqPlaintextPinEntry(String min_length, String max_length, int timeout_value){
        return CmdAPI.setReqPlaintextPinEntry(min_length, max_length, timeout_value);
    }

    /**
     * The method set request offline Encrypted PIN entry.
     * @param min_length Minimum acceptable PIN length. The Sting should be a number between 0 and 99. If the string number greater than 99 will be set 99 and If the value less than 0 will be set 0.
     * @param max_length Maximum acceptable PIN length. The Sting should be a number between 0 and 99. If the string number greater than 99 will be set 99 and If the value less than 0 will be set 0.
     * @param PK_Modulus PK Modulus.
     * @param challenge An 8-byte data from ICC.
     * @param PK_Exponent A 4-byte 32bit data (byte1 = b31-b24, byte2 = b23-b16, byte3=b15-b8, byte4=b7-b0).
     *                    It contains the Exponent of the associated public key.<br/>
     * [Note]:<br/>
     *        challenge data length plus PK_Exponent data length should not greater than 12 bytes.<br/>
     * @param timeout_value Timeout value in seconds, range 000-255.
     * @return Upon successful completion, the number of bytes which were communication service written is return, Otherwise Class Com error code will be returned.
     */
    public static int setReqEncryptedPinEntry(String min_length, String max_length, byte[] PK_Modulus, byte[] challenge, byte[] PK_Exponent, int timeout_value){
        return CmdAPI.setReqEncryptedPinEntry(min_length, max_length, PK_Modulus, challenge,  PK_Exponent, timeout_value);
    }

    /**
     * The method selects ICC slot for future ICC operations. 
     * @param flag Logical ID of the selected IC card or SAM slot defined in below class constants: <br/>
     * <table border=1>
     *   <thead><tr><th>Class Constants</th></thead>
     *   <tbody>
     *     <tr><td>{@link #ICC_SLOT}</td></tr>
     *     <tr><td>{@link #ICC_SLOT_SAM1}</td></tr>
     *     <tr><td>{@link #ICC_SLOT_SAM2}</td></tr>
     *     <tr><td>{@link #ICC_SLOT_SAM3}</td></tr>
     *     <tr><td>{@link #ICC_SLOT_SAM4}</td></tr>
     *     <tr><td>{@link #ICC_SLOT_SAM5}</td></tr>
     *   </tbody>
     * </table>
     * @return Upon successful completion, the number of bytes which were communication service written is return, Otherwise Class Com error code will be returned.
     */
    public static int selectIccSlot(int flag){
        return CmdAPI.selectIccSlot(flag);
    }

    /**
     * The method set ICC mode. 
     * @param flag ICC mode defined in below class constants: <br/>
     * <table border=1>
     *   <thead><tr><th>Class Constants</th></thead>
     *   <tbody>
     *     <tr><td>{@link #ICC_MODE_EMV} : default state</td></tr>
     *     <tr><td>{@link #ICC_MODE_ISO7816} : PCSC...</td></tr>
     *     <tr><td>{@link #ICC_MODE_SLE4418_SLE4428} : memory card</td></tr>
     *     <tr><td>{@link #ICC_MODE_SLE4442_SLE4432} : memory card</td></tr>
     *   </tbody>
     * </table>
     * [Note]:<br/>
     *        EMV mode: EMV L1 filter is on.<br/>
     *        7816 mode: EMV L1 filter is off.<br/>
     *        Memory card is SYNC mode operation.<br/>
     * @return Upon successful completion, the number of bytes which were communication service written is return, Otherwise Class Com error code will be returned.
     */
    public static int setIccMode(int flag){
        return CmdAPI.setIccMode(flag);
    }

    /**
     * The method turns selected SCR off. All SAM slots share the same power control. VNG only turns SAM's power off when all SAMs are off.
     * @return Upon successful completion, the number of bytes which were communication service written is return, Otherwise Class Com error code will be returned.
     */
    public static int turnScrOff(){
        return CmdAPI.turnScrOff();
    }

    /**
     * All SAM slots share the same power control.
     * The method use the first voltage setting of SAM slot and "warm reset" to reset other SAM slot, the following voltage setting and flag are ignored.
     * @param volt smart card reset operating voltage defined in below class constants: <br/>
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>Operating voltage</th></thead>
     *   <tbody>
     *     <tr><td>{@link #ICC_VOLT_18}</td><td>1.8V</td></tr>
     *     <tr><td>{@link #ICC_VOLT_20}</td><td>2V</td></tr>
     *     <tr><td>{@link #ICC_VOLT_30}</td><td>3V</td></tr>
     *   </tbody>
     * </table>
     * @param flag defined in below : <br/>
     * <table border=1>
     *   <thead><tr><th>ICC mode</th><th>Reset type</th></thead>
     *   <tbody>
     *     <tr><td>EMV mode</td><td>flag = {@link #ICC_EMV_COLD_WARM_RESET} : Cold Reset + Warm Reset (if necessary)</td></tr>
     *     <tr><td>7816 mode</td><td>flag = {@link #ICC_7816_COLD_RESET} : Cold Reset ; flag = {@link #ICC_7816_WARM_RESET} : Warm reset</td></tr>
     *     <tr><td>Memory card</td><td>flag is reserved for future use (set to {@link #ICC_MEMORY_CARD_RESET})</td></tr>
     *   </tbody>
     * </table>
     * @return Upon successful completion, the number of bytes which were communication service written is return, Otherwise Class Com error code will be returned.
     */
    public static int powerOnAndResetScr(int volt,int flag){
        return CmdAPI.powerOnAndResetScr(volt,flag);
    }

    /**
     * The method sends APDU to ICC then read response from ICC. If EMV mode then the target device perform EMV L1 protocol.
     * @param len apdu length.
     * @param apdu ISO 7816-4 format (CLA, INS, P1, P2 ...).
     * @return Upon successful completion, the number of bytes which were communication service written is return, Otherwise Class Com error code will be returned.
     */
    public static int sendApdu(int len,byte[] apdu){
        return CmdAPI.sendApdu(len,apdu);
    }

    /**
     * The method use to cancel Cancel Session Request that means VNG will stop any processing command
     */
    public static int cancelSessionRequest(){
        return CmdAPI.cancelSessionRequest();
    }

    //public static int setTouchSreenDiagnostic(int x1,int y1,int x2,int y2,int fx1,int fy1,int fx2,int fy2){
    //    return CmdAPI.setTouchSreenDiagnostic(x1,y1,x2,y2,fx1,fy1,fx2,fy2);
    //}
}
