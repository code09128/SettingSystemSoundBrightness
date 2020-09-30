package saioapi.cmd;

/**
 * The listener that handle notifications when an event is triggered.
 */
public interface OnCommandListener
{
    /**
     * Callback method would be invoked when an event is triggered.
     * Please implement UI behavior in UI thread, not in this method.
     * <p>
     * @param Command Indicates the command of VNG response.
     * @param length Indicates the data length.
     * @param data Indicates the data array of VNG response. 
     */
    void onData(int Command, int length, byte[] data);
}
