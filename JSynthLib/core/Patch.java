/* 
 * $Id$ 
 */

package core;
import java.io.*;
import java.awt.datatransfer.*;

public class Patch extends Object implements Serializable,Transferable
{
    public StringBuffer  comment ;
    public StringBuffer  date ;
    public StringBuffer  author;
    public transient int driverNum;
    public transient int deviceNum;
    public   byte[]  sysex ;
    
    static final long serialVersionUID = 2220769917598497681L;
    
    public Patch ()
    {
        comment = new StringBuffer ();
        date= new StringBuffer ();
        author= new StringBuffer ();
        sysex = new byte [1024];
        ChooseDriver ();
    }
    
    public Patch (byte[] gsysex)
    {
        comment = new StringBuffer ();
        date= new StringBuffer ();
        author= new StringBuffer ();
        sysex= gsysex;
        ChooseDriver ();
    }
    
    public Patch (byte[] gsysex,int offset)
 {
         comment = new StringBuffer ();
        date= new StringBuffer ();
        author= new StringBuffer ();
        sysex=new byte[gsysex.length-offset];
        System.arraycopy(gsysex,offset,sysex,0,gsysex.length-offset);
        ChooseDriver ();
    }
    
    public Patch (byte[] gsysex, String gdate, String gauthor, String gcomment)
    {
        this.comment = new StringBuffer (gcomment);
        this.date= new StringBuffer (gdate);
        this.author= new StringBuffer (gauthor);
        sysex = new byte [1024];
        this.sysex= gsysex;
        ChooseDriver ();
    }
    
    
    public void ChooseDriver ()
    {
        Device dev;
        driverNum=0;
        deviceNum=0;
        Integer intg=new Integer (0);
        StringBuffer patchString= new StringBuffer ("F0");
        for (int i=1;i<16;i++)
        {
            if (sysex.length>i) {
                if (sysex[i]<16) 
                    patchString.append ("0");
               patchString.append (intg.toHexString (sysex[i]&0xff));
            }
        StringBuffer driverString=new StringBuffer ();
        for ( int i =0;i<PatchEdit.deviceList.size (); i++)
        {
            // Outer Loop, iterating over all installed devices
            dev=(Device)PatchEdit.deviceList.get (i);
            for (int j=0;j<dev.driverList.size ();j++)
            {
                // Inner Loop, iterating over all Drivers of a device
                if (((Driver)dev.driverList.get (j)).supportsPatch (patchString,this))
                {
                    driverNum=j;
                    deviceNum=i;
                    getDriver().trimSysex(this);
                    return;
                }
            }
        }
        // Unkown patch, try to guess at least the manufacturer
        comment=new StringBuffer("Probably a "+LookupManufacturer.get(sysex[1],sysex[2],sysex[3])+" Patch");
        
    }
    
    public java.lang.Object getTransferData (java.awt.datatransfer.DataFlavor p1) throws java.awt.datatransfer.UnsupportedFlavorException, java.io.IOException
    {
        return this;
    }
    
    public boolean isDataFlavorSupported (final java.awt.datatransfer.DataFlavor p1)
    {
        //      System.out.println ("isDataFlavorSupported "+driverNum);
        if( p1.equals (
        new DataFlavor (getDriver().getClass (),getDriver().toString ())
        //new DataFlavor (((Device)PatchEdit.deviceList.get (deviceNum)).driverList.get (driverNum).getClass (),((Device)PatchEdit.deviceList.get (driverNum)).driverList.get (driverNum).toString ())
        ) )
        {
            return true;
        }
        return false;
    }
    
    public java.awt.datatransfer.DataFlavor[] getTransferDataFlavors ()
    {
        //      System.out.println ("getTransferDataFlavors "+driverNum);
        DataFlavor[] df=new DataFlavor[1];
//        df[0]= new DataFlavor (((Device)PatchEdit.deviceList.get (deviceNum)).driverList.get (driverNum).getClass (),((Device)PatchEdit.deviceList.get (driverNum)).driverList.get (driverNum).toString ());
          df[0]= new DataFlavor (getDriver().getClass (),getDriver().toString ());
        
        return df;
    }
    
    public Patch[] dissect ()
    {
        Device dev;
        int looplength;
        Patch[] patarray=null;
        StringBuffer patchString= new StringBuffer ("F0");
        for (int k=1;k<16;k++)
        {
            if (sysex[k]<16) patchString.append ("0");
            patchString.append (Integer.toHexString (sysex[k]));
        }
        
        for (int k=0;k<PatchEdit.deviceList.size ();k++)
        { // Do it for all converters. They should be at the beginning of the driver list!
            dev=(Device)PatchEdit.deviceList.get (k);
            for (int j=0;j<dev.driverList.size ();j++)
            {
                if (!(dev.driverList.get (j) instanceof Converter))
                    continue;
                if (!((Driver)(dev.driverList.get (j))).supportsPatch (patchString,this))
                { // Try, until one converter was successfull
                    continue;
                }
                patarray=((Converter)(dev.driverList.get (j))).extractPatch (this);
                if (patarray!=null)
                {
                    break;
                }
            }
            //    k++;
        }
        if (patarray!=null)
        { // Conversion was sucessfull, we have at least one converted patch
            looplength=patarray.length;
        }
        else
        { // No conversion. Try just the original patch....
            looplength=1;
            patarray=new Patch[1];
            patarray[0]=this;
        }
        return patarray;
    }
public Driver getDriver()
 {return PatchEdit.getDriver(deviceNum,driverNum);}    
}

