package synthdrivers.AlesisQS;

import core.Patch;
import core.ParamModel;

/**
 * Param model AlesisQS keyboards.  Allows changing of specific ranges of bits
 * in the patch, taking into account the compression scheme.
 * @author Zellyn Hunter (zjh, zellyn@zellyn.com)
 */

public class QSParamModel extends ParamModel
{
	private int startBit;
	private int bitSize;
	private boolean signed;
	// Patch patch defined in parent

	/**
	 * Create a new param model, given the starting bit and the size (in bits)
	 * of the parameter.  Assumes the standard QS header of 7 bytes before
	 * counting bits
	 * @param patch the underlying patch to modify
	 * @param startBit the starting bit
	 * @param bitSize the number of bits in the parameter
	 * @param signed true if the value is signed, false otherwise
	 */
	public QSParamModel(Patch patch, int startBit, int bitSize,
						boolean signed) {
		this.patch = patch;
		this.startBit = startBit;
		this.bitSize = bitSize;
		this.signed = signed;
	}

	/**
	 * Create a new param model, given the starting and ending bytes and bits
	 * of the parameter.  Assumes the standard QS header of 7 bytes before
	 * counting bytes.
	 * @param patch the underlying patch to modify
	 * @param startByte the starting byte
	 * @param startBit the starting bit (0-7, within the starting byte)
	 * @param endByte the ending byte
	 * @param endBit the ending bit (0-7, within the ending byte)
	 * @param signed true if the value is signed, false otherwise
	 */
	public QSParamModel(Patch patch, int startByte, int startBit,
						int endByte, int endBit, boolean signed) {
		this.patch = patch;
		this.startBit = startByte * 8 + startBit;
		this.bitSize = (endByte * 8 + endBit) - this.startBit + 1;
		this.signed = signed;
	}

	/**
	 * Given an integer representing a new value for the parameter,
	 * set the corresponding bits in the actual patch
	 * @param value the new value for the parameter
	 */
	public void set(int value) {
		SysexRoutines.setBits(value, this.patch.sysex, QSConstants.HEADER,
							  this.startBit, this.bitSize);
	}

	/**
	 * Return an integer representing the current value of the parameter
	 * from the corresponding bits in the actual patch
	 * @return the value of the parameter
	 */
	public int get() {
		return SysexRoutines.getBits(this.patch.sysex, QSConstants.HEADER,
									 this.startBit, this.bitSize, this.signed);
	}

} 
