package core;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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
    /** Array of Nodes provided by constructor */
    protected Node[] nodes;
    /** Array of Params (parameter/variable). */
    protected Param[] params;

    /** EnvelopeCanvas instance */
    protected EnvelopeCanvas envelopeCanvas;
    /** x axis insets (space at the right and left border) */
    protected int xpadding;
    /** y axis insets (space at the top and bottom border) */
    protected int ypadding;

    /**
     * Creates a new <code>SysexWidget</code> instance.
     *
     * @param label a label text for the sysexWidget.
     * @param patch a <code>Patch</code>, which is edited.
     * @param nodes an array of Node.
     * @param xpadding space at the right and left border.
     * @param ypadding space at the top and bottom border.
     */
    public EnvelopeWidget(String label, IPatch patch, Node[] nodes,
			  int xpadding, int ypadding) {
	super(label, patch, null, null);
	this.nodes = nodes;
	this.xpadding = xpadding;
	this.ypadding = ypadding;

	createWidgets();
        layoutWidgets();
    }

    /** <code>xpad</code> and <code>ypad</code> are set to zero. */
    public EnvelopeWidget(String label, IPatch patch, Node[] options) {
	this(label, patch, options, 0, 0);
    }

    protected void createWidgets() {
        // count the number of parameters (faders)
        int j = 0;
        for (int i = 0; i < nodes.length; i++) {
	    if (nodes[i].variableX)
		j++;
	    if (nodes[i].variableY)
		j++;
        }
        setNumFaders(j);
        params = new Param[j];

        j = 0;
        for (int i = 0; i < nodes.length; i++) {
	    if (nodes[i].variableX) {
	        params[j] = new Param(new JLabel(nodes[i].nameX), 
	                new JTextField(String.valueOf(nodes[i].pmodelX.get()), 4), i, true);
	        params[j].textField.setEditable(false);
		nodes[i].faderNumX = j;
		j++;
            }
	    if (nodes[i].variableY) {
	        params[j] = new Param(new JLabel(nodes[i].nameY), 
	                new JTextField(String.valueOf(nodes[i].pmodelY.get()), 4), i, false);
		params[j].textField.setEditable(false);
		nodes[i].faderNumY = j;
		j++;
            }
        }

	envelopeCanvas = new EnvelopeCanvas();
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

        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        for (int j = 0; j < params.length; j++) {
	    gbc.gridy = j;
	    // name of he X/Y axis parameters
	    gbc.gridx = 0;
	    valuePane.add(params[j].label, gbc);
	    // X/Y axis value
	    gbc.gridx = 1;
	    valuePane.add(params[j].textField, gbc);
	}

	envelopeCanvas.setMinimumSize(new Dimension(300, 50));
	envelopeCanvas.setPreferredSize(getMinimumSize());

        add(valuePane, BorderLayout.EAST);
        add(getJLabel(), BorderLayout.NORTH);
        add(envelopeCanvas, BorderLayout.CENTER);
    }

    public void setSliderNum(int num) {
        _setSliderNum(num);
        setToolTipText("Bank " + ((num - 1) / 16 + 1)
		       + "  Slider " + ((num - 1) % 16 + 1)
		       + "  to  Bank " + ((num + params.length - 2) / 16 + 1)
		       + "  Slider " + ((num + params.length - 2) % 16 + 1));

        for (int j = 0; j < params.length; j++) {
	    String t = ("Bank " + ((num - 1) / 16 + 1)
			+ "  Slider " + ((num + j - 1) % 16 + 1));
	    params[j].label.setToolTipText(t);
	    params[j].textField.setToolTipText(t);
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
    protected void setFaderValue(int fader, int value) {
        fader -= getSliderNum(); // set the 1st fader to '0'.

        Node node = nodes[params[fader].node];
        if (params[fader].isX) {
            value = (int) (node.minX + ((float) (value) / 127 * (node.maxX - node.minX)));
            // set paramModel and show the value.
            node.pmodelX.set(value);
            // send System Exclusive Message
            sendSysex(node.senderX, value);
        } else {
            value = (int) (node.minY + ((float) (value) / 127 * (node.maxY - node.minY)));
            // set paramModel and show the value.
            node.pmodelY.set(value);
            // send System Exclusive Message
            sendSysex(node.senderY, value);
        }
        params[fader].textField.setText(String.valueOf(value));
        // update canvas
        envelopeCanvas.repaint();
    }

    public void setValue() {
	envelopeCanvas.repaint();
    }

    public void setEnabled(boolean e) {
	envelopeCanvas.setEnabled(e);
    }

    /** Actual canvas for the envelope lines. */
    private final class EnvelopeCanvas extends JPanel {
        private float scaleX;
	private float scaleY;
	private int xorigin;
	private int canvasHeight;
	
	private boolean enabled = true;  // for setEnabled()
	private static final int DELTA = 6;

        private EnvelopeCanvas() {
	    super();

	    MyListener myListener = new MyListener();
	    addMouseListener(myListener);
	    addMouseMotionListener(myListener);

	    // calculate maximum total x/y value
	    int x = 0;
	    int y = 0;
            for (int i = 0; i < nodes.length; i++) {
		x += nodes[i].maxX;
		if ((nodes[i].maxY + nodes[i].baseY > y)
		    && nodes[i].minY != Node.SAME)
		    y = nodes[i].maxY + nodes[i].baseY;
	    }
	    final int maxX = x;
	    final int maxY = y;

	    // recalculate canvas size dependent parameters every time size is
	    // changed including the time the canvas is created.
	    addComponentListener(new ComponentListener() {
                public void componentResized(ComponentEvent e) {
                    // scale by using actual canvas size
                    Insets insets = getInsets();
                    int canvasWidth = getWidth() - insets.left - insets.right - DELTA * 2;
                    canvasHeight = getHeight() - insets.top - insets.bottom - DELTA;
                    scaleX = (float) canvasWidth / (float) maxX;
                    scaleY = (float) (canvasHeight - DELTA) / (float) maxY;
                    xorigin = insets.left + DELTA;

                    int sumX = 0;
                    for (int i = 0; i < nodes.length; i++) {
                        // calculate coordinates on the canvas
        		sumX += getX(i);
        		nodes[i].posX = xPos(sumX);
        		nodes[i].posY = yPos(getY(i));
                    }
                }
                public void componentMoved(ComponentEvent e) {}
                public void componentShown(ComponentEvent e) {}
                public void componentHidden(ComponentEvent e) {}
            });
        }

        /** Convert X value to X position in the canvas. */
        int xPos(int value) {
            return xorigin + (int) (value * scaleX);
        }
        /** Convert X value for node[i] to X position in the canvas. */
        int xPos(int i, int value) {
            Node node = nodes[i];
            if (i == 0) {
                if (node.invertX)
                    return xPos(node.maxX - value);
                else
                    return xPos(value);
            } else {
                if (node.invertX)
                    return nodes[i - 1].posX
                            + (int) ((node.maxX - value) * scaleX);
                else
                    return nodes[i - 1].posX + (int) (value * scaleX);
            }
        }
        /** Convert Y value to Y position in the canvas. */
        int yPos(int value) {
            return canvasHeight - (int) (value * scaleY);
        }
	/** Return X axis value of <code>node[i]</code>. */
        private int getX(int i) {
	    if (!nodes[i].variableX)
		return nodes[i].minX;
	    else if (nodes[i].invertX)
		return (nodes[i].maxX - nodes[i].pmodelX.get());
	    else
	        return (nodes[i].pmodelX.get());
        }

	/** Return Y axis value of <code>node[i]</code>. */
        private int getY(int i) {
	    if (nodes[i].minY == Node.SAME)
		return getY(i - 1);
	    else if (!nodes[i].variableY)
		return nodes[i].baseY + nodes[i].minY;
	    else
	        return nodes[i].baseY + nodes[i].pmodelY.get();
        }

	protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int sumX = 0;
            for (int i = 0; i < nodes.length; i++) {
                // calculate coordinates on the canvas
		sumX += getX(i);
		nodes[i].posX = xPos(sumX);
		nodes[i].posY = yPos(getY(i));

		// draw a rectangle and a line. Filled box is used for a node for variable.
                if ((nodes[i].variableX || nodes[i].variableY) && enabled)
                    g.fillRect(nodes[i].posX - DELTA / 2, nodes[i].posY - DELTA
                            / 2, DELTA, DELTA);
                else
                    g.drawRect(nodes[i].posX - DELTA / 2, nodes[i].posY - DELTA
                            / 2, DELTA, DELTA);
                if (i > 0)
                    g.drawLine(nodes[i].posX, nodes[i].posY, nodes[i - 1].posX,
                            nodes[i - 1].posY);
            }
        }

	public void setEnabled(boolean enabled) {
	    super.setEnabled(enabled);
	    this.enabled = enabled;
	    repaint();
	}

        private class MyListener extends MouseInputAdapter {
	    /** dragging node number */
            private int dragNodeIdx = -1;
            private boolean toggle = false;

            public void mousePressed(MouseEvent e) {
                if (!enabled) {
                    dragNodeIdx = -1;
                    return;
                }

                int x = e.getX();
                int y = e.getY();
		// Select the first close node.
                // Change search order every time for the case when
                // multiple nodes overlap each other.
                if (toggle) {
                    toggle = false;
                    for (int i = 0; i < nodes.length; i++) {
                        if (isClose(nodes[i], x, y)) {
                            dragNodeIdx = i;
                            return;
                        }
                    }
                } else {
                    toggle = true;
                    for (int i = nodes.length - 1; i >= 0; i--) {
                        if (isClose(nodes[i], x, y)) {
                            dragNodeIdx = i;
                            return;
                        }
                    }
                }
		// not found
                dragNodeIdx = -1;
            }

            private boolean isClose(Node node, int x, int y) {
                // ignore static node
                return ((node.variableX || node.variableY)
                        && (Math.abs(x - node.posX) < DELTA)
                        && (Math.abs(y - node.posY) < DELTA));
            }

            /** last mouse position clocked or dragged. */
            private int oldx, oldy;

            public void mouseDragged(MouseEvent e) {
		if (dragNodeIdx == -1)
		    return;

                int x = e.getX();
                int y = e.getY();
                if ((x == oldx) && (y == oldy))
		    return;

                Node node = nodes[dragNodeIdx];
		// move the selected node one dot by one dot.
                // and send Sysex Message (added Jan. 7, 2005)
                if (node.variableX && x != oldx) {
                    int xVal = node.pmodelX.get();
                    if (node.invertX) {
                        if (x > oldx) { // right
                            while (x - node.posX > DELTA && xVal > node.minX)
                                node.posX = xPos(dragNodeIdx, --xVal);
                        } else { // left
                            while (x - node.posX < -DELTA && xVal < node.maxX)
                                node.posX = xPos(dragNodeIdx, ++xVal);
                        }
                    } else {
                        if (x > oldx) { // right
                            while (x - node.posX > DELTA && xVal < node.maxX)
                                node.posX = xPos(dragNodeIdx, ++xVal);
                        } else { // left
                            while (x - node.posX < -DELTA && xVal > node.minX)
                                node.posX = xPos(dragNodeIdx, --xVal);
                        }
                    }
                    node.pmodelX.set(xVal);
                    params[node.faderNumX].textField.setText(String.valueOf(xVal));
                    sendSysex(node.senderX, xVal);
                    oldx = x;
                }
                if (node.variableY && y != oldy) {
                    int yVal = node.pmodelY.get();
                    if (y < oldy) { // up
                        while (y - node.posY < -DELTA && yVal < node.maxY)
                            node.posY = yPos(node.baseY + ++yVal);
                    } else { // down
                        while (y - node.posY > DELTA && yVal > node.minY)
                            node.posY = yPos(node.baseY + --yVal);
                    }
                    node.pmodelY.set(yVal);
                    params[node.faderNumY].textField.setText(String.valueOf(yVal));
                    sendSysex(node.senderY, yVal);
                    oldy = y;
                }
                repaint();	// invokes paintComponent()
	    }
	}
    }

    /** Data structure for Paramaters. */
    protected static class Param {
        /**
         * JLabel widgets which show the names of the X/Y axis
         * parameters riding each access.
         */
        protected JLabel label;
        /** JTextField which show the X/Y axis value. */
        protected JTextField textField;
        /** array index of 'nodes' for the corresponding Node. */
        protected int node;
        /** true for X value, false for Y value. */
        protected boolean isX;
        protected Param(JLabel label, JTextField textField, int node, boolean isX) {
            this.label = label;
            this.textField = textField;
            this.node = node;
            this.isX = isX;
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
         * When this value is used for <code>miny</code>, Y axis value
         * remains at whatever the Y axis value of the previous node was.
         */
	public static final int SAME = 5000;

	private int minX;
	private int minY;
	private int maxX;
	private int maxY;
	private IParamModel pmodelX;
	private IParamModel pmodelY;
	private ISender senderX;
	private ISender senderY;
	private boolean invertX;
	private int baseY;
	private String nameX;
	private String nameY;

	/** can be moved in X direction or not */
	private boolean variableX;
	/** can be moved in Y direction or not */
	private boolean variableY;
        /** fader number for variable X value. */
        private int faderNumX;
        /** fader number for variable X value. */
        private int faderNumY;
	/** X coordinates on the canvas. */
        private int posX;
	/** Y coordinates on the canvas. */
        private int posY;
 
	/**
	 * Construcutor for a <code>Node</code>.<p>
	 *
	 * Using <code>null</code>s for the Models and Senders and setting
	 * min to max means that the node is stationary on that axis and has
	 * no related parameter.<p>
	 *
	 * @param minx The minimum value permitted by the synth
	 * parameter which rides the X axis of the node.
	 * @param maxx The maximum value permitted by the synth
	 * parameter which rides the X axis of the node.
	 * @param pmodelx The Parameter Model which provides reading/writing
	 * abilities to the sysex data representing the parameter.
	 *
	 * @param miny The minimum value permitted by the synth
	 * parameter which rides the Y axis of the node. Using 
	 * <code>Node.SAME</code> for <code>miny</code> means that
	 * the height remains at whatever the previous node was at.
	 * @param maxy The maximum value permitted by the synth
	 * parameter which rides the Y axis of the node. When 
	 * <code>Node.SAME</code> is used for <code>miny</code>, this 
	 * parameter is ignored.
	 * @param pmodely The Parameter Model which provides reading/writing
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
	 * @param senderx The ISender which send system exclusive messages
	 * to the synths when the Node is moved on the X axis direction.
	 * @param sendery The ISender which send system exclusive messages
	 * to the synths when the Node is moved on the Y axis direction.
	 *
	 * @param namex The names of the X-axis parameters riding each
	 * access.
	 * @param namey The names of the Y-axis parameters riding each
	 * access.
	 *
	 * @see EnvelopeWidget
	 */
	public Node(int minx, int maxx, IParamModel pmodelx,
		    int miny, int maxy, IParamModel pmodely,
		    int basey, boolean invertx,
		    ISender senderx, ISender sendery,
		    String namex, String namey) {
	    baseY = basey;
	    minX = minx;
	    maxX = maxx;
	    minY = miny;
	    maxY = miny == SAME ? miny : maxy;
	    pmodelX = pmodelx;
	    pmodelY = pmodely;
	    senderX = senderx;
	    senderY = sendery;
	    nameX = namex;
	    nameY = namey;
	    invertX = invertx;

	    variableX = minX != maxX;
	    variableY = minY != maxY;
	    if (variableX && (pmodelX == null || senderX == null)
	            || variableY && (pmodelY == null || senderY == null))
	        ErrorMsg.reportError("Illegal parameter",
	                "Parameter model and Sender must be set for variable parameter.");
	}
    }
}
