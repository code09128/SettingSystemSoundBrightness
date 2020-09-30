package saioapi.util;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;

import saioapi.base.Misc;

/**
 * Created by dennis_wang on 2015/10/22.
 */
public class SaioService {
    private static final String TAG = "SaioService";

    static
    {
        //
        //  Load the corresponding library
        //
        System.loadLibrary("SaioUtil");
    }

    /** The inputted parameters are not valid. */
    public static final int ERR_2ND_BRIGHTNESS_INVALID_PARAM      = 0x0000E000;

    /** No such device. */
    public static final int ERR_2ND_BRIGHTNESS_NO_SUCH_DEVICE     = 0x0000E001;

    /** No such device. */
    public static final int ERR_2ND_TOUCH_NO_SUCH_DEVICE     = 0x0000E002;

    /** Fail to switch to target usb device mode. (Curently usb device is disable mode.) */
    public static final int ERR_USBDEV_MODE_DISABLE     = 0x0000E003;

    /** Fail to switch to target usb device mode. (Curently usb device is cdc mode.) */
    public static final int ERR_USBDEV_MODE_CDC         = 0x0000E004;

    /** Fail to switch to target usb device mode. (Curently usb device is unknow mode.) */
    public static final int ERR_USBDEV_MODE_UNKNOWN     = 0x0000E005;

    private static final String SET_2ND_BRIGHTNESS = "BLService.SET_2ND_BRIGHTNESS";
    private static final String GET_2ND_BRIGHTNESS = "BLService.GET_2ND_BRIGHTNESS";
    private static final String RET_2ND_BRIGHTNESS = "BLService.RETURN_2ND_2ND_BRIGHTNESS";
    private static final String BRIGHTNESS_LEVEL = "brightness_level";

    private static final String GET_2ND_TOUCHID = "SaioService.GET_2ND_TOUCHID";
    private static final String RET_2ND_TOUCHID = "SaioService.RETURN_2ND_TOUCHID";
    private static final String SECOND_TOUCHID = "2nd_touch_id";

    private static final String REBOOT_DEVICE = "SaioService.REBOOT_DEVICE";
    private static final String REBOOT_REASON = "reboot_reason";

    private static final String SHUTDOWN_DEVICE = "SaioService.SHUTDOWN_DEVICE";
    private static final String SHUTDOWN_CONFIRM = "shutdown_confirm";

    private static final String ACTION_ACTIVATE_2ND_TOUCH = "SaioService.ACTIVATE_2ND_TOUCH";
    private static final String ACTION_DEACTIVATE_2ND_TOUCH = "SaioService.DEACTIVATE_2ND_TOUCH";

    public static final int ANTENNA_INTERNAL = 1;
    public static final int ANTENNA_EXTERNAL = 0;

    private static final String SET_LED = "SaioService.SET_LED";
    private static final String SET_PINENTRY = "SaioService.SET_PINENTRY";
    private static final String LED_ID = "led_id";
    private static final String LED_VALUE = "led_value";
    private static final String PINENTRY_EN = "pin_enable";

    private static final String START_POLLING_CTLS_LED ="SaioService.START_POLLING_CTLS_LED";
    private static final String STOP_POLLING_CTLS_LED ="SaioService.STOP_POLLING_CTLS_LED";

    public static final String SET_REBOOT_TIME = "SaioService.SET_REBOOT_TIME";
    public static final String REBOOT_HOUR = "SaioService.REBOOT_HOUR";
    public static final String REBOOT_MINUTE = "SaioService.REBOOT_MINUTE";

    private static final String SET_POWER_STATUS = "SaioService.SET_POWER_STATUS";
    private static final String POWER_STATUS = "POWER_STATUS";
    private static final int POWER_SLEEP = 1;
    private static final int POWER_SUSPEND = 2;
    private static final int POWER_WAKEUP = 3;
    private static final int POWER_USERACTIVITY = 4;

    private static final String KEEP_SCREEN_ON = "SaioService.KEEP_SCREEN_ON";
    private static final String SCREEN_ON_STATUS = "SCREEN_ON_STATUS";

    private static final String AUTHORITY = "com.xac.saioservice.sharedprovider.authority";

    private static final String SCREEN_OFF_TIMEOUT = "screen_off_timeout";
    private static final String PM_SLEEP_TIMEOUT = "pm_sleep_timeout";
    private static final String PM_SUSPEND_TIMEOUT = "pm_suspend_timeout";
    private static final String PM_SUSPEND_ENABLE = "pm_suspend_enable";
    private static final String PM_POWEROFF_TIMEOUT = "pm_poweroff_timeout";
    private static final String PM_POWEROFF_ENABLE = "pm_poweroff_enable";
    private static final int FALLBACK_SCREEN_TIMEOUT_VALUE = 30000;

    /** The sleep timeout 15 seconds. */
    public static final int PM_SLEEP_TIME_15_SEC = 15000;

    /** The sleep timeout 30 seconds. */
    public static final int PM_SLEEP_TIME_30_SEC = 30000;

    /** The sleep timeout 1 minute. */
    public static final int PM_SLEEP_TIME_1_MIN  = 60000;

    /** The sleep timeout 2 minutes. */
    public static final int PM_SLEEP_TIME_2_MIN  = 120000;

    /** The sleep timeout 5 minutes. */
    public static final int PM_SLEEP_TIME_5_MIN  = 300000;

    /** The sleep timeout 10 minutes. */
    public static final int PM_SLEEP_TIME_10_MIN = 600000;

    /** The sleep timeout 30 minutes. */
    public static final int PM_SLEEP_TIME_30_MIN = 1800000;

    /** The sleep timeout never, depend on platform setting */
    public static final int PM_SLEEP_TIME_NEVER  = 2147483647;

    /** The suspend timeout 30 seconds. */
    public static final int PM_SUSPEND_TIME_30_SEC = 30000;

    /** The suspend timeout 1 minute. */
    public static final int PM_SUSPEND_TIME_1_MIN  = 60000;

