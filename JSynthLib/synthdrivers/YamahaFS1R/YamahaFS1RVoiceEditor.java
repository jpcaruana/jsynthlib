package synthdrivers.YamahaFS1R;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import core.CheckBoxWidget;
import core.ComboBoxWidget;
import core.EnvelopeWidget;
import core.IPatch;
import core.JSLDesktop;
import core.JSLFrame;
import core.KnobLookupWidget;
import core.KnobWidget;
import core.ParamModel;
import core.Patch;
import core.PatchEditorFrame;
import core.PatchNameWidget;
import core.SpinnerWidget;
import core.SysexSender;

/**
	Voice editor.
	TODO : pour selectionner le numero de partie performance il faut le faire
	avant de construire les composants.
	@author denis queffeulou mailto:dqueffeulou@free.fr
	@version $Id$
*/
class YamahaFS1RVoiceEditor extends PatchEditorFrame
{
	private static final String [] KbdBreakPointName = new String [] {	"C-2","C#-2","D-2","D#-2","E-2","F-2","F#-2","G-2","G#-2","A-2","A#-2","B-2",
	"C-1","C#-1","D-1","D#-1","E-1","F-1","F#-1","G-1","G#-1","A-1","A#-1","B-1",
	"C0","C#0","D0","D#0","E0","F0","F#0","G0","G#0","A0","A#0","B0",
	"C1","C#1","D1","D#1","E1","F1","F#1","G1","G#1","A1","A#1","B1",
	"C2","C#2","D2","D#2","E2","F2","F#2","G2","G#2","A2","A#2","B2",
	"C3","C#3","D3","D#3","E3","F3","F#3","G3","G#3","A3","A#3","B3",
	"C4","C#4","D4","D#4","E4","F4","F#4","G4","G#4","A4","A#4","B4",
	"C5","C#5","D5","D#5","E5","F5","F#5","G5","G#5","A5","A#5","B5",
	"C6","C#6","D6","D#6","E6","F6","F#6","G6","G#6","A6","A#6","B6",
	"C7","C#7","D7","D#7","E7","F7","F#7","G7","G#7","A7","A#7","B7",
    "C8"};

	private static final String [] LSBreakPointName = new String [] {
	"A-1","A#-1","B-1",
	"C0","C#0","D0","D#0","E0","F0","F#0","G0","G#0","A0","A#0","B0",
	"C1","C#1","D1","D#1","E1","F1","F#1","G1","G#1","A1","A#1","B1",
	"C2","C#2","D2","D#2","E2","F2","F#2","G2","G#2","A2","A#2","B2",
	"C3","C#3","D3","D#3","E3","F3","F#3","G3","G#3","A3","A#3","B3",
	"C4","C#4","D4","D#4","E4","F4","F#4","G4","G#4","A4","A#4","B4",
	"C5","C#5","D5","D#5","E5","F5","F#5","G5","G#5","A5","A#5","B5",
	"C6","C#6","D6","D#6","E6","F6","F#6","G6","G#6","A6","A#6","B6",
	"C7","C#7","D7","D#7","E7","F7","F#7","G7","G#7","A7","A#7","B7",
    "C8"};

	private static final String [] FreqCoarseName = new String [] {
	"0.5","1","2","3","4","5","6","7","8","9","10",
		"11","12","13","14","15","16","17","18","19","20",
		"21","22","23","24","25","26","27","28","29","30","31"
	};

	private static final String[] mUnvoicedFreCoarseNames = new String[] {
		"0.000", "0.013", "0.026", "0.053", "0.107", "0.215", "0.429", "0.859",
		"1.719", "3.439", "6.879", "13.75", "27.51", "55.03", "110.0", "220.1",
		"440.2", "880.5", "1761", "3522", "7044", "14088"
	};

	static final String[] mCategories = new String []{"No assign", "Piano", "Chromatic percussion","Organ","Guitar","Bass","Strings/orchestral","Ensemble","Brass","Reed","Pipe","Synth lead","Synth pad","Synth sound effects","Ethnic","Percussive","Sound effects","Drum","Synth Comping","Vocal","Combination","Material wave","Sequence"};

	private static final String[] mAttenuations = new String[] {"0.0dB", "1.5dB", "3.0dB", "4.5dB", "6.0dB", "7.5dB", "9.0dB", "10.5dB", "12.0dB", "13.5dB", "15.0dB", "16.5dB", "18.0dB", "19.5dB", "21.0dB", "22.5dB"};

	static ImageIcon[] mAlgoImages;

	static final int HGAP = 4;
	static final int VGAP = 0;

	static final int ENV_WIDTH = 300;
	static final int ENV_HEIGHT = 200;

	/** part number in performance 1..4 */
	protected int mPart = 1;

	protected int mBankNumber;

	/**
		Default edit the part 1 of current performance.
	*/
	public YamahaFS1RVoiceEditor(Patch patch)
	{
		super ("Yamaha FS1R Voice Editor",patch);
		setupUI();
	}

	/**
		@param aPart performance part number 1..4
		@param aBankNumber number of voice in the bank
	*/
	public YamahaFS1RVoiceEditor(Patch patch, int aPart, int aBankNumber)
	{
		super ("Yamaha FS1R Voice Editor (part "+aPart+")", patch);
		mPart = aPart;
		mBankNumber = aBankNumber;
		setupUI();
	}

	/** send patch to current performance part */
	// It's not good idea to override PatchBasket method.
	public void sendSelectedPatch()
	{
 	  p.getDriver().calculateChecksum(p);
	  ((YamahaFS1RVoiceDriver)(p.getDriver())).sendPatch((Patch)p, getPart());
 	}

