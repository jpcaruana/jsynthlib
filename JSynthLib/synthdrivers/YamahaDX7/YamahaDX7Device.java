/*
 * JSynthlib-Device for Yamaha DX7 Mark-I
 * (with system ROM V 1.8 from October 24th 1985 - article no. IG114690)
 * =====================================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7Device.java
 * date:    23.08.2002
 * @version 0.1
 *
 * Copyright (C) 2002  Torsten.Tittmann@t-online.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package synthdrivers.YamahaDX7;
import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;
import java.util.Arrays;

public class YamahaDX7Device extends Device
{
  // which 6-Operator FM Synths and extensions are existing?
  //public String[] whichSynthString = {"DX7","TX7","DX1","DX5","DX7-SER7","DX7-E!Card","DX7-SuperMax","TX216","TX416","TX816","DX7IID/FD","DX7s","TX802"}; 

  // the Support of these Synths are implemented
  public String[] whichSynthString = {"DX7","TX7"};
  private String  whichSynth;

  // Simulate DX7 panel button pushes by sending SysEx commands ?
  JCheckBox       sPBP;
  private int     sPBPval;
  // switch off panel button pushes information message?
  JCheckBox       sPBPmsg;
  private int      sPBPmsgVal;

  // length of infoText lines should be aprox. 70 characters
  //               "____________________________________________________________________"
  // DX7 atomics of the information messages
  final static String dx7AutomationString =
                   "\n\nYou can automate this if you enable the 'Enable DX7 Remote Control?'"+			// tt+lb
                   "\nfunction in the DX7 Device Config Panel";							// tt+lb

  final static String dx7avoidMsgString =
                   "\n\n(You can avoid this message generally if you disable the 'Display"+			// tt+lb
                   "\nHints and Tips?' function)";								// tt+lb

  // DX7 information messages of single voice and bank driver
  final static String dx7ReceiveString =
                   "\n\nHave you prepared your DX7 to receive a patch?"+					// tt+lb
                   "\n(Memory Protect Off & Sys Info Available)"+						// tt+lb
                   dx7AutomationString+dx7avoidMsgString;

  final static String dx7StoreString =
                   "\n\nThe patch has been placed in the edit buffer!"+						// tt+lb
                   "\n\nYou must now hold the 'STORE' button on the DX7 and choose a location"+			// tt+lb
                   "\n(1-32) to store the patch."+								// tt+lb
                   dx7AutomationString+dx7avoidMsgString;

  final static String dx7RequestVoiceString =
                   "\n\nPlease start the patch dump manually, when you have prepared your DX7"+			// tt+lb
                   "\nto send a patch! (Memory Protect Off & Sys Info Available)"+				// tt+lb
                   dx7AutomationString+dx7avoidMsgString;

  // TX7 information messages of single voice and bank driver
  final static String tx7ReceiveString =
                   "\n\nHave you prepared your TX7 to receive a patch? (Memory Protect Off)"+			// tt+lb
                   dx7AutomationString+dx7avoidMsgString;

  final static String tx7StoreString =
                   "\n\nThe TX7 owner's manual gives no information about storing a single"+			// tt+lb
                   "\nvoice patch. I suppose you have to create a bank with all desired"+			// tt+lb
                   "\nvoices and transmit this bank to the TX7."+						// tt+lb
                   "\n\nThe patch has been placed in the edit buffer!"+						// tt+lb
                   dx7avoidMsgString;
                   
  final static String tx7RequestVoiceString =
                   "\n\nRequesting a single voice dump will deliver the voice in the edit"+			// tt+lb
                   "\nbuffer! You have to choose the desired voice manually."+					// tt+lb
                   dx7avoidMsgString;


    /** Creates new YamahaDX7Device */
    public YamahaDX7Device ()
    {
        manufacturerName="Yamaha";
        modelName="DX7";
        synthName="DX7";

        addDriver (new YamahaDX7BankDriver ());
        addDriver (new YamahaDX7SingleDriver ());

        infoText=
                "JSynthLib supports single/bank librarian and voice editing functions on a"+			// tt+lb
                " Yamaha DX7 Mark-I synthesizer."+								// tt+lb
                "\n\n"+												// tt+lb
                "DX7 - GENERAL INFORMATION"+									// tt+lb
                "\nBecause of the MIDI implementation of the system ROM of the early units"+			// tt+lb
                " (especially the ActiveSensing- and the SysEx-handling), problems will occur with"+		// tt+lb
                " an early DX7 in cooperation with Patch Libraries/Editors."+					// tt+lb
                "\nIt's advisable to upgrade early system ROMs to a newer version. The serial numbers of"+	// tt+lb
                " the instruments concerned are: 1001-24880, 25125-26005. (e.g. see TX7 owner's manual)"+	// tt+lb
                "\nOnly the DX7-I in an original state (system ROM V1.8 from October 24th 1985) is tested;"+	// tt+lb
                " no expansion cards (like E!Card or other) or other models of the DX7 series."+		// tt+lb
                "\n\n"+												// tt+lb
                "The original DX7-I transmits all MIDI data on channel 1. Only the receive channel"+		// tt+lb
                " is variable. It's recommended to choose channel 1 for both. (This should be also"+		// tt+lb
                " true for the TX7)"+										// tt+lb
                "\nTo enable transmitting or receiving of SysEx messages, the memory protection must"+		// tt+lb
                " be turned off and the sys info must be available. (For the TX7 it should only be necessary"+	// tt+lb
                " to switch off the memory protection)"+							// tt+lb
                "\nA received voice patch is held in an edit buffer, but not stored permanently."+		// tt+lb
                "\n\n"+												// tt+lb
                "SINGLE/BANK DRIVER"+										// tt+lb
                "\nYou can choose between the DX7 and the TX7. But the TX7 part is still"+			// tt+lb
                " experimental. Since I have only the owner's manual of the TX7 and not the device"+		// tt+lb
                " itself to test it's behaviour, be carefull with the TX7 related parts."+			// tt+lb
                "\nThe MIDI specification of the DX7 doesn't support requesting of a patch. As an"+		// tt+lb
                " alternative this driver can simulate the necessary panel button pushes, if you"+		// tt+lb
                " switch on the 'Enable DX7 Remote Control' function. This function makes the"+			// tt+lb
                " SysInfo available, switch off the memory protection, choose the desired patch"+		// tt+lb
                " number, etc. This will happen with every 'play', 'send', 'store' and 'edit' action."+		// tt+lb
                "\nIf you are familiar with the DX7 you can switch off the message windows by disabling"+	// tt+lb
                " the 'Display Hints and Tips?' function."+							// tt+lb
                "\n\n"+												// tt+lb
                "VOICE EDITOR"+											// tt+lb
                "\nOnly those parameters are implemented, which are stored in the patch."+			// tt+lb
                " So, you won't find any function parameter like Pitchband, Portamento, etc."+			// tt+lb
                " (Perhaps, these will be part of a PERFORMANCE DRIVER/EDITOR)"+				// tt+lb
                "\nThere is only one exception: the OPERATOR ON/OFF buttons, because they are"+			// tt+lb
                " usefull for programming."+									// tt+lb
                "\nAt this time only the direction JSynthLib->DX7 is working. If a parameter is changed on"+	// tt+lb
                " the DX7 itself, JSynthlib doesn't become aware of this.";					// tt+lb

        setWhichSynth("DX7");
        setSPBPval(0);    // Disable 'Enable DX7 Remote Control?' function by default!
        setSPBPmsgVal(1); // Enable 'Display Hints and Tips?' function by default!
    }

    public JPanel config()
    {
      JPanel panel= new JPanel();
      GridBagLayout gridbag = new GridBagLayout();
      GridBagConstraints c = new GridBagConstraints();
      panel.setLayout(gridbag);
      c.anchor = GridBagConstraints.WEST; 

      c.gridx=0;c.gridy=0;c.gridwidth=9;c.gridheight=1;c.weightx=1;c.anchor=c.WEST;c.fill=c.HORIZONTAL;
      panel.add(new JLabel("                                                  "),c);

      c.gridx=0;c.gridy=1;c.gridwidth=3;c.gridheight=2;
      panel.add(new JLabel("Choose a supported Synthesizer"),c);
      JComboBox synthList = new JComboBox(whichSynthString);
      synthList.setSelectedItem(getWhichSynth());
      synthList.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          JComboBox cb = (JComboBox)e.getSource();
          String synthindex = (String)cb.getSelectedItem();
          setWhichSynth(synthindex);
          setSynthName(getWhichSynth());
        }
      });
      c.gridx=3;c.gridy=1;c.gridwidth=3;c.gridheight=2;
      panel.add(synthList,c);

      c.gridx=0;c.gridy=3;c.gridwidth=3;c.gridheight=2;
      panel.add(new JLabel("Enable DX7 Remote Control?"),c);
      sPBP = new JCheckBox();
      sPBP.setSelected(getSPBPval()==1);
      sPBP.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          JCheckBox chb = (JCheckBox)e.getSource();
          if (chb.isSelected()) setSPBPval(1);
            else setSPBPval(0);
        }
      });
      c.gridx=3;c.gridy=3;c.gridwidth=1;c.gridheight=2;
      panel.add(sPBP,c);

      c.gridx=0;c.gridy=5;c.gridwidth=3;c.gridheight=2;
      panel.add(new JLabel("Display Hints and Tips?"),c);
      sPBPmsg = new JCheckBox();
      sPBPmsg.setSelected(getSPBPmsgVal()==1);
      sPBPmsg.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent e)
        {
          JCheckBox chb = (JCheckBox)e.getSource();
          if (chb.isSelected()) setSPBPmsgVal(1);
            else setSPBPmsgVal(0);
        }
      });
      c.gridx=3;c.gridy=5;c.gridwidth=1;c.gridheight=2;
      panel.add(sPBPmsg,c);

 
     return panel;
   }


  //
  // DX7 Remote Control related part
  //

  // simulate DX7 panel button pushes constants
  final static int DEPRESS   = 0x7F;
  final static int RELEASE   = 0x00;

  final static int FUNCTION  = 0x27;
  final static int BATTERY   = 0x0d;
  final static int MIDI_CH   = 0x07;
  final static int SYSINFO   = 0x07;
  final static int MIDI_XMIT = 0x07;
  final static int YES       = 0x29;
  final static int NO        = 0x28;

  // simulate DX7 single panel button pushes
  final static SysexHandler DX7Button       = new SysexHandler("f0 43 @@ 08 *button* *action* f7"); // common button push
  final static SysexHandler depressDX7Store = new SysexHandler("f0 43 @@ 08 20 7f f7");             //BUTTON depress: Store
  final static SysexHandler releaseDX7Store = new SysexHandler("f0 43 @@ 08 20 00 f7");             //BUTTON release:

  // simulate TX7 single panel button pushes
  final static SysexHandler swOffTX7MemProt = new SysexHandler("f0 43 @@ 11 07 7f f7");             //TX7 function parameter change

  // make DX7 system informations available
  public static void mkDX7SysInfoAvail(int p, byte ch) // port, channel
  {
    DX7Button.send(p, ch, new NameValue("button",FUNCTION) , new NameValue("action",DEPRESS));
    DX7Button.send(p, ch, new NameValue("button",FUNCTION) , new NameValue("action",RELEASE));

    DX7Button.send(p, ch, new NameValue("button",BATTERY)  , new NameValue("action",DEPRESS));
    DX7Button.send(p, ch, new NameValue("button",BATTERY)  , new NameValue("action",RELEASE));

    DX7Button.send(p, ch, new NameValue("button",MIDI_CH)  , new NameValue("action",DEPRESS));
    DX7Button.send(p, ch, new NameValue("button",MIDI_CH)  , new NameValue("action",RELEASE));

    DX7Button.send(p, ch, new NameValue("button",SYSINFO)  , new NameValue("action",DEPRESS));
    DX7Button.send(p, ch, new NameValue("button",SYSINFO)  , new NameValue("action",RELEASE));

    DX7Button.send(p, ch, new NameValue("button",YES)      , new NameValue("action",DEPRESS));
    DX7Button.send(p, ch, new NameValue("button",YES)      , new NameValue("action",RELEASE));
  }

  // switch off DX7 memory protection
  public static void swOffDX7MemProt(int p, byte ch, byte mp, byte bn) // port, channel, memory protection of internal/cartridge, internal/cartridge
  {
    DX7Button.send(p, ch, new NameValue("button",mp),        new NameValue("action",DEPRESS));
    DX7Button.send(p, ch, new NameValue("button",mp),        new NameValue("action",RELEASE));

    DX7Button.send(p, ch, new NameValue("button",NO),        new NameValue("action",DEPRESS));
    DX7Button.send(p, ch, new NameValue("button",NO),        new NameValue("action",RELEASE));

    DX7Button.send(p, ch, new NameValue("button",bn),        new NameValue("action",DEPRESS));
    DX7Button.send(p, ch, new NameValue("button",bn),        new NameValue("action",RELEASE));
  }

  // transmit DX7 bank dump
  public static void xmitDX7BankDump(int p, byte ch) // port, channel
  {
    DX7Button.send(p, ch, new NameValue("button",MIDI_XMIT), new NameValue("action",DEPRESS));
    DX7Button.send(p, ch, new NameValue("button",MIDI_XMIT), new NameValue("action",RELEASE));

    DX7Button.send(p, ch, new NameValue("button",YES)      , new NameValue("action",DEPRESS));
    DX7Button.send(p, ch, new NameValue("button",YES)      , new NameValue("action",RELEASE));
  }

  // switch to desired bank
  public static void chDX7Bank(int p, byte ch, byte bn) // port, channel, internal/cartridge
  {
    DX7Button.send(p, ch, new NameValue("button",bn),        new NameValue("action",DEPRESS));
    DX7Button.send(p, ch, new NameValue("button",bn),        new NameValue("action",RELEASE));
  }

  // switch to desired patch number
  public static void chDX7Patch(int p, byte ch, byte pn) // port, channel, patch number
  {
    DX7Button.send(p, ch, new NameValue("button",pn),        new NameValue("action",DEPRESS));
    DX7Button.send(p, ch, new NameValue("button",pn),        new NameValue("action",RELEASE));
  }


	// For storable interface

	/** Getter for whichSynth */
	public String getWhichSynth() { return this.whichSynth; };
	/** Setter for whichSynth */
	public void setWhichSynth(String whichSynth) { this.whichSynth = whichSynth; };

	/** Getter for sPBPval */
	public int getSPBPval() { return this.sPBPval; };
	/** Setter for sPBPval */
	public void setSPBPval(int sPBPval) { this.sPBPval = sPBPval; };

	/** Getter for sPBPmsgVal */
	public int getSPBPmsgVal() { return this.sPBPmsgVal; };
	/** Setter for sPBPmsgVal */
	public void setSPBPmsgVal(int sPBPmsgVal) { this.sPBPmsgVal = sPBPmsgVal; };


	/**
	 * Get the names of properties that should be stored and loaded.
	 * @return a Set of field names
	 */
	public Set storedProperties() {
		final String[] storedPropertyNames = {
			"whichSynth", "sPBPval", "sPBPmsgVal",
		};
		Set set = super.storedProperties();
		set.addAll(Arrays.asList(storedPropertyNames));
		return set;
	}


}
