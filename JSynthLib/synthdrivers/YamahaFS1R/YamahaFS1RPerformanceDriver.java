package synthdrivers.YamahaFS1R;

import core.*;

import java.io.*;

/**
 *  Multi driver for FS1R. Multi are named Performance on FS1R.
 * @author     Denis Queffeulou mailto:dqueffeulou@free.fr
 * @version $Id$
 */
public class YamahaFS1RPerformanceDriver extends Driver
{
	/** size of patch without header */
	static final int PATCH_SIZE = 400;
	
	/** common + effects parameters size */
	static final int COMMON_SIZE = 192;
	
	/** effects parameters size */
	static final int EFFECTS_SIZE = 112;
	
	/** part parmeters */
	static final int PART_SIZE = 52;
	
	/** start of data in sysex */
	static final int DATA_START = 9;
	
	/** offset without sysex header */
	static final int PATCHNAME_OFFSET = DATA_START;
	
	/** number of characters in patch name */
	static final int PATCHNAME_SIZE = 12;

	/** size of header begin + cs + end */
	static final int HEADER_SIZE = 11;

	/** size of all */
	static final int PATCH_AND_HEADER_SIZE = PATCH_SIZE+HEADER_SIZE;
	
	private static final String mLabels128[] = new String[] { 
		"1","2","3","4","5","6","7",
		"8","9","10","11","12","13","14","15",
        "16","17","18","19","20","21","22","23",
		"24","25","26","27","28","29","30","31",
		"32","33","34","35","36","37","38","39",
		"40","41","42","43","44","45","46","47",
		"48","49","50","51","52","53","54","55",
		"56","57","58","59","60","61","62","63",
		"64","65","66","67","68","69","70","71",
		"72","73","74","75","76","77","78","79",
		"80","81","82","83","84","85","86","87",
		"88","89","90","91","92","93","94","95",
		"96","97","98","99","100","101","102","103",
		"104","105","106","107",
		"108","109","110","111","112","113","114","115",
		"116","117","118","119","120","121","122","123",
		"124","125","126","127", "128"}; 

	private static final byte mInitPerf[] = new byte[] {

(byte)0xF0, 0x43, 0x00, 0x5E, 0x03, 0x10, 0x11, 0x00, 0x2F, 0x49, 0x6E, 0x69, 0x74, 0x50, 0x65, 0x72,
0x66, 0x72, 0x6D, 0x6E, 0x63, 0x00, 0x00, 0x00, 0x00, 0x7F, 0x40, 0x18, 0x00, 0x00, 0x00, 0x01,
0x00, 0x07, 0x68, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00,
0x40, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
0x00, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x00, 0x11, 0x00, 0x0A, 0x00, 0x09, 0x00,
0x18, 0x00, 0x31, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x22, 0x04, 0x45, 0x0A, 0x40,
0x00, 0x1A, 0x05, 0x0D, 0x03, 0x27, 0x08, 0x27, 0x08, 0x00, 0x4A, 0x00, 0x64, 0x00, 0x03, 0x00,
0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1A, 0x00, 0x40, 0x00, 0x2E, 0x00,
0x40, 0x00, 0x40, 0x00, 0x22, 0x00, 0x40, 0x00, 0x32, 0x00, 0x40, 0x00, 0x13, 0x00, 0x34, 0x00,
0x32, 0x00, 0x40, 0x00, 0x40, 0x00, 0x16, 0x00, 0x40, 0x00, 0x30, 0x00, 0x40, 0x00, 0x00, 0x00,
0x00, 0x01, 0x40, 0x40, 0x14, 0x40, 0x40, 0x40, 0x15, 0x40, 0x28, 0x00, 0x7F, 0x40, 0x0C, 0x07,
0x00, 0x40, 0x22, 0x07, 0x40, 0x36, 0x07, 0x00, 0x00, 0x04, 0x01, 0x00, 0x00, 0x00, 0x01, 0x00,
0x00, 0x18, 0x40, 0x40, 0x7F, 0x40, 0x40, 0x40, 0x00, 0x7F, 0x7F, 0x00, 0x28, 0x00, 0x40, 0x40,
0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x02, 0x32, 0x42,
0x3E, 0x32, 0x00, 0x01, 0x7F, 0x00, 0x01, 0x40, 0x40, 0x00, 0x00, 0x00, 0x00, 0x04, 0x01, 0x00,
0x11, 0x7F, 0x01, 0x00, 0x00, 0x18, 0x40, 0x40, 0x7F, 0x40, 0x40, 0x40, 0x00, 0x7F, 0x7F, 0x00,
0x28, 0x00, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40,
0x40, 0x02, 0x32, 0x42, 0x3E, 0x32, 0x00, 0x01, 0x7F, 0x00, 0x01, 0x40, 0x40, 0x00, 0x00, 0x00,
0x00, 0x04, 0x01, 0x00, 0x11, 0x7F, 0x01, 0x00, 0x00, 0x18, 0x40, 0x40, 0x7F, 0x40, 0x40, 0x40,
0x00, 0x7F, 0x7F, 0x00, 0x28, 0x00, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40,
0x40, 0x40, 0x40, 0x40, 0x40, 0x02, 0x32, 0x42, 0x3E, 0x32, 0x00, 0x01, 0x7F, 0x00, 0x01, 0x40,
0x40, 0x00, 0x00, 0x00, 0x00, 0x04, 0x01, 0x00, 0x11, 0x7F, 0x01, 0x00, 0x00, 0x18, 0x40, 0x40,
0x7F, 0x40, 0x40, 0x40, 0x00, 0x7F, 0x7F, 0x00, 0x28, 0x00, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40,
0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x02, 0x32, 0x42, 0x3E, 0x32, 0x00, 0x01,
0x7F, 0x00, 0x01, 0x40, 0x40, 0x00, 0x00, 0x00, 0x00, 0x76, (byte)0xF7
	};

