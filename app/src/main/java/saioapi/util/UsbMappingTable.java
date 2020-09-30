package saioapi.util;

import com.xac.sysmanager.UsbAPI;

import saioapi.base.Misc;

/**
 * Created by dennis_wang on 2015/10/13.
 */
public class UsbMappingTable {
    private static String mProdInfo;
    private static int mHubNum = 0;

    private static int[][] mUsbTable_T3_DVT = {
            {4, 3},{3, 1},{3, 2},{3, 3},{3, 4},{2, 4}, //Port 1~6
            {5, 2}, //PPOS
            {0, 0},{0, 0},{0, 0},
    };

    private static int[][] mUsbTable_T3 = {
            {6, 3},{5, 1},{5, 2},{5, 3},{5, 4},{4, 4}, //Port 1~6
            {6, 2}, //PPOS
            {0, 0},{0, 0},{0, 0},
    };


    public static int getDeviceNum(int usbIdx){
        if(mProdInfo == null)
            getProdInfo();

        if(mProdInfo.contains("T3")) {
            if(getHubNum() == 3)
                return mUsbTable_T3_DVT[usbIdx][0];
            else
                return mUsbTable_T3[usbIdx][0];
        }else
            return -1;
    }

    public static int getPortNum(int usbIdx){
        if(mProdInfo.contains("T3"))
            if(getHubNum() == 3)
                return mUsbTable_T3_DVT[usbIdx][1];
            else
                return mUsbTable_T3[usbIdx][1];
        else
            return -1;
    }

    private static int getHubNum(){
        if(mHubNum == 0){
            int[] num = UsbAPI.getDeviceNum();
            mHubNum = num.length;
        }
        return mHubNum;
    }

    private static void getProdInfo(){
        Misc mMisc = new Misc();
        byte[] info = new byte[20];
        mMisc.getSystemInfo(Misc.INFO_PRODUCT, info);
        int len = info.length;
        for(int i=0; i<info.length; i++){
            if(info[i] == 0){
                len = i;
                break;
            }
        }
        mProdInfo = new String(info);
        mProdInfo = mProdInfo.substring(0, len);
    }
}
