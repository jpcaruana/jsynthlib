package synthdrivers.KawaiK4;
import core.*;

public class KawaiK4BulkConverter extends Converter {
    public KawaiK4BulkConverter () {
        
        manufacturer="Kawai";
        model="K4/K4r";
        patchType="Bulk Dump Converter";
        
        sysexID="F040**220004**00";
    }
    
    public Patch[] extractPatch (Patch p) {
        // System.out.println("Length p: "+p.sysex.length);
        
        byte[] sx=new byte [8393]; //=131*64+9  Single Bank
        byte[] mx=new byte [77*64+9]; //=77*64+9 Multi Bank
        byte[] ex=new byte [9+32*35]; // Effect Bank
        byte[] dx=new byte [9+682]; // Drumset
        
        System.arraycopy (p.sysex,0,sx,0,8392); // Copy the data into the Single Bank
        
        System.arraycopy (p.sysex,0,mx,0,8);         // Copy the data into the Multi Bank
        System.arraycopy (p.sysex,8392,mx,8,77*64);
        

        System.arraycopy (p.sysex,0,dx,0,8);        // Copy the data into the  drumset
        System.arraycopy (p.sysex,8392+(77*64),dx,8,682); 
        
        System.arraycopy (p.sysex,0,ex,0,8);                     // Copy the data into the Effect Bank
        System.arraycopy (p.sysex,8392+(77*64)+682,ex,8,32*35);
        
        sx[8392]=(byte)0xF7;
        sx[3]=0x21;
        sx[7]=0x00;
        
        mx[77*64+8]=(byte)0xF7;
        mx[3]=0x21;
        mx[7]=0x40;
        
        ex[32*35+8]=(byte)0xf7;
        ex[3]=0x21;
        ex[6]=1;
        
        dx[8+682]=(byte)0xf7;
        dx[3]=0x21;
        dx[6]=1;
        dx[7]=0x20;
        
        Patch[] pf=new Patch[4];
        pf[0]=new Patch (sx);
        pf[1]=new Patch (mx);
        pf[2]=new Patch (ex);
        pf[3]=new Patch (dx);
                
        return pf;
    }
}

