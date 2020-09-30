package saioapi.base.v2;

interface PrintInterface
{
    /**
     * The method get called when the class received a notification event of the 
     * printer and the register method has been enabled.
     * @param handle The service handle identifying the opened printer device.
     * @param event Indicates the event defined in class constants.
     */
    public void listener(int handle, int event);
    
}
