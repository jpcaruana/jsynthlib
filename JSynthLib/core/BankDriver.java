package core;
import java.io.*;
import javax.swing.*;

/**This is the base class for all Bank / Bulk Drivers*/
public class BankDriver extends Driver {
    /**The Number of Patches the Bank holds*/
    public int numPatches;
    /**How many columns to use when displaying the patches as a table*/
    public int numColumns;
    /**The Sysex header for the patches which go in this bank*/
    public String singleSysexID;
    /**The size of the patches which go in this bank*/
    public int    singleSize;

    /**Most Banks have no name*/
    public void setPatchName(Patch p, String name) {
    }

    /**Most Banks have no name*/
    public String getPatchName(Patch p) {
	return "-";
    }

    /**Store the bank to a given bank on the synth. Ignores the patchNum
       parameter. Should probably be overridden in most drivers*/
    public void storePatch(Patch p, int bankNum, int patchNum) {
	setBankNum(bankNum);
	sendPatchWorker(p);
    }

    public void sendPatch(Patch p) {
	JOptionPane.showMessageDialog(null,
				      "You can not send bank data (use store)",
				      "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**Chooses which bank to put the patch into*/
    public void choosePatch(Patch p) {
	int bank = 0;
	if (bankNumbers.length == 1) {
	    storePatch(p, 0, 0);
	    return;
	}
	try {
	    String bankstr = (String) JOptionPane.showInputDialog(null, "Please Choose a Bank", "Storing Patch",
								  JOptionPane.QUESTION_MESSAGE, null, bankNumbers, bankNumbers[0]);
	    if (bankstr == null)
		return;
	    for (int i = 0; i < bankNumbers.length; i++)
		if (bankstr.equals(bankNumbers[i])) bank = i;
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	}
	ErrorMsg.reportStatus("BankDriver:ChoosePatch  Bank = " + bank);
	storePatch(p, bank, 0);
    }

    /**Puts a patch into the bank, converting it as needed*/
    public void putPatch(Patch  bank, Patch p, int patchNum) {
    }

    /**Gets a patch from the bank, converting it as needed*/
    public Patch getPatch(Patch bank, int patchNum) {
	return null;
    }

    /**Get the name of the patch at the given number*/
    public String getPatchName(Patch p, int patchNum) {
	return "-";
    }

    /**Banks cannot play*/
    public void playPatch(Patch p) {
	JOptionPane.showMessageDialog(null,
				      "Can not Play Banks, only individual patches.",
				      "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void setPatchName(Patch p, int patchNum, String name) {
    }

    /**Creates an editor window to edit this bank*/
    public JInternalFrame editPatch(Patch p) {
	BankEditorFrame frm = new BankEditorFrame(p);
	return frm;
    }

    /**Compares the header & size of a Single Patch to this driver to
       see if this bank can hold the patch*/
    public boolean canHoldPatch(Patch p) {         
        if ((singleSize != p.sysex.length) && (singleSize != 0))
	    return false;

        StringBuffer patchString =  p.getPatchHeader();

        StringBuffer driverString = new StringBuffer(singleSysexID);
	for (int j = 0; j < driverString.length(); j++)
	    if (driverString.charAt(j) == '*')
		driverString.setCharAt(j, patchString.charAt(j));
	return (driverString.toString().equalsIgnoreCase(patchString.toString().substring(0, driverString.length())));
    }

    public void deletePatch(Patch p, int patchNum) {
	setPatchName(p, patchNum, "          ");
    }
}
