package org.ray.upnp;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class Device {
    static final String DEVICE_TYPE = "deviceType";
    static final String PRESENTATION_URL = "presentationURL";
    static final String FRIENDLY_NAME = "friendlyName";
    
    String mDeviceType;         /* Required */
    String mFriendlyName;       /* Required */
    String mManufactorer;       /* Required */
    String mManufactorerURL;    /* Optional */
    String mModelDescription;   /* Recommended */
    String mModelName;          /* Required */
    String mModelNumber;        /* Recommended */
    String mModelURL;           /* Optional */
    String mSerialNumber;       /* Recommended */
    String mUDN;                /* Required */
    String mUPC;                /* Optional */
    
    public static Device createInstanceFromXML(String url) {
        Device device = new Device();
        
        DefaultHandler dh = new DefaultHandler() {
            String currentElement = null;
            
            @Override
            public void startElement(String uri, String localName,
                    String qName, Attributes attributes) throws SAXException {
                System.out.println(qName);
                
                currentElement = qName;
            }
            
            @Override
            public void characters(char[] ch, int start, int length)
                    throws SAXException {
                if (FRIENDLY_NAME.equals(currentElement)) {
                    String value = new String(ch, start, length);
                    System.out.println(value);
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
    
    public static void main(String[] args) {
        createInstanceFromXML("http://172.16.4.21:49152/description.xml");
    }
}
