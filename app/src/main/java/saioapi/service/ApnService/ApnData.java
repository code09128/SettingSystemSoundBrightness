package saioapi.service.ApnService;

/**
 * This class defines the Access Point Names data members and APIs.
 */
public class ApnData {
    
    //
    //  Constants (Events)
    //
    /** The id for apn authentication not set type. */
    public static final int AUTH_TYPE_NOT_SET = -1;
    /** The id for apn  none authentication type. */
    public static final int AUTH_TYPE_NONE = 0;
    /** The id for apn authentication pap type. */
    public static final int AUTH_TYPE_PAP = 1;
    /** The id for apn authentication chap type. */
    public static final int AUTH_TYPE_CHAP = 2;
    /** The id for apn authentication pap or chap type. */
    public static final int AUTH_TYPE_PAP_OR_CHAP = 3;
    
    /** The id for apn protocol IPv4 type. */
    public static final int PROTOCOL_TYPE_IPV4 = 0;
    /** The id for apn protocol IPv6 type. */
    public static final int PROTOCOL_TYPE_IPV6 = 1;
    /** The id for apn protocol IPv4 or IPv6 type. */
    public static final int PROTOCOL_TYPE_IPV4V6 = 2;
    
    /** The id for apn bearer LTE type. */
    public static final int BEARER_TYPE_LTE = 0;
    /** The id for apn bearer eHRPD type. */
    public static final int BEARER_TYPE_EHRPD = 1;
    /** The id for apn bearer unspecified type. */
    public static final int BEARER_TYPE_UNSPECIFIED = 2;
    
    /** The id for apn none mobile virtual network operator type. */
    public static final int MVNO_TYPE_NONE = 0;
    /** The id for apn mobile virtual network operator spn type. */
    public static final int MVNO_TYPE_SPN = 1;
    /** The id for apn mobile virtual network operator imsi type. */
    public static final int MVNO_TYPE_IMSI = 2;
    /** The id for apn mobile virtual network operator gid type. */
    public static final int MVNO_TYPE_GID = 3;
    
    private String mName = null;
    private String mApn = null;
    private String mProxy = null;
    private String mPort = null;
    private String mMmsProxy = null;
    private String mMmsPort = null;
    private String mUser = null;
    private String mServer = null;
    private String mPassword = null;
    private String mMmsc = null;
    private int mAuthVal = AUTH_TYPE_NOT_SET;
    private int mProtocol = PROTOCOL_TYPE_IPV4;
    private int mRoamingProtocol = PROTOCOL_TYPE_IPV4;
    private String mType = null;
    private String mMcc = null;
    private String mMnc = null;
    private int mBearer = BEARER_TYPE_UNSPECIFIED;
    private int mMvnoType = MVNO_TYPE_NONE;
    private String mMvnoMatchData = null;
    
    /**
     * Set the name of access point setting.
     * @param name access point setting name.
     */
    public void setName(String name){
        mName = name;
    }
    
    /**
     * Get access point setting name.
     * @return If successful, the access point setting name is returned, otherwise null will be returned.
     */
    public String getName(){
        return mName;
    }
    
    /**
     * Set the access point name.
     * @param apn access point name.
     */
    public void setApn(String apn){
        mApn = apn;
    }
    
    /**
     * Get access point name.
     * @return If successful, the access point name is returned, otherwise null will be returned.
     */
    public String getApn(){
        return mApn;
    }
    
    /**
     * Set the Http proxy.
     * @param proxy Http proxy.
     */
    public void setProxy(String proxy){
        mProxy = proxy;
    }

    /**
     * Get Http proxy.
     * @return If successful, the Http proxy is returned, otherwise null will be returned.
     */
    public String getProxy(){
        return mProxy;
    }
    
    /**
     * Set the Http proxy port.
     * @param port Http proxy port.
     */
    public void setPort(String port){
        mPort = port;
    }
    
    /**
     * Get Http proxy port.
     * @return If successful, the Http proxy port is returned, otherwise null will be returned.
     */
    public String getPort(){
        return mPort;
    }
    
