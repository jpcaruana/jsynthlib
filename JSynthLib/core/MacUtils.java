package core;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationListener;

public class MacUtils extends Application{

  protected static MacUtils instance;

  public static boolean isMac() {
    // Should this check for Aqua L&F too?
    return System.getProperty("os.name").startsWith("Mac OS X")
      // Just to make sure.
      && System.getProperty("java.vm.version").startsWith("1.4");
  }
  public static void init(ApplicationListener listener) {
    instance = new MacUtils();
    instance.addApplicationListener(listener);
    instance.setEnabledPreferencesMenu(true);
  }
}
