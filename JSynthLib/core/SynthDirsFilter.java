package core;

import java.io.*;

class SynthDirsFilter implements FileFilter
	{
	public SynthDirsFilter ()
		{
		}
	
	public boolean accept(File dir)
		{
		return (dir.isDirectory ());
		}
	}