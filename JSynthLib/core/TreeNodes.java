/*
 * Copyright 2003 Hiroo Hayashi
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

/**
 * TreeNodes.java
 *
 * Provides a Tree data structure for TreeNodeWidget class.
 *
 * Created: Thu Aug 07 22:20:43 2003
 *
 * @author <a href ="mailto:hiroo.hayashi@computer.org">Hiroo Hayashi</a>
 * @version $Id$
 * @see synthdrivers/RolandTD6/Instrument.java
 */

public interface TreeNodes {

    // Example of tree data structure:
    //
    // Object[] root  =
    //  { "root",
    //    "leaf 0", // { 0 }
    //    "leaf 1", // { 1 }
    //    new Object[] { "branch 2",
    //                   "leaf 2-0",   // { 2, 0 }
    //                   "leaf 2-1" }, // { 2, 1 }
    //    new Object[] { "branch 3",
    //                   new Object[] { "branch 3-0",
    //                                  "leaf 3-0-0" }, // { 3, 0, 0 }
    //                   "leaf 3-1" }, // { 3, 1 }
    //    "leaf 4" }; // { 4 }
    //
    // Assume the node value of leaf node '2-1' is '48',
    //   'getIndices(48)' returns an array '{ 2, 1 }'.
    //   'getValue( { 2, 1 } )' returns '48'.

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

} // TreeNodes
