// written by Kenneth L. Martinez
// @version $Id$

package synthdrivers.RolandMKS50;

import java.awt.GridBagLayout;

import javax.swing.JPanel;

import core.CheckBoxWidget;
import core.ComboBoxWidget;
import core.ParamModel;
import core.Patch;
import core.PatchEditorFrame;
import core.PatchNameWidget;
import core.ScrollBarLookupWidget;
import core.ScrollBarWidget;
import core.SysexSender;

class MKS50PatchSingleEditor extends PatchEditorFrame
{
  static final String noteName[] = new String[] {
    "C 0", "C#0", "D 0", "D#0", "E 0", "F 0", "F#0", "G 0", "G#0", "A 0", "A#0", "B 0",
    "C 1", "C#1", "D 1", "D#1", "E 1", "F 1", "F#1", "G 1", "G#1", "A 1", "A#1", "B 1",
    "C 2", "C#2", "D 2", "D#2", "E 2", "F 2", "F#2", "G 2", "G#2", "A 2", "A#2", "B 2",
    "C 3", "C#3", "D 3", "D#3", "E 3", "F 3", "F#3", "G 3", "G#3", "A 3", "A#3", "B 3",
    "C 4", "C#4", "D 4", "D#4", "E 4", "F 4", "F#4", "G 4", "G#4", "A 4", "A#4", "B 4",
    "C 5", "C#5", "D 5", "D#5", "E 5", "F 5", "F#5", "G 5", "G#5", "A 5", "A#5", "B 5",
    "C 6", "C#6", "D 6", "D#6", "E 6", "F 6", "F#6", "G 6", "G#6", "A 6", "A#6", "B 6",
    "C 7", "C#7", "D 7", "D#7", "E 7", "F 7", "F#7", "G 7", "G#7", "A 7", "A#7", "B 7",
    "C 8"
  };
  static final String toneList[] = new String[] {
    "a11", "a12", "a13", "a14", "a15", "a16", "a17", "a18",
    "a21", "a22", "a23", "a24", "a25", "a26", "a27", "a28",
    "a31", "a32", "a33", "a34", "a35", "a36", "a37", "a38",
    "a41", "a42", "a43", "a44", "a45", "a46", "a47", "a48",
    "a51", "a52", "a53", "a54", "a55", "a56", "a57", "a58",
    "a61", "a62", "a63", "a64", "a65", "a66", "a67", "a68",
    "a71", "a72", "a73", "a74", "a75", "a76", "a77", "a78",
    "a81", "a82", "a83", "a84", "a85", "a86", "a87", "a88",
    "b11", "b12", "b13", "b14", "b15", "b16", "b17", "b18",
    "b21", "b22", "b23", "b24", "b25", "b26", "b27", "b28",
    "b31", "b32", "b33", "b34", "b35", "b36", "b37", "b38",
    "b41", "b42", "b43", "b44", "b45", "b46", "b47", "b48",
    "b51", "b52", "b53", "b54", "b55", "b56", "b57", "b58",
    "b61", "b62", "b63", "b64", "b65", "b66", "b67", "b68",
    "b71", "b72", "b73", "b74", "b75", "b76", "b77", "b78",
    "b81", "b82", "b83", "b84", "b85", "b86", "b87", "b88"
  };

