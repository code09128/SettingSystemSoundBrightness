package com.example.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;

import saioapi.base.Misc;
import saioapi.util.Ver;

//import android.preference.PreferenceCategory;

public class SaioSysInfo extends PreferenceActivity
{
    private static final String TAG             = "SaioSysInfo";
    
//    private static final String KEY_CATEGORY    = "preference_sysinfo_category";
    private static final String KEY_PRODUCT     = "preference_sysinfo_prd";
    private static final String KEY_PRODUCT_VER = "preference_sysinfo_prd_ver";
    private static final String KEY_MFG         = "preference_sysinfo_mfg";
    private static final String KEY_SN          = "preference_sysinfo_sn";
    private static final String KEY_LOADER_VER  = "preference_sysinfo_ldr_ver";
    private static final String KEY_ROOTKEY_ST  = "preference_sysinfo_rk_st";
    private static final String KEY_SECURE_ST   = "preference_sysinfo_secure_st";
    private static final String KEY_WIFI_MAC    = "preference_sysinfo_wifi_mac";
    private static final String KEY_ETH_MAC     = "preference_sysinfo_eth_mac";
    private static final String KEY_SAIO_LIB    = "preference_sysinfo_saio_lib";
    private static final String KEY_UBOOT_ENV   = "preference_sysinfo_uboot_env";
    private static final String PREFS_NAME      = "defaults";
    //
//    private PreferenceCategory mSysInfoCategoryPreference = null;
    private Preference mProductPreference = null;
    private Preference mProductVerPreference = null;
    private Preference mMfgPreference = null;
    private Preference mSnPreference = null;
    private Preference mLoaderVerPreference = null;
    private Preference mRkStPreference = null;
    private Preference mSecureStPreference = null;
    private Preference mWifiMacPreference = null;
    private Preference mEthMacPreference = null;
    private Preference mSaioLibPreference = null;
    private Preference mUBootEnvPreference = null;
    //
    private Misc mMisc = null;
    
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        getPrefs(this);

        //Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_sysinfo);
        mMisc = new Misc();
        
//        mSysInfoCategoryPreference = (PreferenceCategory)findPreference(KEY_CATEGORY);
        //
        mProductPreference = (Preference) findPreference(KEY_PRODUCT);
        mProductVerPreference = (Preference) findPreference(KEY_PRODUCT_VER);
        mMfgPreference = (Preference) findPreference(KEY_MFG);
        mSnPreference = (Preference) findPreference(KEY_SN);
        mLoaderVerPreference = (Preference) findPreference(KEY_LOADER_VER);
        mRkStPreference = (Preference) findPreference(KEY_ROOTKEY_ST);
        mSecureStPreference = (Preference) findPreference(KEY_SECURE_ST);
        mWifiMacPreference = (Preference) findPreference(KEY_WIFI_MAC);
        mEthMacPreference = (Preference) findPreference(KEY_ETH_MAC);
        mSaioLibPreference = (Preference) findPreference(KEY_SAIO_LIB);
        mUBootEnvPreference = (Preference) findPreference(KEY_UBOOT_ENV);
        
