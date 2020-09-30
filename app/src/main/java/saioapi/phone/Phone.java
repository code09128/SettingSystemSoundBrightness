//Saio API, Phone class
package saioapi.phone;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.xac.phoneapi.OnRilAPIRespListener;
import com.xac.phoneapi.OnSipAPIEventListener;
import com.xac.phoneapi.PhoneAPI;

import saioapi.OnEventListener;
import saioapi.base.Misc;

/**
 * This class defines the Phone APIs to be used by the applications.
 */
public class Phone {

    private PhoneAPI mPhoneApi;
    private PhoneJni mPhoneJni;
    private OnSipEventListener mSipListener;
    private OnRilResponseListener mRilListener;
    private HandlerThread mHeadsetSwitchThread;
    private Handler mHandler;
    private Misc mMisc;
    private int mHeadsetStatus;
    
    public Phone(Context context){
        mPhoneApi = new PhoneAPI(context);
        mPhoneJni = new PhoneJni();
        mPhoneJni.open();
        mMisc = new Misc();
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
    
    /** VoIP transport type default */
    public static final int VOIP_DEFAULT        = 0x00000000;
    
    /** VoIP transport type DUP */
    public static final int VOIP_UDP            = 0x00000000;
    
    /** VoIP transport type TCP */
    public static final int VOIP_TCP            = 0x00000001;
    
    /** SIP server registering */
    public static final int SIP_REGISTERING     = 0x00000000;
    
    /** SIP server register success */
    public static final int SIP_REGISTER_SUCCESS = 0x00000001;
    
    /** SIP server register fail */
    public static final int SIP_REGISTER_FAIL   = 0x00000002;

    /** SIP call established */
    public static final int SIP_CALL_ESTABLISHED = 0x00000100;
    
    /** SIP call ended */
    public static final int SIP_CALL_ENDED      = 0x00000101;
    
    /** SIP call busy */
    public static final int SIP_CALL_BUSY       = 0x00000102;
    
    /** SIP call held */
    public static final int SIP_CALL_HELD       = 0x00000103;
    
    /** SIP calling */
    public static final int SIP_CALLING         = 0x00000104;
    
    /** SIP changed */
    public static final int SIP_CHANGED         = 0x00000105;
    
    /** SIP error */
    public static final int SIP_ERROR           = 0x00000106;
    
    /** SIP ready to call */
    public static final int SIP_READY_TO_CALL   = 0x00000107;
    
    /** SIP ringing */
    public static final int SIP_RINGING         = 0x00000108;
    
    /** SIP ringing back */
    public static final int SIP_RINGING_BACK    = 0x00000109;
    
    /** Headset switch on */
    public static final int HEADSET_SWITCH_ON   = 0x00000000;
    
    /** Headset switch off */
    public static final int HEADSET_SWITCH_OFF  = 0x00000001;
    
    /** SIM PIN disable */
    public static final int SIM_PIN_DISABLE  = 0x00000000;
    
    /** SIM PIN enable */
    public static final int SIM_PIN_ENABLE  = 0x00000001;
    
    
    /**
     * The listener that receives notifications when an update event is triggered.
     */
    private OnEventListener mOnEventListener = null;
    
    /**
     * This function will establish communication via preset numbers and channel.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public int dial(){
        if(mPhoneApi.getSelectedHandsetChannel() == CHANNEL_LANDLINE){
            String number = mPhoneApi.getPhoneNumber(CHANNEL_LANDLINE);
            return mPhoneJni.dial(number);
        }else{
            return mPhoneApi.dial();
        }
    }
    
    /**
     * This function will hangup the current call.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public int hangup(){
        if(mPhoneApi.getSelectedHandsetChannel() == CHANNEL_LANDLINE){
            return mPhoneJni.hangup();
        }else{
            return mPhoneApi.hangup();
        }
    }
    
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
    public int switchHandsetChannel(int channel){
        mPhoneJni.switchHandsetChannel(channel);
        return mPhoneApi.switchHandsetChannel(conv2APIChan(channel));
    }
    
    
    /**
     * This method set phone number of selected channel.
     * @param channel The channel type of device.
     * @param phonenumber The string of phone number for selected channel, max length is 32.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public int setPhoneNumber(int channel, String phonenumber){
        return mPhoneApi.setPhoneNumber(conv2APIChan(channel), phonenumber);
    }
    
    /**
     * This method return the current selected channel type.
     * @return Upon successful, the channel type of current selected is returned, Otherwise a {@link #ERR_OPERATION} is 
     *         returned and the method {@link #lastError} can be used to indicate the error.
     */
    public int getSelectedHandsetChannel(){
        return conv2PhoneChan(mPhoneApi.getSelectedHandsetChannel());
    }
    
    /**
     * This method return the phone number read from channel.
     * @param channel The channel type to read.
     * @return Upon successful, the phone number string is returned, otherwise a 
     *          null String is returned and the method {@link #lastError} can be used to indicate the error.
     */
    public String getPhoneNumber(int channel){
        return mPhoneApi.getPhoneNumber(conv2APIChan(channel));
    }

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
    public int adjustVolumeLevel(int direction){
        if(mPhoneApi.getSelectedHandsetChannel() == CHANNEL_LANDLINE)
            return mPhoneJni.adjustVolumeLevel(conv2JNIVolCtrl(direction));
        else
            return mPhoneApi.adjustVolumeLevel(conv2APIVolCtrl(direction));
    }
    
    /**
     * This method gets the max volume level.
     * @return Upon successful, the max volume level is returned, otherwise a 
     *          {@link #ERR_OPERATION} is returned and the method {@link #lastError} can be used to indicate the error.
     */
    public int getVolumeMaxLevel(){
        if(mPhoneApi.getSelectedHandsetChannel() == CHANNEL_LANDLINE)
            return mPhoneJni.getVolumeMaxLevel();
        else
            return mPhoneApi.getVolumeMaxLevel();
    }
    
    /**
     * This method return the current volume level.
     * @return Upon successful, the volume level is returned, otherwise a 
     *          {@link #ERR_OPERATION} is returned and the method {@link #lastError} can be used to indicate the error.
     */
    public int getVolumeLevel(){
        if(mPhoneApi.getSelectedHandsetChannel() == CHANNEL_LANDLINE)
            return mPhoneJni.getVolumeLevel();
        else
            return mPhoneApi.getVolumeLevel();
    }
    
    /**
     * This method sets the muting state of the voice call, landline is not support now.
     * @param mute The new muting state. If mute is TRUE, the method mutes the stream. If FALSE, the method turns off muting.
     * @return Return zero if the muting state changes else nonzero error code defined in class constants.
     */
    public int setMute(boolean mute){
        if(mPhoneApi.getSelectedHandsetChannel() == CHANNEL_LANDLINE){
            return ERR_OPERATION;
        }
        else
            return mPhoneApi.setMute(mute);
    }
    
    /**
     * This method gets the muting state of the voice call, landline is not support now.
     * @return Return voice call mute state
     */
    public boolean getMute(){
        if(mPhoneApi.getSelectedHandsetChannel() == CHANNEL_LANDLINE)
            return false;
        else
            return mPhoneApi.getMute();
    }
    
    /**
     * This method sets the VoIP service settings.
     * @param serverDomain server domain of VoIP server
     * @param username account username
     * @param password account password
     * @param authUsername account authentication username (Optional)
     * @param proxy server proxy (Optional)
     * @param portNum port number (Optional)
     * @param transportType transport type (Optional)
     * <table border=1>
     *   <thead><tr><th>Transport Type</th><th>Description</th></tr></thead>
     *   <tbody>
     *     <tr><td>VOIP_DEFAULT</td><td>Default type</td></tr>
     *     <tr><td>VOIP_UDP</td><td>UDP</td></tr>
     *     <tr><td>VOIP_TCP</td><td>TCP</td></tr>
     *   </tbody>
     * </table>
     * @return Return zero if the settings changes else nonzero error code defined in class constants.
     */
    public int setVoIPSettings(String serverDomain, String username, String password, String authUsername,
                               String proxy, String portNum, int transportType, OnSipEventListener listener){
        mSipListener = listener;
        return mPhoneApi.setVoIPSettings(serverDomain, username, password, authUsername, proxy, portNum, 
                transportType, sipListener);
    }
    
    /**
     * This method send DTMF tone to operator.
     * @param tone DTMF tone
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public int sendDTMFTone(char tone){
        if(mPhoneApi.getSelectedHandsetChannel() == CHANNEL_LANDLINE)
            return mPhoneJni.sendDTMFTone(tone);
        else
            return mPhoneApi.sendDTMFTone(tone);
    }
    
    /**
     * Supply a pin to unlock the SIM.
     * @param pin PIN code
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public int supplyPin(String pin){
        return mPhoneApi.supplyPin(pin);
    }
    
    /**
     * Check if the SIM pin lock is enabled.
     * @return Return zero if the SIM pin disable and return 1 if SIM pin is enabled, or nonzero error code defined in class constants.
     */
    public int isSimPinEnabled(){
        return mPhoneApi.isSimPinEnabled();
    }
    
    /**
     * This method send request string to ril.
     * @param request request string
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public int sendRilRequestStrings(String request){
        return mPhoneApi.sendRilRequestStrings(request);
    }
    
    /**
     * This method set response listener to PhoneAPI.
     * @param listener response listener
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public int setOnRilResponseListener(OnRilResponseListener listener){
        mRilListener = listener;
        return mPhoneApi.setOnRilResponseListener(rilRespListener);
    }
    
    /**
     * Retrieves the last error occurs on phone operation.
     * @return Return zero if there is no error else nonzero error code defined in class constants.
     */
    public int lastError(){
        return mPhoneApi.lastError();
    }
    
    /**
     * Release resource and listener at PhoneAPI
     */
    public void release(){
        mPhoneApi.release();
        if(mHandler != null)
            mHandler.removeCallbacks(queryStatus);
        if(mHeadsetSwitchThread != null)
            mHeadsetSwitchThread.quit();
    }
    
    /**
     * Get the registered Listener that handles the headset switch status event is triggered.
     * @return The callback to be invoked when headset switch status change, 
     *         or null id no callback has been set.
     */
    public final OnEventListener getOnEventListener()
    {
        return mOnEventListener;
    }
    
    /**
     * Register a callback to be invoked when a headset switch status event is triggered.
     * @param listener The callback that will be invoked.
     */
    public void setOnEventListener(OnEventListener listener)
    {
        mOnEventListener = listener;
        if(mOnEventListener != null){
            mHeadsetStatus = 0;
            mHeadsetSwitchThread = new HandlerThread("Phone");
            mHeadsetSwitchThread.start();
            mHandler = new Handler(mHeadsetSwitchThread.getLooper());
            mHandler.post(queryStatus);
        }else{
            if(mHandler != null)
                mHandler.removeCallbacks(queryStatus);
            if(mHeadsetSwitchThread != null)
                mHeadsetSwitchThread.quit();
        }
    }
    
    private OnRilAPIRespListener rilRespListener = new OnRilAPIRespListener(){

        @Override
        public void onResponse(String[] response) {
            // TODO Auto-generated method stub
            if(mRilListener != null)
                mRilListener.onResponse(response);
        }
        
    };
    
    private OnSipAPIEventListener sipListener = new OnSipAPIEventListener(){

        @Override
        public void onEvent(int event, String data) {
            // TODO Auto-generated method stub
            if(mSipListener != null)
                mSipListener.onEvent(event, data);
        }
        
    };

    
    private int conv2APIChan(int channel){
        int apiChannel = PhoneAPI.PHONEAPI_CHANNEL_LANDLINE;
        switch(channel){
        case CHANNEL_LANDLINE:
            apiChannel = PhoneAPI.PHONEAPI_CHANNEL_LANDLINE;
            break;
            
        case CHANNEL_3G:
            apiChannel = PhoneAPI.PHONEAPI_CHANNEL_3G;
            break;
            
        case CHANNEL_VOIP:
            apiChannel = PhoneAPI.PHONEAPI_CHANNEL_VOIP;
            break;
        }
        return apiChannel;
    }
    
    private int conv2PhoneChan(int apiChannel){
        int channel = PhoneAPI.PHONEAPI_CHANNEL_LANDLINE;
        switch(apiChannel){
        case PhoneAPI.PHONEAPI_CHANNEL_LANDLINE:
            channel = CHANNEL_LANDLINE;
            break;
            
        case PhoneAPI.PHONEAPI_CHANNEL_3G:
            channel = CHANNEL_3G;
            break;
            
        case PhoneAPI.PHONEAPI_CHANNEL_VOIP:
            channel = CHANNEL_VOIP;
            break;
        }
        return channel;
    }
    
    private int conv2APIVolCtrl(int direct){
        int apiDirect = PhoneAPI.PHONEAPI_VOLUME_DEFAULT;
        switch(direct){
        case VOLUME_SET_DEFAULT:
            apiDirect = PhoneAPI.PHONEAPI_VOLUME_DEFAULT;
            break;
        case VOLUME_ON:
            apiDirect = PhoneAPI.PHONEAPI_VOLUME_ON;
            break;
        case VOLUME_OFF:
            apiDirect = PhoneAPI.PHONEAPI_VOLUME_OFF;
            break;
        case VOLUME_INCREASE:
            apiDirect = PhoneAPI.PHONEAPI_VOLUME_INC;
            break;
        case VOLUME_DECREASE:
            apiDirect = PhoneAPI.PHONEAPI_VOLUME_DEC;
            break;
        }
        return apiDirect;
    }
    
    private int conv2JNIVolCtrl(int direct){
        int apiDirect = PhoneAPI.PHONEAPI_VOLUME_DEFAULT;
        switch(direct){
        case VOLUME_SET_DEFAULT:
            apiDirect = PhoneJni.VOLUME_SET_DEFAULT;
            break;
        case VOLUME_ON:
            apiDirect = PhoneJni.VOLUME_ON;
            break;
        case VOLUME_OFF:
            apiDirect = PhoneJni.VOLUME_OFF;
            break;
        case VOLUME_INCREASE:
            apiDirect = PhoneJni.VOLUME_INCREASE;
            break;
        case VOLUME_DECREASE:
            apiDirect = PhoneJni.VOLUME_DECREASE;
            break;
        }
        return apiDirect;
    }
    
    private Runnable queryStatus = new Runnable(){

        @Override
        public void run() {
            // TODO Auto-generated method stub
            int status = mMisc.cashDrawer((short) 0, Misc.CASH_DRAWER_QUERY);
            if(status != mHeadsetStatus){
                mHeadsetStatus = status;
                if (null != mOnEventListener)
                {
                    mOnEventListener.onEvent(0, mHeadsetStatus);
                }
            }
            mHandler.postDelayed(queryStatus, 1000); //delay 1 second
        }
    };

}