    /**
     * Set the Mms proxy that will be used only for communicating with the MMS Gateway Server.
     * @param mmsProxy access point Mms proxy.
     */
    public void setMmsProxy(String mmsProxy){
        mMmsProxy = mmsProxy;
    }
    
    /**
     * Get access point Mms proxy.
     * @return If successful, the access point Mms proxy is returned, otherwise null will be returned.
     */
    public String getMmsProxy(){
        return mMmsProxy;
    }
    
    /**
     * Set the Mms proxy port of access point.
     * @param mmsPort access point Mms proxy port.
     */
    public void setMmsPort(String mmsPort){
        mMmsPort = mmsPort;
    }
    
    /**
     * Get access point Mms proxy port.
     * @return If successful, the access point Mms proxy port is returned, otherwise null will be returned.
     */
    public String getMmsPort(){
        return mMmsPort;
    }
    
    /**
     * Set the user name of access point.
     * @param user access point user name.
     */
    public void setUser(String user){
        mUser = user;
    }
    
    /**
     * Get access point user name.
     * @return If successful, the access point user name is returned, otherwise null will be returned.
     */
    public String getUser(){
        return mUser;
    }
    
    /**
     * Set the server address of access point.
     * @param server access point server address.
     */
    public void setServer(String server){
        mServer = server;
    }
    
    /**
     * Get access point server address.
     * @return If successful, the access point server address is returned, otherwise null will be returned.
     */
    public String getServer(){
        return mServer;
    }
    
    /**
     * Set the password of access point.
     * @param password access point password.
     */
    public void setPassword(String password){
        mPassword = password;
    }
    
    /**
     * Get access point password.
     * @return If successful, the access point password is returned, otherwise null will be returned.
     */
    public String getPassword(){
        return mPassword;
    }
    
    /**
     * Set the Mmsc address.
     * @param mmsc Mmsc address.
     */
    public void setMmsc(String mmsc){
        mMmsc = mmsc;
    }
    
    /**
     * Get Mmsc address.
     * @return If successful, the Mmsc address is returned, otherwise null will be returned.
     */
    public String getMmsc(){
        return mMmsc;
    }
    
    /**
     * Set the method that your device may use to supply your username and password to the server for your PPP (Point-to-Point Protocol) connection.
     * @param authVal authentication type.
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>Authentication Type</th></tr></thead>
     *   <tbody>
     *     <tr><td>AUTH_TYPE_NOT_SET</td><td>Not set</td></tr>
     *     <tr><td>AUTH_TYPE_NONE</td><td>None</td></tr>
     *     <tr><td>AUTH_TYPE_PAP</td><td>Password authentication protocol</td></tr>
     *     <tr><td>AUTH_TYPE_CHAP</td><td>Challenge Handshake Authentication Protocol</td></tr>
     *     <tr><td>AUTH_TYPE_PAP_OR_CHAP</td><td>PAP or CHAP</td></tr>
     *   </tbody>
     * </table>
     */
    public void setAuthVal(int authVal){
        if(authVal > AUTH_TYPE_PAP_OR_CHAP)
            return;
        mAuthVal = authVal;
    }
    
    /**
     * Get authentication type.
     * @return If successful, the authentication type is returned.
     */
    public int getAuthVal(){
        return mAuthVal;
    }
    
    /**
     * Set the protocol type of access point setting.
     * @param protocol protocol type.
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>Protocol Type</th></tr></thead>
     *   <tbody>
     *     <tr><td>PROTOCOL_TYPE_IPV4</td><td>Internet Protocol Version 4</td></tr>
     *     <tr><td>PROTOCOL_TYPE_IPV6</td><td>Internet Protocol Version 6</td></tr>
     *     <tr><td>PROTOCOL_TYPE_IPV4V6</td><td>IPv4 or IPv6</td></tr>
     *   </tbody>
     * </table>
     */
    public void setProtocol(int protocol){
        if(protocol > PROTOCOL_TYPE_IPV4V6)
            return;
        mProtocol = protocol;
    }
    
    /**
     * Get protocol type.
     * @return If successful, the protocol type is returned.
     */
    public int getProtocol(){
        return mProtocol;
    }
    
