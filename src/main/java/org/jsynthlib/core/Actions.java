package org.jsynthlib.core;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;

/**
 * Define Action classes
 * @version $Id$
 */
final public class Actions {
    // I hope 64bit is enough for a while.
    static final long EN_ABOUT			= 0x0000000000000001L;
    static final long EN_COPY			= 0x0000000000000002L;
    static final long EN_CROSSBREED		= 0x0000000000000004L;
    static final long EN_CUT			= 0x0000000000000008L;
    static final long EN_DELETE			= 0x0000000000000010L;
    static final long EN_DELETE_DUPLICATES	= 0x0000000000000020L;
    static final long EN_DOCS			= 0x0000000000000040L;
    static final long EN_EDIT			= 0x0000000000000080L;
    static final long EN_EXIT			= 0x0000000000000100L;
    static final long EN_EXPORT			= 0x0000000000000200L;
    static final long EN_EXTRACT		= 0x0000000000000400L;
    static final long EN_GET			= 0x0000000000000800L;
    static final long EN_HOME_PAGE		= 0x0000000000001000L;
    static final long EN_IMPORT			= 0x0000000000002000L;
    static final long EN_IMPORT_ALL		= 0x0000000000004000L;
    static final long EN_LICENSE		= 0x0000000000008000L;
    static final long EN_MONITOR		= 0x0000000000010000L;
    static final long EN_NEW			= 0x0000000000020000L;
    static final long EN_NEW_PATCH		= 0x0000000000040000L;
    static final long EN_NEW_SCENE		= 0x0000000000080000L;
    static final long EN_NEXT_FADER		= 0x0000000000100000L;
    static final long EN_OPEN			= 0x0000000000200000L;
    static final long EN_PASTE			= 0x0000000000400000L;
    static final long EN_PLAY			= 0x0000000000800000L;
    static final long EN_PREFS			= 0x0000000001000000L;
    static final long EN_REASSIGN		= 0x0000000002000000L;
    static final long EN_SAVE			= 0x0000000004000000L;
    static final long EN_SAVE_AS		= 0x0000000008000000L;
    static final long EN_SEARCH			= 0x0000000010000000L;
    static final long EN_SEND			= 0x0000000020000000L;
    static final long EN_SEND_TO		= 0x0000000040000000L;
    static final long EN_SORT			= 0x0000000080000000L;
    static final long EN_STORE			= 0x0000000100000000L;
    static final long EN_TRANSFER_SCENE	= 0x0000000200000000L;
    static final long EN_UPLOAD			= 0x0000000400000000L;
    static final long EN_PREV_FADER		= 0x0000000800000000L;
    static final long EN_PRINT  		= 0x0000001000000000L;
    static final long EN_UPDATE_SCENE           = 0x0000002000000000L;
    static final long EN_UPDATE_SELECTED        = 0x0000004000000000L; 

    /** All actions excluding ones which are always eanbled.  */
    static final long EN_ALL	= (//EN_ABOUT
				   EN_COPY
				   | EN_CROSSBREED
				   | EN_CUT
				   | EN_DELETE
				   | EN_DELETE_DUPLICATES
				   //| EN_DOCS
				   | EN_EDIT
				   //| EN_EXIT
				   | EN_EXPORT
				   | EN_EXTRACT
				   | EN_GET
				   | EN_HOME_PAGE
				   | EN_IMPORT
				   | EN_IMPORT_ALL
				   //| EN_LICENSE
				   //| EN_MONITOR
				   //| EN_NEW
				   | EN_NEW_PATCH
				   //| EN_NEW_SCENE
				   //| EN_NEXT_FADER
				   //| EN_PREV_FADER
				   //| EN_OPEN
				   //| EN_PASTE : 'paste' needs special handling
				   | EN_PLAY
				   //| EN_PREFS
				   | EN_REASSIGN
				   | EN_SAVE
				   | EN_SAVE_AS
				   | EN_SEARCH
				   | EN_SEND
				   | EN_SEND_TO
				   | EN_SORT
				   | EN_STORE
				   | EN_TRANSFER_SCENE
                                     | EN_UPDATE_SCENE
                                     | EN_UPDATE_SELECTED
				   | EN_UPLOAD);

    private static Action aboutAction;
    private static Action copyAction;
    private static Action crossBreedAction;
    private static Action cutAction;
    private static Action deleteAction;
    private static Action deleteDuplicatesAction;
    private static Action docsAction;
    private static Action editAction;
    static Action exitAction; // refered by PatchEdit
    private static Action exportAction;
    private static Action extractAction;
    private static Action getAction;
    private static Action homePageAction;
    private static Action importAction;
    private static Action importAllAction;
    private static Action licenseAction;
    private static Action monitorAction;
    private static Action newAction;
    private static Action newPatchAction;
    private static Action newSceneAction;
    private static Action nextFaderAction;
    private static Action prevFaderAction;
    private static Action openAction;
    private static Action pasteAction;
    private static Action playAction;
    private static Action prefsAction;
    private static Action printAction;
    private static Action reassignAction;
    private static Action saveAction;
    private static Action saveAsAction;
    private static Action searchAction;
    private static Action sendAction;
    private static Action sendToAction;
    private static Action sortAction;
    private static Action storeAction;
    private static Action transferSceneAction;
    private static Action updateSceneAction;
    private static Action updateSelectedAction;
    private static Action uploadAction;

    private static JPopupMenu menuPatchPopup;
    private static MidiMonitor midiMonitor;
    private static SearchDialog searchDialog;
    private static DocumentationWindow docWin;
    private static DocumentationWindow licWin;
    private static DocumentationWindow hpWin;

    private static Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
    /** just for efficiency. */
    private static boolean isMac = MacUtils.isMac();

    // don't have to call constructor for Utility class.
    private Actions() {
    }

