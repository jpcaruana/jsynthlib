package core;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import com.dreamfabric.DKnob;

/**
   Widget de type potentiometre rotatif. Utilise la classe DKnob de
   deamfabric qui se charge du dessin. Ce composant doit plutot etre
   utilise pour les parametres qui n'ont pas besoin d'un ajustement
   precis, et comportant beaucoup de valeurs.

   Il utilise les ToolTip pour afficher la valeur. Une version
   precedente utilisait un textfield pour cela mais le composant
   obtenu n'etait pas coherent, et etait difficile a placer.

   @author denis queffeulou mailto:dqueffeulou@free.fr
*/
public class KnobWidget extends SysexWidget {
    /** decalage a l'affichage */
    private int mBase;
    protected DKnob mKnob = new DKnob();
    //protected JLabel mLabel;
    private ImageIcon[] mImages;
    private JLabel mLabelImage;

    /**
       Constructeur special pour les classes derivees.
    */
    protected KnobWidget(String l, Patch p, int min, int max,
			 ParamModel ofs, SysexSender s) {
	super(l, p, min, max, ofs, s);
        setupUI();
    }

    /**
       @param base value display offset
    */
    public KnobWidget(String l, Patch p, int min, int max, int base,
		      ParamModel ofs, SysexSender s) {
	super(l, p, min, max, ofs, s);
        mBase = base;
        setupUI();
    }

    /**
       Affiche une image a droite de la valeur.
       @param aImages tableau d'images correspondant a chaque valeur.
    */
    public KnobWidget(String l, Patch p, int min, int max, int base,
		      ParamModel ofs, SysexSender s, ImageIcon[] aImages) {
	super(l, p, min, max, ofs, s);
        mBase = base;
	mImages = aImages;
        setupUI();
    }

    protected void setupUI() {
	mKnob.setDragType(DKnob.SIMPLE_MOUSE_DIRECTION);
	if (getLabel() != null) {
	    //mLabel = new JLabel(getLabel(), SwingConstants.CENTER);
	    getJLabel().setHorizontalAlignment(SwingConstants.CENTER);
	}
	int oValue = getValue();
	mKnob.setToolTipText(Integer.toString(oValue + mBase));

	int oWidthOff = 0;
	if (mImages != null) {
	    JPanel oPane = new JPanel(new BorderLayout(0, 0));
	    oPane.add(mKnob, BorderLayout.NORTH);
	    if (getLabel() != null) {
		//oPane.add(mLabel, BorderLayout.SOUTH);
		oPane.add(getJLabel(), BorderLayout.SOUTH);
	    }
	    setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
	    add(oPane);
	    mLabelImage = new JLabel(mImages[oValue]);
	    add(mLabelImage);
	    oWidthOff = 100;
	} else {
	    setLayout(new BorderLayout(0, 0));
	    //setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	    add(mKnob, BorderLayout.NORTH);
	    if (getLabel() != null) {
		//add(mLabel, BorderLayout.SOUTH);
		add(getJLabel(), BorderLayout.SOUTH);
	    }
	}
	// positionner la valeur courante
	mKnob.setValue(((float) getValue() - getValueMin()) / (getValueMax() - getValueMin()));
	//setMaximumSize(new Dimension(120+oWidthOff, 80));

	setupListener();
	setupMouseWheelListener();
    }

    protected void setupListener() {
	// Add a change listener to the knob
	mKnob.addChangeListener(new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
		    DKnob t = (DKnob) e.getSource();
		    int oValue = Math.round(t.getValue() * (getValueMax() - getValueMin())) + getValueMin();
		    String oVStr = Integer.toString(oValue + mBase);
		    t.setToolTipText(oVStr);
		    t.setValueAsString(oVStr);
		    if (mLabelImage != null) {
			mLabelImage.setIcon(mImages[oValue]);
		    }
		    sendSysex(oValue);
		}
	    });
    }

    // This is not overridden by KnobLookupWidget.
    protected void setupMouseWheelListener() {
	// mouse wheel event is supported by J2SE 1.4 and later
	mKnob.addMouseWheelListener(new MouseWheelListener() {
		public void mouseWheelMoved(MouseWheelEvent e) {
		    DKnob t = (DKnob) e.getSource();
		    if (t.hasFocus()) // to make consistent with other operation.
			t.setValue(t.getValue()
				   + (e.getWheelRotation()
				      / (float) (getValueMax() - getValueMin())));
		}
	    });
    }

    public void setValue(int v) {
	super.setValue(v);
	String oVStr = Integer.toString(v + mBase);
	mKnob.setToolTipText(oVStr);
	mKnob.setValueAsString(oVStr);
	if (mLabelImage != null) {
	    mLabelImage.setIcon(mImages[v]);
	}
	mKnob.setValue(((float) v - getValueMin())
		       / (getValueMax() - getValueMin()));
    }

    public void setEnabled(boolean e) {
        mKnob.setEnabled(e);
    }
}
