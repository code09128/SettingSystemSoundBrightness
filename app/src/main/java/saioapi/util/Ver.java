//
//	Saio API, version class
//
package saioapi.util;

import android.content.Context;

/**
 * This class provides methods to get Saio version services.
 */
public class Ver
{
    static
    {
        //
        //	Load the corresponding library
        //
        System.loadLibrary("SaioUtil");
    }
    //
    //	Constants (Error code and Events)
    //
    /** The inputted parameters are not valid. */
    public static final int ERR_INVALID_PARAM       = 0x0000E000;
    
    /** The info is not found for the given type. */
    public static final int ERR_NOT_FOUND           = 0x0000E002;
    
    /** libSaioBase. */
    public static final short LIB_SAIO_BASE         = 0x00;
    
    /** libSaioReader. */
    public static final short LIB_SAIO_READER       = 0x01;
    
    /** libSaioPhone. */
    public static final short LIB_SAIO_PHONE        = 0x02;
    
    /** libSaioVer. */
    public static final short LIB_SAIO_VER          = 0x03;
    
    /** libSaioUtil. */
    public static final short LIB_SAIO_UTIL         = 0x03;
    
    /** All Saio library version. */
    public static final short LIB_SAIO_ALL          = 0xFF;
    
    
    //	
    //	Methods
    //
    
    /**
     * The method retrieves the Saio library version.
     * <br />The information table:
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>Specific Information</th></tr></thead>
     *   <tbody>
     *     <tr><td>LIB_SAIO_BASE</td><td>The version of Saio Base library</td></tr>
     *     <tr><td>LIB_SAIO_READER</td><td>The version of Saio Reader library</td></tr>
     *     <tr><td>LIB_SAIO_PHONE</td><td>The version of Saio Phone library</td></tr>
     *     <tr><td>LIB_SAIO_VER</td><td>The version of Saio Util library (libSaioVer.so is renamed to libSaioUtil.so)</td></tr>
     *     <tr><td>LIB_SAIO_UTIL</td><td>The version of Saio Util library</td></tr>
     *     <tr><td>LIB_SAIO_ALL</td><td>All library version</td></tr>
     *   </tbody>
     * </table>
     * 
     * @param context The context of your application
     * @param type The system information to be retrieved defined in above table.
     * @param info The data array (minimum 256 bytes) to receive the Saio library version.
     * @return zero if there is no error else nonzero error code defined in class constants.
     */
    public static int getSaioVersion(Object context, int type, byte[] info){
        return getSaioVersion(((Context) context).getPackageName(), type, info);
    }
    
    /**
     * The method retrieves the Saio library version.
     * <br />The information table:
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>Specific Information</th></tr></thead>
     *   <tbody>
     *     <tr><td>LIB_SAIO_BASE</td><td>The version of Saio Base library</td></tr>
     *     <tr><td>LIB_SAIO_READER</td><td>The version of Saio Reader library</td></tr>
     *     <tr><td>LIB_SAIO_PHONE</td><td>The version of Saio Phone library</td></tr>
     *     <tr><td>LIB_SAIO_VER</td><td>The version of Saio Util library (libSaioVer.so is renamed to libSaioUtil.so)</td></tr>
     *     <tr><td>LIB_SAIO_UTIL</td><td>The version of Saio Util library</td></tr>
     *     <tr><td>LIB_SAIO_ALL</td><td>All library version</td></tr>
     *   </tbody>
     * </table>
     * 
     * @param packagename The package name of your application
     * @param type The system information to be retrieved defined in above table.
     * @param info The data array (minimum 256 bytes) to receive the Saio library version.
     * @return zero if there is no error else nonzero error code defined in class constants.
     */
    public static native int getSaioVersion(String packagename, int type, byte[] info);
    
    /**
     * The method retrieves the U-boot env version.
     * 
     * @param info The data array (minimum 256 bytes) to receive the U-boot env version.
     * @return zero if there is no error else nonzero error code defined in class constants.
     */
    public static native int getUBootEnvVersion(byte[] info);
   
    /**
     * The method retrieves the U-boot custom settings.
     * 
     * @param info The data array (minimum 256 bytes) to receive the custom settings.
     * @return zero if there is no error else nonzero error code defined in class constants.
     */
    public static native int getCustomSettings(byte[] info);
 
}
