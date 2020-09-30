package saioapi.app.web;

//import java.io.File;
//import java.io.FileInputStream;
//import java.util.Arrays;
//
//import saioapi.base.Update;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.webkit.JavascriptInterface;

import saioapi.service.utility.EppInfo;
import saioapi.service.utility.EppInfo.ModuleInfo;
import saioapi.service.utility.MsgBase;
import saioapi.service.utility.PrinterInfo;
import saioapi.service.utility.UpdateReq;
import saioapi.service.utility.UpdateRsp;

final public class UpdateJsInterface implements SaioJsInterface
{
    final static public String ALIAS = "UpdateJsInterface";
    
    final static private String TAG = ALIAS;
    
    final static private int EVENT_EPP_VERSION = 0;
    final static private int EVENT_PRINTER_VERSION = 1;
    final static private int EVENT_EPP_MODULE = 2;
    final static private int EVENT_EPP_KCV = 3;
    final static private int EVENT_UPDATE_REQUEST = 4;
    final static private int EVENT_UPDATE_RESPONSE = 5;
    final static private int EVENT_SERVICE_STATE = 6;
    
    private JsCallback mCallback = null;
    private Context mContext = null;
    private String mJsListener = null;
    private Handler mEventHandler = new Handler();
    private int mEvent = 0;
    private int mEventResult = 0;
//    private Update mUpdate = null;
//    private String mCertPath = null;
    private Messenger mLocalMessenger = null;
    private Messenger mServiceMessenger = null;
    private SaioUtilityServiceConnection mConnection = null;
    private boolean mIsServiceBound = false;
    //
    private PrinterInfo.FirmwareVersion mPrinterFwVer = null;
    private EppInfo.FirmwareVersion mEppFwVer = null;
    private Bundle mEppModBundle = null;
    private int mEppModSize = 0;
    private String mEppKCV = null;
    private UpdateRsp mUpdRsp = null;
    
    public UpdateJsInterface(Context c)
    {
        mContext = c;
//        mUpdate = new Update();
        mLocalMessenger = new Messenger(mMsgHandler);
        mConnection = new SaioUtilityServiceConnection();
    }
    
    @JavascriptInterface
    public boolean bindService()
    {
        
        if(!mIsServiceBound)
        {
            boolean ret = mContext.bindService(new Intent(MsgBase.SERVICE_NAME), mConnection, Context.BIND_AUTO_CREATE);
            //Log.v(TAG, "bind SaioUtilityService: " + ret);
            
            return ret;
        }
        
        Log.v(TAG, "already bind SaioUtilityService");
        return true;
    }
    
    @JavascriptInterface
    public void unbindService()
    {
        if(mIsServiceBound)
        {
            mContext.unbindService(mConnection);
            mServiceMessenger = null;
            mIsServiceBound = false;
            Log.v(TAG, "unbind SaioUtilityService");
        }
        else
        {
            Log.v(TAG, "already unbind SaioUtilityService");
        }
    }
    
    @JavascriptInterface
    public boolean requestServiceState()
    {
        if(null == mServiceMessenger || null == mLocalMessenger || !mIsServiceBound)
            return false;
        
        Message reqMsg = Message.obtain(null, MsgBase.MSG_SERVICE_STATE, 0, 0);
        reqMsg.replyTo = mLocalMessenger;
    
        try{
            mServiceMessenger.send(reqMsg);
        }catch(RemoteException e){
            e.printStackTrace();
            return false;
        }
        
        mPrinterFwVer = null;
        
        return true;
    }
    
    @JavascriptInterface
    public boolean requestPrinterVersion()
    {
        if(null == mServiceMessenger || null == mLocalMessenger || !mIsServiceBound)
            return false;
        
        Message reqMsg = Message.obtain(null, MsgBase.MSG_PRINTER, EppInfo.ACTION_VERSION, 0);
        reqMsg.replyTo = mLocalMessenger;

        try{
            mServiceMessenger.send(reqMsg);
        }catch(RemoteException e){
            e.printStackTrace();
            return false;
        }
        
        return true;
    }
    
    @JavascriptInterface
    public boolean requestEppVersion()
    {
        if(null == mServiceMessenger || null == mLocalMessenger || !mIsServiceBound)
            return false;
        
        Message reqMsg = Message.obtain(null, MsgBase.MSG_EPP, EppInfo.ACTION_VERSION, 0);
        reqMsg.replyTo = mLocalMessenger;

        try{
            mServiceMessenger.send(reqMsg);
        }catch(RemoteException e){
            e.printStackTrace();
            return false;
        }
        
        mEppFwVer = null;
        
        return true;
    }
    
