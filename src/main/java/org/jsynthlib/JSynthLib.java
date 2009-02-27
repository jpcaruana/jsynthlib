package org.jsynthlib;

import java.util.ArrayList;

import org.jsynthlib.core.PatchEdit;

public class JSynthLib {

	private static int debugLevel = 0;
	private static ArrayList fileList = new ArrayList();

	public static void main(String[] args) {
		parseArgument(args);
		PatchEdit frame = new PatchEdit(fileList, debugLevel);
		// frame.setVisible(true);
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
