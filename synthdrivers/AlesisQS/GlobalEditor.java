package synthdrivers.AlesisQS;
import java.awt.GridBagLayout;

import javax.swing.JPanel;

import core.ComboBoxWidget;
import core.Patch;
import core.PatchEditorFrame;
import core.ScrollBarWidget;

/**
 * Editor for AlesisQS global parameters.
 * @author Zellyn Hunter (zjh, zellyn@zellyn.com)
 * @version $Id$
 *
 *  #  Parameter name                Func  Pot     HiLim    bit address
 *                                      Page  LoLim     bits
 *
 *  0  Global spare (deleted)                           7   0:6 -  0:0
 *  1  Pitch transpose*              0  0  2  -12   12  8   1:7 -  1:0
 *  2  Pitch fine tune*              0  0  3  -99   99  8   2:7 -  2:0
 *  3  Keyboard scaling              0  1  0    0   99  7   3:6 -  3:0
 *  4  Keyboard curve* (QS678 only)  0  1  1    0    2  2   4:1 -  4:0
 *  5  Keyboard Transpose            0  1  2  -12   12  8   5:7 -  5:0
 *  6  Keyboard mode* (QS678 only)   0  1  3    0   17  5   6:4 -  6:0
 *  7  Controller A number           0  2  0    0  120  7   7:6 -  7:0
 *  8  Controller B number           0  2  1    0  120  7   8:6 -  8:0
 *  9  Controller C number           0  2  2    0  120  7   9:6 -  9:0
 * 10  Controller D number           0  2  3    0  120  7  10:6 - 10:0
 * 11  Pedal 1 controller number     0  4  0    0  120  7  11:6 - 11:0
 * 12  Pedal 2 controller number     0  4  2    0  120  7  12:6 - 12:0
 * 13  MIDI program select           0  5  0    0   17  5  13:4 - 13:0
 * 14  Global spare (deleted)                           7  14:6 - 14:0
 * 15  Clock* (QS78R only)           0  -  -    0    1  1  15:0
 * 16  Mix Group Channel* (QSR only) 0  6  0    0   16  5  16:4 - 16:0
 * 17  General MIDI                  0  0  1    0    1  1  17:0
 * 18  A-D controller reset          0  3  1    0    1  1  18:0
 * 19  A-D controller mode           0  3  3    0    2  2  19:1 - 19:0
 *
 * (*) These parameters are transmitted, but are ignored when received as part
 *     of a Global data dump (opcode 0A).
 */