    static void createActions() {
        HashMap mnemonics = new HashMap();

        newAction		= new NewAction(mnemonics);
        openAction		= new OpenAction(mnemonics);
        saveAction		= new SaveAction(mnemonics);
        saveAsAction		= new SaveAsAction(mnemonics);
        newSceneAction		= new NewSceneAction(mnemonics);
        transferSceneAction	= new TransferSceneAction(mnemonics);
        updateSceneAction         = new UpdateSceneAction(mnemonics);
        sortAction		= new SortAction(mnemonics);
        searchAction		= new SearchAction(mnemonics);
        deleteDuplicatesAction	= new DeleteDuplicatesAction(mnemonics);
        exitAction		= new ExitAction(mnemonics);

        copyAction		= new CopyAction(mnemonics);
        cutAction		= new CutAction(mnemonics);
        pasteAction		= new PasteAction(mnemonics);
        deleteAction		= new DeleteAction(mnemonics);
        importAction		= new ImportAction(mnemonics);
        exportAction		= new ExportAction(mnemonics);
        importAllAction		= new ImportAllAction(mnemonics);
        sendAction		= new SendAction(mnemonics);
        sendToAction		= new SendToAction(mnemonics);
        updateSelectedAction      = new UpdateSelectedAction(mnemonics);//R. Wirski
        printAction		= new PrintAction(mnemonics);
        storeAction		= new StoreAction(mnemonics);
        getAction		= new GetAction(mnemonics);

        playAction		= new PlayAction(mnemonics);
        editAction		= new EditAction(mnemonics);
        reassignAction		= new ReassignAction(mnemonics);
        crossBreedAction	= new CrossBreedAction(mnemonics);
        newPatchAction		= new NewPatchAction(mnemonics);
        extractAction		= new ExtractAction(mnemonics);

	prefsAction		= new PrefsAction(mnemonics);
	monitorAction		= new MonitorAction(mnemonics);

        aboutAction		= new AboutAction(mnemonics);
        docsAction		= new DocsAction(mnemonics);
        licenseAction		= new LicenseAction(mnemonics);
        homePageAction		= new HomePageAction(mnemonics);

        nextFaderAction		= new NextFaderAction(mnemonics);
        prevFaderAction		= new PrevFaderAction(mnemonics);
	uploadAction		= new UploadAction(mnemonics);

	if (isMac)
	    MacUtils.init(exitAction, prefsAction, aboutAction);
	else
	    setMnemonics(mnemonics); // set keyboard short cut
    }

    /** This sets up the Menubar. Called from JSLDesktop. */
    static JMenuBar createMenuBar() {
        HashMap mnemonics = new HashMap();
        int mask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu(mnemonics, mask));
	menuBar.add(createLibraryMenu(mnemonics, mask));
        menuBar.add(createPatchMenu(mnemonics, mask));
	menuBar.add(createWindowMenu(mnemonics, mask));
        menuBar.add(createHelpMenu(mnemonics, mask));

	// set keyboard short cut
	if (!isMac)
	    setMnemonics(mnemonics);

