package core;
/* $Id$ */
/* As of version 0.14 the actual functionality of the crossbreeder dialog is hidden away in this file. It seems like a good
   idea to be seperating functionality from GUI code, something I didn't do when I first started JSynthLib. */
public class CrossBreeder
{
  Patch p;            //the patch we are working on
  PatchBasket library; //the patch library we are working on
  public void generateNewPatch()
  {
  try {
     Patch father=getRandomPatch();
     Patch source;
     byte [] sysex= new byte[father.sysex.length];
     p=new Patch(sysex);
     p.comment=new StringBuffer(); // Clear the wrong "Invalid Manufacturer" comment!
     for (int i=0;i<father.sysex.length;i++)
       {do {source=getRandomPatch(); } while ((source.driverNum!=father.driverNum)||(source.sysex.length<i) || source.deviceNum!=father.deviceNum);
         p.sysex[i]=source.sysex[i];
       }
     p.driverNum=father.driverNum;
     p.deviceNum=father.deviceNum;
     PatchEdit.getDriver(p.deviceNum,p.driverNum).calculateChecksum(p);
     //((Driver)(PatchEdit.DriverList.get(p.driverNum))).calculateChecksum(p);
   }catch (Exception e) {ErrorMsg.reportError("Error", "Source Library Must be Focused",e);}
  }
  public Patch getCurrentPatch()
  {
    return p;
  }
  public void workFromLibrary (PatchBasket lib) 
  {
     library=lib;
  }
   public Patch getRandomPatch()
  {
     int num=((int)(Math.random()*library.getPatchCollection().size()));
     return ((Patch)(library.getPatchCollection().get(num)));
  } 

}
