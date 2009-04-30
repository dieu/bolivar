package com.griddynamics.equestrian.deploy.impl;

import com.griddynamics.equestrian.deploy.StartEnvironment;
import com.griddynamics.equestrian.deploy.StartApplication;
import com.griddynamics.equestrian.deploy.StartDependencies;
import com.griddynamics.equestrian.deploy.StartVirtualMachines;

/**
 * @author: apanasenko aka dieu
 * Date: 29.04.2009
 * Time: 19:49:19
 */
public class StartEnvironmentImpl implements StartEnvironment{
	private StartApplication application;
	private StartDependencies dependencies;
	private StartVirtualMachines virtualMachines;

	public void setApplication(StartApplication application) {
		this.application = application;
	}

	public void setDependencies(StartDependencies dependencies) {
		this.dependencies = dependencies;
	}

	public void setVirtualMachines(StartVirtualMachines virtualMachines) {
		this.virtualMachines = virtualMachines;
	}

	public StartApplication getApplication() {
		return this.application;
	}

	public StartDependencies getDependecies() {
		return this.dependencies;
	}

	public StartVirtualMachines getVirtualMachines() {
		return this.virtualMachines;
	}
}