        return menuBar;
    }

    private static JMenu createFileMenu(HashMap mnemonics, int mask) {
        JMenuItem mi;
        JMenu menuFile = new JMenu("File");
        mnemonics.put(menuFile, new Integer(KeyEvent.VK_F));
        mi = menuFile.add(openAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, mask));
        mi = menuFile.add(saveAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, mask));
        menuFile.add(saveAsAction);

        menuFile.addSeparator();
        menuFile.add(importAction);
        menuFile.add(exportAction);
        menuFile.add(importAllAction);

        if (!isMac) {
            menuFile.addSeparator();
            menuFile.add(exitAction);
        }
        return menuFile;
    }

    private static JMenu createLibraryMenu(HashMap mnemonics, int mask) {
        JMenuItem mi;
        JMenu menuLib = new JMenu("Library");
        mnemonics.put(menuLib, new Integer(KeyEvent.VK_L));

        menuLib.add(transferSceneAction);
        menuLib.add(updateSceneAction);
        menuLib.addSeparator();

        menuLib.add(sortAction);
        menuLib.add(searchAction);
        menuLib.add(crossBreedAction);
        menuLib.add(deleteDuplicatesAction);
        menuLib.addSeparator();

        mi = menuLib.add(newAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, mask));
        mi = menuLib.add(newSceneAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, mask));
        return menuLib;
    }

    private static JMenu createPatchMenu(HashMap mnemonics, int mask) {
        JMenuItem mi;
        JMenu menuPatch = new JMenu("Patch");
        mnemonics.put(menuPatch, new Integer(KeyEvent.VK_P));
        mi = menuPatch.add(copyAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_COPY, 0));
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, mask));
        mi = menuPatch.add(cutAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_CUT, 0));
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, mask));
        mi = menuPatch.add(pasteAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PASTE, 0));
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, mask));
        mi = menuPatch.add(deleteAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        menuPatch.addSeparator();

        mi = menuPatch.add(sendAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, mask));
        menuPatch.add(updateSelectedAction); // wirski@op.pl
        menuPatch.add(sendToAction);
        menuPatch.add(storeAction);
        mi = menuPatch.add(getAction); // phil@muqus.com
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, mask)); // wirski@op.pl
        menuPatch.addSeparator();

        mi = menuPatch.add(editAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, mask));
        mi = menuPatch.add(printAction);
        mi = menuPatch.add(playAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, mask));
        menuPatch.addSeparator();

        menuPatch.add(reassignAction);
        menuPatch.add(extractAction);
        menuPatch.addSeparator();

        menuPatch.add(newPatchAction);
        menuPatch.add(uploadAction);
        menuPatch.addMenuListener(new MenuListener() { // need this???
            public void menuCanceled(MenuEvent e) {}
            public void menuDeselected(MenuEvent e) {}
            public void menuSelected(MenuEvent e) {
                pasteAction.setEnabled(true);
            }
        });
        return menuPatch;
    }

    /** List of JSLWindowMenus including one for invisible frame. */
    private static ArrayList windowMenus = new ArrayList();
    private static JMenu createWindowMenu(HashMap mnemonics, int mask) {
        JSLWindowMenu wm = new JSLWindowMenu("Window");
        windowMenus.add(wm);

	if (!isMac) {
	    wm.add(prefsAction);
	    wm.setMnemonic(KeyEvent.VK_W);
	}
	wm.add(monitorAction);
	//wm.add(JSLDesktop.toolBarAction);
	wm.addSeparator();
	JMenuItem mi = wm.add(closeAction);
	mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, mask));
	wm.addSeparator();

	// add menu entries of existing frames
	JSLDesktop desktop = PatchEdit.getDesktop();
	if (desktop != null) { // when desktop is created, 'desktop' is still null.
            Iterator it = desktop.getJSLFrameIterator();
            while (it.hasNext()) {
                wm.add((JSLFrame) it.next());
            }
        }
        return wm;
    }
    private static Action closeAction = new AbstractAction("Close") {
        public void actionPerformed(ActionEvent ex) {
            try {
                MenuDesktop desktop = PatchEdit.getDesktop();
                JSLFrame frame = desktop.getSelectedFrame();
                if (frame != null) {
                    frame.setClosed(true);
                } else {
                    PatchEdit.getDesktop().closingProc();
                }
            } catch (PropertyVetoException e) {
                // don't know how to handle this.
                e.printStackTrace();
            }
        }
    };

    /** to keep track the relation between a JSLMenuBar and a JSLFrame. */
    private static HashMap frames = new HashMap();
    /** JSLDesktop with Menu support. */
    public static class MenuDesktop extends JSLDesktop {

        MenuDesktop(String title, JMenuBar mb, JToolBar tb, Action exitAction) {
            super(title, mb, tb, exitAction);
        }
        public void add(JSLFrame frame) {
            super.add(frame);
            Iterator it = windowMenus.iterator();
            while (it.hasNext()) {
                ((JSLWindowMenu) it.next()).add(frame);
            }
        }
        public void JSLFrameClosed(JSLFrameEvent e) {
            super.JSLFrameClosed(e);
            JSLFrame frame = e.getJSLFrame();
            windowMenus.remove(frames.get(frame));
            frames.remove(frame);
        }
    }
    /** JSLDesktop with Menu support. */
    public static class MenuFrame extends JSLFrame {
        private MenuDesktop desktop;

        public MenuFrame(MenuDesktop desktop, String title, boolean addToolBar,
                boolean resizable, boolean closable,
                boolean maximizable, boolean iconifiable) {
            super(desktop, title, resizable, closable, maximizable, iconifiable);
            this.desktop = desktop;
	    if (addToolBar) {
	        JToolBar tb = createToolBar();
	        getContentPane().add(tb, BorderLayout.NORTH);
	        tb.setVisible(true);
	    }
            if (!JSLDesktop.useMDI()) {
                JMenuBar mb = createMenuBar();
                setJMenuBar(mb);
                frames.put(this, mb);
            }
        }
        /** create a resizable, closable, maximizable, and iconifiable frame with toolbar. */
        public MenuFrame(MenuDesktop desktop, String title) {
            this(desktop, title, AppConfig.getToolBar(), true, true, true, true);
        }
        public void setVisible(boolean b) {
	    if (isMac && !JSLDesktop.useMDI()) {
	        JFrame frame = getJFrame();
		if (b) {
		    if (frame.getJMenuBar() == null)
		        setJMenuBar(frames.get(this) == null ? createMenuBar() : (JMenuBar) frames.get(this));
		} else {
		    // Remove menubar and change focus once so frame can be disposed.
		    // http://lists.apple.com/archives/java-dev/2003/Dec/msg00122.html
		    // Java 1.4.2 still has this bug.
		    setJMenuBar(null);
		    desktop.getInvisible().requestFocus();
		    frame.requestFocus();
		}
	    }
	    super.setVisible(b);
        }
    }
    private static JMenu createHelpMenu(HashMap mnemonics, int mask) {
        JMenuItem mi;
        JMenu menuHelp = new JMenu("Help");
        mnemonics.put(menuHelp, new Integer(KeyEvent.VK_H));
        mi = menuHelp.add(docsAction);
        mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_HELP, 0));
        // J2SE 1.4.2 is not ready for HTML of www.jsynthlib.org site
        //mi = menuHelp.add(homePageAction);
        mi = menuHelp.add(licenseAction);
        if (!isMac)
            menuHelp.add(aboutAction);
        return menuHelp;
    }

    /** This sets up the mnemonics */
    private static void setMnemonics(Map mnemonics) {
    	Iterator it = mnemonics.keySet().iterator();
    	Object key, value;
    	while (it.hasNext()) {
    	    key = it.next();
    	    value = mnemonics.get(key);
    	    if (key instanceof JMenuItem)
    		    ((JMenuItem) key).setMnemonic(((Integer) value).intValue());
    	    else if (key instanceof Action)
    		    ((Action) key).putValue(Action.MNEMONIC_KEY, value);
    	}
    }

    static void createPopupMenu() {
        // create popup menu
        menuPatchPopup = new JPopupMenu();
        menuPatchPopup.add(playAction);
        menuPatchPopup.add(editAction);
        menuPatchPopup.add(updateSelectedAction);
        menuPatchPopup.addSeparator();

        menuPatchPopup.add(sendAction);
        menuPatchPopup.add(sendToAction);
        menuPatchPopup.add(storeAction);
        menuPatchPopup.addSeparator();

        menuPatchPopup.add(reassignAction);
        menuPatchPopup.addSeparator();

        menuPatchPopup.add(copyAction);
        menuPatchPopup.add(cutAction);
        menuPatchPopup.add(pasteAction);
        menuPatchPopup.add(deleteAction);
        menuPatchPopup.addSeparator();

        menuPatchPopup.add(uploadAction);
    }

    /** show popup menu for patch. */
    static void showMenuPatchPopup(JTable tbl, int x, int y) {
        if(menuPatchPopup == null) {
            createPopupMenu();
        }

        menuPatchPopup.show(tbl, x, y);
    }

    static JToolBar createToolBar() {
        // create tool bar
        JToolBar toolBar = new JToolBar();
        toolBar.setPreferredSize(new Dimension(500, 35));
        toolBar.setFloatable(true);

        toolBar.add(createToolBarButton(newAction, "New", "New Library"));
        toolBar.add(createToolBarButton(openAction, "Open", "Open Library"));
        toolBar.add(createToolBarButton(saveAction, "Save", "Save Library"));

        toolBar.addSeparator();

        toolBar.add(createToolBarButton(copyAction, "Copy", "Copy Patch"));
        toolBar.add(createToolBarButton(cutAction, "Cut", "Cut Patch"));
        toolBar.add(createToolBarButton(pasteAction, "Paste", "Paste Patch"));
        toolBar.add(createToolBarButton(importAction, "Import", "Import Patch"));
        toolBar.add(createToolBarButton(exportAction, "Export", "Export Patch"));

        toolBar.addSeparator();

        toolBar.add(createToolBarButton(playAction, "Play", "Play Patch"));
        toolBar.add(createToolBarButton(storeAction, "Store", "Store Patch"));
        toolBar.add(createToolBarButton(editAction, "Edit", "Edit Patch"));

        toolBar.addSeparator();

        toolBar.add(createToolBarButton(prevFaderAction, "Prev",
                "Go to Previous Fader Bank"));
        toolBar.add(createToolBarButton(nextFaderAction, "Next",
                "Go to Next Fader Bank"));

        return toolBar;
    }

    private static JButton createToolBarButton(Action a, String label, String tooltip) {
	String label2 = label.toLowerCase();
	URL u1 = PatchEdit.class.getResource("/images/" + label2 + ".png");
	URL u2 = PatchEdit.class.getResource("/images/disabled-" + label2 + ".png");
	//Create and initialize the button.
	JButton button = new JButton(a);
	button.setToolTipText(tooltip);

	if (u1 != null) {                      //image found
	    button.setText(null);
	    button.setIcon(new ImageIcon(u1, label));
	    if (u2 != null) {
		button.setDisabledIcon(new ImageIcon(u2, label));
	    }
	} else {                                     //no image found
	    button.setText(label);
	    ErrorMsg.reportStatus("Resource not found: " + "images/" + label + ".png");
	}
	return button;
    }

    /**
     * Enable/disable Actions.
     * @param b <code>true</code> to enable Actions,
     * <code>false</code> to disable them.
     * @param v Specify Actions to be enabled/disabled.  Use constants
     * <code>EN_*</code>.
     */
    static void setEnabled(boolean b, long v) {
	if ((v & EN_ABOUT) != 0)
	    aboutAction.setEnabled(b);
	if ((v & EN_COPY) != 0)
	    copyAction.setEnabled(b);
	if ((v & EN_CROSSBREED) != 0)
	    crossBreedAction.setEnabled(b);
	if ((v & EN_CUT) != 0)
	    cutAction.setEnabled(b);
	if ((v & EN_DELETE) != 0)
	    deleteAction.setEnabled(b);
	if ((v & EN_DELETE_DUPLICATES) != 0)
	    deleteDuplicatesAction.setEnabled(b);
	if ((v & EN_DOCS) != 0)
	    docsAction.setEnabled(b);
	if ((v & EN_EDIT) != 0)
	    editAction.setEnabled(b);
	if ((v & EN_EXIT) != 0)
	    exitAction.setEnabled(b);
	if ((v & EN_EXPORT) != 0)
	    exportAction.setEnabled(b);
	if ((v & EN_EXTRACT) != 0)
	    extractAction.setEnabled(b);
	if ((v & EN_GET) != 0)
	    getAction.setEnabled(b);
	if ((v & EN_HOME_PAGE) != 0)
	    homePageAction.setEnabled(b);
	if ((v & EN_IMPORT) != 0)
	    importAction.setEnabled(b);
	if ((v & EN_IMPORT_ALL) != 0)
	    importAllAction.setEnabled(b);
	if ((v & EN_LICENSE) != 0)
	    licenseAction.setEnabled(b);
	if ((v & EN_MONITOR) != 0)
	    monitorAction.setEnabled(b);
	if ((v & EN_NEW) != 0)
	    newAction.setEnabled(b);
	if ((v & EN_NEW_PATCH) != 0)
	    newPatchAction.setEnabled(b);
	if ((v & EN_NEW_SCENE) != 0)
	    newSceneAction.setEnabled(b);
	if ((v & EN_NEXT_FADER) != 0)
	    nextFaderAction.setEnabled(b);
	if ((v & EN_PREV_FADER) != 0)
	    prevFaderAction.setEnabled(b);
	if ((v & EN_OPEN) != 0)
	    openAction.setEnabled(b);
	if ((v & EN_PASTE) != 0)
	    pasteAction.setEnabled(b);
	if ((v & EN_PLAY) != 0)
	    playAction.setEnabled(b);
	if ((v & EN_PREFS) != 0)
	    prefsAction.setEnabled(b);
	if ((v & EN_REASSIGN) != 0)
	    reassignAction.setEnabled(b);
	if ((v & EN_SAVE) != 0)
	    saveAction.setEnabled(b);
	if ((v & EN_SAVE_AS) != 0)
	    saveAsAction.setEnabled(b);
	if ((v & EN_SEARCH) != 0)
	    searchAction.setEnabled(b);
	if ((v & EN_SEND) != 0)
	    sendAction.setEnabled(b);
	if ((v & EN_SEND_TO) != 0)
	    sendToAction.setEnabled(b);
	if ((v & EN_SORT) != 0)
	    sortAction.setEnabled(b);
	if ((v & EN_STORE) != 0)
	    storeAction.setEnabled(b);
	if ((v & EN_TRANSFER_SCENE) != 0)
	    transferSceneAction.setEnabled(b);
        if ((v & EN_UPDATE_SCENE) != 0)
            updateSceneAction.setEnabled(b); // wirski@op.pl
        if ((v & EN_UPDATE_SELECTED) != 0)
            updateSelectedAction.setEnabled(b);// wirski@op.pl
	if ((v & EN_UPLOAD) != 0)
	    uploadAction.setEnabled(b);
    if ((v & EN_PRINT) != 0)
        printAction.setEnabled(b);
    }

    /** Create a new Library Window and load a Library from disk to
	fill it! Fun! */
    static void openFrame(File file) {
        // PatchEdit.showWaitDialog("Loading " + file + " ...");
        // The previous line won't do anything unless we use a new thread to do the loading.
        // Right now, it's using the GUI event thread (since it was triggered from a GUI button
        // so the GUI never gets a chance to make the WaitDialog until we've finished loading.
        // - Emenaker, 2006-02-02
        AbstractLibraryFrame frame;
        if (file.exists()) {
            // try LibraryFrame then SceneFrame
            frame = new LibraryFrame(file);
            try {
                frame.open(file);
            } catch (Exception e) {
                frame = new SceneFrame(file);
                try {
                    frame.open(file);
                } catch (Exception e1) {
                    // PatchEdit.hideWaitDialog();
                    // See comment at beginning of this method
                    ErrorMsg.reportError("Error", "Error Loading Library:\n "
                            + file.getAbsolutePath(), e1);
                    return;
                }
            }
        } else {
            // PatchEdit.hideWaitDialog();
            // See comment at beginning of this method
            ErrorMsg.reportError("Error", "File Does Not Exist:\n" + file.getAbsolutePath() );
            return;
        }
        // PatchEdit.hideWaitDialog();
        // See comment at beginning of this method

        addLibraryFrame(frame);
    }

    /** add and show a Library/Scene Window */
    private static void addLibraryFrame(AbstractLibraryFrame frame) {
        PatchEdit.getDesktop().add(frame);
        frame.moveToDefaultLocation();
        frame.setVisible(true);
        try {
	    frame.setSelected(true);
	} catch (PropertyVetoException e) {
	    //I don't *actually* know what this is for :-)
	    ErrorMsg.reportStatus(e);
	}
    }

    /** This one saves a Library to Disk */
    static void saveFrame() {
	try {
	    AbstractLibraryFrame oFrame = (AbstractLibraryFrame) getSelectedFrame();
	    if (oFrame.getTitle().startsWith("Unsaved ")) {
	        File fn = showSaveDialog(oFrame);
		if (fn != null)
		    oFrame.save(fn);
	    } else {
	        oFrame.save();
	    }
	} catch (IOException e) {
	    ErrorMsg.reportError("Error", "Unable to Save Library", e);
	}
    }

    /** Save and specify a file name */
    private static void saveFrameAs() {
	try {
	    AbstractLibraryFrame oFrame = (AbstractLibraryFrame) getSelectedFrame();
	    File fn = showSaveDialog(oFrame);
	    if (fn != null)
	        oFrame.save(fn);
	} catch (IOException ex) {
	    ErrorMsg.reportError("Error", "Unable to Save Library", ex);
	}
    }

    private static File showSaveDialog(AbstractLibraryFrame oFrame) {
	CompatibleFileDialog fc = new CompatibleFileDialog();
        FileFilter filter = oFrame.getFileFilter();

        fc.addChoosableFileFilter(filter);
        fc.setFileFilter(filter);
        fc.setCurrentDirectory(new File (AppConfig.getLibPath()));
        if (fc.showSaveDialog(PatchEdit.getInstance())
	        != JFileChooser.APPROVE_OPTION)
            return null;
        File file = fc.getSelectedFile();

	if (!file.getName().toLowerCase().endsWith(oFrame.getFileExtension()))
	    file = new File(file.getPath() + oFrame.getFileExtension());
	if (file.isDirectory()) {
	    ErrorMsg.reportError("Error", "Can not Save over a Directory");
	    return null;
	}
	if (file.exists())
	    if (JOptionPane.showConfirmDialog(null, "Are you sure?",
		    "File Exists", JOptionPane.YES_NO_OPTION)
		    == JOptionPane.NO_OPTION)
		return null;
	return file;
    }

    private static PatchBasket getSelectedFrame() {
        return (PatchBasket) PatchEdit.getDesktop().getSelectedFrame();
    }
    ////////////////////////////////////////////////////////////////////////
    /*
     * Now we start with the various action classes. Each of these
     * preforms one of the menu commands and are called either from
     * the menubar, popup menu or toolbar.
     */
    private static class AboutAction extends AbstractAction {
	public AboutAction(Map mnemonics) {
	    super("About");
	    mnemonics.put(this, new Integer('A'));
	}

	public void actionPerformed(ActionEvent e) {
	    JOptionPane.showMessageDialog
		(null,
		 "JSynthLib Version " + Constants.VERSION
		 + "\nCopyright (C) 2000-04 Brian Klock et al.\n"
		 + "See 'Help -> License' for more info.",
		 "About JSynthLib", JOptionPane.INFORMATION_MESSAGE);
	    return;
	}
    }

    private static class ReassignAction extends AbstractAction {
	public ReassignAction(Map mnemonics) {
	    super("Reassign...", null); // show a dialog frame???
	    // mnemonics.put(this, new Integer('R'));
	    this.setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
	    try {
		getSelectedFrame().reassignSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to Reassign must be highlighted in the focused Window.", ex);
	    }
	}
    }

    private static class PlayAction extends AbstractAction {
        public PlayAction(Map mnemonics) {
            super("Play", null);
            mnemonics.put(this, new Integer('P'));
            this.setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            try {
		getSelectedFrame().playSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to Play must be highlighted in the focused Window.", ex);
	    }
        }
    }

    private static class StoreAction extends AbstractAction {
        public StoreAction(Map mnemonics) {
            super("Store...", null);
            mnemonics.put(this, new Integer('R'));
            this.setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            try {
		getSelectedFrame().storeSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to Store must be highlighted in the focused Window.", ex);
	    }
        }
    }

    private static class SendAction extends AbstractAction {
        public SendAction(Map mnemonics) {
            super("Send", null);
	    mnemonics.put(this, new Integer('S'));
            this.setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            try {
		getSelectedFrame().sendSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to Send must be highlighted in the focused Window.", ex);
	    }
        }
    }

    private static class SendToAction extends AbstractAction {
	public SendToAction(Map mnemonics) {
	    super("Send to...", null);
	    // mnemonics.put(this, new Integer('S'));
	    this.setEnabled(false);
	}
	public void actionPerformed(ActionEvent e) {
	    try {
		getSelectedFrame().sendToSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to 'Send to...' must be highlighted in the focused Window.", ex);
	    }
	}
    }

    // Added by Joe Emenaker - 2005-10-24
    private static class PrintAction extends AbstractAction {
        public PrintAction(Map mnemonics) {
            super("Print", null);
            //mnemonics.put(this, new Integer(''));
            this.setEnabled(true);
        }
        public void actionPerformed(ActionEvent e) {
            try {
                if(getSelectedFrame() instanceof BankEditorFrame) {
                    ((BankEditorFrame) getSelectedFrame()).printPatch();
                } else {
                    ErrorMsg.reportError("Error", "You can only print a Bank window.");
                }
            } catch (Exception ex) {
                ErrorMsg.reportError("Error", "Patch to Play must be highlighted in the focused Window.", ex);
            }
        }
    }

    private static class DeleteAction extends AbstractAction {
        public DeleteAction(Map mnemonics) {
            super("Delete", null);
            this.setEnabled(false);
	    mnemonics.put(this, new Integer('D'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		getSelectedFrame().deleteSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to delete must be hilighted\nin the focused Window.", ex);
	    }
        }
    }

    private static class CopyAction extends AbstractAction {
        public CopyAction(Map mnemonics) {
            super("Copy", null);
            this.setEnabled(false);
	    mnemonics.put(this, new Integer('C'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		getSelectedFrame().copySelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to copy must be highlighted\nin the focused Window.", ex);
	    }
        }
    }

    private static class UpdateSelectedAction extends AbstractAction {
        public UpdateSelectedAction(Map mnemonics) {
            super("Update", null);
            setEnabled(false);
            mnemonics.put(this, new Integer('U'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
                ((SceneFrame) getSelectedFrame()).updateSelected();
            } catch (Exception ex) {
                ErrorMsg.reportError("Error", "Patches to update must be highlighted\nin the focused Window.", ex);
            }
        }
    }

    private static class CutAction extends AbstractAction {
        public CutAction(Map mnemonics) {
            super("Cut", null);
            this.setEnabled(false);
	    mnemonics.put(this, new Integer('T'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		getSelectedFrame().copySelectedPatch();
		getSelectedFrame().deleteSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Patch to cut must be hilighted\nin the focused Window.", ex);
	    }
        }
    }

    private static class PasteAction extends AbstractAction {
        public PasteAction(Map mnemonics) {
            super("Paste", null);
            this.setEnabled(false);
	    mnemonics.put(this, new Integer('P'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		getSelectedFrame().pastePatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Library to Paste into must be the focused Window.", ex);
	    }
        }
	public void setEnabled(boolean b) {
	    try {
		JSLFrame f = PatchEdit.getDesktop().getSelectedFrame();
		b = b && f.canImport(cb.getContents(this).getTransferDataFlavors());
		super.setEnabled(b);
	    } catch (Exception ex) {
		super.setEnabled(false);
	    }
	}
    }

    private static class EditAction extends AbstractAction {
        public EditAction(Map mnemonics) {
            super("Edit...", null);
            mnemonics.put(this, new Integer('E'));
            this.setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            EditActionProc();
        }
    }
    static void EditActionProc() {
        class Worker extends Thread {
            public void run() {
            try {
                JSLFrame frm = getSelectedFrame().editSelectedPatch();
                if (frm != null) {
                    PatchEdit.getDesktop().add(frm);
                    frm.moveToDefaultLocation();
                    frm.setVisible(true);
                // hack for old Java bug
                /*
                if (frm instanceof PatchEditorFrame)
                    for (int i = 0; i < ((PatchEditorFrame) frm).sliderList.size(); i++) {
                    JSlider slider = (JSlider) ((PatchEditorFrame) frm).sliderList.get(i);
                    Dimension dim = slider.getSize();
                    if (dim.width > 0) {
                        dim.width++;
                        slider.setSize(dim);
                    }
                    }
                */
                try {
                    frm.setSelected(true);
                } catch (PropertyVetoException e) {
                    ErrorMsg.reportStatus(e);
                }
                }
            } catch (Exception ex) {
                ErrorMsg.reportError("Error",
                         "Error in PatchEditor.", ex);
            }
            }
        }
        Worker w = new Worker();
        w.setDaemon(true);
        w.start();
    }

    private static class ExportAction extends AbstractAction {
        public ExportAction(Map mnemonics) {
            super("Export...", null);
            mnemonics.put(this, new Integer('O'));
            this.setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            CompatibleFileDialog fc3 = new CompatibleFileDialog();
            FileFilter type1 = new ExtensionFilter("Sysex Files (*.syx)", ".syx");
            fc3.addChoosableFileFilter(type1);
            fc3.setFileFilter(type1);
            fc3.setCurrentDirectory(new File (AppConfig.getSysexPath()));
            if (fc3.showSaveDialog(PatchEdit.getInstance()) != JFileChooser.APPROVE_OPTION)
		return;
	    File file = fc3.getSelectedFile();
	    try {
		if (getSelectedFrame() == null) {
		    ErrorMsg.reportError("Error",
					 "Patch to export must be hilighted\n"
					 + "in the currently focuses Library");
		} else {
		    if (!file.getName().toUpperCase().endsWith(".SYX"))
			file = new File(file.getPath() + ".syx");
		    if (file.exists())
			if (JOptionPane.showConfirmDialog(null,
							  "Are you sure?",
							  "File Exists",
							  JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
			    return;

		    getSelectedFrame().exportPatch(file);
		}
	    } catch (IOException ex) {
		ErrorMsg.reportError("Error", "Unable to Save Exported Patch", ex);
	    }
	}
    }

    private static class GetAction extends AbstractAction {
	//-----------------------------------------------------------------
	// Constructor: GetAction
	//-----------------------------------------------------------------
	public GetAction(Map mnemonics) {
	    super("Get...", null);
	    mnemonics.put(this, new Integer('G'));
	    this.setEnabled(false);
	}

	//-----------------------------------------------------------------
	// GetAction->actionPerformed
	//-----------------------------------------------------------------
	public void actionPerformed(ActionEvent e) {
	    SysexGetDialog myDialog = new SysexGetDialog(PatchEdit.getInstance());
	    myDialog.setVisible(true);
	}

    } // End SubClass: GetAction

    private static class UploadAction extends AbstractAction {
	public UploadAction(Map mnemonics) {
	    super("Upload...", null);
	    mnemonics.put(this, new Integer('U'));
	    this.setEnabled(false);
	}

	public void actionPerformed(ActionEvent e) {
	    UploadPatchDialog myDialog = new UploadPatchDialog(PatchEdit.getInstance());
	    myDialog.setVisible(true);
	}

    }

    private static class ImportAction extends AbstractAction {
        public ImportAction(Map mnemonics) {
            super("Import...", null);
            mnemonics.put(this, new Integer('I'));
            this.setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            CompatibleFileDialog fc2 = new CompatibleFileDialog();
            FileFilter type1 = new ExtensionFilter("Sysex Files (*.syx)", ".syx");
	    // core.ImportMidiFile extracts Sysex Messages from MidiFile
            FileFilter type2 = new ExtensionFilter("MIDI Files (*.mid)" , ".mid");
            fc2.addChoosableFileFilter(type1);
            fc2.addChoosableFileFilter(type2);
            fc2.setFileFilter(type1);
            fc2.setCurrentDirectory(new File(AppConfig.getSysexPath()));
            if (fc2.showOpenDialog(PatchEdit.getInstance()) != JFileChooser.APPROVE_OPTION)
		return;
	    File file = fc2.getSelectedFile();
	    try {
		if (getSelectedFrame() == null)
		    ErrorMsg.reportError("Error", "Library to Import Patch\n into Must be in Focus");
		else
		    getSelectedFrame().importPatch(file);
	    } catch (IOException ex) {
		ErrorMsg.reportError("Error", "Unable to Load Sysex Data", ex);
	    }
	}
    }

    private static class NewAction extends AbstractAction {
        public NewAction(Map mnemonics) {
	    super("New Library", null);
	    mnemonics.put(this, new Integer('N'));
        }
        public void actionPerformed(ActionEvent e) {
	    addLibraryFrame(new LibraryFrame());
	}
    }

    private static class NewSceneAction extends AbstractAction {
        public NewSceneAction(Map mnemonics) {
            super("New Scene", null);
        }

        public void actionPerformed(ActionEvent e) {
            addLibraryFrame(new SceneFrame());
        }
    }

    private static class TransferSceneAction extends AbstractAction {
        public TransferSceneAction(Map mnemonics) {
            super("Transfer Scene", null); // show a dialog frame???
            // mnemonics.put(this, new Integer('S'));
            this.setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            try {
		((SceneFrame) getSelectedFrame()).sendScene();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Scene Library must be the selected window.", ex);
	    }
        }
    }

    private static class UpdateSceneAction extends AbstractAction {// wirski@op.pl
        public UpdateSceneAction(Map mnemonics) {
            super("Update Scene", null);
            setEnabled(false);
        }
        public void actionPerformed(ActionEvent e) {
            try {
                ((SceneFrame) getSelectedFrame()).updateScene();
            } catch (Exception ex) {
                ErrorMsg.reportError("Error", "Scene Library must be the selected window.", ex);
            }
        }
    }

    private static class OpenAction extends AbstractAction {
        static final FileFilter filter;
        static {
            String lext = LibraryFrame.FILE_EXTENSION;
            String sext = SceneFrame.FILE_EXTENSION;
            filter = new ExtensionFilter("JSynthLib Library/Scene Files (*" + lext
                    + ", *" + sext + ")", new String[] { lext, sext });
        }

        public OpenAction(Map mnemonics) {
            super("Open...", null);
            mnemonics.put(this, new Integer('O'));
        }
        public void actionPerformed(ActionEvent e) {
            CompatibleFileDialog fc = new CompatibleFileDialog();
            fc.setCurrentDirectory(new File(AppConfig.getLibPath()));
            fc.addChoosableFileFilter(filter);
            fc.setFileFilter(filter);

            if (fc.showOpenDialog(PatchEdit.getInstance()) == JFileChooser.APPROVE_OPTION)
                openFrame(fc.getSelectedFile());
        }
    }

    private static class SaveAction extends AbstractAction {
        public SaveAction(Map mnemonics) {
	    super("Save", null);
	    this.setEnabled(false);
	    mnemonics.put(this, new Integer('S'));
        }

        public void actionPerformed(ActionEvent e) {
            saveFrame();
        }
    }

    private static class SaveAsAction extends AbstractAction {
        public SaveAsAction(Map mnemonics) {
	    super("Save As...", null);
	    this.setEnabled(false);
	    mnemonics.put(this, new Integer('A'));
        }

        public void actionPerformed(ActionEvent e) {
            saveFrameAs();
        }
    }

    private static class ExitAction extends AbstractAction {
        public ExitAction(Map mnemonics) {
	    super("Exit", null);
	    mnemonics.put(this, new Integer('X'));
        }

        public void actionPerformed(ActionEvent e) {
            PatchEdit.getDesktop().closingProc();
        }
    }

    private static class ExtractAction extends AbstractAction {
        public ExtractAction(Map mnemonics) {
            super("Extract", null);
            mnemonics.put(this, new Integer('E'));
            this.setEnabled(false);
        }

        public void actionPerformed(ActionEvent e) {
            try {
		((AbstractLibraryFrame) getSelectedFrame()).extractSelectedPatch();
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Can not Extract (Maybe its not a bank?)", ex);
	    }
        }
    }

    private static class SortAction extends AbstractAction {
        public SortAction(Map mnemonics) {
            super("Sort...", null);
            this.setEnabled(false);
            mnemonics.put(this, new Integer('R'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		SortDialog sd = new SortDialog(PatchEdit.getInstance());
		sd.setVisible(true);
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Library to Sort must be Focused", ex);
	    }
        }
    }

    private static class SearchAction extends AbstractAction {
        public SearchAction(Map mnemonics) {
            super("Search...", null);
            this.setEnabled(false);
            mnemonics.put(this, new Integer('E'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		if (searchDialog == null)
		    searchDialog = new SearchDialog(PatchEdit.getInstance());
		searchDialog.setVisible(true);
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Library to Sort must be Focused", ex);
	    }
        }
    }

    private static class ImportAllAction extends AbstractAction {
        public ImportAllAction(Map mnemonics) {
            super("Import All...", null);
            this.setEnabled(false);
	    mnemonics.put(this, new Integer('A'));
        }

        public void actionPerformed(ActionEvent e) {
            try {
		CompatibleFileDialog fc = new CompatibleFileDialog();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (AppConfig.getLibPath() != null)
		    fc.setSelectedFile(new File(AppConfig.getLibPath()));
		if (fc.showDialog(PatchEdit.getInstance(),
				  "Choose Import All Directory") != JFileChooser.APPROVE_OPTION)
		    return;
		File file = fc.getSelectedFile();

		ImportAllDialog sd = new ImportAllDialog(PatchEdit.getInstance(), file);
		sd.setVisible(true);
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to Import Patches", ex);
	    }
        }
    }

    private static class DeleteDuplicatesAction extends AbstractAction {
        public DeleteDuplicatesAction(Map mnemonics) {
            super("Delete Dups...", null);
            this.setEnabled(false);
            mnemonics.put(this, new Integer('D'));
        }

        public void actionPerformed(ActionEvent e) {
	    if (JOptionPane.showConfirmDialog
		(null,
		 "This Operation will change the ordering of the Patches. Continue?",
		 "Delete Duplicate Patches",
		 JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
		return;
	    PatchEdit.showWaitDialog("Deleting duplicates...");
            try {
		((LibraryFrame) getSelectedFrame()).deleteDuplicates();
	    } catch (Exception ex) {
	        PatchEdit.hideWaitDialog();
		ErrorMsg.reportError("Error", "Library to Delete Duplicates in must be Focused", ex);
		return;
	    }
	    PatchEdit.hideWaitDialog();
        }
    }

    private static class NewPatchAction extends AbstractAction {
        public NewPatchAction(Map mnemonics) {
            super("New Patch...", null);
            this.setEnabled(false);
	    mnemonics.put(this, new Integer('N'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		NewPatchDialog np = new NewPatchDialog(PatchEdit.getInstance());
		np.setVisible(true);
		IPatch p = np.getNewPatch();
		if (p != null)
		    getSelectedFrame().pastePatch(p);
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to create this new patch.", ex);
	    }
        }
    }

    private static class PrefsAction extends AbstractAction {
        public PrefsAction(Map mnemonics) {
            super("Preferences...", null);
	    mnemonics.put(this, new Integer('P'));
        }

        public void actionPerformed(ActionEvent e) {
            PatchEdit.showPrefsDialog();
        }
    }

    private static class CrossBreedAction extends AbstractAction {
        public CrossBreedAction(Map mnemonics) {
            super("Cross Breed...", null);
            this.setEnabled(false);
            mnemonics.put(this, new Integer('B'));
        }
        public void actionPerformed(ActionEvent e) {
            try {
		CrossBreedDialog xbd = new CrossBreedDialog(PatchEdit.getInstance());
		xbd.setVisible(true);
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to perform Crossbreed. (No Library selected?)", ex);
	    }
        }
    }

    private static class NextFaderAction extends AbstractAction {
        public NextFaderAction(Map mnemonics) {
	    super("Go to Next Fader Bank", null);
	    mnemonics.put(this, new Integer('F'));
        }
        public void actionPerformed(ActionEvent e) {
            if (!(getSelectedFrame() instanceof PatchEditorFrame))
		return;
            PatchEditorFrame pf = (PatchEditorFrame) getSelectedFrame();
            pf.nextFader();
	    return;
        }
    }

    private static class PrevFaderAction extends AbstractAction {
        public PrevFaderAction(Map mnemonics) {
	    super("Go to Previous Fader Bank", null);
	    //mnemonics.put(this, new Integer('F'));
        }
        public void actionPerformed(ActionEvent e) {
            if (!(getSelectedFrame() instanceof PatchEditorFrame))
		return;
            PatchEditorFrame pf = (PatchEditorFrame) getSelectedFrame();
            pf.prevFader();
	    return;
        }
    }

    private static class DocsAction extends AbstractAction {
        public DocsAction(Map mnemonics) {
	    super("Help", null);
	    this.setEnabled(true);
	    mnemonics.put(this, new Integer('H'));
        }
        public void actionPerformed(ActionEvent e) {
	    try {
		if (docWin == null)
		    docWin = new DocumentationWindow("text/html", "file:doc/documentation.html");
		docWin.setVisible(true);
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to show Documentation)", ex);
	    }
        }
    }

    private static class LicenseAction extends AbstractAction {
        public LicenseAction(Map mnemonics) {
	    super("License", null);
	    this.setEnabled(true);
	    mnemonics.put(this, new Integer('L'));
        }
        public void actionPerformed(ActionEvent e) {
	    try {
		if (licWin == null)
		    licWin = new DocumentationWindow("text/plain", "file:license.txt");
		licWin.setVisible(true);
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to show Documentation)", ex);
	    }
        }
    }

    private static class HomePageAction extends AbstractAction {
        public HomePageAction(Map mnemonics) {
	    super("JSynthLib Home Page", null);
	    this.setEnabled(true);
	    mnemonics.put(this, new Integer('P'));
        }
        public void actionPerformed(ActionEvent e) {
	    try {
		if (hpWin == null)
		    // www.gnu.org is simple enough for J2SE 1.4.2.
		    //hpWin = new DocumentationWindow("text/html", "http://www.gnu.org/");
		    hpWin = new DocumentationWindow("text/html", "http://www.jsynthlib.org/");
		hpWin.setVisible(true);
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to show Documentation)", ex);
	    }
        }
    }

    private static class MonitorAction extends AbstractAction {
        public MonitorAction(Map mnemonics) {
	    super("MIDI Monitor", null);
	    this.setEnabled(true);
	    mnemonics.put(this, new Integer('M'));
        }
        public void actionPerformed(ActionEvent e) {
	    try {
		if (midiMonitor == null)
		    midiMonitor = new MidiMonitor();
		midiMonitor.setVisible(true);
	    } catch (Exception ex) {
		ErrorMsg.reportError("Error", "Unable to show MIDI Monitor)", ex);
	    }
        }
    }

    /**
     * Output string to MIDI Monitor Window.  Use MidiUtil.log()
     * instead of this.
     *
     * @param s string to be output
     */
    static void midiMonitorLog(String s) {
	if (midiMonitor != null && midiMonitor.isVisible())
	    midiMonitor.log(s);
    }

    ////////////////////////////////////////////////////////////////////////
    static class ExtensionFilter extends FileFilter {
	private String[] exts;
	private String desc;

	public ExtensionFilter (String desc, String exts) {
	    this (desc, new String[] {exts});
	}

	public ExtensionFilter (String desc, String[] exts) {
	    this.desc = desc;
	    this.exts = (String[]) exts.clone();
	}

	public boolean accept (File file) {
	    if (file.isDirectory()) {
		return true;
	    }
	    int count = exts.length;
	    String path = file.getAbsolutePath();
	    for (int i = 0; i < count; i++) {
		String ext = exts[i];
		if (path.toLowerCase().endsWith(ext.toLowerCase())
		    && (path.charAt(path.length() - ext.length()) == '.')) {
		    return true;
		}
	    }
	    return false;
	}

	public String getDescription() {
	    return (desc == null ? exts[0] : desc);
	}
    }
}