  public MKS50PatchSingleEditor(Patch patch)
  {
    super ("Roland MKS-50 Patch Single Editor", patch);

    JPanel leftPane = new JPanel();
    leftPane.setLayout(new GridBagLayout());

    addWidget(leftPane, new PatchNameWidget("Name", patch), 0, 0, 2, 1, 0);
    addWidget(leftPane, new ScrollBarLookupWidget("Tone", patch, 0, 127,
      new ParamModel(patch, 7), new MKSPatchSender(0), toneList), 0, 1, 2, 1, 1);
    addWidget(leftPane, new ComboBoxWidget("Key Range Low", patch,
      new MKSOfsModel(patch, 8, 12), new MKSOfsSender(1, 12), noteName), 0, 2, 1, 1, 2);
    addWidget(leftPane, new ComboBoxWidget("Key Range High", patch,
      new MKSOfsModel(patch, 9, 12), new MKSOfsSender(2, 12), noteName), 1, 2, 1, 1, 3);
    addWidget(leftPane, new ScrollBarWidget("Key Shift", patch, 0, 24, -12,
      new MKS2sCompModel(patch, 13, 12), new MKS2sCompSender(6, 12)), 0, 4, 2, 1, 4);
    addWidget(leftPane, new ScrollBarWidget("Detune", patch, 0, 126, -63,
      new MKS2sCompModel(patch, 15, 63), new MKS2sCompSender(8, 63)), 0, 5, 2, 1, 5);
    addWidget(leftPane, new ScrollBarWidget("Volume", patch, 0, 127, 0,
      new ParamModel(patch, 14), new MKSPatchSender(7)), 0, 6, 2, 1, 6);
    addWidget(leftPane, new ScrollBarWidget("Mod Sens", patch, 0, 127, 0,
      new ParamModel(patch, 12), new MKSPatchSender(5)), 0, 7, 2, 1, 7);
    addWidget(leftPane, new ScrollBarWidget("Portamento Time", patch, 0, 127, 0,
      new ParamModel(patch, 10), new MKSPatchSender(3)), 0, 8, 2, 1, 8);
    addWidget(leftPane, new CheckBoxWidget("Portamento", patch,
      new ParamModel(patch, 11), new MKSPatchSender(4)), 0, 9, 1, 1, -1);
    addWidget(leftPane, new CheckBoxWidget("Midi Aftertouch", patch,
      new MKSBitModel(patch, 16, 6), new MKSBitSender(patch, 16, 6, 9)), 1, 9, 1, 1, -2);
    addWidget(leftPane, new CheckBoxWidget("Midi Pitch Bend", patch,
      new MKSBitModel(patch, 16, 5), new MKSBitSender(patch, 16, 5, 9)), 0, 10, 1, 1, -3);
    addWidget(leftPane, new CheckBoxWidget("Midi Exclusive", patch,
      new MKSBitModel(patch, 16, 4), new MKSBitSender(patch, 16, 4, 9)), 1, 10, 1, 1, -4);
    addWidget(leftPane, new CheckBoxWidget("Midi Hold (Sustain)", patch,
      new MKSBitModel(patch, 16, 3), new MKSBitSender(patch, 16, 3, 9)), 0, 11, 1, 1, -5);
    addWidget(leftPane, new CheckBoxWidget("Midi Mod Wheel", patch,
      new MKSBitModel(patch, 16, 2), new MKSBitSender(patch, 16, 2, 9)), 1, 11, 1, 1, -6);
    addWidget(leftPane, new CheckBoxWidget("Midi Volume", patch,
      new MKSBitModel(patch, 16, 1), new MKSBitSender(patch, 16, 1, 9)), 0, 12, 1, 1, -7);
    addWidget(leftPane, new CheckBoxWidget("Midi Portamento", patch,
      new MKSBitModel(patch, 16, 0), new MKSBitSender(patch, 16, 0, 9)), 1, 12, 1, 1, -8);
    addWidget(leftPane, new ScrollBarWidget("Mono Bender Range", patch, 0, 12, 0,
      new ParamModel(patch, 17), new MKSPatchSender(10)), 0, 13, 2, 1, 9);
    addWidget(leftPane, new ComboBoxWidget("Key Assign Mode", patch,
      new ParamModel(patch, 19), new MKSListSender(12,  new int[] {0, 64, 96}),
      new String [] {"Poly", "Chord Mem", "Mono"}), 0, 14, 1, 1, 10);
    addWidget(leftPane, new ScrollBarWidget("Chord Memory", patch, 0, 15, 1,
      new ParamModel(patch, 18), new MKSPatchSender(11)), 0, 15, 2, 1, 11);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.gridheight = 15;
    scrollPane.add(leftPane, gbc);

    pack();
  }
}

