package org.ray.upnp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import org.ray.upnp.ssdp.SSDP;
import org.ray.upnp.ssdp.SSDPNotifyMsg;
import org.ray.upnp.ssdp.SSDPRespMsg;
import org.ray.upnp.ssdp.SSDPSearchMsg;
import org.ray.upnp.ssdp.SSDPSocket;

public class ControlPoint {
    
    SSDPSocket mSSDPSocket;

    /* To store urls from SSDP notify or response */
    Set<String> mURLs = new TreeSet<String>();

    Runnable mRespNotifyHandler = new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    DatagramPacket dp = mSSDPSocket.receive();
                    if (SSDPRespMsg.isSSDPRespMsg(dp)) {
                        handleRespMsg(dp);
                    } else if (SSDPNotifyMsg.isSSDPNotifyMsg(dp)) {
                        handleNotifyMsg(dp);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    
    public ControlPoint() throws IOException {
        mSSDPSocket = new SSDPSocket();

        new Thread(mRespNotifyHandler).start();
    }
    
    private void handleRespMsg(DatagramPacket dp) {
        String url = SSDP.parseHeaderValue(dp, SSDP.LOCATION);
        notifyDeviceAdded(url);
    }
    
    /* Listen to devices of interest, default to service of ContentDirectory */
    private void handleNotifyMsg(DatagramPacket dp) {        
        if (SSDPNotifyMsg.isContentDirectory(dp)) {
            String url = SSDP.parseHeaderValue(dp, SSDP.LOCATION);
            
            if (SSDPNotifyMsg.isAlive(dp)) {
                notifyDeviceAdded(url);
            } else if (SSDPNotifyMsg.isByeByte(dp)) {
                notifyDeviceRemoved(url);
            } else if (SSDPNotifyMsg.isUpdate(dp)) {
                
            }
        }
    }
    
    /* For test purpose */
    void print(DatagramPacket dp) {
        System.out.println(new String(dp.getData()));
    }  


    public void search(String type) throws IOException {
        SSDPSearchMsg search = new SSDPSearchMsg(type);
        mSSDPSocket.send(search.toString());
    }
    
    /* Default to search for service of ContentDirectory */
    public void search() throws IOException {
        search(SSDP.ST_ContentDirectory);
    }
    
    ControlPointListener mListener = null;
    
    public void registerListener(ControlPointListener listener) {
        mListener = listener;
    }
    
    public void unregisterListener() {
        mListener = null;
    }
    
    private void notifyDeviceAdded(String url) {
        System.out.println("Device add: " + url);
        if (mListener != null) {
            mListener.onDeviceAdded(url);
        }
    }
    
    private void notifyDeviceRemoved(String url) {
        System.out.println("Device remove: " + url);
        if (mListener != null) {
            mListener.onDeviceRemoved(url);
        }
    }

    public void getDeviceDescription(String url) {

    }
    
    /* For test purpose */
    public static void main(String[] args) {
        try {
            ControlPoint cp = new ControlPoint();
            cp.search();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
