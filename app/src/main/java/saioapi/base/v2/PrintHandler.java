package saioapi.base.v2;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.BatteryManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import saioapi.util.Sys;

class PrintHandler
{
    static
    {
        //
        //  Load the corresponding library
        //
        System.loadLibrary("SaioBase");
    }
    
    private final static String TAG                 = "PrintHandlerV2";
    
    //
    //  Printer Status
    //
    private final static int TEMPERATURE_ERROR      = 0x00000001;
    private final static int BUFFER_FULL            = 0x00000002;
    private final static int PAPER_OUT              = 0x00000004;
    private final static int POWER_SUPPLY_FAIL      = 0x00000008;
    private final static int PRINTER_IN_USE         = 0x00000010;
    private final static int PRINTER_ONLINE         = 0x00000020;
    private final static int PRINTER_IN_PENDING     = 0x00000040;
    private final static int COVER_OPEN             = 0x00000080;
    
    //
    // ESCAPE COMMAND
    //
    private final static byte[] ESC_CMD_STATUS      = {0x1D, 0x76};
    private final static byte[] ESC_CMD_CANCEL      = {0X1D, 0x18};
    
    //
    // Action Locks
    //
    private final static int ACTION_NONE            = 0;
    private final static int ACTION_PRINT           = 1;
    private final static int ACTION_PRINT_PENDING   = 2;
    //
    private Integer _nAction                        = ACTION_NONE;
    private Boolean _bRequestStatus                 = false;
    private Boolean _bDoCmd                         = false;
    private Boolean _bDoRsmCncl                     = false;
    
    private Context _c                              = null;
    private UsbInterface _uif                       = null;
    private UsbDeviceConnection _udc                = null;
    private UsbEndpoint _uepOut                     = null;
    private UsbEndpoint _uepIn                      = null;
    private int temperature                         = 0;
    private PrintInterface _p                       = null;
    private int _nHandle                            = 0;
    private int _nErrno                             = 0;
    private int _nJobMode                           = Print.PRINT_JOB_CANCEL;
    private byte[] _batteryInfo                     = null;
    private Integer _nStatus                        = 0;
    private ArrayList<byte[]> _cmdList              = null;
    private PollThread _thrPoll                     = null;
    private PrintThread _thrPrint                   = null;
    private boolean _bSpiPrinter                    = false;
    
    private boolean _debug_dump_graphic_cmd         = false;
    private boolean _debug                          = false;
    
    protected PrintHandler(Context context, PrintInterface p)
    {
        _c = context;
        _p = p;
        _cmdList = new ArrayList<byte[]>();
    }
    
    public int attach(int dev)
    {
        if(null == _p)
        {
            _nErrno = Print.ERR_NO_LISTENER;
            return Print.ERR_OPERATION;
        }
        
        if(null == _c)
        {
            Log.e(TAG, "Context is null.");
            _nErrno = Print.ERR_INVALID_PARAM;
            return Print.ERR_OPERATION;
        }
        
        synchronized(this)
        {
            //already opened
            if(_nHandle != 0)
            {
                _nErrno = Print.ERR_NOT_READY;
                return Print.ERR_OPERATION;
            }
            
            _nHandle = _openPrinter(dev);
            if(Print.ERR_OPERATION == _nHandle)
            {
                Log.e(TAG, "Fail to get xac_usb_printer(" + dev + "): " + _udc + ":" + _uif + ":" + _uepIn + ":" + _uepOut);
                _closePrinter();
                
                _nHandle = 0;   //reset to not opened
                return Print.ERR_OPERATION;
            }
            
            _nAction = ACTION_NONE;
            
            //
            // Empty the printer buffer
            //
            _command(ESC_CMD_CANCEL);

            if(_bSpiPrinter)
            {
                temperature = getBatteryTemperature();

                ////read battery internal resistance
                //_command(new byte[]{(byte)0x12, (byte)0x52, (byte)0x00, (byte)0x00});

                //sent temperature to fw
//TODO: comment out for incorrect AT170R battery
//                _command(new byte[]{(byte)0x12, (byte)0x77, (byte)0x04, (byte)0x00, (byte)0x02, (byte)0x01, (byte)0x00, (byte)temperature});

                ////read temperature from fw
                //_command(new byte[]{(byte)0x12, (byte)0x72, (byte)0x01, (byte)0x00, (byte)0x02});
            }
            
            (_thrPoll = new PollThread()).start();
            (_thrPrint = new PrintThread()).start();
            //_thrPrint.setPriority(Thread.MAX_PRIORITY);
            
            return _nHandle;
        }
    }
    
