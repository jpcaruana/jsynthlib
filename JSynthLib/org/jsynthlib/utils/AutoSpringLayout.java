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
package org.jsynthlib.utils;

import javax.swing.SpringLayout;
import javax.swing.Spring;
import java.awt.Container;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;

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
					  