    @JavascriptInterface
    public boolean requestEppModuleInfo()
    {
        if(null == mServiceMessenger || null == mLocalMessenger || !mIsServiceBound)
            return false;
        
        Message reqMsg = Message.obtain(null, MsgBase.MSG_EPP, EppInfo.ACTION_MODULE, 0);
        reqMsg.replyTo = mLocalMessenger;

        try{
            mServiceMessenger.send(reqMsg);
        }catch(RemoteException e){
            e.printStackTrace();
            return false;
        }
        
        mEppModBundle = null;
        mEppModSize = 0;
        
        return true;
    }
    
    @JavascriptInterface
    public boolean requestKCV(String key)
    {
        if(null == mServiceMessenger || null == mLocalMessenger || !mIsServiceBound)
            return false;
        
        Message reqMsg = Message.obtain(null, MsgBase.MSG_EPP, EppInfo.ACTION_KCV, 0);
        reqMsg.replyTo = mLocalMessenger;
        
        Bundle b = reqMsg.getData();
        EppInfo.setKcv(b, key);
        reqMsg.setData(b);

        try{
            mServiceMessenger.send(reqMsg);
        }catch(RemoteException e){
            e.printStackTrace();
            return false;
        }
        
        mEppKCV = null;
        
        return true;
    }
    
    @JavascriptInterface
    public boolean updateSystem(String dir, String file)
    {
        if(null == mServiceMessenger || null == mLocalMessenger || !mIsServiceBound)
            return false;
        
        Message reqMsg = Message.obtain(null, MsgBase.MSG_UPDATE, UpdateReq.ACTION_SYSTEM, 0);
        reqMsg.replyTo = mLocalMessenger;
        
        Bundle b = reqMsg.getData();
        UpdateReq req = new UpdateReq(dir, file, false, false);
        UpdateReq.setBundleData(b, req);
        reqMsg.setData(b);
        
        try{
            mServiceMessenger.send(reqMsg);
        }catch(RemoteException e){
            e.printStackTrace();
            return false;
        }
        
        mEppFwVer = null;
        
        return true;
    }
    
    @JavascriptInterface
    public boolean updatePrinter(String dir, String file, boolean skipIfSameVer, boolean delSrcIfOk)
    {
        if(null == mServiceMessenger || null == mLocalMessenger || !mIsServiceBound)
            return false;
        
        Message reqMsg = Message.obtain(null, MsgBase.MSG_UPDATE, UpdateReq.ACTION_PRINTER, 0);
        reqMsg.replyTo = mLocalMessenger;
        
        Bundle b = reqMsg.getData();
        UpdateReq req = new UpdateReq(dir, file, skipIfSameVer, delSrcIfOk);
        UpdateReq.setBundleData(b, req);
        reqMsg.setData(b);
        
        try{
            mServiceMessenger.send(reqMsg);
        }catch(RemoteException e){
            e.printStackTrace();
            return false;
        }
        
        mEppFwVer = null;
        
        return true;
    }
    
    @JavascriptInterface
    public boolean updateEpp(String dir, String file, boolean skipIfSameVer, boolean delSrcIfOk)
    {
        if(null == mServiceMessenger || null == mLocalMessenger || !mIsServiceBound)
            return false;
        
        Message reqMsg = Message.obtain(null, MsgBase.MSG_UPDATE, UpdateReq.ACTION_EPP, 0);
        reqMsg.replyTo = mLocalMessenger;
        
        Bundle b = reqMsg.getData();
        UpdateReq req = new UpdateReq(dir, file, skipIfSameVer, delSrcIfOk);
        UpdateReq.setBundleData(b, req);
        reqMsg.setData(b);
        
        try{
            mServiceMessenger.send(reqMsg);
        }catch(RemoteException e){
            e.printStackTrace();
            return false;
        }
        
        mEppFwVer = null;
        
        return true;
    }
    
    @JavascriptInterface
    public String getPrinterVersion()
    {
        if(null != mPrinterFwVer)
            return mPrinterFwVer.getVersion();
        
        return null;
    }
    
