package org.jsynthlib.editorbuilder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SpringLayout.Constraints;

import org.jsynthlib.editorbuilder.widgets.Widget;

public class Anchor extends Spring implements java.io.Serializable {
	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int EAST = 2;
	public static final int WEST = 3;
	/** Don't use this one. It is only used for determining how to 
	 *  layout components. Anchors cannot connect to the center of 
	 *  a component.
	 **/
	static final int CENTER = 4;
	protected static final String[] SIDES = new String[] {
			SpringLayout.NORTH,
			SpringLayout.SOUTH,
			SpringLayout.EAST,
			SpringLayout.WEST,
	};
	
	protected static targetSet targets = null;
	protected static WeakHashMap cmap = new WeakHashMap();
	
	protected Component constrained_component;
	protected int constrained_side;
	protected Component target_component;
	protected int target_side;
	protected Constraints target_constraints;
	protected SpringLayout layout;
	protected Spring padding;
	
	public Anchor(Component c, int cs,
			Component t, int ts, int _padding) {
		this(c, cs, t, ts, Spring.constant(_padding));
	}
	
	public Anchor(Component c, int cs,
			Component t, int ts, Spring _padding) {
		if (targets == null)
			targets = new targetSet();
		
		layout = (SpringLayout)c.getParent().getLayout();
		
		Constraints cons = layout.getConstraints(c);
		
		constrained_component = c;
		constrained_side = cs;
		target_component = t;
		target_constraints = layout.getConstraints(target_component);
		target_side = ts;
		padding = _padding;
		checkSides();
		targets.add(this);
		if (constrained_side < 2)
			setNSAnchor(constrained_component, this);
		else
			setEWAnchor(constrained_component, this);
		
		// Make sure we don't overly constrain.
		cons.setConstraint(SIDES[ opposite(constrained_side) ], null);
		// Set constraint to this.
		cons.setConstraint(SIDES[ constrained_side ], this);
		c.getParent().invalidate();
	}
	public int getPadding() { return padding.getValue(); }
	public Spring getPaddingSpring() { return padding; }
	public void setPadding(int p) { setPadding ( Spring.constant(p) ); }
	public void setPadding(Spring p) { 
		padding = p;
		constrained_component.getParent().invalidate();
	}
	public Component getConstrainedComponent() { return constrained_component; }
	public int getConstrainedSide() { return constrained_side; }
	public Component getTargetComponent() { return target_component; }
	public void setTargetComponent(Component c) {
		if (c == target_component || c == null)
			return;
		targets.remove(this);
		target_component = c;
		target_constraints = layout.getConstraints(c);
		targets.add(this);
		constrained_component.getParent().invalidate();
	}
	public int getTargetSide() { return target_side; }
	public void setTargetSide(int s) {
		if (target_side == s)
			return;
		targets.remove(this);
		target_side = s; 
		checkSides();
		targets.add(this);
		constrained_component.getParent().invalidate();
	}
	protected void checkSides() {
		if (constrained_side < 0 || constrained_side > 3)
			throw new Error(constrained_side + " is not a valid constrained side.");
		if (target_side < 0 || target_side > 3)
			throw new Error(target_side + " is not a valid target side.");
	}
	public static int opposite (int side) {
		if (side % 2 == 0)
			return side + 1;
		else
			return side - 1;
	}
	
	protected Point getCenter(Component component, int side) {
		if (side < 0 || side > 3)
			throw new Error(side + " is an invalid side.");
		
		Constraints c = layout.getConstraints(component);
		int x = c.getX().getValue(), y = c.getY().getValue();
		
		if (side < 2) {
			x += c.getWidth().getValue()/2;
			if (side == 1)
				y += c.getHeight().getValue();
		} else {
			y += c.getHeight().getValue()/2;
			if (side == 2)
				x += c.getWidth().getValue();
		}
		return new Point(x, y);
	}
	
	public int getMinimumValue() {
		return Spring.sum(target_constraints.getConstraint(SIDES[ target_side ]),
				padding).getMinimumValue();
	}
	public int getPreferredValue() {
		return Spring.sum(target_constraints.getConstraint(SIDES[ target_side ]),
				padding).getPreferredValue();
	}
	public int getMaximumValue() {
		return Spring.sum(target_constraints.getConstraint(SIDES[ target_side ]),
				padding).getMaximumValue();
	}
	public int getValue() {
		return Spring.sum(target_constraints.getConstraint(SIDES[ target_side ]),
				padding).getValue();
	}
	public void setValue(int v) {}
	
	// Code for keeping track of all the anchors.
	
