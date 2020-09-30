package saioapi.service.SystemUIService;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

public class SystemUIService {
    private static final String TAG = "SystemUIService";
    /** The id for {@link #setNaviButtonVisibility} to set visibility of navigation bar back button. */
    public static final int NAVIBUTTON_BACK = 0x00000001;
    /** The id for {@link #setNaviButtonVisibility} to set visibility of navigation bar home button. */
    public static final int NAVIBUTTON_HOME = 0x00000010;
    /** The id for {@link #setNaviButtonVisibility} to set visibility of navigation bar recent button. */
    public static final int NAVIBUTTON_RECENT = 0x00000100;
    /** The id for {@link #setNaviButtonVisibility} to set visibility of navigation bar. */
    public static final int NAVIBUTTON_NAVIBAR = 0x00001000;
    
    private static final int STATUSBAR = 0x00010000;
    
    private static final String NAVIBUTTONMGR_ACTION_SHOW = "com.android.systemui.NaviButtonMgr.action.SHOW";
    private static final String NAVIBUTTONMGR_ACTION_HIDE = "com.android.systemui.NaviButtonMgr.action.HIDE";
    private static final String NAVIBUTTONMGR_ACTION_GONE = "com.android.systemui.NaviButtonMgr.action.GONE";
    private static final String NAVIBUTTONMGR_KEY = "com.android.systemui.NaviButtonMgr.KeyMap";
    
    /**
     * Set the enabled state of navigation bar buttons.
     * @param context application context
     * @param buttons Bitwise-or of the buttons
     * @param visibility One of VISIBLE ,INVISIBLE or GONE.
     */
    public static void setNaviButtonVisibility(Context context, int buttons, int visibility){
        Intent setIntent = new Intent();
        if(visibility == View.VISIBLE)
            setIntent.setAction(NAVIBUTTONMGR_ACTION_SHOW);
        else if(visibility == View.INVISIBLE)
            setIntent.setAction(NAVIBUTTONMGR_ACTION_HIDE);
        else if(visibility == View.GONE)
            setIntent.setAction(NAVIBUTTONMGR_ACTION_GONE);
        else
            return;
        setIntent.putExtra(NAVIBUTTONMGR_KEY, buttons);
        Log.i(TAG, "setNaviButtonVisibility Action = " + visibility + ", keyMap = " + String.format("0x%08X", buttons));
        context.sendBroadcast(setIntent);
    }
    
    /**
     * Set the enabled state of status bar.
     * @param context application context
     * @param visibility One of VISIBLE ,INVISIBLE or GONE.
     */
    public static void setStatusBarVisibility(Context context, int visibility){
        Intent setIntent = new Intent();
        if(visibility == View.VISIBLE)
            setIntent.setAction(NAVIBUTTONMGR_ACTION_SHOW);
        else if(visibility == View.INVISIBLE)
            setIntent.setAction(NAVIBUTTONMGR_ACTION_HIDE);
        else if(visibility == View.GONE)
            setIntent.setAction(NAVIBUTTONMGR_ACTION_GONE);
        else
            return;
        setIntent.putExtra(NAVIBUTTONMGR_KEY, STATUSBAR);
        Log.i(TAG, "setNaviButtonVisibility Action = " + visibility + ", keyMap = " + String.format("0x%08X", STATUSBAR));
        context.sendBroadcast(setIntent);
    }
}
