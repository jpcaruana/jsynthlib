/*
 * JSynthlib-Device for Yamaha DX7 Mark-I (with Firmware IG114690)
 * =====================================================================
 * @author  Torsten Tittmann
 * file:    YamahaDX7Device.java
 * date:    20.05.2002
 * @version 0.1
 *
 * Copyright (C) 2002  Torsten.Tittmann@t-online.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package synthdrivers.YamahaDX7;
import core.*;

public class YamahaDX7Device extends Device
{
    
    /** Creates new YamahaDX7Device */
    public YamahaDX7Device ()
    {
        manufacturerName="Yamaha";
        modelName="DX7";
        synthName="DX7";
        infoText="JSynthLib supports single/bank librarian and voice editing functions on a "+
                 "Yamaha DX7 Mark-I synthesizer.\n"+
                 "\n"+
                 "DX7 - GENERAL INFORMATION\n"+
                 "Because of the MIDI implementation of the firmware of the early units "+
                 "(especially the ActiveSensing- and the SysEx-handling), problems will occur with"+
                 " an early DX7 in cooperation with Patch Libraries/Editors.\n"+
                 "It's advisable to upgrade early firmwares to a newer version.\n"+
                 "Only the DX7-I in an original state is tested; no expansion cards (like E!Card"+
                 " or other) or other models of the DX7 series.\n"+
                 "\n"+
                 "The original DX7-I transmits all MIDI data on channel 1. Only the receive channel"+
                 " is variable. It's recommended to choose channel 1 for both.\n"+
                 "To enable transmitting or receiving of SysEx messages, the memory protection must"+
                 " be turned off and the sys info must be available.\n"+
                 "A received voice patch is held in an edit buffer, but not stored permanently.\n"+
                 "\n"+
                 "DRIVER\n"+
                 "The patch location chooser at the `Patch-> Get' and the `Patch-> Store' function"+
                 " doesn't effect anything!\n"+
                 "\n"+
                 "VOICE EDITOR\n"+
                 "Only those parameters are implemented, which are stored in the patch."+
                 " So, you won't find any function parameter like Pitchband, Portamento, etc.\n"+
                 "There is only one exception: the OPERATOR ON/OFF buttons, because they are"+
                 " usefull for programming.\n"+
                 "At time only the direction JSynthLib->DX7 is working. If a parameter is changed on"+
                 " the DX7 itself, JSynthlib doesn't become aware of this.";

        addDriver (new YamahaDX7BankDriver ());
        addDriver (new YamahaDX7SingleDriver ());
    }
    
}
