package synthdrivers.YamahaMotif;
import core.*;
import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListDataEvent;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Iterator;

public class YamahaMotifNormalVoiceEditor extends PatchEditorFrame {
  int slidercount = 0;
  private Container panel;
  public YamahaMotifNormalVoiceEditor(Patch p) {
    super("Yamaha Motif Normal Voice Editor", p);
    JTabbedPane tabs = new JTabbedPane();
    tabs.addTab("General", new GeneralPanel());
    tabs.addTab("Arp/LFO", new ArpPanel());
    //tabs.addTab("Effects", new EffectsPanel());
    for (int i = 0; i < 4; i++)
      tabs.addTab((i==0?"Element ":"") + (i+1), new ElementPanel(i));
    gbc.fill = gbc.BOTH;
    gbc.weightx = gbc.weighty = 10;
    scrollPane.add(tabs, gbc);
  }
  protected ComboBoxWidget addCombo(String name, int address, String[] options){
    return addCombo(name, address, 0, false, options);
  }
  protected ComboBoxWidget addCombo(String name, int address, boolean _short, 
				    String[] options) {
    return addCombo(name, address, 0, _short, options);
  }
  protected ComboBoxWidget addCombo(String name, int address, int mid, 
				    String[] options) {
    return addCombo(name, address, mid, false, options);
  }
  protected ComboBoxWidget addCombo(String name, int address, int mid,
				    boolean _short, String[] options) {
    int sendaddress = address;
    if ( (byte)(address >> 8) == (byte) -1)
      sendaddress = (address & 0x7f007f) | (mid << 8);
    ComboBoxWidget combo = 
      new ComboBoxWidget(name, p, 
			 new MotifParamModel(p, address, mid, _short),
			 new ParamSender(sendaddress, _short), options);
    addWidget(panel, combo, slidercount++ );
    return combo;
  }
  protected ScrollBarWidget addSlider(String name, int address,int min,int max){
    return addSlider(name, address, 0, false, min, max, 0);
  }
  protected ScrollBarWidget addSlider(String name, int address, int min, 
				      int max, int offset) {
    return addSlider(name, address, 0, false, min, max, offset);
  }
  protected ScrollBarWidget addSlider(String name, int address, boolean _short, 
				      int min, int max, int offset){
    return addSlider(name, address, 0, _short, min, max, offset);
  }
  protected ScrollBarWidget addSlider(String name, int address, int mid, 
				      int min, int max, int offset){
    return addSlider(name, address, mid, false, min, max, offset);
  }
  protected ScrollBarWidget addSlider(String name, int address, int mid,
				      boolean _short, int min, int max,
				      int offset) {
    int sendaddress = address;
    if ( (byte)(address >> 8) == (byte) -1)
      sendaddress = (address & 0x7f007f) | (mid << 8);
    ScrollBarWidget bar =
      new ScrollBarWidget(name, p, min, max, offset,
			  new MotifParamModel(p, address, mid, _short),
			  new ParamSender(sendaddress, _short));
    addWidget(panel, bar, slidercount++ );
    return bar;
  }
  protected ScrollBarWidget addLabeledSlider(String name, int address, int mid,
					     boolean _short, int min, int max,
					     int offset) {
    Box box = Box.createVerticalBox();
    panel.add(box);
    int sendaddress = address;
    if ( (byte)(address >> 8) == (byte) -1)
      sendaddress = (address & 0x7f007f) | (mid << 8);
    JLabel label = new JLabel(name);
    label.setMaximumSize(label.getPreferredSize());
    box.add(label);
    ScrollBarWidget bar =
      new ScrollBarWidget("", p, min, max, offset,
			  new MotifParamModel(p, address, mid, _short),
			  new ParamSender(sendaddress, _short));
    bar.jlabel = label;
    box.add(bar);
    addWidget(null, bar, slidercount++, false);
    return bar;
  }

