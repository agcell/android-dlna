package org.ray.upnp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.URL;

import org.ray.upnp.ssdp.SSDP;
import org.ray.upnp.ssdp.SSDPNotifyMsg;
import org.ray.upnp.ssdp.SSDPRespMsg;
import org.ray.upnp.ssdp.SSDPSearchMsg;
import org.ray.upnp.ssdp.SSDPSocket;

public class ControlPoint {
    
    private SSDPSocket mSSDPSocket;

    private Runnable mRespNotifyHandler = new Runnable() {
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
    
    private ControlPointListener mListener = null;
    
    public void registerListener(ControlPointListener listener) {
        mListener = listener;
    }
    
    public void unregisterListener() {
        mListener = null;
    }
    
    private void notifyDeviceAdded(String url) {
        System.out.println("Device add: " + url);
        mDeviceAddUrl = url;
        
        // Create a new thread to do some network operations
        new Thread(mGetDeviceTask, url).start();
    }
    
    private void notifyDeviceRemoved(String url) {
        System.out.println("Device remove: " + url);
    }
    
    private String mDeviceAddUrl;
    Runnable mGetDeviceTask = new Runnable() {
        public void run() {
            Device deviceAdd = Device.createInstanceFromXML(mDeviceAddUrl);
            
            if ((deviceAdd != null) && (mListener != null)) {
                mListener.onDeviceAdd(deviceAdd);
            }
        }
    };

//    static String getDeviceDescription(String strURL) {
//    	URL url;
//    	InputStream is = null;
//    	String description = null;
//        try {            
//            url = new URL(strURL);
//            is = url.openStream(); // Maybe time-consuming
//            BufferedInputStream bis = new BufferedInputStream(is);
//            
//            int len = 1024 * 16;
//            byte[] buf = new byte[len];
//            while (bis.read(buf, 0, len) != -1) {
//                ; // Do nothing
//            }
//            
//            description = new String(buf);
//            System.out.println(description);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (is != null) {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//        
//        return description;
//    }
    
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
