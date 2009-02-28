package org.jsynthlib.xml.editor;

import javax.swing.JPanel;

import org.jsynthlib.core.PatchEditorFrame;
import org.jsynthlib.xml.XMLPatch;



public class XMLEditor extends PatchEditorFrame {

	public XMLEditor(EditorDescription desc, XMLPatch patch) throws Exception {
		super("XML Editor", patch, (JPanel)desc.loadRoot(patch));
	}

}