	private int mCurrentBankNum;

	private static YamahaFS1RPerformanceDriver mSingleton;
	

	/**
	 *  Constructor for the YamahaFS1RPerformanceDriver object
	 */
	public YamahaFS1RPerformanceDriver()
	{
		super ("Performance","Denis Queffeulou");
		sysexID = "F043005E03101*00";
		patchSize = PATCH_AND_HEADER_SIZE;
		patchNameStart = PATCHNAME_OFFSET;	
		patchNameSize = PATCHNAME_SIZE;
		deviceIDoffset = -1;
		checksumStart = 4;
		checksumEnd = PATCH_AND_HEADER_SIZE - 3;
		checksumOffset = PATCH_AND_HEADER_SIZE - 2;
		sysexRequestDump=new SysexHandler("F0 43 20 5E 11 00 *patchNum* F7");
		bankNumbers = new String[]{"Internal", "Current performance"};
		patchNumbers = mLabels128;
		
		mSingleton = this;
	}



	public static YamahaFS1RPerformanceDriver getInstance() {
		return mSingleton;
	}


    public void setBankNum (int bankNum)
    {
		mCurrentBankNum = bankNum;
//		System.out.println("setBankNum = "+ bankNum);
		updateSysexRequest();  
    }


    public void setPatchNum (int patchNum)
    {
		// il ne faut pas envoyer de prog change
    }


    public void sendPatch(IPatch p) {
	super.sendPatch(p);
    }
	
    /**
       Met a jour la requete selon le type de banque.
    */
    private void updateSysexRequest() {
	if (mCurrentBankNum == 1) { 
	    // current
	    sysexRequestDump = new SysexHandler("F0 43 20 5E 10 00 00 F7");
	}
	else {	
	    // internal
	    sysexRequestDump = new SysexHandler("F0 43 20 5E 11 00 *patchNum* F7");
	}
    }


    /**Sends a patch to a set location on a synth.*/
    public void storePatch (IPatch p, int bankNum,int patchNum)
    {
		// change the address to internal performance
		((Patch)p).sysex[6] = (byte)0x11;
		((Patch)p).sysex[7] = (byte)0;
		((Patch)p).sysex[8] = (byte)patchNum;
		calculateChecksum(p); 
        sendPatch (p);
    }



	/**
		@param p a bank patch
		@param aPatchOffset offset of performance in patch sysex
	*/
	String getPatchName(Patch p, int aPatchOffset) {
        if (patchNameSize==0) return ("-");
        try
        {
            String s = new String (p.sysex, aPatchOffset+patchNameStart, patchNameSize, "US-ASCII");
            return s;
        } 
		catch (UnsupportedEncodingException ex)
        {return "-";}
	}


	/**
		@param p a bank patch
		@param aPatchOffset offset of performance in patch sysex
	*/
    public void setPatchName (Patch p, String name, int aPatchOffset)
    {
        if (name.length ()<patchNameSize) name=name+"            ";
        byte [] namebytes = new byte [64];
        try
        {
            namebytes=name.getBytes ("US-ASCII");
            for (int i=0;i<patchNameSize;i++)
                p.sysex[aPatchOffset+patchNameStart+i]=namebytes[i];
            
        } catch (UnsupportedEncodingException ex)
        {return;}
    }


	/**
	 *  Description of the Method
	 *
	 *@return    Description of the Return Value
	 */
	public IPatch createNewPatch()
	{
		byte[] sysex = new byte[PATCH_AND_HEADER_SIZE];
		initPatch(sysex, 0);
		IPatch oPatch = new Patch(sysex, this);
		return oPatch;
	}
	
	static void initPatch(byte[] sysex, int aOffset) {
		for (int i = 0; i < PATCH_AND_HEADER_SIZE; i++) {
			sysex[aOffset+i] = mInitPerf[i];
		}
	}


	/**
	 *  Return editor window for performance
	 *
	 *@param ip  data of the performance
	 *@return    Description of the Return Value
	 */
	public JSLFrame editPatch(IPatch ip)
	{
		Patch p = (Patch)ip;
		// set the address to "current performance" so when patch is sent
		// the FS1R display show this "in edit" performance whenever it comes from.
		p.sysex[6] = (byte)0x10;
		p.sysex[7] = (byte)0;
		p.sysex[8] = (byte)0;
		calculateChecksum(p);
		return new YamahaFS1RPerformanceEditor((Patch)p);
	}

	

