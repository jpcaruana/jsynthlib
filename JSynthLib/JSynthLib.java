/*
 * JSynthLib - a Universal Synthesizer / Patch Editor in Java Copyright (C)
 * 2000-2004 Brian Klock et al.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 675 Mass
 * Ave, Cambridge, MA 02139, USA.
 * 
 * Brian Klock- jsynthlib@overwhelmed.org
 */

import java.util.ArrayList;

import core.PatchEdit;

public class JSynthLib {

    private static int debugLevel = 0;
    private static ArrayList fileList = new ArrayList();

    public static void main(String[] args) {
        parseArgument(args);
        PatchEdit frame = new PatchEdit(fileList, debugLevel);
        //frame.setVisible(true);
    }

    private static void parseArgument(String[] args) {
        try {
            for (int i = 0; i < args.length; i++) {
                if (args[i].startsWith("-D")) {
                    // may cause Illegal Index Exception
                    debugLevel = Integer.parseInt(args[++i]);
                } else if (args[i].startsWith("-h")) {
                    usage(0);
                } else if (args[i].startsWith("-")) {
                    usage(1);
                } else {
                    fileList.add(args[i]);
                }
            }
        } catch (Exception e) {
            usage(1);
        }
    }

    private static void usage(int status) {
        System.err.println("usage: java JSynthLib [-D number] [filename...]");
        System.err.println("    -D number\tset debugging flags (argument is a bit mask)");
        System.err.println("\t1\tmisc");
        System.err.println("\t2\tdump stack");
        System.err.println("\t4\tMIDI");
        System.err.println("\t8\tframe");
        System.err.println("\t...");
        System.err.println("\t-1\tall");
        System.exit(status);
    }
}