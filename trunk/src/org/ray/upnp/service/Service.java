package org.ray.upnp.service;

import java.util.List;

public class Service {
    public String serviceType;    /* Required. UPnP service type. */
    public String serviceId;      /* Required. Service identifier. */
    public String SCPDURL;        /* Required. Relative URL for service description. */
    public String controlURL;     /* Required. Relative URL for control. */
    public String eventSubURL;    /* Relative. Relative URL for eventing. */
    
    List<Action> mActionList;
    List<StateVariable> mServiceStateTable;
    
    boolean parseSCPD() {
        if (SCPDURL == null) {
            return false;
        }
        
        return true;
    }
    
    void parseControl() {
        
    }
    
    void parseEvent() {
        
    }
}
