package saioapi.app;

import android.util.Log;

import saioapi.OnEventListener;
import saioapi.comm.Com;

/**
 * This class is deprecated since version 170308_r1.
 */
public class MsrApi{
    private final String TAG = "MsrApi";
    private final int MSR_TIMEOUT = 5000;
    private final int MSR_TRACK_DATA = 256;
    private final String MSR_ON = "Q40";
    private final String MSR_OFF = "Q41";
    private final String MSR_REQUESTTK2DATA = "Q1";
    private final String MSR_REQUESTDATA = "Q1X";
    private final String MSR_GETSWIPESPEED = "Q5";
    
    private Com mCom;
    private int mHandle;
    private int mLastError;
    private OnEventListener mOnEventListener;
    
    private boolean mTrack1Enable;
    private boolean mTrack2Enable;
    private boolean mTrack3Enable;
    private byte[] mTrack1Data;
    private byte[] mTrack2Data;
    private byte[] mTrack3Data;
    private int mSwipeSpeed;
    
    
    public MsrApi(){
        mCom = new Com();
        
        mTrack1Enable = false;
        mTrack2Enable = false;
        mTrack3Enable = false;
        mSwipeSpeed = 0;
    }
    
    public int open(int devId){
        if(mHandle != 0)
            return Com.ERR_NOT_READY;
        Log.i(TAG, "devId = "+devId);
        int ret;
        mHandle = mCom.open((short)devId);
        if(mHandle != Com.ERR_OPERATION){
            ret = mCom.connect(mHandle, 115200, (byte)0, (byte)0, (byte)0, (byte)Com.PROTOCOL_XAC_VNG, null);
            if(ret != 0){
                Log.i(TAG, "OpenVNG: connect fail: "+ret);
                mLastError = mCom.lastError();
                mHandle = 0;
                return ret;
            }
        }else{
            mHandle = 0;
            return Com.ERR_OPERATION;
        }
        
        mCom.setOnEventListener(comOnEventListener);
        
        byte[] cmd = MSR_ON.getBytes();
        ret = mCom.write(mHandle, cmd, cmd.length, MSR_TIMEOUT);
        if(ret < 0){
            Log.i(TAG, "MSR ON fail: "+ret);
            mLastError = mCom.lastError();
            return ret;
        }
        return 0;
    }
    
    public int close(){
        int ret;
        if(mHandle == 0)
            return 0;
        //MSR OFF
        byte[] cmd = MSR_OFF.getBytes();
        ret = mCom.write(mHandle, cmd, cmd.length, MSR_TIMEOUT);
        if(ret < 0){
            Log.i(TAG, "MSR OFF fail: "+ret);
            return ret;
        }
        
        //Close communicate device
        ret = mCom.close(mHandle);
        
        Log.i(TAG, "CloseMSR result: "+ret);
        if(ret != 0){
            return ret;
        }
        mHandle = 0;
        return 0;
    }
    
    public int readEnable(boolean track1, boolean track2, boolean track3, int timeOut){
        int ret;
        mTrack1Enable = track1;
        mTrack2Enable = track2;
        mTrack3Enable = track3;
        byte[] cmd;
        if((mTrack2Enable == true)&&((mTrack1Enable == false)&&(mTrack3Enable == false)))
            cmd = MSR_REQUESTTK2DATA.getBytes();
        else
            cmd = MSR_REQUESTDATA.getBytes();
        ret = mCom.write(mHandle, cmd, cmd.length, timeOut);
        if(ret < 0){
            Log.i(TAG, "MSR_ReadEnable fail: "+ret);
            mLastError = mCom.lastError();
            return ret;
        }
        return 0;
    }
    
    public int reset(){
        return mCom.cancel(mHandle);
    }
    
    public int getDataLength(int track){
        switch(track){
        case 1:
            if(mTrack1Data != null)
                return mTrack1Data.length;
            else
                return 0;
            
        case 2:
            if(mTrack2Data != null)
                return mTrack2Data.length;
            else
                return 0;
            
        case 3:
            if(mTrack3Data != null)
                return mTrack3Data.length;
            else
                return 0;
        default:
            mLastError = Com.ERR_INVALID_PARAM;
            break;
        }
        return Com.ERR_OPERATION;
    }
    
    public byte[] getData(int track){
        switch(track){
        case 1:
            if(mTrack1Data != null)
                return mTrack1Data.clone();
            else
                return null;
            
        case 2:
            if(mTrack2Data != null)
                return mTrack2Data.clone();
            else
                return null;
            
        case 3:
            if(mTrack3Data != null)
                return mTrack3Data;
            else
                return null;
            
        default:
            return null;
        }
    }
    