class MKSPatchSender extends SysexSender
{
  byte b[] = { (byte)0xF0, (byte)0x41, (byte)0x36, (byte)0x00, (byte)0x23,
               (byte)0x30, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0xF7 };
  public MKSPatchSender(int param)
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

// Offset sender & model are for parms that whose stored range begins above
// zero, e.g. key range is stored as 12-108
class MKSOfsModel extends ParamModel
{
  int pofs;
  public MKSOfsModel(Patch p, int o, int po)
  {
    patch = p;
    ofs = o;
    pofs = po;
  }
  public void set(int i)
  {
    patch.sysex[ofs] = (byte)(i + pofs);
  }
  public int get()
  {
    int i = patch.sysex[ofs] - pofs;
    if (i >= 0)
      return i;
    else
      return 0;
  }
}

class MKSOfsSender extends SysexSender
{
  int offset;
  byte b[] = { (byte)0xF0, (byte)0x41, (byte)0x36, (byte)0x00, (byte)0x23,
               (byte)0x30, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0xF7 };
  public MKSOfsSender(int param, int ofs)
  {
    offset = ofs;
    b[7] = (byte)param;
  }
  public byte [] generate (int value)
  {
    b[3] = (byte)(channel-1);
    b[8] = (byte)(value+offset);
    return b;
  }
}

// bit sender & model are for on/off parms stored as a single bit
// and where bit values are 0=on, 1=off (so value is inverted when
// stored & retrieved from patch)
class MKSBitModel extends ParamModel
{
  int bit;
  public MKSBitModel(Patch p, int o, int b)
  {
    patch = p;
    ofs = o;
    bit = b;
  }
  public void set(int i)
  {
    int mask = ~(1 << bit);
    int value = patch.sysex[ofs] & mask;
    if (i == 0)
      value |= (1 << bit);
    patch.sysex[ofs] = (byte)value;
  }
  public int get()
  {
    int mask = 1 << bit;
    if ((patch.sysex[ofs] & mask) > 0)
      return 0;
    else
      return 1;
  }
}

class MKSBitSender extends SysexSender
{
  Patch patch;
  int ofs;
  int bit;
  byte b[] = { (byte)0xF0, (byte)0x41, (byte)0x36, (byte)0x00, (byte)0x23,
               (byte)0x30, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0xF7 };
  public MKSBitSender(Patch p, int o, int bt, int param)
  {
    patch = p;
    ofs = o;
    bit = bt;
    b[7] = (byte)param;
  }
  public byte [] generate (int value)
  {
    int mask = ~(1 << bit);
    int bitfield = patch.sysex[ofs] & mask;
    if (value == 0)
      bitfield |= (1 << bit);
    b[3] = (byte)(channel - 1);
    b[8] = (byte)bitfield;
    return b;
  }
}

// 2's comp sender & model are for parms with symmetric +/- ranges stored
// in 7-bit 2's complement form, e.g. detune range means -12 to +12, with
// -12 stored as 116, -1 stored as 127, and nonnegative values stored as is.
class MKS2sCompModel extends ParamModel
{
  int max;
  public MKS2sCompModel(Patch p, int o, int m)
  {
    patch = p;
    ofs = o;
    max = m;
  }
  public void set(int i)
  {
    patch.sysex[ofs]=(byte)((i - max) & 0x7F);
  }
  public int get()
  {
    return (patch.sysex[ofs] + max) & 0x7F;
  }
}

class MKS2sCompSender extends SysexSender
{
  int max;
  byte b[] = { (byte)0xF0, (byte)0x41, (byte)0x36, (byte)0x00, (byte)0x23,
               (byte)0x30, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0xF7 };
  public MKS2sCompSender(int param, int m)
  {
    max = m;
    b[7] = (byte)param;
  }
  public byte [] generate (int value)
  {
    b[3] = (byte)(channel - 1);
    b[8] = (byte)((value - max) & 0x7F);
    return b;
  }
}

// list sender is for parms that have discontinuous values
class MKSListSender extends SysexSender
{
  byte b[] = { (byte)0xF0, (byte)0x41, (byte)0x36, (byte)0x00, (byte)0x23,
               (byte)0x30, (byte)0x01, (byte)0x00, (byte)0x00, (byte)0xF7 };
  int valueList[];
  public MKSListSender(int param, int v[])
  {
    b[7] = (byte)param;
    valueList = v;
  }
  public byte [] generate (int value)
  {
    b[3] = (byte)(channel - 1);
    b[8] = (byte)valueList[value];
    return b;
  }
}

