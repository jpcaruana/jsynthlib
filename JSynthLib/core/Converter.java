package core;

/**
 * An implementation of IConverter interface for Patch class.
 * @author ???
 * @version $Id$
 */
abstract public class Converter extends Driver implements IConverter {
    public Converter(String patchType, String authors) {
        super(patchType, authors);
    }

    public Converter() {
        this("Converter", "JSynthLib"); // Who is the auther?
    }

    // If extractPatch returns an array of Patches whose drivers are set
    // properly, override this by;
    //    public IPatch[] createPatch(byte[] sysex) {
    //        return extractPatch(new Patch(sysex, this));
    //    }
    public IPatch[] createPatch(byte[] sysex) {
        Patch patch = new Patch(sysex, this);
        Patch[] patarray = extractPatch(patch);
        if (patarray == null)
            return new Patch[] {patch};
        // Conversion was sucessfull, we have at least one
        // converted patch. Assign a proper driver to each patch of patarray
        Device dev = getDevice();
        for (int i = 0; i < patarray.length; i++) {
            byte[] d = patarray[i].sysex;
            patarray[i].setDriver((IPatchDriver) DriverUtil.chooseDriver(d, dev));
        }
        return patarray;
    }

    /**
     * Convert a bulk patch into an array of single and/or bank patches.
     */
    abstract public Patch[] extractPatch(Patch p);
}
