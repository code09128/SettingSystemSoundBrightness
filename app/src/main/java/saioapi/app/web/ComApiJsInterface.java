package saioapi.app.web;

import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;

import saioapi.OnEventListener;
import saioapi.comm.Com;

public class ComApiJsInterface implements SaioJsInterface {
    public static final String ALIAS = "ComApiJsInterface";
    private final String TAG = ALIAS;
    private final int MAX_DATA_SIZE = 256;
    private Activity mActivity;
    private JsCallback mJsCallback;
    private String mOnEventListener;
    private int mEvent;
    private Com mCom = null;
    private int mHandle = 0;
    private static char[] mHexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
    
    public ComApiJsInterface(Activity activity){
        mActivity = activity;
        mCom = new Com();
    }
    
    public void onDestory(){
        Log.i(TAG, "onDestory");
        if(mHandle != 0)
            mCom.close(mHandle);
        mHandle = 0;
    }
    
    @JavascriptInterface
    public int open(String devId){
        Log.i(TAG, "devId = "+devId);
        int deviceId = Integer.valueOf(devId);
        mHandle = mCom.open((short)deviceId);
        if(mHandle > 0)
            mCom.setOnEventListener(comOnEventListener);
        else
            return mHandle;
        return 0;
    }
    
    @JavascriptInterface
    public int close(){
        if(mHandle == 0)
            return 0;
        int ret = mCom.close(mHandle);
        Log.i(TAG, "CloseCom result: "+ret);
        mHandle = 0;
        return ret;
    }
    
    @JavascriptInterface
    public int connect(int baud, int data_size, int stop_bit, int parity, int protocol){
        mCom.connect(mHandle, 
                        Integer.valueOf(baud),
                        Integer.valueOf(data_size).byteValue(),
                        Integer.valueOf(stop_bit).byteValue(),
                        Integer.valueOf(parity).byteValue(),
                        Integer.valueOf(protocol), null);
        return 0;
    }

    @JavascriptInterface
    public String read(int timeout){
        byte[] byteData = new byte[MAX_DATA_SIZE];
        int ret = mCom.read(mHandle, byteData, byteData.length, Integer.valueOf(timeout));
        Log.i(TAG, "read data length = "+ret);
        if(ret > 0){
            StringBuilder s = new StringBuilder();
            for(int i=0; i<ret; i++){
                s.append(String.format("%c", byteData[i] & 0xFF));
            }
            s.deleteCharAt(s.length()-1);
            return s.toString();
        }else
            return null;
    }
    
    @JavascriptInterface
    public String readHex(int timeout){
        byte[] byteData = new byte[MAX_DATA_SIZE];
        int ret = mCom.read(mHandle, byteData, byteData.length, Integer.valueOf(timeout));
        Log.i(TAG, "read data length = "+ret);
        if(ret > 0){
            StringBuilder s = new StringBuilder();
            int v;

            for(int j=0; j < ret; j++) {
                v = byteData[j] & 0xFF;
                s.append(mHexArray[v>>>4]).append(mHexArray[v & 0x0F]).append(" ");
            }
            return s.toString();
        }else
            return null;
    }
    
    @JavascriptInterface
    public int write(String data, int timeout){
        byte[] byteData = data.getBytes();
        return mCom.write(mHandle, byteData, byteData.length, Integer.valueOf(timeout));
    }
    
    @JavascriptInterface
    public int writeHex(String data, int timeout){
        String[] strData = data.split(" ");
        byte[] byteData = new byte[strData.length];
        for(int i=0; i<strData.length; i++){
            byteData[i] = (byte) ((Character.digit(strData[i].charAt(0), 16) << 4) + Character.digit(strData[i].charAt(1), 16));
        }
        return mCom.write(mHandle, byteData, byteData.length, Integer.valueOf(timeout));
    }
    
    @JavascriptInterface
    public int lastError(){
        return mCom.lastError();
    }
    
    @JavascriptInterface
    public int cancel(){
        return mCom.cancel(mHandle);
    }
    
    @JavascriptInterface
    public int status(){
        return mCom.status(mHandle);
    }

    @Override
    public void setCallback(JsCallback cb) {
        // TODO Auto-generated method stub
        mJsCallback = cb;
    }
    
    @JavascriptInterface
    public void setOnEventListener(String onEventListener){
        mOnEventListener = onEventListener;
    }
    
    private OnEventListener comOnEventListener = new OnEventListener(){

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

}
