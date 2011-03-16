package org.ray.upnp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Map;

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

        /* Send 3 times like WindowsMedia */
        for (int i = 0; i < 3; i++) {
            mSSDPSocket.send(search.toString());

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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
        notifyDeviceAdd(url);
    }

    /* Listen to devices of interest, default to service of ContentDirectory */
    private void handleNotifyMsg(DatagramPacket dp) {
        if (SSDPNotifyMsg.isContentDirectory(dp)) {
            String url = SSDP.parseHeaderValue(dp, SSDP.LOCATION);

            if (SSDPNotifyMsg.isAlive(dp)) {
                notifyDeviceAdd(url);
            } else if (SSDPNotifyMsg.isByeByte(dp)) {
                notifyDeviceRemove(url);
            } else if (SSDPNotifyMsg.isUpdate(dp)) {

            }
        }
    }

    private void notifyDeviceAdd(String url) {
        synchronized (mCache) {
            /* Prevent from creating duplicated devices */
            if (mCache.containsKey(url)) {
                return;
            }
            mCache.put(url, null);
        }

        new Thread(new GetDeviceTask(url), url).start();
    }

    private void notifyDeviceRemove(String url) {
        synchronized (mCache) {
            if (mCache.containsKey(url)) {
                Device device = mCache.get(url);
                System.out.println(device + " remove [" + url);
                if (mListener != null) {
                    mListener.onDeviceRemove(device);
                }
                
                mCache.remove(url);
                System.out.println(mCache);
            }
        }
    }

    private class GetDeviceTask implements Runnable {
        String url;
        
        public GetDeviceTask(String url) {
            this.url = url;
        }
        
        @Override
        public void run() {
            Device device = Device.createInstanceFromXML(url);

            if (device != null) {
                System.out.println(device + " add [" + url + "]");
                if (mListener != null) {
                    mListener.onDeviceAdd(device);
                }
                synchronized (mCache) {
                    mCache.put(url, device);
                    System.out.println(mCache);
                }
            }
        }
    }

    /* For test purpose */
    public static void main(String[] args) {
        try {
            ControlPointListener listener = new ControlPointListener() {

                @Override
                public void onDeviceRemove(Device device) {
//                    System.out.println("Listener device remove");
                }

                @Override
                public void onDeviceAdd(Device device) {
//                    System.out.println("Listener device add");
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