    /** The suspend timeout 2 minutes. */
    public static final int PM_SUSPEND_TIME_2_MIN  = 120000;

    /** The suspend timeout 5 minutes. */
    public static final int PM_SUSPEND_TIME_5_MIN  = 300000;

    /** The suspend timeout 10 minutes. */
    public static final int PM_SUSPEND_TIME_10_MIN = 600000;

    /** The suspend timeout 30 minutes. */
    public static final int PM_SUSPEND_TIME_30_MIN = 1800000;

    /** The suspend timeout 60 minutes. */
    public static final int PM_SUSPEND_TIME_60_MIN = 3600000;

    /** The poweroff minimum timeout 2 hours. */
    public static final int PM_POWEROFF_TIME_MIN_HOUR = 2;

    /** The poweroff maximum timeout 108 hours. */
    public static final int PM_POWEROFF_TIME_MAX_HOUR = 108;

    public static final int DEV_KEYBOARD = 1;
    public static final int DEV_MOUSE = 2;
    public static final int DEV_TOUCH = 3;

    public static final String ACTION_TRIPLE_TAP_POWER_GESTURE = "SaioService.SYSTEM_TRIPLE_TAP_POWER_GESTURE";

    public static final String ACTION_DEVICE_ADMIN_SET = "SaioService.SET_DEVICE_ADMIN";
    public static final String ACTION_DEVICE_ADMIN_REMOVE = "SaioService.REMOVE_DEVICE_ADMIN";
    public static final String DEVICE_ADMIN_PACKAGE_NAME = "SaioService.DEVICE_ADMIN_PKG";

    private static final String ACTION_ACTIVATE_POWERKEY = "SaioService.ACTIVATE_POWERKEY";
    private static final String ACTION_DEACTIVATE_POWERKEY = "SaioService.DEACTIVATE_POWERKEY";

    private static final String ACTION_DEVICE_KEYBOARD = "SaioService.DEVICE_KEYBOARD";
    private static final String ACTION_DEVICE_MOUSE = "SaioService.DEVICE_MOUSE";
    private static final String ACTION_DEVICE_TOUCH = "SaioService.DEVICE_TOUCH";
    private static final String ACTION_DEVICE_ACTIVATE = "SaioService.DEVICE_ACTIVATE";
    private static final String ACTION_DEACTIVATE_TIMEOUT = "SaioService.DEACTIVATE_TIMEOUT";

    private static final String ACTION_LATINIME_ENABLE_SOUND_ON_KEYPRESS = "SaioService.ENABLE_SOUND_ON_KEYPRESS";
    private static final String ACTION_LATINIME_DISABLE_SOUND_ON_KEYPRESS = "SaioService.DISABLE_SOUND_ON_KEYPRESS";

    private static final String ACTION_CRADLE_API_REBOOT = "com.xac.cradle.api.reboot";
    private static final String ACTION_CRADLE_API_FACTORY_RESET = "com.xac.cradle.api.reset";
    private static final String ACTION_CRADLE_API_GET_DEVICE_INFO = "com.xac.cradle.api.getInfo";
    private static final String ACTION_CRADLE_API_GET_DEVICE_LOG = "com.xac.cradle.api.getLog";
    private static final String ACTION_CRADLE_API_UPDATE_CRADLE_OS = "com.xac.cradle.api.osUpdate";
    private static final String ACTION_CRADLE_API_UPDATE_CRADLE_PRINTER_FW = "com.xac.cradle.api.fwUpdate";
    private static final String ACTION_CRADLE_API_BATCH_CONFIG = "com.xac.cradle.api.config";
    private static final String ACTION_CRADLE_API_OPEN_CASHDRAWER_CONFIG = "com.xac.cradle.api.openCashDrawer";
    private static final String ACTION_CRADLE_API_QUERY_CASHDRAWER_CONFIG = "com.xac.cradle.api.queryCashDrawer";
    private static final String ACTION_CRADLE_API_CLOSE_CASHDRAWER_CONFIG = "com.xac.cradle.api.closeCashDrawer";

    private static final String CRADLE_REBOOT_DELAY_VALUE = "Delay_time";
    private static final String CRADLE_STORE_LOG_PATH_TAG = "Save_Log_uri";
    private static final String CRADLE_OS_IMAGE_URI_TAG = "OS_Image_uri";
    private static final String CRADLE_FW_IMAGE_URI_TAG = "FW_Image_uri";
    private static final String CRADLE_CONFIG_URI_TAG = "Config_file_uri";

    private static final String SET_USB_MODE = "SaioService.SET_USBDEV_MODE";
    private static final String USB_MODE = "usb_mode";

    private static final String SET_NOTIFICATION_LOCK = "SaioService.SET_NOTIFICATION_LOCKED";
    private static final String LOCK_STATE = "lock_state";

    private Context mContext;
    private boolean mHasFlagKeepScreenOn = false;
    private boolean mHasDisablePowerkey = false;
    private OnSaioListener mOnSaioListener;
    private BlLevelReceiver mBlLevelReceiver;

    /** The LED id indicates the status bar led with blue light. */
    public static final int LED_STATUS_BAR_BLUE       = 0x00;

    /** The LED id indicates the status bar led with red light. */
    public static final int LED_STATUS_BAR_RED        = 0x01;

    /** The LED id indicates the status bar led with green light. */
    public static final int LED_STATUS_BAR_GREEN      = 0x02;

    /** The LED id indicates the status bar led with yellow light. */
    public static final int LED_STATUS_BAR_YELLOW     = 0x03;

    /** The LED id indicates the 1st led from the left in MSR slot. */
    public static final int LED_MSR_SLOT_LEFT1        = 0x04;

    /** The LED id indicates the 2nd led from the left in MSR slot. */
    public static final int LED_MSR_SLOT_LEFT2        = 0x05;

    /** The LED id indicates the 3rd led from the left in MSR slot. */
    public static final int LED_MSR_SLOT_LEFT3        = 0x06;

