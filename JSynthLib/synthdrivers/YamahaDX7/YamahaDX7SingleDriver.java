/*
 * JSynthlib-SingleDriver for Yamaha DX7 Mark-I (with Firmware IG114690)
 * =====================================================================
 * @author  Torsten Tittmann
 * email:   Torsten.Tittmann@t-online.de
 * file:    YamahaDX7SingleDriver.java
 * date:    15.01.2002
 * @version 0.1
 */

package synthdrivers.YamahaDX7;
import core.*;
import javax.swing.*;

public class YamahaDX7SingleDriver extends Driver
{

  public YamahaDX7SingleDriver()
  {
    manufacturer="Yamaha";
    model="DX7";
    patchType="Single";
    id="DX7";
    sysexID= "F0430*00011B";
    authors="Torsten Tittmann";
    // inquiryID= NONE ;         // The DX7-Mark I didn't know dump request!
    patchNameStart=151;
    patchNameSize=10;
    deviceIDoffset=2;
    checksumOffset=161;
    checksumStart=6;
    checksumEnd=160;
    patchNumbers=new String[] {"I01","I02","I03","I04","I05","I06","I07","I08",
                               "I09","I10","I11","I12","I13","I14","I15","I16",
                               "I17","I18","I19","I20","I21","I22","I23","I24",
                               "I25","I26","I27","I28","I29","I30","I31","I32"};
    bankNumbers =new String[] {"0-Internal"};
    patchSize=163;
    // numSysexMsgs = 1;         
  }

  public void storePatch (Patch p, int bankNum,int patchNum)
  {
    setPatchNum(patchNum);
    sendPatch(p);

    JOptionPane.showMessageDialog(PatchEdit.instance,
      "The patch has been placed in the edit buffer!\n\nYou must now hold the 'STORE' button on the DX7\nand choose a location (1-32) to store the patch.\n\n(Since the DX7 itself makes no distinction between\nthe 'send'- and 'store'-method of JSynthlib,\nyou can store your patch in the same way\nwith the 'send'-Method, still without the\nPatch-Location-Chooser!)",
      getDriverName()+" Driver",
      JOptionPane.WARNING_MESSAGE
    );
  }


  public void requestPatchDump(int bankNum, int patchNum)
  {
      JOptionPane.showMessageDialog(PatchEdit.instance,
        "The " + getDriverName() + " driver does not support patch getting.\n\nPlease start the patch dump manually, when you have prepared\nyour DX7 to send a patch!\n(Memory Protect Off & Sys Info Available)",
        "Get "+getDriverName()+" Patch",
        JOptionPane.WARNING_MESSAGE
      );
  }




  public Patch createNewPatch()                   // the new Patch has the same parameters as a DX7-created "init voice"
  {
    byte [] init_voice = {
    -16,67,0,0,1,27,99,99,99,99,99,99,99,0,0,0,0,0,0,0,
    0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,0,0,0,0,
    0,0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,0,0,0,
    0,0,0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,0,0,
    0,0,0,0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,0,
    0,0,0,0,0,0,0,0,1,0,7,99,99,99,99,99,99,99,0,0,
    0,0,0,0,0,0,0,99,0,1,0,7,99,99,99,99,50,50,50,50,
    0,0,1,35,0,0,0,1,0,3,24,73,78,73,84,32,86,79,73,67,
    69,81,-9};

    Patch p = new Patch(init_voice);
    p.ChooseDriver();

    return p;
  }


  public JInternalFrame editPatch(Patch p)
  {
    return new YamahaDX7SingleEditor(p);
  }

}
