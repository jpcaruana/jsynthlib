package synthdrivers.AlesisQS;

import core.Patch;
import core.ParamModel;

/**
 * Param model AlesisQS keyboards.  Allows changing of specific ranges of bits
 * in the patch, taking into account the compression scheme.
 * @author Zellyn Hunter (zjh, zellyn@zellyn.com)
 * @version $Id$
 */

public class QSParamModel extends ParamModel
{
	private int msBit;
	private int bitSize;
    private boolean signed;
    private int offset;
	// Patch patch defined in parent

	/**
	 * Create a new param model, given the starting and ending bytes and bits
	 * of the parameter.  Assumes the standard QS header of 7 bytes before
	 * counting bytes.
	 * @param patch the underlying patch to modify
	 * @param msByte the starting (most significant) byte
	 * @param msBit the starting (most significant) bit (0-7)
	 * @param lsByte the ending (least significant) byte
	 * @param lsBit the ending (least significant) bit (0-7)
	 * @param signed true if the value is signed, false otherwise
	 */
	public QSParamModel(Patch patch, int msByte, int msBit,
						int lsByte, int lsBit, boolean signed)
	{
		// count # of usable bits in the patch
		int patchBits = (patch.sysex.length - QSConstants.HEADER) * 7;

		if (lsByte<0)
			throw new IllegalArgumentException("lsByte byte must be >= 0");
		if ((msBit<0) || (msBit>7))
			throw new IllegalArgumentException("msBit must be 0-7");
		if ((lsBit<0) || (lsBit>7))
			throw new IllegalArgumentException("lsBit must be 0-7");
		if ((msByte<lsByte) || ((msByte==lsByte) && (msBit<lsBit)))
			throw new IllegalArgumentException("Most significant < least");
		if (msByte*8+msBit>patchBits-1)
			throw new IllegalArgumentException("msByte > sysex length");

		this.patch = patch;
		this.msBit = msByte * 8 + msBit;
		this.bitSize = this.msBit - (lsByte * 8 + lsBit) + 1;
		this.signed = signed;
        this.offset = 0;
	}

	/**
	 * Create a new param model, given the starting bit and the size (in bits)
	 * of the parameter.  Assumes the standard QS header of 7 bytes before
	 * counting bits
	 * @param patch the underlying patch to modify
	 * @param msBit the starting (most significant) bit
	 * @param bitSize the number of bits in the parameter
	 * @param offset the value to add/subtract as given in sysex spec
	 */
	public QSParamModel(Patch patch, int msBit, int bitSize,
						int offset)
	{
		// count # of usable bits in the patch
		int patchBits = (patch.sysex.length - QSConstants.HEADER) * 7;

		if (msBit-bitSize+1<0)
			throw new IllegalArgumentException("Starting bit must be >= 0");
		if (bitSize<=0)
			throw new IllegalArgumentException("Bit size must be > 0");
		if (msBit>patchBits-1)
			throw new IllegalArgumentException("msBit > sysex length");

		this.patch = patch;
		this.msBit = msBit;
		this.bitSize = bitSize;
		this.offset = offset;
        this.signed = false;
	}

	/**
	 * Given an integer representing a new value for the parameter,
	 * set the corresponding bits in the actual patch
	 * @param value the new value for the parameter
	 */
	public void set(int value) {
		// Function header is followed by one normal byte, which is either
		// 0 (for global dump), or the program #, effect #, etc.
		SysexRoutines.setBits(value-offset, this.patch.sysex, QSConstants.HEADER,
							  this.msBit, this.bitSize);
	}

	/**
	 * Return an integer representing the current value of the parameter
	 * from the corresponding bits in the actual patch
	 * @return the value of the parameter
	 */
	public int get() {
		// Function header is followed by one normal byte, which is either
		// 0 (for global dump), or the program #, effect #, etc.
		//System.out.println("msBit=" + this.msBit +
		//				   ", bitSize=" + this.bitSize +
		//				   ", signed=" + this.signed);
		int retVal = offset +
			SysexRoutines.getBits(this.patch.sysex, QSConstants.HEADER,
									 this.msBit, this.bitSize, false);

		//System.out.println("Returning: " + retVal);
		return retVal;
	}

}
