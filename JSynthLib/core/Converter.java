package core;

/**
 * This is a special subclass of Driver which simply converts any
 * patches it's associated with to another format. The Driver
 * supporting the new Driver format must come later in the Driver
 * Chain.
 * @see Device#addDriver(IDriver)
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

    public IPatch[] createPatch(byte[] sysex) {
        Patch patch = new Patch(sysex, this);
        Patch[] patarray = extractPatch(patch);
        if (patarray == null)
            return new Patch[] {patch};
        // Conversion was sucessfull, we have at least one
        // converted patch. Assign a proper driver to each patch of patarray
        Device dev = getDevice();
        for (int i = 0; i < patarray.length; i++) {
            String patchString = patarray[i].getPatchHeader();
            for (int jdrv = 0; jdrv < dev.driverCount(); jdrv++) {
                IPatchDriver drv = (IPatchDriver) dev.getDriver(jdrv);
                if (drv.supportsPatch(patchString, patarray[i].getByteArray()))
                    patarray[i].setDriver(drv);
            }
        }
        return patarray;
    }

    abstract public Patch[] extractPatch(Patch p);
}
