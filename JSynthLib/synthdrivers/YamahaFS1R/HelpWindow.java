package synthdrivers.YamahaFS1R;

import core.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.net.*;
import java.io.*;


class HelpWindow extends JSLFrame
{
	HelpWindow() {
		super("Yamaha FS1R Editor Help",true,true,true,true);
		setSize(530, 400);
		JEditorPane jt = new JEditorPane();
		jt.setEditable(false);
		JScrollPane pane = new JScrollPane();
		pane.setBorder (BorderFactory.createLoweredBevelBorder());
		pane.getViewport().add(jt);
		getContentPane().add(pane);
		Document doc = jt.getDocument();
		try {
			URL url = getClass().getResource("help.html");
			jt.setPage(url);
		} 
		catch (IOException io) {
			System.err.println("Can't find help file "+io);
			jt.setDocument (doc);
		}
	}
	
}
