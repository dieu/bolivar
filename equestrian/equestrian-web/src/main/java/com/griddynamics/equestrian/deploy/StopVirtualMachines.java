package com.griddynamics.equestrian.deploy;

/**
 * @author: apanasenko aka dieu
 * Date: 30.04.2009
 * Time: 14:23:26
 */
public interface StopVirtualMachines {
	void stop();
    void stopNode(String nodeIp);
	boolean verify();
}
