/*
 * Copyright 2004 Hiroo Hayashi
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
 * Interface for a Bank Patch.
 * 
 * @author Hiroo Hayashi
 * @version $Id$
 */
public interface IBankPatch extends IPatch {
    /** returns the number of single patches the bank holds. */
    int getNumPatches();

    /** returns number of columns in bank editor frame. */
    int getNumColumns();

    /**
     * Check a patch if it is for the bank patch and put it into the
     * bank.
     */
    void put(IPatch singlePatch, int patchNum);

    /**
     * Delete a Single patch in a bank patch. A recommented implementation is
     * putting a default patch created by a single driver, and put a null name.
     */
    void delete(int patchNum);

    /** Gets a patch from the bank, converting it as needed. */
    ISinglePatch get(int patchNum);

    /** Get the name of the patch at the given number <code>patchNum</code>. */
    String getName(int patchNum);

    /** Set the name of the patch at the given number <code>patchNum</code>. */
    void setName(int patchNum, String name);
}