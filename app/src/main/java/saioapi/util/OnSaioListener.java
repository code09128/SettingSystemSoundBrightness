package saioapi.util;

public interface OnSaioListener {
    /**
     * Callback method would be invoked when received brightness.
     * Please implement UI behavior in UI thread, not in this method.
     * <p>
     * @param brightness Brightness of 2nd display.
     */
    void onBrightness(float brightness);
    
    /**
     * Callback method would be invoked when received touch id.
     * Please implement UI behavior in UI thread, not in this method.
     * <p>
     * @param touchId Touch id of 2nd touch panel.
     */
    void onTouchId(int touchId);
}
