/*
 * JSynthlib - Device for Yamaha TX802
 * ===================================
 * @author  Torsten Tittmann
 * file:    YamahaTX802Device.java
 * date:    25.02.2003
 * @version 0.1
 *
 * Copyright (C) 2002-2003  Torsten.Tittmann@t-online.de
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
 *         25.02.2003 v0.1: first published release
 *
 */

package synthdrivers.YamahaTX802;
import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Set;
import java.util.Arrays;

public class YamahaTX802Device extends Device
{
  // switch off memory protection?
  JCheckBox		swOffMemProt;
  protected int		swOffMemProtFlag;
  // switch off "Hints and Tips Messages"?
  JCheckBox		tipsMsg;
  protected int		tipsMsgFlag;
  // Editors use spinner?
  JCheckBox		spinnerEditor;
  protected int		spinnerEditorFlag;


  /** Creates new YamahaTX802Device */
  public YamahaTX802Device ()
  {
    setSwOffMemProtFlag(0);     // Disable 'Disable Memory Protection?' function by default!
    setTipsMsgFlag(1);		// Enable  'Display Hints and Tips?'    function by default!
    setSpinnerEditorFlag(0);	// use spinner elements for editors (jdk >= 1.4!)

    manufacturerName="Yamaha";
    modelName="TX802";
    synthName="TX802";

    infoText=TX802Strings.INFO_TEXT;

    // voice patch
    addDriver (new YamahaTX802VoiceSingleDriver());
    addDriver (new YamahaTX802VoiceBankDriver());

    // additional voice patch
    addDriver (new YamahaTX802AdditionalVoiceSingleDriver());   // experimental !!!!
    addDriver (new YamahaTX802AdditionalVoiceBankDriver());     // experimental !!!!

    // micro tuning patch
    addDriver (new YamahaTX802MicroTuningSingleDriver());       // experimental !!!!
    addDriver (new YamahaTX802MicroTuningBankDriver());         // experimental !!!!

    // fractional scaling patch
    addDriver (new YamahaTX802FractionalScalingSingleDriver()); // experimental !!!!
    addDriver (new YamahaTX802FractionalScalingBankDriver());   // experimental !!!!

    // system setup patch
    addDriver (new YamahaTX802SystemSetupDriver());             // experimental !!!!

    // performance patch
    addDriver (new YamahaTX802PerformanceSingleDriver());       // experimental !!!!
    addDriver (new YamahaTX802PerformanceBankDriver());         // experimental !!!!
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
    panel.add(new JLabel("Synthesizer Name"),c);
    final JTextField sl = new JTextField(getSynthName());
    sl.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        setSynthName(sl.getText());
      }
    });
    c.gridx=3;c.gridy=1;c.gridwidth=3;c.gridheight=2;
    panel.add(sl,c);
    c.gridx=4;c.gridy=1;c.gridwidth=3;c.gridheight=1;
    panel.add(new JLabel(" ( free choosable )"),c);

    c.gridx=0;c.gridy=5;c.gridwidth=3;c.gridheight=2;
    panel.add(new JLabel("Disable Memory Protection?"),c);
    swOffMemProt = new JCheckBox();
    swOffMemProt.setSelected(getSwOffMemProtFlag()==1);
    swOffMemProt.setEnabled(false);
    swOffMemProt.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        JCheckBox chb = (JCheckBox)e.getSource();
        if (chb.isSelected()) setSwOffMemProtFlag(1);
          else setSwOffMemProtFlag(0);
      }
    });
    c.gridx=3;c.gridy=5;c.gridwidth=1;c.gridheight=2;
    panel.add(swOffMemProt,c);
    c.gridx=4;c.gridy=5;c.gridwidth=3;c.gridheight=1;
    panel.add(new JLabel(" ( not available yet! )"),c);

    c.gridx=0;c.gridy=7;c.gridwidth=3;c.gridheight=2;
    panel.add(new JLabel("Display Hints and Tips?"),c);
    tipsMsg = new JCheckBox();
    tipsMsg.setSelected(getTipsMsgFlag()==1);
    tipsMsg.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        JCheckBox chb = (JCheckBox)e.getSource();
        if (chb.isSelected()) setTipsMsgFlag(1);
          else setTipsMsgFlag(0);
      }
    });
    c.gridx=3;c.gridy=7;c.gridwidth=1;c.gridheight=2;
    panel.add(tipsMsg,c);

    c.gridx=0;c.gridy=9;c.gridwidth=1;c.gridheight=1;
    panel.add(new JLabel(" "),c);


    c.gridx=0;c.gridy=10;c.gridwidth=3;c.gridheight=1;
    panel.add(new JLabel("Editor settings:"),c);

    c.gridx=0;c.gridy=13;c.gridwidth=3;c.gridheight=2;
    panel.add(new JLabel("use Spinner Elements?"),c);
    spinnerEditor = new JCheckBox();
    spinnerEditor.setSelected(getSpinnerEditorFlag()==1);
    spinnerEditor.setEnabled(true);
    spinnerEditor.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent e)
      {
        JCheckBox chb = (JCheckBox)e.getSource();
        if (chb.isSelected()) setSpinnerEditorFlag(1);
          else setSpinnerEditorFlag(0);
      }
    });
    c.gridx=3;c.gridy=13;c.gridwidth=1;c.gridheight=2;
    panel.add(spinnerEditor,c);
    c.gridx=4;c.gridy=13;c.gridwidth=3;c.gridheight=1;
    panel.add(new JLabel("( needs JDK 1.4 or later! )"),c);

    return panel;
  }

  // For storable interface

  /** Getter for swOffMemProtFlag */
  public int getSwOffMemProtFlag() { return this.swOffMemProtFlag; };
  /** Setter for swOffMemProtFlag */
  public void setSwOffMemProtFlag(int swOffMemProtFlag) { this.swOffMemProtFlag = swOffMemProtFlag; };

  /** Getter for tipsMsgFlag */
  public int getTipsMsgFlag() { return this.tipsMsgFlag; };
  /** Setter for tipsMsgFlag */
  public void setTipsMsgFlag(int tipsMsgFlag) { this.tipsMsgFlag = tipsMsgFlag; };

  /** Getter for spinnerEditorFlag */
  public int getSpinnerEditorFlag() { return this.spinnerEditorFlag; };
  /** Setter for tipsMsgFlag */
  public void setSpinnerEditorFlag(int spinnerEditorFlag) { this.spinnerEditorFlag = spinnerEditorFlag; };


  /**
   * Get the names of properties that should be stored and loaded.
   * @return a Set of field names
   */
  public Set storedProperties()
  {
    final String[] storedPropertyNames = {
					 "swOffMemProtFlag", "tipsMsgFlag" ,"spinnerEditorFlag"
                                         };
    Set set = super.storedProperties();
    set.addAll(Arrays.asList(storedPropertyNames));
    return set;
  }

  /**
   * Method that will be called after loading
   */
  public void afterRestore()
  {
  }

}
