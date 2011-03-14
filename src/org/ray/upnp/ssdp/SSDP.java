package org.ray.upnp.ssdp;

public class SSDP {
    /* New line definition */
    public static final String NEWLINE = "\r\n";
    
    public static final String ADDRESS = "239.255.255.250";
    public static final int PORT = 1900;

    /* Definitions of start line */
    public static final String SL_NOTIFY = "NOTIFY * HTTP/1.1";
    public static final String SL_MSEARCH = "M-SEARCH * HTTP/1.1";
    public static final String SL_OK = "HTTP/1.1 200 OK";

    /* Definitions of search targets */
    public static final String ST_RootDevice = "ST:rootdevice";
    public static final String ST_ContentDirectory = "ST:urn:schemas-upnp-org:service:ContentDirectory:1";
    
    /* Definitions of notification sub type */
    public static final String NTS_ALIVE = "NTS:ssdp:alive";
    public static final String NTS_BYE = "NTS:ssdp:byebye";
    public static final String NTS_UPDATE = "NTS:ssdp:update";
}
