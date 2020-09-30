package saioapi.service.utility;

import android.os.Bundle;

import java.io.Serializable;

/**
 *  This class provide EPP information request and response services
 */
public class EppInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private static final String BUNDLE_KEY_VER = "firmware_version";
    private static final String BUNDLE_KEY_MODULE = "firmware_module_";
    private static final String BUNDLE_KEY_KCV = "firmware_kcv";

    // Service action  value carried in Message.arg1

    /**
     *  Indicates the action by get EPP version.
     *  <p>
     *      Constant Value : 1 (0x00000001)
     *  </p>
     */
    public static final int ACTION_VERSION = 1;

    /**
     *  Indicates the action by get EPP module information.
     *  <p>
     *      Constant Value : 2 (0x00000002)
     *  </p>
     */
    public static final int ACTION_MODULE = 2;

    /**
     *  Indicates the action by get EPP KCV.
     *  <p>
     *      Constant Value : 3 (0x00000003)
     *  </p>
     */
    public static final int ACTION_KCV = 3;

    /**
     *  Indicates the action by get KL02 module information.
     *  <p>
     *      Constant Value : 4 (0x00000004)
     *  </p>
     */
    public static final int ACTION_GSENSOR = 4;

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

        return (FirmwareVersion)b.getSerializable(BUNDLE_KEY_VER);
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

        b.putSerializable(BUNDLE_KEY_VER, firmwareInfo);
    }

    /**
     * Returns the module information description.
     *
     * @param b The mapping from String values to various Parcelable types.
     * @param index The means module's index number.
     *
     * @return  Return the value is module information description. If the function succeeds else return null.
     */
    public static ModuleInfo getModuleInfo(Bundle b, int index)
    {
        if(null == b)
            return null;

        return (ModuleInfo)b.getSerializable(BUNDLE_KEY_MODULE + index);
    }

    /**
     * Sets the module's information description.
     *
     * @param b The mapping from String values to various Parcelable types.
     * @param index The means module's index number.
     * @param moduleInfo The module's information description.
     */
    public static void setModuleInfo(Bundle b, int index, ModuleInfo moduleInfo)
    {
        if(null == b || null == moduleInfo)
            return;

        b.putSerializable(BUNDLE_KEY_MODULE + index, moduleInfo);
    }

    /**
     * Returns the key's check value.
     *
     * @param b The mapping from String values to various Parcelable types.
     *
     * @return  Return the value is key's check value. If the function succeeds else return null.
     */
    public static String getKcv(Bundle b)
    {
        if(null == b)
            return null;

        return (String)b.getSerializable(BUNDLE_KEY_KCV);
    }

    /**
     * Sets the key's check value to request description.
     *
     * @param b The mapping from String values to various Parcelable types.
     * @param kcv The key's slot description.
     */
    public static void setKcv(Bundle b, String kcv)
    {
        if(null == b|| null == kcv)
            return;

        b.putSerializable(BUNDLE_KEY_KCV, kcv);
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

   /*
       *  This class provide module information
       */
    public class ModuleInfo implements Serializable {

        private String strModuleID = null;
        private String strModuleName = null;
        private String strModuleVersion = null;
        private String strModuleBuild = null;
        private String strModuleChecksum = null;
        private boolean mIsLast = false;

       /**
        * Initializes a new, existing ModuleInfo object with a Serializable object
        *
        * @param ID The module's identifier.
        * @param Name The module's name.
        * @param Version The module's version.
        * @param Build The module's build number.
        * @param Checksum The module's checksum.
        * @param isLast The module is list's last.
        */
        public ModuleInfo (String ID, String Name, String Version, String Build, String Checksum, boolean isLast){

            strModuleID = ID;
            strModuleName = Name;
            strModuleVersion = Version;
            strModuleBuild = Build;
            strModuleChecksum = Checksum;
            mIsLast = isLast;
        }

       /**
        * Returns the module's name.
        *
        * @return The return value is the module's name.
        */
        public String getModuleName (){
            return strModuleName;
        }

       /**
        * Returns the module's version.
        *
        * @return The return value is the module's version.
        */
        public String getModuleVersion (){
            return strModuleVersion;
        }

       /**
        * Returns the module's build number.
        *
        * @return The return value is the module's build number.
        */
        public String getModuleBuild (){
            return strModuleBuild;
        }

       /**
        * Returns the module's identifier.
        *
        * @return The return value is the module's ID.
        */
        public String getModuleID (){
            return strModuleID;
        }

       /**
        * Returns the module's checksum.
        *
        * @return The return value is the module's checksum.
        */
        public String getModuleChecksum (){
            return strModuleChecksum;
        }

       /**
        * Returns the module list is end.
        *
        * @return If the module is last return true, else return false.
        */
        public boolean isLast (){
            return mIsLast;
        }
    }
}
