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
	@version $Id$
 */
class YamahaFS1RPerformanceEditor extends PatchEditorFrame
{
	static final String[] mDestinations = {
		"Off", "Ins param 1", "Ins param 2", "Ins param 3", "Ins param 4", "Ins param 5", "Ins param 6", "Ins param 7", "Ins param 8", "Ins param 9", "Ins param 10", "Ins param 11", "Ins param 12", "Ins param 13", "Ins param 14", "Send ins to reverb", "Send ins to var", "Volume", "Panpot", "Reverb send", "Var send", "Filter cutoff", "Filter resonance", "Filter EG depth", "Attack time", "Decay time", "Release time", "PEG initial level", "PEG attack time", "PEG release level", "V/N balance", "Formant", "FM", "Pitch bias", "Amp EG bias", "Freq bias", "Voiced band width", "Unvoiced band width", "LFO1 pitch mod", "LFO1 amp mod", "LFO1 freq mod", "LFO1 filter mod", "LFO1 speed", "LFO2 filter mod", "LFO2 speed", "Fseq speed", "Formant scratch"
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
	/** 128 noms de voices */
    static VoiceName[][] mPrVoices = new VoiceName[mVoicesBanks.length-1][128];
	static String[] mVoicesNamesFiles = {"voicespra.txt", "voicesprb.txt", "voicesprc.txt",
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
		oTabs.add(new MatrixWindow(patch), "Matrix");
		oTabs.add(new FseqWindow(patch), "Fseq");	
		oTabs.add((new EffectsWindow(p)).buildEffectsWindow(), "Effects");	
		for (int i = 1; i <= 4; i++) {
			oTabs.add(buildPartWindow(i), "Part "+i);
		}
		
		JSLFrameListener oList[] = getJSLFrameListeners();
		removeJSLFrameListener(oList[0]);
		addJSLFrameListener(new JSLFrameListener() {
            public void JSLFrameClosing(JSLFrameEvent e) {}
			public void JSLFrameOpened(JSLFrameEvent e) {}
            public void JSLFrameActivated(JSLFrameEvent e) {
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
			public void JSLFrameClosed(JSLFrameEvent e) {}
			public void JSLFrameDeactivated(JSLFrameEvent e) {}
			public void JSLFrameDeiconified(JSLFrameEvent e) {}
			public void JSLFrameIconified(JSLFrameEvent e) {}
		});
		addJSLFrameListener(oList[0]);

		setSize(800, 600);

		pack();
		setVisible(true);	
	}
	
	/**
		Cree les tableaux contenant les noms des voices pour toutes les banques.
	*/
	private void initVoicesNames() 
	{
		// init temporaire qui sert juste a creer les combo
		for(int i = 0; i < 128; i++) 
		{
			mPrVoices[0][i] = new VoiceName(i+" ---------");
		}		
	}
	
	/** Classe permettant de changer les noms de voices dans les combo 
		sans regenerer un setSelectedItem (dans setBankEditorInformation).
		Elle ne se sert veritablement que pour la banque int.
	*/		
	static class VoiceName
	{
		private String mName;
		VoiceName(String aName)
		{
			mName = aName;
		}
		public String toString()
		{
			return mName;
		}
		void setName(String aName)
		{
			mName = aName;
		}
	}

	public void setBankEditorInformation (BankEditorFrame bf, int row,int col)
	{
		super.setBankEditorInformation(bf, row, col);
		// init des noms des voices 
		if (bankFrame != null) 
		{
		for (int b = 0; b < (mVoicesBanks.length-1); b++)
		{
			if (b == 0)
			{
				// interne
				for(int i = 0; i < 128; i++) 
				{
					mPrVoices[b][i] = new VoiceName(""+(i+1)+" "+YamahaFS1RBankDriver.getInstance().getPatchName(((YamahaFS1RBankEditor)bankFrame).getBankPatch(), 128+i));
				}
			}
			else
			{
				try
				{
					InputStream oIS = getClass().getResourceAsStream(mVoicesNamesFiles[b-1]);
					InputStreamReader oISR = new InputStreamReader(oIS);
					BufferedReader oBR = new BufferedReader(oISR);
					String oLine = oBR.readLine();
					int c = 0;
					while(oLine != null && c < 128) {
						mPrVoices[b][c++] = new VoiceName(c+" "+oLine.trim());
						oLine = oBR.readLine();
					}
				}
				catch(IOException ioe)
				{
					ioe.printStackTrace();
				}
			}
		}
			for (int part = 0; part < 4; part++)
			{
				// rafraichir les menus
				int oSelIndex = mBankSelector[part].cb.getSelectedIndex ();
				mVoiceSelector[part].cb.removeAllItems();
				if (oSelIndex > 0)
				{
					int oVoiceIndex = mVoiceSelector[part].getValue();
					for(int i = 0; i < 128; i++) 
					{
						mVoiceSelector[part].cb.addItem(mPrVoices[oSelIndex-1][i]);
					}
					mVoiceSelector[part].setValue(oVoiceIndex);
				}
			}
		}
	}
	
	/** send patch to current performance part */
	public void SendSelectedPatch() {
		super.SendSelectedPatch();
	}
	
	/** Common */
	private Container buildCommonWindow() 
	{
		Box oPanel = Box.createVerticalBox();
		
		JPanel oPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel1.add(new PatchNameWidget("Name",p));
		oPanel1.add(new ComboBoxWidget("Category", p, new YamahaFS1RPerformanceDriver.Model(p, 0x0E), new YamahaFS1RPerformanceDriver.Sender(0x0E), YamahaFS1RVoiceEditor.mCategories));
		oPanel.add(oPanel1);

		JPanel oPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oPanel2.add(new KnobWidget("Volume", p, 0, 0x7F, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x10), new YamahaFS1RPerformanceDriver.Sender(0x10)));
		oPanel2.add(new KnobWidget("Pan", p, 1, 0x7F, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x11), new YamahaFS1RPerformanceDriver.Sender(0x11)));
		oPanel2.add(new KnobWidget("Note shift", p, 0, 48, -24, new YamahaFS1RPerformanceDriver.Model(p, 0x12), new YamahaFS1RPerformanceDriver.Sender(0x12)));
		oPanel2.add(new ComboBoxWidget("Ind out", p, new YamahaFS1RPerformanceDriver.Model(p, 0x14), new YamahaFS1RPerformanceDriver.Sender(0x14), new String []{"Off", "Pre ins", "Post ins"}));
		oPanel.add(oPanel2);

		return oPanel;
	}
        
	


