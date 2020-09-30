//  Saio API, Phone class
package saioapi.phone;

import saioapi.OnEventListener;

public class PhoneJni {
    
    static
    {
        System.loadLibrary("SaioBase");
        System.loadLibrary("SaioPhone");
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
    
    /** The inputted parameters are not valid. */
    public static final int ERR_INVALID_PARAM   = 0x0000E006;
    
    /** An I/O error occurred while making a service operation. */
    public static final int ERR_IO_ERROR        = 0x0000E00A;
    
    /** The specified connection has not been established. */
    public static final int ERR_NO_CONNECTED    = 0x0000E00C;
    
    /** The channel type of land line. */
    public static final int CHANNEL_LANDLINE    = 0x00000000;
    
    /** The channel type of 3G. */
    public static final int CHANNEL_3G          = 0x00000001;
    
    /** The channel type of VOIP. */
    public static final int CHANNEL_VOIP        = 0x00000002;
    
    /** Set volume to default. */
    public static final int VOLUME_SET_DEFAULT  = 0x00000000;
    
    /** Set volume on. */
    public static final int VOLUME_ON           = 0x00000001;
    
    /** Set volume off. */
    public static final int VOLUME_OFF          = 0x00000002;
    
    /** Increase the volume. */
    public static final int VOLUME_INCREASE     = 0x00000003;
    
    /** Decrease the volume. */
    public static final int VOLUME_DECREASE     = 0x00000004;
    
    

    /**
     * The listener that receives notifications when an PhoneJni event is triggered.
     */
    private OnEventListener mOnEventListener = null;
    
    //
    //    Methods
    //
    
    /**
     * Get the registered Listener that handle the PhoneJni event.
     * @return The callback to be invoked with a PhoneJni event is triggered, 
     *         or null id no callback has been set.
     */
    public final OnEventListener getOnEventListener()
    {
        return mOnEventListener;
    }
    
    /**
     * Register a callback to be invoked when a PhoneJni event is triggered.
     * @param listener The callback that will be invoked.
     */
    public void setOnEventListener(OnEventListener listener)
    {
        mOnEventListener = listener;
    }
    
    /**
     * This method creates a handle of the PhoneJni service.
     * @return Upon successful completion, the service handle is returned to identify a communication that subsequent method will 
     *         reference this value, Otherwise a {@link #ERR_OPERATION} is returned and the method {@link #lastError} can be used to 
     *         indicate the error.
     */
    public native int open();
    
    /**
     * This method tries to close an opened PhoneJni service.
     * @return Return zero if the function succeeds else nonzero error code defined in class constants.
     */
    public native int close();

    /**
     * This method is send phone number to LAN module
     * @param phoneNumber The phone number to dial.
     * @return Upon successful completion, the number of bytes which were written is return, Otherwise a {@link #ERR_OPERATION} is returned and 
     *          the method {@link #lastError} can be used to indicate the error.
     */
    public native int dial(String phoneNumber);
    
    /**
     * This function will hangup the current call.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public native int hangup();
    
    /**
     * This method switch current channel type.
     * @param channel The channel type of device.
     * <table border=1>
     *   <thead><tr><th>Channel Type</th><th>Description</th></tr></thead>
     *   <tbody>
     *     <tr><td>CHANNEL_LANDLINE</td><td>Land line</td></tr>
     *     <tr><td>CHANNEL_3G</td><td>3G</td></tr>
     *     <tr><td>CHANNEL_VOIP</td><td>VOIP</td></tr>
     *   </tbody>
     * </table>
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public native int switchHandsetChannel(int channel);
    
    /**
     * This method adjust the volume level by one step in a direction.
     * @param direction The direction to adjust the volume.
     * <table border=1>
     *   <thead><tr><th>Direction</th><th>Description</th></tr></thead>
     *   <tbody>
     *     <tr><td>DEFAULT</td><td>Set volume to default</td></tr>
     *     <tr><td>ON</td><td>Set volume on</td></tr>
     *     <tr><td>OFF</td><td>Set volume off</td></tr>
     *     <tr><td>INC</td><td>Increase the volume</td></tr>
     *     <tr><td>DEC</td><td>Decrease the volume</td></tr>
     *   </tbody>
     * </table>
     * @return Return zero if the volume changes else nonzero error code defined in class constants.
     */
    public native int adjustVolumeLevel(int direction);
    
    /**
     * This method gets the max volume level.
     * @return Return the max volume level.
     */
    public native int getVolumeMaxLevel();
    
    /**
     * This method return the current volume level.
     * @return Return current volume level.
     */
    public native int getVolumeLevel();
    
    /**
     * This method send DTMF tone to operator.
     * @param tone DTMF tone
     * @return Return zero if the send DTMF tone success else nonzero error code defined in class constants.
     */
    public native int sendDTMFTone(char tone);
    

    /**
     * Retrieves the last error occurs on communication operation.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public native int lastError();

    /**
     * The method get called when the class received a notification event of the given PhoneJni service and the register method has 
     * been enabled.
     * @param handle The service handle identifying the opened communication device.
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