    public int detach()
    {
        int handle = _nHandle;
        
        if(null != _thrPoll)
            _thrPoll.doStop();
        if(null != _thrPrint)
            _thrPrint.doStop();
        
        //
        // Delay a moment to wait for threads terminating
        //
        try{
            Thread.sleep(100);
        }catch(InterruptedException e){}
        
        synchronized(_cmdList)
        {
            _cmdList.clear();
        }
        
        synchronized(this)
        {
            if(null != _thrPoll) {
                try {
                    _thrPoll.join(100);
                } catch (InterruptedException e) {
                }
            }
            if(null != _thrPrint) {
                try {
                    _thrPrint.join(100);
                } catch (InterruptedException e) {
                }
            }
            //
            _thrPoll = null;
            _thrPrint = null;
            
            _closePrinter();
            _nHandle = 0;
            //_nErrno = Print.ERR_IO_FAIL;
            
            return 0;
        }
    }
    
    public int print(byte[] data, int len)
    {
        if(0 == _nHandle)
        {
            _nErrno = Print.ERR_NOT_OPEN;
            return Print.ERR_OPERATION;
        }
        
        //TOOD: monitor if it causes the memory allocation issue (GC and lag)
        //it may be better if appending cmd directly
        //_cmdList.add(data);
        
        byte[] trimData = new byte[len];
        System.arraycopy(data, 0, trimData, 0, len);
        synchronized(_cmdList)
        {
            _cmdList.add(trimData);
        }
        
        return 0;
    }
    
    public synchronized int status()
    {
        if(0 == _nHandle)
        {
            _nErrno = Print.ERR_NOT_OPEN;
            return Print.ERR_OPERATION;
        }
        
        if(_debug) Log.v(TAG, "\nRequest status (CMD)");
        
        _bRequestStatus = true;
        int ret = _command(ESC_CMD_STATUS);
        _bRequestStatus = false;
        //
        if(Print.ERR_OPERATION != ret)
            return _nStatus;
        
        return Print.ERR_OPERATION;
    }
    
    public int manage(int ctrl)
    {
        if(0 == _nHandle)
        {
            _nErrno = Print.ERR_NOT_OPEN;
            return Print.ERR_OPERATION;
        }
        
        if(Print.PRINT_JOB_CANCEL != ctrl && Print.PRINT_JOB_PENDING != ctrl)
        {
            _nErrno = Print.ERR_INVALID_PARAM;
            return Print.ERR_OPERATION;
        }
        
        _nJobMode = ctrl;
        
        return 0;
    }
    
    public int resume()
    {
        if(0 == _nHandle)
        {
            _nErrno = Print.ERR_NOT_OPEN;
            return Print.ERR_OPERATION;
        }
        
        synchronized(_nStatus)
        {
            if((_nStatus & PAPER_OUT) == PAPER_OUT || (_nStatus & COVER_OPEN) == COVER_OPEN)
            {
                Log.i(TAG, "PAPER_OUT or COVER_OPEN, keep pending!");
                _nErrno = Print.ERR_NOT_READY;
                return Print.ERR_NOT_READY;
            }
        }
            
        synchronized(_nAction)
        {
            if(ACTION_PRINT_PENDING == _nAction)
            {
                _nAction = ACTION_PRINT;
                
                synchronized(_bDoRsmCncl)
                {
                    _bDoRsmCncl = true;
                }
                Log.i(TAG, "Resume()");
            }
        }
        
        return 0;
    }
    
    public int cancel()
    {
        if(0 == _nHandle)
        {
            _nErrno = Print.ERR_NOT_OPEN;
            return Print.ERR_OPERATION;
        }
        
        synchronized(_cmdList)
        {
            _cmdList.clear();
        }
        
        synchronized(_nAction)
        {
            if(ACTION_PRINT_PENDING == _nAction)
            {
                _nAction = ACTION_PRINT; //consume the unfinished command
            }

            // _nAction will become ACTION_NONE if Print.PRINT_JOB_CANCEL == _ctrl && errors occurred (paper out or cover opened ...)
            // so move below to reset status bit - PRINTER_IN_PENDING
            synchronized(_bDoRsmCncl)
            {
                _bDoRsmCncl = true;
            }
            Log.i(TAG, "Cancel()");
        }
        
        return _command(ESC_CMD_CANCEL);
    }
    
