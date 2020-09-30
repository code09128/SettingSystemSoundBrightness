package saioapi.service.utility;

import android.os.Bundle;

import java.io.Serializable;

/**
 *  This class provide update request services
 */
public class UpdateReq implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String BUNDLE_KEY = "update_req";

    // Service action in Message.arg1

    // INTERNAL ONLY  (RFU)

    /**
     *  Indicates the action by EPP MFG mode.
     *  <p>
     *      Constant Value : 0 (0x00000000)
     *  </p>
     */
    public static final int ACTION_EPP_MFG = 0;

    /**
     *  Indicates the action by printer MFG mode.
     *  <p>
     *      Constant Value : 1 (0x00000001)
     *  </p>
     */
    public static final int ACTION_PRINTER_MFG = 1;

    // NORMAL ACTION
    /**
     *  Indicates the action by EPP.
     *  <p>
     *      Constant Value : 2 (0x00000002)
     *  </p>
     */
    public static final int ACTION_EPP = 2;

    /**
     *  Indicates the action by printer.
     *  <p>
     *      Constant Value : 3 (0x00000003)
     *  </p>
     */
    public static final int ACTION_PRINTER = 3;

    /**
     *  Indicates the action by android system.
     *  <p>
     *      Constant Value : 4 (0x00000004)
     *  </p>
     */
    public static final int ACTION_SYSTEM = 4;//scat

    /**
     *  Indicates the action by android system key.
     *  <p>
     *      Constant Value : 5 (0x00000005)
     *  </p>
     */
    public static final int ACTION_KEY = 5;//spk

    /**
     *  Indicates the action by android system data.
     *  <p>
     *      Constant Value : 6 (0x00000006)
     *  </p>
     */
    public static final int ACTION_DATA = 6; // RFU


    /**
     *  Indicates the update folder form usb disk.
     */
//    public final static String USB_UPDATE_BASE         = "/storage/udisk/Download/";

    /**
     *  Indicates the update folder form system storage.
     */
//    public final static String INTERNAL_UPDATE_BASE    = "/storage/sdcard0/Download/";

    private String mFolder;         // path of *.scat | *.spk
    private String mTarget;
    private boolean mSkipIfSameVer;     // skip update if the version is same with current
    
    // # RFU
    // delete src if updating is ok
    // -- EPP/Printer/System/Data: delete entire folder which contains *.scat
    // -- Key/APK: delete *.spk | *.apk
    private boolean mDelSrcIfOk;

    /**
     *  Indicate the target device port. Only works for Updating Firmware. Default value -1 means using default build-in port number.
     */
    private int mPort = -1;

    /**
     * Initializes a new, existing UpdateReq object with a Serializable object
     *
     * @param folderPath  The parameter is source folder path.
     * @param fileName  The parameter is source file name.
     * @param skipIfSameVer The parameter is control skip same version flag.
     * @param delSrcIfOk The parameter is control delete source file flag. (RFU)
     */
    public UpdateReq(String folderPath, String fileName, boolean skipIfSameVer, boolean delSrcIfOk){

        mFolder = folderPath;
        mTarget = fileName;
    	mSkipIfSameVer = skipIfSameVer;
        mDelSrcIfOk = delSrcIfOk;
        mPort = -1;
    }

    /**
     * Initializes a new, existing UpdateReq object with a Serializable object
     *
     * @param folderPath  The parameter is source folder path.
     * @param fileName  The parameter is source file name.
     * @param skipIfSameVer The parameter is control skip same version flag.
     * @param delSrcIfOk The parameter is control delete source file flag. (RFU)
     * @param port Specific the target device port. Set to -1 to indicate the build-in default port number.
     */
    public UpdateReq(String folderPath, String fileName, boolean skipIfSameVer, boolean delSrcIfOk, int port){

        mFolder = folderPath;
        mTarget = fileName;
        mSkipIfSameVer = skipIfSameVer;
        mDelSrcIfOk = delSrcIfOk;
        mPort = port;
    }

    /**
     * Returns the update request description.
     *
     * @param b The mapping from String values to various Parcelable types.
     *
     * @return  Return the value is update request description. If the function succeeds else return null.
     */
    public static UpdateReq getBundleData(Bundle b) {

        if(null == b) {
            return null;
        }

        return (UpdateReq)b.getSerializable(BUNDLE_KEY);
    }

    /**
     * Sets the update request description.
     *
     * @param b The mapping from String values to various Parcelable types.
     * @param updateMsg The value is UpdateReq description.
     */
    public static void setBundleData(Bundle b, UpdateReq updateMsg) {

        if(null == b || null == updateMsg) {
            return;
        }

        b.putSerializable(BUNDLE_KEY, updateMsg);
    }

    /**
     * Returns the update source folder path.
     *
     * @return The return value is the  source folder path.
     */
    public String getFolderPath (){
        return mFolder;
    }

    /**
     * Returns the update source file name.
     *
     * @return The return value is the source file name.
     */
    public String getTargetFile (){
        return mTarget;
    }

    /**
     * Returns the control skip same version flag.
     *
     * @return The return value is control skip same version flag .
     */
    public boolean getSkipIfSameVer (){
        return mSkipIfSameVer;
    }

    /**
     * Returns the control delete source file flag.
     *
     * @return The return value is control delete source file flag.
     */
    public boolean getDelSrcIfOk (){
        return mDelSrcIfOk;
    }

    /**
     * Returns the control delete source file flag.
     *
     * @return The return value is control delete source file flag.
     */
    public int getPort (){
        return mPort;
    }
}
