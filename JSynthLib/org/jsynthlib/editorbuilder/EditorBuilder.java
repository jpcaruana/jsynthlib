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

import org.jsynthlib.editorbuilder.widgets.Strut;

import java.awt.event.*;
import core.CompatibleFileDialog;

import java.io.*;
import java.beans.*;

public class EditorBuilder {

  protected static ParameterFrame pframe;
  protected static DesignerFrame dframe;
  protected static PropertiesFrame propframe;
  protected static Action[][] actions = {
    {
      new loadParams(),
      new saveParams(),
      new emptyParams(),
      null,
      new openDesigner(),
      new saveDesigner(),
      new export(),
      null,
      new exit(),
    },
    {
      new showpframe(),
      new showdframe(),
      new showpropframe(),
    }
  };

  public static void main (String[] args) throws Exception{
    //    javax.swing.UIManager
    //  .setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    pframe = new ParameterFrame();
    dframe = new DesignerFrame(true);
    propframe = new PropertiesFrame();

    javax.swing.SwingUtilities.invokeAndWait(new Runnable () {
	public void run () {
	  createMenus(pframe);
	  createMenus(dframe);
	  createMenus(propframe);

	  pframe.setLocation(0,0);
	  pframe.setVisible(true);
	  
	  dframe.pack();
	  dframe.setLocation(pframe.getWidth(), 0);
	  dframe.setVisible(true);

	  propframe.setLocation(0, pframe.getHeight());
	  propframe.setVisible(true);
	}
      });
  }
  public static DesignerFrame getDesignerFrame() { return dframe; }
  public static ParameterFrame getParameterFrame() { return pframe; }
  public static PropertiesFrame getPropertiesFrame() { return propframe; }

  protected static void createMenus(JFrame f) {
    JMenuBar bar = new JMenuBar();
    JMenu file = new JMenu("File");
    JMenu window = new JMenu("Window");
    for (int i = 0; i < actions[0].length; i++) {
      if (actions[0][i] == null)
	file.addSeparator();
      else
	file.add(actions[0][i]);
    }
    for (int i = 0; i < actions[1].length; i++)
      window.add(actions[1][i]);

    bar.add(file);
    bar.add(window);
    f.setJMenuBar(bar);
  }

  protected static void save(File file) {
    JMenuBar pmb = pframe.getJMenuBar(), dmb = dframe.getJMenuBar(),
      propmb = propframe.getJMenuBar();

    pframe.setJMenuBar(null); dframe.setJMenuBar(null);
    propframe.setJMenuBar(null);
      
    try {
      XMLEncoder e =
	  new XMLEncoder(
	          new BufferedOutputStream(
		          new FileOutputStream(file)));
      e.setPersistenceDelegate(Anchor.class,
			       new DefaultPersistenceDelegate(new String[] {
				   "constrainedComponent", "constrainedSide",
				   "targetComponent", "targetSide", "padding",
			       }));
      e.setPersistenceDelegate(Strut.class,
			       new DefaultPersistenceDelegate(new String[] {
			       "x", "y", "width", "height",
			       }));
      
      e.writeObject(pframe);
      e.writeObject(dframe);
      e.writeObject(propframe);
      e.close();

    } catch (Exception e) {e.printStackTrace(); }

    pframe.setJMenuBar(pmb); dframe.setJMenuBar(dmb);
    propframe.setJMenuBar(propmb);
  }
  protected static void open (File file) {
    pframe.setVisible(false);
    dframe.setVisible(false);
    propframe.setVisible(false);

    JMenuBar pmb = pframe.getJMenuBar(), dmb = dframe.getJMenuBar(),
      propmb = propframe.getJMenuBar();

    pframe.setJMenuBar(null); dframe.setJMenuBar(null);
    propframe.setJMenuBar(null);
      
    try {
      XMLDecoder d = 
	      new XMLDecoder(
	              new BufferedInputStream(
	                      new FileInputStream( file )));
      pframe = (ParameterFrame)d.readObject();
      dframe = (DesignerFrame)d.readObject();
      propframe = (PropertiesFrame)d.readObject();
      d.close();
    } catch (Exception e) { }

    pframe.setJMenuBar(pmb); dframe.setJMenuBar(dmb);
    propframe.setJMenuBar(propmb);

    pframe.show();
    dframe.show();
    propframe.show();
  }

    protected static class loadParams extends AbstractAction {
	public loadParams() { super("Load Parameters..."); }
	public void actionPerformed(ActionEvent e) {
	    CompatibleFileDialog chooser = new CompatibleFileDialog();
	    int retval = chooser.showOpenDialog(null);
	    if (retval == JFileChooser.APPROVE_OPTION)
		pframe.load(chooser.getSelectedFile());
	}
    }
    protected static class saveParams extends AbstractAction {
	public saveParams() { super("Save Parameters..."); }
	public void actionPerformed(ActionEvent e) {
	    CompatibleFileDialog chooser = new CompatibleFileDialog();
	    int retval = chooser.showSaveDialog(null);
	    if (retval == JFileChooser.APPROVE_OPTION)
		pframe.save(chooser.getSelectedFile());
	}
    }
    protected static class emptyParams extends AbstractAction {
	public emptyParams() { super("Empty Parameter List"); }
	public void actionPerformed(ActionEvent e) {
	    pframe.empty();
	}
    }
    protected static class openDesigner extends AbstractAction {
	public openDesigner() { super("Open..."); setEnabled(false); }
	public void actionPerformed(ActionEvent e) {
	    CompatibleFileDialog chooser = new CompatibleFileDialog();
	    int retval = chooser.showOpenDialog(null);
	    if (retval == JFileChooser.APPROVE_OPTION)
		open(chooser.getSelectedFile());
	}
    }
    protected static class saveDesigner extends AbstractAction {
	public saveDesigner() { super("Save Designer..."); setEnabled(false);}
	public void actionPerformed(ActionEvent e) {
	    CompatibleFileDialog chooser = new CompatibleFileDialog();
	    int retval = chooser.showSaveDialog(null);
	    if (retval == JFileChooser.APPROVE_OPTION)
		save(chooser.getSelectedFile());
	}
    }
    protected static class export extends AbstractAction {
	public export() { super("Export java source..."); }
	public void actionPerformed(ActionEvent e) {
	    CompatibleFileDialog chooser = new CompatibleFileDialog();
	    int retval = chooser.showSaveDialog(null);
	    if (retval == JFileChooser.APPROVE_OPTION)
		dframe.export(chooser.getSelectedFile());
	}
    }

    protected static class exit extends AbstractAction {
	public exit() { super("Exit"); }
	public void actionPerformed(ActionEvent e) {
	    // save
	    System.exit(0);
	}
    }
    protected static class showpframe extends AbstractAction {
	public showpframe() { super("Parameters");}
	public void actionPerformed(ActionEvent e) {
	    pframe.show();
	}
    }
    protected static class showdframe extends AbstractAction {
	public showdframe() { super("Designer");}
	public void actionPerformed(ActionEvent e) {
	    dframe.show();
	}
    }
    protected static class showpropframe extends AbstractAction {
	public showpropframe() { super("Properties");}
	public void actionPerformed(ActionEvent e) {
	    propframe.show();
	}
    }
}