        updateSysInfo();
    }
    
    static SharedPreferences getPrefs(Context context)
    {
        PreferenceManager.setDefaultValues(context, PREFS_NAME, MODE_PRIVATE, R.xml.pref_update, false);
//        return context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
    
    private void updateSysInfo()
    {
        byte[] tmpInfo = new byte[20];
        String tmp = null;
        int errno = 0;
        
        //Product
        errno = mMisc.getSystemInfo(Misc.INFO_PRODUCT, tmpInfo);
        tmp = new String(tmpInfo);
        if(0 == errno)
            mProductPreference.setSummary(tmp.substring(0, tmp.indexOf(0)));
        else
            mProductPreference.setSummary(getMiscErrString(errno));
        
        //Product ver.
        errno = mMisc.getSystemInfo(Misc.INFO_PRODUCT_VER, tmpInfo);
        tmp = new String(tmpInfo);
        if(0 == errno)
            mProductVerPreference.setSummary(tmp.substring(0, tmp.indexOf(0)));
        else
            mProductVerPreference.setSummary(getMiscErrString(errno));
        
        //MFG
        errno = mMisc.getSystemInfo(Misc.INFO_MANUFACTURE, tmpInfo);
        tmp = new String(tmpInfo);
        if(0 == errno)
            mMfgPreference.setSummary(tmp.substring(0, tmp.indexOf(0)));
        else
            mMfgPreference.setSummary(getMiscErrString(errno));
        
        //SN
        errno = mMisc.getSystemInfo(Misc.INFO_SERIAL_NUM, tmpInfo);
        tmp = new String(tmpInfo);
        if(0 == errno)
            mSnPreference.setSummary(tmp.substring(0, tmp.indexOf(0)));
        else
            mSnPreference.setSummary(getMiscErrString(errno));
        
        //Loader Ver.
        errno = mMisc.getSystemInfo(Misc.INFO_LOADER_VER, tmpInfo);
        tmp = new String(tmpInfo);
        if(0 == errno)
            mLoaderVerPreference.setSummary(tmp.substring(0, tmp.indexOf(0)));
        else
            mLoaderVerPreference.setSummary(getMiscErrString(errno));
        
        //Root key status
        errno = mMisc.getSystemInfo(Misc.INFO_ROOTKEY_STATUS, tmpInfo);
        tmp = new String(tmpInfo);
        if(0 == errno)
            mRkStPreference.setSummary(tmp.substring(0, tmp.indexOf(0)));
        else
            mRkStPreference.setSummary(getMiscErrString(errno));

        //Secure system status
        errno = mMisc.getSystemInfo(Misc.INFO_SECURE_STAT, tmpInfo);
        tmp = new String(tmpInfo);
        if(0 == errno)
            mSecureStPreference.setSummary(tmp.substring(0, tmp.indexOf(0)));
        else
            mSecureStPreference.setSummary(getMiscErrString(errno));
        
        //Wifi Mac
        String wifiMac = getWifiMacAddress();
        if(null != wifiMac)
            mWifiMacPreference.setSummary(wifiMac);
        
        //Ethernet Mac
        String ethMac = getEthMacAddress();
        if(null != ethMac)
            mEthMacPreference.setSummary(ethMac);
        
        byte[] info = new byte[256];
        Ver.getSaioVersion(this, Ver.LIB_SAIO_ALL, info);
        int len = info.length;
        for(int i=0; i<info.length; i++){
            if(info[i] == 0){
                len = i;
                break;
            }
        }
        String prodInfo = new String(info);
        prodInfo = prodInfo.substring(0, len);
        if(null != prodInfo)
            mSaioLibPreference.setSummary(prodInfo);

        info = new byte[16];
        if(Ver.getUBootEnvVersion(info) != Ver.ERR_NOT_FOUND) {
            for (int i = 0; i < info.length; i++) {
                if (info[i] == 0) {
                    len = i;
                    break;
                }
            }
            prodInfo = new String(info);
            prodInfo = prodInfo.substring(0, len);
            if (null != prodInfo)
                mUBootEnvPreference.setSummary(prodInfo);
        }else
            mUBootEnvPreference.setSummary("Not support");
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
    {
        if(null == preference.getKey())
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        
        updateSysInfo();
        
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
    
    private static String getMiscErrString(int errno)
    {
        StringBuffer sb = new StringBuffer();
        switch(errno)
        {
        case Misc.ERR_INVALID_PARAM:
            sb.append("The inputted parameters are not valid.");
            break;
        case Misc.ERR_NOT_SUPPORT:
            sb.append("The method is not supported for the given device.");
            break;
        case Misc.ERR_IO_FAIL:
            sb.append("Some unexpected internal error occurred.");
            break;
        case Misc.ERR_FETCH_LOG_FAIL:
            sb.append("Failed to copy the history log to user specified file.");
            break;
        case Misc.ERR_PWD_INCORRECT:
            sb.append("The password is incorrect.");
            break;
        case Misc.ERR_PWD_PROTECTED:
            sb.append("The password is under protected (refuse verification a while).");
            break;
        default:
            sb.append("Unknown.");
            break;
        }
        
        return sb.toString();
    }
    
    public String getEthMacAddress()
    {
        try
        {
            return _loadFileAsString("/sys/class/net/eth0/address").toUpperCase().substring(0, 17);
        }
        catch(Exception e)
        {
            Log.d(TAG, "Exception when getting ethernet mac address - " + e.getMessage());
            return null;
        }
    }
    
    public String getWifiMacAddress()
    {
        try
        {
            return _loadFileAsString("/sys/class/net/wlan0/address").toUpperCase().substring(0, 17);
        }
        catch(Exception e)
        {
            Log.d(TAG, "Exception when getting wifi mac address - " + e.getMessage());
            return null;
        }
    }
    
    private static String _loadFileAsString(String filePath) throws java.io.IOException
    {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1)
        {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }

}
