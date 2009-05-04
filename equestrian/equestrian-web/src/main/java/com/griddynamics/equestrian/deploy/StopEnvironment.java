package com.griddynamics.equestrian.deploy;

/**
 * @author: apanasenko aka dieu
 * Date: 30.04.2009
 * Time: 14:23:01
 */
public interface StopEnvironment {
	StopApplication getApplication();
	StopVirtualMachines getVirtualMachines();
}
