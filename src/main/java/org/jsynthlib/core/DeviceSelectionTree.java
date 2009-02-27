package org.jsynthlib.core;

/**
 * Created by IntelliJ IDEA.
 * User: jemenake
 * Date: Feb 28, 2005
 * Time: 5:23:02 AM
 */

import javax.swing.JTree;
import javax.swing.tree.*;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.Collection;


public class DeviceSelectionTree extends JTree {

    // These are constants used for telling this class how to organize the tree
    // "Type" is just the first letter of the manuf.
    public static final int GROUP_NONE = 0; // No ordering. Just a flat list of devices
    public static final int GROUP_MANUF = 1; // Devices are grouped by manufacturer
    public static final int GROUP_TYPE = 2; // Devices are grouped by type
    public static final int GROUP_TYPE_MANUF = 3; // Devices are grouped by type, then manufacturer
    public static final int GROUP_MANUF_TYPE = 4; // Devices are grouped by manufacturer, then type

    private int groupStyle = GROUP_MANUF;   //There aren't that many mfrs yet.

    /**
     * This initializes the tree and populates it will all of the devices known to DevicesConfig
     */
    public DeviceSelectionTree() {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Devices");
        switch(groupStyle) {
            case GROUP_MANUF:
            case GROUP_MANUF_TYPE:
                rootNode.setUserObject("Manufacturers");
        }
        
        setModel(new DefaultTreeModel(rootNode));
        setSelectionModel(new DeviceSelectionModel()); // Prevent user from selecting anything but a device

        // Built the tree according to the groupStyle
        buildTree(rootNode);

        // Expand the first level so that we don't just see a single folder
        expandRow(0);
    }

    /**
     * This method gets a list of all known DeviceDescriptors and then passes them to either
     * buildTreeNoGrouping(), buildTreeByManufacturer(), or buildTreeByType(), depending upon the groupStyle.
     * @param node The node of the tree to add the items to
     */
    private void buildTree(DefaultMutableTreeNode node) {
        Collection alldevicedescriptors = PatchEdit.devConfig.getDeviceDescriptors();

        switch(groupStyle) {
            case GROUP_NONE:
                buildTreeNoGrouping(alldevicedescriptors, node);
                break;
            case GROUP_MANUF:
            case GROUP_MANUF_TYPE:
                buildTreeByManufacturer(alldevicedescriptors, node);
                break;
            case GROUP_TYPE:
            case GROUP_TYPE_MANUF:
                buildTreeByType(alldevicedescriptors, node);
                break;
            default:
                break; //TODO: Throw an exception here. Emenaker 2005-03-03
        }

    }

    /**
     * This method merely adds all items of the collection to the provided tree node. No grouping is done.
     * @param col The collection of DeviceDescriptors to add
     * @param node The node of the tree to add the items to
     */
    private void buildTreeNoGrouping(Collection col, DefaultMutableTreeNode node) {
        for(Iterator i=col.iterator(); i.hasNext();) {
            DeviceDescriptor descriptor = (DeviceDescriptor) i.next();
            node.add(new DefaultMutableTreeNode(descriptor.getDeviceName()));
        }
    }


    /**
     * This method takes a collection of DeviceDescriptors and makes sub-nodes for all manufacturers of the
     * devices in the collection. If groupStyle is GROUP_MANUF_TYPE, then it has buildTreeByType sub-group the
     * devices even further by type.
     * @param col The collection of DeviceDescriptors to add
     * @param node The node of the tree to add the items to
     */
    private void buildTreeByManufacturer(Collection col, DefaultMutableTreeNode node) {
        // Cycle through all of the manufacturers found in this colleciton
        for(Iterator i=getManufacturersInCollection(col).iterator(); i.hasNext();) {
            // Create a node for this manufacturer...
            String manufacturer = i.next().toString();
            DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(manufacturer);
            node.add(subNode);
            // Get a sub-collection of descriptors for this manufacturer
            Collection subCol = filterDescriptorsByManufacturer(col,manufacturer);
            // Now, if we're grouping by Manufacturer or by Type,Manufacturer, then this is the last
            // step in the grouping. Just add the whole sub-collection to this node. Otherwise, if we're
            // grouping by Manufacturer,Type, then we need to group this sub-collection by type. Emenaker 2005-03-04
            switch(groupStyle) {
                case GROUP_MANUF_TYPE:
                    buildTreeByType(subCol,subNode);
                    break;
                default:
                    buildTreeNoGrouping(subCol, subNode);
                    break;
            }
        }
    }

