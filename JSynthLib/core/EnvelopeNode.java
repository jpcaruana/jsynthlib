/* This is a datatype used by EnvelopeWidget. It stores information
   about a single node (point) in the Widget */
package core;

/**
 * Each <code>EnvelopeNode</code> is one of the movable squares on the
 * envelope. Some of these nodes are stationary, some contain two
 * parameters on the synth and can be moved vertically and
 * horizontally, and others contain only one parameter and can
 * therefore be moved in only one direction.
 * @see EnvelopeWidget
 */
public class EnvelopeNode {
    public static final int SAME = 5000;
    int minX;
    int minY;
    int maxX;
    int maxY;
    ParamModel ofsX;
    ParamModel ofsY;
    SysexSender senderX;
    SysexSender senderY;
    boolean invertX;
    int baseY;
    String nameX;
    String nameY;

    /**
     * Construcutor for a <code>EnvelopeNode</code>.<p>
     *
     * Using <code>null</code>s for the Models and Senders and setting
     * min to max means that a node is stationary on that axis and has
     * no related parameter.<p>
     *
     * Using <code>EnvelopeNode.SAME</code> for <code>miny</code> or
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
     * @param x The SysexSender which send system exclusive messages
     * to the synths when the Node is moved on the X axis direction.
     * @param y The SysexSender which send system exclusive messages
     * to the synths when the Node is moved on the Y axis direction.
     *
     * @param namex The names of the X-axis parameters riding each
     * access.
     * @param namey The names of the Y-axis parameters riding each
     * access.
     *
     * @see EnvelopeWidget
     */
    public EnvelopeNode(int minx, int maxx, ParamModel ofsx,
			int miny, int maxy, ParamModel ofsy,
			int basey, boolean invertx,
			SysexSender x, SysexSender y,
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
