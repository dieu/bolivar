package com.griddynamics.equestrian.helpers;

import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

/**
 * @author: apanasenko aka dieu
 * Date: 29.05.2009
 * Time: 13:38:33
 */
public class AmazonKeys {
    public String AWSAccessKeyId = "";
    public String SecretAccessKey = "";
    public String UserId = "";

    public static void main(String[] ars) {
           new AmazonKeys();
    }

    public AmazonKeys() {
        File aws;
        if(System.getProperty("file.separator").equals("\\")) {
            aws  = new File(ApplicationPath.APPLICATION_PATH_WIN + "aws.keys");
        } else {
            aws  = new File(ApplicationPath.APPLICATION_PATH_NIX + "aws.keys");
        }
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            sp.parse(aws, new DefaultHandler() {
                public void startElement(String namespaceURI, String localName,
                                         String rawName, Attributes attrs) {
                    if(rawName.equals("ak")) {
                        if (attrs != null && AWSAccessKeyId.equals("")) {
                            int len = attrs.getLength();
                            for (int i = 0; i < len; i++) {
                                if(attrs.getQName(i).equals("key")) {
                                    AWSAccessKeyId = attrs.getValue(i);
                                }
                            }
                        }
                    } else if(rawName.equals("sk")) {
                        if (attrs != null && SecretAccessKey.equals("")) {
                            int len = attrs.getLength();
                            for (int i = 0; i < len; i++) {
                                if(attrs.getQName(i).equals("key")) {
                                    SecretAccessKey = attrs.getValue(i);
                                }
                            }
                        }
                    } else if(rawName.equals("uid")) {
                        if (attrs != null && UserId.equals("")) {
                            int len = attrs.getLength();
                            for (int i = 0; i < len; i++) {
                                if(attrs.getQName(i).equals("name")) {
                                    UserId = attrs.getValue(i);
                                }
                            }
                        }
                    }
                }
            });
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAWSAccessKeyId() {
        return AWSAccessKeyId;
    }

    public String getSecretAccessKey() {
        return SecretAccessKey;
    }

    public String getUserId() {
        return UserId;
    }
}