    /**
     * Set the roaming protocol type of access point setting.
     * @param roamingProtocol protocol type.
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>Protocol Type</th></tr></thead>
     *   <tbody>
     *     <tr><td>PROTOCOL_TYPE_IPV4</td><td>Internet Protocol Version 4</td></tr>
     *     <tr><td>PROTOCOL_TYPE_IPV6</td><td>Internet Protocol Version 6</td></tr>
     *     <tr><td>PROTOCOL_TYPE_IPV4V6</td><td>IPv4 or IPv6</td></tr>
     *   </tbody>
     * </table>
     */
    public void setRoamingProtocol(int roamingProtocol){
        if(roamingProtocol > PROTOCOL_TYPE_IPV4V6)
            return;
        mRoamingProtocol = roamingProtocol;
    }
    
    /**
     * Get roaming protocol type.
     * @return If successful, the protocol type is returned.
     */
    public int getRoamingProtocol(){
        return mRoamingProtocol;
    }
    
    /**
     * Set access point type.
     * @param type access point type.
     */
    public void setType(String type){
        mType = type;
    }
    
    /**
     * Get access point type.
     * @return If successful, the access point type is returned, otherwise null will be returned.
     */
    public String getType(){
        return mType;
    }
    
    /**
     * Set mobile country code.
     * @param mcc mobile country code.
     */
    public void setMcc(String mcc){
        mMcc = mcc;
    }
    
    /**
     * Get mobile country code.
     * @return If successful, the mobile country code is returned, otherwise null will be returned.
     */
    public String getMcc(){
        return mMcc;
    }
    
    /**
     * Set mobile network code.
     * @param mnc mobile network code.
     */
    public void setMnc(String mnc){
        mMnc = mnc;
    }
    
    /**
     * Get mobile network code.
     * @return If successful, the mobile network code is returned, otherwise null will be returned.
     */
    public String getMnc(){
        return mMnc;
    }
    
    /**
     * Set the bearer type of access point setting.
     * @param bearer bearer type.
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>Bearer Type</th></tr></thead>
     *   <tbody>
     *     <tr><td>BEARER_TYPE_LTE</td><td>Long Term Evolution</td></tr>
     *     <tr><td>BEARER_TYPE_EHRPD</td><td>Enhanced High Rate Packet Data</td></tr>
     *     <tr><td>BEARER_TYPE_UNSPECIFIED</td><td>Unspecified</td></tr>
     *   </tbody>
     * </table>
     */
    public void setBearer(int bearer){
        if(bearer > BEARER_TYPE_UNSPECIFIED)
            return;
        mBearer = bearer;
    }
    
    /**
     * Get bearer type.
     * @return If successful, the bearer type is returned.
     */
    public int getBearer(){
        return mBearer;
    }
    
    /**
     * Set the mobile virtual network operator type of access point setting.
     * @param mvnoType mvno type.
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>MVNO Type</th></tr></thead>
     *   <tbody>
     *     <tr><td>MVNO_TYPE_NONE</td><td>None</td></tr>
     *     <tr><td>MVNO_TYPE_SPN</td><td>Service Provider Name</td></tr>
     *     <tr><td>MVNO_TYPE_IMSI</td><td>International Mobile Subscriber Identity</td></tr>
     *     <tr><td>MVNO_TYPE_GID</td><td>Group Identifier</td></tr>
     *   </tbody>
     * </table>
     */
    public void setMvnoType(int mvnoType){
        if(mvnoType > MVNO_TYPE_GID)
            return;
        mMvnoType = mvnoType;
    }
    
    /**
     * Get mobile virtual network operator type.
     * @return If successful, the mvno type is returned.
     */
    public int getMvnoType(){
        return mMvnoType;
    }
    
    /**
     * Set Mvno match data Info.
     * @param mvnoMatchData Mvno match data Info.
     */
    public void setMvnoMatchData(String mvnoMatchData){
        mMvnoMatchData = mvnoMatchData;
    }
    
    /**
     * Get Mvno match data Info.
     * @return If successful, the Mvno match data is returned, otherwise null will be returned.
     */
    public String getMvnoMatchData(){
        return mMvnoMatchData;
    }
}