    public int lastError()
    {
        return _nErrno;
    }
    
    public byte[] getBatteryInfo()
    {
        return _batteryInfo;
    }
    
    private class PollThread extends Thread
    {
        boolean _bStop  = false;
        int nSleep      = 50;//sleep interval (ms)
        int nPollCount  = 1000 / nSleep;//20;//request status every (nSleep * nPollCount) ms if not busy
        int i           = 0;
        
        @Override
        public void run()
        {
            if(_bSpiPrinter)
            {
                nSleep = 200;
                nPollCount = 1000 / nSleep;
            }

            while(true)
            {
                try{
                    Thread.sleep(nSleep);
                }catch(InterruptedException e){}
                i++;
                
                //
                // Terminate
                //
                if(_bStop)
                    break;
                
                //
                // User request status
                //
                if(_bRequestStatus)
                {
                    i = 0;
                    continue;
                }
                
                //
                // Check action status
                //
                synchronized(_nAction)
                {
                    //
                    // Printing, skip requesting status
                    //
                    if(ACTION_PRINT == _nAction)
                    {
                        i = 0;
                        continue;
                    }
                    
                    //
                    // Printing job is pending, polling status frequently.
                    //
                    else if(ACTION_PRINT_PENDING == _nAction)
                    {
                        if(_debug)
                        {
                            if(i != nPollCount)
                                continue;
                            Log.v(TAG, "\nPolling status (ACTION_PRINT_PENDING)");
                        }
                        _command(ESC_CMD_STATUS);
                        i = 0;
                    }
                    
                    //
                    // ACTION_NONE
                    //
                    else if(ACTION_NONE == _nAction)
                    {
                        //
                        //In case: Keep polling status frequently till status changed to !PRINTER_IN_USE when printing job is just finished.
                        //
                        if((_nStatus & 0x10) == 0x00)
                        {
                            if(i != nPollCount)
                                continue;
                        }
                        
                        if(_debug) Log.v(TAG, "\nPolling status");
                        _command(ESC_CMD_STATUS);
                        i = 0;
                    }
                    
                    //
                    // Unknown
                    //
                    else
                    {
                        i = 0;
                    }
                }
            }
            if(_debug) Log.v(TAG, "PollThread terminated");
        }
        
        public synchronized void doStop()
        {
            _bStop = true;
        }
    }
    
    private class PrintThread extends Thread
    {
        boolean _bStop = false;
        
        @Override
        public void run()
        {
            while(true)
            {
                try{
                    int nSleep = 50;
                    if(ACTION_PRINT == _nAction)
                        nSleep = 5;
                    Thread.sleep(nSleep);
                    //Log.v(TAG, "PrintThread sleep(" + nSleep + ")");
                }catch(InterruptedException e){}
                
                //
                // Terminate
                //
                if(_bStop)
                {
                    _nAction = ACTION_NONE;
                    break;
                }
                
                //
                // User request status
                //
                if(_bRequestStatus)
                    continue;
                
                //
                // Check action status
                //
                synchronized(_nAction)
                {
                    //
                    // Pending
                    //
                    if(ACTION_PRINT_PENDING == _nAction)
                        continue;
                    
                    //
                    // ACTION_PRINT or ACTION_NONE
                    //
                    else
                    {
                        //
                        // Check status before sending command
                        //      BUFFER_FULL will be handled in _command(), not here.
                        //
                        if((_nStatus & (COVER_OPEN|PAPER_OUT|POWER_SUPPLY_FAIL)) > 0)
                        {
                            if(Print.PRINT_JOB_CANCEL == _nJobMode)
                            {
                                synchronized(_cmdList)
                                {
                                    _cmdList.clear();
                                }
                                
                                if(ACTION_PRINT == _nAction)
                                {
                                    Log.i(TAG, "The print job is canceled.");
                                }
                                
                                _nAction = ACTION_NONE;
                            }
                            else
                            {
                                if(ACTION_PRINT == _nAction || 0 < _cmdList.size())
                                {
                                    _nAction = ACTION_PRINT_PENDING;
                                    Log.i(TAG, "The print job is pending.");
                                }
                            }
                            continue;
                        }
                        
                        //
                        // Check list before send command
                        //
                        synchronized(_cmdList)
                        {
                            if(0 == _cmdList.size())
                                continue;
                            
                            //
                            // Sending command
                            //
                            if(_debug) Log.v(TAG, "\nPrintThread: send cmd to printer (all: " + _cmdList.size() + ")");
                            _nAction = ACTION_PRINT;
                            if(0 == _command(_cmdList.get(0)))//success
                            {
                                _cmdList.remove(0);
                                
                                if(0 == _cmdList.size())
                                {
                                    _nAction = ACTION_NONE;
                                    new Thread()
                                    {
                                        @Override
                                        public void run()
                                        {
                                            if(null != _p && 0 != _nHandle)
                                                _p.listener(_nHandle, Print.EVENT_STATUS_DONE);
                                        }
                                        
                                    }.start();
                                }
                            }
                            //else
                            //{
                            //    ;//re-send in next loop
                            //}
                        }
                    }
                }
            }
            if(_debug) Log.v(TAG, "PrintThread terminated");
        }
        
