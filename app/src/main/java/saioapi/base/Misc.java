//
//	Saio API, miscellanea class
//
package saioapi.base;

/**
 * This class provides methods to access some SAIO miscellanea services.
 */
public class Misc
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
    /** The inputted parameters are not valid. */
    public static final int ERR_INVALID_PARAM       = 0x0000E006;
    
    /** The method is not supported for the given device. */
    public static final int ERR_NOT_SUPPORT         = 0x0000E008;
    
    /** Some unexpected internal error occurred. */
    public static final int ERR_IO_FAIL             = 0x0000E00E;
    
    /** Failed to copy the history log to user specified file. */
    public static final int ERR_FETCH_LOG_FAIL      = 0x0000E010;
    
    /** The password is incorrect. */
    public static final int ERR_PWD_INCORRECT       = 0x0000E012;
    
    /** The password is under protected (refuse verification a while). */
    public static final int ERR_PWD_PROTECTED       = 0x0000E014;
    
    /** Indicates to get detail error code by using {@link #lastError} method. */
    public static final int ERR_OPERATION           = 0xFFFFFFFF;
    
    /** Retrieves the current switch status only. */
    public static final int CASH_DRAWER_QUERY       = 0x00;
    
    /** Set the switch status on, the drawer can be opened then. */
    public static final int CASH_DRAWER_OPEN        = 0x01;
    
    /** Set the switch status to off, the drawer is locked. */
    public static final int CASH_DRAWER_CLOSE       = 0x02;
    
    /** Retrieves the current USB device mode only. */
    public static final int USBDEV_MODE_QUERY       = 0x00;

    /** Disable USB device function. */
    public static final int USBDEV_DISABLE          = 0x01;

    /** Standard Android Gadget mode (default). */
    public static final int USBDEV_ANDROID          = 0x02;

    /** Serial mode with CDC ACM support  */
    public static final int USBDEV_SERIAL_CDC       = 0x03;

    /** The product or hardware information. */
    public static final short INFO_PRODUCT          = 0x00;
    
    /** The product version. */
    public static final short INFO_PRODUCT_VER      = 0x01;
    
    /** The manufacture related information. */
    public static final short INFO_MANUFACTURE      = 0x02;
    
    /** The device serial number. */
    public static final short INFO_SERIAL_NUM       = 0x03;
    
    /** The boot loader (u-boot) version. */
    public static final short INFO_LOADER_VER       = 0x04;
    
    /** The system appDataFolder key status (locked or unlocked). */
    public static final short INFO_ROOTKEY_STATUS   = 0x05;

    /** Indicates if the secure system is enabled */
    public static final short INFO_SECURE_STAT      = 0x06;

    //	
    //	Methods
    //
    /**
     * The method retrieves the specified system information.
     * <br />The information table:
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>Specific Information</th></tr></thead>
     *   <tbody>
     *     <tr><td>INFO_PRODUCT</td><td>The product or hardware information</td></tr>
     *     <tr><td>INFO_PRODUCT_VER</td><td>The product version</td></tr>
     *     <tr><td>INFO_MANUFACTURE</td><td>The manufacture related information</td></tr>
     *     <tr><td>INFO_SERIAL_NUM</td><td>The device serial number</td></tr>
     *     <tr><td>INFO_LOADER_VER</td><td>The boot loader (u-boot) version</td></tr>
     *     <tr><td>INFO_ROOTKEY_STATUS</td><td>The system appDataFolder key status (locked or unlocked).</td></tr>
     *     <tr><td>INFO_SECURE_STAT</td><td>Indicates if the secure system is enabled</td></tr>
     *   </tbody>
     * </table>
     * @param type The system information to be retrieved defined in above table.
     * @param info The data array (minimum 20 bytes) to receive the specified information.
     * @return zero if there is no error else nonzero error code defined in class constants.
     */
    public static native int getSystemInfo(short type, byte[] info);
    
    /**
     * The method programs or retrieves the switch status of the given cash drawer 
     * which attaches to SAIO device.
     * <br />The operation mode:
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>Cashdrawer behaviors</th></tr></thead>
     *   <tbody>
     *     <tr><td>CASH_DRAWER_QUERY</td><td>Retrieves the current switch status only</td></tr>
     *     <tr><td>CASH_DRAWER_OPEN</td><td>Set the switch status on, the drawer can be opened then</td></tr>
     *     <tr><td>CASH_DRAWER_CLOSE</td><td>Set the switch status to off, the drawer is locked</td></tr>
     *   </tbody>
     * </table>
     * @param dev Logical ID of the cash drawer device. Ignored by now.
     * @param operate The operation mode that specify what to do
     * @return If successful, the drawer status is returned (zero if the switch 
     *          is off else one indicating witch is on), otherwise a {@link #ERR_OPERATION} 
     *          is returned and the method {@link #lastError} can be used to indicate 
     *          the error.
     */
    public native int cashDrawer(short dev, int operate);
    
    /**
     * This method retrieves the current tamper status. The status is defined as below table:
     * <br />
     * <table border=1>
     *   <thead><tr><th>BIT</th><th>Function</th><th>BIT=0</th><th>BIT=1</th></tr></thead>
     *   <tbody>
     *     <tr><td>0</td><td>The Root file system tampered</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>1</td><td>The Initial public key tampered</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>2</td><td>The user installed public key tampered</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>3</td><td>The Kernel Image tampered</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>4</td><td>The Device configuration tampered</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>5</td><td>The vital daemon tampered</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>6</td><td>The public key blacklist tampered</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>7</td><td>The peripheral device tampered</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>8 - 31</td><td>Unused</td><td></td><td></td></tr>
     *   </tbody>
     * </table>
     * @return If successful, the tamper status is returned, otherwise a {@link #ERR_OPERATION} is returned and 
     *          the method {@link #lastError} can be used to indicate the error.
     */
    public native int tamperStatus();
    
    /**
     * This method specifies the configuration of the Android system log filter in order to help user focusing 
     * on the interested entries.
     * <p>Remarks:<br />
     * In case the device has been assigned with a password, there are has only three chances to verify the 
     * correct password in a row, or the system will refuse further verification within 60 seconds for security.
     * 
     * The filter specification is defined fully compliance in the "logcat" command of the Android system which 
     * allows to specify filter-spec. as argument as:
     *      tag_process1:priority_code1 tag_process2:priority_code2 ...
     * where the log will be recorded from specified tag processes with priorities higher than the specified code 
     * plus all log entries that generated from unspecified tags (a wildcard "*" can be used to represent all tags).
     * 
     * The available priority code are listed below:
     * V    - verbose (lowest priority)
     * D    - debug
     * W    - warning
     * E    - error
     * F    - fatal
     * S    - silent (highest, on which nothing to be logged)
     * 
     * For example:
     * "MsrApi:V *:E" will record entire MSR process and all tags error and fatal log.
     * "MsrApi:V ScrApi:V *:S" will record the log related to MSR and SCR processes only.
     * 
     * The default filter is "*:S".
     * @param filter The data array contains null terminated string of the new log filter configuration.
     * @param password The data array contains null terminated string of the device password (if the device has 
     *          not been assigned with a password, apply "admin" here as default).
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public int setLogFilter(String filter, String password)
    {
        return setLogFilter(_stringToJniBytes(filter), _stringToJniBytes(password));
    }
    
    /**
     * This method specifies the configuration of the Android system log filter in order to help user focusing 
     * on the interested entries.
     * <p>Remarks:<br />
     * In case the device has been assigned with a password, there are has only three chances to verify the 
     * correct password in a row, or the system will refuse further verification within 60 seconds for security.
     * 
     * The filter specification is defined fully compliance in the "logcat" command of the Android system which 
     * allows to specify filter-spec. as argument as:
     *      tag_process1:priority_code1 tag_process2:priority_code2 ...
     * where the log will be recorded from specified tag processes with priorities higher than the specified code 
     * plus all log entries that generated from unspecified tags (a wildcard "*" can be used to represent all tags).
     * 
     * The available priority code are listed below:
     * V    - verbose (lowest priority)
     * D    - debug
     * W    - warning
     * E    - error
     * F    - fatal
     * S    - silent (highest, on which nothing to be logged)
     * 
     * For example:
     * "MsrApi:V *:E" will record entire MSR process and all tags error and fatal log.
     * "MsrApi:V ScrApi:V *:S" will record the log related to MSR and SCR processes only.
     * 
     * The default filter is "*:S".
     * @param filter The data array contains null terminated string of the new log filter configuration.
     * @param password The data array contains null terminated string of the device password (if the device has 
     *          not been assigned with a password, apply "admin" here as default).
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public native int setLogFilter(byte[] filter, byte[] password);
    
    /**
     * This method retrieves the current configuration of the Android system log filter.
     * @param filter The data array contains null terminated string of the new log filter configuration.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public native int getLogFilter(byte[] filter);
    
    /**
     * This method fetches the up-to-date recorded log history from compressed database, converts to plaintext 
     * format and copies to a user specified file for viewing.
     * @param filename A null terminated string to specify the full path name of the file to receive the plaintext log.
     * @param clear Indicates if entire log should be removed after retrieving. Set integer 1 means remove entire log and integer 0 means not.  
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public int fetchLog(String filename, int clear)
    {
        return fetchLog(_stringToJniBytes(filename), clear);
    }
    
    /**
     * This method fetches the up-to-date recorded log history from compressed database, converts to plaintext 
     * format and copies to a user specified file for viewing.
     * @param filename A null terminated string to specify the full path name of the file to receive the plaintext log.
     * @param clear Indicates if entire log should be removed after retrieving. Set integer 1 means remove entire log and integer 0 means not. 
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public native int fetchLog(byte[] filename, int clear);
    
    /**
     * This method is used to verify that the associated password is correct for the device.
     * <p>Remarks:<br />
     * There are has only three chances to verify the correct password in a row, or this API function will 
     * refuse further verification within 60 seconds for security.
     * @param password The data array contains null terminated string of the device password to be verified.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public int verifyPassword(String password)
    {
        return verifyPassword(_stringToJniBytes(password));
    }
    
    /**
     * This method is used to verify that the associated password is correct for the device.
     * <p>Remarks:<br />
     * There are has only three chances to verify the correct password in a row, or this API function will 
     * refuse further verification within 60 seconds for security.
     * @param password The data array contains null terminated string of the device password to be verified.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public native int verifyPassword(byte[] password);

    /**
     * This method is used to assign new password for the device. The caller must be system application or
     * appDataFolder to have full privileges to access this function.
     * <p>Remarks:<br />
     * There are has only three chances to verify the old password in a row, or this API function will
     * refuse further operation within 60 seconds for security.
     * @param old_password The data array contains null terminated string of the old password to be verified.
     * @param new_password The data array contains null terminated string of the new password to be assigned.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public int changePassword(String old_password, String new_password)
    {
        return changePassword(_stringToJniBytes(old_password), _stringToJniBytes(new_password));
    }

    /**
     * This method is used to assign new password for the device. The caller must be system application or
     * appDataFolder to have full privileges to access this function.
     * <p>Remarks:<br />
     * There are has only three chances to verify the old password in a row, or this API function will
     * refuse further operation within 60 seconds for security.
     * @param old_password The data array contains null terminated string of the old password to be verified.
     * @param new_password The data array contains null terminated string of the new password to be assigned.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public native int changePassword(byte[] old_password, byte[] new_password);

    /**
     * This method is used to program or retrieve the current USB device (slave side) operation mode.
     * <p>Remarks:<br />
     * The change will take effect on next system reboot.
     * @param operate The operation mode.
     * <br />The operation mode that specify what to do:
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>USB Device mode</th></tr></thead>
     *   <tbody>
     *     <tr><td>USBDEV_MODE_QUERY</td><td>Retrieves the current USB device mode only</td></tr>
     *     <tr><td>USBDEV_DISABLE</td><td>Disable USB device function</td></tr>
     *     <tr><td>USBDEV_ANDROID</td><td>Standard Android Gadget mode (default)</td></tr>
     *     <tr><td>USBDEV_SERIAL_CDC</td><td>Serial mode with CDC ACM support </td></tr>
     *   </tbody>
     * </table>
     * @return If successful,the USB device mode is returned (defined on above table), otherwise a ERR_OPERATION is returned and the method lastError can be used to indicate the error.
     */
    public native int usbDeviceMode(int operate);

    /**
     * This  method  assigns  the  network  IP  address  of  the  Cradle  device  which  the  SAIO plan to connect for USB and UART/serial over IP functions.
     * <p>Remarks:<br />
     * The  assignment will take effect right away, If the SAIO already connected to another Cradle device
     * with the IP differ from the new setting, the connection should be dropped and try the new connect with the new IP then.
     * @param ipaddr The data array contains null terminated string of the IP address assignment, formatted as
     *               four numbers separated by period/dot; each number can be zero to 255.
     *               The data length should less than 16 and more than 8(include null terminated).
     * @param password The data array contains null terminated string of the device password (if the device has
     *               not been assigned with a password, apply "admin" here as default.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public int setCradleIp(String ipaddr, String password)
    {
        return setCradleIp(_stringToJniBytes(ipaddr),_stringToJniBytes(password));
    }

    /**
     * This  method  assigns  the  network  IP  address  of  the  Cradle  device  which  the  SAIO plan to connect for USB and UART/serial over IP functions.
     * <p>Remarks:<br />
     * The  assignment will take effect right away, If the SAIO already connected to another Cradle device
     * with the IP differ from the new setting, the connection should be dropped and try the new connect with the new IP then.
     * @param ipaddr The data array contains null terminated string of the IP address assignment,
     *               formatted as four numbers separated by period/dot;each number can be zero to 255.
     *               The data length should less than 16 and more than 8(include null terminated).
     * @param password The data array contains null terminated string of the device password (if the device has
     *               not been assigned with a password, apply "admin" here as default.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public native int setCradleIp(byte[] ipaddr, byte[] password);

    /**
     * This method retrieves the current Cradle IP address assigned to the SAIO.
     * @param ipaddr The data array to receive the current IP address configuration. Remind that data length should more than 16 to avoid missing data.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public native int getCradleIp(byte[] ipaddr);

    /**
     * Retrieves the last error occurs on the SAIO miscellanea services.
     * @return Return zero if there is no error else nonzero error code defined 
     *          in class constants.
     */
    public native int lastError();
    
    private byte[] _stringToJniBytes(String s)
    {
       if(null == s)
           s = "";
       
       int l = s.length();
       byte[] b = new byte[l + 1];
       b[l] = 0;
       System.arraycopy(s.getBytes(), 0, b, 0, l);
       
       return b;
    }
    
}
