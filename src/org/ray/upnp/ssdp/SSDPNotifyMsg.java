package org.ray.upnp.ssdp;

import java.net.DatagramPacket;
import java.util.Scanner;

import javax.xml.crypto.Data;

public class SSDPNotifyMsg {
    public static boolean isSSDPNotifyMsg(DatagramPacket dp) {
        String startLine = SSDP.parseStartLine(dp);
        if (startLine.equals(SSDP.SL_NOTIFY)) {
            return true;
        }
        
        return false;
    }
    
    public static boolean isAlive(DatagramPacket dp) {
        String NTSValue = SSDP.parseHeaderValue(dp, SSDP.NTS);
        if (NTSValue.equals(SSDP.NTS_ALIVE)) {
            return true;
        }
        
        return false;
    }
    
    public static boolean isByeByte(DatagramPacket dp) {
        String NTSValue = SSDP.parseHeaderValue(dp, SSDP.NTS);
        if (NTSValue.equals(SSDP.NTS_BYEBYE)) {
            return true;
        }
        
        return false;
    }
    
    public static boolean isUpdate(DatagramPacket dp) {
        String NTSValue = SSDP.parseHeaderValue(dp, SSDP.NTS);
        if (NTSValue.equals(SSDP.NTS_UPDATE)) {
            return true;
        }
        
        return false;
    }
    
    public static boolean isContentDirectory(DatagramPacket dp) {
        String NTValue = SSDP.parseHeaderValue(dp, SSDP.NT);
        if (NTValue.equals(SSDP.NT_ContentDirectory)) {
            return true;
        }
        
        return false;
    }
}
