// written by Kenneth L. Martinez

package synthdrivers.SCIProphet600;

import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import core.CheckBoxWidget;
import core.ComboBoxWidget;
import core.EnvelopeWidget;
import core.ParamModel;
import core.Patch;
import core.PatchEditorFrame;
import core.ScrollBarWidget;

class P600ProgSingleEditor extends PatchEditorFrame {
  public P600ProgSingleEditor(Patch patch) {
    super ("Sequential Prophet-600 Program Editor", patch);

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

    JPanel osc1Pane = new JPanel();
    osc1Pane.setLayout(new GridBagLayout());
    addWidget(osc1Pane, new ScrollBarWidget("Freq", patch, 0, 63, 0,
      new P6001ByteModel(patch, 4, 0x7E, 1), null), 0, 0, 4, 1, 0);
    addWidget(osc1Pane, new CheckBoxWidget("Saw", patch, new P6001ByteModel(patch, 15, 0x01, 0),
      null), 0, 1, 1, 1, 0);
    addWidget(osc1Pane, new CheckBoxWidget("Tri", patch, new P6001ByteModel(patch, 15, 0x02, 1),
      null), 1, 1, 1, 1, 0);
    addWidget(osc1Pane, new CheckBoxWidget("Pulse", patch, new P6001ByteModel(patch, 14, 0x01, 0),
      null), 2, 1, 1, 1, 0);
    addWidget(osc1Pane, new CheckBoxWidget("Sync", patch, new P6001ByteModel(patch, 15, 0x04, 2),
      null), 3, 1, 1, 1, 0);
    addWidget(osc1Pane, new ScrollBarWidget("PW", patch, 0, 127, 0,
      new P6001ByteModel(patch, 0, 0x7F, 0), null), 0, 2, 4, 1, 0);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    osc1Pane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
      "Osc A", TitledBorder.CENTER, TitledBorder.CENTER));
    leftPane.add(osc1Pane, gbc);

    JPanel osc2Pane = new JPanel();
    osc2Pane.setLayout(new GridBagLayout());
    addWidget(osc2Pane, new ScrollBarWidget("Freq", patch, 0, 63, 0,
      new P6002ByteModel(patch, 3, 0xF8, 3, 0x01, 5), null), 0, 0, 4, 1, 0);
    addWidget(osc2Pane, new ScrollBarWidget("Fine", patch, 0, 127, 0,
      new P6002ByteModel(patch, 4, 0x80, 7, 0x3F, 1), null), 0, 1, 4, 1, 0);
    addWidget(osc2Pane, new CheckBoxWidget("Saw", patch, new P6001ByteModel(patch, 15, 0x08, 3),
      null), 0, 2, 1, 1, 0);
    addWidget(osc2Pane, new CheckBoxWidget("Tri", patch, new P6001ByteModel(patch, 15, 0x10, 4),
      null), 1, 2, 1, 1, 0);
    addWidget(osc2Pane, new CheckBoxWidget("Pulse", patch, new P6001ByteModel(patch, 14, 0x02, 1),
      null), 2, 2, 1, 1, 0);
    addWidget(osc2Pane, new ScrollBarWidget("PW", patch, 0, 127, 0,
      new P6001ByteModel(patch, 13, 0xFE, 1), null), 0, 3, 4, 1, 0);
    addWidget(osc2Pane, new ScrollBarWidget("Osc A-B Mix", patch, 0, 63, 0,
      new P6002ByteModel(patch, 5, 0xC0, 6, 0x0F, 2), null), 0, 4, 4, 1, 0);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    osc2Pane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
      "Osc B", TitledBorder.CENTER, TitledBorder.CENTER));
    leftPane.add(osc2Pane, gbc);

    JPanel pmodPane = new JPanel();
    pmodPane.setLayout(new GridBagLayout());
    gbc.weightx = 1;
    addWidget(pmodPane, new ScrollBarWidget("Filter", patch, 0, 15, 0,
      new P6002ByteModel(patch, 0, 0x80, 7, 0x07, 1), null), 0, 0, 4, 1, 0);
    addWidget(pmodPane, new ScrollBarWidget("Osc B", patch, 0, 127, 0,
      new P6002ByteModel(patch, 1, 0x80, 7, 0x3F, 1), null), 0, 1, 4, 1, 0);
    addWidget(pmodPane, new CheckBoxWidget("Freq A", patch, new P6001ByteModel(patch, 15, 0x20, 5),
      null), 1, 2, 1, 1, 0);
    addWidget(pmodPane, new CheckBoxWidget("Filter", patch, new P6001ByteModel(patch, 15, 0x40, 6),
      null), 2, 2, 1, 1, 0);
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    pmodPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
      "Poly-Mod", TitledBorder.CENTER, TitledBorder.CENTER));
    leftPane.add(pmodPane, gbc);

    JPanel lfoPane = new JPanel();
    lfoPane.setLayout(new GridBagLayout());
    gbc.weightx = 1;
    addWidget(lfoPane, new ScrollBarWidget("Speed", patch, 0, 15, 0,
      new P6001ByteModel(patch, 1, 0x78, 3), null), 0, 0, 4, 1, 0);
    addWidget(lfoPane, new ScrollBarWidget("Init Amt", patch, 0, 31, 0,
      new P6002ByteModel(patch, 2, 0xC0, 6, 0x07, 2), null), 0, 1, 4, 1, 0);
    addWidget(lfoPane, new ComboBoxWidget("Shape", patch,
      new P6001ByteModel(patch, 14, 0x10, 4),
      null, new String [] {"Square", "Triangle"}), 0, 2, 1, 1, 0);
    addWidget(lfoPane, new CheckBoxWidget("Freq A-B", patch, new P6001ByteModel(patch, 14, 0x20, 5),
      null), 1, 2, 1, 1, 0);
    addWidget(lfoPane, new CheckBoxWidget("PW A-B", patch, new P6001ByteModel(patch, 14, 0x40, 6),
      null), 2, 2, 1, 1, 0);
    addWidget(lfoPane, new CheckBoxWidget("Filter", patch, new P6001ByteModel(patch, 14, 0x80, 7),
      null), 3, 2, 1, 1, 0);
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    lfoPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
      "LFO-Mod", TitledBorder.CENTER, TitledBorder.CENTER));
    leftPane.add(lfoPane, gbc);

    JPanel envPane = new JPanel();
    envPane.setLayout(new GridBagLayout());
    addWidget(envPane, new ScrollBarWidget("Cutoff Freq", patch, 0, 127, 0,
      new P6002ByteModel(patch, 6, 0xF0, 4, 0x07, 4), null), 0, 0, 3, 1, 0);
    addWidget(envPane, new ScrollBarWidget("Resonance", patch, 0, 63, 0,
      new P6002ByteModel(patch, 7, 0xF8, 3, 0x01, 5), null), 0, 1, 3, 1, 0);
    addWidget(envPane, new ScrollBarWidget("Env Amount", patch, 0, 15, 0,
      new P6001ByteModel(patch, 8, 0x1E, 1), null), 0, 2, 3, 1, 0);
    addWidget(envPane, new ComboBoxWidget("Kybd Track", patch,
      new P6002FieldModel(patch, 14, 0x08, 3, 0x04, 1),
      null, new String [] {"Off", "1/2", "Full"}), 0, 3, 1, 1, 0);
    addWidget(envPane, new EnvelopeWidget(" ", patch, new EnvelopeWidget.Node [] {
      new EnvelopeWidget.Node(0, 0, null, 0, 0, null, 0, false, null, null, null, null),
      new EnvelopeWidget.Node(0, 15, new P6001ByteModel(patch, 10, 0x1E, 1), 15, 15,
        null, 0, false, null, null, "Attack", null),
      new EnvelopeWidget.Node(0, 15, new P6002ByteModel(patch, 9, 0xE0, 5, 0x01, 3), 0, 15,
        new P6001ByteModel(patch, 9, 0x1E, 1), 0, false, null, null, "Decay", "Sustain"),
      new EnvelopeWidget.Node(0, 15, new P6002ByteModel(patch, 8, 0xE0, 5, 0x01, 3), 0, 0,
        null, 0, false, null, null, "Release", null),
    }     ), 0, 4, 3, 4, 0);
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    envPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
      "Filter", TitledBorder.CENTER, TitledBorder.CENTER));
    rightPane.add(envPane, gbc);

    JPanel ampPane = new JPanel();
    ampPane.setLayout(new GridBagLayout());
    addWidget(ampPane, new EnvelopeWidget(" ", patch, new EnvelopeWidget.Node [] {
      new EnvelopeWidget.Node(0, 0, null, 0, 0, null, 0, false, null, null, null, null),
      new EnvelopeWidget.Node(0, 15, new P6001ByteModel(patch, 12, 0x1E, 1), 15, 15,
        null, 0, false, null, null, "Attack", null),
      new EnvelopeWidget.Node(0, 15, new P6002ByteModel(patch, 11, 0xE0, 5, 0x01, 3), 0, 15,
        new P6001ByteModel(patch, 11, 0x1E, 1), 0, false, null, null, "Decay", "Sustain"),
      new EnvelopeWidget.Node(0, 15, new P6002ByteModel(patch, 10, 0xE0, 5, 0x01, 3), 0, 0,
        null, 0, false, null, null, "Release", null),
    }     ), 0, 0, 3, 4, 0);
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    ampPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
      "Amplifier", TitledBorder.CENTER, TitledBorder.CENTER));
    rightPane.add(ampPane, gbc);

    JPanel cmnPane =  new JPanel();
    cmnPane.setLayout(new GridBagLayout());
    addWidget(cmnPane, new ScrollBarWidget("Glide", patch, 0, 15, 0,
      new P6002ByteModel(patch, 12, 0xE0, 5, 0x01, 3), null), 0, 1, 3, 1, 0);
    addWidget(cmnPane, new CheckBoxWidget("Unison", patch, new P6001ByteModel(patch, 15, 0x80, 7),
      null), 2, 2, 1, 1, 0);
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.gridwidth = 1;
    gbc.gridheight = 1;
    cmnPane.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED),
      "Common", TitledBorder.CENTER, TitledBorder.CENTER));
    rightPane.add(cmnPane, gbc);

    pack();
  }
}

