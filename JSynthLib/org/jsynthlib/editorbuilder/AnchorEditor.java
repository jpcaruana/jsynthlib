/*
 * Copyright 2002 Rib Rdb (TM). All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution.
 * 
 * Neither the name of Rib Rdb (TM) or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. RIB RDB AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL RIB RDB OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF RIB RDB HAS
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */
package org.jsynthlib.editorbuilder;
import javax.swing.*;
import javax.swing.event.*;

import org.jsynthlib.editorbuilder.widgets.Widget;

import java.util.*;
import java.awt.event.*;
import java.awt.Component;

public class AnchorEditor extends Box implements ActionListener,
java.io.Serializable {
	protected Anchor anchor;
	protected boolean vertical;
	protected LinkedList listeners = new LinkedList();
	protected JRadioButton rb0;
	protected JRadioButton rb1;
	protected JComboBox side;
	protected JComboBox widget;
	
	
	public AnchorEditor(Anchor a) {
		super(BoxLayout.Y_AXIS);
		anchor = a;
		if (anchor.getConstrainedSide() < 2)
			vertical = true;
		else
			vertical = false;
		Box b = createHorizontalBox();
		add(b);
		rb0 = new JRadioButton(vertical ? "North"
				: "East" );
		rb0.addActionListener(this);
		ButtonGroup bg = new ButtonGroup();
		bg.add(rb0);
		b.add(rb0);
		
		rb1 = new JRadioButton(vertical ? "South"
				: "West" );
		rb1.addActionListener(this);
		bg.add(rb1);
		b.add(rb1);
		
		if (anchor.getConstrainedSide() % 2 == 0)
			bg.setSelected(rb0.getModel(), true);
		else
			bg.setSelected(rb1.getModel(), true);
		
		b = createHorizontalBox();
		add(b);
		
		b.add(new JLabel("connected to"));
		b.add(Box.createHorizontalGlue());
		
		side = new JComboBox(new String[] {
				"North","South","East","West"
		});
		side.setSelectedIndex(anchor.getTargetSide());
		side.addActionListener(this);
		b.add(side);
		
		b = createHorizontalBox();
		add(b);
		
		b.add(new JLabel("of"));
		b.add(Box.createHorizontalGlue());
		
		Widget t = EditorBuilder.getDesignerFrame().getRootWidget();
		if (anchor.getTargetComponent() instanceof Widget)
			t = (Widget)anchor.getTargetComponent();
		
		widget = new JComboBox(((GlassPane)EditorBuilder.getDesignerFrame()
				.getGlassPane())
				.newWidgetComboBoxModel());
		widget.setSelectedItem(t.getId());
		widget.addActionListener(this);
		b.add(widget);
		
		b = createHorizontalBox();
		add(b);
		
		b.add(new JLabel("with padding of"));
		b.add(Box.createHorizontalGlue());
		
		JTextField tf = new JTextField();
		tf.setText(""+anchor.getPadding());
		tf.addActionListener(this);
		b.add(tf);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source instanceof JRadioButton) {
			int newside = vertical ? 0 : 2;
			if (source == rb1)
				newside += 1;
			if (anchor.getConstrainedSide() != newside) {
				anchor = new Anchor(anchor.getConstrainedComponent(),
						newside,
						anchor.getTargetComponent(),
						anchor.getTargetSide(),
						anchor.getPaddingSpring());
			}
		} else if (source instanceof JComboBox) {
			if (source == side) {
				anchor.setTargetSide(((JComboBox)side).getSelectedIndex());
			} else if (source == widget) {
				if (GlassPane.isWidgetModelNotifying())
					return;
				String id = (String)widget.getSelectedItem();
				Widget w = ((GlassPane)EditorBuilder.getDesignerFrame()
						.getGlassPane()).getWidget(id);
				if (w instanceof Component)
					anchor.setTargetComponent((Component) w);
			}
		} else if (source instanceof JTextField) {
			try {
				anchor.setPadding(
						Integer.parseInt(
								((JTextField)source).getText()));
			} catch (Exception ex) {}
			((JTextField)source).setText("" + anchor.getPadding());
		} else
			throw new Error("Unrecognized source " + source);
		
		notifyListeners();
	}
	protected void notifyListeners() {
		Iterator it = listeners.iterator();
		while (it.hasNext())
			((ChangeListener)it.next()).stateChanged(new ChangeEvent(this));
	}
	public void addChangeListener(ChangeListener l) {
		listeners.add(l);
	}
	
}
