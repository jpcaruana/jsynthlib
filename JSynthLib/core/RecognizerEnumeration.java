/*
 * RecognizerVector.java
 *
 * Created on 19. Oktober 2000, 22:32
 */

package core;

import java.util.Vector;
import java.io.File;
import java.security.*;
import java.util.Enumeration;

/**
 *
 * @author Gerrit Gehnen <Gerrit.Gehnen@gmx.de>
 * @version 0.2
 */
public class RecognizerEnumeration extends Object
{
    static  Vector synthis;
    private Enumeration enum;
    
    static
    {
        MyClassLoader loader;
        File synthdevicesDir =new File ("synthdrivers");
        File synthDirs[]=synthdevicesDir.listFiles (new SynthDirsFilter ());
        synthis=new Vector ();
        
        for (int i=0;i<synthDirs.length ;i++) // for all subdirectories = synthesizer models
        {
            File actSynthDir= new File ("synthdrivers",synthDirs[i].getName ());
            String synthDevices[]=actSynthDir.list (new SynthFileFilter ());
            try
            {
                loader=new MyClassLoader (actSynthDir.getPath ());
                for (int j=0;j<synthDevices.length ;j++)
                {
                    // Remove the ".class" from the list of files
                    synthDevices[j]=synthDevices[j].substring (0,synthDevices[j].indexOf ('.'));
                    try
                    {
                        // Relict of the Pre-Device era
                     /*
                   if (synthDevives[j].indexOf ("Converter")>0)
                      synthis.add (0,(loader.loadClass
                         (synthDrivers[j],true)).newInstance ());
                      else
                      */
                        synthis.add ((loader.loadClass (synthDevices[j],true)).newInstance ());
                        // System.out.println ("Synth Added "+synthDrivers[j]);
                    }
                    catch (Exception e)
                    {
                        ErrorMsg.reportStatus (e);
                    }
                }
            } catch (Exception e)
            {
                ErrorMsg.reportStatus (e);
            }
        }
    }
    
    /** Creates new RecognizerVector */
    public RecognizerEnumeration ()
    {
        enum=synthis.elements ();
    }
    /** Resets the Enumeration-Pointer to the first element
     */
    public void reset ()
    {
        
        enum=synthis.elements ();
    }
    public boolean hasMoreElements ()
    {
        return enum.hasMoreElements ();
    }
    
    public Device nextElement ()
    {
        return (Device) enum.nextElement ();
    }
    
}
