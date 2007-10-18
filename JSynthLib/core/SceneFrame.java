package core;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.EventObject;
import java.lang.reflect.InvocationTargetException;
import javax.swing.Timer;
import java.awt.event.ActionEvent;

import javax.sound.midi.SysexMessage;
import javax.sound.midi.InvalidMidiDataException;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.SwingUtilities;

import core.AbstractLibraryFrame.PatchTableModel;

//import core.SysexGetDialog.TimerActionListener;

/**
 *
 * @author Gerrit Gehnen
 * @version $Id$
 */
class SceneFrame extends AbstractLibraryFrame {
    private static int openFrameCount = 0;
    // column indices
    private static final int SYNTH      = 0;
    private static final int TYPE       = 1;
    private static final int PATCH_NAME = 2;
    private static final int BANK_NUM   = 3;
    private static final int PATCH_NUM  = 4;
    private static final int COMMENT    = 5;

    static final String FILE_EXTENSION= ".scenelib";
    private static final FileFilter FILE_FILTER = new Actions.ExtensionFilter(
            "PatchEdit Scene Files (*" + FILE_EXTENSION + ")", FILE_EXTENSION);
    private static final PatchTransferHandler pth = new SceneListTransferHandler();

    SceneFrame(File file) {
        super(file.getName(), "Scene", pth);
    }

    SceneFrame() {
        super("Unsaved Scene #" + (++openFrameCount), "Scene", pth);
    }

    PatchTableModel createTableModel() {
        return new SceneListModel(/* false */);
    }

    void setupColumns() {
        SceneTableCellEditor rowEditor = new SceneTableCellEditor();

        TableColumn column = null;
        column = table.getColumnModel().getColumn(SYNTH);
        column.setPreferredWidth(150); // wirski@op.pl
        column = table.getColumnModel().getColumn(TYPE);
        column.setPreferredWidth(100); // wirski@op.pl
        column = table.getColumnModel().getColumn(PATCH_NAME);
        column.setPreferredWidth(150); // wirski@op.pl
        column = table.getColumnModel().getColumn(BANK_NUM);
        column.setPreferredWidth(150); // wirski@op.pl
        // Set the special pop-up Editor for Bank numbers
        column.setCellEditor(rowEditor);
        column = table.getColumnModel().getColumn(PATCH_NUM);
        column.setPreferredWidth(100); // wirski@op.pl
        // Set the special pop-up Editor for Patch Numbers
        column.setCellEditor(rowEditor);
        column = table.getColumnModel().getColumn(COMMENT);
        column.setPreferredWidth(200);
    }

    void frameActivated() {
        Actions.setEnabled(false, Actions.EN_ALL);

        // always enabled
        Actions.setEnabled(true,
                Actions.EN_GET | Actions.EN_IMPORT
                | Actions.EN_IMPORT_ALL | Actions.EN_NEW_PATCH);
        enableActions();
    }

    /** change state of Actions based on the state of the table. */
    void enableActions() {
        // one or more patches are included.
        Actions.setEnabled(table.getRowCount() > 0,
                Actions.EN_SAVE
                | Actions.EN_SAVE_AS | Actions.EN_SEARCH
                | Actions.EN_TRANSFER_SCENE | Actions.EN_UPDATE_SCENE);

        // one or more patches are selected
        Actions.setEnabled(table.getSelectedRowCount() > 0,
                Actions.EN_DELETE | Actions.EN_UPDATE_SELECTED);

        Actions.setEnabled(table.getSelectedRowCount() == 1,
                Actions.EN_COPY | Actions.EN_EDIT // wirski@op.pl
                | Actions.EN_CUT | Actions.EN_EXPORT | Actions.EN_REASSIGN
                | Actions.EN_STORE | Actions.EN_UPLOAD);

        // one single patch is selected
        Actions.setEnabled(table.getSelectedRowCount() == 1
                && myModel.getPatchAt(table.getSelectedRow()).isSinglePatch(),
                Actions.EN_SEND | Actions.EN_SEND_TO | Actions.EN_PLAY);

        // enable paste if the clipboard has contents.
        Actions.setEnabled(Toolkit.getDefaultToolkit().getSystemClipboard()
                .getContents(this) != null, Actions.EN_PASTE);
    }

