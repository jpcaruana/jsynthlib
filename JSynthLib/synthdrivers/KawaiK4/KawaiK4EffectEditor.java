package synthdrivers.KawaiK4;
import core.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.border.*;

/**
 * @author Gerrit Gehnen
 * @version $Id$
 */
class KawaiK4EffectEditor extends PatchEditorFrame {

    private final String[] effectName  = new String[] {
        " 1  Reverb 1",
        " 2  Reverb 2",
        " 3  Reverb 3",
        " 4  Reverb 4",
        " 5  Gate Reverb",
        " 6  Reverse Gate",
        " 7  Normal Delay",
        " 8  Stereo Panpot Delay",
        " 9  Chorus",
        "10  Overdrive + Flanger",
        "11  Overdrive + Normal Delay",
        "12  Overdrive + Delay",
        "13  Normal Delay + Normal Delay",
        "14  Normal Delay + Stero Pan. Delay",
        "15  Chorus + Normal Delay",
        "16  Chorus + Stereo Pan. Delay"
    };

    private final String[] effectParam1 = new String[] {
        "Pre. Delay",
        "Pre. Delay",
        "Pre. Delay",
        "Pre. Delay",
        "Pre. Delay",
        "Pre. Delay",
        "Feedback",
        "Feedback",
        "Width",
        "Drive",
        "Drive",
        "Drive",
        "Delay 1",
        "Delay 1",
        "Chorus",
        "Chorus"
    };

    private final String[] effectParam2 = new String[] {
        "Rev. Time",
        "Rev. Time",
        "Rev. Time",
        "Rev. Time",
        "Gate Time",
        "Gate Time",
        "Tone",
        "L/R Delay",
        "Feedback",
        "Fl. Type",
        "Delay Time",
        "Rev. Type",
        "Delay 2",
        "Delay 2",
        "Delay",
        "Delay"
    };

    private final String[] effectParam3 = new String[] {
        "Tone",
        "Tone",
        "Tone",
        "Tone",
        "Tone",
        "Tone",
        "Delay",
        "Delay",
        "Rate",
        "1->2 Bal.",
        "1->2 Bal.",
        "1->2 Bal.",
        "1->2 Bal.",
        "1->2 Bal.",
        "1->2 Bal.",
        "1->2 Bal."
    };

