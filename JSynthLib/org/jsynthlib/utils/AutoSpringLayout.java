package org.jsynthlib.utils;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.HashMap;

import javax.swing.Spring;
import javax.swing.SpringLayout;

public class AutoSpringLayout extends SpringLayout {
  protected HashMap is_sized = new HashMap();

  public Dimension minimumLayoutSize(Container parent) {
    if (is_sized.get(parent) == null
	    || !((Boolean)is_sized.get(parent)).booleanValue())
      sizeContainer(parent);
    return super.minimumLayoutSize(parent);
  }
    
  public Dimension preferredLayoutSize(Container parent) {
    if (is_sized.get(parent) == null
	    || !((Boolean)is_sized.get(parent)).booleanValue())
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

    is_sized.put(parent, Boolean.TRUE);
  }

  public void layoutContainer(Container parent) {
    sizeContainer(parent);
    super.layoutContainer(parent);
  }
}
					  
