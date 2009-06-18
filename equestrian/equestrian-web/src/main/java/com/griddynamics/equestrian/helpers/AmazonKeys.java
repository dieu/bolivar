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
    private String AWSAccessKeyId = "";
    private String SecretAccessKey = "";
    private String UserId = "";
    private String workerImageId = "";
    private String serverImageId = "";
    private String schedulerImageId = "";

    public static void main(String[] ars) {
        new AmazonKeys();
    }

    public AmazonKeys() {
        File aws = new File(ApplicationPath.APPLICATION_PATH + "aws.keys");
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
                    } else if(rawName.equals("wid")) {
                        if (attrs != null && workerImageId.equals("")) {
                            int len = attrs.getLength();
                            for (int i = 0; i < len; i++) {
                                if(attrs.getQName(i).equals("id")) {
                                    workerImageId = attrs.getValue(i);
                                }
                            }
                        }
                    } else if(rawName.equals("sid")) {
                        if (attrs != null && serverImageId.equals("")) {
                            int len = attrs.getLength();
                            for (int i = 0; i < len; i++) {
                                if(attrs.getQName(i).equals("id")) {
                                    serverImageId = attrs.getValue(i);
                                }
                            }
                        }
                    } else if(rawName.equals("schid")) {
                        if (attrs != null && schedulerImageId.equals("")) {
                            int len = attrs.getLength();
                            for (int i = 0; i < len; i++) {
                                if(attrs.getQName(i).equals("id")) {
                                    schedulerImageId = attrs.getValue(i);
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

    public String getWorkerImageId() {
        return workerImageId;
    }

    public String getServerImageId() {
        return serverImageId;
    }

    public String getSchedulerImageId() {
        return schedulerImageId;
    }
}
