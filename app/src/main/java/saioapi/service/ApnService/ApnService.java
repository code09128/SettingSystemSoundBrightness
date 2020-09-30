package saioapi.service.ApnService;

import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;

/**
 * This class defines the Access Point Names APIs to be used by the applications.
 */
public class ApnService {
    //private static final String TAG = "ApnService";
    public static final String SETAPN = "ApnService.SET_APN";
    
    /**
     * This method is used to set APN and enable as default.
     * @param context user application context.
     * @param apnData the apn data to be written to settings. 
     */
    public static void setApn(Context context, ApnData apnData){
        Intent setIntent = new Intent();
        setIntent.setAction(SETAPN);
        setIntent.putExtra(Telephony.Carriers.NAME, apnData.getName());
        setIntent.putExtra(Telephony.Carriers.APN, apnData.getApn());
        setIntent.putExtra(Telephony.Carriers.PROXY, apnData.getProxy());
        setIntent.putExtra(Telephony.Carriers.PORT, apnData.getPort());
        setIntent.putExtra(Telephony.Carriers.USER, apnData.getUser());
        setIntent.putExtra(Telephony.Carriers.SERVER, apnData.getServer());
        setIntent.putExtra(Telephony.Carriers.PASSWORD, apnData.getPassword());
        setIntent.putExtra(Telephony.Carriers.MMSC, apnData.getMmsc());
        setIntent.putExtra(Telephony.Carriers.MCC, apnData.getMcc());
        setIntent.putExtra(Telephony.Carriers.MNC, apnData.getMnc());
        setIntent.putExtra(Telephony.Carriers.NUMERIC, apnData.getMcc()+apnData.getMnc());
        setIntent.putExtra(Telephony.Carriers.MMSPROXY, apnData.getMmsProxy());
        setIntent.putExtra(Telephony.Carriers.MMSPORT, apnData.getMmsPort());
        setIntent.putExtra(Telephony.Carriers.AUTH_TYPE, apnData.getAuthVal());
        setIntent.putExtra(Telephony.Carriers.TYPE, apnData.getType());
        setIntent.putExtra(Telephony.Carriers.PROTOCOL, apnData.getProtocol());
        setIntent.putExtra(Telephony.Carriers.BEARER, apnData.getBearer());
        setIntent.putExtra(Telephony.Carriers.ROAMING_PROTOCOL, apnData.getRoamingProtocol());
        setIntent.putExtra(Telephony.Carriers.MVNO_TYPE, apnData.getMvnoType());
        setIntent.putExtra(Telephony.Carriers.MVNO_MATCH_DATA, apnData.getMvnoMatchData());
        context.sendBroadcast(setIntent);
    }
}
