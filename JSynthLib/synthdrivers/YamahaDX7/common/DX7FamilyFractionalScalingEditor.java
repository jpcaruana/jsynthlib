/*
 * JSynthlib -	generic "Fractional Scaling" Editor for Yamaha DX7 Family
 *		(used by DX7-II, DX7s, TX802)
 * ======================================================================
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

public class DX7FamilyFractionalScalingEditor extends PatchEditorFrame 
{
	//{"Operator 6","Operator 5","Operator 4","Operator 3","Operator 2","Operator 1"};
	final String [] OpName = new String [] {
		"OP 6",
		"OP 5",
		"OP 4",
		"OP 3",
		"OP 2",
		"OP 1"
	};

	final String [] KeyGrpName = new String [] {
		"Offset",
		"C-2 - C-1",
		"C#-1 - D#-1","E-1 - F#-1","G-1 - A-1","A#-1 - C0",
		"C#0  - D#0" ,"E0  - F#0" ,"G0	- A0" ,"A#0  - C1",
		"C#1  - D#1" ,"E1  - F#1" ,"G1	- A1" ,"A#1  - C2",
		"C#2  - D#2" ,"E2  - F#2" ,"G2	- A2" ,"A#2  - C3",
		"C#3  - D#3" ,"E3  - F#3" ,"G3	- A3" ,"A#3  - C4",
		"C#4  - D#4" ,"E4  - F#4" ,"G4	- A4" ,"A#4  - C5",
		"C#5  - D#5" ,"E5  - F#5" ,"G5	- A5" ,"A#5  - C6",
		"C#6  - D#6" ,"E6  - F#6" ,"G6	- A6" ,"A#6  - C7",
		"C#7  - D#7" ,"E7  - F#7" ,"G7	- A7" ,"A#7  - C8",
		"C#8  - D#8" ,"E8  - F#8" ,"G8"
	};

	final String [] offSetName = new String [] {
		" -127"," -126"," -125"," -124"," -123"," -122"," -121"," -120"," -119"," -118"," -117"," -116"," -115"," -114"," -113",
		" -112"," -111"," -110"," -109"," -108"," -107"," -106"," -105"," -104"," -103"," -102"," -101"," -100"," -99" ," -98" ," -97",
		" -96" ," -95" ," -94" ," -93" ," -92" ," -91" ," -90" ," -89" ," -88" ," -87" ," -86" ," -85" ," -84" ," -83" ," -82" ," -81",
		" -80" ," -79" ," -78" ," -77" ," -76" ," -75" ," -74" ," -73" ," -72" ," -71" ," -70" ," -69" ," -68" ," -67" ," -66" ," -65",
		" -64" ," -63" ," -62" ," -61" ," -60" ," -59" ," -58" ," -57" ," -56" ," -55" ," -54" ," -53" ," -52" ," -51" ," -50" ," -49",
		" -48" ," -47" ," -46" ," -45" ," -44" ," -43" ," -42" ," -41" ," -40" ," -39" ," -38" ," -37" ," -36" ," -35" ," -34" ," -33",
		" -32" ," -31" ," -30" ," -29" ," -28" ," -27" ," -26" ," -25" ," -24" ," -23" ," -22" ," -21" ," -20" ," -19" ," -18" ," -17",
		" -16" ," -15" ," -14" ," -13" ," -12" ," -11" ," -10" ,"  -9" ,"  -8" ,"  -7" ,"  -6" ,"  -5" ,"  -4" ,"  -3" ,"  -2" ,"  -1",
		"   0" ,"   1" ,"   2" ,"   3" ,"   4" ,"   5" ,"   6" ,"   7" ,"   8" ,"   9" ,"  10" ,"  11" ,"  12" ,"  13" ,"  14" ,"  15",
		"  16" ,"  17" ,"  18" ,"  19" ,"  20" ,"  21" ,"  22" ,"  23" ,"  24" ,"  25" ,"  26" ,"  27" ,"  28" ,"  29" ,"  30" ,"  31",
		"  32" ,"  33" ,"  34" ,"  35" ,"  36" ,"  37" ,"  38" ,"  39" ,"  40" ,"  41" ,"  42" ,"  43" ,"  44" ,"  45" ,"  46" ,"  47",
		"  48" ,"  49" ,"  50" ,"  51" ,"  52" ,"  53" ,"  54" ,"  55" ,"  56" ,"  57" ,"  58" ,"  59" ,"  60" ,"  61" ,"  62" ,"  63",
		"  64" ,"  65" ,"  66" ,"  67" ,"  68" ,"  69" ,"  70" ,"  71" ,"  72" ,"  73" ,"  74" ,"  75" ,"  76" ,"  77" ,"  78" ,"  79",
		"  80" ,"  81" ,"  82" ,"  83" ,"  84" ,"  85" ,"  86" ,"  87" ,"  88" ,"  89" ,"  90" ,"  91" ,"  92" ,"  93" ,"  94" ,"  95",
		"  96" ,"  97" ,"  98" ,"  99" ," 100" ," 101" ," 102" ," 103" ," 104" ," 105" ," 106" ," 107" ," 108" ," 109" ," 110" ," 111",
		" 112" ," 113" ," 114" ," 115" ," 116" ," 117" ," 118" ," 119" ," 120" ," 121" ," 122" ," 123" ," 124" ," 125" ," 126" ," 127"
	};

	final String [] scalingName = new String [] {
		"   0","   1","   2","	 3","	4","   5","   6","   7","   8","   9","  10","	11","  12","  13","  14","  15",
		"  16","  17","  18","	19","  20","  21","  22","  23","  24","  25","  26","	27","  28","  29","  30","  31",
		"  32","  33","  34","	35","  36","  37","  38","  39","  40","  41","  42","	43","  44","  45","  46","  47",
		"  48","  49","  50","	51","  52","  53","  54","  55","  56","  57","  58","	59","  60","  61","  62","  63",
		"  64","  65","  66","	67","  68","  69","  70","  71","  72","  73","  74","	75","  76","  77","  78","  79",
		"  80","  81","  82","	83","  84","  85","  86","  87","  88","  89","  90","	91","  92","  93","  94","  95",
		"  96","  97","  98","	99"," 100"," 101"," 102"," 103"," 104"," 105"," 106"," 107"," 108"," 109"," 110"," 111",
		" 112"," 113"," 114"," 115"," 116"," 117"," 118"," 119"," 120"," 121"," 122"," 123"," 124"," 125"," 126"," 127",
		" 128"," 129"," 130"," 131"," 132"," 133"," 134"," 135"," 136"," 137"," 138"," 139"," 140"," 141"," 142"," 143",
		" 144"," 145"," 146"," 147"," 148"," 149"," 150"," 151"," 152"," 153"," 154"," 155"," 156"," 157"," 158"," 159",
		" 160"," 161"," 162"," 163"," 164"," 165"," 166"," 167"," 168"," 169"," 170"," 171"," 172"," 173"," 174"," 175",
		" 176"," 177"," 178"," 179"," 180"," 181"," 182"," 183"," 184"," 185"," 186"," 187"," 188"," 189"," 190"," 191",
		" 192"," 193"," 194"," 195"," 196"," 197"," 198"," 199"," 200"," 201"," 202"," 203"," 204"," 205"," 206"," 207",
		" 208"," 209"," 210"," 211"," 212"," 213"," 214"," 215"," 216"," 217"," 218"," 219"," 220"," 221"," 222"," 223",
		" 224"," 225"," 226"," 227"," 228"," 229"," 230"," 231"," 232"," 233"," 234"," 235"," 236"," 237"," 238"," 239",
		" 240"," 241"," 242"," 243"," 244"," 245"," 246"," 247"," 248"," 249"," 250"," 251"," 252"," 253"," 254"," 255"
	};


	public DX7FamilyFractionalScalingEditor(String name, Patch patch)
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
			microPane.add(new JLabel(OpName[OpNum]),gbc);

			for (KeyNum = 0; KeyNum < KeyGrpName.length ; KeyNum++) {
				gbc.gridx=0;gbc.gridy=10+2*KeyNum;gbc.gridwidth=6;gbc.gridheight=1;gbc.fill=gbc.BOTH;gbc.anchor=gbc.WEST;
				microPane.add(new JLabel(KeyGrpName[KeyNum]),gbc);

				if ( (KeyNum+OpNum*KeyGrpName.length)%41==0) {
					addWidget(microPane, new ComboBoxWidget("",
						      patch,
						      new FractionalScalingModel(patch,KeyNum+OpNum*KeyGrpName.length),
						      new FractionalScalingSender(patch, OpNum, KeyNum),
						      offSetName),
						      6+3*OpNum, 10+2*KeyNum, 3, 1, KeyNum+OpNum*KeyGrpName.length);
				} else {
					addWidget(microPane, new ComboBoxWidget("",
						      patch,
						      new FractionalScalingModel(patch,KeyNum+OpNum*KeyGrpName.length),
						      new FractionalScalingSender(patch, OpNum, KeyNum),
						      scalingName),
						      6+3*OpNum, 10+2*KeyNum, 3, 1, KeyNum+OpNum*KeyGrpName.length);
				}
			}
		}

		scrollPane.add(microPane,gbc);
		pack();
		show();

		PatchEdit.waitDialog.hide();	// Okay, the editor frame is ready
	}


	/*
	 * Parameter Model - Fractional Scaling
	 */
	class FractionalScalingModel extends ParamModel
	{
		int upper_nibble,lower_nibble;

		public FractionalScalingModel(Patch p, int o)
		{
			patch=p;
			ofs=16+(2*o);
		}
	
		public void set(int i)
		{
			patch.sysex[ofs  ] = (byte)( (i / 16  ) + 0x30 );
			patch.sysex[ofs+1] = (byte)( (i & 0x0f) + 0x30 );
		}
	
		public int get()
		{
			return ( ((patch.sysex[ofs]  -0x30) *16 +
				  (patch.sysex[ofs+1]-0x30)	  ) & 0xff 
			);
		}
	}


	/*
	 * SysexSender - Fractional Scaling
	 *		     (g=6; h=0)
	 */
	class FractionalScalingSender extends SysexSender
	{
		Patch patch;
		int keyNumber, opNumber;
		byte []b = new byte [10];

		public FractionalScalingSender(Patch p, int o, int k)
		{
			patch	= p;
			opNumber  = o;
			keyNumber = k;

			b[0]=(byte)0xF0;
			b[1]=(byte)0x43;
			b[3]=(byte)0x18;
			b[4]=(byte)0x7F;
			b[5]=(byte)opNumber;
			b[6]=(byte)keyNumber;
			b[9]=(byte)0xF7;
		}
	
		public byte [] generate (int value)
		{
			b[2]=(byte)(0x10+channel-1);
			b[7]=(byte)( (value / 16  ) + 0x30 );
			b[8]=(byte)( (value & 0x0f) + 0x30 );

			return b;
		}
	}
}
