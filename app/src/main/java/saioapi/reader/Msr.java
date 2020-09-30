//
//  Saio API, MSR class
//
package saioapi.reader;

import saioapi.OnEventListener;


/**
 * This class is deprecated since version 170308_r1.
 * This class defines the Magnetic stripe reader APIs to be used by the applications.
 */
public class Msr
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
    
    /** The method is not supported for the given device. */
    public static final int ERR_NOT_SUPPORT     = 0x0000E00A;
    
    
    /** Signals the user has swiped the card and the read card is 
     * successful and data available and can be read by {@link #getData}. */
    public static final int EVENT_DATA_READY    = 0x00000001;
    
    /** Signals there is at least one card read error detected. */
    public static final int EVENT_READ_ERROR    = 0x00000003;
    
    /** Indicates the card read operation has been cancelled. */
    public static final int EVENT_CANCEL        = 0x00000004;
    
    /** No error. */
    public static final int MSR_NO_ERROR        = 0x00000000;
    
    /** Encoded media was detected, but unable to be read. */
    public static final int MSR_UNABLE_READ     = 0x00000001;

    /** Media had invalid encoding. Data was not recovered for the track. */
    public static final int MSR_INVALID_ENCODE  = 0x00000002;

    /** No media encoding. Data not recovered for the track. */
    public static final int MSR_NO_ENCODE       = 0x00000003;

    /** The track is not supported by the reader. */
    public static final int MSR_TRACK_UNSUPPORT = 0x00000004;
    
    /** The track characters were invalid or corrupt. */
    public static final int MSR_DATA_CORRUPT    = 0x00000005;

    /** Parity error detected in 1 or more characters in the track. */
    public static final int MSR_PARITY_ERROR    = 0x00000006;

    /** LRC error was detected across the characters in the track. */
    public static final int MSR_LRC_ERROR       = 0x00000007;

    /** No track data was present. */
    public static final int MSR_NO_DATA         = 0x00000008;

    /** No encoded track signal was present. */
    public static final int MSR_NO_ENCODE_TRACK = 0x00000009;
    
    /**
     * The listener that receives notifications when an MSR event is triggered.
     */
    private OnEventListener mOnEventListener = null;
    
    //
    //    Methods
    //
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * Get the registered Listener that handle the MSR event.
     * @return The callback to be invoked with a MSR event is triggered, 
     *         or null id no callback has been set.
     */
    public final OnEventListener getOnEventListener()
    {
        return mOnEventListener;
    }
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * Register a callback to be invoked when a MSR event is triggered.
     * @param listener The callback that will be invoked.
     */
    public void setOnEventListener(OnEventListener listener)
    {
        mOnEventListener = listener;
    }
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method creates a handle of the MSR service. It always implements 
     * an exclusive open.
     * @param dev Logical ID of the MSR service to open, it is ignored by now and should be zero.
     * @return Upon successful completion, the service handle is returned to 
     *          identify a MSR service that subsequent method will reference 
     *          this value, Otherwise a {@link #ERR_OPERATION} is returned and the 
     *          method {@link #lastError} can be used to indicate the error.
     */
    public native int open(short dev);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * The method results in the opened MSR service to be closed for use.
     * @param handle The service handle identifying the opened MSR device.
     * @return Return zero if the function succeeds else nonzero error code defined 
     *          in class constants.
     */
    public native int close(int handle);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method initiates the operation to obtain the data read from the MSR 
     * device by swiping the card. The request will complete and generate the resulted 
     * operation event once a new card has been swiped after this request is posted. 
     * In order to ensure the integrity of data, this method first reinitializes any 
     * existing card data buffer that may have been created by previous card swiping, 
     * before waiting for a new card to be read. No effective reading of the card will 
     * be done until a read request is pending. 
     * 
     * <p>Remarks: <br />
     * This method is asynchronous and results in an event that is sent to the 
     * registered class {@link #listener} method. On success, the read data 
     * can be retrieved using the {@link #getData} method.
     * @param handle The service handle identifying the opened MSR device.
     * @param track1 Indicates if the service must attempt to read from track1.
     * @param track2 Indicates if the service must attempt to read from track2.
     * @param track3 Indicates if the service must attempt to read from track3.
     * @param timeout The maximum pending time (in milliseconds) of the operation, 
     *          if the timeout is set to zero the timer will never expire and enable 
     *          is always on, else the timeout will begin when the request begins 
     *          to be processed.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public native int readEnable(int handle, boolean track1, boolean track2, boolean track3, int timeout);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This function resets the MSR reader service and turns the reader off.
     * @param handle The service handle identifying the opened MSR device.
     * @return Return zero if there is no error else nonzero error code defined 
     *          in class constants.
     */
    public native int reset(int handle);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method returns the length (byte count) of the data array read from a track. 
     * @param handle The service handle identifying the opened MSR device.
     * @param track The track to read (1-3).
     * @return If successful, the number of bytes in card data array, otherwise 
     *          a {@link #ERR_OPERATION} is returned and the method {@link #lastError} 
     *          can be used to indicate the error.
     */
    public native int getDataLength(int handle, byte track);

    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method retrieves a buffer containing the data read from the device. 
     * @param handle The service handle identifying the opened MSR device.
     * @param track The track to read (1-3).
     * @param buffer The byte array to receive data from to the card.
     * @return Upon successful, the number of bytes copied into the data array is 
     *          returned, otherwise a {@link #ERR_OPERATION} is returned and the 
     *          method {@link #lastError} can be used to indicate the error.
     */
    public native int getData(int handle, byte track, byte[] buffer);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method indicates the nature of a track read error, if any. The errors 
     * are specific to the individual track and defined in below class constants. 
     * Other tracks may have no errors or different errors.<br />
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>Error Description</th></tr></thead>
     *   <tbody>
     *     <tr><td>MSR_NO_ERROR</td><td>No error</td></tr>
     *     <tr><td>MSR_UNABLE_READ</td><td>Encoded media was detected, but unable to be read</td></tr>
     *     <tr><td>MSR_INVALID_ENCODE</td><td>Media had invalid encoding. Data was not recovered for the track</td></tr>
     *     <tr><td>MSR_NO_ENCODE</td><td>No media encoding. Data not recovered for the track</td></tr>
     *     <tr><td>MSR_TRACK_UNSUPPORT</td><td>The track is not supported by the reader</td></tr>
     *     <tr><td>MSR_DATA_CORRUPT</td><td>The track characters were invalid or corrupt</td></tr>
     *     <tr><td>MSR_PARITY_ERROR</td><td>Parity error detected in 1 or more characters in the track</td></tr>
     *     <tr><td>MSR_LRC_ERROR</td><td>LRC error was detected across the characters in the track</td></tr>
     *     <tr><td>MSR_NO_DATA</td><td>No track data was present</td></tr>
     *     <tr><td>MSR_NO_ENCODE_TRACK</td><td>No encoded track signal was present</td></tr>
     *   </tbody>
     * </table>
     * @param handle The service handle identifying the opened MSR device.
     * @param track [In] The track to get the read errors for (1-3).
     * @return If successful, the MSR track read error is returned, otherwise a 
     *          {@link #ERR_OPERATION} is returned and the method {@link #lastError} 
     *          can be used to indicate the error.
     */
    public native int getTrackError(int handle, byte track);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * Retrieves the last error occurs on the MSR operation.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public native int lastError();
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method retrieves the recent swipe duration (speed) in milliseconds which 
     * the Magnetic Card was wiped over the reader header.
     * @param handle The service handle identifying the opened MSR device.
     * @return If successful, the MSR swipe speed (the duration in milliseconds) 
     *          is returned, otherwise a {@link #ERR_OPERATION} is returned and the 
     *          method {@link #lastError} can be used to indicate the error.
     */
    public native int swipeSpeed(int handle);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * The method get called when the class received a notification event of the 
     * given MSR device and the register method has been enabled.
     * @param handle The service handle identifying the opened MSR device.
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
