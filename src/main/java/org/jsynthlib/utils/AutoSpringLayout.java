package org.jsynthlib.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Spring;
import javax.swing.SpringLayout;

public class AutoSpringLayout extends SpringLayout {
  protected Map<Container, Boolean> isSized = new HashMap<Container, Boolean>();

  public Dimension minimumLayoutSize(Container parent) {
    if (isSized.get(parent) == null
	    || !((Boolean)isSized.get(parent)).booleanValue())
      sizeContainer(parent);
    return super.minimumLayoutSize(parent);
  }
    
  public Dimension preferredLayoutSize(Container parent) {
    if (isSized.get(parent) == null
	    || !((Boolean)isSized.get(parent)).booleanValue())
      sizeContainer(parent);
    return super.preferredLayoutSize(parent);
  }
    
  public Dimension maximumLayoutSize(Container parent) {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }
    
  protected void sizeContainer(Container parent) {
    Component[] components = parent.getComponents();
    int height = 0, width = 0;
    SpringLayout.Constraints pconstraints, constraints;
    pconstraints = getConstraints(parent);
    
    for (int i = 0; i < components.length; i++) {
      constraints = getConstraints(components[i]);
      height = Math.max(height,
			constraints.getConstraint(SpringLayout.SOUTH)
			           .getValue());
      width = Math.max(width,
		       constraints.getConstraint(SpringLayout.EAST)
		                  .getValue());
    }
    pconstraints.setConstraint(SpringLayout.SOUTH, Spring.constant(height));
    pconstraints.setConstraint(SpringLayout.EAST, Spring.constant(width));

    isSized.put(parent, Boolean.TRUE);
  }

  public void layoutContainer(Container parent) {
    sizeContainer(parent);
    super.layoutContainer(parent);
  }
}
					  
