package saioapi.service.utility;

import android.os.Bundle;

import java.io.Serializable;

/**
 *  This class provide printer information request and response services
 */
public class PrinterInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String BUNDLE_KEY = "printer_version";

    // Service action value carried in Message.arg1

    /**
     *  Indicates the action by get printer version.
     *  <p>
     *      Constant Value : 1 (0x00000001)
     *  </p>
     */
    public static final int ACTION_VERSION = 1;


    /**
     * Returns the firmware version description.
     *
     * @param b The mapping from String values to various Parcelable types.
     *
     * @return  Return the value is firmware version description. If the function succeeds else return null.
     */
    public static FirmwareVersion getFirmwareVersion(Bundle b)
    {
        if(null == b)
            return null;

        return (FirmwareVersion)b.getSerializable(BUNDLE_KEY);
    }

    /**
     * Sets the firmware version description.
     *
     * @param b The mapping from String values to various Parcelable types.
     * @param firmwareInfo The value is firmware version description.
     */
    public static void setFirmwareVersion(Bundle b, FirmwareVersion firmwareInfo)
    {
        if(null == b || null == firmwareInfo)
            return;

        b.putSerializable(BUNDLE_KEY, firmwareInfo);
    }

    /*
        *  This class provide firmware information
        */
    public class FirmwareVersion implements Serializable {

        private String strModel = null;
        private String strVersion = null;
        private String strBuild = null;
        private String strSN =  null;
        private String strCID =  null;

        /**
         * Initializes a new, existing FirmwareVersion object with a Serializable object
         *
         * @param Model The model's name.
         * @param Version The model's firmware version.
         * @param Build The firmware's firmware build number.
         * @param SN The model's serial number.
         * @param CID The customer identifier form model's.
         */
        public FirmwareVersion  (String Model, String Version, String Build, String SN, String CID){

            strModel = Model;
            strVersion = Version;
            strBuild = Build;
            strSN = SN;
            strCID = CID;
        }

        /**
         * Returns the model's name.
         *
         * @return The return value is the model's name.
         */
        public String getModelName (){
            return strModel;
        }

        /**
         * Returns the model's firmware version.
         *
         * @return The return value is the model's firmware version.
         */
        public String getVersion (){
            return strVersion;
        }

        /**
         * Returns the model's firmware build number.
         *
         * @return The return value is the firmware's firmware build number.
         */
        public String getBuild (){
            return strBuild;
        }

        /**
         * Returns the model's serial number.
         *
         * @return The return value is the model's serial number.
         */
        public String getSN (){
            return strSN;
        }

        /**
         * Returns the customer identifier form model's.
         *
         * @return The return value is the customer identifier form model's.
         */
        public String getCID (){
            return strCID;
        }
    }
}
