package core;
import javax.swing.JComponent;
import javax.swing.JTable;

class PatchListTransferHandler extends PatchTransferHandler {
    protected Patch getSelectedPatch(JComponent c) {
	try {
	    JTable t = (JTable)c;
	    AbstractPatchListModel m = 
		(AbstractPatchListModel)t.getModel();
	    return m.getPatchAt(t.getSelectedRow());
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	    return null;
	}
    }
    protected boolean storePatch(Patch p, JComponent c) {
	try {
	    p.chooseDriver();
	    JTable t = (JTable)c;
	    AbstractPatchListModel m =
		(AbstractPatchListModel)t.getModel();
	    m.addPatch(p);
	    return true;
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	    return false;
	}
    }
}