package synthdrivers.YamahaFS1R;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import core.ComboBoxWidget;
import core.Driver;
import core.JSLDesktop;
import core.JSLFrame;
import core.KnobWidget;
import core.Patch;
import core.PatchEditorFrame;
import core.PatchNameWidget;
import core.SpinnerWidget;
 
/**
	Editor for Formant sequence.  
	@author denis queffeulou mailto:dqueffeulou@free.fr
	@version $Id$
 */
class YamahaFS1RFseqEditor extends PatchEditorFrame
{
	static final Color mColors[] = {Color.blue, Color.red, Color.cyan, Color.gray,
			Color.magenta, Color.green, Color.orange, Color.pink};

	static final String SEQ_PITCH = "P";
	static final String SEQ_VOICEDFREQ = "VF"; 
	static final String SEQ_VOICEDLEVEL = "VL";
	static final String SEQ_UNVOICEDFREQ = "UF";
	static final String SEQ_UNVOICEDLEVEL = "UL";

	public YamahaFS1RFseqEditor(Patch patch)
	{
		super ("Yamaha FS1R Formant sequence Editor",patch);   

		scrollPane.add(buildHeaderWindow());
		
		pack();
		setVisible(true);	
	}

	private Container buildHeaderWindow() {
		Box oPanel = Box.createVerticalBox();
		
		JPanel oPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel1.add(new PatchNameWidget("Name", p, ((Driver) p.getDriver()).getPatchNameSize()));
		oPanel1.add(new ComboBoxWidget("Loop mode", p, new YamahaFS1RFseqDriver.Model((Patch)p, 0x14), new YamahaFS1RFseqDriver.Sender(0x14), new String []{"One way", "Round"}));
		oPanel1.add(new ComboBoxWidget("Pitch mode", p, new YamahaFS1RFseqDriver.Model((Patch)p, 0x17), new YamahaFS1RFseqDriver.Sender(0x17), new String []{"Pitch", "Non-pitch"}));
		YamahaFS1RFseqDriver.Model oFrameModel = new YamahaFS1RFseqDriver.Model((Patch)p, 0x1B);
		oPanel1.add(new ComboBoxWidget("Frame format", p, oFrameModel, new YamahaFS1RFseqDriver.Sender(0x1B), new String []{"128", "256", "384", "512"}));
		oPanel.add(oPanel1);

		JPanel oPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		// les 3 valeurs qui suivent ne doivent pas depasser le frame format
		oPanel2.add(new KnobWidget("Data end", p, 0, 512, 0, new YamahaFS1RFseqDriver.DoubleModel((Patch)p, 0x1E), new YamahaFS1RFseqDriver.Sender(0x1E)));
		oPanel2.add(new KnobWidget("Loop start", p, 0, 512, 0, new YamahaFS1RFseqDriver.DoubleModel((Patch)p, 0x10), new YamahaFS1RFseqDriver.Sender(0x10)));
		oPanel2.add(new KnobWidget("Loop end", p, 0, 512, 0, new YamahaFS1RFseqDriver.DoubleModel((Patch)p, 0x12), new YamahaFS1RFseqDriver.Sender(0x12)));
		oPanel2.add(new KnobWidget("Speed adjust", p, 0, 127, -64, new YamahaFS1RFseqDriver.Model((Patch)p, 0x15), new YamahaFS1RFseqDriver.Sender(0x15)));
		oPanel2.add(new KnobWidget("Tempo vel sens", p, 0, 127, 0, new YamahaFS1RFseqDriver.Model((Patch)p, 0x16), new YamahaFS1RFseqDriver.Sender(0x16)));
		oPanel.add(oPanel2);
		
		JPanel oPanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel3.add(new ComboBoxWidget("Formant note", p, new YamahaFS1RFseqDriver.Model((Patch)p, 0x18), new YamahaFS1RFseqDriver.Sender(0x18), YamahaFS1RPerformanceEditor.mNotes));
		oPanel3.add(new SpinnerWidget("Pitch tuning", p, 0, 126, -63, new YamahaFS1RFseqDriver.Model((Patch)p, 0x19), new YamahaFS1RFseqDriver.Sender(0x19)));
		oPanel3.add(new KnobWidget("Delay", p, 0, 0x63, 0, new YamahaFS1RFseqDriver.Model((Patch)p, 0x1A), new YamahaFS1RFseqDriver.Sender(0x1A)));
		oPanel.add(oPanel3);
		
		JPanel oPanel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton oGraphButton = new JButton("Edit graph");
		oGraphButton.addActionListener(new ActionListener() {
                    public void actionPerformed (ActionEvent e) {
                        JSLFrame oWin = new FSeqGraphWindow();
						JSLDesktop.add(oWin);
                        oWin.setVisible(true);
						oWin.moveToFront();
                    }}
		);
		oPanel4.add(oGraphButton);
		oPanel.add(oPanel4);
		
		return oPanel;
	}
	
