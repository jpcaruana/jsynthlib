/*
 * Copyright 2004 Sander Brandenburg
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
package synthdrivers.RolandJV80;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import core.Device;
import core.DriverUtil;

/**
 * Copied the wavegroup data names from the jvtool project at sourceforge.net
 */

// if only this version of eclipse would support enums :/

/**
 * @author Sander Brandenburg
 * @version $Id$
 */
public class RolandJV80PatchBank {
    protected final static List bankList = new ArrayList();
    protected final static Map bankMap = new HashMap();
    
    public final static RolandJV80PatchBank INTERNAL = new RolandJV80PatchBank("Internal", 0,
            DriverUtil.generateNumbers(1, 64, "I-00"));

    public final static RolandJV80PatchBank CARD = new RolandJV80PatchBank("Card", 64,
            DriverUtil.generateNumbers(1, 64, "C-00"));

    public final static RolandJV80PatchBank PRESET_A = new RolandJV80PatchBank("Preset A", 128,
            new String[] { "A01: A.Piano 1", "A02: A.Piano 2",
                    "A03: Mellow Piano", "A04: Pop Piano 1",
                    "A05: Pop Piano 2", "A06: Pop Piano 3",
                    "A07: MIDIed Grand", "A08: Country Bar",
                    "A09: Glist EPiano", "A10: MIDI EPiano", "A11: SA Rhodes",
                    "A12: Dig Rhodes 1", "A13: Dig Rhodes 2",
                    "A14: Stiky Rhodes", "A15: Guitr Rhodes",
                    "A16: Nylon Rhodes", "A17: Clav 1", "A18: Clav 2",
                    "A19: Marimba", "A20: Marimba SW", "A21: Warm Vibe",
                    "A22: Vibe", "A23: Wave Bells", "A24: Vibro Bell",
                    "A25: Pipe Organ 1", "A26: Pipe Organ 2",
                    "A27: Pipe Organ 3", "A28: E.Organ 1", "A29: E.Organ 2",
                    "A30: Jazz Organ 1", "A31: Jazz Organ 2",
                    "A32: Metal Organ", "A33: Nylon Gtr 1",
                    "A34: Flanged Nyln", "A35: Steel Guitar",
                    "A36: PickedGuitar", "A37: 12 Strings",
                    "A38: Velo Harmnix", "A39: Nylon+Steel",
                    "A40: SwitchOnMute", "A41: JC Strat", "A42: Stratus",
                    "A43: Syn Strat", "A44: Pop Strat", "A45: Clean Strat",
                    "A46: Funk Gtr", "A47: Syn Guitar", "A48: Overdrive",
                    "A49: Fretless", "A50: St Fretless", "A51: Woody Bass 1",
                    "A52: Woody Bass 2", "A53: Analog Bs 1", "A54: House Bass",
                    "A55: Hip Bass", "A56: RockOut Bass", "A57: Slap Bass",
                    "A58: Thumpin Bass", "A59: Pick Bass", "A60: Wonder Bass",
                    "A61: Yowza Bass", "A62: Rubber Bs 1", "A63: Rubber Bs 2",
                    "A64: Stereoww Bs", });

    public final static RolandJV80PatchBank PRESET_B = new RolandJV80PatchBank("Preset B", 192,
            new String[] { "B01: Pizzicato", "B02: Real Pizz",
                    "B03: Harp", "B04: SoarinString", "B05: Warm Strings",
                    "B06: Marcato", "B07: St Strings", "B08: Orch Strings",
                    "B09: Slow Strings", "B10: Velo Strings",
                    "B11: BrightStrngs", "B12: TremoloStrng",
                    "B13: Orch Stab 1", "B14: Brite Stab", "B15: JP-8 Strings",
                    "B16: String Synth", "B17: Wire Strings",
                    "B18: New Age Vox", "B19: Arasian Morn", "B20: Beauty Vox",
                    "B21: Vento Voxx", "B22: Pvox Oooze", "B23: Glass Voices",
                    "B24: Space Ahh", "B25: Trumpet", "B26: Trombone",
                    "B27: Harmon Mute1", "B28: Harmon Mute2",
                    "B29: TeaJay Brass", "B30: Brass Sect 1",
                    "B31: Brass Sect 2", "B32: Brass Swell",
                    "B33: Brass Combo", "B34: Stab Brass", "B35: Soft Brass",
                    "B36: Horn Brass", "B37: French Horn", "B38: AltoLead Sax",
                    "B39: Alto Sax", "B40: Tenor Sax 1", "B41: Tenor Sax 2",
                    "B42: Sax Section", "B43: Sax Tp Tb", "B44: FlutePiccolo",
                    "B45: Flute mod", "B46: Ocarina", "B47: OverblownPan",
                    "B48: Air Lead", "B49: Steel Drum", "B50: Log Drum",
                    "B51: Box Lead", "B52: Soft Lead", "B53: Whistle",
                    "B54: Square Lead", "B55: Touch Lead", "B56: NightShade",
                    "B57: Pizza Hutt", "B58: EP+Exp Pad", "B59: JP-8 Pad",
                    "B60: Puff", "B61: SpaciosSweep", "B62: Big n Beefy",
                    "B63: RevCymBend", "B64: Analog Seq" });


    private final String name;

    private final int offset;

    private final String[] names;

    RolandJV80PatchBank(String name, int offset, String[] names) {
        this.name = name;
        this.offset = offset;
        this.names = names;

        bankList.add(name);
        bankMap.put(name, this);
    }

    public int getOffset() {
        return offset;
    }
    
    public String getName() {
        return name;
    }

    public String[] getPatches() {
        return names;
    }

    public static RolandJV80PatchBank getBank(String bankName) {
        return (RolandJV80PatchBank) bankMap.get(bankName);
    }
    
    public static RolandJV80PatchBank getBankByPatchNumber(int patchnr) {
        Iterator i = bankList.iterator();
        while (i.hasNext()) {
            String s = (String) i.next();
            RolandJV80PatchBank pb = getBank(s);
            if (pb.getOffset() + 64 > patchnr)
                return pb;
        }
        return null;
    }

    public static String[] getBanks(Device dev) {
        if (Boolean.valueOf(
                RolandJV80Device.getPref(dev, RolandJV80Device.PREF_DATA))
                .booleanValue()) {
            return new String[] { INTERNAL.name, CARD.name, PRESET_A.name,
                    PRESET_B.name };
        } 

        return new String[] { INTERNAL.name, PRESET_A.name, PRESET_B.name };
    }
}
