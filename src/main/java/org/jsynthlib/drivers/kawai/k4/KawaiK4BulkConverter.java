package org.jsynthlib.drivers.kawai.k4;
import org.jsynthlib.core.Converter;
import org.jsynthlib.core.Patch;

/**
 * Convert a bulk patch into an array of single bank, multi bank,
 * drumset, and effect bank patches.
 * @version $Id$
 */
public class KawaiK4BulkConverter extends Converter {
    /** Header Size */
    private static final int HSIZE = 8;
    /** Single Bank size */
    private static final int SSIZE = 131 * 64;
    /** Multi Bank size */
    private static final int MSIZE = 77 * 64;
    /** Drum Set size */
    private static final int DSIZE = 682;
    /** Effect Bank size */
    private static final int ESIZE = 35 * 32;

    public KawaiK4BulkConverter() {
        super("Bulk Dump Converter", "Brian Klock & Gerrit Gehnen");
        sysexID = "F040**220004**00";
    }

    /**
     * Convert a bulk patch into an array of single bank, multi bank,
     * drumset, and effect bank patches.
     */
    public Patch[] extractPatch(Patch p) {
        byte[] sysex = p.getByteArray();
        // System.out.println("Length p: " + sysex.length);
        byte[] sx = new byte[HSIZE + SSIZE + 1]; // Single Bank
        byte[] mx = new byte[HSIZE + MSIZE + 1]; // Multi Bank
        byte[] dx = new byte[HSIZE + DSIZE + 1]; // Drumset
        byte[] ex = new byte[HSIZE + ESIZE + 1]; // Effect Bank

	// Copy the data into the Single Bank
        System.arraycopy(sysex, 0, sx, 0, HSIZE + SSIZE);
	// Copy the data into the Multi Bank
        System.arraycopy(sysex, 0, mx, 0, HSIZE);
        System.arraycopy(sysex, HSIZE + SSIZE,
			 mx, HSIZE, MSIZE);
        // Copy the data into the  drumset
        System.arraycopy(sysex, 0, dx, 0, HSIZE);
        System.arraycopy(sysex, HSIZE + SSIZE + MSIZE,
			 dx, HSIZE, DSIZE);
	// Copy the data into the Effect Bank
        System.arraycopy(sysex, 0, ex, 0, HSIZE);
        System.arraycopy(sysex, HSIZE + SSIZE + MSIZE + DSIZE,
			 ex, HSIZE, ESIZE);

        sx[HSIZE + SSIZE] = (byte) 0xF7;
        sx[3] = 0x21;
        sx[7] = 0x00;

        mx[HSIZE + MSIZE] = (byte) 0xF7;
        mx[3] = 0x21;
        mx[7] = 0x40;

        dx[HSIZE + DSIZE] = (byte) 0xf7;
        dx[3] = 0x21;
        dx[6] = 1;
        dx[7] = 0x20;

        ex[HSIZE + ESIZE] = (byte) 0xf7;
        ex[3] = 0x21;
        ex[6] = 1;

        Patch[] pf = new Patch[4];
	// use Patch(byte[], Driver) instead of Patch(byte[]). !!!FIXIT!!!
        pf[0] = new Patch(sx);
        pf[1] = new Patch(mx);
        pf[2] = new Patch(ex);
        pf[3] = new Patch(dx);

        return pf;
    }
}
