package synthdrivers.YamahaFS1R;

import java.awt.Dimension;

import javax.swing.JTable;

import core.BankEditorFrame;
import core.JSLFrame;
import core.Patch;
import core.PatchBasket;
import core.PatchEditorFrame;

/**
	Specific bank editor for YamahaFS1R. This bank holds 128 voices + 128 performances.
	@author denis queffeulou mailto:dqueffeulou@free.fr
*/
public class YamahaFS1RBankEditor extends BankEditorFrame
{
    {
	preferredScrollableViewportSize = new Dimension(100, 100);
	autoResizeMode = JTable.AUTO_RESIZE_OFF;
	preferredColumnWidth = 130;
    }

    public YamahaFS1RBankEditor (Patch p)
    {
		super(p);
    }

	/**
		Edit a patch without select it. This allow the performance to edit
		a patch from the bank.
		@param aPart performance part number 1..4
	*/
    public JSLFrame EditPatch (int aNumPatch, int aPart) // This is not called. OK?
    {
        Patch p = bankDriver.getPatch (bankData, aNumPatch);
        if (p==null) {
			return null;
		}
        PatchEditorFrame pf= (PatchEditorFrame)(YamahaFS1RVoiceDriver.getInstance().editPatch(p, aPart, aNumPatch-128));
        //pf.setBankEditorInformation (this,table.getSelectedRow (),table.getSelectedColumn ());
		pf.setBankEditorInformation(this, aNumPatch % YamahaFS1RBankDriver.NB_ROWS, aNumPatch/YamahaFS1RBankDriver.NB_ROWS);
        return pf;
    }

    public Patch getBankPatch() {
	return bankData;
    }
}
