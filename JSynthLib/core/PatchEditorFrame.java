/* $Id$ */
package core;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.event.*;
import java.io.*;
import javax.swing.border.*;
public class PatchEditorFrame extends JInternalFrame implements PatchBasket
{
  public  Patch p;         //this is the patch we are working on
  final byte [] originalPatch = new byte[32000]; // this is a copy of the patch when we started editing (in case user wants to revert)
                 //i hope this is big enough, it really should be dynamically allocated to the size of p.sysex, but cannot be due
		 // to a design "feature" in Java
  public GridBagLayout layout;
  public GridBagConstraints gbc;
  public JPanel scrollPane;
  public ArrayList sliderList = new ArrayList(); //workaround for Java Swing Bug
  public ArrayList widgetList = new ArrayList();
  public SysexWidget recentWidget;
  public int faderBank;
  public int numFaderBanks;
  public byte lastFader;
  public JScrollPane scroller;
  //information about BankEditorFrame which created this PatchEditor frame
  //(if applicable) so we can update that frame with the edited data on close
  public BankEditorFrame bankFrame=null;
  int patchRow; int patchCol;  //which patch in bank we're editing

  public PatchEditorFrame(String name,Patch patch)
  { super (name,true,true,true,true);
   p=patch;
    System.arraycopy(p.sysex,0,originalPatch,0,p.sysex.length);   //make backup copy
    layout=new GridBagLayout();
    gbc = new GridBagConstraints();
    scrollPane=new JPanel();
    scrollPane.setLayout(layout);
    scrollPane.setSize(600,400); 
    scroller = new JScrollPane (scrollPane);
    getContentPane().add(scroller);
    setSize(600,400);
    scroller.getVerticalScrollBar().addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {
                    repaint();
                }            
        });
    scroller.getHorizontalScrollBar().addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {}
            public void mouseReleased(MouseEvent e) {
                    repaint();
                }            
        });
    addInternalFrameListener(new InternalFrameListener() {
            public void internalFrameClosing(InternalFrameEvent e) 
	        {
		String choices[] = new String [] {"Keep Changes", "Revert to Original", "Place Changed Version on Clipboard"};
		   int choice=JOptionPane.CLOSED_OPTION;
		   while (choice==JOptionPane.CLOSED_OPTION)
		    choice = JOptionPane.showOptionDialog((Component)null, "What do you wish to do with the changed copy of the Patch?",
		      "Save Changes?",JOptionPane.OK_OPTION,JOptionPane.QUESTION_MESSAGE,null,choices,choices[0]);
 		      if (choice==0)
                        {
                        if (bankFrame==null) return;
                        bankFrame.myModel.setPatchAt(p,patchRow,patchCol);
                        return;
                        }
		      if (choice==2) CopySelectedPatch();    //put on clipboard but don't 'return' just yet	             
		     System.arraycopy(originalPatch,0,p.sysex,0,p.sysex.length);   //restore backup
  	    
		}
	     public void internalFrameOpened(InternalFrameEvent e) {};
             public void internalFrameActivated(InternalFrameEvent e) 
	     {((Driver)(PatchEdit.getDriver(p.deviceNum,p.driverNum))).calculateChecksum(p);
	      ((Driver)(PatchEdit.getDriver(p.deviceNum,p.driverNum))).sendPatch(p);
               gotFocus();
	       PatchEdit.receiveAction.setEnabled (false);
               PatchEdit.pasteAction.setEnabled (false);
               PatchEdit.sendAction.setEnabled (true);
               PatchEdit.sendToAction.setEnabled (true);
               PatchEdit.playAction.setEnabled (true);
               PatchEdit.storeAction.setEnabled (true);
               PatchEdit.copyAction.setEnabled (true);
               PatchEdit.reassignAction.setEnabled (true);
               PatchEdit.exportAction.setEnabled (true);
	     
	     };
	     public void internalFrameClosed(InternalFrameEvent e) {};
	     public void internalFrameDeactivated(InternalFrameEvent e) {
	       PatchEdit.sendAction.setEnabled (false);
               PatchEdit.playAction.setEnabled (false);
               PatchEdit.storeAction.setEnabled (false);
               PatchEdit.sendToAction.setEnabled (false);
               PatchEdit.copyAction.setEnabled (false);
               PatchEdit.reassignAction.setEnabled (false);
               PatchEdit.exportAction.setEnabled (false);
               lostFocus();

	     };
	     public void internalFrameDeiconified(InternalFrameEvent e) {};
	     public void internalFrameIconified(InternalFrameEvent e) {};
		
	});
  
  
  }
 public ArrayList getPatchCollection() {return null;}
 public void ImportPatch (File file) throws IOException,FileNotFoundException {}
 public void ExportPatch (File file) throws IOException,FileNotFoundException {};
 public void DeleteSelectedPatch() {}
 public void CopySelectedPatch() 
 {
  try{
  byte [] mySysex = new byte[p.sysex.length];
    System.arraycopy(p.sysex,0,mySysex,0,p.sysex.length);
    PatchEdit.Clipboard=new Patch(mySysex,
                                  (p.date.toString()),
                                  (p.author.toString()),
                                  (p.comment.toString()));     
 }catch (Exception e) {};
 };
 public void SendSelectedPatch()
         {
 	  ((Driver)(PatchEdit.getDriver(p.deviceNum,p.driverNum))).calculateChecksum(p);
 	  ((Driver)(PatchEdit.getDriver(p.deviceNum,p.driverNum))).sendPatch(p);
 	};
  public void SendToSelectedPatch()
         {
 	  ((Driver)(PatchEdit.getDriver(p.deviceNum,p.driverNum))).calculateChecksum(p);
 	  new SysexSendToDialog(p);
 	};
  public void ReassignSelectedPatch()
         {
 	  ((Driver)(PatchEdit.getDriver(p.deviceNum,p.driverNum))).calculateChecksum(p);
 	  new ReassignPatchDialog(p);
 	};
 public void PlaySelectedPatch()
         {
         ((Driver)(PatchEdit.getDriver(p.deviceNum,p.driverNum))).calculateChecksum(p);
	 ((Driver)(PatchEdit.getDriver(p.deviceNum,p.driverNum))).sendPatch(p);
         ((Driver)(PatchEdit.getDriver(p.deviceNum,p.driverNum))).playPatch(p);
	};
 public void StoreSelectedPatch(){};
 public JInternalFrame EditSelectedPatch(){return null;};
 public void PastePatch() {}

 public void addWidget(SysexWidget widget, int gridx, int gridy, int gridwidth, int gridheight,int slidernum)
  {
	  try {
  gbc.fill=gbc.BOTH;
  gbc.anchor=gbc.EAST;
  gbc.gridx=gridx;
    gbc.gridy=gridy;
    gbc.gridwidth=gridwidth;
    gbc.gridheight=gridheight;
    scrollPane.add (widget,gbc);
    widget.setSliderNum(slidernum);
    if (widget instanceof ScrollBarWidget) sliderList.add(((ScrollBarWidget)widget).slider);
     if (widget instanceof VertScrollBarWidget) sliderList.add(((VertScrollBarWidget)widget).slider);
     if (widget instanceof ScrollBarLookupWidget) sliderList.add(((ScrollBarLookupWidget)widget).slider);
    widgetList.add(widget);
 
   }catch (Exception e) {ErrorMsg.reportStatus(e);}     
	  }
  public void addWidget(JComponent parent,SysexWidget widget, int gridx, int gridy, int gridwidth, int gridheight,int slidernum)
  {
	  try {
  gbc.fill=gbc.HORIZONTAL;
  gbc.anchor=gbc.NORTHEAST;
  gbc.gridx=gridx;
    gbc.gridy=gridy;
    gbc.gridwidth=gridwidth;
    gbc.gridheight=gridheight;
    parent.add (widget,gbc);
    widget.setSliderNum(slidernum);

   if (widget instanceof ScrollBarWidget) sliderList.add(((ScrollBarWidget)widget).slider);
     if (widget instanceof VertScrollBarWidget) sliderList.add(((VertScrollBarWidget)widget).slider);
     if (widget instanceof ScrollBarLookupWidget) sliderList.add(((ScrollBarLookupWidget)widget).slider);
    widgetList.add(widget);
   
  }catch (Exception e) {ErrorMsg.reportStatus(e);}     
	  }