    @JavascriptInterface
    public String getPrinterBuild()
    {
        if(null != mPrinterFwVer)
            return mPrinterFwVer.getBuild();
        
        return null;
    }
    
//    @JavascriptInterface
//    public String getPrinterCID()
//    {
//        if(null != mPrinterFwVer)
//            return mPrinterFwVer.getCID();
//        
//        return null;
//    }
//    
//    @JavascriptInterface
//    public String getPrinterModelName()
//    {
//        if(null != mPrinterFwVer)
//            return mPrinterFwVer.getModelName();
//        
//        return null;
//    }
//    
//    @JavascriptInterface
//    public String getPrinterSN()
//    {
//        if(null != mPrinterFwVer)
//            return mPrinterFwVer.getSN();
//        
//        return null;
//    }
//    
    @JavascriptInterface
    public String getEppVersion()
    {
        if(null != mEppFwVer)
            return mEppFwVer.getVersion();
        
        return null;
    }
    
    @JavascriptInterface
    public String getEppBuild()
    {
        if(null != mEppFwVer)
            return mEppFwVer.getBuild();
        
        return null;
    }
    
    @JavascriptInterface
    public String getEppCID()
    {
        if(null != mEppFwVer)
            return mEppFwVer.getCID();
        
        return null;
    }
    
    @JavascriptInterface
    public String getEppModelName()
    {
        if(null != mEppFwVer)
            return mEppFwVer.getModelName();
        
        return null;
    }
    
    @JavascriptInterface
    public String getEppSN()
    {
        if(null != mEppFwVer)
            return mEppFwVer.getSN();
        
        return null;
    }
    
    @JavascriptInterface
    public String getKCV()
    {
        return mEppKCV;
    }
    
    @JavascriptInterface
    public int getEppModuleCount()
    {
        return mEppModSize;
    }
    
    @JavascriptInterface
    public String getEppModuleBuild(int index)
    {
        ModuleInfo mod = _getEppMod(index);
        if(null != mod)
            return mod.getModuleBuild();
        
        return null;
    }
    
    @JavascriptInterface
    public String getEppModuleChecksum(int index)
    {
        ModuleInfo mod = _getEppMod(index);
        if(null != mod)
            return mod.getModuleChecksum();
        
        return null;
    }
    
    @JavascriptInterface
    public String getEppModuleID(int index)
    {
        ModuleInfo mod = _getEppMod(index);
        if(null != mod)
            return mod.getModuleID();
        
        return null;
    }
    
    @JavascriptInterface
    public String getEppModuleName(int index)
    {
        ModuleInfo mod = _getEppMod(index);
        if(null != mod)
            return mod.getModuleName();
        
        return null;
    }
    
    @JavascriptInterface
    public String getEppModuleVersion(int index)
    {
        ModuleInfo mod = _getEppMod(index);
        if(null != mod)
            return mod.getModuleVersion();
        
        return null;
    }
    
    @JavascriptInterface
    public int getUpdateFinishCount()
    {
        if(null != mUpdRsp)
            return mUpdRsp.getRunFinishCount();
        
        return 0;
    }
    
    @JavascriptInterface
    public int getUpdateTotalCount()
    {
        if(null != mUpdRsp)
            return mUpdRsp.getTotalCount();
        
        return 0;
    }
    
    @JavascriptInterface
    public int getUpdatePercentage()
    {
        if(null != mUpdRsp)
            return (int)mUpdRsp.getUpdatePercentage();
        
        return 0;
    }
    
    @JavascriptInterface
    public String getUpdateMessage()
    {
        if(null != mUpdRsp)
            return mUpdRsp.getMessage();
        
        return null;
    }
    
//    @JavascriptInterface
//    public int getPubkeyNum()
//    {
//        return mUpdate.getPubkeyNum();
//    }
//    
//    @JavascriptInterface
//    public int getPubkeyCert(int idx, byte[] cert)
//    {
//        return mUpdate.getPubkeyCert(idx, cert);
//    }
//    
//    @JavascriptInterface
//    public int getCertificate(byte[] spk, byte[] cert)
//    {
//        //return mUpdate.certificate(cert, certInfo);
//        return 0;
//    }
//    
//    @JavascriptInterface
//    public int install(String srcDir)
//    {
//        // get the full certificate path
//        mCertPath = UPDATE_BASE + srcDir;
//        
//        // certificate it
//        File f = new File(mCertPath);
//        byte[] buffer = new byte[(int) f.length()];
//        Log.d(TAG, "File '" + mCertPath + "' size: " + f.length());
//        
//        try
//        {
//            int nIndex = 0;
//            int nRead = 0;
//            byte[] tmp = new byte[200];
//            Arrays.fill(buffer, (byte) 0);
//            FileInputStream is = new FileInputStream(f);
//            //
//            while (-1 != (nRead = is.read(tmp)))
//            {
//                System.arraycopy(tmp, 0, buffer, nIndex, nRead);
//                nIndex = nIndex + nRead;
//            }
//            is.close();
//        }
//        catch(Exception e)
//        {
//            Log.e(TAG, "Read file '" + mCertPath + "' error: " + e.getMessage());
//            return -1;
//        }
//        
//        return mUpdate.install(UPDATE_BASE, buffer);
//    }
//    
//    //TODO: install app
//    
    @JavascriptInterface
    public void setOnEventListener(String listener)
    {
        mJsListener = listener;
    }
    
