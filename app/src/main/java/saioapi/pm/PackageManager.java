package saioapi.pm;

import android.content.Context;
import android.net.Uri;

import com.xac.packagemanager.OnPMObserver;
import com.xac.packagemanager.PackageAPI;


/**
 * This class defines the Package Manager APIs to be used by the applications.
 */
public class PackageManager {
    private PackageAPI mPackageAPI;
    
    public PackageManager(Context context){
        mPackageAPI = new PackageAPI(context);
    }
    
    //
    //  Constants (Error code and Events)
    //
    /**
     * Return action: this is passed to the {@link saioapi.OnEventListener} on install action.
     */
    public static final int ACTION_INSTALL = 0;

    /**
     * Return action: this is passed to the {@link saioapi.OnEventListener} on delete action.
     */
    public static final int ACTION_DELETE = 1;
    
    /**
     * Return action: this is passed to the {@link saioapi.OnEventListener} on unknown action.
     */
    public static final int ACTION_UNKNOWN = -1;

    
    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} on success.
     */
    public static final int INSTALL_SUCCEEDED = 1;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if the package is
     * already installed.
     */
    public static final int INSTALL_FAILED_ALREADY_EXISTS = -1;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if the package archive
     * file is invalid.
     */
    public static final int INSTALL_FAILED_INVALID_APK = -2;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if the URI passed in
     * is invalid.
     */
    public static final int INSTALL_FAILED_INVALID_URI = -3;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if the package manager
     * service found that the device didn't have enough storage space to install the app.
     */
    public static final int INSTALL_FAILED_INSUFFICIENT_STORAGE = -4;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if a
     * package is already installed with the same name.
     */
    public static final int INSTALL_FAILED_DUPLICATE_PACKAGE = -5;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the requested shared user does not exist.
     */
    public static final int INSTALL_FAILED_NO_SHARED_USER = -6;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * a previously installed package of the same name has a different signature
     * than the new package (and the old package's data was not removed).
     */
    public static final int INSTALL_FAILED_UPDATE_INCOMPATIBLE = -7;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the new package is requested a shared user which is already installed on the
     * device and does not have matching signature.
     */
    public static final int INSTALL_FAILED_SHARED_USER_INCOMPATIBLE = -8;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the new package uses a shared library that is not available.
     */
    public static final int INSTALL_FAILED_MISSING_SHARED_LIBRARY = -9;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the new package uses a shared library that is not available.
     */
    public static final int INSTALL_FAILED_REPLACE_COULDNT_DELETE = -10;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the new package failed while optimizing and validating its dex files,
     * either because there was not enough storage or the validation failed.
     */
    public static final int INSTALL_FAILED_DEXOPT = -11;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the new package failed because the current SDK version is older than
     * that required by the package.
     */
    public static final int INSTALL_FAILED_OLDER_SDK = -12;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the new package failed because it contains a content provider with the
     * same authority as a provider already installed in the system.
     */
    public static final int INSTALL_FAILED_CONFLICTING_PROVIDER = -13;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the new package failed because the current SDK version is newer than
     * that required by the package.
     */
    public static final int INSTALL_FAILED_NEWER_SDK = -14;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the new package failed because it has specified that it is a test-only
     * flag.
     */
    public static final int INSTALL_FAILED_TEST_ONLY = -15;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the package being installed contains native code, but none that is
     * compatible with the the device's CPU_ABI.
     */
    public static final int INSTALL_FAILED_CPU_ABI_INCOMPATIBLE = -16;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the new package uses a feature that is not available.
     */
    public static final int INSTALL_FAILED_MISSING_FEATURE = -17;

    // ------ Errors related to sdcard
    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * a secure container mount point couldn't be accessed on external media.
     */
    public static final int INSTALL_FAILED_CONTAINER_ERROR = -18;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the new package couldn't be installed in the specified install
     * location.
     */
    public static final int INSTALL_FAILED_INVALID_INSTALL_LOCATION = -19;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the new package couldn't be installed in the specified install
     * location because the media is not available.
     */
    public static final int INSTALL_FAILED_MEDIA_UNAVAILABLE = -20;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the new package couldn't be installed because the verification timed out.
     */
    public static final int INSTALL_FAILED_VERIFICATION_TIMEOUT = -21;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the new package couldn't be installed because the verification did not succeed.
     */
    public static final int INSTALL_FAILED_VERIFICATION_FAILURE = -22;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the package changed from what the calling program expected.
     */
    public static final int INSTALL_FAILED_PACKAGE_CHANGED = -23;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the new package is assigned a different UID than it previously held.
     */
    public static final int INSTALL_FAILED_UID_CHANGED = -24;

    /**
     * Installation return code: this is passed to the {@link saioapi.OnEventListener} if
     * the new package has an older version code than the currently installed package.
     */
    public static final int INSTALL_FAILED_VERSION_DOWNGRADE = -25;

    /**
     * Installation parse return code: this is passed to the {@link saioapi.OnEventListener} if 
     * the parser was given a path that is not a file, or does not end with the expected
     * '.apk' extension.
     */
    public static final int INSTALL_PARSE_FAILED_NOT_APK = -100;

    /**
     * Installation parse return code: this is passed to the {@link saioapi.OnEventListener} 
     * if the parser was unable to retrieve the AndroidManifest.xml file.
     */
    public static final int INSTALL_PARSE_FAILED_BAD_MANIFEST = -101;

    /**
     * Installation parse return code: this is passed to the {@link saioapi.OnEventListener} 
     * if the parser encountered an unexpected exception.
     */
    public static final int INSTALL_PARSE_FAILED_UNEXPECTED_EXCEPTION = -102;

    /**
     * Installation parse return code: this is passed to the {@link saioapi.OnEventListener} 
     * if the parser did not find any certificates in the .apk.
     */
    public static final int INSTALL_PARSE_FAILED_NO_CERTIFICATES = -103;

    /**
     * Installation parse return code: this is passed to the {@link saioapi.OnEventListener} 
     * if the parser found inconsistent certificates on the files in the .apk.
     */
    public static final int INSTALL_PARSE_FAILED_INCONSISTENT_CERTIFICATES = -104;

    /**
     * Installation parse return code: this is passed to the {@link saioapi.OnEventListener} 
     * if the parser encountered a CertificateEncodingException in one of the
     * files in the .apk.
     */
    public static final int INSTALL_PARSE_FAILED_CERTIFICATE_ENCODING = -105;

    /**
     * Installation parse return code: this is passed to the {@link saioapi.OnEventListener} 
     * if the parser encountered a bad or missing package name in the manifest.
     */
    public static final int INSTALL_PARSE_FAILED_BAD_PACKAGE_NAME = -106;

    /**
     * Installation parse return code: this is passed to the {@link saioapi.OnEventListener} 
     * if the parser encountered a bad shared user id name in the manifest.
     */
    public static final int INSTALL_PARSE_FAILED_BAD_SHARED_USER_ID = -107;

    /**
     * Installation parse return code: this is passed to the {@link saioapi.OnEventListener} 
     * if the parser encountered some structural problem in the manifest.
     */
    public static final int INSTALL_PARSE_FAILED_MANIFEST_MALFORMED = -108;

    /**
     * Installation parse return code: this is passed to the {@link saioapi.OnEventListener} 
     * if the parser did not find any actionable tags (instrumentation or application)
     * in the manifest.
     */
    public static final int INSTALL_PARSE_FAILED_MANIFEST_EMPTY = -109;

    /**
     * Installation failed return code: this is passed to the {@link saioapi.OnEventListener} 
     * if the system failed to install the package because of system issues.
     */
    public static final int INSTALL_FAILED_INTERNAL_ERROR = -110;

    /**
     * Installation failed return code: this is passed to the {@link saioapi.OnEventListener} 
     * if the system failed to install the package because the user is restricted from 
     * installing apps.
     */
    public static final int INSTALL_FAILED_USER_RESTRICTED = -111;
    
    /**
     * Return code for when package deletion succeeds. This is passed to the
     * {@link saioapi.OnEventListener} if the system succeeded in deleting the package.
     */
    public static final int DELETE_SUCCEEDED = 1;

    /**
     * Deletion failed return code: this is passed to the {@link saioapi.OnEventListener} if the 
     * system failed to delete the package for an unspecified reason.
     */
    public static final int DELETE_FAILED_INTERNAL_ERROR = -1;

    /**
     * Deletion failed return code: this is passed to the {@link saioapi.OnEventListener} if the 
     * system failed to delete the package because it is the active DevicePolicy
     * manager.
     */
    public static final int DELETE_FAILED_DEVICE_POLICY_MANAGER = -2;

    /**
     * Deletion failed return code: this is passed to the {@link saioapi.OnEventListener} if the 
     * system failed to delete the package since the user is restricted.
     */
    public static final int DELETE_FAILED_USER_RESTRICTED = -3;
    
    
    //
    //    Methods
    //
    
    /**
     * Get the registered observer that handle the PackageManager return code.
     * @return The observer to be invoked with a PackageManager return code is triggered, 
     *         or null id no callback has been set.
     */
    public final OnPMObserver getOnPMObserver()
    {
        return mPackageAPI.getOnPMObserver();
    }
    
    /**
     * Register a observer to be invoked when a PackageManager return code is triggered.
     * @param observer The observer that will be invoked.
     */
    public void setOnPMObserver(OnPMObserver observer)
    {
        mPackageAPI.setOnPMObserver(observer);
    }
    
    /**
     * Release package manager resources
     */
    public void finish()
    {
        mPackageAPI.release();
    }
    
    /**
     * Install a package. Since this may take a little while, the result will
     * be posted back to the observer.
     * @param packageUri The location of the package file to install.  This can be a 'file:' or a
     * 'content:' URI.
     */
    public void install(Uri packageUri){
        mPackageAPI.Install(packageUri);
    }
    
    /**
     * Attempts to delete a package.  Since this may take a little while, the result will
     * be posted back to the observer.
     * @param packagename The name of the package to delete
     */
    public void delete(String packagename){
        mPackageAPI.Delete(packagename);
    }
    
    /**
     * Attempts to delete all installed package.  Since this may take a little while, the result will
     * be posted back to the observer.
     */
    public void deleteAll(){
        mPackageAPI.DeleteAll();
    }
    

}
