/*
 * JSynthlib-Device for Yamaha DX7 Mark-I
 * (with system ROM V 1.8 from October 24th 1985 - article no. IG114690)
 * =====================================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7Device.java
 * date:    31.10.2002
 * @version 0.2
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.i
 *
 *
 * history:
 *         23.08.2002 v0.1: first published release
 *         31.10.2002 v0.2: - name of voice driver changed (YamahaDX7SingleDriver -> YamahaDX7SingleVoiceDriver)
 *                                                         (YamahaDX7BankDriver -> YamahaDX7BankVoiceDriver)
 *                          - added TX7 Performance support
 *                          - changed driver configuration access
 *                           
 *
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
  // what's about the KX serie, QX serie and CX5M?
  //public final String[] whichSynthString = {"DX7-I","TX7","DX1","DX5","TX216","TX416","TX816","DX7s","DX7IID/FD","TX802","DX7-SER7","DX7-E!Card","DX7-SuperMax"}; 

  // the Support of these Synths are implemented
  private final String[] whichSynthString = {"DX7-I","TX7"};
  private String   whichSynth;

  // Simulate DX7 panel button pushes by sending SysEx commands ?
  JCheckBox       sPBP;
  private int     sPBPval;
  // switch off panel button pushes information message?
  JCheckBox       sPBPmsg;
  private int     sPBPmsgVal;

  // length of infoText lines should be aprox. 70 characters
  //               "____________________________________________________________________"
  // DX7 atomics of the information messages
  final static String dx7AutomationString =
                   "\n\n(You can automate this if you enable the \"Enable Remote Control?\""+			// ja
                   "\nfunction in the Device Config Panel)";							// ja

  final static String dx7avoidMsgString =
                   "\n\n(You can avoid this message generally if you disable the \"Display"+			//ja
                   "\nHints and Tips?\" function in the Device Config Panel)";					//ja

  // DX7 information messages of single voice and bank driver
  final static String dx7ReceiveString =
                   "\n\nHave you prepared your DX7-I to receive a patch?"+					//ja
                   "\n(Memory Protect Off & Sys Info Available)"+						//ja
                   dx7AutomationString+dx7avoidMsgString;

  final static String dx7StoreSingleVoiceString =
                   "\n\nThe patch has been placed in the edit buffer!"+						//ja
                   "\n\nYou must now hold the \"STORE\" button on the DX7-I and choose a location"+		//ja
                   "\n(1-32) to store the patch."+								//ja
                   dx7AutomationString+dx7avoidMsgString;

  final static String dx7RequestVoiceString =
                   "\n\nPlease start the patch dump manually, when you have prepared your DX7-I"+		//ja
                   "\nto send a patch! (Memory Protect Off & Sys Info Available)"+				//ja
                   dx7AutomationString+dx7avoidMsgString;

  final static String dx7StoreSinglePerformanceString =
		   "\n\nThe DX7-I doesn't support storing of a single performance data patch."+			//ja
                   "\n\nThe patch has been placed in the edit buffer!"+						//ja
                   dx7avoidMsgString;

  final static String dx7PerformanceString =
		   "\n\nThe DX7-I supports only the receiving of a TX7 single performance data"+		//ja
                   "\nand \"parameter change\" commands. The DX7-I doesn't support either the"+			//ja
                   "\nTX7 bank performance data or the transmitting and requesting of"+				//ja
                   "\nsingle/bank performance data!"+								//ja
                   dx7avoidMsgString;

  final static String dx7PerformanceEditorString =
                   "\nHave you prepared your DX7-I to receive the \"parameter change\" commands"+		//ja
                   "\nfor the singular performance parameters? (Sys Info Available)"+				//ja
                   dx7AutomationString+dx7avoidMsgString;
		  

  // TX7 information messages of single voice and bank driver
  final static String tx7ReceiveBankString =
                   "\n\nHave you prepared your TX7 to receive a bank patch? (Memory Protect Off)"+		//ja
                   dx7AutomationString+dx7avoidMsgString;

  final static String tx7StoreSingleVoiceString =
                   "\n\nThe TX7 doesn't support storing of a single voice patch."+				//ja
                   "\nYou have to create a bank with all desired voices and transmit this bank"+		//ja
                   "\nto the TX7."+										//ja
                   "\n\nThe patch has been placed in the edit buffer!"+						//ja
                   dx7avoidMsgString;
                   
  final static String tx7RequestSingleVoiceString =
                   "\n\nRequesting a single voice dump will deliver the voice in the edit buffer!"+		//ja
                   "\nYou have to choose the desired voice manually."+						//ja
                   dx7AutomationString+dx7avoidMsgString;

  final static String tx7RequestSinglePerformanceString =
                   "\n\nRequesting a single performance dump will deliver the performance data"+		//ja
                   "\npatch of the current voice in the edit buffer!"+						//ja
                   "\nYou have to choose the desired voice manually."+						//ja
                   dx7AutomationString+dx7avoidMsgString;

  final static String tx7StoreSinglePerformanceString =
		   "\n\nThe TX7 doesn't support storing of a single performance data patch."+			//ja
                   "\nYou have to create a bank with all desired performance data patches and"+			//ja
                   "\ntransmit this bank to the TX7."+								//ja
                   "\n\nThe patch has been placed in the edit buffer!"+						//ja
                   dx7avoidMsgString;


    /** Creates new YamahaDX7Device */
    public YamahaDX7Device ()
    {
        manufacturerName="Yamaha";
        modelName="DX7";
        synthName="DX7";

        addDriver (new YamahaDX7SingleVoiceDriver ());
        addDriver (new YamahaDX7BankVoiceDriver ());
        addDriver (new YamahaTX7SinglePerformanceDriver ());	// experimental !!!!
        addDriver (new YamahaTX7BankPerformanceDriver ());	// experimental !!!!

        infoText=
                "This device driver supports single/bank librarian and editing functions of"+			//ja
                " Yamaha DX7 voice and Yamaha TX7 performance patches."+					//ja			
                "\n\n"+												//ja
                "DX7 MARK-I - GENERAL INFORMATION"+								//ja
                "\nBecause of the MIDI implementation of the system ROM of the early units"+			//ja
                " (especially the ActiveSensing- and the SysEx-handling), problems will occur with"+		//ja
                " an early DX7-I in cooperation with patch libraries/editors."+					//ja
                "\nIt's advisable to upgrade early system ROMs to a newer version. The serial numbers of"+	//ja
                " the instruments concerned are: 1001-24880, 25125-26005. (e.g. see TX7 owner's manual)"+	//ja
                "\nOnly the DX7-I in an original state (system ROM V1.8 from October 24th 1985) is tested;"+	//ja
                " no expansion cards (like E!Card or other) or other models of the DX7 series."+		//ja
                "\n"+												//ja
                "The original DX7-I transmits all MIDI data on channel 1. Only the receive channel"+		//ja
                " is variable. It's recommended to choose channel 1 for both."+			 		//ja
                "\nTo enable transmitting or receiving of SysEx messages, the memory protection must"+		//ja
                " be turned off and the sys info must be available."+						//ja
                "\nA received voice patch is held in an edit buffer, but not stored permanently."+		//ja
                "\n\n"+												//ja
                "DEVICE DRIVER - GENERAL INFORMATION"+								//ja
                "\nThis device driver supports the Yamaha DX7-I and TX7 synthesizer."+				//ja
                " But all members of the DX7 family should work with this driver too. Maybe with some"+		//ja
                " reductions."+											//ja
                "\nThe difference between the singular supported models from the driver's view is the"+		//ja
                " following:"+											//ja
                "\n\n"+												//ja
                "- DX7 Mark-I:"+										//ja
                "\n1. The MIDI specification of the DX7-I doesn't support requesting of a patch. As an"+	//ja
                " alternative this driver can simulate the necessary panel button pushes, if you"+		//ja
                " switch on the \"Enable Remote Control\" function."+						//ja
                "\nThe \"Enable Remote Control\" function makes the SysInfo available, switch off"+		//ja
                " the memory protection, choose the desired patch number, etc. This will happen"+		//ja
                " with every \"play\", \"send\", \"store\" and \"edit\" action."+				//ja
                "\n2. The DX7-I doesn't support the TX7 performance data completely and uses it's own"+		//ja
                " \"parameter change\" messages."+								//ja
                "\n\n"+												//ja
                "- TX7:"+											//ja
                "\n1. This model knows the dump request function for voice and performance patches."+		//ja
                " For that reason the \"Enable Remote Control\" function will only switch off the"+		//ja
                " memory protection of your TX7 and choose the desired patch number for sending/"+		//ja
                "requesting a TX7 single performance patch!"+							//ja
                "\n2. Of course, the TX7 supports the TX7 performance data completely and uses the"+		//ja
                " appropriate \"parameter change\" commands."+							//ja
                "\n\n"+												//ja
                "- Other models:"+										//ja
                "\nAs I mentioned above all other members of the DX7 family should function. At least"+		//ja
                " with the DX7 single/bank voice driver! Probably only the models TX216/416/816 will"+		//ja
                " work with the TX7 single/bank performance driver too."+					//ja
                "\nNote, the DX7-II models (DX7s, DX7-II, TX802) use a different performance data format!"+	//ja
                "\nFor a base configuration of unsupported models you should choose \"TX7\" and switch off"+	//ja
                " the \"Enable Remote Control\" function."+							//ja
                "\nAgain, it's important to choose the right synthesizer in the driver configuration."+		//ja
                " Only if you have a DX7-I then choose \"DX7-I\", in all other cases \"TX7\"!"+			//ja
                "\n\n"+												//ja
                "If you are familiar with the DX7 you can switch off the message windows by disabling"+		//ja
                " the 'Display Hints and Tips?' function. This will avoid all messages!"+		 	//ja
                "\n\n"+												//ja
                "At this time only the direction JSynthLib->DX7 is working. If a parameter is changed on"+	//ja
                " the DX7 itself, JSynthlib doesn't become aware of this."+					//ja
                "\n\n"+												//ja
                "DX7 SINGLE/BANK VOICE DRIVER"+									//ja
                "\nThis driver also supports the use of cartridges with the DX7-I as far as possible."+		//ja
                " Of course, you have to use the switches of the cartridges themselves to choose bank A"+	//ja
                " or bank B of a \"DX7 VOICE ROM\" cartridge or to switch on/off the memory protection of"+	//ja
                " \"RAM1\" cartridges. Further, the storing of a voice bank to a cartridge \"RAM1\" isn't"+	//ja
		" supported!"+											//ja
                "\n\n"+												//ja
                "DX7 VOICE EDITOR"+										//ja
                "\nOnly those parameters are implemented, which are stored in the patch."+			//ja
                "\nSo, you don't find any function parameter like pitchband, portamento, etc."+		 	//ja
                " These are part of the TX7 PERFORMANCE DRIVER/EDITOR."+				 	//ja
                "\nThere is only one exception: the OPERATOR ON/OFF buttons, because they are"+			//ja
                " usefull for programming."+									//ja
		"\n\n"+												//ja
		"TX7 SINGLE/BANK PERFORMANCE DRIVER"+								//ja
		"\nOnly the TX7 and the TF1 module of the TX216/416/816 supports this patch type completely!"+	//ja
                " The DX7-I supports only the receiving of a single performance data patch!"+			//ja
                "\nThis patch format contains a lot of undocumented parameters! Therefore it might be the"+	//ja
                " case, that not all undocumented parameters are computed well. Please give me a note, if"+	//ja
                " this driver shows/causes a strange behaviour."+						//ja
		"\nThe format of the performance patches of the DX7-II Synths (DX7s, DX7-II, TX802)"+		//ja
		" is different!"+										//ja
		"\n\n"+												//ja
		"TX7 PERFORMANCE EDITOR"+									//ja
		"\nThe TX7 performance data format contains parameters for 2 voices. But only the"+		//ja
                " \"voice A\" is supported by the DX7-I.";							//ja

        setWhichSynth("DX7-I"); // DX7 Mark-I by default!
        setSPBPval(0);          // Disable 'Enable Remote Control?' function by default!
        setSPBPmsgVal(1);       // Enable 'Display Hints and Tips?' function by default!
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
          setSynthName(getWhichSynth());	//Is it usefull? The modelname of the driver isn't named (e.g. TX7 Single Performance instead of Single Performance)
        }
      });
      c.gridx=3;c.gridy=1;c.gridwidth=3;c.gridheight=2;
      panel.add(synthList,c);

      c.gridx=0;c.gridy=3;c.gridwidth=3;c.gridheight=2;
      panel.add(new JLabel("Enable Remote Control?"),c);
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
  // Remote Control related part
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
  final static SysexHandler swOffTX7MemProt = new SysexHandler("f0 43 @@ 11 07 00 f7");             //TX7 function parameter change

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


  // For storable interface (Zellyn Hunter)

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
