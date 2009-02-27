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

import org.jsynthlib.core.Patch;

/**
 * This class handles bit manipulation for the pratial mute checkboxes which
 * operates on the same underlying data byte. The model is used by the
 * PartMuteParamModel for getting and setting bits and the PartMuteSender
 * for retrieving data to send.
 * 
 * @author Roger Westerlund
 */
class PartMuteDataModel extends D10ParamModel {

    public PartMuteDataModel(Patch patch, int offset) {
        super(patch, offset);
    }

    public int get(int bit) {
        return get() & (1 << bit);
    }

    public void set(int bit, int value) {
        if (value != 0) {
            super.set(super.get() | (1 << bit));
        } else {
            super.set(super.get() & ~(1 << bit));
        }
    }

    public int getData() {
        return super.get();
    }
}
