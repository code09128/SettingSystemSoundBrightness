//
//  Saio API, Card class
//
package saioapi.reader;

import saioapi.OnEventListener;

/**
 * This class is deprecated since version 170308_r1.
 * This class defines the card transporter methods. That may typically be implemented for a 
 * smart card reader or magnetic stripe card reader automated with motors or solenoids for 
 * card insertion, swallowing, reading, and ejection.
 */
public class Card {
    static
    {
        System.loadLibrary("SaioBase");
        System.loadLibrary("SaioReader");
    }
    
    //
    //  Constants (Error code and Events)
    //
    /** Indicates to get detail error code by using {@link #lastError} method. */
    public static final int ERR_OPERATION       = 0xFFFFFFFF;
    
    /** The device is no longer available. The device is in use by another. */
    public static final int ERR_NOT_READY       = 0x0000E000;
    
    /** The device does not exist. */
    public static final int ERR_NOT_EXIST       = 0x0000E002;
    
    /** The card transporter device has not been opened. */
    public static final int ERR_NOT_OPEN        = 0x0000E004;
    
    /** The inputted parameters are not valid. */
    public static final int ERR_INVALID_PARAM   = 0x0000E006;
    
    /** Unable to get method reference of the class listener. */
    public static final int ERR_NO_LISTENER     = 0x0000E008;  
    
    /** Signals the card transporter status changed. */
    public static final int EVENT_STATUS_CHG    = 0x00000001;
    
    /**
     * The listener that receives notifications when an Card event is triggered.
     */
    private OnEventListener mOnEventListener = null;
    
    //
    //    Methods
    //
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * Get the registered Listener that handle the Card event.
     * @return The callback to be invoked with a Card event is triggered, 
     *         or null id no callback has been set.
     */
    public final OnEventListener getOnEventListener()
    {
        return mOnEventListener;
    }
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * Register a callback to be invoked when a Card event is triggered.
     * @param listener The callback that will be invoked.
     */
    public void setOnEventListener(OnEventListener listener)
    {
        mOnEventListener = listener;
    }
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method open a card transporter service exclusively.
     * @return Return zero if the function succeeds else nonzero error code defined in 
     *          class constants.
     */
    public native int open();
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method tries to close an opened card transporter service.
     * @return Return zero if the function succeeds else nonzero error code defined in 
     *          class constants.
     */
    public native int close();
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method enables or disables the card acceptance by the card transporter 
     * service.
     * @param enable Indicates to enable (TRUE) or disable (FALSE) the card transporter.
     * @return Return zero if the function succeeds else nonzero error code defined in 
     *          class constants.
     */
    public native int acceptance(boolean enable);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * The method enable or disable the card locking mechanism, a card is automatically 
     * locked when inserted and cannot be removed from the device until it is unlocked.
     * @param enable Indicates to enable (TRUE) or disable (FALSE) the card transporter.
     * @return Return zero if the function succeeds else nonzero error code defined in 
     *          class constants.
     */
    public native int lock(boolean enable);
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method results in the card ejection. In case the card is locked it will 
     * unlock before the ejection.
     * @return Return zero if the function succeeds else nonzero error code defined in 
     *          class constants.
     */
    public native int eject();
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * Retrieves the last error occurs on the card transporter service.
     * @return Return zero if there is no error else nonzero error code defined in 
     *          class constants.
     */
    public native int lastError();
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * This method retrieves the card status. The following table shows predefined 
     * state bits and their interpretations.<br />
     * <table border=1>
     *   <thead><tr><th>Class Constants</th><th>IC Card slot and SAM service</th></tr></thead>
     *   <tbody>
     *     <tr><td>BIT</td><td>Status</td><td>BIT=0</td><td>BIT=1</td></tr>
     *     <tr><td>0</td><td>Card acceptance is enabled</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>1</td><td>Card locking is enabled</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>2</td><td>Card is being inserted</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>3</td><td>Card has been inserted</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>4</td><td>Card is jammed</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>5</td><td>Card is being removed</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>6</td><td>No Card</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>7</td><td>Card Transporter device error</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>8</td><td>Card Transported device is on-line</td><td>No</td><td>Yes</td></tr>
     *     <tr><td>9-31</td><td>Unused</td><td></td><td></td></tr>
     *   </tbody>
     * </table>
     * @return If successful, the card transporter status is returned, otherwise a 
     *          {@link #ERR_OPERATION} is returned and the method {@link #lastError} 
     *          can be used to indicate the error.
     */
    public native int status();
    
    /**
     * @deprecated This method is deprecated since version 170308_r1.
     * The method get called when the class received a notification event of the 
     * card transporter and the above register method has been enabled.
     * @param event Indicates the event defined in class constants.
     */
    public void listener(int event)
    {
        // 
        //  Call your real function to handle event here
        //
        if (null != mOnEventListener)
        {
            mOnEventListener.onEvent(0, event);
        }
    }
}
