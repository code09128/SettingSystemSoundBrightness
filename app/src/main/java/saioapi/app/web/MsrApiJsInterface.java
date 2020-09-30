package saioapi.app.web;

import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;

import saioapi.OnEventListener;
import saioapi.app.MsrApi;

public class MsrApiJsInterface implements SaioJsInterface {
    public static final String ALIAS = "MsrApiJsInterface";
    private final String TAG = ALIAS;
    private Activity mActivity;
    private JsCallback mJsCallback;
    private String mOnEventListener;
    private int mEvent;
    private MsrApi mMsrApi = null;
    private String mIPS;
    
    
    public MsrApiJsInterface(Activity activity){
        mActivity = activity;
        mMsrApi = new MsrApi();
    }
    
    public void onDestory(){
        if(mMsrApi != null)
            close();
    }
    
    @JavascriptInterface
    public int open(String devId){
        Log.i(TAG, "devId = "+devId);
        int deviceId = Integer.valueOf(devId);
        int ret = mMsrApi.open((short)deviceId);
        if(ret == 0)
            mMsrApi.setOnEventListener(msrOnEventListener);
        else
            return ret;
        Log.i(TAG, "MSR_Open return 0");
        return 0;
    }
    
    @JavascriptInterface
    public int close(){
        int ret = mMsrApi.close();
        Log.i(TAG, "CloseMSR result: "+ret);
        return ret;
    }
    
    @JavascriptInterface
    public int readEnable(boolean track1, boolean track2, boolean track3, String timeOut){
        return mMsrApi.readEnable(track1, track2, track3, Integer.valueOf(timeOut));
    }
    
    @JavascriptInterface
    public int reset(){
        return mMsrApi.reset();
    }
    
    @JavascriptInterface
    public int getDataLength(String track){
        int trackId = Integer.valueOf(track);
        return mMsrApi.getDataLength(trackId);
    }
    
    @JavascriptInterface
    public String getData(String track){
        int trackId = Integer.valueOf(track);
        StringBuilder data = new StringBuilder();
        byte[] dataBuffer = mMsrApi.getData(trackId);
        if(dataBuffer == null)
            return "";
        for(int i=0; i<getDataLength(track); i++)
            data.append(String.format("%c", dataBuffer[i] & 0xFF));
        return data.toString();
    }
    
    @JavascriptInterface
    public int getTrackError(String track){
        int trackId = Integer.valueOf(track);
        return mMsrApi.getTrackError(trackId);
    }
    
    @JavascriptInterface
    public int swipeSpeed(){
        return mMsrApi.swipeSpeed();
    }
    
    @JavascriptInterface
    public String getIPS(){
        int speed = mMsrApi.swipeSpeed();
        double ips = ipsCalc(speed);
        if(ips != 0){
            mIPS = Double.toString(ips);
            int dotpos = mIPS.indexOf('.');
            mIPS = mIPS.substring(0, dotpos+2);
        }
        return mIPS;
    }
    
    @JavascriptInterface
    public void setOnEventListener(String onEventListener){
        mOnEventListener = onEventListener;
    }
    
    @JavascriptInterface
    public int lastError(){
        return mMsrApi.lastError();
    }

    @Override
    public void setCallback(JsCallback cb) {
        // TODO Auto-generated method stub
        mJsCallback = cb;
    }
    
    private OnEventListener msrOnEventListener = new OnEventListener(){

        @Override
        public void onEvent(int handle, int event) {
            // TODO Auto-generated method stub
            if(mJsCallback != null){
                mEvent = event;
                mActivity.runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        mJsCallback.onLoadUrl("javascript:"+mOnEventListener+"('"+mEvent+"')");
                    }
                    
                });
            }
        }
        
    };

    private double ipsCalc(int speed){
        //y = 3.9439x4 + 6.4425x3 + 15.291x2 + 37.427x - 0.0013
        double x = 86/((double)speed);
        double value = (3.9439* Math.pow(x, 4))+(6.4425* Math.pow(x, 3))+(15.291* Math.pow(x, 2))+(37.427*x)-0.0013;
        return value;
    }

}
