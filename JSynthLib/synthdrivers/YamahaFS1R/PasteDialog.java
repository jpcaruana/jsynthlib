package synthdrivers.YamahaFS1R;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import core.IPatch;

/**
	Dialog to choose sources and destinations for operator paste. 
*/
class PasteDialog extends JDialog
{
	private ButtonGroup rbg;
	private JCheckBox mDest[] = new JCheckBox[8];
	private IPatch mSourcePatch;
	private IPatch mDestPatch;
	
	PasteDialog(IPatch aSource, IPatch aDest)
	{
		super(core.PatchEdit.getInstance(), "Paste operators", true);
		
		mSourcePatch = aSource;
		mDestPatch = aDest;
		
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
		JButton oCopyUnvoices = new JButton ("Paste Unvoices");
		oCopyUnvoices.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e)
			{
				setVisible (false);
		}});
		getContentPane().add(oCopyUnvoices);
		JButton cancel = new JButton ("Cancel");
		cancel.addActionListener (new ActionListener () {
			public void actionPerformed (ActionEvent e)
			{
				setVisible (false);
			}});
		getContentPane().add ( cancel );
		getRootPane().setDefaultButton (cancel);
		setSize(250, 400);
		show();
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
			System.out.println("copy from "+oIndexSource+" to "+oIndexDest);
				System.arraycopy(mSourcePatch.getByteArray(), oIndexSource,  mDestPatch.getByteArray(),  oIndexDest,  YamahaFS1RVoiceDriver.VOICE_VOICE_SIZE);
			}
		}
	}


}