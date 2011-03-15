package org.ray.upnp.ssdp;

import java.net.DatagramPacket;
import java.util.Scanner;

public class SSDPRespMsg {
    public static boolean isSSDPRespMsg(DatagramPacket dp) {
        String content = new String(dp.getData());
        Scanner s = new Scanner(content);
        String startLine = s.nextLine();
        if (SSDP.SL_OK.equals(startLine)) {
            return true;
        }
        
        return false;
    }
}