        public synchronized void doStop()
        {
            _bStop = true;
        }
    }
    
    /**
     * @param data
     * @return 0 if success. -1 if IO error or busy
     */
    byte[] _buf = new byte[256];
    private synchronized int _command(byte[] data)
    {
        if(_debug) Log.v(TAG, "_command() IN");
        
        byte nLRC = 0;
        byte[] header = {0x70, (byte)(data.length >> 8), (byte)(data.length & 0xFF)};
        Arrays.fill(_buf, (byte)0);
        
        //
        // Calculate LRC
        //
        nLRC = (byte)(header[0] ^ header[1] ^ header[2]);
        for(int i = 0; i < data.length; i++)
            nLRC = (byte)(nLRC ^ data[i]);
        
        while(true)
        {
            //
            // Prevent write or read after printer being closed.
            //
            synchronized(_bDoCmd)
            {
                if(0 == _nHandle)
                {
                    Log.w(TAG, "Printer is closed!");
                    _nErrno = Print.ERR_NOT_OPEN;
                    return Print.ERR_OPERATION;
                }
                
                if(_bSpiPrinter)
                {
                    byte[] cmd = new byte[data.length + 4];
                    cmd[0] = 0x70;
                    cmd[1] = (byte)(data.length >> 8);
                    cmd[2] = (byte)(data.length & 0xFF);
                    cmd[cmd.length - 1] = nLRC;
                    System.arraycopy(data, 0, cmd, 3, data.length);
                    if (_debug) {
                        if (data[0] == 0x1B && data[1] == 0x2A && !_debug_dump_graphic_cmd)//graphic command is too long ...
                        {
                            Log.v(TAG, "write( ... GRAPHIC_COMMAND ... )");
                        } else {
                            StringBuffer sb = new StringBuffer();
                            sb.append("write(");
                            for (int i = 0; i < cmd.length; i++) {
                                if (i % 32 == 0)
                                {
                                    Log.v(TAG, sb.toString());
                                    sb.setLength(0);
                                    sb.append("\t\t");
                                }
                                sb.append(String.format(" %02X", cmd[i]));
                            }
                            sb.append(" )");
                            Log.v(TAG, sb.toString());
                        }
                    }
                    //
                    int ret = native_spi_write(_nHandle, cmd, cmd.length);
                    if(Print.ERR_OPERATION == ret)
                    {
                        Log.e(TAG, "Write error!");
                        _nErrno = Print.ERR_IO_FAIL;
                        break;
                    }
                }
                else
                {
                    //
                    // Send header - compare the written length?!
                    //
                    //if(_debug)Log.v(TAG, String.format("write( %02X  %02X  %02X )", header[0], header[1], header[2]));
                    if(0 > _udc.bulkTransfer(_uepOut, header, header.length, 1000))
                    {
                        Log.e(TAG, "Write error! (HEADER)");
                        _nErrno = Print.ERR_IO_FAIL;
                        break;
                    }

                    //
                    // Send INF - compare the written length?!
                    //
                    if(_debug)
                    {
                        if(data[0] == 0x1B && data[1] == 0x2A && !_debug_dump_graphic_cmd)//graphic command is too long ...
                        {
                            Log.v(TAG, "write( ... GRAPHIC_COMMAND ... )");
                        }
                        else
                        {
                            StringBuffer sb = new StringBuffer();
                            sb.append("write(");
                            for(int i = 0; i < data.length; i++)
                            {
                                if(i % 32 == 0)
                                {
                                    Log.v(TAG, sb.toString());
                                    sb.setLength(0);
                                    sb.append("\t\t");
                                }
                                sb.append(String.format(" %02X", data[i]));
                            }
                            sb.append(" )");
                            Log.v(TAG, sb.toString());
                        }
                    }
                    if(0 > _udc.bulkTransfer(_uepOut, data, data.length, 1000))
                    {
                        Log.e(TAG, "Write error! (INF)");
                        _nErrno = Print.ERR_IO_FAIL;
                        break;
                    }

                    //
                    // Send LRC - compare the written length?!
                    //
                    //if(_debug)Log.v(TAG, String.format("write( %02X )", nLRC));
                    if(0 > _udc.bulkTransfer(_uepOut, new byte[]{nLRC}, 1, 1000))
                    {
                        Log.e(TAG, "Write error! (LRC)");
                        _nErrno = Print.ERR_IO_FAIL;
                        break;
                    }
                }

                //
                // Read
                //
                int rlen = 0;
                if(_bSpiPrinter)
                {
                    rlen = _buf.length;
                    rlen = native_spi_read(_nHandle, _buf, rlen, 12000);
                    if(Print.ERR_OPERATION == rlen)
                    {
                        Log.e(TAG, "Read error!");
                        if(native_last_error() == Print.ERR_DEV_BUSY)
                        {
                            Log.e(TAG, "SPI-Printer: re-send cmd!!!");
                            continue; //retry
                        }
                        _nErrno = Print.ERR_IO_FAIL;
                        break;
                    }
                }
                else
                {
                    rlen = _udc.bulkTransfer(_uepIn, _buf, _buf.length, 12000);
                    if(0 > rlen)
                    {
                        Log.e(TAG, "Read error!");
                        _nErrno = Print.ERR_IO_FAIL;
                        break;
                    }
                }
                
                if(_debug)
                {
                    StringBuffer sb = new StringBuffer();
                    sb.append("read(");
                    for(int i = 0; i < rlen; i++)
                    {
                        if(i % 32 == 0 && i > 0)
                            sb.append("\n\t\t");
                        sb.append(String.format(" %02X", _buf[i]));
                    }
                    sb.append(" ), len: " + rlen);
                    Log.v(TAG, sb.toString());
                }
            }
            
            //
            // Check response LRC
            //
            byte nResLRC = 0;
            for(int i = 0; i < _buf.length; i++)
                nResLRC = (byte)(nResLRC ^ _buf[i]);
            if(0 != nResLRC)
            {
                Log.e(TAG, "Response LRC is incorrect!");
                _nErrno = Print.ERR_IO_FAIL;
                break;
            }
            
            //
            // Check INF Command
            //      Test Case: The length of text command may be less than 2, like "\n"
            //
            else if(1 < data.length && _buf[3] != data[1] && _buf[0] != 0x72)
            {
                Log.e(TAG, "Response cmd is not matched! (" + String.format("%02X <-> %02x", _buf[3], data[1]) + ")");
                _nErrno = Print.ERR_IO_FAIL;
                break;
            }
            
            //
            // Check INF status
            //
            else
            {
                if(0x71 == _buf[0])
                {
                    switch(_buf[3])
                    {
                        case 0x76://<1Dh><76h>
                        case 0x2A://<1Bh><2Ah>
                        case 0x4A://<1Bh><4Ah>
                        case 0x6F://<1Dh><6Fh>
                        case 0x18://<1Dh><18h>
                        case 0x79://<1Dh><79h>
                        case 0x52://<12h><52h>
                            break;
                        //
                        // Other commands, do not check the status
                        //      EPSON escape command will return 0x00 if success
                        //
                        default:
                            if((((_buf[1] & 0xFF) << 8) + (_buf[2] & 0xFF)) >= 2 && 0x54 == _buf[5])
                                break;
                            if(_debug) Log.v(TAG, "_command() OUT (OK - NO_ST_RET)");
                            return 0;
                    }
                    
                    //
                    // Check status
                    //
                    if((((_buf[1] & 0xFF) << 8) + (_buf[2] & 0xFF)) >= 3)
                        _updateStatus(_buf[4], _buf[5]);
                    else
                        _updateStatus(_buf[4], (byte)0);
                    
                    //battery status
                    if(0x52 == _buf[3])
                    {
                        _batteryInfo = new byte[3];
                        _batteryInfo[0] = _buf[4];
                        _batteryInfo[1] = _buf[5];
                        _batteryInfo[2] = _buf[6];
                        _p.listener(_nHandle, Print.EVENT_BATTERY_ST);
                        return 0;
                    }
                    
                    //
                    // Status Command. It should not be resent anyway.
                    //
                    if(0x76 == _buf[3])
                    {
                        if(_debug) Log.v(TAG, "_command() OUT (OK - ESC_CMD_ST)");
                        return 0;
                    }
                    
                    if((_nStatus & BUFFER_FULL) == 0) //sent and not buffer_full => success
                    {
                        if(_debug) Log.v(TAG, "_command() OUT (OK)");
                        return 0;
                    }
                    //
                    if((_nStatus & (COVER_OPEN|PAPER_OUT|POWER_SUPPLY_FAIL)) > 0)
                    {
                        _nErrno = Print.ERR_NOT_READY;
                        break;
                    }
                    
                    //
                    // Sleep longer to prevent printer is dead on buffer full
                    //
                    try{
                        Thread.sleep(60);
                    }catch(InterruptedException e){}
                    
                    //
                    // Retry - prevent infinite loop
                    //
                    if(_debug) Log.d(TAG, "Resend command!!!");
                }
                else// if(0x72 == buf[0])
                {
                    int err_no_idx = 3;
                    Log.w(TAG, String.format("Response error: %02X", _buf[err_no_idx]));

                    //
                    // Don't care COMMAND_ERROR (0x03). Because the command's request and response are not real-time.
                    //      EPSON escape command will return 0x03 if failed
                    //
                    if(0x03 == _buf[err_no_idx])
                    {
                        if(_debug) Log.v(TAG, "_command() OUT (72-03)");
                        return 0;
                    }

                    _nErrno = Print.ERR_IO_FAIL;
                    break;
                }
            }
        }
        
        if(_debug) Log.v(TAG, "_command() OUT (ERR)");
        return Print.ERR_OPERATION;
    }
    
