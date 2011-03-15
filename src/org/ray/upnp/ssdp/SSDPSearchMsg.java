package org.ray.upnp.ssdp;

import static org.ray.upnp.ssdp.SSDP.NEWLINE;

public class SSDPSearchMsg {
    static final String HOST = "Host:" + SSDP.ADDRESS + ":" + SSDP.PORT;
    static final String MAN = "Man:\"ssdp:discover\"";
    
    int mMX = 3;    /* seconds to delay response */
    String mST;     /* Search target */
    
    public SSDPSearchMsg(String ST) {
        mST = ST;
    }
    
    public int getmMX() {
        return mMX;
    }

    public void setmMX(int mMX) {
        this.mMX = mMX;
    }

    public String getmST() {
        return mST;
    }

    public void setmST(String mST) {
        this.mST = mST;
    }
    
    @Override
    public String toString() {
        StringBuilder content = new StringBuilder();
        
        content.append(SSDP.SL_MSEARCH).append(NEWLINE);
        content.append(HOST).append(NEWLINE);
        content.append(MAN).append(NEWLINE);
        content.append(mST).append(NEWLINE);
        content.append("MX:" + mMX).append(NEWLINE);
        content.append(NEWLINE);
        
        return content.toString();
    }
}
