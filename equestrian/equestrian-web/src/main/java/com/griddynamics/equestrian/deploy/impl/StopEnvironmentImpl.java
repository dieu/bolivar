package com.griddynamics.equestrian.deploy.impl;

import com.griddynamics.equestrian.deploy.StopEnvironment;
import com.griddynamics.equestrian.deploy.StopApplication;
import com.griddynamics.equestrian.deploy.StopVirtualMachines;

/**
 * @author: apanasenko aka dieu
 * Date: 30.04.2009
 * Time: 14:27:15
 */
public class StopEnvironmentImpl implements StopEnvironment{
	private StopApplication application;
	private StopVirtualMachines virtualMachines;

	public StopApplication getApplication() {
		return application;
	}

	public void setApplication(StopApplication application) {
		this.application = application;
	}

	public StopVirtualMachines getVirtualMachines() {
		return virtualMachines;
	}

	public void setVirtualMachines(StopVirtualMachines virtualMachines) {
		this.virtualMachines = virtualMachines;
	}
}
