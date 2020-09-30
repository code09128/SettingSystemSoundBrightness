package saioapi.util;

/**
 * Created by pjchang on 2018/4/12.
 */
public class CanBus {
    static
    {
        System.loadLibrary("SaioUtil");
    }
    /** The id for communication device at CAN0. */
    public static final int DEVICE_CAN0          = 0x00000000;

    /**
     * This method setup the bus bitrate and starts the can interface with the given device id.
     * It simply changes the if state of the interface to up.
     * @param dev_id  Logical ID of the selected can interface.
     *
     * @return Return zero if the function succeeds. Otherwise a nonzero if failed.
     */
    public int open(int dev_id,int bitrate){
        return canOpen(dev_id, bitrate);
    }

    /**
     * This method stops the can interface with the given device id. It simply changes the if
     * state of the interface to down. Any running communication would be stopped.
     *
     * @return Return zero if the function succeeds. Otherwise a nonzero if failed.
     */
    public int close(){
        return canClose();
    }

    /**
     * This method is used to send the given can frame.(Need to setCanId/setCanDlc/getCanData first)
     * @param frame  The CAN frame to be written.
     *
     * @return Return zero if the function succeeds. Otherwise a nonzero if failed.
     */
    public int send(CanFrame frame){
        return canSend(frame);
    }

    /**
     * This method is used to receive the Can frame,
     *
     * @return  The can frame was received.
     */
    public CanFrame receive(){
        return canRecv();
    }

    public final static class CanFrame {
        private int canId;
        private int canDlc;
        private byte[] canData;

        /**
         * New a can Frame..
         */
        public CanFrame() {}

        /**
         * Retrieves the can frame's id.
         *
         * @return The can frame's id
         */
        public int getCanId() {
            return canId;
        }

        /**
         * Retrieves the can frame's data length code.
         *
         * @return The can frame's data length code.
         */
        public int getCanDlc() {
            return canDlc;
        }

        /**
         * Retrieves the can frame's data.
         *
         * @return The can frame's data.
         */
        public byte[] getCanData() {
            return canData;
        }

        /**
         * Set can frame id.
         */
        public void setCanId(int canid) {
            this.canId = canid;
        }

        /**
         * Set can frame data length code.
         */
        public void setCanDlc(int candlc) {
            this.canDlc= candlc;
        }

        /**
         * Set can frame data.
         */
        public void setCanData(byte[] candata) {
            this.canData = candata;
        }
    }

    private native int canOpen(int dev,int bitrate);
    private native int canSend(CanFrame frame);
    private native CanFrame canRecv();
    private native int canClose();
}



