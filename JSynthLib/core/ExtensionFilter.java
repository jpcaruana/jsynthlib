package core;
import java.io.File;
import java.io.FilenameFilter;
public class ExtensionFilter implements FilenameFilter {
    private String exts[];
    private String desc;
    public ExtensionFilter (String desc, String exts) {
      this (desc, new String[] {exts});
    }
    public ExtensionFilter (String desc, String exts[]) {
      this.desc = desc;
      this.exts = (String[])exts.clone();
    }
    public boolean accept (File dir, String name) {
      for (int i =0;i < exts.length;i++) {
        String ext = exts[i];
        if (name.toUpperCase().endsWith(ext.toUpperCase()) && (name.charAt(name.length()-ext.length()) == '.')) {
          return true;
        }
      }
      return false;
    }		
 }