public void faderMoved(byte fader,byte value)
  {
    SysexWidget w;
    if (fader==32) {faderBank=(faderBank+1)% numFaderBanks; faderHighlight();return;}
    if (fader==31)  {faderBank=faderBank-1; if (faderBank<0) faderBank=numFaderBanks-1;faderHighlight(); return;}
    if (fader>16) fader=(byte)(0-(fader-16)-(faderBank*16)); else fader+=(faderBank*16);
    if (recentWidget!=null)
    { if (fader==faderBank*16) fader=lastFader;
       if (recentWidget.isShowing() && (recentWidget.sliderNum==fader))
         {if (recentWidget.numFaders==1)
		  recentWidget.setValue((int)(recentWidget.valueMin+((float)(value)/127*(recentWidget.valueMax-recentWidget.valueMin))));
		 else 
		  recentWidget.setValue(fader,(int)value);

	 recentWidget.repaint(); return;}
    }
    lastFader=fader;
    for (int i=0; i<widgetList.size();i++)
      {
	 w=((SysexWidget)(widgetList.get(i)));
	 if (((w.sliderNum ==fader)|((w.sliderNum<fader)& w.sliderNum+w.numFaders>fader))  &&(w.isShowing()))
      {  recentWidget=w;
	      if (recentWidget.numFaders==1)
		  recentWidget.setValue((int)(recentWidget.valueMin+((float)(value)/127*(recentWidget.valueMax-recentWidget.valueMin))));
		 else 
		  recentWidget.setValue(fader,(int)value);
	 recentWidget.repaint();return;}
      }
     }

