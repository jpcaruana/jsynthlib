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
package org.jsynthlib.editorbuilder.widgets;
import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.Spring;
import javax.swing.SpringLayout.Constraints;

import org.jsynthlib.utils.XMLWriter;
import org.xml.sax.SAXException;

public class Strut extends Widget{
  protected Constraints constraints;

  public Strut(int x, int y) { this(x, y, 8, 100); }
  public Strut(int x, int y, int width, int height) {
  	super("strut");
    setBorder(BorderFactory.createLineBorder(Color.red, 2));
    constraints = new Constraints(Spring.constant( x ),
				  Spring.constant( y ),
				  Spring.constant( width ),
				  Spring.constant( height ));
  }
  public Constraints getConstraints() { return constraints; }
  public void setX( int x ) {
    constraints.setX(Spring.constant( x ));
    setBounds(x,getY(),getWidth(),getHeight());
    //updateBounds();
  }
  public void setY( int y ) {
    constraints.setY(Spring.constant( y ));
    setBounds(getX(),y, getWidth(), getHeight());
    //updateBounds();
  }
  public void setWidth( int width ) {
    constraints.setWidth(Spring.constant( width ));
    setBounds(getX(),getY(),width, getHeight());
    //updateBounds();
  }
  public void setHeight( int height ) {
    constraints.setHeight(Spring.constant( height ));
    setBounds(getX(),getY(),getWidth(),height);
    //updateBounds();
  }
  private void updateBounds() {
    setBounds(new Rectangle(getX(), getY(), getWidth(), getHeight()));
  }
  
/*
  public String construct() { return "new JLabel()"; }
  public String postinitialize() {
    String id = GlassPane.getWidgetIdentifier(this);
    return
	"((SpringLayout)"+id+".getParent().getLayout()).addLayoutComponent(\n"+
"            " + id + ",\n" +
"            new Constraints(Spring.constant( " + getX() + " ),\n" +
"                            Spring.constant( " + getY() + " ),\n" +
"                            Spring.constant( " + getWidth()+ " ),\n" +
"                            Spring.constant( " + getHeight() + " )));";
  }
  public String preinitialize() { return null; }
*/
  public int getCX() {
      return constraints.getX().getValue();
  }
  public int getCY() {
      return constraints.getY().getValue();
  }
	protected void writeContent(XMLWriter xml) throws SAXException {
		xml.setAttribute("type", "absolute");
		xml.startElement("position");
		xml.writeProperty("x", getCX());
		xml.writeProperty("y", getCY());
		xml.endElement("position");
		
		xml.startElement("size");
		xml.writeProperty("width", getWidth());
		xml.writeProperty("height", getHeight());
		xml.endElement("size");
	}
}