	static class Sender extends SysexSender
	{
		protected int parameter;
		protected byte []b = new byte [10];
		protected int mPart = 0;
		/**
			Common parameter
		 */
		Sender(int param) {
			parameter=param;
			b[0]=(byte)0xF0;
			b[1]=(byte)0x43;
			b[2]=(byte)0x10;
			b[3]=(byte)0x5E;
			b[4]= (byte)(0x10);
			b[5]=(byte)((parameter >> 8) & 0x7F);
			b[6]=(byte)(parameter & 0x7F);
			b[9]=(byte)0xF7;
		}
		/**
			Part parameter
			@param aPart number 1..4
		 */
		Sender(int param, int aPart) {
			this(param);
			b[4]= (byte)(0x30 + aPart -1);
			b[5]= 0;
			mPart = aPart;
		}
		public byte [] generate (int value) {
			b[7]=(byte)((value >> 7)&127);
			b[8]=(byte)(value&127);
			return b;
		}
	}

	static class Model extends ParamModel
	{
		protected int mPart = 0;
		protected boolean mIs16Bits;
		protected int mOffset;
		/**
		*	Parametre common
		 @param offset dans la table (7bits MSB + 7bits LSB)
		 */
		Model(Patch p,int offset) {
			// l'offset indique dans la doc ne correspond pas a un offset lineaire
			// dans le sysex, il faut recalculer la valeur sur 16bits lineaires
			super(p, (offset & 127) + ((offset >> 1) & 0x3F80) + DATA_START);
			//System.out.println("offset 0x"+Integer.toHexString(offset)+"  => "+((offset & 127) + ((offset >> 1) & 0x3F80) + DATA_START));
			// les parametres d'effets sont en 16 bits
			mIs16Bits = (offset >= 0x50 && offset < 0x60) || (offset >= 0x68 && offset < 0x0128);
			mOffset = offset;
		}

		/**
			*	Parametre de part
		 @param offset dans la table
		 @param aPart part number 1..4
		 */
		Model(Patch p,int offset, int aPart) {
			this(p, offset);
			mPart = aPart;
			if (aPart > 0) {
				ofs = COMMON_SIZE + PART_SIZE*(mPart-1) + offset + DATA_START;
			}
		}
		public void set(int i) {
			if (mIs16Bits)
			{
				patch.sysex[ofs] = (byte)((i >> 7) & 127);
				patch.sysex[ofs+1] = (byte)(i & 127);
			}
			else
			{
				patch.sysex[ofs] = (byte)(i & 127);
			}
			//System.out.println("set part = "+mPart+" ofs = "+Integer.toHexString(mOffset)+" valL = "+Integer.toHexString(patch.sysex[ofs])+" valH = "+Integer.toHexString(patch.sysex[ofs+1]));
		}

		public int get() {
			int oRet = patch.sysex[ofs];
			if (mIs16Bits)
			{
				oRet = patch.sysex[ofs+1] | (patch.sysex[ofs] << 7);
			}
			//System.out.println("get part = "+mPart+" ofs = "+Integer.toHexString(mOffset)+" = "+oRet);
			return oRet;
		}
	}

	static class BitModel extends Model
	{
		private int mMask;
		private int mShift;
		/**
			Peut servir pour common ou part selon le decalage et le numero
		 de part (0 pour common).
		 @param aPart part number 0,1..4
		 @param offset decalage soit sur common soit sur part
		 */
		BitModel(Patch p,int offset, int aPart, int aMask, int aShift) {
			super(p, offset, aPart);
			mMask = aMask;
			mShift = aShift;
			if (aPart > 0) {
				ofs = COMMON_SIZE+PART_SIZE*(mPart-1) + offset + DATA_START;
			}
		}
		public void set(int i) {
			patch.sysex[ofs] &= ~mMask;
			patch.sysex[ofs] |= (i << mShift);
		}
		public int get() {
			return (patch.sysex[ofs] & mMask) >> mShift;
		}
	}

	static class BitSender extends Sender
	{
		private Patch mPatch;
		/** Common parameter */
		BitSender(Patch aPatch, int param) {
			super(param);
			mPatch = aPatch;
		}
		/**
			Part parameter
		 @param aPart number 1..4
		 */
		BitSender(Patch aPatch, int param, int aPart) {
			super(param, aPart);
			mPatch = aPatch;
		}
		public byte [] generate (int value) {
			// on recupere la valeur directement dans le patch
			// DATA_START est le decalage de l'entete sysex
			int oValue = mPatch.sysex[parameter + DATA_START];
			if (mPart > 0) {
				oValue = mPatch.sysex[COMMON_SIZE + PART_SIZE*(mPart-1) + parameter + DATA_START];
			}
			b[7]=0;
			b[8]=(byte)(oValue&127);
			return b;
		}
	}
}



