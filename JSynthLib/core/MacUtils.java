package core;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.Action;
import javax.swing.SwingUtilities;

public final class MacUtils extends Application {

    private static MacUtils instance;

    private MacUtils() {
    }

    public static boolean isMac() {
	// Should this check for Aqua L&F too?
	return System.getProperty("os.name").startsWith("Mac OS X")
	    // Just to make sure.
	    && System.getProperty("java.vm.version").startsWith("1.4")
		// It's really confusing if screen menu bar isn't enabled
		&& "true".equals(System.getProperty("apple.laf.useScreenMenuBar"));
    }

    static void init(final Action exitAction,
		     final Action prefsAction,
		     final Action aboutAction) {
	instance = new MacUtils(); // to create a static context
	instance.addApplicationListener(new ApplicationAdapter() {
		public void handleAbout(ApplicationEvent e) {
		    final ActionEvent event =
			new ActionEvent(e.getSource(), 0, "About");
		    // opens dialog, so I think we need to do this to
		    // avoid deadlock
		    SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
				try {
				    aboutAction.actionPerformed(event);
				} catch (Exception e) {
				    ErrorMsg.reportStatus(e);
				}
			    }
			});
		    e.setHandled(true);
		}
		public void handleOpenFile(ApplicationEvent e) {
		    final File file = new File(e.getFilename());
		    SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
				try {
				    Actions.openFrame(file);
				} catch (Exception e) {
				    ErrorMsg.reportStatus(e);
				}
			    }
			});
		    e.setHandled(true);
		}
		public void handlePreferences(ApplicationEvent e) {
		    e.setHandled(true);
		    final ActionEvent event =
			new ActionEvent(e.getSource(), 0, "Preferences");
		    SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
				try {
				    prefsAction.actionPerformed(event);
				} catch (Exception e) {
				    ErrorMsg.reportStatus(e);
				}
			    }
			});
		}
		public void handleQuit(ApplicationEvent e) {
		    exitAction.actionPerformed(new ActionEvent(e.getSource(), 0,
							       "Exit"));
		    e.setHandled(true);
		}
	    });
	instance.setEnabledPreferencesMenu(true);
    }
}
