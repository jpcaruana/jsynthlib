package core;

/**
 * This is a special subclass of Driver which simply converts any
 * patches it's associated with to another format. The Driver
 * supporting the new Driver format must come later in the Driver
 * Chain.
 * @see Device#addDriver(Driver)
 * @author ???
 * @version $Id$
 */
abstract public class Converter extends Driver {
    public Converter(String patchType, String authors) {
	super(patchType, authors);
    }

    public Converter() {
	this("Converter", "JSynthLib"); // Who is the auther?
    }

    /** Convert a Patch into an array of Patches. */
    // called by Patch.dissect().
    abstract public Patch[] extractPatch(Patch p);

    // comment out since there is no difference from Driver.supportsPatch
    /*
    protected boolean supportsPatch (StringBuffer patchString,Patch p) {
        Integer intg = new Integer (0);

        if ((patchSize != p.sysex.length) && (patchSize != 0))
	    return false;

        StringBuffer driverString = new StringBuffer(sysexID);
        if (patchString.length() < driverString.length())
	    return false;
        for (int j = 0; j < driverString.length (); j++)
            if (driverString.charAt(j) == '*')
		driverString.setCharAt(j, patchString.charAt(j));
        return (driverString.toString().equalsIgnoreCase(patchString.toString().substring(0, driverString.length())));
    }
    */
}