    public int getTrackError(int track){
        return 0;
    }
    
    public int swipeSpeed(){
        int ret;
        //Get swipe speed
        byte[] cmd = MSR_GETSWIPESPEED.getBytes();
        ret = mCom.write(mHandle, cmd, cmd.length, MSR_TIMEOUT);
        if(ret < 0){
            Log.i(TAG, "MSR get swipe speed fail: "+ret);
            mLastError = mCom.lastError();
            return ret;
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mSwipeSpeed;
    }
    
    public void setOnEventListener(OnEventListener onEventListener){
        mOnEventListener = onEventListener;
    }
    
    public int lastError(){
        return mLastError;
    }

    private OnEventListener comOnEventListener = new OnEventListener(){

        @Override
        public void onEvent(int handle, int event) {
            // TODO Auto-generated method stub
            if(mHandle != handle)
                return;
            Log.i(TAG, "Received event: "+event);
            if(handle == mHandle){
                if(event == Com.EVENT_DATA_READY){
                    byte[] inBuffer = new byte[MSR_TRACK_DATA];
                    int result = mCom.read(handle, inBuffer, inBuffer.length, MSR_TIMEOUT);
                    
                    if(result != Com.ERR_OPERATION){
                        if((inBuffer[0] == 0x38)&&(inBuffer[1] == 0x31)&&(inBuffer[2] == 0x2E)){ //81., MSR data
                            getTrackData(inBuffer);
                        }else if((inBuffer[0] == 0x51)&&(inBuffer[1] == 0x35)){ //Q5, get swipe speed
                            getSwipeSpeed(inBuffer);
                            return;
                        }
                    }
                }
                if(mOnEventListener != null)
                    mOnEventListener.onEvent(handle, event);
            }
        }
        
    };
    
    private void getSwipeSpeed(byte[] data){
        mSwipeSpeed = (data[2]-0x30)*1000;
        mSwipeSpeed += (data[3]-0x30)*100;
        mSwipeSpeed += (data[4]-0x30)*10;
        mSwipeSpeed += (data[5]-0x30);
    }
    
    private void getTrackData(byte[] data){
        //<STX>81{flag}{TK2 data}<ETX>{LRC}
        //flag ={.} – MSR data, {:} – RFID data
        //      0x2E            0x3A
        //<STX>81{flag}{TK1 data}<FS>{TK2 data}<FS>{TK3 data}<RS>{Len}{Data}<ETX>{LRC}
        //flag: {.} = MSR, {:} = RFID, {+} = MSRE, {#} = RFIDE
        //      0x2E       0x3A        0x2B        0x23
        //<FS> = 0x1c, <RS> = 0x1e.
        int i, index;
        int track1Length = 0;
        int track2Length = 0;
        int track3Length = 0;
        int datalen = 0;
        boolean containExData = false;
        
        for(i = 3; i<data.length; i++){
            if(data[i] == 0x1E){
                containExData = true;
                break;
            }
        }
        
        for(i = 3; i<data.length; i++){
            if(data[i] == 0x00){
                datalen = i;
                break;
            }
        }
        
        
        index = 0;
        if(containExData == true){
            for(i = 3; i<datalen; i++){
                if((data[i] == 0x1C)||(data[i] == 0x1E)){
                    if(index == 0)
                        track1Length = i-3;
                    else if(index == 1)
                        track2Length = i-track1Length-4;
                    else if(index == 2){
                        track3Length = i-track2Length-track1Length-5;
                        break;
                    }
                    index++;
                }
                index++;
            }
        }else{
            for(i = 3; i<datalen; i++){
                if((data[i] == 0x1C)||(data[i] == 0x1E)){
                    if(index == 0)
                        track1Length = i-3;
                    else if(index == 1){
                        track2Length = i-track1Length-4;
                        break;
                    }
                    index++;
                }
            }
            track3Length = datalen - (track1Length+track2Length+5); //81{flag} + <FS>x2
        }
        
        if(track1Length > 0){
            mTrack1Data = new byte[track1Length];
            index = 3;
            for(i = 0; i<track1Length; i++){
                mTrack1Data[i] = data[i+index];
            }
        }else
            mTrack1Data = null;
        
        if(track2Length > 0){
            mTrack2Data = new byte[track2Length];
            index = track1Length+4;
            for(i = 0; i<track2Length; i++){
                mTrack2Data[i] = data[i+index];
            }
        }else
            mTrack2Data = null;
        
        if(track3Length > 0){
            mTrack3Data = new byte[track3Length];
            index = track1Length+track2Length+5;
            for(i = 0; i<track3Length; i++){
                mTrack3Data[i] = data[i+index];
            }
        }else
            mTrack3Data = null;
    }
}
