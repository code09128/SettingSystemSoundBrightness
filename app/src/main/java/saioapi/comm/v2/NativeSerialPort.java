package saioapi.comm.v2;

class NativeSerialPort {

    final String TAG = "SaioComManager-NativeSerialPort";
    private OnComEventListener mOnComEventListener = null;
    int handle = ComManager.ERR_OPERATION;

    NativeSerialPort(OnComEventListener listener)
    {
        mOnComEventListener = listener;
    }

    int open(int dev) {
        handle = openSerialPort((short) dev);
        if (handle == ComManager.ERR_OPERATION ){
            return handle;
        }else{
            return 0;
        }
    }

    int close() {
        int result=0;
        if(handle != ComManager.ERR_OPERATION) {
            cancelSerialPort(handle);
            result=closeSerialPort(handle);
            handle = ComManager.ERR_OPERATION;
            return result;
        }else{
            return ComManager.ERR_NOT_OPEN;
        }
    }

    int connect(int baud, int data_size, int stop_bit, int parity, int flow_control, int protocol, byte[] extra) {
        return connectSerialPort(handle, baud, data_size, stop_bit, parity, flow_control, protocol, extra);
    }

    int write(byte[] data, int len, int timeout) {
        return writeSerialPort(handle, data, len, timeout);
    }

    int read(byte[] data, int len, int timeout) {
        return readSerialPort(handle, data, len, timeout);
    }

    int lastError() {
        return lastErrorSerialPort();
    }

    int status() {
        return statusSerialPort(handle);
    }

    int getCts() {
        return getCtsSerialPort(handle);
    }

    int setRts(int rts) {
        return setRtsSerialPort(handle, rts);
    }

    private native int openSerialPort(short dev);
    private native int closeSerialPort(int handle);
    private native int cancelSerialPort(int handle);
    private native int connectSerialPort(int handle,int baud, int data_size, int stop_bit, int parity, int flow_control, int protocol, byte[] extra);
    private native int writeSerialPort(int handle,byte[] data,int len, int timeout);
    private native int readSerialPort(int handle,byte[] data,int len, int timeout);
    private native int lastErrorSerialPort();
    private native int statusSerialPort(int handle);
    private native int getCtsSerialPort(int handle);
    private native int setRtsSerialPort(int handle,int rts);

    private void listener(int handle, int event)
    {
        //
        //  Call your real function to handle event here
        //
        if (null != mOnComEventListener)
        {
            mOnComEventListener.onEvent(event);
        }
    }
}
