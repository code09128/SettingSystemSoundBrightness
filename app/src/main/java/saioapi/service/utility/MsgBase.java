package saioapi.service.utility;

import saioapi.base.Update;

public class MsgBase {

    public static final String SERVICE_NAME = "com.xac.SaioUtilityService";

    //
    //	Constants (Service Event(msg.what), Type(msg.arg1) and Returns(msg.arg2))
    //
    // Event value carried in Message.what

    /**
     *  Indicates the cancel task request.
     *  <p>
     *      Constant Value : 0 (0x00000000)
     *  </p>
     */
    public static final int MSG_CANCEL = 0;

    /**
     *  Indicates the updating service request.
     *  <p>
     *      Constant Value : 1 (0x00000001)
     *  </p>
     */
    public static final int MSG_UPDATE = 1;

    /**
     *  Indicates the updating information request.
     *  <p>
     *      Constant Value : 2 (0x00000002)
     *  </p>
     */
    public static final int MSG_UPDATE_INFO = 2;

    /**
     *  Indicates the EPP service request.
     *  <p>
     *      Constant Value : 3 (0x00000003)
     *  </p>
     */
    public static final int MSG_EPP = 3;

    /**
     *  Indicates the printer service request.
     *  <p>
     *      Constant Value : 4 (0x00000004)
     *  </p>
     */
    public static final int MSG_PRINTER = 4;

    /**
     *  Indicates the service state request.
     *  <p>
     *      Constant Value : 5 (0x00000005)
     *  </p>
     */
    public static final int MSG_SERVICE_STATE = 5;

    // Service action value carried in Message.arg1
    // Refer by EppInfo, PrinterInfo, UpdateReq class
    //

    //Service result value carried in Message.arg2

    /**
     *  Indicates the no error response.
     *  <p>
     *      Constant Value : 0 (0x00000000)
     *  </p>
     */
    public static final int ERR_OK = 0;

    /**
     *  Indicates the all case of busy error response.
     *  <p>
     *      Constant Value : -1 (0xFFFFFFFF)
     *  </p>
     */
    public static final int ERR_BUSY = -1;

    /**
     *  Indicates the invalid parameter error response.
     *  <p>
     *      Constant Value : -2 (0xFFFFFFFE)
     *  </p>
     */
    public static final int ERR_INVALID_PARAM = -2;

    /**
     * Indicates the timeout error response.
     *  <p>
     *      Constant Value : -3 (0xFFFFFFFD)
     *  </p>
     */
    public static final int ERR_RSP_TIMEOUT = -3;

    /**
     *  Indicates the install error response.
     *  <p>
     *      Constant Value : -4 (0xFFFFFFFC)
     *  </p>
     */
    public static final int ERR_INSTALL_FAIL = -4;

    /**
     *   Indicates the scat error response.
     *  <p>
     *      Constant Value : -5 (0xFFFFFFFB)
     *  </p>
     */
    public static final int ERR_SCAT_ERROR = -5;

    /**
     *  Indicates the file error response.
     *  <p>
     *      Constant Value : -6 (0xFFFFFFFA)
     *  </p>
     */
    public static final int ERR_FILE_ERROR = -6;

    /**
     *  Indicates the file exist error response.
     *  <p>
     *      Constant Value : -7 (0xFFFFFFF9)
     *  </p>
     */
    public static final int ERR_FILE_NOT_EXIST = -7;

    /**
     *  Indicates the device not ready error response.
     *  <p>
     *      Constant Value : -8 (0xFFFFFFF8)
     *  </p>
     */
    public static final int ERR_NOT_READY = -8;

    /**
     *  Indicates the device tamper error response.
     *  <p>
     *      Constant Value : -9 (0xFFFFFFF7)
     *  </p>
     */
    public static final int ERR_FW_TAMPER = -9;

    /**
     *  Indicates the device has loader only error response.
     *  <p>
     *      Constant Value : -10 (0xFFFFFFF6)
     *  </p>
     */
    public static final int ERR_ONLY_LOADER = -10;