    private void _updateStatus(byte status1, byte status2)
    {
        int i = PRINTER_ONLINE;
        if ((status1 & 0x01) == 0x01) i |= TEMPERATURE_ERROR;
        if ((status1 & 0x02) == 0x02) i |= POWER_SUPPLY_FAIL;
        if ((status1 & 0x04) == 0x04) i |= BUFFER_FULL;
        if ((status1 & 0x08) == 0x08) i |= PAPER_OUT;
        if ((status1 & 0x10) == 0x10) i |= PRINTER_IN_USE;
        if ((status2 & 0x01) == 0x01) i |= COVER_OPEN;
        
        if(_cmdList.size() > 0)
        {
            i |= PRINTER_IN_USE;
        }
        
        if ((_nStatus & PRINTER_IN_PENDING) == PRINTER_IN_PENDING) //already pending
            i |= PRINTER_IN_PENDING;
        else if((i & PRINTER_IN_USE) == PRINTER_IN_USE //busy and paper out, or busy and cover open
                && ((status1 & 0x08) == 0x08 || (status2 & 0x01) == 0x01))
            i |= PRINTER_IN_PENDING;
        
        synchronized(_bDoRsmCncl)
        {
            if((i & PRINTER_IN_PENDING) == PRINTER_IN_PENDING && _bDoRsmCncl)
            {
                i &= ~PRINTER_IN_PENDING;
            }
            _bDoRsmCncl = false;
        }
        
        synchronized(_nStatus)
        {
            if(_nStatus != i)
            {
                //Log.v(TAG, String.format("Status changed! (0x%02X -> 0x%02X)", _nStatus, i));
                _nStatus = i;
                
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        if(null != _p && 0 != _nHandle)
                            _p.listener(_nHandle, Print.EVENT_STATUS_CHG);
                    }
                    
                }.start();
            }
        }
    }
    
    private int getBatteryTemperature()
    {
        Intent intent = _c.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int temp = ((int) intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0)) / 10;
        int voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);
        if(_debug)
            Log.d(TAG, "batery temp=" + temp + ", voltage=" + voltage);
        
        return temp;
    }
    
    private int _openPrinter(int dev)
    {
        //spi-if
        if((dev & Print.DEVICE_PRINTER_IF_MASK) == Print.DEVICE_PRINTER_IF_SPI)
        {
            int ret = native_spi_open(dev);
            //opened
            if(Print.ERR_OPERATION != dev)
            {
                _bSpiPrinter = true;
                return ret;
            }
            else
            {
                _nErrno = Print.ERR_IO_FAIL;
                return Print.ERR_OPERATION;
            }
        }

        //usb-if
        UsbManager um = (UsbManager) _c.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = um.getDeviceList();
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        UsbDevice device = null;

        int i = 0;
        if(_debug)
            Log.d(TAG, "finding xac_usb_printer:");
        while(deviceIterator.hasNext())
        {
            device = deviceIterator.next();
            if(_debug)
                Log.d(TAG, "    usbdevice" + i +
                        ": name=" + device.getDeviceName() +
                        ", vid=" + device.getVendorId() +
                        ", pid=" + device.getProductId() +
                        ", id=" + device.getDeviceId() +
                        ", ifcount=" + device.getInterfaceCount() +
                        ", protocol=" + device.getDeviceProtocol() +
                        ", class=" + device.getDeviceClass() +
                        ", subclass=" + device.getDeviceSubclass());

            //only for xac_usb_printer & the devId is matched with user specified
            if(device.getDeviceId() == dev && 0x2182 == device.getVendorId() && 0x7000 == device.getProductId())
            {
                int ifCount = device.getInterfaceCount();
                UsbInterface uif = null;
                for(int j = 0; j < ifCount; j++)
                {
                    uif = device.getInterface(j);
                    if(_debug)
                        Log.d(TAG, "        usbinterface" + j +
                                ": epcount=" + uif.getEndpointCount() +
                                ", id=" + uif.getId() +
                                ", class=" + uif.getInterfaceClass() +
                                ", protocol=" + uif.getInterfaceProtocol() +
                                ", subclass=" + uif.getInterfaceSubclass());

                    //class for printer
                    if(7 == uif.getInterfaceClass() && null == _udc)
                    {
                        _uif = uif;
                        _udc = um.openDevice(device);

                        UsbEndpoint uep = null;
                        for (int k = 0; k < uif.getEndpointCount(); k++)
                        {
                            uep = uif.getEndpoint(k);
                            if(_debug)
                                Log.d(TAG, "            ep" + k +
                                        ": direction=" + uep.getDirection() +
                                        ", addr=" + uep.getAddress() +
                                        ", attr=" + uep.getAttributes() +
                                        ", num=" + uep.getEndpointNumber() +
                                        ", interval=" + uep.getInterval() +
                                        ", maxPackSize=" + uep.getMaxPacketSize() +
                                        ", type=" + uep.getType());

                            if(UsbConstants.USB_ENDPOINT_XFER_BULK == uep.getType())
                            {
                                if (uep.getDirection() == UsbConstants.USB_DIR_IN)
                                    _uepIn = uep;
                                else
                                    _uepOut = uep;
                            }
                            //comment out this to dump all
                            if(null != _uepIn && null != _uepOut)
                                break;
                        }
                    }

                    //comment out this to dump all
                    if(null != _uepIn && null != _uepOut && null != _udc && null != _uif)
                        break;
                }
            }
            i++;
        }

        if(null == _uepIn || null == _uepOut || null == _udc || null == _uif)
        {
            _nErrno = Print.ERR_NOT_EXIST;
            return Print.ERR_OPERATION;
        }
        
        //!!! claim must be true, bcz need to set temperature to notify FW to avoid printer low temperature issue!!!
        boolean claim = true;
        boolean claimed = _udc.claimInterface(_uif, claim);
        Log.d(TAG, "claimInterface(" + claim + "): " + claimed);
        //
        int ret = 0;
        byte[] buf = new byte[1285];
        StringBuffer sb = null;
        /*
        //id
        Arrays.fill(buf, (byte) 0);
        ret = _udc.controlTransfer(0xa1, 0x0, 0x0a00, 0x0, buf, 1284, 1000);
        Log.d(TAG, "controlTransfer(id): " + ret);
        if(ret > 0)
        {
            sb = new StringBuffer();
            sb.append("    ");
            for(int z = 0; z < ret; z++)
                sb.append(String.format("%02X ", buf[z]));
            Log.d(TAG, sb.toString());
            Log.d(TAG, "    " + new String(buf, 0, ret));
        }
        //
        //status
        Arrays.fill(buf, (byte) 0);
        ret = _udc.controlTransfer(0xa1, 0x1, 0x0, 0x0, buf, 1, 1000);
        Log.d(TAG, "controlTransfer(status): " + ret);
        if(ret > 0)
        {
            sb = new StringBuffer();
            sb.append("    ");
            for(int z = 0; z < ret; z++)
                sb.append(String.format("%02X ", buf[z]));
            Log.d(TAG, sb.toString());
            //Log.d(TAG, "    " + new String(buf, 0, ret));
        }
        //*/
        //
        //temperature
        //*
        Arrays.fill(buf, (byte) 0);
        temperature = getBatteryTemperature();
        //temperature = (temperature + 5) % 30;//debug-test
        ret = _udc.controlTransfer(0xa1, 0x10, temperature, 0x0, buf, 10, 1000);
        Log.i(TAG, "controlTransfer(t=" + temperature + "): " + ret);
        if(ret > 0)
        {
            sb = new StringBuffer();
            sb.append("    ");
            for(int z = 0; z < ret; z++)
                sb.append(String.format("%02X ", buf[z]));
            Log.d(TAG, sb.toString());
            Log.d(TAG, "    " + new String(buf, 0, ret));
        }
        //*/

        //To avoid the issue - usb data toggle is reset
        for(int x = 0; x < 2; x++)
        {
            int rw = _udc.bulkTransfer(_uepOut, new byte[]{(byte)0x1D, (byte)0x49}, 2, 1000);
            if(0 > rw)
            {
                Log.e(TAG, "Initial write (GS_I) error: " + rw);
                _nErrno = Print.ERR_IO_FAIL;
                //return Print.ERR_OPERATION;
            }
            else
            {
                Arrays.fill(buf, (byte)0);
                rw = _udc.bulkTransfer(_uepIn, buf, buf.length, 500);
                //*
                if(rw > 0)
                {
                    sb = new StringBuffer();
                    sb.append("Response for GS_I: (" + rw + ") " + new String(buf, 0, rw));
                    /*
                    sb.append("\n    ");
                    for(int z = 0; z < rw; z++)
                        sb.append(String.format("%02X ", buf[z]));
                    //*/
                    Log.d(TAG, sb.toString());
                    
                    break;
                }
                else
                {
                    Log.e(TAG, "No response for GS_I! (" + rw + ")");
                }
            }
        }

        //Now only A64 can/must adjust cpufreq, we do platform filter in JNI
        //TODO: Now we filter all xac_printers adapted on allwinner products, may need to separate internal and external in the future
        Sys.setCpuFreqGov(Sys.CPUFREQ_SCALING_MAX_FREQ_MASK | Sys.CPUFREQ_GOVERNOR_USERSPACE_480000);
        return dev;
    }
    
    private void _closePrinter()
    {
        //spi-if
        if(_bSpiPrinter)
        {
            _bSpiPrinter = false;
            int ret = native_spi_close(_nHandle);
            Log.v(TAG, "close spi-printer: " + ret);

            return;
        }
        
        //usb-if
        if(_udc != null)
        {
            if(_uif != null)
            {
            	_udc.releaseInterface(_uif);
                _uif = null;
            }
            _udc.close();
            _udc = null;
        }
        if(_uepOut != null)
            _uepOut = null;
        if(_uepIn != null)
            _uepIn = null;

        //Now only A64 can/must adjust cpufreq, we do platform filter in JNI
        //TODO: Now we filter all xac_printers adapted on allwinner products, may need to separate internal and external in the future
        Sys.setCpuFreqGov(Sys.CPUFREQ_SCALING_MAX_FREQ_MASK | Sys.CPUFREQ_GOVERNOR_USERSPACE_1152000);
    }
    
    protected void finalize()
    {
        if(null != _thrPoll)
            _thrPoll.doStop();
        if(null != _thrPrint)
            _thrPrint.doStop();
        
        //
        // Delay a moment to wait for threads terminating
        //
        try{
            Thread.sleep(100);
        }catch(InterruptedException e){}
        
        synchronized(_cmdList)
        {
            _cmdList.clear();
        }
        
        synchronized(this)
        {
            try{
                if(null != _thrPoll)
                    _thrPoll.join(100);
            }catch(InterruptedException e){}
            try{
                if(null != _thrPrint)
                    _thrPrint.join(100);
            }catch(InterruptedException e){}
            //
            _thrPoll = null;
            _thrPrint = null;
            
            if(_nHandle > 0)
                _closePrinter();
            _nHandle = 0;
        }
    }

    protected native int native_last_error();
    protected native int native_spi_open(int dev);
    protected native int native_spi_close(int handle);
    protected native int native_spi_write(int handle, byte[] data, int len);
    protected native int native_spi_read(int handle, byte[] data, int len, int timeout);
    
}