// for parms stored as a single bit field: need offset in patch (0-15), mask
// of bits used, and # bits to shift right
class P6001ByteModel extends ParamModel {
  static final int HDRLEN = 4;
  int mask;
  int shft;
  public P6001ByteModel(Patch p, int o, int m, int s) {
    super(p, o);
    mask = m;
    shft = s;
  }
  public void set(int i) {
    int dat = patch.sysex[ofs * 2 + HDRLEN] | (patch.sysex[ofs * 2 + HDRLEN + 1] << 4);
    dat = (dat & ~mask) | ((i << shft) & mask);
    patch.sysex[ofs * 2 + HDRLEN] = (byte)(dat & 0x0F);
    patch.sysex[ofs * 2 + HDRLEN + 1] = (byte)(dat >> 4);
  }
  public int get() {
    int dat = patch.sysex[ofs * 2 + HDRLEN] | (patch.sysex[ofs * 2 + HDRLEN + 1] << 4);
    return (dat & mask) >> shft;
  }
}

// for parms stored as two bit fields: need offset in patch (0-15), mask
// of bits used and # bits to shift right, plus mask and shift values
// for next byte in patch
class P6002ByteModel extends ParamModel {
  static final int HDRLEN = 4;
  int mask1;
  int shft1;
  int mask2;
  int shft2;
  public P6002ByteModel(Patch p, int o, int m1, int s1, int m2, int s2) {
    super(p, o);
    mask1 = m1;
    shft1 = s1;
    mask2 = m2;
    shft2 = s2;
  }
  public void set(int i) {
    int dat1 = patch.sysex[ofs * 2 + HDRLEN] | (patch.sysex[ofs * 2 + HDRLEN + 1] << 4);
    int dat2 = patch.sysex[ofs * 2 + HDRLEN + 2] | (patch.sysex[ofs * 2 + HDRLEN + 3] << 4);
    dat1 = (dat1 & ~mask1) | ((i << shft1) & mask1);
    dat2 = (dat2 & ~mask2) | ((i >> shft2) & mask2);
    patch.sysex[ofs * 2 + HDRLEN] = (byte)(dat1 & 0x0F);
    patch.sysex[ofs * 2 + HDRLEN + 1] = (byte)(dat1 >> 4);
    patch.sysex[ofs * 2 + HDRLEN + 2] = (byte)(dat2 & 0x0F);
    patch.sysex[ofs * 2 + HDRLEN + 3] = (byte)(dat2 >> 4);
  }
  public int get() {
    int dat1 = patch.sysex[ofs * 2 + HDRLEN] | (patch.sysex[ofs * 2 + HDRLEN + 1] << 4);
    int dat2 = patch.sysex[ofs * 2 + HDRLEN + 2] | (patch.sysex[ofs * 2 + HDRLEN + 3] << 4);
    return ((dat1 & mask1) >> shft1) |
           ((dat2 & mask2) << shft2);
  }
}

