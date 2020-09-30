//
//	Saio API, Update class
//
package saioapi.base;

import saioapi.OnEventListener;

/**
 * Besides the standard Android APK method for all application level install or update, SAIO platform 
 * request specific accessing for both security (the public key with certificate) and operation system 
 * related purposes, such as shared library (native layer API), Linux kernel, loadable driver modules 
 * and Android system image etc. But these system vital items located on different storage partition 
 * of Linux and unable to be manipulated by application itself without granting with appDataFolder permission.
 * So this class provides a safe means, that protect system from installing of the rouge module by 
 * authenticating the certificate, yet achieve the task with full privileges access right and be controlled 
 * by application.
 */
public class Update
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
	public static final int	ERR_INVALID_PARAM = 0x0000E006;
	
	/** Unable to get method reference of the class listener. */
	public static final int	ERR_NO_LISTENER = 0x0000E00C;
	
	/** Some unexpected internal error occurred. */
	public static final int	ERR_IO_FAIL = 0x0000E00E;
	
	/**	The signed installation package contains bad format. */
	public static final int ERR_BAD_PACKAGE = 0x0000E100;
	
	/** The CA public key specified by certificate does not exist. */
	public static final int ERR_NO_CA_KEY = 0x0000E102;
	
	/** The associated public key or its CA key is in blacklist. */
	public static final int ERR_KEY_IN_BLACKLIST = 0x0000E103;
	
	/** The certificate doesn&rsquo;t match the specific public key modulus. */
	public static final int ERR_BAD_CERT = 0x0000E104;
	
	/** No authenticated data found. */
	public static final int ERR_NO_DATA = 0x0000E106;
	
	/** The data contains invalid format. */
	public static final int ERR_BAD_DATA = 0x0000E108;
	
	/** Unknown certificate information format. */
	public static final int ERR_CERT_FORMAT = 0x0000E10A;
	
	/** The system does not support the certificate type. */
	public static final int ERR_CERT_TYPE = 0x0000E10C;
	
	/** The system does not support the hash algorithm of certificate. */
	public static final int ERR_CERT_ALGID = 0x0000E10E;
	
	/** The certificate has not been activated yet. */
	public static final int ERR_CERT_NOT_ACTIVE = 0x0000E110;
	
	/** The certificate has expired. */
	public static final int ERR_CERT_EXPIRED = 0x0000E112;
	
	/** Unknown security catalog format. */
	public static final int ERR_CAT_FORMAT = 0x0000E114;
	
	/** The system does not support the items in security catalog. */
	public static final int ERR_CAT_ITEM = 0x0000E116;
	
	/** The system does not support the hash algorithm in catalog. */
	public static final int ERR_CAT_ALGID = 0x0000E118;
	
	/** The file defined in catalog not found. */
	public static final int ERR_FILE_NOT_FOUND = 0x0000E11A;
	
	/** The update authentication was failure. */
	public static final int ERR_AUTH_FAIL = 0x0000E11C;
	
	/** The update failed in some unexpected way. */
	public static final int ERR_UPDATE_FAIL = 0x0000E11E;
	
	/** The method failed to launch the AUTORUN program. */
	public static final int ERR_AUTORUN_FAIL = 0x0000E120;
	
	/** The system was locked into other appDataFolder key. */
	public static final int ERR_KEY_IN_LOCK = 0x0000E122;
	
	/** Indicates to get detail error code by using methods. */
	public static final int	ERR_OPERATION = 0xFFFFFFFF;
	
	/**
	 * The listener that receives notifications when an update event is triggered.
	 */
	private OnEventListener mOnEventListener = null;
	
	//	
   	//	Methods
	//
	/**
	 * The method retrieves the certificate information of the signed install package.
	 * @param spk The data array contains the signed installation package data.
	 * @param cert The data array(minimum 600 bytes)to receive the certificate (certificate info plus digital signature) formatted as below:
	 * <br /><br />
	 * <table border=1>
	 *   <thead><tr><th>Data Element</th><th>Format</th><th>Length</th><th>Description</th></tr></thead>
	 *   <tbody>
	 *     <tr><td>Version</td><td>Binary</td><td>4</td><td>Version information</td></tr>
	 *     <tr><td>Comment</td><td>ASCII</td><td>16</td><td>Comment about this update task</td></tr>
	 *     <tr><td>Effective date</td><td>Binary</td><td>4</td><td>Activate UTC YYYYMMDD in BCD</td></tr>
	 *     <tr><td>Expiration date</td><td>Binary</td><td>4</td><td>Expire UTC YYYYMMDD in BCD</td></tr>
	 *     <tr><td>Type</td><td>Binary</td><td>1</td><td>Specify the type of the Certificate</td></tr>
	 *     <tr><td>CA-ID</td><td>Binary</td><td>16</td><td>CA Identifier of the Certificate Issuer</td></tr>
	 *     <tr><td>Reserved</td><td>Binary</td><td>26</td><td>Reserved for future use</td></tr>
	 *     <tr><td>Hash Algorithm</td><td>Binary</td><td>1</td><td>0x00 = MD2<br />0x01 = MD4<br />
	 *     0x02 = MD5<br />0x03 = SHA-1 (160bits)<br />0x04 = SHA-2 (256bits)<br />0x05 = SHA-2 (384bits)<br />
	 *     0x06 = SHA-2 (512bits)</td></tr>
	 *     <tr><td>Data signature</td><td>Binary</td><td>128/512</td><td>Digital signature signed by the issuer</td></tr>
	 *   </tbody>
	 * </table>
	 * <br />
	 * @return Return zero if there is no error else nonzero error code defined in class constants.
	 */
	public native int certificate(byte[] spk,byte[] cert);
	
	/**
	 * This method installs or update the public key or images/files declared in the signed installation package.
	 * <p>Remarks:<br />
	 * This method checks the signed install package data against the certificate. Provided the authentication 
	 * succeeds, it will perform the actual installation task according the Type value defined in certificate 
	 * information and Item value defined in security catalog (if there is any).
	 * <br />
	 * <table border=1>
	 *   <thead><tr><th colspan="2">The type of the Certificate</th></tr></thead>
	 *   <thead><tr><th>Value</th><th>Description</th></tr></thead>
	 *   <tbody>
	 *     <tr><td>0x00</td><td>Authenticated data contains a public key (Microsoft key BLOB format) 
	 *     of a CA<br /> to be installed /updated to the system, the CA ID is defined in certificate</td></tr>
	 *     <tr><td>0x01</td><td>Authenticated data is a security catalog contains a list of files (programs 
	 *     or<br /> parameters) to be installed/updated to the system</td></tr>
	 *   </tbody>
	 * </table>
	 * <br />
	 * The security catalog is a table of files (images, programs and parameter) or subdirectories that 
	 * represents the installation tree where each index has been defined as following format:
	 * <br /><br />
	 * <table border=1>
	 *   <thead><tr><th>Data Element</th><th>Format</th><th>Len</th><th>Description</th></tr></thead>
	 *   <tbody>
	 *     <tr><td>File Name</td><td>ASCII</td><td>100</td><td>Name of the file or subdirectory without path</td></tr>
	 *     <tr><td>Path Name</td><td>ASCII</td><td>100</td><td>Full path where the above file to be updated</td></tr>
	 *     <tr><td>File Attribute</td><td>Binary</td><td>4</td><td>Permission setting of the files or subdirectories. Ignored for image items</td></tr>
	 *     <tr><td>Item</td><td>Binary</td><td>2</td><td>What is going to be updated</td></tr>
	 *     <tr><td>Reserved</td><td>Binary</td><td>8</td><td>Reserved for future use</td></tr>
	 *     <tr><td>Hash</td><td>Binary</td><td>32</td><td>Message Digest (256 bits SHA-2 only) of the file</td></tr>
	 *   </tbody>
	 * </table>
	 * <br />
	 * The items under security catalog are defined as below:
	 * <br /><br />
	 * <table border=1>
	 *   <thead><tr><th colspan="2">Items</th><th></th></tr></thead>
	 *   <thead><tr><th>1<sup>st</sup> byte</th><th>2<sup>nd</sup> byte</th><th>Description</th></tr></thead>
	 *   <tbody>
	 *     <tr><td>0x00</td><td>0</td><td>Updates System Loader image</td></tr>
	 *     <tr><td>0x01</td><td>0</td><td>Updates Boot (kernel + Ram disk) image</td></tr>
	 *     <tr><td>0x02</td><td>0</td><td>Reserved</td></tr>
	 *     <tr><td>0x03</td><td>0</td><td>Updates Android System image</td></tr>
	 *     <tr><td>0x04</td><td>0 or 0x80 *</td><td>Creates, updates or delete subfolder</td></tr>
	 *     <tr><td>0x05</td><td>0 or 0x80 *</td><td>Updates or adds the file</td></tr>
	 *     <tr><td>0x06</td><td>0</td><td>The AUTORUN program, the file will be executed<br /> automatically 
	 *     during Update process and only this {@link #install}<br /> method is able to launch the program</td></tr>
	 *     <tr><td>0x07</td><td>0</td><td>Reserved</td></tr>
	 *   </tbody>
	 * </table>
	 * *Note: if the most significant bit of second byte is set, the item is going to be removed.
	 * <br /><br />
	 * All Executable and Linking files (The file with ELF header) listed in catalog table must embed 
	 * a digital signature signed by the CA who public has been installed onto the system already.
	 * @param srcDir A null-terminate string that specifies where the files listed in catalog are located. It should be ignored if the certificate is for public key installation.
	 * @param spk The data array contains the signed installation package data.
	 * @return Return zero if there is no error else nonzero error code defined in class constants.
	 */
	public native int install(String srcDir, byte[] spk);
	
	/**
	 * This method returns the total number of the public key which has been installed onto the system.
	 * @return The total number of the installed public keys.
	 */
	public native int getPubkeyNum();
	
	/**
	 * This method retrieves the certificate information under the given public key index which has been installed onto the system.
	 * @param idx The index number (starting from 1) of the system public key table.
	 * @param cert The data array(minimum 600 bytes) to receive the certificate (certificate info plus digital signature) defined on above {@link #certificate} method.
	 * @return Return zero if there is no error else nonzero error code defined in class constants.
	 */
	public native int getPubkeyCert(int idx,byte[] cert);
	
	/**
	 * The method get called during the {@link #install} method processing to indicate the update progress.
	 * @param percentage The percentage (0-100) to report the progress of the running updating task.
	 */
	public void listener(int percentage)
	{
		// 
		//	Call your real function to handle event here
		//
		if (null != mOnEventListener)
		{
			mOnEventListener.onEvent(0, percentage);
		}
	}
    
    /**
     * Get the registered Listener that handles the update event.
     * @return The callback to be invoked with a update event is triggered, 
     *         or null id no callback has been set.
     */
    public final OnEventListener getOnEventListener()
    {
        return mOnEventListener;
    }
    
    /**
     * Register a callback to be invoked when a update event is triggered.
     * @param listener The callback that will be invoked.
     */
    public void setOnEventListener(OnEventListener listener)
    {
        mOnEventListener = listener;
    }
    
}
