package com.apple.eawt;

import java.awt.Point;

/**
 * This is a simple stub for this class which is specific to the 1.4.1
 * virtual machine from Apple. It allows developers to compile the
 * MRJ Adapter library on platforms other than Mac OS X with the
 * 1.4.1 VM. You don't need to use these stubs if you're using the
 * precompiled version of MRJ Adapter.
 *
 * @author Steve Roy
 */
public class Application
{
	public Application()
	{
	}
	
	public void addApplicationListener(ApplicationListener l)
	{
	}
	
	public void removeApplicationListener(ApplicationListener l)
	{
	}
	
	public boolean getEnabledPreferencesMenu()
	{
		return false;
	}
	
	public void setEnabledPreferencesMenu(boolean enabled)
	{
	}
	
	public static Point getMouseLocationOnScreen()
	{
		return null;
	}
}
