/*
 * Copyright 2003, 2004 Hiroo Hayashi
 *
 * This file is part of JSynthLib.
 *
 * JSynthLib is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or(at your option) any later version.
 *
 * JSynthLib is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JSynthLib; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 */

package core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;
import javax.swing.tree.*;

/**
 * A SysexWidget implementing JTree component.<p>
 *
 * See synthdrivers.RolandTD6.TD6SingleEditor as an example.<p>
 *
 * This widget is still under development. The inferface may be
 * changed. If you would like to use this in you driver, contact the
 * author.  It will save your time.
 *
 * Created: Sun Jun 22 13:53:35 2003
 *
 * @author <a href ="mailto:hiroo.hayashi@computer.org">Hiroo Hayashi</a>
 * @version $Id$
 */
public class TreeWidget extends SysexWidget {
    private JTree tree;
    private Nodes treeNodes;
    private DefaultMutableTreeNode rootNode;

    //Optionally play with line styles.  Possible values are
    //"Angled", "Horizontal", and "None"(the default).
    private final boolean playWithLineStyle = false;
    private final String lineStyle = "Angled";

    /**
     * Creates a new <code>TreeWidget</code> instance.
     *
     * @param label not used in the current implementation.
     * @param patch a <code>Patch</code>, which is edited.
     * @param treeNodes a <code>TreeWidget.Nodes</code> object specifying the
     * tree data structure.
     * @param paramModel a <code>ParamModel</code> value
     * @param sysexString SysexSender for transmitting the value at
     * editing the parameter.
     * @see SysexWidget
     * @see Nodes
     */
    public TreeWidget(String label, Patch patch, Nodes treeNodes,
		      ParamModel paramModel, SysexSender sysexString) {
	super(label, patch, paramModel, sysexString);
	this.treeNodes = treeNodes;
	setup();
    } // TreeWidget constructor

