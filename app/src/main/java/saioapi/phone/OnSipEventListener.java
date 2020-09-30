package saioapi.phone;

public interface OnSipEventListener {
    /**
     * Callback method would be invoked when an event is triggered.
     * Please implement UI behavior in UI thread, not in this method.
     * <p>
     * @param event Indicates the event defined in service class constants.
     * @param data return data.
     */
    void onEvent(int event, String data);
}
