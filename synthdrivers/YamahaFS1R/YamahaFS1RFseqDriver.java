package synthdrivers.YamahaFS1R;
import core.Driver;
import core.JSLFrame;
import core.ParamModel;
import core.Patch;
import core.SysexHandler;
import core.SysexSender;

/**
	Formant sequence driver for FS1R.
	Size of sysex depends of frame format which can be 128, 256, 384 or 512 bytes long.
	It gives sizes from 6443 to 25643.
 * @author     Denis Queffeulou mailto:dqueffeulou@free.fr
 * @version    $Id$
 */
public class YamahaFS1RFseqDriver extends Driver
{
	/** header parameters size */
	static final int FSEQHEADER_SIZE = 0x20;
	
	static final int FRAME_SIZE = 50;
	
	/** start of data in sysex */
	static final int DATA_START = 9;
	
	/** offset without sysex header */
	static final int PATCHNAME_OFFSET = DATA_START;
	
	/** number of characters in patch name */
	static final int PATCHNAME_SIZE = 8;

	/** size of header begin + end */
	static final int HEADER_SIZE = 11;
	
	private static String mLabels[] = new String[] { 
		"1","2","3","4","5","6"}; 

	private int mCurrentBank;
	
	/**
	 */
	public YamahaFS1RFseqDriver()
	{
		super ("FSeq","Denis Queffeulou");
		sysexID = "F043005E****6*00";
		patchSize = 0;	// variable
		patchNameStart = PATCHNAME_OFFSET;	
		patchNameSize = PATCHNAME_SIZE;
		deviceIDoffset = -1;
		checksumStart = 4;
//		checksumEnd = PATCH_AND_HEADER_SIZE - 3;
//		checksumOffset = PATCH_AND_HEADER_SIZE - 2;
		sysexRequestDump=new SysexHandler("F0 43 20 5E 60 00 *patchNum* F7");
		bankNumbers = new String[]{"Current", "Internal"};
		patchNumbers = mLabels;
	}


	/**
		@param bankNum 0..1
	*/
	public void setBankNum (int bankNum) {
		mCurrentBank = bankNum;
		sysexRequestDump = new SysexHandler("F0 43 20 5E 6"+bankNum+" 00 *patchNum* F7");
	}
	
    public void setPatchNum (int patchNum)
    {
		// il ne faut pas envoyer de prog change
    }



	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public Patch createNewPatch()
	{/*
		byte[] sysex = new byte[PATCH_AND_HEADER_SIZE];
		sysex[0] = (byte) 0xF0;
		sysex[1] = (byte) 0x0F;
		sysex[2] = (byte) 0x05;
		sysex[3] = (byte) 0x00;
		sysex[4] = (byte) 0x00;
		sysex[5] = (byte) 0x0B;	
		sysex[PATCH_AND_HEADER_SIZE-1] = (byte) 0xF7;
		Patch p = new Patch(sysex);
		p.ChooseDriver();
		p.getDriver().setPatchName(p, "Untitled");
		return p;
*/
		return null;
	}


	/**
	 *  Description of the Method
	 *
	 *@param  p  Description of the Parameter
	 *@return    Description of the Return Value
	 */
	public JSLFrame editPatch(Patch p)
	{
		return new YamahaFS1RFseqEditor((Patch)p);
	}


	/**
		get fundamental pitch values. The unit is 5Hz from 0 to 925.
	*/
	static int[] getPitch(Patch p) {
		int oNbFrames = getNbFrames(p);
		int oPitch[] = new int[oNbFrames];
		for (int i = 0; i < oNbFrames; i++) {
			int off = FSEQHEADER_SIZE + DATA_START + i*FRAME_SIZE;
			oPitch[i] = (int)(p.sysex[off+1] + (p.sysex[off] << 7));
//			System.out.println("pitch "+i+" off "+off+" = "+oPitch[i]);
		}
		return oPitch;
	} 

	/**
		get operator (un)voiced frequencies
		@param aOp operator 0..7
	*/
	static int[] getFrequencies(Patch p, int aOp, boolean aVoiced) {
		int oNbFrames = getNbFrames(p);
		int oRet[] = new int[oNbFrames];
		int offset = 0x1A;
		if (aVoiced) {
			offset = 2;
		}
		for (int i = 0; i < oNbFrames; i++) {
			int off = FSEQHEADER_SIZE + DATA_START + i*FRAME_SIZE + offset + aOp;
			oRet[i] = (int)(p.sysex[off+8] + (p.sysex[off] << 7));
//			System.out.println("voiced freq "+i+" off "+off+" = "+oRet[i]);
		}
		return oRet;
	} 
	
	static int[] getLevels(Patch p, int aOp, boolean aVoiced) {
		int oNbFrames = getNbFrames(p);
		int oRet[] = new int[oNbFrames];
		int offset = 0x2A;
		if (aVoiced) {
			offset = 0x12;
		}
		for (int i = 0; i < oNbFrames; i++) {
			int off = FSEQHEADER_SIZE + DATA_START + i*FRAME_SIZE + offset + aOp;
			oRet[i] = (int)(p.sysex[off]);
		}
		return oRet;
	} 


	static int getNbFrames(Patch p) {
		Model oFrameModel = new Model(p, 0x1B);
		int oNbFrames = 128;
		if (oFrameModel.get() == 1) oNbFrames = 256;
		if (oFrameModel.get() == 2) oNbFrames = 384;
		if (oFrameModel.get() == 3) oNbFrames = 512;
		return oNbFrames;
	}

	static class Sender extends SysexSender
	{
		protected int parameter;
		protected byte []b = new byte [10];
		/**
			FSeq Header parameter
		 */
		Sender(int param) {
			parameter=param;
			b[0]=(byte)0xF0;
			b[1]=(byte)0x43;
			b[2]=(byte)0x10;
			b[3]=(byte)0x5E;
			b[4]= (byte)(0x70);
			b[5]=(byte)(0);
			b[6]=(byte)(parameter & 0x7F);
			b[9]=(byte)0xF7;
		}
		public byte [] generate (int value) {
			b[7]=(byte)((value >> 7)&127);
			b[8]=(byte)(value&127);
			return b;
		}
	}
	
	static class Model extends ParamModel
	{
		Model(Patch p,int offset) {
			super(p, (offset & 127) + DATA_START);
		}
		public void set(int i) {
			patch.sysex[ofs] = (byte)(i & 127);
		}
		public int get() {
			return patch.sysex[ofs];
		}
	}

	static class DoubleModel extends Model
	{
		DoubleModel(Patch p,int offset) {
			super(p, offset);
		}
		public void set(int i) {
			patch.sysex[ofs] = (byte)((i >> 7) & 127);
			patch.sysex[ofs+1] = (byte)(i & 127);
		}
		public int get() {
			int oVal = patch.sysex[ofs+1] + (patch.sysex[ofs] << 7);
			return oVal;
		}
	}
	
	
}

