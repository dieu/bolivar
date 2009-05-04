package com.griddynamics.equestrian.deploy;

/**
 * @author: apanasenko aka dieu
 * Date: 29.04.2009
 * Time: 19:03:27
 */
public interface StartVirtualMachines {
	void create(int nMachines);
	boolean verify();
}
