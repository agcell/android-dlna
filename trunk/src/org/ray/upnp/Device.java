package org.ray.upnp;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class Device {
    static final String DEVICE_TYPE = "deviceType";
    static final String FRIENDLY_NAME = "friendlyName";
    static final String MANUFACTURER = "manufacturer";
    static final String MANUFACTURER_URL = "manufacturerURL";
    static final String MODEL_DESCRIPTION = "modelDescription";
    static final String MODEL_NAME = "modelName";
    static final String MODEL_NUMBER = "modelNumber";
    static final String MODEL_URL = "modelURL";
    static final String SERIAL_NUMBER = "serialNumber";
    static final String UDN = "UDN";
    
    String mDeviceType;         /* Required */
    String mFriendlyName;       /* Required */
    String mManufactorer;       /* Required */
//    String mManufactorerURL;    /* Optional */
//    String mModelDescription;   /* Recommended */
    String mModelName;          /* Required */
//    String mModelNumber;        /* Recommended */
//    String mModelURL;           /* Optional */
//    String mSerialNumber;       /* Recommended */
    String mUDN;                /* Required */
//    String mUPC;                /* Optional */
    
    public static Device createInstanceFromXML(String url) {
        final Device device = new Device();
        
        DefaultHandler dh = new DefaultHandler() {
            String currentValue = null;
            
            @Override
            public void characters(char[] ch, int start, int length)
                    throws SAXException {
                currentValue = new String(ch, start, length);
            }
            
            @Override
            public void endElement(String uri, String localName, String qName)
                    throws SAXException {
                if (DEVICE_TYPE.equals(qName)) {
                    device.mDeviceType = currentValue;
                } else if (FRIENDLY_NAME.equals(qName)) {
                    device.mFriendlyName = currentValue;
                } else if (MANUFACTURER.equals(qName)) {
                    device.mManufactorer = currentValue;
                } else if (MODEL_NAME.equals(qName)) {
                    device.mModelName = currentValue;
                } else if (UDN.equals(qName)) {
                    device.mUDN = currentValue;
                } 
            }
        };
        
        SAXParserFactory factory = SAXParserFactory.newInstance();
        
        SAXParser parser;
        try {
            parser = factory.newSAXParser();
            parser.parse(url, dh);

            return device;
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    @Override
    public String toString() {
        return mFriendlyName;
    }
}