    /**
     *  Indicates the device low power error response.
     *  <p>
     *      Constant Value : -11 (0xFFFFFFF5)
     *  </p>
     */
    public static final int ERR_LOW_POWER = -11;

    /**
     *  Indicates the device flash deferment error response.
     *  <p>
     *      Constant Value : -12 (0xFFFFFFF4)
     *  </p>
     */
    public static final int ERR_FLASH_DEFERMENT = -12;

    /**
     *   Indicates the device firmware miss error response.
     *  <p>
     *      Constant Value : -13 (0xFFFFFFF3)
     *  </p>
     */
    public static final int ERR_FW_MISS = -13;

    /**
     *   Indicates the device does not need update error response.
     *  <p>
     *      Constant Value : -14 (0xFFFFFFF2)
     *  </p>
     */
    public static final int ERR_NOT_NEED_UPDATE = -14;

    /**
     *  Indicates the fw authenticate error response.
     *  <p>
     *      Constant Value : -15 (0xFFFFFFF1)
     *  </p>
     */
    public static final int ERR_FW_AUTHENTICATE = -15;

    /**
     *  Indicates the fw image of block error response.
     *  <p>
     *      Constant Value : -16 (0xFFFFFFF0)
     *  </p>
     */
    public static final int ERR_FW_IMAGE_BLOCK = -16;

    /**
     *  Indicates the fw reset error response.
     *  <p>
     *      Constant Value : -17 (0xFFFFFFEF)
     *  </p>
     */
    public static final int ERR_FW_RESERT = -17;

    /**
     *  Indicates the exception error response.
     *  <p>
     *      Constant Value : -18 (0xFFFFFFEE)
     *  </p>
     */
    public static final int ERR_EXCEPTION = -18;

    /**
     *  Indicates the device low power error response.
     *  <p>
     *      Constant Value : -19 (0xFFFFFFED)
     *  </p>
     */
    public static final int ERR_LOW_SAFE_SPACE = -19;

    /**
     *  Indicates the battery to low below 50% error response.
     *  <p>
     *      Constant Value : -20 (0xFFFFFFEC)
     *  </p>
     */
    public static final int ERR_OS_SAFE_BATTERY_LOW = -20;

    /**
     *  Indicates the battery to low below 30% error response.
     *  <p>
     *      Constant Value : -21 (0xFFFFFFEB)
     *  </p>
     */
    public static final int ERR_FW_SAFE_BATTERY_LOW = -21;



    /**
     *  Indicates the get epp version case of busy error response.
     *  <p>
     *      Constant Value : -100 (0xFFFFFF9C)
     *  </p>
     */
    public static final int ERR_BUSY_EPP_VERSION = -100;

    /**
     *  Indicates the get epp module case of busy error response.
     *  <p>
     *      Constant Value : -101 (0xFFFFFF9B)
     *  </p>
     */
    public static final int ERR_BUSY_EPP_MODULE = -101;

    /**
     *  Indicates the get epp KCV case of busy error response.
     *  <p>
     *      Constant Value : -102 (0xFFFFFF9A)
     *  </p>
     */
    public static final int ERR_BUSY_EPP_KCV = -102;

    /**
     *  Indicates the epp updating case of busy error response.
     *  <p>
     *      Constant Value : -103 (0xFFFFFF99)
     *  </p>
     */
    public static final int ERR_BUSY_EPP_UPDATING = -103;

    /**
     *  Indicates the get printer version case of busy error response.
     *  <p>
     *      Constant Value : -104 (0xFFFFFF98)
     *  </p>
     */
    public static final int ERR_BUSY_PRINTER_VERSION = -104;

    /**
     *  Indicates the printer updating case of busy error response.
     *  <p>
     *      Constant Value : -105 (0xFFFFFF97)
     *  </p>
     */
    public static final int ERR_BUSY_PRINTER_UPDATING = -105;

    /**
     *  Indicates the os updating case of busy error response.
     *  <p>
     *      Constant Value : -106 (0xFFFFFF96)
     *  </p>
     */
    public static final int ERR_BUSY_OS_UPDATING = -106;

