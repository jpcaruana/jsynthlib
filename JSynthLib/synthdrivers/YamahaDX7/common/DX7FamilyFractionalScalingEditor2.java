/*
 * JSynthlib -	generic "Fractional Scaling" Editor No.2 for Yamaha DX7 Family
 *		(used by DX7-II, DX7s, TX802)
 * ===========================================================================
 * @version $Id$
 * @author  Torsten Tittmann
 *
 * Copyright (C) 2002-2004 Torsten.Tittmann@gmx.de
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
 */
package synthdrivers.YamahaDX7.common;
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

public class DX7FamilyFractionalScalingEditor2 extends DX7FamilyFractionalScalingEditor
{
	public DX7FamilyFractionalScalingEditor2(String name, Patch patch)
	{
		super (name, patch);

		buildEditor(patch);
	}


	protected void buildEditor(Patch patch)
	{
		PatchEdit.waitDialog.show();	// Because it needs some time to build up the editor frame

		int OpNum, KeyNum;

		JPanel microPane = new JPanel();
		microPane.setLayout(new GridBagLayout());gbc.weightx=1;

		for (OpNum = 0 ; OpNum < OpName.length ; OpNum++) {
			gbc.gridx=6+3*OpNum;gbc.gridy=0;gbc.gridwidth=3;gbc.gridheight=1;gbc.fill=gbc.BOTH;gbc.anchor=gbc.CENTER;
			microPane.add(new JLabel(OpName[OpNum],SwingConstants.LEFT),gbc);

			for (KeyNum = 0; KeyNum < KeyGrpName.length ; KeyNum++) {
				gbc.gridx=0;gbc.gridy=10+2*KeyNum;gbc.gridwidth=6;gbc.gridheight=1;gbc.fill=gbc.BOTH;gbc.anchor=gbc.CENTER;
				microPane.add(new JLabel(KeyGrpName[KeyNum],SwingConstants.LEFT),gbc);

				if ( (KeyNum+OpNum*KeyGrpName.length)%41==0) {
					addWidget(microPane, new SpinnerWidget(	"",
						patch,
						0,
						254,
						-127,
						new FractionalScalingModel(patch,KeyNum+OpNum*KeyGrpName.length),
						new FractionalScalingSender(patch, OpNum, KeyNum)),
						6+3*OpNum, 10+2*KeyNum, 1, 1, KeyNum+OpNum*KeyGrpName.length);
				} else {
					addWidget(microPane, new SpinnerWidget(	"",
						patch,
						0,
						255,
						0,
						new FractionalScalingModel(patch,KeyNum+OpNum*KeyGrpName.length),
						new FractionalScalingSender(patch, OpNum, KeyNum)),
						6+3*OpNum, 10+2*KeyNum, 1, 1, KeyNum+OpNum*KeyGrpName.length);
				}

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