    @Override
    public void setCallback(JsCallback cb)
    {
        mCallback = cb;
    }
    
    private Handler mMsgHandler = new Handler()
            {
                @Override
                public void handleMessage(Message msg)
                {
                    //Log.d(TAG, "MsgHandler.handleMessage(" + msg.what + ", " + msg.arg1 + ", " + msg.arg2 + ")");
                    if(MsgBase.MSG_EPP == msg.what)
                    {
                        if(EppInfo.ACTION_VERSION == msg.arg1)
                        {
                            mEppFwVer = EppInfo.getFirmwareVersion(msg.getData());
                            _sendEvent(EVENT_EPP_VERSION, msg.arg2);
                        }
                        else if(EppInfo.ACTION_MODULE == msg.arg1)
                        {
                            mEppModBundle = msg.getData();
                            
                            for(mEppModSize = 0;;)
                            {
                                ModuleInfo mod = EppInfo.getModuleInfo(mEppModBundle, mEppModSize);
                                if(null != mod)
                                {
                                    mEppModSize++;
                                    if(mod.isLast())
                                        break;
                                }
                                else
                                    break;
                            }
                            _sendEvent(EVENT_EPP_MODULE, msg.arg2);
                        }
                        else if(EppInfo.ACTION_KCV == msg.arg1)
                        {
                            mEppKCV = EppInfo.getKcv(msg.getData());
                            _sendEvent(EVENT_EPP_KCV, msg.arg2);
                        }
                        else
                        {
                            Log.w(TAG, "Unknown action (" + msg.arg1 + ") of message (" + msg.what + ")");
                        }
                    }
                    else if(MsgBase.MSG_PRINTER == msg.what)
                    {
                        if (PrinterInfo.ACTION_VERSION == msg.arg1)
                        {
                            mPrinterFwVer = PrinterInfo.getFirmwareVersion(msg.getData());
                            _sendEvent(EVENT_PRINTER_VERSION, msg.arg2);
                        }
                        else
                        {
                            Log.w(TAG, "Unknown action (" + msg.arg1 + ") of message (" + msg.what + ")");
                        }
                    }
                    else if(MsgBase.MSG_UPDATE_INFO == msg.what)
                    {
                        mUpdRsp = UpdateRsp.getBundleData(msg.getData());
                        _sendEvent(EVENT_UPDATE_RESPONSE, msg.arg2);
                    }
                    else if(MsgBase.MSG_UPDATE == msg.what)
                    {
                        _sendEvent(EVENT_UPDATE_REQUEST, msg.arg2);
                    }
                    else if(MsgBase.MSG_SERVICE_STATE == msg.what)
                    {
                        _sendEvent(EVENT_SERVICE_STATE, msg.arg2);
                    }
                    else
                    {
                        Log.w(TAG, "Unknown msg (" + msg.what + ")");
                    }
                }
            };
    
    private class SaioUtilityServiceConnection implements ServiceConnection
    {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            Log.i(TAG, "Connected to SaioUtilityService");
            mIsServiceBound = true;
            mServiceMessenger = new Messenger(service);
        }
        
        @Override
        public void onServiceDisconnected(ComponentName name)
        {
            Log.i(TAG, "Disonnected to SaioUtilityService");
            mServiceMessenger = null;
            mIsServiceBound = false;
        }
        
    };
    
    private void _sendEvent(int event, int result)
    {
        if(null != mCallback)
        {
            mEvent = event;
            mEventResult = result; 
            mEventHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            //Log.d(TAG, "onEvent(javascript:" + mJsListener + "('" + mEvent + "', '" + mEventResult + "'))");
                            if(null != mJsListener)
                                mCallback.onLoadUrl("javascript:" + mJsListener + "('" + mEvent + "', '" + mEventResult + "')");
                        }
                    });
        }
    }
    
    private ModuleInfo _getEppMod(int index)
    {
        if(null != mEppModBundle)
        {
            ModuleInfo mod = EppInfo.getModuleInfo(mEppModBundle, index);
            return mod;
        }
        
        return null;
    }
    
}