    // begin PatchBasket methods
    public ArrayList getPatchCollection() {
        ArrayList ar = new ArrayList();
        for (int i = 0; i < myModel.getRowCount(); i++)
            ar.add(myModel.getPatchAt(i));
        return ar;
    }
    // end of PatchBasket methods

    /**
     * Send all patches of the scene to the configured places in the synth's.
     */
    void sendScene() {
        //     ErrorMsg.reportStatus("Transfering Scene");
        for (int i = 0; i < myModel.getRowCount(); i++) {
            Scene scene = ((SceneListModel) myModel).getSceneAt(i);
            scene.getPatch().send(scene.getBankNumber(), scene.getPatchNumber());
        }
    }

    public void storeSelectedPatch() {
        Scene scene = ((SceneListModel) myModel).getSceneAt(table.getSelectedRow());
        new SysexStoreDialog(scene.getPatch(), scene.getBankNumber(), scene.getPatchNumber()); // wirski@op.pl
    }

    void UpdatePatch(int row) { // wirski@op.pl
            int repeat;
            java.util.List queue = new ArrayList();
            Scene scene = ((SceneListModel) myModel).getSceneAt(row);
            IPatchDriver driver = scene.getPatch().getDriver();
            int inPort = driver.getDevice().getInPort();
            long patchSize = driver.getPatchSize();
            long sysexSize = 0;
            boolean variableSize = false;
            long timeout;
            if (patchSize == 0) {
                variableSize = true;
                timeout = 5000;
            } else {
                timeout = patchSize + 500;
            };
            do  {
                repeat = JOptionPane.NO_OPTION;
                sysexSize = 0;
                MidiUtil.clearSysexInputQueue(inPort);
                driver.requestPatchDump(scene.getBankNumber(), scene.getPatchNumber());
                try {
                    do {
                        SysexMessage msg;
                        msg = (SysexMessage) MidiUtil.getMessage(inPort, timeout);
                        queue.add(msg);
                        sysexSize += msg.getLength();
                    } while ((variableSize) || (sysexSize < patchSize));
                } catch (MidiUtil.TimeoutException ex) {
                    if (!variableSize) {
                        repeat =  JOptionPane.showConfirmDialog(
                            null, "Cannot receive sysex from " + driver + ".\nDo you want to try again?",
                            "Scene Update Warning",
                            JOptionPane.YES_NO_OPTION);
                    }
                } catch (InvalidMidiDataException ex) {
                    ErrorMsg.reportError ("Error", "Invalid MIDI data error");
                }
            } while (repeat == JOptionPane.YES_OPTION);
            if ((variableSize) || (sysexSize == patchSize)) {
                SysexMessage[] msgs = (SysexMessage[]) queue.toArray(new SysexMessage[0]);
                IPatch[] patarray = driver.createPatches(msgs);
                if (patarray.length == 1) {
                    myModel.setPatchAt(patarray[0], row, scene.getBankNumber(), scene.getPatchNumber());
                } else {
                    ErrorMsg.reportError ("Error", "To many patches received");
                } 
            } else {
                ErrorMsg.reportError ("Error", "Incorrect patch size received");
            }
        }

    public class UpdateSceneTask { // wirski@op.pl
        private ArrayList Undo;
        private int lengthOfTask;
        private int current = 0;
        private boolean done = false;
        private boolean canceled = false;
        private String statMessage;
        private int noOfPatches;
        private int[] sceneIndx;
        private int[] syxArray;
        int waitToClose=0;
        public UpdateSceneTask(PatchTableModel model, int[] indx) {
            Undo = (ArrayList)((SceneListModel) myModel).getList().clone();
            sceneIndx = indx;
            noOfPatches = sceneIndx.length;
            syxArray = new int[noOfPatches];
            for (int i = 0; i < noOfPatches; i++) {
                syxArray[i] = ((SceneListModel) myModel).getSceneAt(i).getPatch().getDriver().getPatchSize()/10;
                lengthOfTask += syxArray[i];
            }
            lengthOfTask += 1; // to force progessMonitor not to quit at end of the task
        }

