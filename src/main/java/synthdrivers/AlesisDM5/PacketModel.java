/*
 * Copyright 2004 Jeff Weber
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

package synthdrivers.AlesisDM5;

import org.jsynthlib.core.Patch;

/** The PacketModel derives from the BitModel class and allows for editing of
* the individual note "packets" within a single drumset sysex record.
* Each "packet" represents all the parameters of a single note within the single
* drumset sysex record of which there are 60. The parameters for a single note
* are volume, panning, output, group, drum bank, drum number, fine tune and
* coarse tune. A single PacketModel can be used to edit one parameter for all 60
* notes in the sysex record. The current note being edited is given by packetIndex.
*
* @author Jeff Weber
*/
class PacketModel extends BitModel {
    private final static int PACKET_SIZE = 5;
    private int packetIndex;
    
    /** Constructs a PacketModel given the patch, the offset into the sysex
        * record, the mask representing the parameter, and the packetIndex.
        */
    PacketModel(Patch p, int offset, byte mask, int packetIndex) {
        super(p, offset, mask);
        this.packetIndex = packetIndex;
    }
    
    /** Constructs a PacketModel given the patch, the offset into the sysex
        * record, and the mask representing the parameter. The packetIndex is
        * defaulted to zero.
        */
    PacketModel(Patch p, int offset, byte mask) {
        super(p, offset, mask);
        this.packetIndex = 0;
    }
    
    /** Updates the bits defined by mask within the byte in the sysex record 
        * defined by offset with the value supplied by i.
        */
    public void set(int i) {
        patch.sysex[(PACKET_SIZE * packetIndex) + ofs] = (byte)((patch.sysex[(PACKET_SIZE * packetIndex) + ofs] & (~mask)) | ((i << power) & mask));
    }
    
    /** Gets the value of the bits defined by mask within the byte in the
        * sysex record defined by offset and returns the value as in int.
        */
    public int get() {
        int returnVal =  ((patch.sysex[(PACKET_SIZE * packetIndex) + ofs] & mask) >> power);
        return returnVal;
    }
    
    /** Sets the packetIndex
        */
    void setPacketIndex(int packetIndex) {
        this.packetIndex = packetIndex;
    }
}