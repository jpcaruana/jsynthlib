package synthdrivers.YamahaFS1R;

import core.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.io.*;
 
/**
	Editor for performance, ie group of 4 parts, each part holds a voice.
	Performance contains also effects, modulation matrix and fseq.  
	@author denis queffeulou mailto:dqueffeulou@free.fr
 */
class YamahaFS1RPerformanceEditor extends PatchEditorFrame
{
	static final String[] mDestinations = {
		"Off", "Ins param 1", "Ins param 2", "Ins param 3", "Ins param 4", "Ins param 5", "Ins param 6", "Ins param 7", "Ins param 8", "Ins param 9", "Ins param 10", "Ins param 11", "Ins param 12", "Ins param 13", "Ins param 14", "Send ins to reverb", "Send ins to var", "Volume", "Panpot", "Reverb send", "Var send", "Filter cutoff", "Filter resonance", "Filter EG depth", "Attack time", "Decay time", "Release time", "PEG initial level", "PEG attack time", "PEG release level", "V/N balance", "Formant", "FM", "Pitch bias", "Amp EG bias", "Freq bias", "Voiced band width", "Unvoiced band width", "LFO1 pitch mod", "LFO1 amp mod", "LFO1 freq mod", "LFO1 filter mod", "LFO1 speed", "LFO2 filter mod", "LFO2 speed", "Fseq speed", "Formant scratch"
	};
	
	static final String[] mReverbs = { "No effect", "Hall 1", "Hall 2", "Room 1", "Room 2", "Room 3", "Stage 1", "Stage 2", "Plate", "White room", "Tunnel", "Basement", "Canyon", "Delay LCR", "Delay L,R", "Echo", "Cross delay"
	};

	static final String[] mVariations = { "No effect", "Chorus", "Celeste", "Flanger", "Symphonic", "Phaser 1", "Phaser 2", "Ensemble detune", "Rotary speaker", "Tremolo", "Auto pan", "Auto wah", "Touch wah", "3-band EQ", "HM enhencer", "Noise gate", "Compressor", "Distortion", "Overdrive", "Amp sim", "Delay LCR", "Delay L,R", "Echo", "Cross delay", "Karaoke", "Hall", "Room", "Stage", "Plate"
	};
	
	static final String[] mInsertions = { "Thru", "Chorus", "Celeste", "Flanger", "Symphonic", "Phaser 1", "Phaser 2", "Pitch change", "Ensemble detune", "Rotary speaker", "2 way rotary", "Tremolo", "Auto pan", "Ambiance", "Auto wah+distortion", "Auto wah+overdrive", "Touch wah+distortion", "Touch wah+overdrive", "TWah+Dist+Delay", "TWah+Ovdr+Delay", "Lo-fi", "3-band EQ", "HM enhencer", "Noise gate", "Compressor", "Comp+Dist", "Comp+Dist+Delay", "Comp+Ovdr+Delay", "Distortion", "Dist+Delay", "Overdrive", "Ovdr+Delay", "Amp sim", "Delay LCR", "Delay L,R", "Echo", "Cross delay", "ER 1", "ER 2", "Gate reverb", "Reverse gate"
	};
	
	static final String[] mFreqs = { 
	"", "", "", "", "32", "36", "40", "45", "50", "56", "63", 
	"70", "80", "90", "100", "110", "125", "140", "160", "180", "200", 
	"225", "250", "280", "315", "355", "400", "450", "500", "560", "630", 
	"700", "800", "900", "1.0k", "1.1k", "1.2k", "1.4k", "1.6k", "1.8k", "2.0k",
	"2.2k", "2.5k", "2.8k", "3.2k", "3.6k", "4.0k", "4.5k", "5.0k", "5.6k", "6.3k", "7.0k", "8.0k", "9.0k", "10.0k", "11.0k", "12.0k", "14.0k", "16.0k"
	};
		
	static final String[] mQs = { 
	"", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9", 
	"1.0", "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "1.8", "1.9", 
	"2.0", "2.1", "2.2", "2.3", "2.4", "2.5", "2.6", "2.7", "2.8", "2.9", 
	"3.0", "3.1", "3.2", "3.3", "3.4", "3.5", "3.6", "3.7", "3.8", "3.9",
	"4.0", "4.1", "4.2", "4.3", "4.4", "4.5", "4.6", "4.7", "4.8", "4.9",
	"5.0", "5.1", "5.2", "5.3", "5.4", "5.5", "5.6", "5.7", "5.8", "5.9",
	"6.0", "6.1", "6.2", "6.3", "6.4", "6.5", "6.6", "6.7", "6.8", "6.9",
	"7.0", "7.1", "7.2", "7.3", "7.4", "7.5", "7.6", "7.7", "7.8", "7.9",
	"8.0", "8.1", "8.2", "8.3", "8.4", "8.5", "8.6", "8.7", "8.8", "8.9",
	"9.0", "9.1", "9.2", "9.3", "9.4", "9.5", "9.6", "9.7", "9.8", "9.9",
	"10.0", "10.1", "10.2", "10.3", "10.4", "10.5", "10.6", "10.7", "10.8", "10.9", 
	"11.0", "11.1", "11.2", "11.3", "11.4", "11.5", "11.6", "11.7", "11.8", "11.9", "12.0" 
	};

