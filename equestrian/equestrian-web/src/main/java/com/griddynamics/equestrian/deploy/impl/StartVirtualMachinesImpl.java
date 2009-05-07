package com.griddynamics.equestrian.deploy.impl;

import com.griddynamics.equestrian.deploy.StartVirtualMachines;
import com.griddynamics.equestrian.helpers.AccessKey;
import com.amazonaws.ec2.AmazonEC2;
import com.amazonaws.ec2.AmazonEC2Client;
import com.amazonaws.ec2.AmazonEC2Exception;
import com.amazonaws.ec2.AmazonEC2Config;
import com.amazonaws.ec2.mock.AmazonEC2Mock;
import com.amazonaws.ec2.model.*;

import javax.xml.bind.annotation.XmlAccessorType;
import java.util.List;

/**
 * @author: apanasenko aka dieu
 * Date: 29.04.2009
 * Time: 19:48:35
 */
public class StartVirtualMachinesImpl implements StartVirtualMachines {
    private AmazonEC2 service;
    private DescribeImagesRequest request;

    public static void main(String[] arg) {
        StartVirtualMachinesImpl v = new StartVirtualMachinesImpl();
        v.create(1);
        v.verify();

    }

    public void create(int nMachines) {
        String accessKeyId = AccessKey.ACCESS_KEY_ID;
        String accessKeySecret = AccessKey.ACCESS_KEY_SECRET;
//        this.service = new AmazonEC2Client(accessKeyId, accessKeySecret);

//        AmazonEC2Config config = new AmazonEC2Config();
//        config.setSignatureVersion("0");
//        this.service = new AmazonEC2Client(accessKeyId, accessKeySecret, config);

        this.service = new AmazonEC2Mock();
        this.request = new DescribeImagesRequest();
    }

    public boolean verify() {
        try {
            DescribeImagesResponse response = service.describeImages(request);
            if (response.isSetDescribeImagesResult()) {
                DescribeImagesResult describeImagesResult = response.getDescribeImagesResult();
                List<Image> imageList = describeImagesResult.getImage();
                for (Image image : imageList) {
                    if (image.isSetImageId()) {
                        System.out.print("                ImageId");
                        System.out.println();
                        System.out.print("                    " + image.getImageId());
                        System.out.println();
                    }
                    if (image.isSetImageLocation()) {
                        System.out.print("                ImageLocation");
                        System.out.println();
                        System.out.print("                    " + image.getImageLocation());
                        System.out.println();
                    }
                    if (image.isSetImageState()) {
                        System.out.print("                ImageState");
                        System.out.println();
                        System.out.print("                    " + image.getImageState());
                        System.out.println();
                    }
                    if (image.isSetOwnerId()) {
                        System.out.print("                OwnerId");
                        System.out.println();
                        System.out.print("                    " + image.getOwnerId());
                        System.out.println();
                    }
                    if (image.isSetVisibility()) {
                        System.out.print("                Visibility");
                        System.out.println();
                        System.out.print("                    " + image.getVisibility());
                        System.out.println();
                    }
                    List<String> productCodeList  =  image.getProductCode();
                    for (String productCode : productCodeList) {
                        System.out.print("                ProductCode");
                        System.out.println();
                        System.out.print("                    " + productCode);
                    }
                    if (image.isSetArchitecture()) {
                        System.out.print("                Architecture");
                        System.out.println();
                        System.out.print("                    " + image.getArchitecture());
                        System.out.println();
                    }
                    if (image.isSetImageType()) {
                        System.out.print("                ImageType");
                        System.out.println();
                        System.out.print("                    " + image.getImageType());
                        System.out.println();
                    }
                    if (image.isSetKernelId()) {
                        System.out.print("                KernelId");
                        System.out.println();
                        System.out.print("                    " + image.getKernelId());
                        System.out.println();
                    }
                    if (image.isSetRamdiskId()) {
                        System.out.print("                RamdiskId");
                        System.out.println();
                        System.out.print("                    " + image.getRamdiskId());
                        System.out.println();
                    }
                    if (image.isSetPlatform()) {
                        System.out.print("                Platform");
                        System.out.println();
                        System.out.print("                    " + image.getPlatform());
                        System.out.println();
                    }
                }
            }
        } catch (AmazonEC2Exception ex) {
            System.out.println("Caught Exception: " + ex.getMessage());
            System.out.println("Response Status Code: " + ex.getStatusCode());
            System.out.println("Error Code: " + ex.getErrorCode());
            System.out.println("Error Type: " + ex.getErrorType());
            System.out.println("Request ID: " + ex.getRequestId());
            System.out.print("XML: " + ex.getXML());
        }

        return true;
    }
}
