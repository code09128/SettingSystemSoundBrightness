package saioapi.comm.v2;

/**
 * The listener that handle notifications when an event is triggered.
 */
public interface OnComEventListener
{
    /**
     * Callback method would be invoked when an event is triggered.
     * Please implement UI behavior in UI thread, not in this method.
     * <p>
     * @param event Indicates the event defined in service class constants.
     */
    void onEvent(int event);
}
