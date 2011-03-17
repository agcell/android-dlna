package org.ray.upnp.ssdp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;

public class SSDPSocket {
    SocketAddress mSSDPMulticastGroup;
    MulticastSocket mLocalSocket;
    
    NetworkInterface mNetIf;

    public SSDPSocket() throws IOException {
        InetAddress localInAddress = InetAddress.getLocalHost();
        System.out.println("Local address: " + localInAddress.getHostAddress());

        mSSDPMulticastGroup = new InetSocketAddress(SSDP.ADDRESS, SSDP.PORT);
        mLocalSocket = new MulticastSocket(new InetSocketAddress(localInAddress,
                SSDP.PORT));

        mNetIf = NetworkInterface.getByInetAddress(localInAddress);
        mLocalSocket.joinGroup(mSSDPMulticastGroup, mNetIf);
    }

    /** Used to send SSDP packet */
    public void send(String data) throws IOException {
        DatagramPacket dp = new DatagramPacket(data.getBytes(), data.length(),
                mSSDPMulticastGroup);

        mLocalSocket.send(dp);
    }

    /** Used to receive SSDP packet */
    public DatagramPacket receive() throws IOException {
        byte[] buf = new byte[1024];
        DatagramPacket dp = new DatagramPacket(buf, buf.length);

        mLocalSocket.receive(dp);

        return dp;
    }

    /** Close the socket */
    public void close() {
        if (mLocalSocket != null) {
            try {
                mLocalSocket.leaveGroup(mSSDPMulticastGroup, mNetIf);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mLocalSocket.close();
        }
    }

    /* For test purpose */
    public static void main(String[] args) {

        SSDPSearchMsg search = new SSDPSearchMsg(SSDP.ST_ContentDirectory);
        System.out.println(search.toString());
        
        SSDPSocket sock;
        try {
            sock = new SSDPSocket();
            sock.send(search.toString());
            
            while (true) {
                DatagramPacket dp = sock.receive();
                String c = new String(dp.getData());
                System.out.println(c);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
