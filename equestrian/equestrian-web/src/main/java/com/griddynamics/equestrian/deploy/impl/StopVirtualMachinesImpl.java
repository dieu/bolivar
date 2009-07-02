package com.griddynamics.equestrian.deploy.impl;

import com.griddynamics.equestrian.deploy.StopVirtualMachines;
import com.griddynamics.equestrian.helpers.AmazonKeys;
import com.xerox.amazonws.ec2.Jec2;
import com.xerox.amazonws.ec2.ReservationDescription;
import com.xerox.amazonws.ec2.EC2Exception;
import com.xerox.amazonws.ec2.TerminatingInstanceDescription;
import com.xerox.amazonws.typica.jaxb.TerminateInstances;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author: apanasenko aka dieu
 * Date: 30.04.2009
 * Time: 14:25:55
 */
public class StopVirtualMachinesImpl implements StopVirtualMachines{
    private AmazonKeys aws;

    public StopVirtualMachinesImpl() {
    }

    public void setAws(AmazonKeys aws) {
        this.aws = aws;
    }

    public void stop() {
        try {
            Jec2 ec2 = new Jec2(aws.getAWSAccessKeyId(), aws.getSecretAccessKey());
            List<String> terminateInstances = monitoringInstance(ec2);
            ec2.terminateInstances(terminateInstances);
            while(true) {
                terminateInstances = monitoringInstance(ec2);
                if(terminateInstances.size() == 0) {
                    return;
                } else {
                    Thread.sleep(30000L);
                }
            }
        } catch (EC2Exception e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopNode(String nodeIp) {
        if(!nodeIp.equals("")) {
            try {
                Jec2 ec2 = new Jec2(aws.getAWSAccessKeyId(), aws.getSecretAccessKey());
                List<String> params = new ArrayList<String>();
                List<ReservationDescription> instances = ec2.describeInstances(params);
                for (ReservationDescription res : instances) {
                    if (res.getInstances() != null) {
                        for (ReservationDescription.Instance inst : res.getInstances()) {
                            if(inst.isRunning() && inst.getPrivateDnsName().startsWith(nodeIp)) {
                                ec2.terminateInstances(new ArrayList<String>(Arrays.asList(inst.getInstanceId())));
                            }
                        }
                    }
                }
            } catch (EC2Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean verify() {
        return true;
    }

    private List<String> monitoringInstance(Jec2 ec2) throws EC2Exception {
        List<String> terminateInstances = new ArrayList<String>();
        List<String> params = new ArrayList<String>();
        List<ReservationDescription> instances = ec2.describeInstances(params);
        for (ReservationDescription res : instances) {
            if (res.getInstances() != null) {
                for (ReservationDescription.Instance inst : res.getInstances()) {
                    if(inst.isRunning() && (aws.getUserId().equals("") || inst.getKeyName().equals(aws.getUserId()))) {
                        if(inst.getImageId().equals(aws.getWorkerImageId())) {
                            if(inst.isRunning()) {
                                terminateInstances.add(inst.getInstanceId());
                            }
                        }
                        if(inst.getImageId().equals(aws.getServerImageId())) {
                            if(inst.isRunning()) {
                                terminateInstances.add(inst.getInstanceId());
                            }
                        }
                        if(inst.getImageId().equals(aws.getSchedulerImageId())) {
                            if(inst.isRunning()) {
                                terminateInstances.add(inst.getInstanceId());
                            }
                        }
                    }
                }
            }
        }
        return terminateInstances;
    }
}
