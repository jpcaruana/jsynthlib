/* JSynthLib - a Universal Synthesizer / Patch Editor in Java
   Copyright (C) 2000-2002  Brian Klock et al.

	This program is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.

Brian Klock- jsynthlib@overwhelmed.org
*/


import core.*;
public class JSynthLib
{

    public static void main(String[] args) {
        ErrorMsg.debug = 0;
	if (args.length > 0) try {
	  ErrorMsg.debug=Integer.parseInt(args[0]);
	} catch (NumberFormatException e) {}
	PatchEdit frame = new PatchEdit();
        //frame.setVisible(true);
   }
}
