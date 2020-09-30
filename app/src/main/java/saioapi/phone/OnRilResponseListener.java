package saioapi.phone;

public interface OnRilResponseListener {
    /**
     * Callback method would be invoked when an event is triggered.
     * Please implement UI behavior in UI thread, not in this method.
     * <p>
     * @param response Indicates the response defined in service class constants.
     */
    void onResponse(String[] response);
}
