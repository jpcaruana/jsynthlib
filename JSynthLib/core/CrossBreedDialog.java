/* $Id$ */

package core;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.*;
import java.beans.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;

public class CrossBreedDialog extends JDialog {
  final JLabel l1;
  CrossBreeder crossBreeder;
  public CrossBreedDialog(JFrame Parent) {
        super(Parent,"Patch Cross-Breeder",false);
        crossBreeder = new CrossBreeder();
	JPanel container= new JPanel();
        container.setLayout (new BorderLayout());
        JPanel p4 = new JPanel();
        p4.setLayout (new ColumnLayout());
	l1 = new JLabel("Patch Type: ");

	JPanel buttonPanel = new JPanel();
	JPanel buttonPanel2=new JPanel();
	JButton gen = new JButton("Generate");
	gen.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
                                   generatePressed();
			       }});
        buttonPanel2.add(gen);
	JButton play = new JButton("Play");
	play.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
                                 play();
			       }});
        buttonPanel2.add(play);
	JButton keep = new JButton("Keep");
	keep.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
			       try {
			        PatchBasket library=(PatchBasket)JSLDesktop.getSelectedFrame();
				Patch q = crossBreeder.getCurrentPatch();
				library.PastePatch(q);
			       }catch (Exception ex){JOptionPane.showMessageDialog(null, "Destination Library Must be Focused","Error", JOptionPane.ERROR_MESSAGE);}
			       }});
        buttonPanel2.add(keep);

	JButton ok = new JButton("Close");
        ok.addActionListener(new ActionListener() {
	                       public void actionPerformed(ActionEvent e) {
                                   OKPressed();
			       }});
	buttonPanel.add( ok );
	getRootPane().setDefaultButton(ok);
        p4.add(l1);

	container.add(p4,BorderLayout.NORTH);
	container.add(buttonPanel2,BorderLayout.CENTER);
	container.add(buttonPanel,BorderLayout.SOUTH);
	getContentPane().add(container);
       // setSize(400,300);
        pack();
	centerDialog();


  }
   public void show()
   {
       super.show();


   }
    protected void centerDialog() {
        Dimension screenSize = this.getToolkit().getScreenSize();
	Dimension size = this.getSize();
	screenSize.height = screenSize.height/2;
	screenSize.width = screenSize.width/2;
	size.height = size.height/2;
	size.width = size.width/2;
	int y = screenSize.height - size.height;
	int x = screenSize.width - size.width;
	this.setLocation(x,y);
    }

void OKPressed() {
 	this.setVisible(false);
  }
void generatePressed()
{
   crossBreeder.workFromLibrary((PatchBasket)JSLDesktop.getSelectedFrame());
   crossBreeder.generateNewPatch();
   Patch p=crossBreeder.getCurrentPatch();
   try {
   l1.setText("Patch Type: "+ p.getDevice().getManufacturerName()+" "+
	      p.getDevice().getModelName()+" "+
	      p.getDriver().getPatchType());
   play();
   } catch (Exception e) {/*already taken care off-- we just don't want this thrown any farther*/}
}
void play()
 {
  Patch p=crossBreeder.getCurrentPatch();
  if (p==null) return;
  p.getDriver().sendPatch(p);
  p.getDriver().playPatch(p);
 }

}