    public KawaiK4EffectEditor (Patch patch) {
        super ("Kawai K4 Effect Editor", patch);
	/*
	final Image algoPic1=Toolkit.getDefaultToolkit ().getImage ("synthdrivers/KawaiK4/effect1.gif");
	final Image algoPic2=Toolkit.getDefaultToolkit ().getImage ("synthdrivers/KawaiK4/effect2.gif");
	final ImageIcon algoIcon1=new ImageIcon (algoPic1);
	final ImageIcon algoIcon2=new ImageIcon (algoPic2);
	*/
        final JLabel parameter1 = new JLabel(effectParam1[0]);
        final JLabel parameter2 = new JLabel(effectParam2[0]);
        final JLabel parameter3 = new JLabel(effectParam3[0]);
	final ScrollBarWidget scroll1
	    = new ScrollBarWidget("Send2", patch, 0, 100, 0,
				  new K4Model(patch, 12), new K4Sender(88, 0));
	scroll1.setValue(0);
	final ScrollBarWidget scroll2
	    = new ScrollBarWidget("Send2", patch, 0, 100, 0,
				  new K4Model(patch, 15), new K4Sender(88, 1));
	scroll2.setValue(0);
	final ScrollBarWidget scroll3
	    = new ScrollBarWidget("Send2", patch, 0, 100, 0,
				  new K4Model(patch, 18), new K4Sender(88, 2));
	scroll3.setValue(0);
	final ScrollBarWidget scroll4
	    = new ScrollBarWidget("Send2", patch, 0, 100, 0,
				  new K4Model(patch, 21), new K4Sender(88, 3));
	scroll4.setValue(0);
	final ScrollBarWidget scroll5
	    = new ScrollBarWidget("Send2", patch, 0, 100, 0,
				  new K4Model(patch, 24), new K4Sender(88, 4));
	scroll5.setValue(0);
	final ScrollBarWidget scroll6
	    = new ScrollBarWidget("Send2", patch, 0, 100, 0,
				  new K4Model(patch, 27), new K4Sender(88, 5));
	scroll6.setValue(0);
	final ScrollBarWidget scroll7
	    = new ScrollBarWidget("Send2", patch, 0, 100, 0,
				  new K4Model(patch, 30), new K4Sender(88, 6));
	scroll7.setValue(0);
	final ScrollBarWidget scroll8
	    = new ScrollBarWidget("Send2", patch, 0, 100, 0,
				  new K4Model(patch, 33), new K4Sender(88, 7));
	scroll8.setValue(0);

        // final JLabel picture=new JLabel();
        final EffectAlgoPanel picturePanel = new EffectAlgoPanel();
	/*
	if (patch.sysex[8]>=9)
	    picture.setIcon (algoIcon2);
	else
	    picture.setIcon (algoIcon1);
	*/

        scroll1.setEnabled(patch.sysex[8] >= 9);
        scroll2.setEnabled(patch.sysex[8] >= 9);
        scroll3.setEnabled(patch.sysex[8] >= 9);
        scroll4.setEnabled(patch.sysex[8] >= 9);
        scroll5.setEnabled(patch.sysex[8] >= 9);
        scroll6.setEnabled(patch.sysex[8] >= 9);
        scroll7.setEnabled(patch.sysex[8] >= 9);
        scroll8.setEnabled(patch.sysex[8] >= 9);
        parameter1.setText(effectParam1[patch.sysex[8]]);
        parameter2.setText(effectParam2[patch.sysex[8]]);
        parameter3.setText(effectParam3[patch.sysex[8]]);

	ComboBoxWidget effectTypeWidget
	    = new ComboBoxWidget("Effect Type", patch,
				 new K4Model(patch, 0), new K4Sender(82), effectName);

        effectTypeWidget.cb.addItemListener(new ItemListener() {
		public void itemStateChanged(ItemEvent e) {
		    if (e.getStateChange() == ItemEvent.SELECTED) {
			int i = ((JComboBox) e.getSource()).getSelectedIndex();
			parameter1.setText(effectParam1[i]);
			parameter2.setText(effectParam2[i]);
			parameter3.setText(effectParam3[i]);
			//System.out.println("Effect selected:"+i);
			scroll1.setEnabled(i >= 9);
			scroll2.setEnabled(i >= 9);
			scroll3.setEnabled(i >= 9);
			scroll4.setEnabled(i >= 9);
			scroll5.setEnabled(i >= 9);
			scroll6.setEnabled(i >= 9);
			scroll7.setEnabled(i >= 9);
			scroll8.setEnabled(i >= 9);
			picturePanel.setEffectNumber(i);
			/*
			if (i>=9)
			    picture.setIcon (algoIcon2);
			else
			    picture.setIcon (algoIcon1);
			*/
		    }
		}
	    }
					    );

        // Common Pane
        picturePanel.setEffectNumber(patch.sysex[8]);
        gbc.weightx = 0;

        gbc.gridx = 1;
        gbc.gridy = 0;
        //scrollPane.add(picture,gbc);
        scrollPane.add(picturePanel, gbc);

        picturePanel.repaint();
        JPanel cmnPane = new JPanel();
        cmnPane.setLayout(new GridBagLayout());

        addWidget(cmnPane, effectTypeWidget, 0, 3, 2, 1, 3);
        addWidget(cmnPane, new LabelWidget(parameter1), 0, 4, 1, 1, 1);
        addWidget(cmnPane, new LabelWidget(parameter2), 0, 5, 1, 1, 1);
        addWidget(cmnPane, new LabelWidget(parameter3), 0, 6, 1, 1, 1);

        addWidget(cmnPane,
		  new ScrollBarWidget("", patch, 0, 7, 0,
				      new K4Model(patch, 1),
				      new K4Sender(83)),
		  1, 4, 1, 1, 5);
        addWidget(cmnPane,
		  new ScrollBarWidget("", patch, 0, 7, 0,
				      new K4Model(patch, 2),
				      new K4Sender(84)),
		  1, 5, 1, 1, 5);
        addWidget(cmnPane,
		  new ScrollBarWidget("", patch, 0, 30, 0,
				      new K4Model(patch, 3),
				      new K4Sender(85)),
		  1, 6, 1, 1, 5);
        cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					   "Common", TitledBorder.CENTER, TitledBorder.CENTER));
        gbc.gridx  = 0;
        gbc.gridy = 0;

        scrollPane.add(cmnPane, gbc);

        JPanel outputPane1 = new JPanel();
        outputPane1.setLayout(new GridBagLayout());
        addWidget(outputPane1,
		  new ScrollBarWidget("Pan", patch, 0, 14, -7,
				      new K4Model(patch, 10),
				      new K4Sender(86, 0)),
		  0, 3, 2, 1, 3);
        addWidget(outputPane1,
		  new ScrollBarWidget("Send1", patch, 0, 100, 0,
				      new K4Model(patch, 11),
				      new K4Sender(87, 0)),
		  2, 3, 2, 1, 3);
        addWidget(outputPane1, scroll1, 4, 3, 2, 1, 3);
        outputPane1.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					       "Output A", TitledBorder.CENTER, TitledBorder.CENTER));
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        scrollPane.add(outputPane1, gbc);

        JPanel outputPane2 = new JPanel();
        outputPane2.setLayout(new GridBagLayout());
        addWidget(outputPane2,
		  new ScrollBarWidget("Pan", patch, 0, 14, -7,
				      new K4Model(patch, 13),
				      new K4Sender(86, 1)),
		  0, 3, 2, 1, 3);
        addWidget(outputPane2,
		  new ScrollBarWidget("Send1", patch, 0, 100, 0,
				      new K4Model(patch, 14),
				      new K4Sender(87, 1)),
		  2, 3, 2, 1, 3);
        addWidget(outputPane2, scroll2, 4, 3, 2, 1, 3);
        outputPane2.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					       "Output B", TitledBorder.CENTER, TitledBorder.CENTER));
        gbc.gridx = 0;
        gbc.gridy  = GridBagConstraints.RELATIVE;
        scrollPane.add(outputPane2, gbc);
        //scrollPane.add(picturePanel,gbc);

        JPanel outputPane3 = new JPanel();
        outputPane3.setLayout(new GridBagLayout());
        addWidget(outputPane3,
		  new ScrollBarWidget("Pan", patch, 0, 14, -7,
				      new K4Model(patch, 16),
				      new K4Sender(86, 2)),
		  0, 3, 2, 1, 3);
        addWidget(outputPane3,
		  new ScrollBarWidget("Send1", patch, 0, 100, 0,
				      new K4Model(patch, 17),
				      new K4Sender(87, 2)),
		  2, 3, 2, 1, 3);
        addWidget(outputPane3, scroll3, 4, 3, 2, 1, 3);
        outputPane3.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					       "Output C", TitledBorder.CENTER, TitledBorder.CENTER));
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        scrollPane.add(outputPane3, gbc);

        JPanel outputPane4 = new JPanel();
        outputPane4.setLayout(new GridBagLayout());
        addWidget(outputPane4,
		  new ScrollBarWidget("Pan", patch, 0, 14, -7,
				      new K4Model(patch, 19),
				      new K4Sender(86, 3)),
		  0, 0, 2, 1, 3);
        addWidget(outputPane4,
		  new ScrollBarWidget("Send1", patch, 0, 100, 0,
				      new K4Model(patch, 20),
				      new K4Sender(87, 3)),
		  2, 0, 2, 1, 3);
        addWidget(outputPane4, scroll4, 4, 0, 2, 1, 3);
        outputPane4.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					       "Output D", TitledBorder.CENTER, TitledBorder.CENTER));
        gbc.gridx = 0;
        gbc.gridy =  GridBagConstraints.RELATIVE;
        scrollPane.add(outputPane4, gbc);

        JPanel outputPane5 = new JPanel();
        outputPane5.setLayout(new GridBagLayout());
        addWidget(outputPane5,
		  new ScrollBarWidget("Pan", patch, 0, 14, -7,
				      new K4Model(patch, 22),
				      new K4Sender(86, 4)),
		  0, 3, 2, 1, 3);
        addWidget(outputPane5,
		  new ScrollBarWidget("Send1", patch, 0, 100, 0,
				      new K4Model(patch, 23),
				      new K4Sender(87, 4)),
		  2, 3, 2, 1, 3);
        addWidget(outputPane5, scroll5, 4, 3, 2, 1, 3);
        outputPane5.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					       "Output E", TitledBorder.CENTER, TitledBorder.CENTER));
        gbc.gridx  = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        scrollPane.add(outputPane5, gbc);

        JPanel outputPane6 = new JPanel();
        outputPane6.setLayout(new GridBagLayout());
        addWidget(outputPane6,
		  new ScrollBarWidget("Pan", patch, 0, 14, -7,
				      new K4Model(patch, 25),
				      new K4Sender(86, 5)),
		  0, 3, 2, 1, 3);
        addWidget(outputPane6,
		  new ScrollBarWidget("Send1", patch, 0, 100, 0,
				      new K4Model(patch, 26),
				      new K4Sender(87, 5)),
		  2, 3, 2, 1, 3);
        addWidget(outputPane6, scroll6, 4, 3, 2, 1, 3);
        outputPane6.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					       "Output F", TitledBorder.CENTER, TitledBorder.CENTER));
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        scrollPane.add(outputPane6, gbc);

        JPanel outputPane7 = new JPanel();
        outputPane7.setLayout(new GridBagLayout());
        addWidget(outputPane7,
		  new ScrollBarWidget("Pan", patch, 0, 14, -7,
				      new K4Model(patch, 28),
				      new K4Sender(86, 6)),
		  0, 0, 2, 1, 3);
        addWidget(outputPane7,
		  new ScrollBarWidget("Send1", patch, 0, 100, 0,
				      new K4Model(patch, 29),
				      new K4Sender(87, 6)),
		  2, 0, 2, 1, 3);
        addWidget(outputPane7, scroll7, 4, 0, 2, 1, 3);
        outputPane7.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					       "Output G", TitledBorder.CENTER, TitledBorder.CENTER));
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        scrollPane.add(outputPane7, gbc);

        JPanel outputPane8 = new JPanel();
        outputPane8.setLayout(new GridBagLayout());
        addWidget(outputPane8,
		  new ScrollBarWidget("Pan", patch, 0, 14, -7,
				      new K4Model(patch, 31),
				      new K4Sender(86, 7)),
		  0, 3, 2, 1, 3);
        addWidget(outputPane8,
		  new ScrollBarWidget("Send1", patch, 0, 100, 0,
				      new K4Model(patch, 32),
				      new K4Sender(87, 7)),
		  2, 3, 2, 1, 3);
        addWidget(outputPane8, scroll8, 4, 3, 2, 1, 3);
        outputPane8.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
					       "Output H", TitledBorder.CENTER, TitledBorder.CENTER));
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        scrollPane.add(outputPane8, gbc);

        pack();
        show();
    }
}