class GlobalEditor extends PatchEditorFrame
{
	public GlobalEditor(Patch patch)
	{
		super ("Alesis QS Global Editor",patch);

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		//gbc.weightx=1;

		//  #  Parameter name                Func  Pot     HiLim    bit address
		//                                      Page  LoLim     bits

		//  1  Pitch transpose*              0  0  2  -12   12  8   1:7 -  1:0
		addWidget(panel,
				  new ScrollBarWidget("Pitch Transpose*", patch, -12, 12, 0,
									  new QSParamModel(patch,1,7,1,0,true),
									  new MidiEditSender(0,0,0,0,0,2)),
				  1,1,1,1, 1);

		//  2  Pitch fine tune*              0  0  3  -99   99  8   2:7 -  2:0
		addWidget(panel,
				  new ScrollBarWidget("Pitch Fine Tune*", patch, -99, 99, 0,
									  new QSParamModel(patch,2,7,2,0,true),
									  new MidiEditSender(0,0,0,0,0,3)),
				  1,2,1,1, 2);
		
		//  3  Keyboard scaling              0  1  0    0   99  7   3:6 -  3:0
		addWidget(panel,
				  new ScrollBarWidget("Keyboard Scaling(65)", patch, 0, 99, 0,
									  new QSParamModel(patch,3,6,3,0,false),
									  new MidiEditSender(0,0,0,1,0,0)),
				  1,3,1,1, 3);
		//  4  Keyboard curve* (QS678 only)  0  1  1    0    2  2   4:1 -  4:0
		//  Weighted, Plastic, Maximum
		addWidget(panel,
				  new ComboBoxWidget("Keyboard Curve*", patch,
									 new QSParamModel(patch,4,1,4,0,false),
									 new MidiEditSender(0,0,0,1,0,1),
									 new String[] {"Weighted","Plastic",
												   "Maximum"}),
				  1,4,1,1,4);
		//  5  Keyboard Transpose            0  1  2  -12   12  8   5:7 -  5:0
		addWidget(panel,
				  new ScrollBarWidget("Keyboard Transpose", patch, -12, 12, 0,
									  new QSParamModel(patch,5,7,5,0,true),
									  new MidiEditSender(0,0,0,1,0,2)),
				  1,5,1,1, 5);
		//  6  Keyboard mode* (QS678 only)   0  1  3    0   17  5   6:4 -  6:0
		// Normal, Ch Solo, Out 01, Out 02, ... , Out 16
		addWidget(panel,
				  new ComboBoxWidget("Keyboard Mode*", patch,
									 new QSParamModel(patch,6,4,6,0,false),
									 new MidiEditSender(0,0,0,1,0,3),
									 new String[] {"Normal","Ch Solo",
												   "Out 01","Out 02","Out 03",
												   "Out 04","Out 05","Out 06",
												   "Out 07","Out 08","Out 09",
												   "Out 10","Out 11","Out 12",
												   "Out 13","Out 14","Out 15",
												   "Out 16"}),
				  1,6,1,1,6);

		//TODO:zjh - include some way for user to see which controller number
		//           does what (see Manual pg 171)

		//  7  Controller A number           0  2  0    0  120  7   7:6 -  7:0
		addWidget(panel,
				  new ScrollBarWidget("Controller A Number(12)",patch,0,120,0,
									  new QSParamModel(patch,7,6,7,0,false),
									  new MidiEditSender(0,0,0,2,0,0)),
				  1,7,1,1, 7);
		//  8  Controller B number           0  2  1    0  120  7   8:6 -  8:0
		addWidget(panel,
				  new ScrollBarWidget("Controller B Number(13)",patch,0,120,0,
									  new QSParamModel(patch,8,6,8,0,false),
									  new MidiEditSender(0,0,0,2,0,1)),
				  1,8,1,1, 8);
		//  9  Controller C number           0  2  2    0  120  7   9:6 -  9:0
		addWidget(panel,
				  new ScrollBarWidget("Controller C Number(91)",patch,0,120,0,
									  new QSParamModel(patch,9,6,9,0,false),
									  new MidiEditSender(0,0,0,2,0,2)),
				  1,9,1,1, 9);
		// 10  Controller D number           0  2  3    0  120  7  10:6 - 10:0
		addWidget(panel,
				  new ScrollBarWidget("Controller D Number(93)",patch,0,120,0,
									  new QSParamModel(patch,10,6,10,0,false),
									  new MidiEditSender(0,0,0,2,0,3)),
				  1,10,1,1, 10);
		// 11  Pedal 1 controller number     0  4  0    0  120  7  11:6 - 11:0
		addWidget(panel,
				  new ScrollBarWidget("Pedal 1 Controller Number(7)",patch,
									  0,120,0,
									  new QSParamModel(patch,11,6,11,0,false),
									  new MidiEditSender(0,0,0,4,0,0)),
				  1,11,1,1, 11);
		// 12  Pedal 2 controller number     0  4  2    0  120  7  12:6 - 12:0
		addWidget(panel,
				  new ScrollBarWidget("Pedal 2 Controller Number(4)",patch,
									  0,120,0,
									  new QSParamModel(patch,12,6,12,0,false),
									  new MidiEditSender(0,0,0,4,0,2)),
				  1,12,1,1, 12);
		// 13  MIDI program select           0  5  0    0   17  5  13:4 - 13:0
		// Off, On, Ch 01, Ch 02, ... , Ch 16
		addWidget(panel,
				  new ComboBoxWidget("Midi Program Select", patch,
									 new QSParamModel(patch,13,4,13,0,false),
									 new MidiEditSender(0,0,0,5,0,0),
									 new String[] {"Off","On",
												   "Ch 01","Ch 02","Ch 03",
												   "Ch 04","Ch 05","Ch 06",
												   "Ch 07","Ch 08","Ch 09",
												   "Ch 10","Ch 11","Ch 12",
												   "Ch 13","Ch 14","Ch 15",
												   "Ch 16"}),
				  1,13,1,1,13);
		// 14  Global spare (deleted)                           7  14:6 - 14:0
		// 15  Clock* (QS78R only)           0  -  -    0    1  1  15:0
		// 16  Mix Group Channel* (QSR only) 0  6  0    0   16  5  16:4 - 16:0
		addWidget(panel,
				  new ComboBoxWidget("Mix Group Channel* (QSR only)", patch,
									 new QSParamModel(patch,16,4,16,0,false),
									 new MidiEditSender(0,0,0,6,0,0),
									 new String[] {"Ch 01","Ch 02","Ch 03",
												   "Ch 04","Ch 05","Ch 06",
												   "Ch 07","Ch 08","Ch 09",
												   "Ch 10","Ch 11","Ch 12",
												   "Ch 13","Ch 14","Ch 15",
												   "Ch 16"}),
				  1,14,1,1,14);
		// 17  General MIDI                  0  0  1    0    1  1  17:0
		// Off, On
		addWidget(panel,
				  new ComboBoxWidget("General Midi", patch,
									 new QSParamModel(patch,17,0,17,0,false),
									 new MidiEditSender(0,0,0,0,0,1),
									 new String[] {"Off","On"}), 1,15,1,1,15);
		// 18  A-D controller reset          0  3  1    0    1  1  18:0
		addWidget(panel,
				  new ComboBoxWidget("A-D Controller Reset", patch,
									 new QSParamModel(patch,18,0,18,0,false),
									 new MidiEditSender(0,0,0,3,0,1),
									 new String[] {"Off","On"}), 1,16,1,1,16);
		// 19  A-D controller mode           0  3  3    0    2  2  19:1 - 19:0
		addWidget(panel,
				  new ComboBoxWidget("A-D Controller Mode", patch,
									 new QSParamModel(patch,19,1,19,0,false),
									 new MidiEditSender(0,0,0,3,0,3),
									 new String[] {"Local","Midi", "Both"}),
				  1,17,1,1,17);

		// Add the panel to the scrollpane
		scrollPane.add(panel, gbc);
		pack();
	}
}