	private void setupUI() {
		if (mAlgoImages == null) {
			mAlgoImages = new ImageIcon[88];
			for (int i = 0; i < 88; i++) {
				mAlgoImages[i]=new ImageIcon(this.getClass().getResource("images/a"+(i+1)+".gif"));
			}
		}
		JTabbedPane oTabs = new JTabbedPane();
		scrollPane.add(oTabs);

		oTabs.add(buildCommonWindow(), "Common");
		oTabs.add(buildFilterWindow(), "Filter");
		oTabs.add(buildPitchWindow(), "Pitch");
		for (int op = 1; op <= 8; op++) {
			// voiced operator
			oTabs.add(buildOperatorWindow(op), "V "+op);
		}
		for (int op = 1; op <= 8; op++) {
			// unvoiced operator
			oTabs.add(buildUnvoicedWindow(op), "U "+op);
		}
		pack();
		show();
	}

	/**
		Common controls
	*/
	private Container buildCommonWindow() {
		Patch patch = (Patch)p;
		Box oCommonOthersPane = Box.createVerticalBox();
		//JPanel oCommonOthersPane = new JPanel(new GridLayout(10, 1));

		JButton oHelp = new JButton("Help");
		oHelp.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				JSLFrame oHelpWin = new HelpWindow();
				JSLDesktop.add(oHelpWin);
				oHelpWin.setVisible(true);
			}}
		);

		JPanel oMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, HGAP, 0));
		oMenu.add(oHelp);
		oMenu.add(new JLabel("Performance part "+mPart+" - bank number "+mBankNumber));
		oCommonOthersPane.add(oMenu);

		// Common Pane (common-others)
		JPanel oCommonPane = new JPanel(new FlowLayout(FlowLayout.LEFT, HGAP, 0));
		oCommonPane.add(new PatchNameWidget(" Name  ",patch));
		oCommonPane.add(new SpinnerWidget("Note shift", patch, 0, 48, -24, new FS1RModel(patch, 0x1E), new FS1RSender(0x1E)));
		oCommonPane.add(new ComboBoxWidget("Category", patch, new FS1RModel(patch, 0x0E), new FS1RSender(0x0E), mCategories));
		oCommonPane.add(new KnobWidget("Algorithm", patch, 0, 87, 1, new FS1RModel(patch, 0x2C), new FS1RSender(0x2C), mAlgoImages));
		oCommonPane.add(new SpinnerWidget("Feedback", patch, 0, 7, 0, new FS1RModel(patch, 0x3D), new FS1RSender(0x3D)));

		oCommonPane.setBorder(new TitledBorder(LineBorder.createGrayLineBorder(), "Common", TitledBorder.LEFT, TitledBorder.CENTER));
		oCommonOthersPane.add(oCommonPane);

		// common LFO1
		JPanel oLFO1Pane = new JPanel(new FlowLayout(FlowLayout.LEFT, HGAP, VGAP));
		oLFO1Pane.add(new ComboBoxWidget("Wave", patch, new FS1RModel(patch, 0x10), new FS1RSender(0x10), new String []{"Triangle", "Saw dawn", "Saw up","Square","Sine","Sample & hold"}));
		oLFO1Pane.add(new KnobWidget("Speed", patch, 0, 99, 0, new FS1RModel(patch, 0x11), new FS1RSender(0x11)));
		oLFO1Pane.add(new KnobWidget("Delay", patch, 0, 99, 0, new FS1RModel(patch, 0x12), new FS1RSender(0x12)));
		oLFO1Pane.add(new CheckBoxWidget("Key sync",patch,new FS1RModel(patch,0x13), new FS1RSender(0x13)));
		oLFO1Pane.add(new KnobWidget("Pitch Mod", patch, 0, 99, 0, new FS1RModel(patch, 0x15), new FS1RSender(0x15)));
		oLFO1Pane.add(new KnobWidget("Amp Mod", patch, 0, 99, 0, new FS1RModel(patch, 0x16), new FS1RSender(0x16)));
		oLFO1Pane.add(new KnobWidget("Freq Mod", patch, 0, 99, 0, new FS1RModel(patch, 0x17), new FS1RSender(0x17)));
		oLFO1Pane.add(new KnobWidget("Filter Mod", patch, 0, 99, 0, new FS1RModel(patch, 0x59), new FS1RSender(0x59)));
		oLFO1Pane.setBorder(new TitledBorder(LineBorder.createGrayLineBorder(), "LFO1",TitledBorder.LEFT,TitledBorder.CENTER));
		oCommonOthersPane.add(oLFO1Pane);

		// common LFO2
		JPanel oLFO2Pane = new JPanel(new FlowLayout(FlowLayout.LEFT, HGAP, VGAP));

		oLFO2Pane.add(new ComboBoxWidget("Wave", patch, new FS1RModel(patch, 0x18), new FS1RSender(0x18), new String []{"Triangle", "Saw dawn", "Saw up","Square","Sine","Sample & hold"}));
		oLFO2Pane.add(new KnobWidget("Speed", patch, 0, 99, 0, new FS1RModel(patch, 0x19), new FS1RSender(0x19)));
		oLFO2Pane.add(new CheckBoxWidget("Key sync",patch,new FS1RModel(patch,0x1D), new FS1RSender(0x1D)));
		oLFO2Pane.add(new ComboBoxWidget("Phase", patch, new FS1RModel(patch, 0x1C), new FS1RSender(0x1C), new String []{"0¡", "90¡", "180¡", "270¡"}));
		oLFO2Pane.add(new KnobWidget("Filter Mod", patch, 0, 99, 0, new FS1RModel(patch, 0x5A), new FS1RSender(0x5A)));

		oLFO2Pane.setBorder(new TitledBorder(LineBorder.createGrayLineBorder(), "LFO2",TitledBorder.LEFT,TitledBorder.CENTER));
		oCommonOthersPane.add(oLFO2Pane);

		// tableau des switch Fseq
		Component oTable1Check[][] = new Component[8][3];
		for (int i = 0; i < 8; i++) {
			if (i < 7) {
				oTable1Check[i][0] = new CheckBoxWidget("", patch, new BitModel(patch, 0x29, 0, 1 << i, i), new BitSender(patch, 0x29));
				oTable1Check[i][1] = new CheckBoxWidget("", patch, new BitModel(patch, 0x2B, 0, 1 << i, i), new BitSender(patch, 0x2B));
			}
			else {	// 7
				oTable1Check[i][0] = new CheckBoxWidget("", patch, new BitModel(patch, 0x28, 0, 1, 0), new FS1RSender(0x28));
				oTable1Check[i][1] = new CheckBoxWidget("", patch, new BitModel(patch, 0x2A, 0, 1, 0), new FS1RSender(0x2A));
			}
			// Fseq track number
			SpinnerWidget oFseqTrack = new SpinnerWidget("", p, 0, 7, 1, new BitModel((Patch)p, 0x05, i+1, 7, 0), new BitSender((Patch)p, 0x05, i+1));
			oFseqTrack.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			oTable1Check[i][2] = oFseqTrack;

		}
		JTable oTable = new JTable(new SwitchTableModel(oTable1Check));
		JScrollPane oSwitchPanel=new JScrollPane(oTable);
		oSwitchPanel.setBorder(new TitledBorder(LineBorder.createGrayLineBorder(), "Fseq switches",TitledBorder.LEFT,TitledBorder.CENTER));

		for (int i = 1; i < 9; i++) {
			TableColumn column = oTable.getColumnModel().getColumn(i);
			column.setPreferredWidth(30);
			CheckBoxCellRenderer checkBoxCellRenderer = new CheckBoxCellRenderer(patch, oTable1Check);
			column.setCellRenderer (checkBoxCellRenderer);
			CheckBoxCellEditor checkBoxCellEditor = new CheckBoxCellEditor(oTable1Check);
			column.setCellEditor(checkBoxCellEditor);
		}
		oTable.setIntercellSpacing(new Dimension(0,0));
		oTable.setRowHeight(oTable.getRowHeight()+10);
		oTable.setShowGrid(false);
		oTable.setRowMargin(0);
		//oTable.setAlignmentX(Component.LEFT_ALIGNMENT);
		TableColumn column = oTable.getColumnModel().getColumn(0);
		column.setPreferredWidth(80);
		oSwitchPanel.setPreferredSize(new Dimension(500, 110));
		oCommonOthersPane.add(oSwitchPanel);

		return oCommonOthersPane;
	}

	private static class SwitchTableModel extends AbstractTableModel {
        final String[] columnNames = {"Fseq", "Op1", "Op2", "Op3", "Op4", "Op5", "Op6", "Op7", "Op8"};
		private Object mTable[];
		SwitchTableModel(Object aTable[]) {
				mTable = aTable;
		}
		public int getColumnCount() {return columnNames.length;}
		public String getColumnName(int col) { return columnNames[col];}
		public int getRowCount() { return 3;}
		public Class getColumnClass(int c) {return getValueAt(0, c).getClass();}
		public Object getValueAt(int row, int col) {
			if (col == 0) {
				if (row == 0)
					return "Voiced";
				else if (row == 1)
					return "Unvoiced";
				else if (row == 2)
					return "Track";
			}
			return new Integer(0);
		}
        public boolean isCellEditable(int row, int col) {
             return col > 0;
        }
        public void setValueAt(Object value, int row, int col) {
			fireTableCellUpdated(row, col);
        }
	}

	class CheckBoxCellRenderer  implements TableCellRenderer {
		IPatch patch;
		private Component[][] mCheck;
		CheckBoxCellRenderer(IPatch p, Component[][] aCheck) {
			patch=p;
			mCheck = aCheck;
		}
		public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
			return mCheck[col-1][row];
		}
	}
	class CheckBoxCellEditor implements TableCellEditor  {
		private Component[][] mCheck;
		CheckBoxCellEditor(Component[][] aCheck) {
			mCheck = aCheck;
		}
		public Component getTableCellEditorComponent (JTable table, Object value, boolean isSelected, int row, int col) {
			return mCheck[col-1][row];
		}
		public void cancelCellEditing(){};
		public Object getCellEditorValue() {return new Integer(0);}
		public void addCellEditorListener (CellEditorListener l) {}
		public boolean isCellEditable (EventObject e) {return true;}
		public void removeCellEditorListener (CellEditorListener l) {}
		public boolean shouldSelectCell (EventObject e) {return true;}
		public boolean stopCellEditing() {return true;}
	}

	/**
		FILTER EG
	*/
	private Container buildFilterWindow() {
		Box oFilterPane = Box.createVerticalBox();

		JPanel oPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//addWidget(oFilterPane,new CheckBoxWidget("Part switch",patch,new FS1RModel((Patch)p,0x1D), new FS1RSender(0x1D)), 0, 0, 1, 1, -1);
		oPanel1.add(new ComboBoxWidget("Type", p, new FS1RModel((Patch)p, 0x54), new FS1RSender(0x54), new String []{"Low pass 24db", "Low pass 18db", "Low pass 12db", "High pass", "Band pass", "Notch"}));
		oPanel1.add(new KnobWidget("Input gain", p, 0, 0x18, -12, new FS1RModel((Patch)p, 0x5D), new FS1RSender(0x5D)));
		oPanel1.add(new KnobWidget("Cutoff", p, 0, 116, -16, new FS1RModel((Patch)p, 0x57), new FS1RSender(0x57)));
		oPanel1.add(new KnobWidget("Resonance", p, 0, 116, -16, new FS1RModel((Patch)p, 0x55), new FS1RSender(0x55)));
		oFilterPane.add(oPanel1);

		JPanel oPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel2.add(new KnobWidget("Resonance", p, 0, 14, -7, new FS1RModel((Patch)p, 0x56), new FS1RSender(0x56)));
		oPanel2.add(new KnobWidget("EG attack time", p, 0, 14, -7, new FS1RModel((Patch)p, 0x6E), new FS1RSender(0x6E)));
		oPanel2.add(new KnobWidget("EG depth", p, 0, 14, -7, new FS1RModel((Patch)p, 0x58), new FS1RSender(0x58)));
		oPanel2.setBorder(new TitledBorder(LineBorder.createGrayLineBorder(),"Velocity sensitivity",TitledBorder.LEFT,TitledBorder.CENTER));  
		oFilterPane.add(oPanel2);

		JPanel oPanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel3.add(new KnobWidget("EG time scaling", p, 0, 7, 0, new FS1RModel((Patch)p, 0x6E), new FS1RSender(0x6E)));
		oPanel3.add(new KnobWidget("EG depth", p, 0, 127, -64, new FS1RModel((Patch)p, 0x64), new FS1RSender(0x64)));
		oPanel3.add(new KnobWidget("Freq scaling", p, 0, 127, -64, new FS1RModel((Patch)p, 0x5B), new FS1RSender(0x5B)));
		oPanel3.add(new ComboBoxWidget("Freq scale Breakpoint",p,new FS1RModel((Patch)p,0x5C),new FS1RSender(0x5C), KbdBreakPointName));
		oFilterPane.add(oPanel3);

		JPanel oPanel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		EnvelopeWidget oEnv = new EnvelopeWidget("Envelope Generator",p,new EnvelopeWidget.Node []
		{
        new EnvelopeWidget.Node(0,0,null, 50, 50,null,0,false,null,null,null,null),
        new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x69), 0, 100,new FS1RModel((Patch)p, 0x66), 0,false,new FS1RSender(0x69), new FS1RSender(0x66),"R1","L1"),
        new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x6A), 0, 100,new FS1RModel((Patch)p, 0x67), 0,false,new FS1RSender(0x6A), new FS1RSender(0x67),"R2","L2"),
        new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x6B), 0, 100,new FS1RModel((Patch)p, 0x68), 0,false,new FS1RSender(0x6B), new FS1RSender(0x68),"R3","L3"),
        new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x6C), 0, 100,new FS1RModel((Patch)p, 0x65), 0,false,new FS1RSender(0x6C), new FS1RSender(0x65),"R4","L4")
		});
		oEnv.setPreferredSize(new Dimension(ENV_WIDTH, ENV_HEIGHT));
		oPanel4.add(oEnv);
		oFilterPane.add(oPanel4);

		return oFilterPane;
	}

	/**
		PITCH EG
	*/
	private Container buildPitchWindow() {
		//Box oPane = Box.createVerticalBox();

		JPanel oP1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		oP1.add(new KnobWidget("Time scaling", p, 0, 7, 0, new FS1RModel((Patch)p, 0x3C), new FS1RSender(0x3C)));
		oP1.add(new ComboBoxWidget("Range", p, new FS1RModel((Patch)p, 0x3B), new FS1RSender(0x3B), new String []{"8 octaves", "2 octaves", "1 octave", "1/2 octave"}));
		oP1.add(new KnobWidget("Vel sens", p, 0, 7, 0, new FS1RModel((Patch)p, 0x27), new FS1RSender(0x27)));

		JPanel oPanel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		EnvelopeWidget oEnv = new EnvelopeWidget("Envelope Generator",p,new EnvelopeWidget.Node []
		{
        new EnvelopeWidget.Node(0,0,null, 50, 50,null,0,false,null,null,null,null),
        new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x23), 0, 100,new FS1RModel((Patch)p, 0x1F), 0,false,new FS1RSender(0x23), new FS1RSender(0x1F),"R1","L1"),
        new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x24), 0, 100,new FS1RModel((Patch)p, 0x20), 0,false,new FS1RSender(0x24), new FS1RSender(0x20),"R2","L2"),
        new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x25), 0, 100,new FS1RModel((Patch)p, 0x21), 0,false,new FS1RSender(0x25), new FS1RSender(0x21),"R3","L3"),
        new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x26), 0, 100,new FS1RModel((Patch)p, 0x22), 0,false,new FS1RSender(0x26), new FS1RSender(0x22),"R4","L4")
		});
		oEnv.setPreferredSize(new Dimension(ENV_WIDTH, ENV_HEIGHT));
		oPanel4.add(oEnv);
		Box oPane = Box.createVerticalBox();
		oPane.add(oP1);
		oPane.add(oPanel4);
		return oPane;
	}

	/**
	 * Build a window for one voiced operator
     * @param aOp Operator number (1 ... 8)
	*/
	private Container buildOperatorWindow(int aOp) {
		Box oPane = Box.createVerticalBox();
		// selon la forme d'onde les reglages sont differents
		JPanel oSinePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		FS1RModel oFreqModeM = new BitModel((Patch)p, 0x05, aOp, 0x40, 6);
		BitModel oKeySyncM = new BitModel((Patch)p, 0, aOp, 0x40, 6);
		oSinePanel.add(new ComboBoxWidget("Freq mode", p, oFreqModeM, new BitSender((Patch)p, 0x05, aOp), new String []{"Ratio", "Fixed"}));
		oSinePanel.add(new CheckBoxWidget("Key sync", p, oKeySyncM, new BitSender((Patch)p, 0, aOp)));

		JPanel oAllPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		FS1RModel oSkirtM = new BitModel((Patch)p, 0x05, aOp, 0x38, 3);
		oAllPanel.add(new ComboBoxWidget("Freq mode", p, oFreqModeM, new BitSender((Patch)p, 0x05, aOp), new String []{"Ratio", "Fixed"}));
		oAllPanel.add(new SpinnerWidget("Skirt", p, 0, 7, 0, oSkirtM, new BitSender((Patch)p, 0x05, aOp)));
		oAllPanel.add(new CheckBoxWidget("Key sync", p, oKeySyncM, new BitSender((Patch)p, 0, aOp)));

		JPanel oResPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oResPanel.add(new ComboBoxWidget("Freq mode", p, oFreqModeM, new BitSender((Patch)p, 0x05, aOp), new String []{"Ratio", "Fixed"}));
		oResPanel.add(new SpinnerWidget("Skirt", p, 0, 7, 0, oSkirtM, new BitSender((Patch)p, 0x05, aOp)));
		oResPanel.add(new KnobWidget("Resonance", p, 0, 99, 0, new FS1RModel((Patch)p, 0x06, aOp), new FS1RSender(0x06, aOp)));
		oResPanel.add(new CheckBoxWidget("Key sync", p, oKeySyncM, new BitSender((Patch)p, 0, aOp)));

		JPanel oFormantPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oFormantPanel.add(new SpinnerWidget("Transpose", p, 0, 48, -24, new BitModel((Patch)p, 0, aOp, 0x3F, 0), new BitSender((Patch)p, 0, aOp)));
		oFormantPanel.add(new KnobWidget("Band width", p, 0, 99, 0, new FS1RModel((Patch)p, 0x06, aOp), new FS1RSender(0x06, aOp)));
		oFormantPanel.add(new SpinnerWidget("Skirt", p, 0, 7, 0, oSkirtM, new BitSender((Patch)p, 0x05, aOp)));

		String[] oWaves = new String[]{"Sine", "All 1", "All 2", "Odd 1", "Odd 2", "Res 1", "Res 2", "Formant"};

		JPanel oCardPane = new JPanel(new CardLayout());
		oCardPane.add("Sine", oSinePanel);
		oCardPane.add("All", oAllPanel);
		oCardPane.add("Res", oResPanel);
		oCardPane.add("Formant", oFormantPanel);

		WaveComboListener oComboListener = new WaveComboListener(oCardPane, oWaves);
		oComboListener.notifyChange(new BitModel((Patch)p, 0x04, aOp, 7, 0).get());
		
		JPanel oPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel1.add(new ComboActionWidget("Wave", p, new BitModel((Patch)p, 0x04, aOp, 7, 0), new BitSender((Patch)p, 0x04, aOp), oWaves, oComboListener));
		oPanel1.add(new KnobLookupWidget("Freq coarse", p, new FS1RModel((Patch)p, 0x01, aOp), new FS1RSender(0x01, aOp), FreqCoarseName));
		oPanel1.add(new KnobWidget("Freq fine", p, 0, 0x7F, 0, new FS1RModel((Patch)p, 0x02, aOp), new FS1RSender(0x02, aOp)));
		oPanel1.add(new KnobWidget("Freq scaling", p, 0, 99, 0, new FS1RModel((Patch)p, 0x03, aOp), new FS1RSender(0x03, aOp)));
		oPanel1.add(new KnobWidget("Detune", p, 0, 30, -15, new FS1RModel((Patch)p, 0x07, aOp), new FS1RSender(0x07, aOp)));
		oPanel1.add(oCardPane);
		oPane.add(oPanel1);

		JPanel oPanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel3.add(new ComboBoxWidget("Left curve", p, new FS1RModel((Patch)p, 0x1A, aOp), new FS1RSender(0x1A, aOp), new String []{"-lin", "-exp", "+lin", "+exp"}));
		oPanel3.add(new ComboBoxWidget("Breakpoint", p, new FS1RModel((Patch)p, 0x17, aOp), new FS1RSender(0x17), LSBreakPointName));		
		oPanel3.add(new ComboBoxWidget("Right curve", p, new FS1RModel((Patch)p, 0x1B, aOp), new FS1RSender(0x1B, aOp), new String []{"-lin", "-exp", "+lin", "+exp"}));
		oPanel3.add(new KnobWidget("Left depth", p, 0, 99, 0, new FS1RModel((Patch)p, 0x18, aOp), new FS1RSender(0x18, aOp)));		
		oPanel3.add(new KnobWidget("Right depth", p, 0, 99, 0, new FS1RModel((Patch)p, 0x19, aOp), new FS1RSender(0x19, aOp)));
		oPanel3.setBorder(new TitledBorder(LineBorder.createGrayLineBorder(),"Level Scaling",TitledBorder.LEFT,TitledBorder.CENTER));  
		oPane.add(oPanel3);

		JPanel oPanel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel4.add(new KnobLookupWidget("Attenuation", p, new FS1RModel((Patch)p, 0x2D+aOp-1), new FS1RSender(0x2D+aOp-1), mAttenuations));
		oPanel4.add(new KnobWidget("Output level", p, 0, 99, 0, new FS1RModel((Patch)p, 0x16, aOp), new FS1RSender(0x16, aOp)));
		oPane.add(oPanel4);

//		JPanel oPanel5 = new JPanel();
//		oPanel5.setLayout(new FlowLayout(FlowLayout.LEFT));
		Box oPanel5 = Box.createHorizontalBox();
		EnvelopeWidget oEnv = new EnvelopeWidget("Amplitude Envelope Generator",p,new EnvelopeWidget.Node []
		{
        new EnvelopeWidget.Node(0,0,null, 0, 0,null,0,false,null,null,null,null),
        new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x14, aOp), 0, 0, null, 0, false, new FS1RSender(0x14), null, "Hold", null),
        new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x10, aOp), 0, 99,new FS1RModel((Patch)p, 0x0C, aOp), 0,false,new FS1RSender(0x10, aOp), new FS1RSender(0x0C, aOp),"R1","L1"),
        new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x11, aOp), 0, 99,new FS1RModel((Patch)p, 0x0D, aOp), 0,false,new FS1RSender(0x11, aOp), new FS1RSender(0x0D, aOp),"R2","L2"),
			new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x12, aOp), 0, 99,new FS1RModel((Patch)p, 0x0E, aOp), 0,false,new FS1RSender(0x12, aOp), new FS1RSender(0x0E, aOp),"R3","L3"),
        new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x13, aOp), 0, 99,new FS1RModel((Patch)p, 0x0F, aOp), 0,false,new FS1RSender(0x13, aOp), new FS1RSender(0x0F, aOp),"R4","L4")
		});
		//oEnv.setPreferredSize(new Dimension(ENV_WIDTH, ENV_HEIGHT));
		oPanel5.add(oEnv);
		oPanel5.add(Box.createHorizontalStrut(5));

		oEnv = new EnvelopeWidget("Frequency Envelope Generator",p,new EnvelopeWidget.Node []
		{
//        new EnvelopeNode(50, 50, null, 0, 0, null,0,false,null,null,null,null),
        new EnvelopeWidget.Node(0, 0, null, 0, 0x64, new FS1RModel((Patch)p, 8, aOp), 0, false, null, new FS1RSender(8, aOp), null, "Initial level"),
        new EnvelopeWidget.Node(0, 0x63, new FS1RModel((Patch)p, 0x0A, aOp), 0, 0x64, new FS1RModel((Patch)p, 9, aOp), 0, false, new FS1RSender(0x14, aOp), new FS1RSender(9, aOp), "Attack time", "Attack level"),
        new EnvelopeWidget.Node(0, 0x63, new FS1RModel((Patch)p, 0x0B, aOp), 0, 0, null, 0, false, new FS1RSender(0x0B, aOp), null, "Decay", null)
		});
		//oEnv.setPreferredSize(new Dimension(ENV_WIDTH, ENV_HEIGHT));
		oPanel5.add(oEnv);
		oPane.add(oPanel5);

		JPanel oSensPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oSensPanel.add(new KnobWidget("Amp velocity", p, 0, 14, -7, new BitModel((Patch)p, 0x21, aOp, 0x0F, 0), new BitSender((Patch)p, 0x21, aOp)));
		oSensPanel.add(new KnobWidget("Freq velocity", p, 0, 14, -7, new BitModel((Patch)p, 0x20, aOp, 0x0F, 0), new BitSender((Patch)p, 0x20, aOp)));
		oSensPanel.add(new KnobWidget("Amp EG bias", p, 0, 14, -7, new FS1RModel((Patch)p, 0x22, aOp), new FS1RSender(0x22, aOp)));
		oSensPanel.add(new KnobWidget("Freq bias", p, 0, 14, -7, new BitModel((Patch)p, 0x1F, aOp, 0x78, 3), new BitSender((Patch)p, 0x1F, aOp)));
		oSensPanel.add(new KnobWidget("Width bias", p, 0, 14, -7, new BitModel((Patch)p, 0x04, aOp, 0x78, 3), new BitSender((Patch)p, 0x04, aOp)));
		oSensPanel.add(new KnobWidget("Pitch mod", p, 0, 7, 0, new BitModel((Patch)p, 0x1F, aOp, 0x07, 0), new BitSender((Patch)p, 0x1F, aOp)));
		oSensPanel.add(new KnobWidget("Amp mod", p, 0, 7, 0, new BitModel((Patch)p, 0x21, aOp, 0x70, 4), new BitSender((Patch)p, 0x21, aOp)));
		oSensPanel.add(new KnobWidget("Freq mod", p, 0, 7, 0, new BitModel((Patch)p, 0x20, aOp, 0x70, 4), new BitSender((Patch)p, 0x20, aOp)));
		oPane.add(oSensPanel);

		return oPane;
	}

	private class WaveComboListener implements ComboActionListener {
		private CardLayout mCard;
		private String []mComponents;
		private Container mParent;
		WaveComboListener(Container aParent, String []aComps) {
			mComponents = aComps;
			mCard = (CardLayout)aParent.getLayout();
			mParent = aParent;
		}
		public void notifyChange(int aIndex) {
			switch(aIndex) {
			case 0: mCard.show(mParent, "Sine"); break;
			case 1: case 2: case 3: case 4: mCard.show(mParent, "All"); break;
			case 5: case 6: mCard.show(mParent, "Res"); break;
			case 7: mCard.show(mParent, "Formant"); break;
			}
		}
	}


	/**
		Build a window for one unvoiced operator
		@param aOp Operator number (1 ... 8)
	*/
	private Container buildUnvoicedWindow(int aOp) {
		Box oPane = Box.createVerticalBox();
		JPanel oPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel1.add(new ComboBoxWidget("Freq mode", p, new BitModel((Patch)p, 0x24, aOp, 0x60, 5), new BitSender((Patch)p, 0x24, aOp), new String []{"Normal", "LinkF0", "LinkFF"}));
		oPanel1.add(new SpinnerWidget("Transpose", p, 0, 48, -24, new BitModel((Patch)p, 0, aOp, 0x3F, 0), new BitSender((Patch)p, 0, aOp)));
		oPanel1.add(new KnobLookupWidget("Freq coarse", p, new BitModel((Patch)p, 0x24, aOp, 0x1F, 0), new BitSender((Patch)p, 0x24, aOp), mUnvoicedFreCoarseNames));
		oPanel1.add(new KnobWidget("Freq fine", p, 0, 0x7F, 0, new FS1RModel((Patch)p, 0x25, aOp), new FS1RSender(0x25, aOp)));
		oPanel1.add(new KnobWidget("Freq scaling", p, 0, 99, 0, new FS1RModel((Patch)p, 0x26, aOp), new FS1RSender(0x26, aOp)));
		oPane.add(oPanel1);

		JPanel oPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel2.add(new KnobWidget("Band width", p, 0, 0x63, 0, new FS1RModel((Patch)p, 0x27, aOp), new FS1RSender(0x27, aOp)));
		oPanel2.add(new KnobWidget("Resonance", p, 0, 7, 0, new BitModel((Patch)p, 0x29, aOp, 0x38, 3), new BitSender((Patch)p, 0x29, aOp)));
		oPanel2.add(new SpinnerWidget("Skirt", p, 0, 7, 0, new BitModel((Patch)p, 0x29, aOp, 7, 0), new BitSender((Patch)p, 0x29, aOp)));
		oPanel2.add(new KnobWidget("Output level", p, 0, 99, 0, new FS1RModel((Patch)p, 0x2E, aOp), new FS1RSender(0x2E, aOp)));
		oPanel2.add(new KnobWidget("Level scaling", p, 0, 14, -7, new FS1RModel((Patch)p, 0x2F, aOp), new FS1RSender(0x2F, aOp)));
		oPane.add(oPanel2);

//		JPanel oPanel5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		Box oPanel5 = Box.createHorizontalBox();
//		JPanel oPanel5 = new JPanel(new GridLayout(1, 2));
		EnvelopeWidget oEnvAmp = new EnvelopeWidget("Amplitude Envelope Generator",p,new EnvelopeWidget.Node []
		{
        new EnvelopeWidget.Node(0,0,null, 0, 0,null,0,false,null,null,null,null),
        new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x38, aOp), 0, 0, null, 0, false, new FS1RSender(0x38), null, "Hold", null),
        new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x34, aOp), 0, 99,new FS1RModel((Patch)p, 0x30, aOp), 0,false,new FS1RSender(0x34, aOp), new FS1RSender(0x30, aOp),"R1","L1"),
        new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x35, aOp), 0, 99,new FS1RModel((Patch)p, 0x31, aOp), 0,false,new FS1RSender(0x35, aOp), new FS1RSender(0x31, aOp),"R2","L2"),
		new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x36, aOp), 0, 99,new FS1RModel((Patch)p, 0x32, aOp), 0,false,new FS1RSender(0x36, aOp), new FS1RSender(0x32, aOp),"R3","L3"),
        new EnvelopeWidget.Node(0, 99, new FS1RModel((Patch)p, 0x37, aOp), 0, 99,new FS1RModel((Patch)p, 0x33, aOp), 0,false,new FS1RSender(0x37, aOp), new FS1RSender(0x33, aOp),"R4","L4")
		});
		//oEnvAmp.setPreferredSize(new Dimension(ENV_WIDTH, ENV_HEIGHT));
		oPanel5.add(oEnvAmp);

		oPanel5.add(Box.createHorizontalStrut(5));

		EnvelopeWidget oEnvFreq = new EnvelopeWidget("Frequency Envelope Generator",p,new EnvelopeWidget.Node []
		{
        new EnvelopeWidget.Node(0, 0, null, 0, 0x64, new FS1RModel((Patch)p, 0x2A, aOp), 0, false, null, new FS1RSender(0x2A, aOp), null, "Initial level"),
        new EnvelopeWidget.Node(0, 0x63, new FS1RModel((Patch)p, 0x2C, aOp), 0, 0x64, new FS1RModel((Patch)p, 0x2B, aOp), 0, false, new FS1RSender(0x2C, aOp), new FS1RSender(0x2B, aOp), "Attack time", "Attack level"),
        new EnvelopeWidget.Node(0, 0x63, new FS1RModel((Patch)p, 0x2D, aOp), 0, 0, null, 0, false, new FS1RSender(0x2D, aOp), null, "Decay", null)
		});
		//oEnvFreq.setPreferredSize(new Dimension(ENV_WIDTH, ENV_HEIGHT));
		oPanel5.add(oEnvFreq);
		//oPanel5.setPreferredSize(new Dimension(500, 300));
		oPane.add(oPanel5);

		JPanel oSensPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oSensPanel.add(new KnobWidget("Amp velocity", p, 0, 14, -7, new BitModel((Patch)p, 0x3C, aOp, 0x0F, 0), new BitSender((Patch)p, 0x3C, aOp)));
		oSensPanel.add(new KnobWidget("Freq velocity", p, 0, 14, -7, new BitModel((Patch)p, 0x3B, aOp, 0x0F, 0), new BitSender((Patch)p, 0x3B, aOp)));
		oSensPanel.add(new KnobWidget("Amp EG bias", p, 0, 14, -7, new FS1RModel((Patch)p, 0x3D, aOp), new FS1RSender(0x3D, aOp)));
		oSensPanel.add(new KnobWidget("Freq bias", p, 0, 14, -7, new FS1RModel((Patch)p, 0x3A, aOp), new FS1RSender(0x3A, aOp)));
		oSensPanel.add(new KnobWidget("Width bias", p, 0, 14, -7, new FS1RModel((Patch)p, 0x28, aOp), new FS1RSender(0x28, aOp)));
		oSensPanel.add(new KnobWidget("Amp mod", p, 0, 7, 0, new BitModel((Patch)p, 0x3C, aOp, 0x70, 4), new BitSender((Patch)p, 0x3C, aOp)));
		oSensPanel.add(new KnobWidget("Freq mod", p, 0, 7, 0, new BitModel((Patch)p, 0x3B, aOp, 0x70, 4), new BitSender((Patch)p, 0x3B, aOp)));
		oPane.add(oSensPanel);

		return oPane;
	}


	/**
		@return performance part number 1..4
	*/
	protected int getPart() {
		return mPart;
	}

	class FS1RSender extends SysexSender
	{
		protected int parameter;
		protected byte []b = new byte [10];
		protected int mOperator = 0;
		/**
			Common parameter
		*/
		FS1RSender(int param) {
			parameter=param;
			b[0]=(byte)0xF0;
			b[1]=(byte)0x43;
			b[2]=(byte)0x10;
			b[3]=(byte)0x5E;
			b[4]= (byte)(0x40 + getPart() - 1);
			b[5]=0;
			b[6]=(byte)parameter ;
			b[9]=(byte)0xF7;
		}
		/**
			Operator parameter
			@param aOperator number 1..8
		*/
		FS1RSender(int param, int aOperator) {
			this(param);
			b[4]= (byte)(0x60 + getPart() - 1);
			b[5]= (byte)(aOperator - 1);
			mOperator = aOperator;
		}
		public byte [] generate (int value) {
//			b[7]=(byte)((value/128));
			b[7]=(byte)((value >> 7)&127);
			b[8]=(byte)(value&127);
			return b;
		}
	}
	class BitSender extends FS1RSender
	{
		private Patch mPatch;
		/** Common parameter */
		BitSender(Patch aPatch, int param) {
			super(param);
			mPatch = aPatch;
		}
		/**
			Operator parameter
			@param aOperator number 1..8
		*/
		BitSender(Patch aPatch, int param, int aOperator) {
			super(param, aOperator);
			mPatch = aPatch;
		}
		public byte [] generate (int value) {
			// on recupere la valeur directement dans le patch
			// 9 est le decalage de l'entete sysex
			int oValue = mPatch.sysex[parameter+9];
			if (mOperator > 0) {
				oValue = mPatch.sysex[YamahaFS1RVoiceDriver.COMMON_SIZE+YamahaFS1RVoiceDriver.VOICE_SIZE*(mOperator-1)+parameter+9];
			}
//			b[7]=(byte)((oValue/128));
			b[7]=(byte)((oValue >> 7)&127);
			b[8]=(byte)(oValue&127);
			return b;
		}
	}


	static class FS1RModel extends ParamModel
	{
		protected int mOperator = -1;
		/**
		 *	Parametre common
			@param offset dans la table
		*/
		FS1RModel(Patch p,int offset) {
			ofs = offset + 9;
			patch = p;
		}
		/**
		*	Parametre d'operateur
		 @param offset dans la table
		 @param aOp operator number 1..8
		 */
		FS1RModel(Patch p,int offset, int aOp) {
			mOperator = aOp;
			ofs = YamahaFS1RVoiceDriver.COMMON_SIZE+YamahaFS1RVoiceDriver.VOICE_SIZE*(mOperator-1) + offset + 9;
			patch = p;
		}
		public void set(int i) {
			patch.sysex[ofs] = (byte)(i & 127);
		}
		public int get() {
			return patch.sysex[ofs];
		}
	}

	static class BitModel extends FS1RModel
	{
		private int mMask;
		private int mShift;
		/**
			Peut servir pour common ou operator selon le decalage et le numero
			d'operateur (0 pour common).
			@param aOp operator number 0,1..8
			@param offset decalage soit sur common soit sur operator
		*/
		BitModel(Patch p,int offset, int aOp, int aMask, int aShift) {
			super((Patch)p, offset, aOp);
			mMask = aMask;
			mShift = aShift;
			if (aOp > 0) {
				ofs = YamahaFS1RVoiceDriver.COMMON_SIZE + YamahaFS1RVoiceDriver.VOICE_SIZE*(mOperator-1) + offset + 9;
			}
		}
		public void set(int i) {
			patch.sysex[ofs] &= ~mMask;
			patch.sysex[ofs] |= (i << mShift);
		}
		public int get() {
			return (patch.sysex[ofs] & mMask) >> mShift;
		}
	}

}