  protected void addFiller(int w, int h) {
    panel.add(Box.createRigidArea(new Dimension(w, h)));
  }
  protected void addFiller(Container c, int w, int h) {
    c.add(Box.createRigidArea(new Dimension(w, h)));
  }
  protected void addGlue() {panel.add(Box.createGlue());}
  protected void addGlue(Container c) {c.add(Box.createGlue());}
  protected void addHGlue() {panel.add(Box.createHorizontalGlue());}
  protected void addHGlue(Container c) {c.add(Box.createHorizontalGlue());}
  protected void addVGlue() {panel.add(Box.createVerticalGlue());}
  protected void addVGlue(Container c) {c.add(Box.createVerticalGlue());}
  protected void addLabel(String l) {panel.add(new JLabel(l));}
  // Sorry, I like boxes.
  public void addWidget(Container parent, SysexWidget widget, int slidernum) {
    addWidget(parent, widget, slidernum, true);
  }
  public void addWidget(Container parent, SysexWidget widget, int slidernum, 
			boolean alignleft) {
    try {
      if (parent != null)
	parent.add (widget);
      widget.setSliderNum(slidernum);
      //      widget.setBorder(BorderFactory.createLineBorder(Color.red));
      if (alignleft)
	widget.setAlignmentX(widget.LEFT_ALIGNMENT);
      widget.setMaximumSize(widget.getPreferredSize());
	 setPreferredSize(getMinimumSize());

      if (widget instanceof ScrollBarWidget)
	sliderList.add(((ScrollBarWidget)widget).slider);
      if (widget instanceof VertScrollBarWidget)
	sliderList.add(((VertScrollBarWidget)widget).slider);
     if (widget instanceof ScrollBarLookupWidget)
       sliderList.add(((ScrollBarLookupWidget)widget).slider);
     widgetList.add(widget);
    }
    catch (Exception e) {ErrorMsg.reportStatus(e);}     

  }

