package core;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

/**
 * @version $Id$
 */
public class EnvelopeWidget extends SysexWidget {
    /** Array of EnvelopNodes provied by constructor */
    private EnvelopeNode[] nodes;
    /** Array of JLabel widgets which show the names of the X/Y axis
     * parameters riding each access.*/
    private JLabel[] valueLabels;
    /** Array of JTextField which show the X/Y axis value. */
    private JTextField[] values;
    /** A number of valid paramters (values). */
    private int numValues;
    /** EnvelopeCanvas instance */
    private EnvelopeCanvas envelopeCanvas;
    /** x axis padding used for insets */
    private int xpadding;
    /** y axis padding used for insets */
    private int ypadding;

    /**
     * Creates a new <code>SysexWidget</code> instance.
     *
     * @param l a label text for the sysexWidget.
     * @param p a <code>Patch</code>, which is edited.
     * @param n an array of EnvelopeNode.
     * @see SysexWidget
     * @see EnvelopeNode
     */
    public EnvelopeWidget(String l, Patch p, EnvelopeNode[] n) {
	this(l, p, n, 0, 0);
    }

    public EnvelopeWidget(String l, Patch p, EnvelopeNode[] n,
			  int xpad, int ypad) {
	super(l, p, null, null);
	nodes = n;
	xpadding=xpad;
	ypadding=ypad;

	createWidgets();
        layoutWidgets();
    }

    protected void createWidgets() {
        valueLabels = new JLabel[nodes.length * 2];
        values = new JTextField[nodes.length * 2];
        int j = 0;
	// Why don't we simply define valueLabelsX/Y, valuesX/Y? Hiroo
        for (int i = 0; i < nodes.length; i++) {
	    /*
	     * Using <code>null</code>s for the Models and Senders and
	     * setting min to max means that a node is stationary on
	     * that axis and has no related parameter.<p>
	     */
	    if (nodes[i].minX != nodes[i].maxX) {
		valueLabels[j] = new JLabel(nodes[i].nameX);
		values[j] = new JTextField(new Integer(nodes[i].ofsX.get()).toString(), 4);
		values[j].setEditable(false);
		j++;
            }
	    if (nodes[i].minY != nodes[i].maxY) {
		valueLabels[j] = new JLabel(nodes[i].nameY);
		values[j] = new JTextField(new Integer(nodes[i].ofsY.get()).toString(), 4);
		values[j].setEditable(false);
		j++;
            }
        }
        numValues = j;
        setNumFaders(numValues); // numFaders == numValues?

	envelopeCanvas = new EnvelopeCanvas(nodes, values);
    }

    protected void layoutWidgets() {
        setLayout(new BorderLayout());

        JPanel valuePane = new JPanel();
        valuePane.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = gbc.BOTH;
        gbc.anchor = gbc.EAST;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(ypadding, xpadding, ypadding, xpadding);

        for (int j = 0; j < numValues; j++) {
	    // name of he X/Y axis paramters
	    gbc.gridx = 0;
	    gbc.gridy = j;
	    gbc.gridwidth = 1;
	    gbc.gridheight = 1;
	    valuePane.add(valueLabels[j], gbc);
	    // X/Y axis value
	    gbc.gridx = 1;
	    gbc.gridy = j;
	    gbc.gridwidth = 1;
	    gbc.gridheight = 1;
	    valuePane.add(values[j], gbc);
	}
        add(valuePane, BorderLayout.EAST);
        add(getJLabel(), BorderLayout.NORTH);
        add(envelopeCanvas, BorderLayout.CENTER);
    }

    public void setSliderNum(int num) {
        _setSliderNum(num);
        setToolTipText("Bank " + ((num - 1) / 16)
		       + "  Sliders " + (((num - 1) % 16) + 1)
		       + " to " + (((num - 1) % 16) + getNumFaders()));
// Should be???	       + " to " + (((num - 1 + numValues - 1) % 16) + 1));
        for (int j = 0; j < numValues; j++) {
	    String t = ("Bank " + ((num - 1) / 16)
			+ "  Slider " + (((num - 1 + j) % 16) + 1));
	    values[j].setToolTipText(t);
	    valueLabels[j].setToolTipText(t);
	}
    }

