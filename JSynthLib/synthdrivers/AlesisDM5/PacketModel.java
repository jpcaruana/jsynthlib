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

import core.*;

class PacketModel extends BitModel {
    private final static int PACKET_SIZE = 5;
    private int packetIndex;
    
    /** Constructs a PacketModel. */
    PacketModel(Patch p, int offset, byte mask, int packetIndex) {
        super(p, offset, mask);
        this.packetIndex = packetIndex;
    }
    
    /** Constructs a PacketModel. */
    PacketModel(Patch p, int offset, byte mask) {
        super(p, offset, mask);
        this.packetIndex = 0;
    }
    
    /** Constructs a PacketModel. */
    PacketModel(Patch p, int offset, byte mask, byte test) {
        this(p, offset, mask, 0);
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
    
    void setPacketIndex(int packetIndex) {
        this.packetIndex = packetIndex;
    }
}