        public void go() {
            final SwingWorker worker = new SwingWorker() {
                public Object construct() {
                    current = 0;
                    done = false;
                    canceled = false;
                    statMessage = null;
                    return new ActualTask();
                }
            };
            worker.start();
        }

        /**
         * Called from ProgressBarDemo to find out how much work needs
         * to be done.
         */
        public int getLengthOfTask() {
            return lengthOfTask;
        }

        /**
         * Called from ProgressBarDemo to find out how much has been done.
         */
        public int getCurrent() {
            return current;
        }

        public void cancel() {
            canceled = true;
            statMessage = null;
        }

        /**
         * Called from ProgressBarDemo to find out if the task has completed.
         */
        public boolean isDone() {
            return done;
        }

        /**
         * Returns the most recent status message, or null
         * if there is no current status message.
         */
        public String getMessage() {
            return statMessage;
        }

        public void undo() {
            myModel.setList(Undo);
            myModel.fireTableDataChanged();
        }

        /**
         * The actual long running task.  This runs in a SwingWorker thread.
         */
        class ActualTask {
            ActualTask() {
                for(int i = 0; i < noOfPatches; i++) {
                    if (canceled) {
                        break;
                    }
                    IPatch patch = ((SceneListModel) myModel).getSceneAt(i).getPatch();
                    statMessage = patch.getDriver() + ":" + patch.getName();
                    UpdatePatch(sceneIndx[i]);
                    current += syxArray[i];
                }
                statMessage = "Finished";
                done = true;
                changed = true;
                myModel.fireTableDataChanged();
            }
        }
    }

    void updateScene() { // wirski@op.pl
        int len = myModel.getRowCount();
        int[] indx = new int[len];
        for (int i = 0; i < len; i++) {
            indx[i]=i;
        }
        updateScene(indx);
    }

    void updateSelected() {// wirski@op.pl
        int[] indx = table.getSelectedRows();
        updateScene(indx);
    }
       