    /**
     *  Indicates the os key loading case of busy error response.
     *  <p>
     *      Constant Value : -107 (0xFFFFFF95)
     *  </p>
     */
    public static final int ERR_BUSY_OS_KEY_LOADING = -107;

    /**
     *  Indicates the os data updating case of busy error response.
     *  <p>
     *      Constant Value : -108 (0xFFFFFF94)
     *  </p>
     */
    public static final int ERR_BUSY_OS_DATA_UPDATING = -108;



    // Update function Error Code
    /**
     *  @see saioapi.base.Update#ERR_INVALID_PARAM
     *  <p></p>
     */
    public static final int	U_ERR_INVALID_PARAM = Update.ERR_INVALID_PARAM;     // The inputted parameters are not valid.

    /**
     *  @see saioapi.base.Update#ERR_NO_LISTENER
     *  <p></p>
     */
    public static final int	U_ERR_NO_LISTENER = Update.ERR_NO_LISTENER;        // Unable to get method reference of the class listener.

    /**
     *  @see saioapi.base.Update#ERR_IO_FAIL
     *  <p></p>
     */
    public static final int	U_ERR_IO_FAIL = Update.ERR_IO_FAIL;                // Some unexpected internal error occurred.

    /**
     *  @see saioapi.base.Update#ERR_BAD_PACKAGE
     *  <p></p>
     */
    public static final int U_ERR_BAD_PACKAGE = Update.ERR_BAD_PACKAGE;        // The signed installation package contains bad format.

    /**
     *  @see saioapi.base.Update#ERR_NO_CA_KEY
     *  <p></p>
     */
    public static final int U_ERR_NO_CA_KEY = Update.ERR_NO_CA_KEY;           // The CA public key specified by certificate does not exist.

    /**
     *  @see saioapi.base.Update#ERR_KEY_IN_BLACKLIST
     *  <p></p>
     */
    public static final int U_ERR_KEY_IN_BLACKLIST = Update.ERR_KEY_IN_BLACKLIST;    // The associated public key or its CA key is in blacklist.

    /**
     *  @see saioapi.base.Update#ERR_BAD_CERT
     *  <p></p>
     */
    public static final int U_ERR_BAD_CERT = Update.ERR_BAD_CERT;            // The certificate does not match the specific public key modulus.

    /**
     *  @see saioapi.base.Update#ERR_NO_DATA
     *  <p></p>
     */
    public static final int U_ERR_NO_DATA = Update.ERR_NO_DATA;              // No authenticated data found.

    /**
     *  @see saioapi.base.Update#ERR_BAD_DATA
     *  <p></p>
     */
    public static final int U_ERR_BAD_DATA = Update.ERR_BAD_DATA;            // The data contains invalid format.

    /**
     *  @see saioapi.base.Update#ERR_CERT_FORMAT
     *  <p></p>
     */
    public static final int U_ERR_CERT_FORMAT = Update.ERR_CERT_FORMAT;      // Unknown certificate information format.

    /**
     *  @see saioapi.base.Update#ERR_CERT_TYPE
     *  <p></p>
     */
    public static final int U_ERR_CERT_TYPE = Update.ERR_CERT_TYPE;         // The system does not support the certificate type.

    /**
     *  @see saioapi.base.Update#ERR_CERT_ALGID
     *  <p></p>
     */
    public static final int U_ERR_CERT_ALGID = Update.ERR_CERT_ALGID;      // The system does not support the hash algorithm of certificate.

    /**
     *  @see saioapi.base.Update#ERR_CERT_NOT_ACTIVE
     *  <p></p>
     */
    public static final int U_ERR_CERT_NOT_ACTIVE = Update.ERR_CERT_NOT_ACTIVE;  // The certificate has not been activated yet.

   /**
    *  @see saioapi.base.Update#ERR_CERT_EXPIRED
    *  <p></p>
    */
    public static final int U_ERR_CERT_EXPIRED = Update.ERR_CERT_EXPIRED;     // The certificate has expired.

