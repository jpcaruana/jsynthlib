package core;

import java.io.File;
import javax.swing.filechooser.FileFilter;

public class ExtensionFilter extends FileFilter {
    private String exts[];
    private String desc;

    public ExtensionFilter (String desc, String exts) {
      this (desc, new String[] {exts});
    }

    public ExtensionFilter (String desc, String exts[]) {
      this.desc = desc;
      this.exts = (String[])exts.clone();
    }

    public boolean accept (File file) {
	if (file.isDirectory()) {
	    return true;
	}
	int count = exts.length;
	String path = file.getAbsolutePath();
	for (int i = 0; i < count; i++) {
	    String ext = exts[i];
	    if (path.toUpperCase().endsWith(ext.toUpperCase())
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
