package core;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;

/**
 * A SysexWidget implements an envelope editing function.<p>
 *
 * Compatibility Note: <code>EnvelopeNode</code> class was replaced by
 * <code>EnvelopWidget.Node</code> class.
 *
 * @version $Id$
 * @see Node
 * @see SysexWidget
 */
public class EnvelopeWidget extends SysexWidget {
    /** Array of EnvelopNodes provied by constructor */
    protected Node[] nodes;
    /**
     * Array of JLabel widgets which show the names of the X/Y axis
     * parameters riding each access.
     */
    protected JLabel[] valueLabels;
    /** Array of JTextField which show the X/Y axis value. */
    protected JTextField[] values;
    /** A number of valid paramters (values). */
    protected int numValues;
    /** EnvelopeCanvas instance */
    protected EnvelopeCanvas envelopeCanvas;
    /** x axis padding used for insets */
    private int xpadding;
    /** y axis padding used for insets */
    private int ypadding;

    /**
     * Creates a new <code>SysexWidget</code> instance.
     *
     * @param label a label text for the sysexWidget.
     * @param patch a <code>Patch</code>, which is edited.
     * @param nodes an array of Node.
     * @param xpad horizontal padding value.
     * @param ypad vertical padding value.
     */
    public EnvelopeWidget(String label, IPatch patch, Node[] nodes,
			  int xpad, int ypad) {
	super(label, patch, null, null);
	this.nodes = nodes;
	xpadding = xpad;
	ypadding = ypad;

	createWidgets();
        layoutWidgets();
    }

    /** <code>xpad</code> and <code>ypad</code> are set to zero. */
    public EnvelopeWidget(String label, IPatch patch, Node[] options) {
	this(label, patch, options, 0, 0);
    }

