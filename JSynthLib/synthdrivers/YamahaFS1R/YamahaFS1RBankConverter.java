package synthdrivers.YamahaFS1R;

import core.*;

/**
	Convert from FS1R bank OS9 editor into JSynthLib bank format.
	@author Denis Queffeulou mailto:dqueffeulou@free.fr
	@version $Id$
*/
public class YamahaFS1RBankConverter extends Converter
{
	public YamahaFS1RBankConverter() 
	{
		super ("Bank Dump Converter","Denis Queffeulou");
		// en fait ce n'est pas un sysex donc je met le debut du fichier
		// sauf le premier caractere (F0) qui est mis en dur dans le code core...
		// Il ne semble pas prevu d'importer autre chose que des sysex !
		sysexID="F02E3*3*";
		patchSize = 129340;
	}
	
	public IPatch[] extractPatch (IPatch p) 
	{
		Patch oBank[] = new Patch[1];
		oBank[0] = importFSEditor(((Patch)p).sysex);
		return oBank;
	}
	
	Patch importFSEditor(byte[] aBuffer)
	{
		Patch oBank = (Patch)YamahaFS1RBankDriver.getInstance().createNewPatch();
		int oIndex = 26; 
		int oIDest = 0;//YamahaFS1RBankDriver.DATA_START;
		for (int p = 0; p < 128; p++)
		{
			oBank.sysex[oIDest++] = (byte)0xF0;
			oBank.sysex[oIDest++] = (byte)0x43;
			oBank.sysex[oIDest++] = (byte)0x0;
			oBank.sysex[oIDest++] = (byte)0x5E;
			int oCSStart = oIDest;
			oBank.sysex[oIDest++] = (byte)0x03;
			oBank.sysex[oIDest++] = (byte)0x10;
			oBank.sysex[oIDest++] = (byte)0x11;
			oBank.sysex[oIDest++] = (byte)0;
			oBank.sysex[oIDest++] = (byte)p;
			for (int b = 0; b < YamahaFS1RPerformanceDriver.PATCH_SIZE; b++)
			{
				oBank.sysex[oIDest++] = aBuffer[oIndex++];
			}
			int oCSEnd = oIDest-1;
			oIDest++;
			oBank.sysex[oIDest++] = (byte)0xF7; 
			calculateChecksum(oBank, oCSStart, oCSEnd, oCSEnd+1);
		}
		for (int v = 0; v < 128; v++)
		{
			oBank.sysex[oIDest++] = (byte)0xF0;
			oBank.sysex[oIDest++] = (byte)0x43;
			oBank.sysex[oIDest++] = (byte)0x0;
			oBank.sysex[oIDest++] = (byte)0x5E;
			int oCSStart = oIDest;
			oBank.sysex[oIDest++] = (byte)0x04;
			oBank.sysex[oIDest++] = (byte)0x60;
			oBank.sysex[oIDest++] = (byte)0x51;
			oBank.sysex[oIDest++] = (byte)0;
			oBank.sysex[oIDest++] = (byte)v;
			for (int b = 0; b < YamahaFS1RVoiceDriver.PATCH_SIZE; b++)
			{
				oBank.sysex[oIDest++] = aBuffer[oIndex++];
			}
			int oCSEnd = oIDest-1;
			oIDest++;
			oBank.sysex[oIDest++] = (byte)0xF7; 
			calculateChecksum(oBank, oCSStart, oCSEnd, oCSEnd+1);
		}
        return oBank;
	}
	
}
