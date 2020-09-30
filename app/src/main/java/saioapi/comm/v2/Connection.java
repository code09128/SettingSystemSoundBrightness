package saioapi.comm.v2;

import android.util.Log;

import java.util.Arrays;
import java.util.Locale;

abstract class Connection {
    private final String TAG = "SaioComManager-Connection";

    private final int MAX_PACKET = 1024;
    private final int MAX_READ_PACKET = 1024;
    private final int MAX_RETRIES = 3;
    private int mProtocol;
    byte[] Receiveddata = new byte[MAX_PACKET];
    int Receivedlength = 0;

    private byte[] mFullPacket = null;
    private int mFullPacketLength = 0;
    private int mVngstart = -1;
    private int mRetries;
    private byte[] mVngCmd;

    ////////////////////////////////////////////////////////////////////////////////////////////////
    abstract int open(int dev);
    abstract int[] getUsbDevId(int vid, int pid);
    abstract int close();
    protected abstract int bulkWrite(byte[] data, int timeout);
    protected abstract int bulkRead(byte[] data, int dataLength, int timeout);
    abstract void listener(int event);
    abstract boolean isOpened();
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private byte[] formatMessage(byte[] request) {
        byte lrc = 0;
        int i;

        byte[] bCmd = new byte[request.length + 3];

        // Add STX
        bCmd[0] = VNG.STX;

        // Add Command
        System.arraycopy(request, 0, bCmd, 1, request.length);

        // Add ETX
        bCmd[request.length + 1] = VNG.ETX;

        // Add LRC
        for (i = 1; i < request.length + 2; i++) {
            lrc = (byte) (lrc ^ bCmd[i]);
        }
        bCmd[i] = lrc;

        return bCmd;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean validateLrc(byte[] response, int length) {
        byte lrc = 0;

        for (int i = 1; i < length - 1; i++) {
            lrc = (byte) (lrc ^ response[i]);
        }

        return (lrc == response[length - 1]);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    public byte[] concat(byte[] a, int aLen, byte[] b, int bLen) {
        byte[] c = new byte[aLen + bLen];
        if (a != null) {
            System.arraycopy(a, 0, c, 0, aLen);
        }
        if (b != null) {
            System.arraycopy(b, 0, c, aLen, bLen);
        }
        return c;
    }

    public int connect(int protocol) {
        mProtocol= protocol;
        return 0;
    }
    public int getProtocol() {
        return mProtocol;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public int VNG_write(byte[] request, int len, final int timeout) {
        int writeLen = ComManager.ERR_OPERATION;
        int retries;
        //byte[] bCmd;
        byte[] Cmd;

        if (isOpened() == false)
            return ComManager.ERR_NOT_OPEN;
        Log.i("BalderTest","VNG_write="+byte2HexStr(request,request.length));
        try {
            Cmd = Arrays.copyOf(request,len);
            mVngCmd = formatMessage(Cmd);
            // Attempt to send command to the device
            // Only attempt retries if we are receiving NAKs
            for (retries = 0; retries < MAX_RETRIES; retries++) {
                writeLen = bulkWrite(mVngCmd, timeout);
                if (writeLen > 0) {
                    //Send command succeeded
                    break;
                }
                else {
                    //Send command failed
                    mVngCmd = null;
                    return ComManager.ERR_NOT_READY;
                }
            }

            // Send EOT if maximum retries was reached
            if (retries == MAX_RETRIES) {
                bulkWrite(new byte[]{VNG.EOT}, timeout);
                mVngCmd = null;
                return ComManager.ERR_NOT_READY;
            }
            return request.length;
        }
        catch (Exception e) {
            Log.d(TAG, "usbSendAndGetResponse Exception");
            mVngCmd = null;
            e.printStackTrace();
        }
        return ComManager.ERR_NOT_READY;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public int RAW_DATA_write(byte[] request, int len, final int timeout) {
        int writeLen=ComManager.ERR_OPERATION;;
        byte[] bCmd;

        if (isOpened() == false)
            return ComManager.ERR_NOT_OPEN;

        try {
            bCmd = Arrays.copyOf(request,len);;
            writeLen = bulkWrite(bCmd, timeout);
            if (writeLen > 0) {
                return writeLen;
            }
            else {
                return ComManager.ERR_NOT_READY;
            }
        }
        catch (Exception e) {
            Log.d(TAG, "usbSendAndGetResponse Exception");
            e.printStackTrace();
        }
        return ComManager.ERR_NOT_READY;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    public int VNG_read (final int timeout) {
        byte[] response = null;
        byte[] fullresponse = new byte[MAX_READ_PACKET];
        int readLen;
        int writeLen;

        readLen = bulkRead(fullresponse, MAX_READ_PACKET, timeout);
        Log.i("BalderTest","RX  mFullPacket  byte2HexStr="+byte2HexStr(fullresponse,fullresponse.length));
        Log.i("BalderTest","readLen"+readLen+" mFullPacketLength="+mFullPacketLength+" mVngstart="+mVngstart);
        if (readLen != ComManager.ERR_NO_CONNECTED) {
            if (readLen > 0) {
                if (mFullPacketLength == 0) {
                    if ((fullresponse[0] == VNG.EOT || fullresponse[0] == VNG.ACK || fullresponse[0] == VNG.DLE)
                            && readLen == 1) {
                        return ComManager.ERR_NOT_READY;
                    }else if(fullresponse[0] == VNG.NAK && readLen == 1){
                        mRetries ++;
                        // Send EOT if maximum retries was reached
                        if (mRetries == MAX_RETRIES) {
                            bulkWrite(new byte[]{VNG.EOT}, timeout);
                            mVngCmd = null;
                            return ComManager.ERR_NOT_READY;
                        }
                        writeLen = bulkWrite(mVngCmd, timeout);
                        if (writeLen < 0) {
                            mVngCmd = null;
                            return ComManager.ERR_NOT_READY;
                        }
                    }
                    mVngstart = -1;
                    int i;
                    for (i = 0; i < fullresponse.length; i++) {
                        if (fullresponse[i] == VNG.STX && mVngstart == -1) {
                            mVngstart = i;
                            readLen = readLen - i;
                        }
                    }
                    if(mVngstart == -1){
                        return 0;
                    }else{
                        response = new byte[readLen];
                        if(fullresponse != null) {
                            System.arraycopy(fullresponse, mVngstart, response, 0, readLen);
                            readLen = response.length;
                        }
                    }
                }
                mRetries = 0;
                // Append response to full packet
                if(mFullPacketLength == 0) {
                    mFullPacket = concat(mFullPacket, mFullPacketLength, response, readLen);
                }else{
                    mFullPacket = concat(mFullPacket, mFullPacketLength, fullresponse, readLen);
                }
                mFullPacketLength = mFullPacket.length;
                Log.i("BalderTest","mFullPacket  byte2HexStr="+byte2HexStr(mFullPacket,mFullPacketLength));
                // Determine if full message was processed
                boolean fullPacketReceived = false;
                int i;
                for (i = 0; i < mFullPacketLength; i++) {
                    if (mFullPacket[i] == VNG.ETX) {
                        // Make sure we have the LRC in the next position
                        if (i == mFullPacketLength - 2) {
                            fullPacketReceived = true;
                        }
                    } else if (mFullPacket[i] == VNG.RS) {
                        // Check if we have enough data to read the tag payload length
                        if (i < mFullPacketLength - 2) {
                            int payloadLength = ((int) mFullPacket[i + 1] & 0xFF) << 8;
                            payloadLength += (int) mFullPacket[i + 2] & 0xFF;
                            payloadLength += i + 5; // Processed message length + RS, LEN1, LEN2, ETX, LRC

                            if (payloadLength <= mFullPacketLength) {
                                fullPacketReceived = true;
                            }
                        }
                    }
                }
                if (fullPacketReceived) {
                    // Process entirety of message now that it is all delivered
                    if (validateLrc(mFullPacket, mFullPacketLength)) {
                        writeLen = bulkWrite(new byte[]{VNG.ACK}, timeout);
                        if (writeLen > 0) {
                            // Return the actual response, not the entire byte array used to read the response
                            byte[] saturnResp = new byte[mFullPacketLength - 3];
                            System.arraycopy(mFullPacket, 1, saturnResp, 0, mFullPacketLength - 3);
                            System.arraycopy(saturnResp, 0, Receiveddata, 0, saturnResp.length);
                            Receivedlength = saturnResp.length;
                            listener(ComManager.EVENT_DATA_READY);
                            mFullPacket = null;
                            mFullPacketLength = 0;
                            mVngstart = -1;
                        }
                    } else {
                        bulkWrite(new byte[]{VNG.NAK}, timeout);
                        mFullPacket = null;
                        mFullPacketLength =0;
                        mVngstart = -1;
                    }
                }
            }
            return 0;

        }else{
            listener(ComManager.EVENT_DISCONNECT);
            return ComManager.ERR_NO_CONNECTED;
        }
    }
    private final static char[] mChars = "0123456789ABCDEF".toCharArray();
    public static String byte2HexStr(byte[] b, int iLen){
        StringBuilder sb = new StringBuilder();
        for (int n=0; n<iLen; n++){
            sb.append(mChars[(b[n] & 0xFF) >> 4]);
            sb.append(mChars[b[n] & 0x0F]);
            //sb.append(' ');
        }
        return sb.toString().trim().toUpperCase(Locale.US);
    }
    public int RAW_DATA_read (final int timeout) {
        byte[] response = new byte[MAX_READ_PACKET];
        int readLen;

        readLen = bulkRead(response, MAX_READ_PACKET, timeout);
        if (readLen != ComManager.ERR_NO_CONNECTED) {
            if (readLen > 0) {
                System.arraycopy(response, 0, Receiveddata, 0, readLen);
                Receivedlength = readLen;
                listener(ComManager.EVENT_DATA_READY);
            }
            return 0;
        }else{
            listener(ComManager.EVENT_DISCONNECT);
            return ComManager.ERR_NO_CONNECTED;
        }
    }

    public int read (byte[] data,int len, int timout){
        if (Receivedlength > data.length || Receivedlength <= 0){
            return ComManager.ERR_OPERATION;
        }else{
            System.arraycopy(Receiveddata, 0, data, 0, Receivedlength);
            Arrays.fill(Receiveddata, (byte) 0);
            return Receivedlength;
        }
    }
}

