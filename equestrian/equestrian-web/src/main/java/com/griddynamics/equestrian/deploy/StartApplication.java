package com.griddynamics.equestrian.deploy;

/**
 * @author: apanasenko aka dieu
 * Date: 29.04.2009
 * Time: 19:42:06
 */
public interface StartApplication<T> {
	void deploy(int n);
	void start();
	T verify();
}
