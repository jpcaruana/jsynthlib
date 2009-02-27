package org.jsynthlib.editorbuilder;
import javax.swing.*;
import javax.swing.tree.*;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;

import java.beans.*;
import java.io.*;

import java.util.HashMap;
import java.util.Enumeration;

public class ParameterFrame extends JFrame implements DragGestureListener,
DragSourceListener,
Serializable {
    protected JTree tree;
    protected DragSourceListener dsl;
    protected DefaultMutableTreeNode pnode;
    protected static HashMap parameters = new HashMap();
    
    public ParameterFrame() {
        super("Parameters");
        setSize(300,300);
        
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Widgets");
        createNodes(top);
        
        tree = new JTree( new DefaultTreeModel(top));
        tree.getSelectionModel().setSelectionMode
        (TreeSelectionModel.SINGLE_TREE_SELECTION);
        
        DragSource.getDefaultDragSource()
        .createDefaultDragGestureRecognizer( tree, 
                DnDConstants.ACTION_LINK,
                this );
        
        JScrollPane sp = new JScrollPane(tree, 
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        sp.setBorder(null);
        getContentPane().add(sp, BorderLayout.CENTER);
    }
    
    public static void main (String[] args) {
        final ParameterFrame pf = new ParameterFrame();
        pf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JMenuBar bar = new JMenuBar();
        bar.add(new JMenu(new AbstractAction ("Empty") {
            public void actionPerformed(ActionEvent e) {
                pf.empty();
            }
        }));
        pf.setJMenuBar(bar);
        pf.setVisible(true);
    }
    
    private void createNodes(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode special;
        DefaultMutableTreeNode node;
        String[][] nodes = new String[][] {
                {"strut", "Strut"},
                {"label", "Label"},
                {"button", "Button"},
                {"panel", "Panel"},
        };
        
        special = new DefaultMutableTreeNode("Special");
        top.add(special);
        for (int i = 0; i < nodes.length; i++) {
            node = new DefaultMutableTreeNode(nodes[i][1], false);
            special.add(node);
        }
        pnode = new DefaultMutableTreeNode("Parameters", true);
        top.add(pnode);
    }
    
    public void dragGestureRecognized(DragGestureEvent e) {
        TreePath path = tree.getSelectionPath();
        if (path == null)
            return;
        DefaultMutableTreeNode node = 
            (DefaultMutableTreeNode)path.getLastPathComponent();
        Object d = node.getUserObject();
        String data;
        if (d instanceof Parameter) {
            data = ((Parameter)d).getId();
        } else if (d instanceof String) {
            data = (String)d;
        } else {
            return;
        }
        // Get point for calculation image offset
        Point origin = e.getDragOrigin();
        Rectangle bounds = tree.getPathBounds(path);
        Point offset = new Point(bounds.x - origin.x, bounds.y - origin.y);
        // Create ghost image
        Component renderer = 
            tree.getCellRenderer().getTreeCellRendererComponent(tree, node, true,
                    false, true, 0,
                    false);
        
        renderer.setSize((int)bounds.getWidth(), (int)bounds.getHeight());
        BufferedImage ghost = new BufferedImage((int)bounds.getWidth(),
                (int)bounds.getHeight(),
                BufferedImage.TYPE_INT_ARGB_PRE);
        
        Graphics2D g2 = ghost.createGraphics();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.5f));
        renderer.paint(g2);
        g2.dispose();
        
        
        e.startDrag(DragSource.DefaultLinkDrop, ghost, offset,
                new StringSelection( data ),
                this);
    }
    
    public static Parameter getParameter(String address) {
        return (Parameter) parameters.get(address);
    }
    
    public void dragDropEnd(DragSourceDropEvent e) {}
    public void dragEnter(DragSourceDragEvent e) {}
    public void dragExit(DragSourceEvent e) {}
    public void dragOver(DragSourceDragEvent e) {}
    public void dropActionChanged(DragSourceDragEvent e) {}
    
    public void save(File file) {
        try {
            XMLEncoder e =
                new XMLEncoder(
                        new BufferedOutputStream(
                                new FileOutputStream(file)));
            e.writeObject(pnode);
            e.close();
        } catch (Exception e) { }
    }
    public void load(File file) {
        try {
            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            ParamLoadHandler h = new ParamLoadHandler();
            parser.parse(file, h);
            setParameterNode(h.getParamNode());
        } catch (Exception e) {
            file = null;
        }
    }
    public DefaultMutableTreeNode getParameterNode() {return pnode;}
    public void setParameterNode(DefaultMutableTreeNode node) {
        while (node.getChildCount() > 0) {
            DefaultMutableTreeNode child =
                (DefaultMutableTreeNode)node.getFirstChild();
            if (child.getUserObject() instanceof Parameter) {
                Parameter p = (Parameter)child.getUserObject();
                parameters.put(p.getId(), p);
            }
            if (child.getChildCount() > 0)
                findParameters(child);
            pnode.add(child);
        }
        ((DefaultTreeModel)tree.getModel()).reload();
    }
    protected void findParameters(DefaultMutableTreeNode node) {
        Enumeration e = node.children();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode child =
                (DefaultMutableTreeNode) e.nextElement();
            if (child.getUserObject() instanceof Parameter) {
                Parameter p = (Parameter)child.getUserObject();
                parameters.put(p.getId(), p);
            }
            if (child.getChildCount() > 0)
                findParameters(child);
        }	
    }
    public void empty() {
        pnode.removeAllChildren();
        ((DefaultTreeModel)tree.getModel()).reload();
    }
}
