package core;

import java.io.*;

class SynthFileFilter implements FilenameFilter
{
    public SynthFileFilter ()
    {
    }
    
    public boolean accept (File dir,String name)
    {
        // return ((name.endsWith ("Driver.class")||name.endsWith("Converter.class")) && name.indexOf ('$')==-1);
        return ((name.endsWith ("Device.class")) && (name.indexOf ('$')==-1));
    }
}