    /** The LED id indicates the 4th led from the left in MSR slot. */
    public static final int LED_MSR_SLOT_LEFT4        = 0x07;

    /** The LED id indicates the 5th led from the left in MSR slot. */
    public static final int LED_MSR_SLOT_LEFT5        = 0x08;

    /** The LED id indicates the 6th led from the left in MSR slot. */
    public static final int LED_MSR_SLOT_LEFT6        = 0x09;

    /** The LED id indicates the logo led. */
    public static final int LED_LOGO                  = 0x0A;

    /** The LED id indicates the led in SCR slot. */
    public static final int LED_SCR_SLOT              = 0x0B;

    /** The LED id indicates the keypad led. */
    public static final int LED_KEY_PAD               = 0x0C;

    /** The LED id indicates the MSR led of product E200CP. */
    public static final int LED_MSR_SLOT_E200CP       = 0x0D;
    
    public static final int USB_HIGH_SPEED = 0;
    public static final int USB_FULL_SPEED = 1;
    public static final int USB_NOT_SUPPORT = -1;

    /**
     * SaioService constructor.
     *
     * @param context App context.
     * @param onSaioListener listener to get data from SaioService
     */
    public SaioService(Context context, OnSaioListener onSaioListener){
        mContext = context;
        mOnSaioListener = onSaioListener;

        mBlLevelReceiver = new BlLevelReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(RET_2ND_BRIGHTNESS);
        filter.addAction(RET_2ND_TOUCHID);
        mContext.registerReceiver(mBlLevelReceiver, filter);
    }

    public SaioService(Context context){
        mContext = context;
    }

    /**
     * The method will unregister OnSaioListener.
     *
     */
    public void release(){
        if(mBlLevelReceiver != null) {
            mContext.unregisterReceiver(mBlLevelReceiver);
            mBlLevelReceiver = null;
            mContext = null;
        }
    }

    /**
     * The method can be used to set the brightness of the 2nd display.
     * <p>
     *     Note: only for E200I, T3
     * </p>
     *
     * @param brightness The brightness of the 2nd display (0~1.0).
     * @return zero if there is no error else nonzero error code defined in class constants.
     */
    public int set2ndDispBrightness(float brightness){
        DisplayManager displayManager = (DisplayManager)mContext.getSystemService(Context.DISPLAY_SERVICE);
        Display[] display = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
        if(display.length == 0){
            return ERR_2ND_BRIGHTNESS_NO_SUCH_DEVICE;
        }
        if((brightness < 0)||(brightness > 1.0))
            return ERR_2ND_BRIGHTNESS_INVALID_PARAM;
        Intent blIntent = new Intent();
        blIntent.setAction(SET_2ND_BRIGHTNESS);
        blIntent.putExtra(BRIGHTNESS_LEVEL, brightness);
        mContext.sendBroadcast(blIntent);
        return 0;
    }

    /**
     * The method send request to get brightness of the 2nd display, will return by OnSaioListener.
     * <p>
     *     Note: only for E200I, T3
     * </p>
     *
     * @return zero if there is no error else nonzero error code defined in class constants.
     */
    public int get2ndDispBrightness(){
        DisplayManager displayManager = (DisplayManager)mContext.getSystemService(Context.DISPLAY_SERVICE);
        Display[] display = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
        if(display.length == 0){
            return ERR_2ND_BRIGHTNESS_NO_SUCH_DEVICE;
        }
        Intent blIntent = new Intent();
        blIntent.setAction(GET_2ND_BRIGHTNESS);
        mContext.sendBroadcast(blIntent);
        return 0;
    }

    /**
     * The method will return secondary touch id.
     * <p>
     *     Note: only for E200I, T3
     * </p>
     *
     * @return device id if there is no error else nonzero error code defined in class constants.
     */
    public int get2ndTouchDeviceId(){
        DisplayManager displayManager = (DisplayManager)mContext.getSystemService(Context.DISPLAY_SERVICE);
        Display[] display = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
        if(display.length == 0){
            return ERR_2ND_TOUCH_NO_SUCH_DEVICE;
        }
        Intent tpIntent = new Intent();
        tpIntent.setAction(GET_2ND_TOUCHID);
        mContext.sendBroadcast(tpIntent);
        return 0;
    }

    /**
     * Activate the secondary touch.
     * <p>
     *     Note: only for E200I, T3
     * </p>
     */
    public void activate2ndTouch()
    {
        Intent intent = new Intent();
        intent.setAction(ACTION_ACTIVATE_2ND_TOUCH);
        mContext.sendBroadcast(intent);
    }

    /**
     * Deactivate the secondary touch.
     * <p>
     *     Note: only for E200I, T3
     * </p>
     */
    public void deactivate2ndTouch()
    {
        Intent intent = new Intent();
        intent.setAction(ACTION_DEACTIVATE_2ND_TOUCH);
        mContext.sendBroadcast(intent);
    }

    /**
     * Activate or deactivate the secondary touch if it's available.
     * <p>
     *     Note: only for E200I, T3
     * </p>
     *
     * @param on set true to activate; false to deactivate
     */
    private static void set2ndTouchActive(boolean on)
    {
        native_set2ndTouchActive((on)?1:0);
    }

    /**
     * Call this method to reboot device.
     *
     * @param reason code to pass to the kernel (e.g., "recovery") to request special boot modes, or null.
     * @return device id if there is no error else nonzero error code defined in class constants.
     */
    public int reboot(String reason){
        Intent rebootIntent = new Intent();
        rebootIntent.setAction(REBOOT_DEVICE);
        rebootIntent.putExtra(REBOOT_REASON, reason);
        mContext.sendBroadcast(rebootIntent);
        return 0;
    }