	static final String[] mPartChannels = new String[] {"A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "A10", "A11", "A12", "A13", "A14", "A15", "A16", "pfm", "Off"};
	
	static final String [] mNotes = new String [] {	"C-2","C#-2","D-2","D#-2","E-2","F-2","F#-2","G-2","G#-2","A-2","A#-2","B-2",
	"C-1","C#-1","D-1","D#-1","E-1","F-1","F#-1","G-1","G#-1","A-1","A#-1","B-1",
	"C0","C#0","D0","D#0","E0","F0","F#0","G0","G#0","A0","A#0","B0",
	"C1","C#1","D1","D#1","E1","F1","F#1","G1","G#1","A1","A#1","B1",
	"C2","C#2","D2","D#2","E2","F2","F#2","G2","G#2","A2","A#2","B2",
	"C3","C#3","D3","D#3","E3","F3","F#3","G3","G#3","A3","A#3","B3",
	"C4","C#4","D4","D#4","E4","F4","F#4","G4","G#4","A4","A#4","B4",
	"C5","C#5","D5","D#5","E5","F5","F#5","G5","G#5","A5","A#5","B5",
	"C6","C#6","D6","D#6","E6","F6","F#6","G6","G#6","A6","A#6","B6",
	"C7","C#7","D7","D#7","E7","F7","F#7","G7","G#7","A7","A#7","B7",
    "C8","C#8","D8","D#8","E8","F8","F#8","G8"};

	static String[] mVoicesBanks = new String[] {"off", "int", "PrA", "PrB", "PrC", "PrD", "PrE", "PrF", "PrG", "PrH", "PrI", "PrJ", "PrK"};
    static String[][] mPrVoices;
	static String[] mVoicesNames = {"voicespra.txt", "voicesprb.txt", "voicesprc.txt",
	"voicesprd.txt", "voicespre.txt", "voicesprf.txt", "voicesprg.txt", "voicesprh.txt",
	"voicespri.txt", "voicesprj.txt", "voicesprk.txt"};
	
	static final int HGAP = 3;
	static final int VGAP = 0;
	
	protected int sysexSize = 0;
	protected int mPartToEdit;
	protected static byte[] buffer = new byte[1024];    
	protected static byte[] sysex = new byte [1024];
    
	protected ComboBoxWidget mBankSelector[] = new ComboBoxWidget[4];
	protected ComboBoxWidget mVoiceSelector[] = new ComboBoxWidget[4];
	protected ComboBoxWidget mPartChannel[] = new ComboBoxWidget[4];
			        
	protected YamahaFS1RVoiceEditor mVoicesInEdit[] = new YamahaFS1RVoiceEditor[4];
					
	public YamahaFS1RPerformanceEditor(Patch patch)
	{
		super ("Yamaha FS1R Performance Editor",patch);   

		initVoicesNames();
		
		JTabbedPane oTabs = new JTabbedPane();
		scrollPane.add(oTabs);
		
		oTabs.add(buildCommonWindow(), "Common");	
		oTabs.add(buildEffectsWindow(), "Effects");	
		for (int i = 1; i <= 4; i++) {
			oTabs.add(buildPartWindow(i), "Part "+i);
		}
		
		InternalFrameListener oList[] = getInternalFrameListeners();
		removeInternalFrameListener(oList[0]);
		addInternalFrameListener(new InternalFrameListener() {
            public void internalFrameClosing(InternalFrameEvent e) {}
			public void internalFrameOpened(InternalFrameEvent e) {}
            public void internalFrameActivated(InternalFrameEvent e) {
				// send part voice if bank is int
				for (int oPart = 0; oPart < 4; oPart++) {
					if (mBankSelector[oPart].getValue() == 1)// && mPartChannel[oPart].getValue() != 17) 
					{
						Patch oPatch = null;
						if (mVoicesInEdit[oPart] != null) {
							// voice currently in editing
							oPatch = mVoicesInEdit[oPart].p;
						}
						else {
							oPatch = YamahaFS1RBankDriver.getInstance().getPatch(((YamahaFS1RBankEditor)bankFrame).getBankPatch(), 128+mVoiceSelector[oPart].getValue());
						}
						//System.out.println("SEND VOICE "+oPart+" "+mVoiceSelector[oPart].getValue());
						YamahaFS1RVoiceDriver.getInstance().sendPatch (oPatch, oPart+1);
					}
				}
			}
			public void internalFrameClosed(InternalFrameEvent e) {}
			public void internalFrameDeactivated(InternalFrameEvent e) {}
			public void internalFrameDeiconified(InternalFrameEvent e) {}
			public void internalFrameIconified(InternalFrameEvent e) {}
		});
		addInternalFrameListener(oList[0]);
		
		pack();
		setVisible(true);	
	}
	
	/**
		Cree les tableaux contenant les noms des voices pour toutes les banques.
	*/
	private void initVoicesNames() {
		if (mPrVoices == null) {
			try {
				mPrVoices = new String[mVoicesNames.length+1][128];
				for (int f = 0; f < (mVoicesNames.length+1); f++) {
					if (f == 0) {
						for(int i = 0; i < 128; i++) {
							mPrVoices[f][i] = Integer.toString(i+1);
						}
					}
					else {
						InputStream oIS = getClass().getResourceAsStream(mVoicesNames[f-1]);
						InputStreamReader oISR = new InputStreamReader(oIS);
						BufferedReader oBR = new BufferedReader(oISR);
						String oLine = oBR.readLine();
						int i = 0;
						while(oLine != null && i < 128) {
							mPrVoices[f][i++] = oLine;
							oLine = oBR.readLine();
						}
					}
				}
			}
			catch(IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	public void setBankEditorInformation (BankEditorFrame bf, int row,int col)
	{
		super.setBankEditorInformation(bf, row, col);
		// init des noms des voices de la banque interne
		if (bankFrame != null) {
			for(int part = 0; part < 4; part++) {
				mVoiceSelector[part].cb.removeAllItems();
				for(int i = 0; i < 128; i++) {
					mPrVoices[0][i] = ""+i+" "+YamahaFS1RBankDriver.getInstance().getPatchName(((YamahaFS1RBankEditor)bankFrame).getBankPatch(), 128+i);
					mVoiceSelector[part].cb.addItem(mPrVoices[0][i]);
				}
			}
		}
	}
	
	/** send patch to current performance part */
	public void SendSelectedPatch() {
		System.out.println("SendSelectedPatch");
		super.SendSelectedPatch();
	}
	
	/** Common */
	private Container buildCommonWindow() 
	{
		Box oPanel = Box.createVerticalBox();
		
		JPanel oPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel1.add(new PatchNameWidget(p, "Name"));
		oPanel1.add(new ComboBoxWidget("Category", p, new YamahaFS1RPerformanceDriver.Model(p, 0x0E), new YamahaFS1RPerformanceDriver.Sender(0x0E), YamahaFS1RVoiceEditor.mCategories));
		oPanel.add(oPanel1);

		JPanel oPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel2.add(new KnobWidget("Volume", p, 0, 0x7F, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x10), new YamahaFS1RPerformanceDriver.Sender(0x10)));
		oPanel2.add(new KnobWidget("Pan", p, 1, 0x7F, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x11), new YamahaFS1RPerformanceDriver.Sender(0x11)));
		oPanel2.add(new KnobWidget("Note shift", p, 0, 48, -24, new YamahaFS1RPerformanceDriver.Model(p, 0x12), new YamahaFS1RPerformanceDriver.Sender(0x12)));
		oPanel2.add(new ComboBoxWidget("Ind out", p, new YamahaFS1RPerformanceDriver.Model(p, 0x14), new YamahaFS1RPerformanceDriver.Sender(0x14), new String []{"Off", "Pre ins", "Post ins"}));
		oPanel.add(oPanel2);

		// matrice de controles
		JPanel oPanel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton oMatrixButton = new JButton("Matrix");
		oMatrixButton.addActionListener(new ActionListener() {
                    public void actionPerformed (ActionEvent e) {
                        JInternalFrame oWin = new MatrixWindow(p);
						getDesktopPane().add(oWin);
                        oWin.setVisible(true);
						oWin.moveToFront();
                    }}
		);
		oPanel4.add(oMatrixButton);
		// formant sequences
		JButton oFseqButton = new JButton("Fseq");
		oFseqButton.addActionListener(new ActionListener() {
                    public void actionPerformed (ActionEvent e) {
                        JInternalFrame oWin = new FseqWindow(p);
						getDesktopPane().add(oWin);
                        oWin.setVisible(true);
						oWin.moveToFront();
                    }}
		);
		oPanel4.add(oFseqButton);
		oPanel.add(oPanel4);
                		
		return oPanel;
	}
        
	

	/** Effects parameters */
	private Container buildEffectsWindow() {
		Box oPanel = Box.createVerticalBox();

		// EQ
		JPanel oPanel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JButton oEQButton = new JButton("Equalizer");
		oEQButton.addActionListener(new ActionListener() {
                    public void actionPerformed (ActionEvent e) {
                        JInternalFrame oWin = new EqualizerWindow(p);
						getDesktopPane().add(oWin);
                        oWin.setVisible(true);
						oWin.moveToFront();
                    }}
		);
		oPanel4.add(oEQButton);
		oPanel.add(oPanel4);
		
		// reverb
		JPanel oPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel1.add(new ComboBoxWidget("", p, new YamahaFS1RPerformanceDriver.Model(p, 0x0128), new YamahaFS1RPerformanceDriver.Sender(0x0128), mReverbs));
		oPanel1.add(new KnobWidget("Pan", p, 1, 0x7F, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x0129), new YamahaFS1RPerformanceDriver.Sender(0x0129)));
		oPanel1.add(new KnobWidget("Return", p, 0, 0x7F, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x012A), new YamahaFS1RPerformanceDriver.Sender(0x012A)));		
		oPanel1.setBorder(new TitledBorder(LineBorder.createGrayLineBorder(), "Reverberation", TitledBorder.LEFT, TitledBorder.CENTER));
		oPanel.add(oPanel1);
		
		// variation
		JPanel oPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel2.add(new ComboBoxWidget("", p, new YamahaFS1RPerformanceDriver.Model(p, 0x012B), new YamahaFS1RPerformanceDriver.Sender(0x012B), mVariations));
		oPanel2.add(new KnobWidget("Pan", p, 1, 0x7F, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x012C), new YamahaFS1RPerformanceDriver.Sender(0x012C)));
		oPanel2.add(new KnobWidget("Return", p, 0, 0x7F, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x012D), new YamahaFS1RPerformanceDriver.Sender(0x012D)));		
		oPanel2.add(new KnobWidget("Send to Reverb", p, 0, 0x7F, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x012E), new YamahaFS1RPerformanceDriver.Sender(0x012E)));		
		oPanel2.setBorder(new TitledBorder(LineBorder.createGrayLineBorder(), "Variation", TitledBorder.LEFT, TitledBorder.CENTER));
		oPanel.add(oPanel2);
		
		// insertion
		JPanel oPanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel3.add(new ComboBoxWidget("", p, new YamahaFS1RPerformanceDriver.Model(p, 0x012F), new YamahaFS1RPerformanceDriver.Sender(0x012F), mInsertions));
		oPanel3.add(new KnobWidget("Pan", p, 1, 0x7F, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x0130), new YamahaFS1RPerformanceDriver.Sender(0x0130)));
		oPanel3.add(new KnobWidget("Send to Reverb", p, 0, 0x7F, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x0131), new YamahaFS1RPerformanceDriver.Sender(0x0131)));		
		oPanel3.add(new KnobWidget("Send to Var", p, 0, 0x7F, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x0132), new YamahaFS1RPerformanceDriver.Sender(0x0132)));		
		oPanel3.add(new KnobWidget("Level", p, 0, 0x7F, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x0133), new YamahaFS1RPerformanceDriver.Sender(0x0133)));		
		oPanel3.setBorder(new TitledBorder(LineBorder.createGrayLineBorder(), "Insertion", TitledBorder.LEFT, TitledBorder.CENTER));
		oPanel.add(oPanel3);

		return oPanel;
	}


	/**
		Affiche les parametres de l'EQ dans une fenetre separee.
	*/
	private static class EqualizerWindow extends JInternalFrame implements PatchContainer
	{
		private Patch p;
		public Patch getPatch()
		{
			return p;
		}
		
		EqualizerWindow(Patch aPatch) {
			super("Yamaha FS1R Equalizer",true,true,true,true);
			p = aPatch;
			setSize(500, 300);
			Container oPane = getContentPane();
			oPane.setLayout(new BoxLayout(oPane, BoxLayout.Y_AXIS));
			JPanel oPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			oPanel1.add(new KnobWidget("Low gain", p, 0x34, 0x4C, -12 - 0x34, new YamahaFS1RPerformanceDriver.Model(p, 0x0134), new YamahaFS1RPerformanceDriver.Sender(0x0134)));
			oPanel1.add(new KnobLookupWidget("Low freq", p, 4, 0x28, new YamahaFS1RPerformanceDriver.Model(p, 0x0135), new YamahaFS1RPerformanceDriver.Sender(0x0135), mFreqs));
			oPanel1.add(new KnobLookupWidget("Low Q", p, 1, 0x78, new YamahaFS1RPerformanceDriver.Model(p, 0x0136), new YamahaFS1RPerformanceDriver.Sender(0x0136), mQs));
			oPanel1.add(new ComboBoxWidget("Low Shape", p, new YamahaFS1RPerformanceDriver.Model(p, 0x0137), new YamahaFS1RPerformanceDriver.Sender(0x0137), new String[] {"Shelving", "Peaking"}));
			oPane.add(oPanel1);

			JPanel oPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			oPanel2.add(new KnobWidget("Mid gain", p, 0x34, 0x4C, -12 - 0x34, new YamahaFS1RPerformanceDriver.Model(p, 0x0138), new YamahaFS1RPerformanceDriver.Sender(0x0138)));
			oPanel2.add(new KnobLookupWidget("Mid freq", p, 0x0E, 0x36, new YamahaFS1RPerformanceDriver.Model(p, 0x0139), new YamahaFS1RPerformanceDriver.Sender(0x0139), mFreqs));
			oPanel2.add(new KnobLookupWidget("Mid Q", p, 1, 0x78, new YamahaFS1RPerformanceDriver.Model(p, 0x013A), new YamahaFS1RPerformanceDriver.Sender(0x013A), mQs));
			oPane.add(oPanel2);

			JPanel oPanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
			oPanel3.add(new KnobWidget("High gain", p, 0x34, 0x4C, -12 - 0x34, new YamahaFS1RPerformanceDriver.Model(p, 0x013B), new YamahaFS1RPerformanceDriver.Sender(0x013B)));
			oPanel3.add(new KnobLookupWidget("High freq", p, 0x1C, 0x3A, new YamahaFS1RPerformanceDriver.Model(p, 0x013C), new YamahaFS1RPerformanceDriver.Sender(0x013C), mFreqs));
			oPanel3.add(new KnobLookupWidget("High Q", p, 1, 0x78, new YamahaFS1RPerformanceDriver.Model(p, 0x013D), new YamahaFS1RPerformanceDriver.Sender(0x013D), mQs));
			oPanel3.add(new ComboBoxWidget("High Shape", p, new YamahaFS1RPerformanceDriver.Model(p, 0x013E), new YamahaFS1RPerformanceDriver.Sender(0x013E), new String[] {"Shelving", "Peaking"}));
			oPane.add(oPanel3);
			pack();
		}
	}

	/** combo box listener for voices names change according to the chosen bank */
	class BankItemListener implements ItemListener
	{
		private Container mCard;
		BankItemListener(Container aCard) {
			mCard = aCard;
		}
		public void itemStateChanged (ItemEvent e)
		{
			JComboBox cb = (JComboBox)e.getSource();
			int i = cb.getSelectedIndex ();
			((CardLayout)mCard.getLayout()).show(mCard, mVoicesBanks[i]);
		}
	}
	

	/** Part parameters */
	private Container buildPartWindow(final int aPart) {
		Box oPanel = Box.createVerticalBox();
		
		JPanel oPartPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		// afficher les noms de voices PrA..PrK
		CardLayout oCard = new CardLayout();
		JPanel oVoicesPanel = new JPanel(oCard);
		Component oCB[] = new Component[mVoicesBanks.length];
		for (int i = 0; i < mVoicesBanks.length; i++) {
			if (i == 0) {
				oCB[i] = new JLabel("off");
			}
			else {
				oCB[i] = new ComboBoxWidget("Voice", p, new YamahaFS1RPerformanceDriver.Model(p, 2, aPart), new YamahaFS1RPerformanceDriver.Sender(2, aPart), mPrVoices[i-1]);
				if (i == 1) {
					// get int voice selector
					mVoiceSelector[aPart-1] = (ComboBoxWidget)oCB[i];
					// j'inverse l'ordre des listeners sinon le sysex annule
					// l'envoi du patch et on retombe sur un son interne du FS1R et non 
					// celui de la banque courante
					ItemListener oList[] = mVoiceSelector[aPart-1].cb.getItemListeners();
					mVoiceSelector[aPart-1].cb.removeItemListener(oList[0]);
					mVoiceSelector[aPart-1].cb.addItemListener(new ItemListener() {
						// send the bank voice to the current part
						public void itemStateChanged (ItemEvent e) {
				//System.out.println("SEND PATCH");
							JComboBox oCB = (JComboBox)e.getSource();
							Patch oPatch = YamahaFS1RBankDriver.getInstance().getPatch(((YamahaFS1RBankEditor)bankFrame).getBankPatch(), 128+oCB.getSelectedIndex());
							YamahaFS1RVoiceDriver.getInstance().sendPatch (oPatch, aPart);
						}
					});
					mVoiceSelector[aPart-1].cb.addItemListener(oList[0]);
				}
			}
			oVoicesPanel.add(mVoicesBanks[i], oCB[i]);
		}
		YamahaFS1RPerformanceDriver.Model oModel = new YamahaFS1RPerformanceDriver.Model(p, 1, aPart);
		ComboBoxWidget oBankCB = new ComboBoxWidget("Bank", p, oModel, new YamahaFS1RPerformanceDriver.Sender(1, aPart), mVoicesBanks);
		oBankCB.cb.addItemListener(new BankItemListener(oVoicesPanel));
		mBankSelector[aPart-1] = oBankCB;
		// choisir la bonne banque au depart
		oCard.show(oVoicesPanel, mVoicesBanks[oModel.get()]);
		oPartPane.add(oBankCB);
		oPartPane.add(oVoicesPanel);
		
		// get the part voice
		JButton oEditButton = new JButton("Edit");
		oEditButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				if (mBankSelector[aPart-1].getValue() == 1 && bankFrame != null) {
					// int bank, on edite le voice de la banque 
					int oIndex = 128+mVoiceSelector[aPart-1].getValue();
					PatchEditorFrame oEdit = (PatchEditorFrame)((YamahaFS1RBankEditor)bankFrame).EditPatch(oIndex, aPart);
					mVoicesInEdit[aPart-1] = (YamahaFS1RVoiceEditor)oEdit;
					oEdit.addInternalFrameListener(new InternalFrameListener() {
						public void internalFrameClosing(InternalFrameEvent e) {
							mVoicesInEdit[aPart-1] = null;
						}
						public void internalFrameOpened(InternalFrameEvent e) {}
						public void internalFrameActivated(InternalFrameEvent e) {}
						public void internalFrameClosed(InternalFrameEvent e) {}
						public void internalFrameDeactivated(InternalFrameEvent e) {}
						public void internalFrameDeiconified(InternalFrameEvent e) {}
						public void internalFrameIconified(InternalFrameEvent e) {}
					});

					getDesktopPane().add(oEdit);
					oEdit.setVisible(true);
					oEdit.moveToFront();
				}
			}}
		);
		oPartPane.add(oEditButton);
		
		// TODO gerer le OFF
		mPartChannel[aPart-1] = new ComboBoxWidget("Chan", p, new YamahaFS1RPerformanceDriver.Model(p, 0x04, aPart), new YamahaFS1RPerformanceDriver.Sender(0x04, aPart), mPartChannels);
		oPartPane.add(mPartChannel[aPart-1]);
		//oPartPane.setBorder(new TitledBorder(LineBorder.createGrayLineBorder(), "Part", TitledBorder.LEFT, TitledBorder.CENTER));
		oPanel.add(oPartPane);

		JPanel oEffectsPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oEffectsPane.add(new KnobWidget("Volume", p, 0, 127, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x0B, aPart), new YamahaFS1RPerformanceDriver.Sender(0x0B, aPart)));
		oEffectsPane.add(new CheckBoxWidget("Insertion", p, new YamahaFS1RPerformanceDriver.Model(p, 0x14, aPart), new YamahaFS1RPerformanceDriver.Sender(0x14, aPart)));
		oEffectsPane.add(new KnobWidget("Dry", p, 0, 127, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x11, aPart), new YamahaFS1RPerformanceDriver.Sender(0x11, aPart)));
		oEffectsPane.add(new KnobWidget("Variation", p, 0, 127, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x12, aPart), new YamahaFS1RPerformanceDriver.Sender(0x12, aPart)));
		oEffectsPane.add(new KnobWidget("Reverb", p, 0, 127, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x13, aPart), new YamahaFS1RPerformanceDriver.Sender(0x13, aPart)));
		// TODO gerer le rnd pour 0
		oEffectsPane.add(new KnobWidget("Pan", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x0E, aPart), new YamahaFS1RPerformanceDriver.Sender(0x0E, aPart)));
		JButton oDetailsButton = new JButton("Edit details");
		oDetailsButton.addActionListener(new ActionListener() {
                    public void actionPerformed (ActionEvent e) {
                        JInternalFrame oWin = new PartDetailsWindow(p, aPart);
						getDesktopPane().add(oWin);
                        oWin.setVisible(true);
						oWin.moveToFront();
                    }}
		);
		oEffectsPane.add(oDetailsButton);
		oPanel.add(oEffectsPane);

		JPanel oFreqPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oFreqPane.add(new KnobWidget("Note shift", p, 0, 48, -24, new YamahaFS1RPerformanceDriver.Model(p, 8, aPart), new YamahaFS1RPerformanceDriver.Sender(8, aPart)));
		oFreqPane.add(new KnobWidget("Detune", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 9, aPart), new YamahaFS1RPerformanceDriver.Sender(9, aPart)));
		oPanel.add(oFreqPane);
						
		JPanel oOthersPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oOthersPane.add(new CheckBoxWidget("Filter", p, new YamahaFS1RPerformanceDriver.Model(p, 7, aPart), new YamahaFS1RPerformanceDriver.Sender(7, aPart)));
		oOthersPane.add(new KnobWidget("Cutoff", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x18, aPart), new YamahaFS1RPerformanceDriver.Sender(0x18, aPart)));
		oOthersPane.add(new KnobWidget("Res", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x19, aPart), new YamahaFS1RPerformanceDriver.Sender(0x19, aPart)));
		oOthersPane.add(new KnobWidget("EG depth", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x1F, aPart), new YamahaFS1RPerformanceDriver.Sender(0x1F, aPart)));
		oOthersPane.setBorder(new TitledBorder(LineBorder.createGrayLineBorder(), "Filter", TitledBorder.LEFT, TitledBorder.CENTER));
		oPanel.add(oOthersPane);
		
		return oPanel;
	}

	/** 
		Window displaying secondary parameters.
		Because these are so much, I found better to display some
		less important parameters in another window.
	*/
	static class PartDetailsWindow extends JInternalFrame implements PatchContainer
	{
		private Patch p;
		public Patch getPatch()
		{
			return p;
		}
		PartDetailsWindow(Patch aPatch, int aPart) {
			super("FS1R Part "+aPart+" details",true,true,true,true);
			p = aPatch;
			setSize(600, 400);
			Box oPanel = Box.createVerticalBox();

			JPanel oPartPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
			oPartPane.add(new ComboBoxWidget("", p, new YamahaFS1RPerformanceDriver.Model(p, 0x05, aPart), new YamahaFS1RPerformanceDriver.Sender(0x05, aPart), new String[] {"Mono", "Poly"}));
			oPartPane.add(new ComboBoxWidget("Priority", p, new YamahaFS1RPerformanceDriver.Model(p, 0x06, aPart), new YamahaFS1RPerformanceDriver.Sender(0x06, aPart), new String[] {"Last", "Top", "Bottom", "First"}));
			oPartPane.add(new SpinnerWidget("Note res", p, 0, 32, 0, new YamahaFS1RPerformanceDriver.Model(p, 0, aPart), new YamahaFS1RPerformanceDriver.Sender(0, aPart)));
			oPartPane.add(new ComboBoxWidget("Note limit low", p, new YamahaFS1RPerformanceDriver.Model(p, 0x0F, aPart), new YamahaFS1RPerformanceDriver.Sender(0x0F, aPart), mNotes));
			oPartPane.add(new ComboBoxWidget("Note limit high", p, new YamahaFS1RPerformanceDriver.Model(p, 0x10, aPart), new YamahaFS1RPerformanceDriver.Sender(0x10, aPart), mNotes));
			oPanel.add(oPartPane);

			JPanel oEffectsPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
			oEffectsPane.add(new KnobWidget("Pan scaling", p, 0, 100, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x28, aPart), new YamahaFS1RPerformanceDriver.Sender(0x28, aPart)));
			oEffectsPane.add(new KnobWidget("Pan LFO depth", p, 0, 99, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x29, aPart), new YamahaFS1RPerformanceDriver.Sender(0x29, aPart)));
			oEffectsPane.add(new KnobWidget("Formant", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x1D, aPart), new YamahaFS1RPerformanceDriver.Sender(0x1D, aPart)));
			oEffectsPane.add(new KnobWidget("FM", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x1E, aPart), new YamahaFS1RPerformanceDriver.Sender(0x1E, aPart)));
			oEffectsPane.add(new KnobWidget("V/N balance", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x0A, aPart), new YamahaFS1RPerformanceDriver.Sender(0x0A, aPart)));
			oPanel.add(oEffectsPane);

			JPanel oFreqPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
			oFreqPane.add(new CheckBoxWidget("Portamento", p, new YamahaFS1RPerformanceDriver.BitModel(p, 0x24, aPart, 1, 0), new YamahaFS1RPerformanceDriver.BitSender(p, 0x24, aPart)));
			oFreqPane.add(new ComboBoxWidget("Porta mode", p, new YamahaFS1RPerformanceDriver.BitModel(p, 0x24, aPart, 2, 1), new YamahaFS1RPerformanceDriver.BitSender(p, 0x24, aPart), new String[] {"Fingered", "Fulltime"}));
			oFreqPane.add(new KnobWidget("Porta time", p, 0, 127, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x25, aPart), new YamahaFS1RPerformanceDriver.Sender(0x25, aPart)));
			oFreqPane.add(new SpinnerWidget("Pitch Bend low", p, 0x10, 0x58, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x27, aPart), new YamahaFS1RPerformanceDriver.Sender(0x27, aPart)));
			oFreqPane.add(new SpinnerWidget("Pitch Bend high", p, 0x10, 0x58, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x26, aPart), new YamahaFS1RPerformanceDriver.Sender(0x26, aPart)));
			oPanel.add(oFreqPane);

			JPanel oTonePane = new JPanel(new FlowLayout(FlowLayout.LEFT));
			oTonePane.add(new KnobWidget("LFO1 speed", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x15, aPart), new YamahaFS1RPerformanceDriver.Sender(0x15, aPart)));
			oTonePane.add(new KnobWidget("LFO1 pitch mod", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x16, aPart), new YamahaFS1RPerformanceDriver.Sender(0x16, aPart)));
			oTonePane.add(new KnobWidget("LFO1 delay", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x17, aPart), new YamahaFS1RPerformanceDriver.Sender(0x17, aPart)));
			oTonePane.add(new KnobWidget("LFO2 speed", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x2E, aPart), new YamahaFS1RPerformanceDriver.Sender(0x2E, aPart)));
			oTonePane.add(new KnobWidget("LFO2 Filter mod", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x2F, aPart), new YamahaFS1RPerformanceDriver.Sender(0x2F, aPart)));
			oPanel.add(oTonePane);

			JPanel oEGPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
			oEGPane.add(new KnobWidget("Attack time", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x1A, aPart), new YamahaFS1RPerformanceDriver.Sender(0x1A, aPart)));
			oEGPane.add(new KnobWidget("Decay time", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x1B, aPart), new YamahaFS1RPerformanceDriver.Sender(0x1B, aPart)));
			oEGPane.add(new KnobWidget("Release time", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x1C, aPart), new YamahaFS1RPerformanceDriver.Sender(0x1C, aPart)));
			oEGPane.add(new KnobWidget("PEG init level", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x20, aPart), new YamahaFS1RPerformanceDriver.Sender(0x20, aPart)));
			oEGPane.add(new KnobWidget("PEG attack time", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x21, aPart), new YamahaFS1RPerformanceDriver.Sender(0x21, aPart)));
			oEGPane.add(new KnobWidget("PEG rel level", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x22, aPart), new YamahaFS1RPerformanceDriver.Sender(0x22, aPart)));
			oEGPane.add(new KnobWidget("PEG rel time", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x23, aPart), new YamahaFS1RPerformanceDriver.Sender(0x23, aPart)));
			oPanel.add(oEGPane);

			JPanel oOthersPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
			oOthersPane.add(new KnobWidget("Vel Sens depth", p, 0, 127, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x0C, aPart), new YamahaFS1RPerformanceDriver.Sender(0x0C, aPart)));
			oOthersPane.add(new KnobWidget("Vel Sens offset", p, 0, 127, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x0D, aPart), new YamahaFS1RPerformanceDriver.Sender(0x0D, aPart)));
			oOthersPane.add(new KnobWidget("Vel limit low", p, 1, 127, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x2A, aPart), new YamahaFS1RPerformanceDriver.Sender(0x2A, aPart)));
			oOthersPane.add(new KnobWidget("Vel limit high", p, 1, 127, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x2B, aPart), new YamahaFS1RPerformanceDriver.Sender(0x2B, aPart)));
			oOthersPane.add(new KnobWidget("Expr low limit", p, 0, 127, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x2C, aPart), new YamahaFS1RPerformanceDriver.Sender(0x2C, aPart)));
			oOthersPane.add(new CheckBoxWidget("Sustain", p, new YamahaFS1RPerformanceDriver.Model(p, 0x2D, aPart), new YamahaFS1RPerformanceDriver.Sender(0x2D, aPart)));
			oPanel.add(oOthersPane);

			getContentPane().add(oPanel);
			pack();
		}
	}

}

