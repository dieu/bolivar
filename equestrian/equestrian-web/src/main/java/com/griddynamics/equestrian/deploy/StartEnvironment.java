package com.griddynamics.equestrian.deploy;

/**
 * @author: apanasenko aka dieu
 * Date: 29.04.2009
 * Time: 18:59:12
 */
public interface StartEnvironment {
	StartApplication getApplication();
	StartDependencies getDependecies();
	StartVirtualMachines getVirtualMachines();
}
