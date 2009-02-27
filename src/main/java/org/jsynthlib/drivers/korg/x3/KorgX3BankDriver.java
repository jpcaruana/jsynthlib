package org.jsynthlib.drivers.korg.x3;
import java.io.UnsupportedEncodingException;

import javax.swing.JOptionPane;

import org.jsynthlib.core.BankDriver;
import org.jsynthlib.core.ErrorMsg;
import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexHandler;

/**
 * This class is a bank driver for Korg X3 -synthesizer to be used in
 * JSynthLib-program. Might work directly with Korg X2 as well.
 * Making drivers for N-series (N264, N364) should be an easy
 * task if one has the original reference guide.
 *
 * @author  Juha Tukkinen
 * @version $Id$
 */
public class KorgX3BankDriver extends BankDriver
{

  /** Unneeded header data */
  public static final int EXTRA_HEADER=6;

  /**
   * Default constructor. Initialize default values for
   * class variables.
   */
  public KorgX3BankDriver()
  {
    super ("Bank","Juha Tukkinen",200,4);
    sysexID="F042**35";
    // all program parameter dump request:
    sysexRequestDump=new SysexHandler("F0 42 30 35 1C 00 F7"); 
    
    deviceIDoffset=2; //device id = midi global channel
    bankNumbers = new String[]
      {"Program A+B"};
    patchNumbers=new String[]
      {"A00-","A01-","A02-","A03-","A04-","A05-","A06-","A07-",
       "A08-","A09-","A10-","A11-","A12-","A13-","A14-","A15-",
       "A16-","A17-","A18-","A19-","A20-","A21-","A22-","A23-",
       "A24-","A25-","A26-","A27-","A28-","A29-","A30-","A31-",
       "A32-","A33-","A34-","A35-","A36-","A37-","A38-","A39-",
       "A40-","A41-","A42-","A43-","A44-","A45-","A46-","A47-",
       "A48-","A49-","A50-","A51-","A52-","A53-","A54-","A55-",
       "A56-","A57-","A58-","A59-","A60-","A61-","A62-","A63-",
       "A64-","A65-","A66-","A67-","A68-","A69-","A70-","A71-",
       "A72-","A73-","A74-","A75-","A76-","A77-","A78-","A79-",
       "A80-","A81-","A82-","A83-","A84-","A85-","A86-","A87-",
       "A88-","A89-","A90-","A91-","A92-","A93-","A94-","A95-",
       "A96-","A97-","A98-","A99-",
       "B00-","B01-","B02-","B03-","B04-","B05-","B06-","B07-",
       "B08-","B09-","B10-","B11-","B12-","B13-","B14-","B15-",
       "B16-","B17-","B18-","B19-","B20-","B21-","B22-","B23-",
       "B24-","B25-","B26-","B27-","B28-","B29-","B30-","B31-",
       "B32-","B33-","B34-","B35-","B36-","B37-","B38-","B39-",
       "B40-","B41-","B42-","B43-","B44-","B45-","B46-","B47-",
       "B48-","B49-","B50-","B51-","B52-","B53-","B54-","B55-",
       "B56-","B57-","B58-","B59-","B60-","B61-","B62-","B63-",
       "B64-","B65-","B66-","B67-","B68-","B69-","B70-","B71-",
       "B72-","B73-","B74-","B75-","B76-","B77-","B78-","B79-",
       "B80-","B81-","B82-","B83-","B84-","B85-","B86-","B87-",
       "B88-","B89-","B90-","B91-","B92-","B93-","B94-","B95-",
       "B96-","B97-","B98-","B99-"};

     
    singleSysexID="F042**35";
    // size after conversion:
    patchSize=37600+EXTRA_HEADER; //164*200Byte = 7*4685+5 -> 8*4685+(1+5) = 37486Byte
  }
  
  /** 
   * Returns the index of the selected patch
   * 
   * @param patchNum Patch number
   * @return Index of the patch
   */
  public int getPatchStart(int patchNum)
  {
    int start=(164*patchNum);
    start+=EXTRA_HEADER;  //sysex header
    return start;
  }

  /**
   * Gets the patch name
   *
   * @param p Patch
   * @param patchNum Patch number
   * @return Patch name
   */
  public String getPatchName(Patch p,int patchNum) {
    int nameStart=getPatchStart(patchNum);
    nameStart+=0; //offset of name in patch data
    try {
      StringBuffer s= new StringBuffer(new String(((Patch)p).sysex,nameStart,
						  10,"US-ASCII"));
      return s.toString();
    } catch (UnsupportedEncodingException ex) {
      return "-";
    }   
  }

  /**
   * Sets the patch name
   *
   * @param p Patch
   * @param patchNum Patch number
   * @param name Name
   */
  public void setPatchName(Patch p,int patchNum, String name)
  {
    patchNameSize=10;
    patchNameStart=getPatchStart(patchNum);
    
    if (name.length()<patchNameSize) name=name+"            ";
    byte [] namebytes = new byte [64];
    try {
      namebytes=name.getBytes("US-ASCII");
      for (int i=0;i<patchNameSize;i++)
	((Patch)p).sysex[patchNameStart+i]=namebytes[i];
      
    } catch (UnsupportedEncodingException ex) {return;}
  }
 
  /**
   * Not used in KorgX3.
   */
//  protected static void calculateChecksum(Patch p,int start,int end,int ofs)
//  {
//  }

  /**
   * Not used in KorgX3.
   */
  public void calculateChecksum (Patch p)
  {
  }                                     

  /**
   * Puts a single patch to bank.
   *
   * @param bank Bank
   * @param p Patch
   * @param patchNum Patch number in bank
   */
  public void putPatch(Patch bank,Patch p,int patchNum)
  { 
    if (!canHoldPatch(p)) {
      JOptionPane.showMessageDialog(null, "This type of patch does not fit in to this type of bank.","Error", JOptionPane.ERROR_MESSAGE); 
      return;
    }
                
    System.arraycopy(((Patch)p).sysex,KorgX3SingleDriver.EXTRA_HEADER,((Patch)bank).sysex,getPatchStart(patchNum),164);
  }

  /**
   * Gets a single patch from bank.
   *
   * @param bank Bank
   * @param patchNum Patch number
   * @return Single patch
   */
  public Patch getPatch(Patch bank, int patchNum)
  {
    try {
      byte [] sysex=new byte[187];
      sysex[00]=(byte)0xF0;sysex[01]=(byte)0x42;sysex[02]=(byte)0x30;
      sysex[03]=(byte)0x35;sysex[186]=(byte)0xF7;
      
      System.arraycopy(((Patch)bank).sysex,getPatchStart(patchNum),sysex,KorgX3SingleDriver.EXTRA_HEADER,164);
      return new Patch(sysex);
    }catch (Exception e) {ErrorMsg.reportError("Error","Error in Korg X3 Bank Driver",e);return null;}
  }
   
  /**
   * Not implemented as not needed.
   */
  public Patch createNewPatch()
  {
    JOptionPane.showMessageDialog(null, "Creating a new Bank is not possible.","Error", JOptionPane.ERROR_MESSAGE);
    return null;
  }
   
}
