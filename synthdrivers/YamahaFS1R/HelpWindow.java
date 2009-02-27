package synthdrivers.YamahaFS1R;

import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.Document;

import core.Actions;
import core.PatchEdit;


class HelpWindow extends Actions.MenuFrame
{
	HelpWindow() {
		super(PatchEdit.getDesktop(),"Yamaha FS1R Editor Help",true,true,true,true,false);
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
