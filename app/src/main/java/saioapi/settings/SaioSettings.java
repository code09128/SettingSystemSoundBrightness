package saioapi.settings;

import android.content.Context;
import android.content.Intent;

/**
 * This class defines the Settings constant definition to be used by the applications.
 */
public class SaioSettings {
    //
    //  Constants
    //
    public static final String PACKAGE_NAME = "android.settings.CUSTOMIZEDSETTINGS";
    
    public static final String DATA_KEY = "android.customizedsettings.list";
    
    public static final String ACTION_ETHERNET_SETTINGS = "android.settings.ETHERNET_SETTINGS";

    private static final String ETHERNET_ACTION = "android.intent.action.UPDATE_ETHERNET_CONFIGURATIONS";

    private static final String ETHERNET_TYPE_KEY = "com.xac.ethernet.type";
    private static final String ETHERNET_IP_KEY = "com.xac.ethernet.ip";
    private static final String ETHERNET_NETMASK_KEY = "com.xac.ethernet.netmask";
    private static final String ETHERNET_DNS1_KEY = "com.xac.ethernet.dns1";
    private static final String ETHERNET_DNS2_KEY = "com.xac.ethernet.dns2";
    private static final String ETHERNET_GATEWAY_KEY = "com.xac.ethernet.gateway";
    private static final String ETHERNET_PROXY_KEY = "com.xac.ethernet.proxy";
    private static final String ETHERNET_PORT_KEY = "com.xac.ethernet.port";
    private static final String ETHERNET_BYPASS_KEY = "com.xac.ethernet.bypass";


    public static void setEthConfig(Context context, SaioEthConfig config){
        Intent intent = new Intent();
        intent.setAction(ETHERNET_ACTION);
        intent.putExtra(ETHERNET_TYPE_KEY, config.getType());
        intent.putExtra(ETHERNET_IP_KEY, config.getIP());
        intent.putExtra(ETHERNET_NETMASK_KEY, config.getNetMask());
        intent.putExtra(ETHERNET_DNS1_KEY, config.getDNS());
        intent.putExtra(ETHERNET_DNS2_KEY, config.getDNS2());
        intent.putExtra(ETHERNET_GATEWAY_KEY, config.getGateway());
        intent.putExtra(ETHERNET_PROXY_KEY, config.getProxy());
        intent.putExtra(ETHERNET_PORT_KEY, config.getProxyPort());
        intent.putExtra(ETHERNET_BYPASS_KEY, config.getBypass());
        context.sendBroadcast(intent);
    }

}