	/** combo box listener for voices names change according to the chosen bank */
	class BankItemListener implements ItemListener
	{
		private int mPart;
		BankItemListener(int aPart) 
		{
			mPart = aPart;
		}
		public void itemStateChanged (ItemEvent e)
		{
			if (e.getStateChange() == ItemEvent.SELECTED)
			{
				JComboBox cb = (JComboBox)e.getSource();
				int oSelIndex = cb.getSelectedIndex ();
				mVoiceSelector[mPart-1].cb.removeAllItems();
				if (oSelIndex > 0)
				{
					for(int i = 0; i < 128; i++) 
					{
						mVoiceSelector[mPart-1].cb.addItem(mPrVoices[oSelIndex-1][i]);
					}
				}
			}
		}
	}
	

	/** Part parameters */
	private Container buildPartWindow(final int aPart) 
	{
		JTabbedPane oTabs = new JTabbedPane();
		Box oPanel = Box.createVerticalBox();
		
		JPanel oPartPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		// afficher les noms de voices PrA..PrK
		YamahaFS1RPerformanceDriver.Model oModel = new YamahaFS1RPerformanceDriver.Model(p, 1, aPart);
		ComboBoxWidget oBankCB = new ComboBoxWidget("Bank", p, oModel, new YamahaFS1RPerformanceDriver.Sender(1, aPart), mVoicesBanks);
		ItemListener oList1[] = oBankCB.cb.getItemListeners();
		oBankCB.cb.removeItemListener(oList1[0]);
		oBankCB.cb.addItemListener(new BankItemListener(aPart));
		oBankCB.cb.addItemListener(oList1[0]);
		mBankSelector[aPart-1] = oBankCB;
		oPartPane.add(oBankCB);
				
		mVoiceSelector[aPart-1] = new ComboBoxWidget("Voice", p, new YamahaFS1RPerformanceDriver.Model(p, 2, aPart), new YamahaFS1RPerformanceDriver.Sender(2, aPart), mPrVoices[0]);
		// j'inverse l'ordre des listeners sinon le sysex annule
		// l'envoi du patch et on retombe sur un son interne du FS1R et non 
		// celui de la banque courante
		ItemListener oList[] = mVoiceSelector[aPart-1].cb.getItemListeners();
		mVoiceSelector[aPart-1].cb.removeItemListener(oList[0]);
		mVoiceSelector[aPart-1].cb.addItemListener(new ItemListener() {
			// send the bank voice to the current part
			public void itemStateChanged (ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
				{
					if (mBankSelector[aPart-1].cb.getSelectedIndex() == 1)
					{ 
						// envoi sysex de la voice interne selectionnee
						JComboBox oCB = (JComboBox)e.getSource();
						Patch oPatch = YamahaFS1RBankDriver.getInstance().getPatch(((YamahaFS1RBankEditor)bankFrame).getBankPatch(), 128+oCB.getSelectedIndex());
						YamahaFS1RVoiceDriver.getInstance().sendPatch (oPatch, aPart);
					}
				}
			}
		});
		mVoiceSelector[aPart-1].cb.addItemListener(oList[0]);
		oPartPane.add(mVoiceSelector[aPart-1]);
		
		// get the part voice
		JButton oEditButton = new JButton("Edit");
		oEditButton.addActionListener(new ActionListener() {
			public void actionPerformed (ActionEvent e) {
				if (mBankSelector[aPart-1].getValue() == 1 && bankFrame != null) {
					// int bank, on edite le voice de la banque 
					int oIndex = 128+mVoiceSelector[aPart-1].getValue();
					PatchEditorFrame oEdit = (PatchEditorFrame)((YamahaFS1RBankEditor)bankFrame).EditPatch(oIndex, aPart);
					mVoicesInEdit[aPart-1] = (YamahaFS1RVoiceEditor)oEdit;
					oEdit.addJSLFrameListener(new JSLFrameListener() {
						public void JSLFrameClosing(JSLFrameEvent e) {
							mVoicesInEdit[aPart-1] = null;
						}
						public void JSLFrameOpened(JSLFrameEvent e) {}
						public void JSLFrameActivated(JSLFrameEvent e) {}
						public void JSLFrameClosed(JSLFrameEvent e) {}
						public void JSLFrameDeactivated(JSLFrameEvent e) {}
						public void JSLFrameDeiconified(JSLFrameEvent e) {}
						public void JSLFrameIconified(JSLFrameEvent e) {}
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
		oPanel.add(oPartPane);

		JPanel oEffectsPane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		oEffectsPane.add(new KnobWidget("Volume", p, 0, 127, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x0B, aPart), new YamahaFS1RPerformanceDriver.Sender(0x0B, aPart)));
		oEffectsPane.add(new CheckBoxWidget("Insertion", p, new YamahaFS1RPerformanceDriver.Model(p, 0x14, aPart), new YamahaFS1RPerformanceDriver.Sender(0x14, aPart)));
		oEffectsPane.add(new KnobWidget("Dry", p, 0, 127, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x11, aPart), new YamahaFS1RPerformanceDriver.Sender(0x11, aPart)));
		oEffectsPane.add(new KnobWidget("Variation", p, 0, 127, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x12, aPart), new YamahaFS1RPerformanceDriver.Sender(0x12, aPart)));
		oEffectsPane.add(new KnobWidget("Reverb", p, 0, 127, 0, new YamahaFS1RPerformanceDriver.Model(p, 0x13, aPart), new YamahaFS1RPerformanceDriver.Sender(0x13, aPart)));
		// TODO gerer le rnd pour 0
		oEffectsPane.add(new KnobWidget("Pan", p, 0, 127, -64, new YamahaFS1RPerformanceDriver.Model(p, 0x0E, aPart), new YamahaFS1RPerformanceDriver.Sender(0x0E, aPart)));
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
		
		oTabs.addTab("Main", oPanel);
		oTabs.addTab("Details", new PartDetailsWindow(p, aPart));
		return oTabs;
	}

	/** 
		Window displaying secondary parameters.
		Because these are so much, I found better to display some
		less important parameters in another window.
	*/
	static class PartDetailsWindow extends JPanel
	{
		private Patch p;
		public Patch getPatch()
		{
			return p;
		}
		PartDetailsWindow(Patch aPatch, int aPart) {
			//super("FS1R Part "+aPart+" details",true,true,true,true);
			p = aPatch;
			//setSize(600, 400);
			//Box oPanel = Box.createVerticalBox();
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			JPanel oPanel = this;

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

			//getContentPane().add(oPanel);
			//pack();
		}
	}

}

