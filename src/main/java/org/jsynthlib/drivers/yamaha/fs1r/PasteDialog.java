package org.jsynthlib.drivers.yamaha.fs1r;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.jsynthlib.core.Patch;
import org.jsynthlib.core.SysexWidget;



/**
	Dialog to choose sources and destinations for operator paste. 
*/
class PasteDialog extends JDialog
{
	private ButtonGroup rbg;
	private JCheckBox mDest[] = new JCheckBox[8];
	private Patch mSourcePatch;
	private Patch mDestPatch;
	private ArrayList[] mWidgets;
	
	/**
		@param aOwner owner for dialog
		@param aWidgets table of operator widgets lists
	*/
	PasteDialog(Frame aOwner, Patch aSource, Patch aDest, int aPart, ArrayList aWidgets[])
	{
		super(aOwner, "Paste operators", true);
		
		mSourcePatch = aSource;
		mDestPatch = aDest;
		mWidgets = aWidgets;
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		rbg = new ButtonGroup();
		for (int i = 1; i <= 8; i++)
		{
			JPanel oPane = new JPanel();
			oPane.add(new JLabel("Paste operator"));
			JRadioButton oRadio = new JRadioButton(""+i);
			oRadio.setActionCommand(""+(i-1));
			oPane.add(oRadio);
			rbg.add(oRadio);
			oPane.add(new JLabel("to"));
			mDest[i-1] = new JCheckBox(""+i);
			oPane.add(mDest[i-1]);
			getContentPane().add(oPane);
		}
		JButton oCopyVoices = new JButton ("Paste Voices");
		oCopyVoices.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e)
			{
				pasteVoices();
				setVisible (false);
		}});
		getContentPane().add(oCopyVoices);
		/*
		JButton oCopyUnvoices = new JButton ("Paste Unvoices");
		oCopyUnvoices.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e)
			{
				setVisible (false);
		}});
		getContentPane().add(oCopyUnvoices);
		*/
		JButton cancel = new JButton ("Cancel");
		cancel.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e)
			{
				setVisible(false);
			}});
		getContentPane().add ( cancel );
		getRootPane().setDefaultButton (cancel);
		setSize(250, 400);
		setVisible(true);
	}


	private void pasteVoices()
	{
		ButtonModel oSel = rbg.getSelection();
		int oIndexSource = YamahaFS1RVoiceDriver.VOICE_VOICE_OFFSET + YamahaFS1RVoiceDriver.VOICE_SIZE * Integer.valueOf(oSel.getActionCommand()).intValue();

		for (int i = 0; i < 8; i++)
		{
			int oIndexDest = YamahaFS1RVoiceDriver.VOICE_VOICE_OFFSET + YamahaFS1RVoiceDriver.VOICE_SIZE * i;
			if (mDest[i].isSelected())
			{
				System.arraycopy(mSourcePatch.getByteArray(), oIndexSource,  mDestPatch.getByteArray(),  oIndexDest,  YamahaFS1RVoiceDriver.VOICE_VOICE_SIZE);
				// update widgets
				ArrayList oWidgets = mWidgets[i];
				for (int w = 0; w < oWidgets.size(); w++)
				{
					SysexWidget oWid = (SysexWidget)oWidgets.get(w);
					oWid.setValue();
				}
			}
		}
	}


}