    /**
     * This method takes a collection of DeviceDescriptors and makes sub-nodes for all types of the devices
     * in the collection. If groupStyle is GROUP_TYPE_MANUF, then it has buildTreeByManufacturer sub-group the
     * devices even further by manufacturer.
     * @param col The collection of DeviceDescriptors to add
     * @param node The node of the tree to add the items to
     */
    private void buildTreeByType(Collection col, DefaultMutableTreeNode node) {
        // Cycle through all of the types found in this colleciton
        for(Iterator i=getTypesInCollection(col).iterator(); i.hasNext();) {
            // Create a node for this type...
            String type = i.next().toString();
            DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(type);
            node.add(subNode);
            // Get a sub-collection of descriptors for this type
            Collection subCol = filterDescriptorsByType(col,type);
            // Now, if we're grouping by Type or by Manufacturer,Type, then this is the last
            // step in the grouping. Just add the whole sub-collection to this node. Otherwise, if we're
            // grouping by Type,Manufacturer, then we need to group this sub-collection by manufacturer. Emenaker 2005-03-04
            switch(groupStyle) {
                case GROUP_TYPE_MANUF:
                    buildTreeByManufacturer(subCol,subNode);
                    break;
                default:
                    buildTreeNoGrouping(subCol, subNode);
                    break;
            }
        }
    }

    /**
     * Returns a sorted collection of manufacturers of the devices in a collection
     * @param col The collection of DeviceDescriptors
     * @return A sorted Collection of Strings containing the manufacturers
     */
    private Collection getManufacturersInCollection(Collection col) {
        TreeSet manufacturers = new TreeSet();
        for(Iterator i=col.iterator(); i.hasNext();) {
            manufacturers.add(((DeviceDescriptor) i.next()).getManufacturer());
        }
        return(manufacturers);
    }

    /**
     * Returns a sorted collection of types of the devices in a collection
     * @param col The collection of DeviceDescriptors
     * @return A sorted Collection of Strings containing the types
     */
    private Collection getTypesInCollection(Collection col) {
        TreeSet types = new TreeSet();
        for(Iterator i=col.iterator(); i.hasNext();) {
            types.add(((DeviceDescriptor) i.next()).getType());
        }
        return(types);
    }

    /**
     * Returns a sorted collection of DeviceDescriptors with the manufacturer <code>manufacturer</code>
     * @param col The Collection of DeviceDescriptors to filter
     * @param manufacturer The manufacturer name
     * @return The sorted, filtered Collection of DeviceDescriptors
     */
    private Collection filterDescriptorsByManufacturer(Collection col, String manufacturer) {
        TreeSet treeset = new TreeSet();
        for(Iterator i=col.iterator(); i.hasNext();) {
            DeviceDescriptor descriptor = (DeviceDescriptor) i.next();
            if(descriptor.getManufacturer().equals(manufacturer)) {
                treeset.add(descriptor);
            }
        }
        return(treeset);
    }

    /**
     * Returns a sorted collection of DeviceDescriptors of the type <code>type</code>
     * @param col The Collection of DeviceDescriptors to filter
     * @param type The manufacturer name
     * @return The sorted, filtered Collection of DeviceDescriptors
     */
    private Collection filterDescriptorsByType(Collection col, String type) {
        TreeSet treeset = new TreeSet();
        for(Iterator i=col.iterator(); i.hasNext();) {
            DeviceDescriptor descriptor = (DeviceDescriptor) i.next();
            if(descriptor.getType().equals(type)) {
                treeset.add(descriptor);
            }
        }
        return(treeset);
    }

    /**
     * This returns the DeviceName of the currently-selected value in the tree
     * @return The currently-selected DeviceName
     */
    public String getSelectedValue() {
        return(getSelectionPath().getLastPathComponent().toString());
    }

    /**
     * This class overrides a normal DefaultTreeSelectionModel used by JTrees. This is to prevent the user
     * from being able to select any node that isn't a *leaf* (ie, an actual devicename). This way, the user
     * can't select manufacturer and type "folders". Emenaker 2005-03-03
     */
    class DeviceSelectionModel extends DefaultTreeSelectionModel {
        DeviceSelectionModel() {
            setSelectionMode(DefaultTreeSelectionModel.SINGLE_TREE_SELECTION);
        }

        public void addSelectionPath(TreePath path) {
            // This is called if the user tries "Ctrl-Click" or any other multiple-selection keystroke
            // Since we don't allow multiple selections, we'll just do setSelectionPath
            setSelectionPath(path);
         }

        public void addSelectionPaths(TreePath[] paths) {
            // We don't allow multiple selections right now - Emenaker 2005-03-03
        }

        /**
         * This overrides the superclass' setSelectionPath. We need to figure out if the user
         * clicked on a leaf (ie, an actual drivernname) or a non-leaf (ie, a type or manufacturer)
         * We don't want to let them select non-leafs - Emenaker 2005-03-03
         */
        public void setSelectionPath(TreePath path) {
            if(((TreeNode) path.getLastPathComponent()).isLeaf()) {
                super.setSelectionPath(path);
            } else {
                clearSelection();
            }
        }
    }
}
