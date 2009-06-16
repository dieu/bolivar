package com.griddynamics.equestrian.deploy.impl;

import com.griddynamics.equestrian.deploy.StartVirtualMachines;
import com.griddynamics.equestrian.helpers.AmazonKeys;
import com.xerox.amazonws.ec2.*;
import com.xerox.amazonws.ec2.ReservationDescription.Instance;

import java.util.List;
import java.util.ArrayList;

/**
 * @author: apanasenko aka dieu
 * Date: 29.04.2009
 * Time: 19:48:35
 */
public class StartVirtualMachinesImpl implements StartVirtualMachines {
    private AmazonKeys aws;

    public StartVirtualMachinesImpl() {
    }

    public void create(int countWorkers, int countScheduler, int countServer) {
        Jec2 ec2 = new Jec2(aws.getAWSAccessKeyId(), aws.getSecretAccessKey());
        try {
            LaunchConfiguration configuration = new LaunchConfiguration(aws.getSchedulerImageId(), 1, 1);
            configuration.setInstanceType(InstanceType.LARGE);
            if(countScheduler != 0) {
                ec2.runInstances(configuration);
            }
            if(!aws.getSchedulerImageId().equals(aws.getServerImageId()) && countServer != 0) {
                configuration.setImageId(aws.getServerImageId());
                configuration.setMinCount(1);
                configuration.setMaxCount(1);
                configuration.setInstanceType(InstanceType.LARGE);
                ec2.runInstances(configuration);
            }
            configuration.setImageId(aws.getWorkerImageId());
            configuration.setMinCount(countWorkers);
            configuration.setMaxCount(countWorkers);
            configuration.setInstanceType(InstanceType.LARGE);
            ec2.runInstances(configuration);
        } catch (EC2Exception e) {
            e.printStackTrace();
        }
    }

    public boolean verify() {
        try {
            Thread.sleep(15000L);
            Jec2 ec2 = new Jec2(aws.getAWSAccessKeyId(), aws.getSecretAccessKey());
            List<String> params = new ArrayList<String>();
            List<ReservationDescription> instances = ec2.describeInstances(params);
            for (ReservationDescription res : instances) {
                if (res.getInstances() != null) {
                    for (ReservationDescription.Instance inst : res.getInstances()) {
                        if(inst.isRunning() && (aws.getUserId().equals("") || inst.getKeyName().equals(aws.getUserId()))) {
                            if(inst.getImageId().equals(aws.getWorkerImageId())) {
                                if(!inst.isRunning()) {
                                    return  false;
                                }
                            }
                            if(inst.getImageId().equals(aws.getServerImageId())) {
                                if(!inst.isRunning()) {
                                    return  false;
                                }
                            }
                            if(inst.getImageId().equals(aws.getSchedulerImageId())) {
                                if(!inst.isRunning()) {
                                    return  false;
                                }
                            }
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (EC2Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void setAws(AmazonKeys aws) {
        this.aws = aws;
    }
}
