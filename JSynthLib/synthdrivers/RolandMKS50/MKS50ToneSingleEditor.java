// written by Kenneth L. Martinez
// @version $Id$

package synthdrivers.RolandMKS50;

import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import core.CheckBoxWidget;
import core.ComboBoxWidget;
import core.Driver;
import core.EnvelopeWidget;
import core.ParamModel;
import core.Patch;
import core.PatchEditorFrame;
import core.PatchNameWidget;
import core.ScrollBarWidget;
import core.SysexSender;

class MKS50ToneSingleEditor extends PatchEditorFrame
{
  public MKS50ToneSingleEditor(Patch patch)
  {
    super ("Roland MKS-50 Tone Single Editor", patch);

    JPanel leftPane = new JPanel();
    leftPane.setLayout(new GridBagLayout());
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    scrollPane.add(leftPane, gbc);
    JPanel rightPane = new JPanel();
    rightPane.setLayout(new GridBagLayout());
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    scrollPane.add(rightPane, gbc);

    JPanel namePane = new JPanel();
    addWidget(namePane, new PatchNameWidget(" Name", patch, ((Driver) patch.getDriver()).getPatchNameSize()), 0, 0, 2, 1, 0);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    leftPane.add(namePane, gbc);

    JPanel oscPane = new JPanel();
    oscPane.setLayout(new GridBagLayout());
    addWidget(oscPane, new ComboBoxWidget("Range", patch, new ParamModel(patch, 13),
      new MKSToneSender(6), new String [] {"4'", "8'", "16'", "32'"}), 0, 0, 1, 1, 1);
    addWidget(oscPane, new ComboBoxWidget("ENV Mode", patch, new ParamModel(patch, 7),
      new MKSToneSender(0), new String [] {"Normal", "Inverted", "Norm-Dyn", "Inv-Dyn"}), 1, 0, 1, 1, 2);
    addWidget(oscPane, new ScrollBarWidget("Saw Wave", patch, 0, 5, 0,
      new ParamModel(patch, 11), new MKSToneSender(4)), 0, 1, 3, 1, 3);
    addWidget(oscPane, new ScrollBarWidget("Pulse Wave", patch, 0, 3, 0,
      new ParamModel(patch, 10), new MKSToneSender(3)), 0, 2, 3, 1, 4);
    addWidget(oscPane, new ScrollBarWidget("PW / PWM", patch, 0, 127, 0,
      new ParamModel(patch, 21), new MKSToneSender(14)), 0, 3, 3, 1, 5);
    addWidget(oscPane, new ScrollBarWidget("PWM Rate", patch, 0, 127, 0,
      new ParamModel(patch, 22), new MKSToneSender(15)), 0, 4, 3, 1, 6);
    addWidget(oscPane, new ScrollBarWidget("Sub Wave", patch, 0, 5, 0,
      new ParamModel(patch, 12), new MKSToneSender(5)), 0, 5, 3, 1, 7);
    addWidget(oscPane, new ScrollBarWidget("Sub Level", patch, 0, 3, 0,
      new ParamModel(patch, 14), new MKSToneSender(7)), 0, 6, 3, 1, 8);
    addWidget(oscPane, new ScrollBarWidget("Noise Level", patch, 0, 3, 0,
      new ParamModel(patch, 15), new MKSToneSender(8)), 0, 7, 3, 1, 9);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    oscPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
      "DCO Settings", TitledBorder.CENTER, TitledBorder.CENTER));
    leftPane.add(oscPane, gbc);

    JPanel vcfPane = new JPanel();
    vcfPane.setLayout(new GridBagLayout());
    addWidget(vcfPane, new ScrollBarWidget("Cutoff Freq", patch, 0, 127, 0,
      new ParamModel(patch, 23), new MKSToneSender(16)), 0, 0, 3, 1, 10);
    addWidget(vcfPane, new ScrollBarWidget("Resonance", patch, 0, 127, 0,
      new ParamModel(patch, 24), new MKSToneSender(17)), 0, 1, 3, 1, 11);
    addWidget(vcfPane, new ScrollBarWidget("Key Follow", patch, 0, 15, 0,
      new MKSShiftModel(patch, 27), new MKSShiftSender(20)), 0, 2, 3, 1, 12);
    addWidget(vcfPane, new ComboBoxWidget("VCF ENV Mode", patch, new ParamModel(patch, 8),
      new MKSToneSender(1), new String [] {"Normal", "Inverted", "Norm-Dyn", "Inv-Dyn"}), 0, 3, 1, 1, 13);
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    vcfPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
      "VCF Settings", TitledBorder.CENTER, TitledBorder.CENTER));
    leftPane.add(vcfPane, gbc);

    JPanel lfoPane = new JPanel();
    lfoPane.setLayout(new GridBagLayout());
    gbc.weightx = 1;
    addWidget(lfoPane, new ScrollBarWidget("LFO Speed", patch, 0, 127, 0,
      new ParamModel(patch, 31), new MKSToneSender(24)), 0, 0, 7, 1, 14);
    addWidget(lfoPane, new ScrollBarWidget("LFO Delay", patch, 0, 127, 0,
      new ParamModel(patch, 32), new MKSToneSender(25)), 0, 1, 7, 1, 15);
    addWidget(lfoPane, new ScrollBarWidget("DCO Mod Depth", patch, 0, 127, 0,
      new ParamModel(patch, 18), new MKSToneSender(11)), 0, 2, 7, 1, 16);
    addWidget(lfoPane, new ScrollBarWidget("VCF Mod Depth", patch, 0, 127, 0,
      new ParamModel(patch, 25), new MKSToneSender(18)), 0, 3, 7, 1, 17);
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    lfoPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
      "LFO Settings", TitledBorder.CENTER, TitledBorder.CENTER));
    leftPane.add(lfoPane, gbc);

    JPanel envPane = new JPanel();
    envPane.setLayout(new GridBagLayout());
    addWidget(envPane, new EnvelopeWidget(" ", patch, new EnvelopeWidget.Node [] {
      new EnvelopeWidget.Node(0, 0, null, 0, 0, null, 0, false, null, null, null, null),
      new EnvelopeWidget.Node(0, 127, new ParamModel(patch, 33), 0, 127,
        new ParamModel(patch, 34), 10, false, new MKSToneSender(26), new MKSToneSender(27), " T1", " L1"),
      new EnvelopeWidget.Node(0, 127, new ParamModel(patch, 35), 0, 127,
        new ParamModel(patch, 36), 10, false, new MKSToneSender(28), new MKSToneSender(29), " T2", " L2"),
      new EnvelopeWidget.Node(0, 127, new ParamModel(patch, 37), 0, 127,
        new ParamModel(patch, 38), 10, false, new MKSToneSender(30), new MKSToneSender(31), " T3", " L3"),
      new EnvelopeWidget.Node(0, 127, new ParamModel(patch, 39), 0, 0,
        null, 0, false, new MKSToneSender(32), null, " T4", null),
    }     ), 3, 0, 3, 5, 18);
    addWidget(envPane, new ScrollBarWidget("Key Follow", patch, 0, 15, 0,
      new MKSShiftModel(patch, 40), new MKSShiftSender(33)), 3, 6, 3, 1, 25);
    addWidget(envPane, new ScrollBarWidget("DCO Mod Depth", patch, 0, 127, 0,
      new ParamModel(patch, 19), new MKSToneSender(12)), 3, 7, 3, 1, 26);
    addWidget(envPane, new ScrollBarWidget("VCF Mod Depth", patch, 0, 127, 0,
      new ParamModel(patch, 26), new MKSToneSender(19)), 3, 8, 3, 1, 27);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    envPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
      "Envelope", TitledBorder.CENTER, TitledBorder.CENTER));
    rightPane.add(envPane, gbc);

    JPanel cmnPane =  new JPanel();
    cmnPane.setLayout(new GridBagLayout());
    addWidget(cmnPane, new ScrollBarWidget("DCO Aftertouch LFO", patch, 0, 15, 0,
      new MKSShiftModel(patch, 20), new MKSShiftSender(13)), 0, 0, 3, 1, 28);
    addWidget(cmnPane, new ScrollBarWidget("VCF Aftertouch Depth", patch, 0, 15, 0,
      new MKSShiftModel(patch, 28), new MKSShiftSender(21)), 0, 1, 3, 1, 29);
    addWidget(cmnPane, new ScrollBarWidget("VCA Aftertouch Depth", patch, 0, 15, 0,
      new MKSShiftModel(patch, 30), new MKSShiftSender(23)), 0, 2, 3, 1, 30);
    addWidget(cmnPane, new ScrollBarWidget("VCA Level", patch, 0, 127, 0,
      new ParamModel(patch, 29), new MKSToneSender(22)), 0, 3, 3, 1, 31);
    addWidget(cmnPane, new ComboBoxWidget("VCA ENV Mode", patch, new ParamModel(patch, 9),
      new MKSToneSender(2), new String [] {"Env", "Gate", "Env-Dyn", "Gate-Dyn"}), 0, 4, 1, 1, 32);
    addWidget(cmnPane, new CheckBoxWidget("Chorus", patch, new ParamModel(patch, 17),
      new MKSToneSender(10)), 1, 4, 1, 1, -1);
    addWidget(cmnPane, new ScrollBarWidget("Chorus Rate", patch, 0, 127, 0,
      new ParamModel(patch, 41), new MKSToneSender(34)), 0, 5, 3, 1, 33);
    addWidget(cmnPane, new ScrollBarWidget("HPF", patch, 0, 3, 0,
      new ParamModel(patch, 16), new MKSToneSender(9)), 0, 6, 3, 1, 34);
    addWidget(cmnPane, new ScrollBarWidget("Bender Range", patch, 0, 12, 0,
      new ParamModel(patch, 42), new MKSToneSender(35)), 0, 7, 3, 1, 35);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
      "Common", TitledBorder.CENTER, TitledBorder.CENTER));
    rightPane.add(cmnPane, gbc);

    pack();
    show();
  }
}

