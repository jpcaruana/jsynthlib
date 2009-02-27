/*
 * Copyright 2006 Roger Westerlund
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
package synthdrivers.RolandD10;

import org.jsynthlib.core.SysexWidget.IParamModel;

/**
 * This class acts as a param model for each of the four partial mute
 * checkboxes. Since they all operate on the same byte, they share a
 * common data model which manipulates the data.
 * 
 * @author Roger Westerlund
 */
class PartMuteParamModel implements IParamModel {

    private int bit;
    private PartMuteDataModel dataModel;

    /**
     * Constructor.
     * 
     * @param part
     *      The partial this param model handles.
     * @param dataModel
     *      The model responsible for the underlying data manipulation.
     */
    public PartMuteParamModel(int part, PartMuteDataModel dataModel) {
        this.bit = part - 1;
        this.dataModel = dataModel;
    }

    public int get() {
        return dataModel.get(bit);
    }

    public void set(int value) {
        dataModel.set(bit, value);
    }
}
