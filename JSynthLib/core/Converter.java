package core;
/**This is a special subclass of Driver which simply converts any patches it's associated with to another
 * format. The Driver supporting the new Driver format must come later in the Driver Chain */

/** @version: $Id$ */

public class Converter extends Driver
{
    public Converter() {
	super("Converter", "JSynthLib"); // Who is the auther?
    }

    public boolean supportsPatch (StringBuffer patchString,Patch p)
    {
 
        Integer intg=new Integer (0);
        
        if ((patchSize!=p.sysex.length) && (patchSize!=0)) return false;
      
        StringBuffer driverString=new StringBuffer (sysexID);
        if (patchString.length() <driverString.length()) return false;
        for (int j=0;j<driverString.length ();j++)
            if (driverString.charAt (j)=='*') driverString.setCharAt (j,patchString.charAt (j));
        return (driverString.toString ().equalsIgnoreCase (patchString.toString ().substring (0,driverString.length ())));
    }
    
    
  /*  public void convertPatch (Patch p)
    {}
    */
    public Patch[] extractPatch (Patch p)
    {
        return null;
    }
}