public void faderHighlight() 
  {
      SysexWidget w;
      for (int i=0; i<widgetList.size();i++)
     { w=(SysexWidget)widgetList.get(i);
	if (w.jlabel!=null) {
	  if (((Math.abs(w.sliderNum-1) & 240))==faderBank*16) {
	    Color c = UIManager.getColor("controlText");
	    if (c == null)
	      c = new Color(75,75,100);
	    w.jlabel.setForeground(c);
	  } else {
	    Color c = UIManager.getColor("textInactiveText");
	    if (c == null)
	      c = new Color(102,102,153);
	    w.jlabel.setForeground(c);
	  }
	  w.jlabel.repaint();
	}
     }
  }

//when showing the dialog, also check how many components there are to determine the
//number of widget banks needed
     public void show() 
    { 
      int high=0;
      SysexWidget x;
      for (int i=0;i<widgetList.size();i++)
      {
          x=(SysexWidget)widgetList.get(i);
	  if ((x.sliderNum+x.numFaders-1)>high) high=x.sliderNum+x.numFaders-1;	  
      }
       numFaderBanks=(high/16)+1;
       faderHighlight();
      ErrorMsg.reportStatus("PatchEditorFrame:Show   Num Fader Banks =  "+numFaderBanks);
    
           Dimension screenSize =PatchEdit.desktop.getSize ();
        Dimension frameSize = this.getSize ();
        if (frameSize.height>screenSize.height) {
            // Add necessary place for the vertical Scrollbar
            frameSize.width+=scroller.getVerticalScrollBar().getPreferredSize ().width;
            this.setSize (frameSize.width,screenSize.height);
        }
        if (frameSize.width>screenSize.width) {
            // Add necessary place for the horizontal Scrollbar
            frameSize.height+=scroller.getHorizontalScrollBar().getPreferredSize ().height;
            // If the entire frame doen't fit in the window, then rescale it to fit
            if (frameSize.height>screenSize.height)
                frameSize.height=screenSize.height;
            this.setSize (screenSize.width,frameSize.height);
        }
       
        super.show ();
       } // end of method

    

//let bankeditorframe set information about itself when it creates a
//patch editor frame
public void setBankEditorInformation (BankEditorFrame bf, int row,int col)
 {
   bankFrame=bf;
   patchRow=row;
   patchCol=col;
  }
  public void revalidateDriver()
  {
  p.ChooseDriver();
  if (p.deviceNum==0) {try{setClosed(true);}catch (Exception e){}; return;}
  }
  

public void gotFocus()
  {
  }
public void lostFocus()
  {
  }

}      
	

