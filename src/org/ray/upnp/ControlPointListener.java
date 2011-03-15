package org.ray.upnp;

public interface ControlPointListener {
    void onDeviceAdded(String url);
    void onDeviceRemoved(String url);
}
