package saioapi.comm.v2;


public class VNG {
    /* Control Symbol */
    /** Start of Text Symbol to mark the start of the Transaction Message/Response. */
    public static final byte STX = (byte)0x02;

    /** End of Text symbol to mark the end of the Transaction Message/Response. */
    public static final byte ETX = (byte)0x03;

    /** Shift In Symbol to mark the start of the Transaction Message/Response. */
    public static final byte SI = (byte)0x0F;

    /** Shift Out symbol to mark the end of the Transaction Message/Response. */
    public static final byte SO = (byte)0x0E;

    /** Acknowledge symbol informs the Message/Response was received correctly. */
    public static final byte ACK = (byte)0x06;

    /** Not Acknowledge symbol if the previous Transaction Message/Response was not received correctly. */
    public static final byte NAK = (byte)0x15;

    /** Field Separator is used to separate the various data segments within the Message/ Response. */
    public static final byte FS = (byte)0x1C;

    /** Clear Screen Symbol is used to clear LCD. */
    public static final byte SUB = (byte)0x1A;

    /** Record Separator is used to separate the binary data segments within the Message/Response. */
    public static final byte RS = (byte)0x1E;

    /** End-of-Transmission symbol informs the transaction is complete and terminate the communication. */
    public static final byte EOT = (byte)0x04;


    public static final byte DLE = 0x10;
    /* Control Symbol - End*/

    public static final int COMMAND_NONE = 0x0000;

}

