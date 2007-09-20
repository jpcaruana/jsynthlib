/*
 * DKnob.java
 * (c) 2000 by Joakim Eriksson
 */
package com.dreamfabric;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 * DKnob is a component similar to JSlider but with
 * round "user interface", a knob.
 * @author Denis Queffeulou for some modifications and optimization.
 */
public class DKnob extends JComponent {
    private final static float START = 225;
    private final static float LENGTH = 270;
    private final static float START_ANG = (START/360)*(float)Math.PI*2;
    private final static float LENGTH_ANG = (LENGTH/360)*(float)Math.PI*2;
    private final static float LENGTH_ANG_DIV10 = (float)(LENGTH_ANG/10.01);
    private final static float MULTIP = 180 / (float)Math.PI;
    private final static Color DEFAULT_FOCUS_COLOR = new Color(0x8080ff);

    private int SHADOWX = 1;
    private int SHADOWY = 1;
    private float DRAG_SPEED;
    private float CLICK_SPEED;
    private int size;
    private int middle;

    public final static int SIMPLE = 1;
    public final static int ROUND  = 2;
    public final static int SIMPLE_MOUSE_DIRECTION = 3;
    private int dragType = ROUND;

    public final static int DEFAULT_WIDTH = 60;
    public final static int DEFAULT_HEIGHT = 45;
    private final static Dimension MIN_SIZE = new Dimension(40, 40);
    private final static Dimension PREF_SIZE = new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);

    private final static float MID_OFFSET = (float)1./220;

    // Set the antialiasing to get the right look!
    private final static RenderingHints AALIAS =
    new RenderingHints(RenderingHints.KEY_ANTIALIASING,
		       RenderingHints.VALUE_ANTIALIAS_ON);

    private ChangeEvent changeEvent = null;
    private EventListenerList listenerList = new EventListenerList();

    private Arc2D hitArc = new Arc2D.Float(Arc2D.PIE);

    private float ang = START_ANG;
    private float val;
    private int dragpos = -1;
    private float startVal;
    private Color focusColor;
    private double lastAng;

    private int mWidth;
    private int mHeight;

    /** value displayed if not null */
    private String mValueAsString;

    /**
       Knob with default size.
    */
    public DKnob() {
	this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public DKnob(int aWidth, int aHeight) {
	mWidth = aWidth;
	mHeight = aHeight;
	DRAG_SPEED = 0.0075F;//0.01F;
	CLICK_SPEED = 0.01F;
	SHADOWX = 1;
	SHADOWY = 1;

	focusColor = DEFAULT_FOCUS_COLOR;

	setPreferredSize(PREF_SIZE);
	hitArc.setAngleStart(235); // Degrees ??? Radians???
	addMouseListener(new MouseAdapter() {
		public void mousePressed(MouseEvent me) {
		    dragpos = me.getX() + me.getY();
		    startVal = val;

		    // Fix last angle
		    int xpos = middle - me.getX();
		    int ypos = middle - me.getY();
		    lastAng = Math.atan2(xpos, ypos);

		    requestFocus();
		}

		public void mouseClicked(MouseEvent me) {
		    hitArc.setAngleExtent(-(LENGTH + 20));
		    if  (hitArc.contains(me.getX(), me.getY())) {
			hitArc.setAngleExtent(MULTIP * (ang-START_ANG)-10);
			if  (hitArc.contains(me.getX(), me.getY())) {
			    decValue();
			} else incValue();
		    }
		}
	    });

	// Let the user control the knob with the mouse
	addMouseMotionListener(new MouseMotionAdapter() {
		public void mouseDragged(MouseEvent me) {
            float speed = DRAG_SPEED;
            if((me.getModifiersEx() & (InputEvent.BUTTON2_DOWN_MASK | InputEvent.BUTTON3_DOWN_MASK)) != 0)
                speed /= 10;
            if((me.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0)
                speed /= 10;
            if((me.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0)
                speed /= 10;
            
		    if ( dragType == SIMPLE) {
			float f = speed * ((me.getX() + me.getY()) - dragpos);
			setValue(startVal + f);
		    }
		    else if (dragType == SIMPLE_MOUSE_DIRECTION) {
			float f = (speed * (me.getX() + me.getY() - dragpos));
			setValue(startVal - f);
		    }
		    else if ( dragType == ROUND) {
			// Measure relative the middle of the button!
			int xpos = middle - me.getX();
			int ypos = middle - me.getY();
			double ang = Math.atan2(xpos, ypos);
			double diff = lastAng - ang;
			setValue((float) (getValue() + (diff / LENGTH_ANG)));

			lastAng = ang;
		    }
		}

		public void mouseMoved(MouseEvent me) {
		}
	    });

	// Let the user control the knob with the keyboard
	addKeyListener(new KeyListener() {

		public void keyTyped(KeyEvent e) {}
		public void keyReleased(KeyEvent e) {}
		public void keyPressed(KeyEvent e) {
		    int k = e.getKeyCode();
		    if (k == KeyEvent.VK_RIGHT)
			incValue();
		    else if (k == KeyEvent.VK_LEFT)
			decValue();
		}
	    });

	// Handle focus so that the knob gets the correct focus highlighting.
	addFocusListener(new FocusListener() {
		public void focusGained(FocusEvent e) {
		    repaint();
		}
		public void focusLost(FocusEvent e) {
		    mValueAsString = null;
		    repaint();
		}
	    });
    }

    public void setDragType(int type) {
	dragType = type;
    }
    public int getDragType() {
	return dragType;
    }

    // comment out to make focus policy consistent with other Components.
    // Sep. 26, 2004, Hiroo Hayashi
//    public boolean isManagingFocus() {
//	return true;
//    }

    public boolean isFocusable() {
	return true;
    }

    private void incValue() {
	setValue(val + CLICK_SPEED);
    }

    private void decValue() {
	setValue(val - CLICK_SPEED);
    }

    public float getValue() {
	return val;
    }

    public void setValue(float val) {
	if (val < 0) val = 0;
	if (val > 1) val = 1;
	this.val = val;
	ang = START_ANG - LENGTH_ANG * val;
	repaint();
	fireChangeEvent();
    }

    /**
     * Set the value as string (for display if not null)
     */
    public void setValueAsString(String aStrValue) {
	mValueAsString = aStrValue;
    }

    public void addChangeListener(ChangeListener cl) {
	listenerList.add(ChangeListener.class, cl);
    }

    public void removeChangeListener(ChangeListener cl) {
	listenerList.remove(ChangeListener.class, cl);
    }

    public Dimension getMinimumSize() {
	return MIN_SIZE;
    }

    protected void fireChangeEvent() {
	// Guaranteed to return a non-null array
	Object[] listeners = listenerList.getListenerList();
	// Process the listeners last to first, notifying
	// those that are interested in this event
	for (int i = listeners.length-2; i>=0; i-=2) {
	    if (listeners[i] == ChangeListener.class) {
		// Lazily create the event:
		if (changeEvent == null)
		    changeEvent = new ChangeEvent(this);
		((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
	    }
	}
    }

    /**
     * Paint the DKnob
     */
    public void paint(Graphics g) {
	if (isEnabled()) {
	    // denis: i set the size because I can't deal with layout and resize consequences
	    int width = mWidth;//getWidth();
	    int height = mHeight;//getHeight();
	    //		size = Math.min(width, height) - 22;
	    // the drawing is wider than it is tall
	    size = width - 22;
	    int oSizeDiv2 = size/2;
	    int oOffset = 5;//10
	    middle = oOffset + oSizeDiv2;

	    if (g instanceof Graphics2D) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setBackground(getParent().getBackground());
		g2d.addRenderingHints(AALIAS);

		// For the size of the "mouse click" area
		hitArc.setFrame(4, 4, size+12, size+12);
	    }

	    // Paint the "markers"
	    for (float a2 = START_ANG; a2 >= START_ANG - LENGTH_ANG; a2 = a2 - LENGTH_ANG_DIV10) {
		int x = oOffset + oSizeDiv2 + (int)((6+oSizeDiv2) * Math.cos(a2));
		int y = oOffset + oSizeDiv2 - (int)((6+oSizeDiv2) * Math.sin(a2));
		g.drawLine(oOffset + oSizeDiv2, oOffset + oSizeDiv2, x, y);
	    }

	    // Paint focus if in focus
	    if (hasFocus()) {
		g.setColor(focusColor);
	    } else {
		g.setColor(Color.white);
	    }

	    g.fillOval(oOffset, oOffset, size, size);
	    g.setColor(Color.gray);
	    g.fillOval(oOffset + 4 + SHADOWX, oOffset + 4 + SHADOWY, size-8, size-8);

	    g.setColor(Color.black);
	    g.drawArc(oOffset, oOffset, size, size, 315, 270);
	    g.fillOval(oOffset + 4, oOffset + 4, size-8, size-8);
	    g.setColor(Color.white);

        // center of knob is at (oOffset + oSizeDiv2, oOffset + oSizeDiv2)
        // make the line 2 pixels wide
	    double oCos = Math.cos(ang);
	    double oSin = Math.sin(ang);
        int dx = (int)(2 * oSin);
        int dy = (int)(2 * oCos);
        // compute 'right-hand' vertex of needle at center
	    int x = oOffset + oSizeDiv2 + (int)(oSizeDiv2 * oCos);
	    int y = oOffset + oSizeDiv2 - (int)(oSizeDiv2 * oSin);
/*        
        // Draw pointy needle
	    g.drawLine(oOffset + oSizeDiv2, oOffset + oSizeDiv2, x, y);
	    g.setColor(Color.gray);
	    int s2 = Math.max(size / 6, 6);
	    g.drawOval(oOffset + s2, oOffset + s2, size - s2*2, size - s2*2);
	    g.drawLine(oOffset + dx + oSizeDiv2, oOffset + dy + oSizeDiv2, x, y);
	    g.drawLine(oOffset - dx + oSizeDiv2, oOffset - dy + oSizeDiv2, x, y);
*/
        
	    // denis: i prefer rectangular look for indicator needle
        int s2 = Math.max(size / 6, 6);
        g.setColor(Color.gray);
        g.drawOval(oOffset + s2, oOffset + s2, size - s2*2-1, size - s2*2-1);
        
	    int xPoints[] = new int[] {oOffset + dx + oSizeDiv2, x + dx, x - dx, oOffset - dx + oSizeDiv2};
	    int yPoints[] = new int[] {oOffset + dy + oSizeDiv2, y + dy, y - dy, oOffset - dy + oSizeDiv2};
	    // denis: hightlight dead center
	    if (val > (0.5 - MID_OFFSET) && val < (0.5 + MID_OFFSET))
	        g.setColor(Color.white);
	    else
	        g.setColor(Color.lightGray);
	    g.fillPolygon(xPoints, yPoints, 4);
        // round tail of needle
	    g.fillOval(oOffset + oSizeDiv2 - 1, oOffset + oSizeDiv2 - 1, 2, 2);
        
	    // denis: display value
	    if (mValueAsString != null) {
    		g.setColor(Color.white);
    		g.drawString(mValueAsString, oSizeDiv2-5, height-10);
	    }
	}
    }
}