    void updateScene(int[] indx) {// wirski@op.pl
        final Timer timer;
        final UpdateSceneTask task;
        ActionListener TimerListener;
        task = new UpdateSceneTask(myModel,indx);
        final ProgressMonitor progressMonitor = new ProgressMonitor(table,
                "Update Scene",
                "", 0, task.getLengthOfTask());
        progressMonitor.setProgress(0);
        progressMonitor.setMillisToDecideToPopup(0);
        progressMonitor.setMillisToPopup(0);
        timer = new Timer( 100, null);

        TimerListener = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                progressMonitor.setProgress(task.getCurrent());
                String s = task.getMessage();
                if (s != null) {
                    progressMonitor.setNote(s);
                }
                if (progressMonitor.isCanceled()) {
                    timer.stop();
                    progressMonitor.close();
                    task.cancel();
                    task.undo();
                }
                if (task.isDone()) {
                    if (task.waitToClose>10) {
                        timer.stop();
                        progressMonitor.setProgress(task.getLengthOfTask());
                    } else {
                    task.waitToClose++;
                    }
                }
            }
        };
        timer.addActionListener(TimerListener);
 
        task.go();
        timer.start();
    }
    
    FileFilter getFileFilter() {
        return FILE_FILTER;
    }

    String getFileExtension() {
        return FILE_EXTENSION;
    }

    /**
     * Refactored from PerformanceListModel
     * 
     * @author Gerrit Gehnen
     */
    private class SceneListModel extends PatchTableModel {
        private final String[] columnNames = { "Synth", "Type", "Patch Name",
                "Bank Number", "Patch Number", "Comment" };

// TODO: Remove these comments after June, 2006
// This isn't the same "changed" as the one used in AbstractLibraryFrame, and this one is never used
// - Emenaker 2006-02-21
//        private boolean changed;

        private ArrayList list = new ArrayList();

        public SceneListModel(/*boolean c*/) {
// TODO: Remove these comments after June, 2006
// This isn't the same "changed" as the one used in AbstractLibraryFrame, and this one is never used
// - Emenaker 2006-02-21
//            changed = c;
        }

        public int getRowCount() {
            return list.size();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            Scene myScene = (Scene) list.get(row);
            IPatch myPatch = myScene.getPatch();
            try {
                switch (col) {
                case SYNTH:
                    return myPatch.getDevice().getSynthName();
                case TYPE:
                    return myPatch.getType();
                case PATCH_NAME:
                    return myPatch.getName();
                case BANK_NUM:
                    // generic driver returns null
                    String[] bn = myPatch.getDriver().getBankNumbers();
                    if (bn != null)
                        return bn[myScene.getBankNumber()];
                    else
                        return String.valueOf(myScene.getBankNumber());
                case PATCH_NUM:
                    String[] pn = myPatch.getDriver().getPatchNumbers();
                    if (pn != null)
                        return pn[myScene.getPatchNumber()];
                    else
                        return String.valueOf(myScene.getPatchNumber());
                case COMMENT:
                    return myScene.getComment();
                default:
                    ErrorMsg.reportStatus("SceneFrame.getValueAt: internal error.");
                    return null;
                }
            } catch (NullPointerException e) {
                ErrorMsg.reportStatus("SceneFrame.getValueAt: row=" + row
                        + ", col=" + col + ", Patch=" + myPatch);
                ErrorMsg.reportStatus("row count =" + getRowCount());
                e.printStackTrace();
                return null;
            }
        }

        /*
         * JTable uses this method to determine the default renderer/ editor for
         * each cell. If we didn't implement this method, then the last column
         * would contain text ("true"/"false"), rather than a check box.
         */
        public Class getColumnClass(int c) {
            return String.class;
        }

        public boolean isCellEditable(int row, int col) {
            return (col > PATCH_NAME
                    && !((col == BANK_NUM || col == PATCH_NUM)
                            && ((Scene) list.get(row)).getPatch().hasNullDriver()));
        }

        public void setValueAt(Object value, int row, int col) {
            //ErrorMsg.reportStatus("SetValue at "+row+" "+col+"
            // Value:"+value);
// TODO: Remove these comments after June, 2006
// This isn't the same "changed" as the one used in AbstractLibraryFrame, and this one is never used
// - Emenaker 2006-02-21
//            changed = true;
            Scene myScene = getSceneAt(row);
            switch (col) {
            case BANK_NUM:
                myScene.setBankNumber(((Integer) value).intValue());
                break;
            case PATCH_NUM:
                myScene.setPatchNumber(((Integer) value).intValue());
                break;
            case COMMENT:
                myScene.setComment((String) value);
                break;
            default:
                ErrorMsg.reportStatus("SceneFrame.setValueAt: internal error.");
            }
            list.set(row, myScene);
        }

        // begin PatchTableModel interface methods
        // It is caller's responsibility to update Table.
        void addPatch(IPatch p) {
            list.add(new Scene(p));
        }

        void addPatch(IPatch p, int bankNum, int patchNum) { // wirski@op.pl
            list.add(new Scene(p, bankNum, patchNum));
        }

        void setPatchAt(IPatch p, int row, int bankNum, int patchNum) { // wirski@op.pl
            list.set(row, new Scene(p, bankNum, patchNum));
        }
        
        void setPatchAt(IPatch p, int row) {
            list.set(row, new Scene(p));
        }

        IPatch getPatchAt(int row) {
//            return ((Scene) list.get(row)).getPatch();
            return ((Scene) list.get(row)).getPatch();
        }

        String getCommentAt(int row) {
            return ((Scene) list.get(row)).getComment();
        }

        void removeAt(int row) {
            this.list.remove(row);
        }

        ArrayList getList() {
            return this.list;
        }

        void setList(ArrayList newList) {
            this.list = newList;
        }
        // end PatchTableModel interface methods

        Scene getSceneAt(int row) {
            return (Scene) list.get(row);
        }

        void addScene(Scene s) {
            list.add(s);
        }
    }

    /**
     * SceneListTransferHandler
     *
     * This class extends PatchTransferHandler by allowing scenes to be placed into the Transferable and also
     * allowing scenes to be inserted into the JTable as *scenes* and not just as the inner patch data (which
     * is the default behavior of PatchTransferHandler) - Emenaker - 2006-02-27
     */
    private static class SceneListTransferHandler extends PatchTransferHandler {

        protected Transferable createTransferable(JComponent c) {
            PatchesAndScenes patchesAndScenes = new PatchesAndScenes();
            if(c instanceof JTable) {
                JTable table = (JTable) c;
                SceneListModel slm = (SceneListModel) table.getModel();
                int[] rowIdxs = table.getSelectedRows();
                for(int i=0; i<rowIdxs.length; i++) {
                    Scene scene = slm.getSceneAt(rowIdxs[i]);
                    patchesAndScenes.add(scene);
                }
            } else {
                ErrorMsg.reportStatus("PatchTransferHandler.createTransferable doesn't recognize the component it was given");
            }
            return(patchesAndScenes);
        }

        protected boolean storeScene(Scene s, JComponent c) {
            SceneListModel model = (SceneListModel) ((JTable) c).getModel();
            model.addScene(s);
            ErrorMsg.reportStatus("Stored a Scene into a SceneList");
            // TODO This method shouldn't have to worry about calling fireTableDataChanged(). Find a better way.
            model.fireTableDataChanged();
            return true;
        }

        protected boolean storePatch(IPatch p, JComponent c) {
            SceneListModel model = (SceneListModel) ((JTable) c).getModel();
            model.addPatch(p);
            // TODO This method shouldn't have to worry about calling fireTableDataChanged(). Find a better way.
            model.fireTableDataChanged();
            return true;
//            return false;
        }
    }

    /**
     * @author Gerrit Gehnen
     */
    private class SceneTableCellEditor implements TableCellEditor,
            TableModelListener {
        private TableCellEditor editor, defaultEditor;
        private JComboBox box;
        private int oldrow = -1;
        private int oldcol = -1;

        /**
         * Constructs a SceneTableCellEditor. create default editor
         * 
         * @see TableCellEditor
         * @see DefaultCellEditor
         */
        public SceneTableCellEditor() {
            defaultEditor = new DefaultCellEditor(new JTextField());
            table.getModel().addTableModelListener(this);
        }

        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected, int row, int column) {
            return editor.getTableCellEditorComponent(table, value, isSelected,
                    row, column);
        }

        public Object getCellEditorValue() {
            //         ErrorMsg.reportStatus("getCellEditorValue
            // "+box.getSelectedItem());
            return new Integer(box.getSelectedIndex());
        }

        public boolean stopCellEditing() {
            return editor.stopCellEditing();
        }

        public void cancelCellEditing() {
            editor.cancelCellEditing();
        }

        public boolean isCellEditable(EventObject anEvent) {
            selectEditor((MouseEvent) anEvent);
            return editor.isCellEditable(anEvent);
        }

        public void addCellEditorListener(CellEditorListener l) {
            editor.addCellEditorListener(l);
        }

        public void removeCellEditorListener(CellEditorListener l) {
            editor.removeCellEditorListener(l);
        }

        public boolean shouldSelectCell(EventObject anEvent) {
            selectEditor((MouseEvent) anEvent);
            return editor.shouldSelectCell(anEvent);
        }

        protected void selectEditor(MouseEvent e) {
            IPatchDriver driver;
            int row, col;

            if (e == null) {
                row = table.getSelectionModel().getAnchorSelectionIndex();
                col = table.getSelectedColumn();
            } else {
                row = table.rowAtPoint(e.getPoint());
                col = table.columnAtPoint(e.getPoint());
            }
            //    ErrorMsg.reportStatus("selectEditor "+ row);
            if ((row != oldrow) || (col != oldcol)) {
                oldrow = row;
                oldcol = col;
                box = new JComboBox();

                driver = ((SceneListModel) table.getModel()).getPatchAt(row)
                        .getDriver();
                String[] patchNumbers = driver.getPatchNumbers();
                String[] bankNumbers = driver.getBankNumbers();
                if (patchNumbers.length > 1) {
                    if (col == BANK_NUM) {
                        for (int i = 0; i < bankNumbers.length; i++) {
                            box.addItem(bankNumbers[i]);
                        }
                    } else if (col == PATCH_NUM)
                        for (int i = 0; i < patchNumbers.length; i++) {
                            box.addItem(patchNumbers[i]);
                        }
                }
                editor = new DefaultCellEditor(box);
                if (editor == null) {
                    editor = defaultEditor;
                }
            }
        }

        public void tableChanged(TableModelEvent tableModelEvent) {
            oldcol = -1;
            oldrow = -1;
        }
    }
}