    /**
     * Shuts down the device.
     *
     * @param confirm If true, shows a shutdown confirmation dialog.
     */
    public void shutdown(boolean confirm){
        Intent shutdownIntent = new Intent();
        shutdownIntent.setAction(SHUTDOWN_DEVICE);
        shutdownIntent.putExtra(SHUTDOWN_CONFIRM, confirm);
        mContext.sendBroadcast(shutdownIntent);
    }

    /**
     * Switch antenna between internal(default) or external.
     * <p>
     *     Note: only for T3
     * </p>
     *
     * @param dist {@link #ANTENNA_INTERNAL} or {@link #ANTENNA_EXTERNAL} only
     */
    public static void switchAntenna(int dist){
        if((dist != ANTENNA_INTERNAL)&&(dist != ANTENNA_EXTERNAL))
            return;
        native_switchAntenna(dist);
    }

    /**
     * Call this method to activate/deactivate Epp.
     * <p>
     *     Note: only for 200NP
     * </p>
     * @param enabled activate or deactive Epp.
     */
    public static int setEppEnabled(boolean enabled){
        return native_setEppEnabled(enabled);
    }

    /**
     * Call this method to activate/deactivate PA.
     * <p>
     *     Note: only for AW
     * </p>
     * @param enabled activate or deactive PA.
     */
    public static int setPAEnabled(boolean enabled){
        return native_setPAEnabled(enabled);
    }
    
    /**
     * Call this method to set USB full/high speed.
     * <p>
     *     Note: only for imx6 4.4.2
     * </p>
     * @param mode USB full or high speed.
     */
    public static void setUsbSpeed(int mode){
        //int ret = 
		native_setUsbSpeed(mode);
        //if (ret != 0)
        //    return USB_NOT_SUPPORT;
        //return ret;
    }
    
    /**
     * Call this method to get USB full/high speed.
     * <p>
     *     Note: only for imx6 4.4.2
     */
    public static int getUsbSpeed(){
        int mode = native_getUsbSpeed();
        if ((mode != USB_FULL_SPEED)&&(mode != USB_HIGH_SPEED))
            return USB_NOT_SUPPORT;
        return mode;
    }


    /**
     * Power on Maxim
     * <p>
     *     Note: only for Allwinner
     * </p>
     */
    public static void powerOnMaxim(int dist){
        native_powerOnMaxim(dist);
    }

    /**
     * Reset Maxim
     * <p>
     *     Note: only for Allwinner
     * </p>
     */
    public static void resetMaxim(int dist){
        native_resetMaxim(dist);
    }

    /**
     * Power On GPS
     * <p>
     *     Note: only for Allwinner
     * </p>
     */
    public static void powerOnGPS(int dist){
        native_powerOnGPS(dist);
    }

    /**
     * Reset GPS
     * <p>
     *     Note: only for Allwinner
     * </p>
     */
    public static void resetGPS(int dist){
        native_resetGPS(dist);
    }

    /**
     * Heater Enable
     * <p>
     *     Note: only for Allwinner
     * </p>
     */
    public static void setHeaterEnable(int dist){
        native_setHeaterEnable(dist);
    }

    /**
     * Power On P95
     * <p>
     *     Note: only for Allwinner
     * </p>
     */
    public static void powerOnP95(int dist){
        native_powerOnP95(dist);
    }

    /**
     * Power on 3G module manually.
     * <p>
     *     Note: only for T3
     * </p>
     */
    public static void powerOn3Gmodule(){
        native_powerOn3Gmodule();
    }

    /**
     * Reset 3G module manually
     */
    public static void reset3Gmodule(){
        native_reset3Gmodule();
    }

    /**
     * The method allow user to control the on-board LED on or off.
     * <p>
     *     Note: only for A3
     * </p>
     *
     * @param led_id The LED id.
     * @param enable Indicates to turn on (true) or off (false) the LED
     */
    public void setLed(int led_id, boolean enabled){
        if(enabled)
            setLedValue(led_id,255);
        else
            setLedValue(led_id,0);
    }

    /**
     * The method allow user to adjust the on-board led brightness.
     * <p>
     *     Note: only for A3
     * </p>
     *
     * @param led_id The led id.
     * @param value Indicates LED brightness value and must be an integer between 0 and 255.
     *          [Note]: LED_SCR_SLOT/LED_KEY_PAD/LED_MSR_SLOT_E200CP are NOT able to adjust the brightness. The brightness value of these led_id should be 0 or 1.
     *          If the value greater than or equal to 1,that means to set the led_id ON.
     */
    public void setLedValue(int led_id, int value){
        Intent ledIntent = new Intent();
        ledIntent.setAction(SET_LED);
        ledIntent.putExtra(LED_ID, led_id);
        ledIntent.putExtra(LED_VALUE, value);
        mContext.sendBroadcast(ledIntent);
    }

