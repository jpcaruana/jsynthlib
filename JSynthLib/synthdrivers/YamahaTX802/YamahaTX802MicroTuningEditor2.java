/*
 * JSynthlib - "Micro Tuning" Editor No.2 for Yamaha TX802
 * =======================================================
 * @version $Id$
 * @author  Torsten Tittmann
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * CAUTION: This is an experimental driver. It is not tested on a real device yet!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 *
 */
package synthdrivers.YamahaTX802;
import core.*;
import java.lang.String.*;
import java.text.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.*;
import javax.swing.event.*;

class YamahaTX802MicroTuningEditor2 extends PatchEditorFrame 
{
  static final String [] SemiToneName		= new String [] {"C","C#","D","D#","E","F","F#","G","G#","A","A#","B"};

  static final String [] OctaveName		= new String [] {"-2","-1","0","1","2","3","4","5","6","7","8"};


  public YamahaTX802MicroTuningEditor2(Patch patch)
  {
    super ("Yamaha TX802 \"Micro Tuning\" Editor",patch);


    PatchEdit.waitDialog.show();        // Because it needs some time to build up the editor frame


    int SemiTone, Octave, keyByte;

    JPanel microPane = new JPanel();
    microPane.setLayout(new GridBagLayout());gbc.weightx=1;

    for (Octave = 0 ; Octave < OctaveName.length ; Octave++)
    {
      for (SemiTone = 0; SemiTone < SemiToneName.length ; SemiTone++)
      {
	if ( (Octave == OctaveName.length-1) && (SemiTone == 8) ) break;	// The last octave goes only till "G8"

        keyByte = SemiTone+12*Octave;

	if (SemiTone == 0)
	{
        gbc.gridx=3*SemiTone;gbc.gridy=3*Octave+11;gbc.gridwidth=2;gbc.gridheight=1;gbc.fill=gbc.BOTH;gbc.anchor=gbc.CENTER;
        microPane.add(new JLabel("Semitone",SwingConstants.LEFT),gbc);	//Semitone = 100 cent
        gbc.gridx=3*SemiTone;gbc.gridy=3*Octave+12;gbc.gridwidth=2;gbc.gridheight=1;gbc.fill=gbc.BOTH;gbc.anchor=gbc.CENTER;
        microPane.add(new JLabel("1.1719 c",SwingConstants.LEFT),gbc);	//Step     = 1.1719 cent
	}
	else
	{
        gbc.gridx=3*SemiTone+10;gbc.gridy=3*Octave+10;gbc.gridwidth=1;gbc.gridheight=1;
        microPane.add(new JLabel(" "),gbc);
	}

        gbc.gridx=3*SemiTone+11;gbc.gridy=3*Octave+10;gbc.gridwidth=1;gbc.gridheight=1;gbc.fill=gbc.BOTH;gbc.anchor=gbc.CENTER;
        microPane.add(new JLabel(SemiToneName[SemiTone]+OctaveName[Octave],SwingConstants.LEFT),gbc);

        addWidget(microPane, new SpinnerWidget(	// coarse
					"",
					patch,
					0,
					84,
					-42,
					new ParamModel(patch,16+2*keyByte),
					new MicroTuningSender(patch, keyByte , true )),	// true -> coarse
					3*SemiTone+11, 3*Octave+11, 1, 1, 24*Octave+2*SemiTone   );

        addWidget(microPane, new SpinnerWidget(	// fine
					"",
					patch,
					0,
					127,
					0,
					new ParamModel(patch,16+2*keyByte+1),
					new MicroTuningSender(patch, keyByte , false)),	// false -> fine
					3*SemiTone+11, 3*Octave+12, 1, 1, 24*Octave+2*SemiTone+1 );


        gbc.gridx=3*SemiTone+12;gbc.gridy=3*Octave+13;gbc.gridwidth=1;gbc.gridheight=1;
        microPane.add(new JLabel(" "),gbc);
      }
    }

    scrollPane.add(microPane,gbc);
    pack();
    show();


    PatchEdit.waitDialog.hide();        // Okay, the editor frame is ready
  }
}