   /**
    *  @see saioapi.base.Update#ERR_CAT_FORMAT
    *  <p></p>
    */
    public static final int U_ERR_CAT_FORMAT = Update.ERR_CAT_FORMAT;       // Unknown security catalog format.

   /**
    *  @see saioapi.base.Update#ERR_CAT_ITEM
    *  <p></p>
    */
    public static final int U_ERR_CAT_ITEM = Update.ERR_CAT_ITEM;          // The system does not support the items in security catalog.

   /**
    *  @see saioapi.base.Update#ERR_CAT_ALGID
    *  <p></p>
    */
    public static final int U_ERR_CAT_ALGID = Update.ERR_CAT_ALGID;        // The system does not support the hash algorithm in catalog.

    /**
     *  @see saioapi.base.Update#ERR_FILE_NOT_FOUND
     *  <p></p>
     */
    public static final int U_ERR_FILE_NOT_FOUND = Update.ERR_FILE_NOT_FOUND;       // The file defined in catalog not found.

   /**
    *  @see saioapi.base.Update#ERR_AUTH_FAIL
    *  <p></p>
    */
    public static final int U_ERR_AUTH_FAIL = Update.ERR_AUTH_FAIL;       // The update authentication was failure.

   /**
    *  @see saioapi.base.Update#ERR_UPDATE_FAIL
    *  <p></p>
    */
    public static final int U_ERR_UPDATE_FAIL = Update.ERR_UPDATE_FAIL;  // The update failed in some unexpected way.

   /**
    *  @see saioapi.base.Update#ERR_AUTORUN_FAIL
    *  <p></p>
    */
    public static final int U_ERR_AUTORUN_FAIL = Update.ERR_AUTORUN_FAIL;    // The method failed to launch the AUTORUN program.

   /**
    *  @see saioapi.base.Update#ERR_KEY_IN_LOCK
    *  <p></p>
    */
    public static final int U_ERR_KEY_IN_LOCK = Update.ERR_KEY_IN_LOCK;    // The system was locked into other appDataFolder key.