    /**
     * The method enables or disables the PIN entry mode.
     * <p>
     *     Note: only for A3
     * </p>
     *
     * @param enable Indicates to enable (true) or disable (false) the PIN entry mode
     */
    public void setPinEntryModeEnabled(boolean enabled){
        if(enabled){
            // Check to see if FLAG_KEEP_SCREEN_ON is already set.  If not, set it
            int flags = ((Activity)mContext).getWindow().getAttributes().flags;
            if ((flags & WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) == 0) {
                ((Activity)mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            else {
                mHasFlagKeepScreenOn = true;
            }
            //mHasDisablePowerkey = getPowerkeyEnabled();
            //setPowerkeyEnabled(!enabled);
        }else{
            if (!mHasFlagKeepScreenOn) {
                ((Activity)mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            //setPowerkeyEnabled(mHasDisablePowerkey);
        }
        Intent pinIntent = new Intent();
        pinIntent.setAction(SET_PINENTRY);
        pinIntent.putExtra(PINENTRY_EN, enabled);
        mContext.sendBroadcast(pinIntent);
        keepScreenOn(enabled);
    }

    /**
     * The method start polling ctls leds status.Use this method,then recevie intent "SaioService.CHANGE_CTLSLed" and extra Int objects with names "ctls_led" to get Ctls Leds status value.The value is Ctls 4 led lights correspond to a 4-bit number.
     * <p>
     *     Note: only for A3
     * </p>
     */
    public void startPollingCtlsLeds(){
        Intent StartIntent = new Intent();
        StartIntent.setAction(START_POLLING_CTLS_LED);
        mContext.sendBroadcast(StartIntent);
    }

    /**
     * The method stop polling ctls leds status.
     * <p>
     *     Note: only for A3
     * </p>
     */
    public void stopPollingCtlsLeds(){
        Intent StopIntent = new Intent();
        StopIntent.setAction(STOP_POLLING_CTLS_LED);
        mContext.sendBroadcast(StopIntent);
    }

    /**
     * For SaioService to get SystemInfo only.
     */
    public static String getSystemInfo(){
        Misc misc = new Misc();
        byte[] info = new byte[20];
        misc.getSystemInfo(Misc.INFO_PRODUCT, info);
        int len = info.length;
        for(int i=0; i<info.length; i++){
            if(info[i] == 0){
                len = i;
                break;
            }
        }
        String prodInfo = new String(info);
        prodInfo = prodInfo.substring(0, len);
        return prodInfo;
    }

    /**
     * For SaioService to check whether Suspend is available
     */
    private static boolean isSuspendAvailable() {
        String prodInfo = getSystemInfo();
        if (prodInfo.contains("AP-10") || prodInfo.contains("T305") || prodInfo.contains("SUD12") || prodInfo.contains("SUD7")) {
            Log.d(TAG, "AP-10/T305/SUD12/SUD7 don't support Suspend function");
            return false;
        } else {
            return true;
        }
    }

    /**
     * For SaioService to check whether PowerOff is available
     */
    private static boolean isPowerOffAvailable() {
        String prodInfo = getSystemInfo();
        if (prodInfo.contains("AP-10") || prodInfo.contains("T305") || prodInfo.contains("SUD12") || prodInfo.contains("SUD7")) {
            Log.d(TAG, "AP-10/T305/SUD12/SUD7 don't support PowerOff function");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Call this method to activate/deactivate power key.
     *
     * @param enabled activate or deactive power key.
     */
    public void setPowerkeyEnabled(boolean enabled){
        Intent intent = new Intent();
        if(enabled)
            intent.setAction(ACTION_ACTIVATE_POWERKEY);
        else
            intent.setAction(ACTION_DEACTIVATE_POWERKEY);
        mContext.sendBroadcast(intent);
    }

    /**
     * Call this method to activate/deactivate keyboard, mouse, touch.
     *
     * @param dev set keyboard, mouse or touch device.
     * @param enabled activate or deactive keyboard, mouse or touch.
     * @param timeout milliseconds. It is valid when enabled is false.
     */
    public void setInputEventEnabled(int dev, boolean enabled, int timeout){
        Intent intent = new Intent();
        switch (dev) {
            case DEV_KEYBOARD:
                intent.setAction(ACTION_DEVICE_KEYBOARD)
                    .putExtra(ACTION_DEVICE_ACTIVATE, enabled)
                    .putExtra(ACTION_DEACTIVATE_TIMEOUT, timeout);
                break;
            case DEV_MOUSE:
                intent.setAction(ACTION_DEVICE_MOUSE)
                    .putExtra(ACTION_DEVICE_ACTIVATE, enabled)
                    .putExtra(ACTION_DEACTIVATE_TIMEOUT, timeout);
                break;
            case DEV_TOUCH:
                intent.setAction(ACTION_DEVICE_TOUCH)
                    .putExtra(ACTION_DEVICE_ACTIVATE, enabled)
                    .putExtra(ACTION_DEACTIVATE_TIMEOUT, timeout);
                break;
        }   
        mContext.sendBroadcast(intent);
    }

    /**
     * The method will return current power key enable
     *
     * @return device power key enable.
     */
    public boolean getPowerkeyEnabled(){
        Method get = null;
        String value = "false";
        try {
            if (null == get) {
                if (null == get) {
                    Class<?> cls = Class.forName("android.os.SystemProperties");
                    get = cls.getDeclaredMethod("get", new Class<?>[]{String.class, String.class});
                }
            }
            value = (String) (get.invoke(null, new Object[]{"persist.sys.powerkey.enabled", "true"}));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return value.equals("true");
    }

    /**
     * Call this method to set enable/disable LatinIME sound on keypress.
     *
     * @param enabled enable or disable LatinIME sound on keypress settings.
     */
    public void setLatinIMESoundOnKeypressEnabled(boolean enabled){
        Intent intent = new Intent();
        if(enabled)
            intent.setAction(ACTION_LATINIME_ENABLE_SOUND_ON_KEYPRESS);
        else
            intent.setAction(ACTION_LATINIME_DISABLE_SOUND_ON_KEYPRESS);
        mContext.sendBroadcast(intent);
    }

    /**
     * The method will return auto reboot status
     *
     * @return device auto reboot enabled or disabled.
     */
    public boolean isRebootEnabled(){
        Method get = null;
        boolean enabled = true;
        String value = "true";
        try {
            if (null == get) {
                Class<?> cls = Class.forName("android.os.SystemProperties");
                get = cls.getDeclaredMethod("get", new Class<?>[]{String.class, String.class});
            }
            value = (String) (get.invoke(null, new Object[]{"persist.sys.reboot.enable", "true"}));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return value.equals("true");
    }

    /**
     * The method will return current reboot time
     *
     * @return device reboot time.
     */
    public String getRebootTime(){
        Method get = null;
        String value = "2:0";
        try {
            if (null == get) {
                if (null == get) {
                    Class<?> cls = Class.forName("android.os.SystemProperties");
                    get = cls.getDeclaredMethod("get", new Class<?>[]{String.class, String.class});
                }
            }
            value = (String) (get.invoke(null, new Object[]{"persist.sys.reboot.time", "2:0"}));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * The method will set reboot time
     *
     * @return parameter valid or not.
     */
    public boolean setRebootTime(int hour, int minute){
        if((((hour >= 0)&&(hour <= 23))||(hour == 99))&&(((minute >= 0)&&(minute <= 59))||(minute == 99))) {
            Intent intent = new Intent();
            intent.setAction(SET_REBOOT_TIME);
            intent.putExtra(REBOOT_HOUR, hour);
            intent.putExtra(REBOOT_MINUTE, minute);
            mContext.sendBroadcast(intent);
            return true;
        }else
            return false;
    }

    /**
     * The method will get sleep time
     *
     * @return sleep time milliseconds.
     */
    public int getSleepTime(){
        int time = Settings.System.getInt(mContext.getContentResolver(), SCREEN_OFF_TIMEOUT, FALLBACK_SCREEN_TIMEOUT_VALUE);
        return time;
    }

    /**
     * The method will set sleep time
     *
     * @data the value is PM_SLEEP_TIME_15_SEC, PM_SLEEP_TIME_30_SEC,
     *       PM_SLEEP_TIME_1_MIN, PM_SLEEP_TIME_2_MIN, PM_SLEEP_TIME_5_MIN,
     *       PM_SLEEP_TIME_10_MIN, PM_SLEEP_TIME_30_MIN, PM_SLEEP_TIME_NEVER
     * @return true if success, else false indicates to passed parameter is invalid
     */
    public boolean setSleepTime(int time){
        String prodInfo = getSystemInfo();
        if ( (time==PM_SLEEP_TIME_15_SEC) ||
                (time==PM_SLEEP_TIME_30_SEC) ||
                (time==PM_SLEEP_TIME_1_MIN)  ||
                (time==PM_SLEEP_TIME_2_MIN)  ||
                (time==PM_SLEEP_TIME_5_MIN)  ||
                (time==PM_SLEEP_TIME_10_MIN) ||
                (time==PM_SLEEP_TIME_30_MIN) ||
                ((time==PM_SLEEP_TIME_NEVER) && (prodInfo.contains("T305") || prodInfo.contains("SUD12") || prodInfo.contains("SUD7"))) ) {
            Uri CONTENT_PREFERENCE_URI = Uri.parse("content://" + AUTHORITY + "/shared");
            ContentValues contentValues = new ContentValues();
            contentValues.put(PM_SLEEP_TIMEOUT, time);
            mContext.getContentResolver().insert(CONTENT_PREFERENCE_URI, contentValues);
            return true;
        } else {
            return false;
        }
    }

    /**
     * The method will return current suspend mode
     *
     * @return true if suspend mode is enabled, else suspend mode is disabled
     */
    public boolean isSuspendEnabled(){
        Method get = null;
        String value = "false";
        try {
            if (null == get) {
                if (null == get) {
                    Class<?> cls = Class.forName("android.os.SystemProperties");
                    get = cls.getDeclaredMethod("get", new Class<?>[]{String.class, String.class});
                }
            }
            value = (String) (get.invoke(null, new Object[]{"persist.sys.suspend.enable", "false"}));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return value.equals("true");
    }

    /**
     * The method will return current suspend time
     *
     * @return suspend time milliseconds.
     */
    public int getSuspendTime(){
        Method get = null;
        String value = "0";
        try {
            if (null == get) {
                if (null == get) {
                    Class<?> cls = Class.forName("android.os.SystemProperties");
                    get = cls.getDeclaredMethod("get", new Class<?>[]{String.class, String.class});
                }
            }
            value = (String) (get.invoke(null, new Object[]{"persist.sys.suspend.timeout", "0"}));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return Integer.parseInt(value);
    }

    /**
     * The method will enable/disable suspend mode
     *
     * @return true if success, else false indicates to fail
     */
    public boolean setSuspendEnabled(boolean enable){
        if (isSuspendAvailable()) {
            Uri CONTENT_PREFERENCE_URI = Uri.parse("content://" + AUTHORITY + "/shared");
            ContentValues contentValues = new ContentValues();
            contentValues.put(PM_SUSPEND_ENABLE, enable);
            mContext.getContentResolver().insert(CONTENT_PREFERENCE_URI, contentValues);
            return true;
        } else {
            return false;
        }
    }

    /**
     * The method will set suspend time
     *
     * @data the value is PM_SUSPEND_TIME_30_SEC, PM_SUSPEND_TIME_1_MIN,
     *       PM_SUSPEND_TIME_2_MIN, PM_SUSPEND_TIME_5_MIN, PM_SUSPEND_TIME_10_MIN,
     *       PM_SUSPEND_TIME_30_MIN, PM_SUSPEND_TIME_60_MIN
     * @return true if success, else false indicates to passed parameter is invalid
     */
    public boolean setSuspendTime(int time){
        if (isSuspendAvailable()) {
            if ( (time==PM_SUSPEND_TIME_30_SEC) ||
                    (time==PM_SUSPEND_TIME_1_MIN)  ||
                    (time==PM_SUSPEND_TIME_2_MIN)  ||
                    (time==PM_SUSPEND_TIME_5_MIN)  ||
                    (time==PM_SUSPEND_TIME_10_MIN) ||
                    (time==PM_SUSPEND_TIME_30_MIN) ||
                    (time==PM_SUSPEND_TIME_60_MIN) ) {
                Uri CONTENT_PREFERENCE_URI = Uri.parse("content://" + AUTHORITY + "/shared");
                ContentValues contentValues = new ContentValues();
                contentValues.put(PM_SUSPEND_TIMEOUT, time);
                mContext.getContentResolver().insert(CONTENT_PREFERENCE_URI, contentValues);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * The method will return current poweroff mode
     *
     * @return true if poweroff mode is enabled, else poweroff mode is disabled
     */
    public boolean isPowerOffEnabled(){
        Method get = null;
        String value = "false";
        try {
            if (null == get) {
                if (null == get) {
                    Class<?> cls = Class.forName("android.os.SystemProperties");
                    get = cls.getDeclaredMethod("get", new Class<?>[]{String.class, String.class});
                }
            }
            value = (String) (get.invoke(null, new Object[]{"persist.sys.poweroff.enable", "false"}));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return value.equals("true");
    }

    /**
     * The method will return current poweroff time
     *
     * @return poweroff time hours.
     */
    public int getPowerOffTime(){
        Method get = null;
        String value = "0";
        try {
            if (null == get) {
                if (null == get) {
                    Class<?> cls = Class.forName("android.os.SystemProperties");
                    get = cls.getDeclaredMethod("get", new Class<?>[]{String.class, String.class});
                }
            }
            value = (String) (get.invoke(null, new Object[]{"persist.sys.poweroff.timeout", "0"}));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return Integer.parseInt(value);
    }

    /**
     * The method will enable/disable poweroff mode
     *
     * @return true if success, else false indicates to fail
     */
    public boolean setPowerOffEnabled(boolean enable){
        if (isPowerOffAvailable()) {
            Uri CONTENT_PREFERENCE_URI = Uri.parse("content://" + AUTHORITY + "/shared");
            ContentValues contentValues = new ContentValues();
            contentValues.put(PM_POWEROFF_ENABLE, enable);
            mContext.getContentResolver().insert(CONTENT_PREFERENCE_URI, contentValues);
            return true;
        } else {
            return false;
        }
    }

    /**
     * The method will set poweroff time
     *
     * @data the value is beteween PM_POWEROFF_TIME_MIN_HOUR to PM_POWEROFF_TIME_MAX_HOUR
     * @return true if success, else false indicates to passed parameter is invalid
     */
    public boolean setPowerOffTime(int time){
        if (isPowerOffAvailable()) {
            if  ((time>=PM_POWEROFF_TIME_MIN_HOUR) && (time <=PM_POWEROFF_TIME_MAX_HOUR)) {
                Uri CONTENT_PREFERENCE_URI = Uri.parse("content://" + AUTHORITY + "/shared");
                ContentValues contentValues = new ContentValues();
                contentValues.put(PM_POWEROFF_TIMEOUT, time);
                mContext.getContentResolver().insert(CONTENT_PREFERENCE_URI, contentValues);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * The method let system go to sleep.
     */
    public void goToSleep(){
        Intent intent = new Intent();
        intent.setAction(SET_POWER_STATUS);
        intent.putExtra(POWER_STATUS, POWER_SLEEP);
        mContext.sendBroadcast(intent);
    }

    /**
     * The method let system go to suspend.
     */
    public void goToSuspend(){
        if (isSuspendAvailable()) {
            Intent intent = new Intent();
            intent.setAction(SET_POWER_STATUS);
            intent.putExtra(POWER_STATUS, POWER_SUSPEND);
            mContext.sendBroadcast(intent);
        }
    }

    /**
     * The method let system wakeup.
     */
    public void wakeUp(){
        Intent intent = new Intent();
        intent.setAction(SET_POWER_STATUS);
        intent.putExtra(POWER_STATUS, POWER_WAKEUP);
        mContext.sendBroadcast(intent);
    }

    /**
     * Manually report user activity to keep the device awake
     */
    public void userActivity(){
        Intent intent = new Intent();
        intent.setAction(SET_POWER_STATUS);
        intent.putExtra(POWER_STATUS, POWER_USERACTIVITY);
        mContext.sendBroadcast(intent);
    }

    /**
     * The method enables or disables keep screen on.
     * @param enable Indicates to enable (true) or disable (false) keep screen on
     */
    public void keepScreenOn(boolean enable){
        Intent intent = new Intent();
        intent.setAction(KEEP_SCREEN_ON);
        intent.putExtra(SCREEN_ON_STATUS, enable);
        mContext.sendBroadcast(intent);
    }

    /**
     * The method enables or disables usb device mtp. It only works when device is at Android mode.
     * (Computer might take a moment to identify the device.)
     *
     * @param enable Indicates to enable (true) or disable (false) usb device mtp mode.
     *
     * @return zero for sending request to set usb device mtp mode successfully else error code defined in class constants.
     */
    public int setMtpEnabled(boolean enable){
        Misc misc = new Misc();
        int status = misc.usbDeviceMode(Misc.USBDEV_MODE_QUERY);
        if (status == Misc.USBDEV_ANDROID) {
            Intent ledIntent = new Intent();
            ledIntent.setAction(SET_USB_MODE);
            if (enable) {
                ledIntent.putExtra(USB_MODE, "MTP");
            } else {
                ledIntent.putExtra(USB_MODE, "NONE");
            }
            mContext.sendBroadcast(ledIntent);
            return 0;
        } else if (status == Misc.USBDEV_DISABLE) {
            return ERR_USBDEV_MODE_DISABLE;
        } else if (status == Misc.USBDEV_SERIAL_CDC) {
            return ERR_USBDEV_MODE_CDC;
        } else {
            return ERR_USBDEV_MODE_UNKNOWN;
        }
    }

    /**
     * The method gets a value indicating the enabled or disabled status of usb device mtp mode.
     * (Switching USB debugging on/off will affect the return value. Please do not change USB debugging state before using this function.)
     *
     * @return zero if mtp mode is disabled, one if mtp mode is enabled else error code defined in class constants.
     */
    public int isMtpEnabled(){
        Misc misc = new Misc();
        int status = misc.usbDeviceMode(Misc.USBDEV_MODE_QUERY);
        if (status == Misc.USBDEV_ANDROID) {
            Method get = null;
            String unlock_value = "0";
            String usb_state = "";
            try {
                if (null == get) {
                    if (null == get) {
                        Class<?> cls = Class.forName("android.os.SystemProperties");
                        get = cls.getDeclaredMethod("get", new Class<?>[]{String.class, String.class});
                    }
                }
                unlock_value = (String) (get.invoke(null, new Object[]{"persist.sys.usb.data.unlock", "0"}));
                usb_state = (String) (get.invoke(null, new Object[]{"sys.usb.state", ""}));
            } catch (Throwable e) {
                e.printStackTrace();
            }

            if (unlock_value.equals("1") && usb_state.contains("mtp"))
                return 1;
            else
                return 0;

        } else if (status == Misc.USBDEV_DISABLE) {
            return ERR_USBDEV_MODE_DISABLE;
        } else if (status == Misc.USBDEV_SERIAL_CDC) {
            return ERR_USBDEV_MODE_CDC;
        } else {
            return ERR_USBDEV_MODE_UNKNOWN;
        }
    }

    /**
     * The method locks or unlocks notifications.
     * @param locked Indicates to lock (true) or unlock (false) notifications.
     */
    public void setNotificationLocked(boolean locked){
        Intent intent = new Intent();
        intent.setAction(SET_NOTIFICATION_LOCK);
        intent.putExtra(LOCK_STATE, locked);
        mContext.sendBroadcast(intent);
    }

    private class BlLevelReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if(mOnSaioListener == null){
                return;
            }

            if(intent.getAction().equals(RET_2ND_BRIGHTNESS)) {
                float currLevel = intent.getExtras().getFloat(BRIGHTNESS_LEVEL);
                mOnSaioListener.onBrightness(currLevel);
            }else if(intent.getAction().equals(RET_2ND_TOUCHID)) {
                int touchId = intent.getExtras().getInt(SECOND_TOUCHID);
                mOnSaioListener.onTouchId(touchId);
            }
        }
    }

    //For Cradle
    /**
     * The method will set cradle reboot time
     *
     * @param  delay_time  after delay time time out, cradle would reboot
     */
    public void cradleReboot(int delay_time){
        Intent reboot = new Intent();
        reboot.setAction(ACTION_CRADLE_API_REBOOT);
        reboot.putExtra(CRADLE_REBOOT_DELAY_VALUE, delay_time);
        mContext.sendBroadcast(reboot);
    }

    /**
     * The method will let the cradle device exec factory reset
     *
     */
    public void cradleFactoryReset(){
        Intent reset = new Intent();
        reset.setAction(ACTION_CRADLE_API_FACTORY_RESET);
        mContext.sendBroadcast(reset);
    }

    /**
     * The method will get cradle device os and fw version
     *
     *
     */
    public void cradleGetInfo(){
        Intent getInfo = new Intent();
        getInfo.setAction(ACTION_CRADLE_API_GET_DEVICE_INFO);
        mContext.sendBroadcast(getInfo);
    }

    /**
     * The method will get cradle device log
     *
     * @param  uri  it is the log file storage uri  that you want to save
     */
    public void cradleGetLog(String uri){
        Intent getlog = new Intent();
        getlog.setAction(ACTION_CRADLE_API_GET_DEVICE_LOG);
        getlog.putExtra(CRADLE_STORE_LOG_PATH_TAG, uri);
        mContext.sendBroadcast(getlog);
    }

    /**
     * The method will let cradle device update os by local image
     *
     * @param  uri  it is the image file uri that you want to upgrade
     */
    public void cradleOsUpdate(String uri){
        Intent osUpdate = new Intent();
        osUpdate.setAction(ACTION_CRADLE_API_UPDATE_CRADLE_OS);
        osUpdate.putExtra(CRADLE_OS_IMAGE_URI_TAG, uri);
        mContext.sendBroadcast(osUpdate);
    }

    /**
     * The method will let cradle device update firmware by local image
     *
     * @param  uri  it is the image file uri that you want to upgrade
     */
    public void cradleFwUpdate(String uri){
        Intent fwUpdate = new Intent();
        fwUpdate.setAction(ACTION_CRADLE_API_UPDATE_CRADLE_PRINTER_FW);
        fwUpdate.putExtra(CRADLE_FW_IMAGE_URI_TAG, uri);
        mContext.sendBroadcast(fwUpdate);
    }

    /**
     * The method will let cradle set config by the config file
     *
     * @param  uri  it is the file uri that configuration (need follow format)
     */
    public void cradleBatchConfig(String uri){
        Intent config = new Intent();
        config.setAction(ACTION_CRADLE_API_BATCH_CONFIG);
        config.putExtra(CRADLE_CONFIG_URI_TAG, uri);
        mContext.sendBroadcast(config);
    }

    /**
     * The method will use cradle device to open cash drawer
     */
    public void cradleOpenCashDrawer(){
        Intent open = new Intent();
        open.setAction(ACTION_CRADLE_API_OPEN_CASHDRAWER_CONFIG);
        mContext.sendBroadcast(open);
    }

    /**
     * The method will use cradle device to query cash drawer  status(open or close)
     */
    public void cradleQueryCashDrawer(){
        Intent open = new Intent();
        open.setAction(ACTION_CRADLE_API_QUERY_CASHDRAWER_CONFIG);
        mContext.sendBroadcast(open);
    }

    /**
     * The method will use cradle device to close cash drawer
     * it does not work, because cash drawer need manual close
     */
    public void cradleCloseCashDrawer(){
        Intent open = new Intent();
        open.setAction(ACTION_CRADLE_API_CLOSE_CASHDRAWER_CONFIG);
        mContext.sendBroadcast(open);
    }

    private static native int native_set2ndTouchActive(int onoff);

    private static native int native_switchAntenna(int dist);

    private static native int native_powerOnMaxim(int dist);

    private static native int native_resetMaxim(int dist);

    private static native int native_powerOnGPS(int dist);

    private static native int native_resetGPS(int dist);

    private static native int native_setHeaterEnable(int dist);

    private static native int native_powerOnP95(int dist);

    private static native int native_powerOn3Gmodule();

    private static native int native_reset3Gmodule();

    private static native int native_setEppEnabled(boolean enabled);

    private static native int native_setPAEnabled(boolean enabled);
    
    private static native int native_setUsbSpeed(int mode);
    
    private static native int native_getUsbSpeed();
}
