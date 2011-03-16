package org.ray.upnp;

public interface ControlPointListener {
    void onDeviceAdd(Device device);
    void onDeviceRemove(Device device);
}
