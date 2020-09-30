package saioapi;

/**
 * The listener that handle notifications when an event is triggered.
 */
public interface OnEventListener
{
    /**
     * Callback method would be invoked when an event is triggered.
     * Please implement UI behavior in UI thread, not in this method.
     * <p>
     * @param handle The service handle identifying the opened device.
     * @param event Indicates the event defined in service class constants.
     */
    void onEvent(int handle, int event);
}