   /**
    * Constructs a error code to description
    *
    * @param err The parameter is error code value.
    *
    * @return The return value is the transmit error code message.
    */
    public static final String getErrorDescription(int err) {

        String description = "";

        switch (err) {
            case ERR_OK:
                description = "No Error.";
                break;
            case ERR_BUSY:
                description = "Update Service is busy.";
                break;
            case ERR_INVALID_PARAM:
                description = "Invalid Parameter.";
                break;
            case ERR_RSP_TIMEOUT:
                description = "Firmware No Response and Timeout";
                break;
            case ERR_INSTALL_FAIL:
                description = "Call <install> returns error as updating data/ firmware / os.";
                break;
            case ERR_SCAT_ERROR:
                description = "Call <install> returns error as updating sign file checked.";
                break;
            case ERR_FILE_ERROR:
                description = "Missing or incorrect firmware image.";
                break;
            case ERR_FILE_NOT_EXIST:
                description = "Cant find file.";
                break;
            case ERR_NOT_READY:
                description = "Device is not found or not ready.";
                break;
            case ERR_FW_TAMPER:
                description = "<R12> Device Tamper.";
                break;
            case ERR_ONLY_LOADER:
                description = "<R1F> Firmware is in Loader Mode.";
                break;
            case ERR_LOW_POWER:
                description = "<R17> Device is low power.";
                break;
            case ERR_FLASH_DEFERMENT:
                description = "<R18> Firmware is busy to deferment flash.";
                break;
            case ERR_FW_MISS:
                description = "<R1G> Firmware is missing necessary modules.";
                break;
            case ERR_NOT_NEED_UPDATE:
                description = "Firmware version is the same. Do not need update.";
                break;
            case ERR_FW_AUTHENTICATE:
                description = "<X72> Firmware authentication fail.";
                break;
            case ERR_FW_IMAGE_BLOCK:
                description = "<X73> Send Firmware image block fail.";
                break;
            case ERR_FW_RESERT:
                description = "<X74> Firmware reset and return error.";
                break;
            case ERR_EXCEPTION:
                description = "Raise Exception as <install>.";
                break;
            case ERR_LOW_SAFE_SPACE:
                description = "Internal Storage safe space too small.";
                break;
            case ERR_OS_SAFE_BATTERY_LOW:
                description = "Voltage was below 50% from battery.";
                break;
            case ERR_FW_SAFE_BATTERY_LOW:
                description = "Voltage was below 30% from battery.";
                break;
            case ERR_BUSY_EPP_VERSION:
                description = "Service is busy (Get EPP Version).";
                break;
            case ERR_BUSY_EPP_MODULE:
                description = "Service is busy (Get EPP Module Info).";
                break;
            case ERR_BUSY_EPP_KCV:
                description = "Service is busy (Get EPP KCV).";
                break;
            case ERR_BUSY_EPP_UPDATING:
                description = "Service is busy (Updating EPP Firmware).";
                break;
            case ERR_BUSY_PRINTER_VERSION:
                description = "Service is busy (Get Printer Version).";
                break;
            case ERR_BUSY_PRINTER_UPDATING:
                description = "Service is busy (Updating Printer Firmware).";
                break;
            case ERR_BUSY_OS_UPDATING:
                description = "Service is busy (Updating OS Image).";
                break;
            case ERR_BUSY_OS_KEY_LOADING:
                description = "Service is busy (Loading OS Key).";
                break;
            case ERR_BUSY_OS_DATA_UPDATING:
                description = "Service is busy (Updating Data).";
                break;
            case U_ERR_INVALID_PARAM:
                description = "<install> Invalid Parameter.";
                break;
            case U_ERR_NO_LISTENER:
                description = "<install> Unable to get method reference of the class listener.";
                break;
            case U_ERR_IO_FAIL:
                description = "<install> Some unexpected internal error occurred.";
                break;
            case U_ERR_BAD_PACKAGE:
                description = "<install> The signed installation package contains bad format.";
                break;
            case U_ERR_NO_CA_KEY:
                description = "<install> The CA public key specified by certificate does not exist.";
                break;
            case U_ERR_KEY_IN_BLACKLIST:
                description = "<install> The associated public key or its CA key is in blacklist.";
                break;
            case U_ERR_BAD_CERT:
                description = "<install> The certificate does not match the specific public key modulus.";
                break;
            case U_ERR_NO_DATA:
                description = "<install> No authenticated data found.";
                break;
            case U_ERR_BAD_DATA:
                description = "<install> The data contains invalid format.";
                break;
            case U_ERR_CERT_FORMAT:
                description = "<install> Unknown certificate information format.";
                break;
            case U_ERR_CERT_TYPE:
                description = "<install> The system does not support the certificate type.";
                break;
            case U_ERR_CERT_ALGID:
                description = "<install> The system does not support the hash algorithm of certificate.";
                break;
            case U_ERR_CERT_NOT_ACTIVE:
                description = "<install> The certificate has not been activated yet.";
                break;
            case U_ERR_CERT_EXPIRED:
                description = "<install> The certificate has expired.";
                break;
            case U_ERR_CAT_FORMAT:
                description = "<install> Unknown security catalog format.";
                break;
            case U_ERR_CAT_ITEM:
                description = "<install> The system does not support the items in security catalog.";
                break;
            case U_ERR_CAT_ALGID:
                description = "<install> The system does not support the hash algorithm in catalog.";
                break;
            case U_ERR_FILE_NOT_FOUND:
                description = "<install> The file defined in catalog not found.";
                break;
            case U_ERR_AUTH_FAIL:
                description = "<install> The update authentication was failure.";
                break;
            case U_ERR_UPDATE_FAIL:
                description = "<install> The update failed in some unexpected way.";
                break;
            case U_ERR_AUTORUN_FAIL:
                description = "<install> The method failed to launch the AUTORUN program.";
                break;
            case U_ERR_KEY_IN_LOCK:
                description = "<install> The system was locked into other appDataFolder key.";
                break;
            default:
                description = "Un-Recognized Error.";
                break;
        }
        return description;
    }
}
