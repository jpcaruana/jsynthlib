/*
 * JSynthlib - "Fractional Scaling" Editor No.2 for Yamaha DX7s
 * ===================================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7sFractionalScalingEditor2.java
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 * CAUTION: This is an experimental driver. It is not tested on a real device yet!
 * !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 *
 * history:
 *         25.02.2003 v0.1: first published release
 *                          
 *
 */

package synthdrivers.YamahaDX7s;
import core.*;
import java.lang.String.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;

class YamahaDX7sFractionalScalingEditor2 extends PatchEditorFrame 
{

  final String [] OpName		= new String [] {"OP 6","OP 5","OP 4","OP 3","OP 2","OP 1"};

  final String [] KeyGrpName		= new String [] {"Offset",
							 "C-2 - C-1",
                                                         "C#-1 - D#-1","E-1 - F#-1","G-1 - A-1","A#-1 - C0",
                                                         "C#0 - D#0","E0 - F#0","G0 - A0","A#0 - C1",
                                                         "C#1 - D#1","E1 - F#1","G1 - A1","A#1 - C2",
                                                         "C#2 - D#2","E2 - F#2","G2 - A2","A#2 - C3",
                                                         "C#3 - D#3","E3 - F#3","G3 - A3","A#3 - C4",
                                                         "C#4 - D#4","E4 - F#4","G4 - A4","A#4 - C5",
                                                         "C#5 - D#5","E5 - F#5","G5 - A5","A#5 - C6",
                                                         "C#6 - D#6","E6 - F#6","G6 - A6","A#6 - C7",
                                                         "C#7 - D#7","E7 - F#7","G7 - A7","A#7 - C8",
                                                         "C#8 - D#8","E8 - F#8","G8"
                                                                 };


  public YamahaDX7sFractionalScalingEditor2(Patch patch)
  {
    super ("Yamaha DX7s \"Fractional Scaling\" Editor",patch);


    PatchEdit.waitDialog.show();	// Because it needs some time to build up the editor frame
    

    int OpNum, KeyNum;

    JPanel microPane = new JPanel();
    microPane.setLayout(new GridBagLayout());gbc.weightx=1;

    for (OpNum = 0 ; OpNum < OpName.length ; OpNum++)
    {
      gbc.gridx=6+3*OpNum;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1;gbc.fill=gbc.BOTH;gbc.anchor=gbc.CENTER;
      microPane.add(new JLabel(OpName[OpNum],SwingConstants.LEFT),gbc);

      for (KeyNum = 0; KeyNum < KeyGrpName.length ; KeyNum++)
      {
        gbc.gridx=0;gbc.gridy=10+2*KeyNum;gbc.gridwidth=6;gbc.gridheight=1;gbc.fill=gbc.BOTH;gbc.anchor=gbc.CENTER;
        microPane.add(new JLabel(KeyGrpName[KeyNum],SwingConstants.LEFT),gbc);

        if ( (KeyNum+OpNum*KeyGrpName.length)%41==0)
              addWidget(microPane, new SpinnerWidget(	"",
                                     			patch,
							0,
							254,
							-127,
                                                     	new FractionalScalingModel(patch,KeyNum+OpNum*KeyGrpName.length),
                                                      	new FractionalScalingSender(patch, OpNum, KeyNum)),
                                                      6+3*OpNum, 10+2*KeyNum, 1, 1, KeyNum+OpNum*KeyGrpName.length);
        else
              addWidget(microPane, new SpinnerWidget(	"",
                                                      	patch,
							0,
							255,
							0,
                                                      	new FractionalScalingModel(patch,KeyNum+OpNum*KeyGrpName.length),
                                                      	new FractionalScalingSender(patch, OpNum, KeyNum)),
                                                      6+3*OpNum, 10+2*KeyNum, 1, 1, KeyNum+OpNum*KeyGrpName.length);

        gbc.gridx=6+3*OpNum+1;gbc.gridy=10+2*KeyNum;gbc.gridwidth=1;gbc.gridheight=1;
        microPane.add(new JLabel(" "),gbc);
      }
    }

    scrollPane.add(microPane,gbc);
    pack();
    show();


    PatchEdit.waitDialog.hide();	// Okay, the editor frame is ready
  }
}