class MKSToneSender extends SysexSender
{
  byte b[] = { (byte)0xF0, (byte)0x41, (byte)0x36, (byte)0x00, (byte)0x23,
               (byte)0x20, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0xF7 };
  public MKSToneSender(int param)
  {
    b[7] = (byte)param;
  }
  public byte [] generate (int value)
  {
    b[3] = (byte)(channel - 1);
    b[8] = (byte)value;
    return b;
  }
}

// shift sender & model are for parms whose value must be shifted left
// 3 bits (multiplied by 8) to arrive at the sysex parm value
class MKSShiftModel extends ParamModel
{
  public MKSShiftModel(Patch p, int o)
  {
    ofs = o;
    patch = p;
  }
  public void set(int i)
  {
    patch.sysex[ofs] = (byte)(i << 3);
  }
  public int get()
  {
    return patch.sysex[ofs] >> 3;
  }
}

class MKSShiftSender extends SysexSender
{
  byte b[] = { (byte)0xF0, (byte)0x41, (byte)0x36, (byte)0x00, (byte)0x23,
               (byte)0x20, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0xF7 };
  public MKSShiftSender(int param)
  {
    b[7] = (byte)param;
  }
  public byte [] generate (int value)
  {
    b[3] = (byte)(channel - 1);
    b[8] = (byte)(value << 3);
    return b;
  }
}

