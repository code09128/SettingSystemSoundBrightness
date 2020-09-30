package saioapi.settings;


/**
 * Created by dennis_wang on 2015/6/29.
 */
public class SaioEthConfig {
    public static final String DHCP = "dhcp";
    public static final String STATIC_IP = "static_ip";
    private String mType;
    private String mIP;
    private String mNetMask;
    private String mDNS;
    private String mDNS2;
    private String mGateway;
    private String mProxy;
    private String mProxyPort;
    private String mBypass;

    public void setType(String type){
        mType = type;
    }

    public String getType(){
        return mType;
    }

    public void setIP(String ip){
        mIP = ip;
    }

    public String getIP(){
        return mIP;
    }

    public void setNetMask(String netMask){
        mNetMask = netMask;
    }

    public String getNetMask(){
        return mNetMask;
    }

    public void setDNS(String dns){
        mDNS = dns;
    }

    public String getDNS(){
        return mDNS;
    }

    public void setDNS2(String dns){
        mDNS2 = dns;
    }

    public String getDNS2(){
        return mDNS2;
    }

    public void setGateway(String gateway){
        mGateway = gateway;
    }

    public String getGateway(){
        return mGateway;
    }

    public void setProxy(String proxy){
        mProxy = proxy;
    }

    public String getProxy(){
        return mProxy;
    }

    public void setProxyPort(String port){
        mProxyPort = port;
    }

    public String getProxyPort(){
        return mProxyPort;
    }

    public void setBypass(String bypass){
        mBypass = bypass;
    }

    public String getBypass(){
        return mBypass;
    }

}
