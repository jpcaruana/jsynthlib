package core;
import javax.swing.JComponent;
import javax.swing.JTable;

class PatchGridTransferHandler extends PatchTransferHandler {
    protected Patch getSelectedPatch(JComponent c) {
	try {
	    JTable t = (JTable)c;
	    PatchGridModel m = 
		(PatchGridModel)t.getModel();
	    return m.getPatchAt(t.getSelectedRow(), t.getSelectedColumn());
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	    return null;
	}
    }
    protected boolean storePatch(Patch p, JComponent c) {
	try {
	    p.chooseDriver();
	    JTable t = (JTable)c;
	    PatchGridModel m =
		(PatchGridModel)t.getModel();
	    m.setPatchAt(p, t.getSelectedRow(), t.getSelectedColumn());
	    return true;
	} catch (Exception e) {
	    ErrorMsg.reportStatus(e);
	    return false;
	}
    }
}