	protected class targetSet {
		Map targets = new WeakHashMap();
		public void add(Anchor anchor) {
			if (anchor == null)
				return;
			Component c = anchor.getTargetComponent();
			getNode(c).add(anchor);
		}
		public boolean remove(Anchor anchor) {
			if (anchor == null)
				return false;
			Component c = anchor.getTargetComponent();
			return getNode(c).remove(anchor);
		}
		public void remove(Component c) {
			Container parent = c.getParent();
			Constraints cons = ((SpringLayout)parent.getLayout()).getConstraints(c);
			targetSetNode node = getNode(c);
			for (int i = 0; i < CENTER; i++) {
				int padding = cons.getConstraint(SIDES[ i ]).getValue();
				Iterator it = node.getSet(i).iterator();
				while (it.hasNext()) {
					Anchor a = (Anchor)it.next();
					it.remove();
					a.setTargetComponent(parent);
					a.setPadding(padding);
				}
			}
			targets.remove(c);
		}
		public Set getSet(Component c, int side) {
			if (c == null || side < 0 || side > 3)
				return null;
			return getNode(c).getSet(side);
		}
		protected targetSetNode getNode(Component c) {
			targetSetNode node = (targetSetNode)targets.get(c);
			if (node == null) {
				node = new targetSetNode();
				targets.put(c, node);
			}
			return node;
		}
		protected class targetSetNode {
			protected Set[] anchors = new Set[] {
					new HashSet(),
					new HashSet(),
					new HashSet(),
					new HashSet(),
			};
			public void add(Anchor a) {
				anchors[a.getTargetSide()].add(a);
			}
			public boolean remove(Anchor a) {
				return anchors[a.getTargetSide()].remove(a);
			}
			public Set getSet(int side) {
				return anchors[side];
			}
		}
	}
	public static void printCmap() {
		java.util.Iterator it = cmap.values().iterator();
		while (it.hasNext()) {
			Anchor[] al = (Anchor[])it.next();
			for (int i = 0; i < al.length; i++) {
				String cstring, tstring;
				if (al[i].getConstrainedComponent() instanceof javax.swing.JLabel)
					cstring = ((javax.swing.JLabel)al[i].getConstrainedComponent()).getText();
				else
					cstring = al[i].getConstrainedComponent().toString();
				if (al[i].getTargetComponent() instanceof javax.swing.JLabel)
					tstring = ((javax.swing.JLabel)al[i].getTargetComponent()).getText();
				else
					tstring = al[i].getTargetComponent().toString();
				
				System.out.println("Side "+al[i].getConstrainedSide() + " of " +
						cstring + " points to side " +
						al[i].getTargetSide() + " of " + tstring);
			}
		}
		System.out.println();
	}
	public static Set getSet(Component c, int side) {
		return targets.getSet(c, side);
	}
	public static Anchor getNSAnchor(Component c) {
		if (c == null)
			return null;
		Anchor[] as = (Anchor[])cmap.get(c);
		if (as == null) {
			as = new Anchor[2];
			cmap.put(c, as);
		}
		return as[0];
	}
	public static Anchor getEWAnchor(Component c) {
		if (c == null)
			return null;
		Anchor[] as = (Anchor[])cmap.get(c);
		if (as == null) {
			as = new Anchor[2];
			cmap.put(c, as);
		}
		return as[1];
	}
	protected static void setNSAnchor(Component c, Anchor a) {
		if (c == null)
			return;
		Anchor[] as = (Anchor[])cmap.get(c);
		if (as == null) {
			as = new Anchor[2];
			cmap.put(c, as);
		}
		if (as[0] != null)
			targets.remove(as[0]);
		as[0] = a;
	}
	protected static void setEWAnchor(Component c, Anchor a) {
		if (c == null)
			return;
		Anchor[] as = (Anchor[])cmap.get(c);
		if (as == null) {
			as = new Anchor[2];
			cmap.put(c, as);
		}
		if (as[1] != null)
			targets.remove(as[1]);
		as[1] = a;
	}
	
	// For java export
	public String toString() {
		String cid =GlassPane.getWidgetIdentifier((Widget)constrained_component);
		String tid;
		if (target_component instanceof Widget)
			tid = GlassPane.getWidgetIdentifier((Widget)target_component);
		else
			tid = "scrollPane";
		return
		"((SpringLayout)"+ cid +".getParent().getLayout()).putConstraint(\n"+
		"             \"" + SIDES[ constrained_side ] + "\", " + cid + ", "
		+ "Spring.constant( " + padding.getMinimumValue() + ", "
		+ padding.getPreferredValue() +", " + padding.getMaximumValue()
		+ " ),\n" +
		"             \"" + SIDES[ target_side ] + "\", " + tid + ");";
	}
	public void drawConnector(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		Point ptA = getCenter(constrained_component, constrained_side);
		Point ptB = getCenter(target_component, target_side);
		
		int[] xpoints = new int[] { ptA.x, ptA.x, ptB.x, ptB.x };
		int[] ypoints = new int[] { ptA.y, ptA.y, ptB.y, ptB.y };
		
		if (constrained_side < EAST) { // vertical
			ypoints[1] = ypoints[2] = ptA.y + (ptB.y - ptA.y)/2;
		} else {
			xpoints[1] = xpoints[2] = ptA.x + (ptB.x - ptA.x)/2;
		}
		
		Stroke s = g2d.getStroke();
		Color c = g2d.getColor();
		g2d.setStroke(new BasicStroke(3));
		g2d.setColor(Color.blue);
		g2d.drawPolyline(xpoints, ypoints, 4);
		
		/*    ptA = SwingUtilities.convertPoint(source, ptA, destination);
		 ptB = SwingUtilities.convertPoint(source, ptB, destination);*/
	}
}
