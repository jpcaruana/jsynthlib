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

    public IPatch[] extractPatch(IPatch p) {
        return (IPatch[]) extractPatch((Patch) p);
    }

    abstract public Patch[] extractPatch(Patch p);
}