	class FSeqGraphWindow extends JSLFrame
	{
		FSeqGraphWindow() {
			super("Formant sequence graphic editor", true, true, true, true);
			setSize(600, 400);
			Container oPane = getContentPane();
			
			oPane.setLayout(new BoxLayout(oPane, BoxLayout.Y_AXIS));

			GraphDisplay oGraph = new GraphDisplay();

			ButtonGroup oRBGroup = new ButtonGroup();
			JPanel oRBPane = new JPanel();
			RadioListener myListener = new RadioListener(oGraph);
			JRadioButton oPitch = new JRadioButton("Pitch");
			oPitch.addActionListener(myListener);
			oPitch.setActionCommand(SEQ_PITCH);
			oRBPane.add(oPitch);
			oRBGroup.add(oPitch);
			oPitch.setSelected(true);
			
			JRadioButton oVoicedFreq = new JRadioButton("Voiced freq");
			oVoicedFreq.addActionListener(myListener);
			oVoicedFreq.setActionCommand(SEQ_VOICEDFREQ);
			oRBPane.add(oVoicedFreq);
			oRBGroup.add(oVoicedFreq);
			
			JRadioButton oVoicedLevel = new JRadioButton("Voiced level");
			oVoicedLevel.addActionListener(myListener);
			oVoicedLevel.setActionCommand(SEQ_VOICEDLEVEL);
			oRBPane.add(oVoicedLevel);
			oRBGroup.add(oVoicedLevel);
			
			JRadioButton oUnvFreq = new JRadioButton("Unvoiced freq");
			oUnvFreq.addActionListener(myListener);
			oUnvFreq.setActionCommand(SEQ_UNVOICEDFREQ);
			oRBPane.add(oUnvFreq);
			oRBGroup.add(oUnvFreq);
			
			JRadioButton oUnvLevel = new JRadioButton("Unvoiced level");
			oUnvLevel.addActionListener(myListener);
			oUnvLevel.setActionCommand(SEQ_UNVOICEDLEVEL);
			oRBPane.add(oUnvLevel);
			oRBGroup.add(oUnvLevel);
			oRBPane.setBorder(new TitledBorder(LineBorder.createGrayLineBorder(), "Sequence", TitledBorder.LEFT, TitledBorder.CENTER));

			oPane.add(oRBPane);
			
			oPane.add(oGraph);
			oGraph.getSequence(SEQ_PITCH);			
			pack();
		}
	} 
	
    /** An ActionListener to change the sequence display. */
    class RadioListener implements ActionListener {
		private GraphDisplay mGraph;
		RadioListener(GraphDisplay aGraph) {
			mGraph = aGraph;
		}
		public void actionPerformed(ActionEvent e) {
			mGraph.getSequence(e.getActionCommand());	    
		}
    }
	
	
	class GraphDisplay extends Canvas 
	{
		private String mSequence;
		private int mNbOp;
		private int mX[];
		private int mY[][] = new int[8][];
		GraphDisplay() {
			setSize(512,400);
		}
		void getSequence(String aSeq) {
			mSequence = aSeq;
			if (aSeq.equals(SEQ_PITCH)) {
				mNbOp = 1;
				mY[0] = YamahaFS1RFseqDriver.getPitch((Patch)p);
				if (mX == null) {
					mX = new int[mY[0].length];
				}
				for (int i = 0; i < mY[0].length; i++) {
					mY[0][i] = (mY[0][i]*400)/0x3FFF; 
					mX[i] = i;
				}
			}
			else {
				mNbOp = 8;
				for (int op = 0; op < mNbOp; op++) {
					if (aSeq.equals(SEQ_VOICEDFREQ)) {
						mY[op] = YamahaFS1RFseqDriver.getFrequencies((Patch)p, op, true);
					}
					else if (aSeq.equals(SEQ_VOICEDLEVEL)) {
						mY[op] = YamahaFS1RFseqDriver.getLevels((Patch)p, op, true);
					}
					else if (aSeq.equals(SEQ_UNVOICEDFREQ)) {
						mY[op] = YamahaFS1RFseqDriver.getFrequencies((Patch)p, op, false);
					}
					else {
						mY[op] = YamahaFS1RFseqDriver.getLevels((Patch)p, op, false);
					}
					if (mX == null) {
						mX = new int[mY[op].length];
					}
					for (int i = 0; i < mY[op].length; i++) {
						mY[op][i] = (mY[op][i]*400)/0x3FFF; 
						mX[i] = i;
					}
				}
			}
			repaint();
		}
		public void paint(Graphics g) {
			g.setColor(Color.black);
			g.fillRect(0,0,getWidth(), getHeight());
			for (int op = 0; op < mNbOp; op++) {
				g.setColor(mColors[op]);
				g.drawPolyline(mX, mY[op], mX.length);
			}
		}
	}
}
