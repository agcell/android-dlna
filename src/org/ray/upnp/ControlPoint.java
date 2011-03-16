package org.ray.upnp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ray.upnp.ssdp.SSDP;
import org.ray.upnp.ssdp.SSDPNotifyMsg;
import org.ray.upnp.ssdp.SSDPRespMsg;
import org.ray.upnp.ssdp.SSDPSearchMsg;
import org.ray.upnp.ssdp.SSDPSocket;

public class ControlPoint {
    
    private SSDPSocket mSSDPSocket;
    
    private Map<String, Device> mCache = new HashMap<String, Device>();
    
    private ControlPointListener mListener = null;

    public ControlPoint() throws IOException {
        mSSDPSocket = new SSDPSocket();

        new Thread(mRespNotifyHandler).start();
    }
    
    public void search(String type) throws IOException {
        SSDPSearchMsg search = new SSDPSearchMsg(type);
        mSSDPSocket.send(search.toString());
    }
    
    /* Default to search for service of ContentDirectory */
    public void search() throws IOException {
        search(SSDP.ST_ContentDirectory);
    }
    
    public void registerListener(ControlPointListener listener) {
        mListener = listener;
    }
    
    public void unregisterListener() {
        mListener = null;
    }
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
    
    private void notifyDeviceAdded(String url) {
        /* Prevent from creating duplicated devices */
        if (mCache.containsKey(url)) {
            return;
        }
        
        mDeviceAddUrl = url;
                
        /* Create a new thread to do some network operations */
        new Thread(mGetDeviceTask, url).start();
    }
    
    private void notifyDeviceRemoved(String url) {
        if (mCache.containsKey(url)) {
            if (mListener != null) {
                mListener.onDeviceRemove(mCache.get(url));
            }
            mCache.remove(url);
        }
    }
    
    private String mDeviceAddUrl;
    private Runnable mGetDeviceTask = new Runnable() {
        public void run() {
            Device deviceAdd = Device.createInstanceFromXML(mDeviceAddUrl);
            
            if (deviceAdd != null) {
                System.out.println(deviceAdd);
                mCache.put(mDeviceAddUrl, deviceAdd);
                
                if (mListener != null) {
                    mListener.onDeviceAdd(deviceAdd);
                }
            }
        }
    };
    
    /* For test purpose */
    public static void main(String[] args) {
        try {
            ControlPointListener listener = new ControlPointListener() {
                
                @Override
                public void onDeviceRemove(Device device) {
                    System.out.println(device + " remove");
                }
                
                @Override
                public void onDeviceAdd(Device device) {
                    System.out.println(device + " add");
                }
            };
            ControlPoint cp = new ControlPoint();
            cp.registerListener(listener);
            cp.search();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /* For test purpose */
    private void print(DatagramPacket dp) {
        System.out.println(new String(dp.getData()));
    }
}