    private void setup() {
	//Create the nodes.
	rootNode = populate(treeNodes.getRoot());

	//Create a tree that allows one selection at a time.
	tree = new JTree(rootNode);
	tree.getSelectionModel().setSelectionMode
	    (TreeSelectionModel.SINGLE_TREE_SELECTION);

	//Listen for when the selection changes.
	tree.addTreeSelectionListener(new TreeSelectionListener() {
		public void valueChanged(TreeSelectionEvent e) {
		    DefaultMutableTreeNode node
			= (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		    if (node != null && node.isLeaf()) {
			/*
			ErrorMsg.reportStatus("TreeSelectionLister: "
					      + e.getPath());
			int[] tmp = getIndices(e.getPath());
			for (int i = 0; i < tmp.length; i++)
			    System.out.println(tmp[i]);
			*/
			sendSysex(treeNodes.getValue(getIndices(e.getPath())));
		    } else {
			return;
		    }
		}
	    });

	if (playWithLineStyle) {
	    tree.putClientProperty("JTree.lineStyle", lineStyle);
	}

	// disable default file folder icon
	// Can anyone provide any fancy icons?
	DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer();
	//renderer.setLeafIcon(new ImageIcon("images/middle.gif"));
	//renderer.setOpenIcon(null);
	//renderer.setClosedIcon(null);
	renderer.setLeafIcon(null);
	tree.setCellRenderer(renderer);

	//Create the scroll pane and add the tree to it.
	JScrollPane treeScrollPane = new JScrollPane(tree);
	// consider to add Dimension parameter to all SysexWidget classes
	// !!!FIXIT!!!
	treeScrollPane.setPreferredSize(new Dimension(180, 320));
	add(treeScrollPane, BorderLayout.CENTER);

	// set selection
	//setSelection(getValue());
    }

    /**
     * Small routine that will make node out of the first entry in the
     * array, then make nodes out of subsequent entries and make them
     * child nodes of the first one. The process is repeated recursively
     * for entries that are arrays.
     * This routine is borrowed from <a href=
     * "http://www.apl.jhu.edu/~hall/java/Swing-Tutorial/Swing-Tutorial-JTree.html">
     * Quick Swing Tutorial for AWT Programmers</a>.
     *
     * @param root a tree structure given via <code>treeNode</code>
     * constructor parameter.
     * @return a <code>DefaultMutableTreeNode</code> value
     */
    protected DefaultMutableTreeNode populate(Object[] root) {
	DefaultMutableTreeNode node = new DefaultMutableTreeNode(root[0]);
	DefaultMutableTreeNode child;
	for (int i = 1; i < root.length; i++) {
	    Object nodeSpecifier = root[i];
	    if (nodeSpecifier instanceof Object[] )  // Ie node with children
		child = populate((Object[] ) nodeSpecifier);
	    else
		child = new DefaultMutableTreeNode(nodeSpecifier); // Ie Leaf
	    node.add(child);
	}
	return(node);
    }

    /**
     * Convert <code>TreePath</code> data to <code>int[] </code> data.
     *
     * @param path a <code>TreePath</code> value
     * @return an <code>int[] </code> value.  Each entry is the index of
     * TreeNode in <code>path</code>.
     */
    protected int[] getIndices(TreePath path) {
	int[] indices = new int[path.getPathCount() - 1];
	TreeNode parent =(TreeNode) path.getPathComponent(0);
	for (int i = 0; i < indices.length; i++) {
	    TreeNode child =  (TreeNode) path.getPathComponent(i + 1);
	    indices[i] = parent.getIndex(child);
	    parent = child;
	}
	return indices;
    }

    /**
     * Convert <code>int[] </code> data to <code>TreePath</code> data.
     *
     * @param indices an <code>int[] </code> value
     * @return a <code>TreePath</code> value
     */
    protected TreePath getTreePath(int indices[] ) {
	TreePath path = tree.getPathForRow(0);
	for (int i = 0; i < indices.length; i++) {
	    TreeNode node =(TreeNode) path.getLastPathComponent();
	    path = path.pathByAddingChild(node.getChildAt(indices[i]));
	    ErrorMsg.reportStatus("getTreePath" + path);
	}
	return path;
    }

    /**
     * Select a tree node specified by <code>n</code>.
     *
     * @param n an <code>int</code> value specifing a tree node.
     * @see Nodes#getIndices
     */
    protected void setSelection(int n) {
	TreePath path = getTreePath(treeNodes.getIndices(n));
	tree.setSelectionPath(path);
	// Make sure the user can see the lovely new node.
	tree.scrollPathToVisible(path);
    }

    /**
     * Return a tree node object specified by <code>n</code>.
     *
     * @param n an <code>int</code> value specifing a tree node.
     * @return an <code>Object</code> value
     * @see Nodes#getIndices
     */
    public Object getNode(int n) {
	return getNode(treeNodes.getIndices(n));
    }

    protected Object getNode(int indices[] ) {
	TreeNode node = rootNode;
	for (int i = 0; i < indices.length; i++)
	    node = node.getChildAt(indices[i]);
	return node;
    }

    /**
     * Set a value and Select the corresponding tree node.
     *
     * @param value an <code>int</code> value to be set.
     */
    public void setValue(int value) {
	super.setValue(value);
	setSelection(value);
    }

    public void setEnabled(boolean e) {
        tree.setEnabled(e);
    }

    /**
     * Structured data for Tree Widget.
     * Example of tree data:
     * <pre>
     * Object[] root  =
     *  { "root",
     *    "leaf 0", // { 0 }
     *    "leaf 1", // { 1 }
     *    new Object[] { "branch 2",
     *                   "leaf 2-0",   // { 2, 0 }
     *                   "leaf 2-1" }, // { 2, 1 }
     *    new Object[] { "branch 3",
     *                   new Object[] { "branch 3-0",
     *                                  "leaf 3-0-0" }, // { 3, 0, 0 }
     *                   "leaf 3-1" }, // { 3, 1 }
     *    "leaf 4" }; // { 4 }
     * </pre>
     * Assume the node value of leaf node '2-1' is '48',
     *   'getIndices(48)' returns an array '{ 2, 1 }'.
     *   'getValue( { 2, 1 } )' returns '48'.
     */
    public interface Nodes {

	/**
	 * Returns tree strucutre.  Used by TreeWidget.
	 *
	 * @return an array of a tree structure.
	 */
	Object[] getRoot();

	/**
	 * Returns an array of indices which specifies a node whose value is
	 * <code>n</code>.
	 *
	 * @param n a node value
	 * @return an array of indices.
	 */
	int[] getIndices(int n);
	/**
	 * Returns a value of a node which is specified by an array of indices.
	 *
	 * @param indices an array of indices.
	 * @return a node value
	 */
	int getValue(int[] indices);

    } // Nodes
} // TreeWidget