    protected void createWidgets() {
        valueLabels = new JLabel[nodes.length * 2];
        values = new JTextField[nodes.length * 2];
        int j = 0;
        for (int i = 0; i < nodes.length; i++) {
	    // Using <code>null</code>s for the Models and Senders and
	    // setting min to max means that a node is stationary on
	    // that axis and has no related parameter.
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
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.EAST;
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

	envelopeCanvas.setMinimumSize(new Dimension(300, 50));
	envelopeCanvas.setPreferredSize(getMinimumSize());

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
     * Set specified by <code>fader</code> and send System Exclusive
     * message to a MIDI port.<p>
     * Called by PatchEditorFrame.faderMoved(byte, byte).
     * This method is used and must be extended by a SysexWidget with
     * multiple prameters (i.e. numFaders != 1, only EnvelopeWidget now).
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
		    break;	// added by hiroo
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
		    break;	// added by hiroo
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

    /** Actual canvas for the envelop lines. */
    private final class EnvelopeCanvas extends JPanel {
        private Node[] nodes;
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

        private EnvelopeCanvas(Node[] e, JTextField[] f) {
	    super();
	    values = f;
	    nodes = e;
	    nodeX = new int[nodes.length];
	    nodeY = new int[nodes.length];
	    //setMinimumSize(new Dimension(300, 50));
	    //setPreferredSize(getMinimumSize());
	    MyListener myListener = new MyListener();
	    addMouseListener(myListener);
	    addMouseMotionListener(myListener);

	    // calculate width and height
	    width = 0;
	    height = 0;
            for (int i = 0; i < nodes.length; i++) {
		width += nodes[i].maxX;
		if ((nodes[i].maxY + nodes[i].baseY > height)
		    && nodes[i].minY != Node.SAME
		    && nodes[i].maxY != Node.SAME)
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
            Insets insets = getInsets(); // What's this? Hiroo

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
	 * Using <code>Node.SAME</code> for <code>miny</code>
	 * or <code>maxy</code> means that the height remains at
	 * whatever the previous node was at.
	 */
	/** Return Y axis value of <code>node[i]</code>. */
        private int getY(int i) {
	    if (nodes[i].minY == Node.SAME
		|| nodes[i].maxY == Node.SAME)
		return getY(i - 1);
	    if (nodes[i].minY == nodes[i].maxY)
		return nodes[i].minY + nodes[i].baseY;
	    return (nodes[i].ofsY.get() + nodes[i].baseY);
        }

        private class MyListener extends MouseInputAdapter {
	    /** dragging node number */
            private int dragNodeIdx;
	    private Node dragNode;

            private int oldx;
	    private int oldy;

	    // set dragNode
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
		// select the first close node.
                for (int i = 0; i < nodes.length; i++)
                    if (((Math.abs(x - nodeX[i])) < DELTA)
			&& ((Math.abs(y - nodeY[i]) < DELTA))) {
			dragNodeIdx = i;
			dragNode = nodes[i];
			oldx = nodeX[i];
			oldy = nodeY[i];
			repaint();
			return;
		    }
		// not found
		dragNode = null;
            }

            public void mouseDragged(MouseEvent e) {
		if (dragNode == null)
		    return;
                int x = e.getX();
                int y = e.getY();
                if ((x == oldx) && (y == oldy))
		    return;
		// move the selected node one dot by one dot. (Why? Hiroo)
                if (x - oldx > 0) { // X+
		    while ((x - nodeX[dragNodeIdx] > DELTA)
			   && (getX(dragNodeIdx) < dragNode.maxX)
			   && (dragNode.ofsX != null)) {
			if (dragNode.invertX)
			    dragNode.ofsX.set(dragNode.ofsX.get() - 1);
			else
			    dragNode.ofsX.set(dragNode.ofsX.get() + 1);
			paintComponent(null);
		    }
		} else if (x - oldx < 0) {	// X-
		    while ((x - nodeX[dragNodeIdx] < -DELTA)
			   && (getX(dragNodeIdx) > dragNode.minX)
			   && (dragNode.ofsX != null)) {
			if (dragNode.invertX)
			    dragNode.ofsX.set(dragNode.ofsX.get() + 1);
			else
			    dragNode.ofsX.set(dragNode.ofsX.get() - 1);
			paintComponent(null);
		    }
		}
                if (y - oldy < 0) { // Y-
		    while ((y - nodeY[dragNodeIdx] < -DELTA)
			   && (getY(dragNodeIdx) < dragNode.maxY + dragNode.baseY)
			   && (dragNode.ofsY != null)) {
			dragNode.ofsY.set(dragNode.ofsY.get() + 1);
			paintComponent(null);
		    }
		} else if (y - oldy > 0) { // Y+
		    while ((y - nodeY[dragNodeIdx] > DELTA)
			   && (getY(dragNodeIdx) > dragNode.minY + dragNode.baseY)
			   && (dragNode.ofsY != null)) {
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
			    //break; // added by hiroo
			    //bk: this break here is wrong, code below might
			    //improperly not execute.
			}
			j++;
		    }
		    if (nodes[i].minY != nodes[i].maxY) {
			if (i == dragNodeIdx) {
			    values[j].setText(new Integer(nodes[i].ofsY.get()).toString());
			    break; // added by hiroo
			}
			j++;
		    }
		}
                repaint();
	    }

            public void mouseReleased(MouseEvent e) {
		if (dragNode == null)
		    return;
		// Send System Exclusive message
		if (dragNode.ofsX != null)
		    sendSysex(dragNode.senderX, dragNode.ofsX.get());
		if (dragNode.ofsY != null)
		    sendSysex(dragNode.senderY, dragNode.ofsY.get());
	    }
	}
    }

    /**
     * A data type used by EnvelopeWidget which stores information about a
     * single node (point) in the Widget.<p>
     *
     * Each <code>Node</code> is one of the movable squares on the
     * envelope. Some of these nodes are stationary, some contain two
     * parameters on the synth and can be moved vertically and
     * horizontally, and others contain only one parameter and can
     * therefore be moved in only one direction.
     * @see EnvelopeWidget
     */
    public static class Node {
	/**
	 * Using <code>Node.SAME</code> for <code>miny</code> or
	 * <code>maxy</code> means that the height remains at whatever
	 * the previous node was at.
	 */
	public static final int SAME = 5000;

	private int minX;
	private int minY;
	private int maxX;
	private int maxY;
	private IParamModel ofsX;
	private IParamModel ofsY;
	private ISender senderX;
	private ISender senderY;
	private boolean invertX;
	private int baseY;
	private String nameX;
	private String nameY;

	/**
	 * Construcutor for a <code>Node</code>.<p>
	 *
	 * Using <code>null</code>s for the Models and Senders and setting
	 * min to max means that a node is stationary on that axis and has
	 * no related parameter.<p>
	 *
	 * Using <code>Node.SAME</code> for <code>miny</code> or
	 * <code>maxy</code> means that the height remains at whatever
	 * the previous node was at.
	 *
	 * @param minx The minimum value permitted by the synth
	 * parameter which rides the X axis of the node.
	 * @param maxx The maximum value permitted by the synth
	 * parameter which rides the X axis of the node.
	 * @param ofsx The Parameter Model which provides reading/writing
	 * abilities to the sysex data representing the parameter.
	 *
	 * @param miny The minimum value permitted by the synth
	 * parameter which rides the Y axis of the node.
	 * @param maxy The maximum value permitted by the synth
	 * parameter which rides the Y axis of the node.
	 * @param ofsy The Parameter Model which provides reading/writing
	 * abilities to the sysex data representing the parameter.
	 *
	 * @param basey The value will be added to all Y values.  This
	 * doesn't change the function of the EnvelopeWidget, but makes it
	 * look nicer and possibly be more intuitive to use.  Sometimes
	 * you don't want zero on a Y-axis-riding-parameter to be all the
	 * way down at the bottom. This gives it a little bit of rise.
	 *
	 * @param invertx Sometimes on an X-axis-riding attribute 0 is the
	 * fastest, other times it is the slowest. This allows you to choose.
	 *
	 * @param x The ISender which send system exclusive messages
	 * to the synths when the Node is moved on the X axis direction.
	 * @param y The ISender which send system exclusive messages
	 * to the synths when the Node is moved on the Y axis direction.
	 *
	 * @param namex The names of the X-axis parameters riding each
	 * access.
	 * @param namey The names of the Y-axis parameters riding each
	 * access.
	 *
	 * @see EnvelopeWidget
	 */
	public Node(int minx, int maxx, IParamModel ofsx,
		    int miny, int maxy, IParamModel ofsy,
		    int basey, boolean invertx,
		    ISender x, ISender y,
		    String namex, String namey) {
	    baseY = basey;
	    minX = minx;
	    maxX = maxx;
	    minY = miny;
	    maxY = maxy;
	    ofsX = ofsx;
	    ofsY = ofsy;
	    senderX = x;
	    senderY = y;
	    nameX = namex;
	    nameY = namey;
	    invertX = invertx;
	}
    }
}