  class GeneralPanel extends JPanel {
    public GeneralPanel () {
      super();

      setLayout( new BoxLayout(this, BoxLayout.Y_AXIS) );
      Container c;
      Container row = Box.createHorizontalBox();
      add(row);

      panel = new ControlPanel("Name");
      row.add(panel);
      addFiller(row, 5, 0);
      // Add an action listener to update the name, or do one ourselves.
      //panel = Box.createHorizontalBox();
      //c.add(panel);
      addWidget(panel, new PatchNameWidget(p, "Name"),slidercount++);
      //addHGlue();
      //panel = Box.createHorizontalBox();
      //c.add(panel);
      ComboBoxWidget cat, sub;
      cat = 
	addCombo("Category", 0x40700C, 
		  new String[] {"--","A. Piano","Keyboard","Organ","Guitar",
				"Bass","Strings","Brass","Reed/Pipe",
				"Synth Lead","Syn Pad/Choir","Synth Comp",
				"Chromatic Perc","Drum/Perc","Sound FX",
				"Musical FX","Combination"});
      //addHGlue();
      //panel = Box.createHorizontalBox();
      //c.add(panel);
      sub = addCombo("Subcategory", 0x40700D,new String[] {"--"});
      cat.cb.addActionListener( new CategoryActionListener(cat, sub) );
      //addHGlue();
      //addVGlue(c);

      c = new ControlPanel("Poly Mode");
      row.add(c);
      panel = Box.createHorizontalBox();
      c.add(panel);
      addCombo("",0x400003,new String[]{"Mono","Poly"});
      addHGlue();
      addCombo("Note Mode",0x400002, new String[]{"Single","Multi"});
      
      panel = Box.createHorizontalBox();
      c.add(panel);
      addCombo("Tuning",0x400004, 
	       new String[] { "Equal Temp","Pure Major C","Pure Major C#",
			      "Pure Major D","Pure Major D#","Pure Major E",
			      "Pure Major F","Pure Major F#","Pure Major G",
			      "Pure Major G#","Pure Major A","Pure Major A#",
			      "Pure Major B","Pure Minor A","Pure Minor A#",
			      "Pure Minor B","Pure Minor C","Pure Minor C#",
			      "Pure Minor D","Pure Minor D#","Pure Minor E",
			      "Pure Minor F","Pure Minor F#","Pure Minor G",
			      "Pure Minor G#","Werkmeister","Kirnberger",
			      "Vallotti & Young","1/4 Shifted","1/4 Tone",
			      "1/8 Tone","Indian"});
      addHGlue();
      addVGlue(c);
      addHGlue(row);

      row = Box.createHorizontalBox();
      add(row);
      c = new ControlPanel("Portamento");
      row.add(c);
      addFiller(row, 5, 0);

      panel = Box.createHorizontalBox();
      c.add(panel);
      addCombo("",0x400008, new String[] {"Off","On"});
      addHGlue();
      panel = Box.createHorizontalBox();
      c.add(panel);
      addSlider("Time", 0x400009, 0, 0x7f);
      addHGlue();
      panel = Box.createHorizontalBox();
      c.add(panel);
      addCombo("Mode",0x400007, new String[]{"Legato","Always"});
      addHGlue();
      addCombo("Time Mode",0x40000A,new String[]{"Rate","Time"});
      addHGlue(row);


      row = new ControlPanel("Quick Edit");
      add(row);
      addHGlue(row);
      c = new ControlPanel("Output");
      row.add(c);
      addFiller(row, 0, 5);
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Volume", 0x400001, 0, false,  0, 0x7f, 0);
      addLabeledSlider("Pan", 0x407025, 0, false, 1, 0x7f, -64);
      //addHGlue();
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Chorus Send", 0x40000C, 0, false, 0, 0x7f, 0);
      addLabeledSlider("Reverb Send", 0x40000B, 0, false, 0, 0x7f, 0);
      //addHGlue();

      c = new ControlPanel("Master EQ Offsets");
      row.add(c);
      addFiller(row, 0, 5);
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Low", 0x407031,0, false, 0, 127, -64);
      addLabeledSlider("Low Mid", 0x407032,0, false, 0, 127, -64);
      //addHGlue();
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("High Mid", 0x407033,0, false, 0, 127, -64);
      addLabeledSlider("High", 0x407034,0, false, 0, 127, -64);
      //addHGlue();

      c = new ControlPanel("Amp EG Offsets");
      row.add(c);
      panel = Box.createHorizontalBox();
      c.add(panel);
      addFiller(c, 0, 5);
      addLabeledSlider("Attack",0x407039,0, false, 0,0x7f,-64);
      addLabeledSlider("Decay",0x407027,0, false, 0, 0x7f, -64);
      //addHGlue();
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Sustain", 0x407028,0, false, 0, 0x7f, -64);
      addLabeledSlider("Release", 0x40703a,0, false, 0, 0x7f, -64);
      //addHGlue();
      
      c = new ControlPanel("Filter EG Offsets");
      row.add(c);
      addFiller(row, 0, 5);
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Attack",0x407029,0, false, 0,0x7f,-64);
      addLabeledSlider("Decay",0x40702A,0, false, 0, 0x7f, -64);
      //addHGlue();
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Release", 0x40703a,0, false, 0, 0x7f, -64);
      addLabeledSlider("Depth", 0x40702D,0, false, 0, 0x7f, -64);
      //addHGlue();

      c = new ControlPanel("Assignable Knobs");
      row.add(c);
      addFiller(row, 0, 5);
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Knob A", 0x40702e,0, false, 0, 127, -64);
      addLabeledSlider("Knob B", 0x40702f,0, false, 0, 127, -64);
      //addHGlue();
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Knob 1", 0x407035,0, false, 0, 127, -64);
      addLabeledSlider("Knob 2", 0x407036,0, false, 0, 127, -64);
      //addHGlue();

      
      c = new ControlPanel("Misc.");
      row.add(c);
      addFiller(row, 0, 5);
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Filter Cutoff Offset",0x407037,0, false, 0, 0x7f, -64);
      addLabeledSlider("Resonance Offset", 0x407038,0, false, 0, 0x7f, -64);
      //addHGlue();
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Chorus Send Offset", 0x407026, 0, false, 0, 0x7f, -64);
      addLabeledSlider("Pitch Bend Upper Range",0x400005,0,false,0x10,0x58,-64);
      //addHGlue();
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Pitch Bend Lower Range",0x400006,0,false,0x10,0x58,-64);
      addCombo("Default Knob Mode", 0x407024, new String[] {"Pan & Send","Tone",
							    "Assign",
							    "M EQ Offset"});
      //addHGlue();

    }
  }
  class CategoryActionListener implements ActionListener {
    final String[][] subs = 
      new String[][] { {"--"},
		       {"--","A. Piano","E. Grand","Other"},
		       {"--","E. Piano","Clavi","Other"},
		       {"--","Electric","Pipe","Other"},
		       {"--","A. Guitar","E. Guitar","Pluck"},
		       {"--","A. Bass","E. Bass","Synth"},
		       {"--","Solo","Ensemble","Synth"},
		       {"--","Solo","Section","Synth"},
		       {"--","Sax","Pipe","Other"},
		       {"--","Hard","Soft"},
		       {"--","Bright","Soft","Choir"},
		       {"--","Hard","Soft"},
		       {"--","Mallet","Bell","Percussion"},
		       {"--","Drums","Percussion"},
		       {"--","Synth","Natural"},
		       {"--","Motion","Hit"},
		       {"--","Split","Sequence"}
                     };
    ComboBoxWidget cat, sub;
    public CategoryActionListener (ComboBoxWidget _cat, ComboBoxWidget _sub) {
      cat = _cat;
      sub = _sub;
      int value = cat.getValue();
      if (value >= subs.length || value < 0)
	value = 0;
      sub.cb.removeAllItems();
      for (int i = 0; i < subs[value].length; i++)
	sub.cb.addItem(subs[value][i]);
      try {
	sub.cb.setSelectedIndex(sub.getValue());
      } catch (Exception e) {}
    }
    public void actionPerformed (ActionEvent e) {
      int value = cat.getValue();
      if (value >= subs.length || value < 0)
	value = 0;
      sub.cb.removeAllItems();
      for (int i = 0; i < subs[value].length; i++)
	sub.cb.addItem(subs[value][i]);
      sub.setValue(0);
    }
  }
  class ArpPanel extends JPanel {
    public ArpPanel () {
      super();
      setLayout( new BoxLayout(this, BoxLayout.Y_AXIS) );
      ControlPanel arp = new ControlPanel("Arpeggio");
      //      arp.setLayout(new BoxLayout(arp, BoxLayout.X_AXIS));
      Container c;
      arp.setAlignmentX(arp.LEFT_ALIGNMENT);
      add(arp);
      addFiller(this, 0, 5);

      panel = new ControlPanel("General");
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      arp.add(panel);
      //addFiller(arp, 0, 5);
      addCombo("",0x407015,new String[] {"Off", "On"});
      addCombo("Hold",0x407016, new String[] {"sync-off","off","on"});
      addCombo("Bank",0x407013, new String[] {"Pre 1", "Pre 2", "User"});
      ComboBoxWidget cw = 
	addCombo("Arp. #", 0x407014, new String[] {""});
      cw.cb.setModel(new NumberComboBoxModel(128));
      cw.setValue(cw.getValue());
      addHGlue();

      c = new ControlPanel("Limit");
      arp.add(c);
      addFiller(arp, 0, 5);
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Note Low", 0x407018, 0, false, 0, 0x7f, 0);
      addLabeledSlider("Note High",0x407019, 0, false, 0, 0x7f, 0);
      addHGlue();
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Vel. Low", 0x40701A, 0, false, 0, 0x7f, 0);
      addLabeledSlider("Vel. High",0x40701B, 0, false, 0, 0x7f, 0);
      addHGlue();

      c = new ControlPanel("Other");
      arp.add(c);
      addFiller(arp, 0, 5);
      panel = Box.createHorizontalBox();
      c.add(panel);
      addCombo("Key Mode",0x40701C, new String[] {"Sort","Thru","Direct"});
      addCombo("Vel. Mode",0x40701D,new String[]{"Original", "Thru"});
      addHGlue();
      panel = Box.createHorizontalBox();
      c.add(panel);
      addSlider("Arp Tempo", 0x407022, true, 1, 300, 0);
      addCombo("Unit Multiply", 0x407017, new String[] {"50%","66%","75%",
							"100%","133%","150%",
							"200%"});
      addHGlue();
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Vel. Rate",0x40701E, 0,true, 0, 0xC8, 0);
      addLabeledSlider("Gate Rate",0x407020, 0, true, 0, 0xC8, 0);
      addHGlue();
      addHGlue(arp);


      arp = new ControlPanel("LFO");
      arp.setAlignmentX(arp.LEFT_ALIGNMENT);
      add(arp);
      EnvelopeWidget w = 
	new EnvelopeWidget("Envelope", p,
			   new EnvelopeNode[] {
			     new EnvelopeNode(0, 127,
					      new MotifParamModel(p,0x400605),
					      5,5, null,
					      0, false,
					      new ParamSender(0x400605),null,
					      "Delay",null),
			     new EnvelopeNode(0,127,
					      new MotifParamModel(p,0x400606),
					      30,30,null,
					      0, false,
					      new ParamSender(0x400606),null,
					      "Fade In", null),
			     new EnvelopeNode(0,127,
					      new MotifParamModel(p,0x400607),
					      30,30,null,
					      0, false,
					      new ParamSender(0x400607),null,
					      "Hold", null),
			     new EnvelopeNode(0,127,
					      new MotifParamModel(p,0x400608),
					      5,5,null,
					      0, false,
					      new ParamSender(0x400608),null,
					      "Fade Out", null),
			   });
      addWidget(arp, w, 500);
    }
  }
  /*  class EffectsPanel extends JPanel {
    public EffectsPanel () {
      super();
      setLayout( new BoxLayout(this, BoxLayout.Y_AXIS) );
    }
    }*/
  class ElementPanel extends JPanel {
    int element;
    ComboBoxWidget bank, wave;
    public ElementPanel ( int i ) {
      super();
      element = i;
      setLayout( new BoxLayout(this, BoxLayout.Y_AXIS) );

      Container c = new ControlPanel("Oscillator");
      add(c);
      panel = Box.createHorizontalBox();
      c.add(panel);
      addCombo("",0x41ff00,element,new String[] {"Off","On"});
      bank = addCombo("Bank", 0x41ff01,element,new String[] {"Preset","User"});
      wave = addCombo("Wave",0x41ff03,element, true, new String[] {""});
      wave.cb.setModel(new NumberComboBoxModel(16384,0));
      bank.cb.addActionListener(new BankActionListener());
      addHGlue();

      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Level",0x41ff20,element, false, 0, 0x7f, 0);
      addLabeledSlider("Pan",0x41ff08, element, false, 0, 0x7f, -64);
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Random Pan Depth",0x41ff09,element, false, 0, 0x7f,0);
      addLabeledSlider("Alternate Pan Depth",0x41ff0A,element,false,0,0x7f,-64);
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Scaling Pan Depth",0x41ff0B,element,false,0,0x7f,-64);
      addLabeledSlider("Velocity Cross Fade",0x41ff10,element,false,0,0x7f,0);
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Note Limit Low", 0x41ff0C,element,false,0,0x7f,0);
      addLabeledSlider("Note Limit High",0x41ff0D,element,false,0,0x7f,0);
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Vel. Limit Low", 0x41ff0E,element,false,0,0x7f,0);
      addLabeledSlider("Vel. Limit High",0x41ff0F,element,false,0,0x7f,0);
      panel = Box.createHorizontalBox();
      c.add(panel);
      addLabeledSlider("Level Velocity Sensitivity",0x41ff21,element,false,
		       0,0x7f,-64);
      addLabeledSlider("Level Sensitivity Curve",0x41ff22,element,false,
		       0,4,0);
      panel = Box.createHorizontalBox();
      c.add(panel);
      addWidget(panel, new 
		EnvelopeWidget("Amp Envelope", p, new EnvelopeNode[] { new
			       EnvelopeNode(0,127,
					    new MotifParamModel(p,0x41ff11,
								element),
					    0,0,null,
					    0, false,
					    new ParamSender(0x41ff11,element)
 					    ,null,"Delay", null), new
			       EnvelopeNode(10,10,null,
					    0,127,
					    new MotifParamModel(p,0x41ff28,
								element),
					    0,false,
					    null,new
					    ParamSender(0x41ff28, element),
					    null,"Init"), new
			       EnvelopeNode(0,127,new
					    MotifParamModel(p,0x41ff24,element),
					    127,127,null,0,false,new
					    ParamSender(0x41ff24, element),
					    null,"Attack",null), new
			       EnvelopeNode(0,127,new
					    MotifParamModel(p,0x41ff25,element),
					    0,127,new
					    MotifParamModel(p,0x41ff2a,element),
					    0,false,new
					    ParamSender(0x41ff25, element),
 					    new ParamSender(0x41ff25,element),
					    "Decay 1 Time", "Decay 1 Level"),
			       new EnvelopeNode(0,127,new
					    MotifParamModel(p,0x41ff26,element),
					    0,127,new
					    MotifParamModel(p,0x41ff2b,element),
					    0,false,new
                                            ParamSender(0x410025, element),
 					    new ParamSender(0x41ff25,element),
					    "Decay 2", "Sustain"), new
			       EnvelopeNode(100,100,null,
					    EnvelopeNode.SAME,EnvelopeNode.SAME,
					    null,0,false,null,
					    null,null,null), new
			       EnvelopeNode(0,127,new
					    MotifParamModel(p,0x41ff27,element),
					    0,0,
					    null,0,false,new
					    ParamSender(0x41ff27, element),
					    null,"Release",null),

			       }),600+10*element);
    }