    /**
     * Set value from fader and send System Exclusive message to a
     * MIDI port.<p>
     * Called by PatchEditorFrame.faderMoved(byte, byte).
     * This method is used and must be extended by a SysexWidget with
     * multiple prameters. (i.e. numFaders != 1, only EnvelopeWidget now)
     *
     * @param fader fader number.
     * @param value value to be set. [0-127]
     */
    // Should the Widget state be updated?
    protected void setFaderValue(int fader, int value) {
        fader -= getSliderNum(); // set the 1st fader to '0'.
        int j = 0;
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].minX != nodes[i].maxX) {
		if (fader == j) {
		    value = (int) (nodes[i].minX
				   + ((float) (value) / 127 * (nodes[i].maxX - nodes[i].minX)));
		    // set paramModel and show the value.
		    nodes[i].ofsX.set(value);
		    values[j].setText(new Integer(value).toString());
		    // send System Exclusive Message
		    sendSysex(nodes[i].senderX, value);
		    break;	// hiroo
                }
                j++;
            }
            if (nodes[i].minY != nodes[i].maxY) {
		if (fader == j) {
		    value = (int) (nodes[i].minY
				   + ((float) (value) / 127 * (nodes[i].maxY - nodes[i].minY)));
		    // set paramModel and show the value.
		    nodes[i].ofsY.set(value);
		    values[j].setText(new Integer(value).toString());
		    // send System Exclusive Message
		    sendSysex(nodes[i].senderY, value);
		    break;	// hiroo
                }
                j++;
            }
        }
    }

    public void setValue() {
	// Does this work?
	//envelopeCanvas.repaint();
    }

    /** not implemented yet */
    public void setEnabled(boolean e) {
	// Does this work?
	//envelopeCanvas.setEnabled(e);
    }

    final class EnvelopeCanvas extends JPanel {
        private EnvelopeNode[] nodes;
        private JTextField[] values;
	/** X coordinates on the canvas. */
        private int[] nodeX;
	/** Y coordinates on the canvas. */
        private int[] nodeY;
	/** width of abscissa. */
	private int width;
	/** height of ordinate. */
	private int height;

	private static final int DELTA = 5;

        private EnvelopeCanvas(EnvelopeNode[] e, JTextField[] f) {
	    super();
	    values = f;
	    nodes = e;
	    nodeX = new int[nodes.length];
	    nodeY = new int[nodes.length];
	    setMinimumSize(new Dimension(300, 50));
	    setPreferredSize(getMinimumSize());
	    MyListener myListener = new MyListener();
	    addMouseListener(myListener);
	    addMouseMotionListener(myListener);

	    // calculate width and height
	    width = 0;
	    height = 0;
            for (int i = 0; i < nodes.length; i++) {
		width += nodes[i].maxX;
		if ((nodes[i].maxY + nodes[i].baseY > height)
		    && nodes[i].minY != EnvelopeNode.SAME
		    && nodes[i].maxY != EnvelopeNode.SAME)
		    height = nodes[i].maxY + nodes[i].baseY;
	    }
        }

        /**
	 * Update Envelope Canvas.
	 *
	 * @param g a <code>Graphics</code> value.
	 */
	// How is this called?  I see only 'paintComponent(null)' in
	// this class.  Does repaint() call this indirectly? Hiroo
	protected void paintComponent(Graphics g) {
            if (g != null)
		super.paintComponent(g);
            Insets insets = getInsets();

	    // scale by using actual canvas size
            int currentWidth = getWidth() - insets.left - insets.right - DELTA * 2;
            int currentHeight = getHeight() - insets.top - insets.bottom - DELTA;
            float scaleX = (float) currentWidth / (float) width;
            float scaleY = (float) (currentHeight - DELTA) / (float) height;

            int sumX = insets.left + DELTA;
            for (int i = 0; i < nodes.length; i++) {
		int x = getX(i);
		int y = getY(i);

		sumX += x;
		// calculate coordinates
		nodeX[i] = (int) (sumX * scaleX);
		nodeY[i] = currentHeight - (int) (y * scaleY);
		// draw a rectangle and a line
		if (g != null) {
		    g.fillRect(nodeX[i] - 3, nodeY[i] - 3, 6, 6);
		    if (i > 0)
			g.drawLine(nodeX[i], nodeY[i],
				   nodeX[i - 1], nodeY[i - 1]);
		}
            }
        }

	/** Return X axis value of <code>node[i]</code>. */
        private int getX(int i) {
	    if (nodes[i].minX == nodes[i].maxX)
		return nodes[i].minX;
	    if (nodes[i].invertX)
		return (nodes[i].maxX - nodes[i].ofsX.get());
	    return (nodes[i].ofsX.get());
	    // I think the following is the correct code. We need to
	    // consider min==max && invertX case, don't we?  Hiroo.
	    /*
	    int v = (nodes[i].minX == nodes[i].maxX
		     ? nodes[i].minX : nodes[i].ofsX.get());
	    return nodes[i].invertX ? nodes[i].maxX - v : v;
	    */
        }

	/*
	 * Using <code>EnvelopeNode.SAME</code> for <code>miny</code>
	 * or <code>maxy</code> means that the height remains at
	 * whatever the previous node was at.
	 */
	/** Return Y axis value of <code>node[i]</code>. */
        private int getY(int i) {
	    if (nodes[i].minY == EnvelopeNode.SAME
		|| nodes[i].maxY == EnvelopeNode.SAME)
		return getY(i - 1);
	    if (nodes[i].minY == nodes[i].maxY)
		return nodes[i].minY + nodes[i].baseY;
	    return (nodes[i].ofsY.get() + nodes[i].baseY);
        }

        private class MyListener extends MouseInputAdapter {
	    /** dragging node number */
            private int dragNodeIdx;
	    private EnvelopeNode dragNode;

	    // set dragNode
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
		// select the first close node.
		// If none was selected, the last node is selected.
                for (int i = 0; i < nodes.length; i++)
                    if (((Math.abs(x - nodeX[i])) < DELTA)
			&& ((Math.abs(y - nodeY[i]) < DELTA))) {
			dragNodeIdx = i;
			dragNode = nodes[i];
			break;	// hiroo
		    }
                repaint();
            }

            private int oldx;
	    private int oldy;

            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if ((x == oldx) && (y == oldy))
		    return;
		// move the selected node one dot by one dot. (Why? Hiroo)
                if (x - oldx > 0) { // X+
		    while ((x - nodeX[dragNodeIdx] > DELTA)
			   && (getX(dragNodeIdx) < dragNode.maxX)) {
			if (dragNode.invertX)
			    dragNode.ofsX.set(dragNode.ofsX.get() - 1);
			else
			    dragNode.ofsX.set(dragNode.ofsX.get() + 1);
			paintComponent(null);
		    }
		} else if (x - oldx < 0) {	// X-
		    while ((x - nodeX[dragNodeIdx] < -DELTA)
			   && (getX(dragNodeIdx) > dragNode.minX)) {
			if (dragNode.invertX)
			    dragNode.ofsX.set(dragNode.ofsX.get() + 1);
			else
			    dragNode.ofsX.set(dragNode.ofsX.get() - 1);
			paintComponent(null);
		    }
		}
                if (y - oldy < 0) { // Y-
		    while ((y - nodeY[dragNodeIdx] < -DELTA)
			   && (getY(dragNodeIdx) < dragNode.maxY + dragNode.baseY)) {
			dragNode.ofsY.set(dragNode.ofsY.get() + 1);
			paintComponent(null);
		    }
		} else if (y - oldy > 0) { // Y+
		    while ((y - nodeY[dragNodeIdx] > DELTA)
			   && (getY(dragNodeIdx) > dragNode.minY + dragNode.baseY)) {
			dragNode.ofsY.set(dragNode.ofsY.get() - 1);
			paintComponent(null);
		    }
		}
		oldx = x;
		oldy = y;

		// update JTextField
                int j = 0;
                for (int i = 0; i < nodes.length; i++) {
		    if (nodes[i].minX != nodes[i].maxX) {
			if (i == dragNodeIdx) {
			    values[j].setText(new Integer(nodes[i].ofsX.get()).toString());
			    //break; // hiroo
			    //bk: this break here is wrong, code below might
			    //improperly not execute.
			}
			j++;
		    }
		    if (nodes[i].minY != nodes[i].maxY) {
			if (i == dragNodeIdx) {
			    values[j].setText(new Integer(nodes[i].ofsY.get()).toString());
			    break; // hiroo
			}
			j++;
		    }
		}
                repaint();
	    }

            public void mouseReleased(MouseEvent e) {
		// Send System Exclusive message
		if (dragNode.ofsX != null)
		    sendSysex(dragNode.senderX, dragNode.ofsX.get());
		if (dragNode.ofsY != null)
		    sendSysex(dragNode.senderY, dragNode.ofsY.get());
	    }
	}
    }
}
