package saioapi.app.web;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import saioapi.base.Misc;

final public class MiscJsInterface implements SaioJsInterface
{
    final static public String ALIAS = "MiscJsInterface";
    
    final static private String TAG = ALIAS;

    private Misc mMisc = null;
    private Context mContext = null;
    private JsCallback mCallback = null;
    
    public MiscJsInterface(Context c)
    {
        mContext = c;
        mMisc = new Misc();
    }
    
    @JavascriptInterface
    public int cashDrawerStatus()
    {
        int ret = mMisc.cashDrawer((short)0, Misc.CASH_DRAWER_QUERY);
        //Log.d(TAG, "cashDrawerStatus(Misc.CASH_DRAWER_QUERY): " + ret);
        
        return ret;
    }
    
    @JavascriptInterface
    public int openCashDrawer()
    {
        int ret = mMisc.cashDrawer((short)0, Misc.CASH_DRAWER_OPEN);
        //Log.d(TAG, "openCashDrawer(CASH_DRAWER_OPEN): " + ret);
        
        return ret;
    }
    
    @JavascriptInterface
    public int lastError()
    {
        int errno = mMisc.lastError();
        //Log.d(TAG, "lastError(): " + errno);
        
        return errno;
    }
    
    /**
     * Intent to invoke Settings
     */
    @JavascriptInterface
    public void startSettings()
    {
        Log.v(TAG, "invoke Settings");
        mContext.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
    }

    @Override
    public void setCallback(JsCallback cb)
    {
        mCallback = cb;
    }
    
}