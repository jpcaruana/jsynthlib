package com.apple.eawt;

import java.util.EventObject;

/**
 * This is a simple stub for this class which is specific to the 1.4.1
 * virtual machine from Apple. It allows developers to compile the
 * MRJ Adapter library on platforms other than Mac OS X with the
 * 1.4.1 VM. You don't need to use these stubs if you're using the
 * precompiled version of MRJ Adapter.
 *
 * @author Steve Roy
 */
public class ApplicationEvent extends EventObject
{
	ApplicationEvent(Object source)
	{
		super(source);
	}
	
	public String getFilename()
	{
		return null;
	}
	
	public boolean isHandled()
	{
		return false;
	}
	
	public void setHandled(boolean handled)
	{
	}
}
