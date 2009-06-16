package com.griddynamics.equestrian.deploy.impl;

import com.griddynamics.equestrian.deploy.StartVirtualMachines;
import com.griddynamics.equestrian.helpers.AmazonKeys;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.LaunchConfiguration;
import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.ReservationDescription;
import com.xerox.amazonws.ec2.ReservationDescription.Instance;

import java.util.List;

/**
 * @author: apanasenko aka dieu
 * Date: 29.04.2009
 * Time: 19:48:35
 */
public class StartVirtualMachinesImpl implements StartVirtualMachines {
    private AmazonKeys aws;
    private ReservationDescription scheduler;
    private ReservationDescription server;
    private ReservationDescription workers;

    public StartVirtualMachinesImpl() {
        scheduler = null;
        server = null;
        workers = null;
    }

    public void create(int nMachines) {
        Jec2 ec2 = new Jec2(aws.getAWSAccessKeyId(), aws.getSecretAccessKey());
        try {
            LaunchConfiguration configuration = new LaunchConfiguration(aws.getSchedulerImageId(), 1, 1);
            scheduler = ec2.runInstances(configuration);
            if(!aws.getSchedulerImageId().equals(aws.getServerImageId())) {
                configuration.setImageId(aws.getServerImageId());
                configuration.setMinCount(1);
                configuration.setMaxCount(1);
                server = ec2.runInstances(configuration);
            }
            configuration.setImageId(aws.getWorkerImageId());
            configuration.setMinCount(nMachines);
            configuration.setMaxCount(nMachines);
            workers = ec2.runInstances(configuration);
        } catch (EC2Exception e) {
            e.printStackTrace();
        }
    }

    public boolean verify() {
        List<Instance> scheduler = this.scheduler.getInstances();
        List<Instance> server = this.server.getInstances();
        List<Instance> workers = this.workers.getInstances();
        for(Instance item: scheduler) {
            if(item.isRunning()) {
                return false;
            }
        }
        if(server != null) {
            for(Instance item: server) {
                if(item.isRunning()) {
                    return false;
                }
            }
        }
        for(Instance item: workers) {
            if(item.isRunning()) {
                return false;
            }
        }
        return true;
    }

    public void setAws(AmazonKeys aws) {
        this.aws = aws;
    }
}