// for parms stored as two bit fields in one byte: need offset in patch (0-15),
// mask of bits used and # bits to shift right for first field, plus mask and
// shift values for second field
class P6002FieldModel extends ParamModel {
  static final int HDRLEN = 4;
  int mask1;
  int shft1;
  int mask2;
  int shft2;
  public P6002FieldModel(Patch p, int o, int m1, int s1, int m2, int s2) {
    super(p, o);
    mask1 = m1;
    shft1 = s1;
    mask2 = m2;
    shft2 = s2;
  }
  public void set(int i) {
    int dat = patch.sysex[ofs * 2 + HDRLEN] | (patch.sysex[ofs * 2 + HDRLEN + 1] << 4);
    dat = (dat & ~mask1) | ((i << shft1) & mask1);
    dat = (dat & ~mask2) | ((i << shft2) & mask2);
    patch.sysex[ofs * 2 + HDRLEN] = (byte)(dat & 0x0F);
    patch.sysex[ofs * 2 + HDRLEN + 1] = (byte)(dat >> 4);
  }
  public int get() {
    int dat = patch.sysex[ofs * 2 + HDRLEN] | (patch.sysex[ofs * 2 + HDRLEN + 1] << 4);
    return ((dat & mask1) >> shft1) |
           ((dat & mask2) >> shft2);
  }
}