    class BankActionListener implements ActionListener {
      public BankActionListener() {
	int value = bank.getValue();
	if (value != 0)
	  ((NumberComboBoxModel)(wave.cb.getModel())).setSize(257);
	wave.setValue(wave.getValue());
      }
      public void actionPerformed (ActionEvent e) {
	int value = bank.getValue();
	wave.setValue(0);
	if (value == 0)
	  ((NumberComboBoxModel)(wave.cb.getModel())).setSize(16384);
	else
	  ((NumberComboBoxModel)(wave.cb.getModel())).setSize(257);
      }
    }
  }
  class ControlPanel extends JPanel {
    public ControlPanel ( String name ) {
      super();
      setBorder( BorderFactory.createTitledBorder( name ));
      setLayout( new BoxLayout(this, BoxLayout.Y_AXIS) );
    }
  }
  class ParamSender extends SysexSender{
    byte[] sysex;
    boolean twobytes;
    public ParamSender ( int address ) {
      twobytes = false;
      sysex = new byte[9];
      setup(address);
    }
    public ParamSender (int address, boolean _twobytes ) {
      twobytes = _twobytes;
      sysex = new byte[ (twobytes ? 10 : 9 ) ];
      setup(address);
    }
    public ParamSender (int address, int mid) {
       if ( (byte)(address >> 8) == -1 )
 	address = (address & 0x7f007f) | (mid << 8);
       twobytes = false;
       sysex = new byte[9];
       setup(address);
     }
     public ParamSender (int address, int mid, boolean _twobytes) {
       if ( (byte)(address >> 8) == -1 )
 	address = (address & 0x7f007f) | (mid << 8);
       twobytes = _twobytes;
       sysex = new byte[ (twobytes ? 10 : 9 ) ];
       setup(address);
     }
    private void setup(int address) {
      sysex[0] = (byte) 0xF0;
      sysex[1] = (byte) 0x43;
      sysex[3] = (byte) 0x6B;
      sysex[4] = (byte) ((address >> 16) & 127);
      sysex[5] = (byte) ((address >>8) & 127);
      sysex[6] = (byte) (address & 127);
      sysex[sysex.length - 1] = (byte) 0xF7;
    }
    public byte[] generate(int value) {
      sysex[2] = (byte) (0x10 | (channel - 1));
      if (twobytes) {
	sysex[7] = (byte)( ( value >> 7 ) & 127 );
	sysex[8] = (byte)( value & 127 );
      } else {
	sysex[7] = (byte)( value & 127 );
      }
      return sysex;
    }
  }
  class MotifParamModel extends ParamModel {
    boolean twobytes;
    public MotifParamModel ( Patch p, int address ) {
      super(p, address);
      setAddress( 0 );
      twobytes = false;
    }
    public MotifParamModel(Patch p, int address, boolean _short) {
      super(p, address);
      setAddress( 0 );
      twobytes = _short;
    }
    public MotifParamModel(Patch p, int address, int _mid) {
      super(p, address);
      setAddress( _mid & 127 );
      twobytes = false;
    }
    public MotifParamModel(Patch p, int address, int _mid, boolean _short) {
      super(p, address);
      setAddress( _mid & 127 );
      twobytes = _short;
    }
    protected void setAddress( int mid ) {
       if ( (byte)(ofs >> 8) == -1 )
 	ofs = (ofs & 0x7f007f) | (mid << 8);
     }	
    public void set(int value) {
      if (twobytes)
	YamahaMotifSysexUtility.setShortParameter(patch.sysex, ofs, value);
      else
	YamahaMotifSysexUtility.setParameter(patch.sysex, ofs, value);
    }
    public int get () {
      if (twobytes)
	return YamahaMotifSysexUtility.getShortParameter(patch.sysex, ofs);
      else
	return YamahaMotifSysexUtility.getParameter(patch.sysex, ofs);
    }
  }
  class NumberComboBoxModel implements ComboBoxModel {
    protected int size;
    protected int offset = 1;
    protected int selected;
    protected HashSet listeners = new HashSet();
    public NumberComboBoxModel (int _size) {
      size = _size;
    }
    public NumberComboBoxModel(int _size, int _offset) {
      size = _size;
      offset = _offset;
    }
    public void setSelectedItem(Object str) {
      if (str == null)
	selected = 0;
      else
	selected = Integer.parseInt((String)str) - offset;
    }
    public Object getSelectedItem() {
      return Integer.toString(selected + offset);
    }
    public int getSize() { 
      return size; 
    }
    public Object getElementAt(int i) {
      return Integer.toString(i + offset);
    }
    public void addListDataListener(ListDataListener l) {
      listeners.add(l);
    }
    public void removeListDataListener(ListDataListener l) {
      listeners.remove(l);
    }
    public int getOffset () {
      return offset;
    }
    public void setOffset(int _offset) {
      offset= _offset;
    }
    public void setSize( int new_size ) {
      ListDataEvent e;
      Iterator it = listeners.iterator();
      if (new_size == size)
	return;
      else if (new_size > size) {
	e = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, size,
			      new_size - 1);
	while (it.hasNext())
	  ((ListDataListener)(it.next())).intervalAdded(e);
      } else {
	e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, new_size, 
			      size - 1);
	while (it.hasNext())
	  ((ListDataListener)(it.next())).intervalRemoved(e);
      }
    }
  }
}
