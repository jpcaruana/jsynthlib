package core;

import javax.swing.*;
import java.util.*;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
public class WaitDialog extends JDialog {
 
   public WaitDialog(JFrame Parent) {
	   
        super(Parent,"Please wait while the operation is completed",false);
               setSize(350,24);
	centerDialog();
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

}


