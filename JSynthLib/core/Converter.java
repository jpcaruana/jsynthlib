package core;
/**This is a special subclass of Driver which simply converts any patches it's associated with to another
 * format. The Driver supporting the new Driver format must come later in the Driver Chain */
public class Converter extends Driver
{
    public boolean supportsPatch (StringBuffer patchString,Patch p)
    {
   /*   Old code
    Integer intg=new Integer (0);
        System.out.println ("Converter support Patch "+manufacturer+" "+model+" "+sysexID);
        if ((patchSize!=p.sysex.length) && (patchSize!=0)) return false;
        System.out.println ("Size ok");
        StringBuffer driverString=new StringBuffer (sysexID);
        for (int j=0;j<driverString.length ();j++)
            if (driverString.charAt (j)=='*') driverString.setCharAt (j,patchString.charAt (j));
        if ( (driverString.toString ().equalsIgnoreCase (patchString.toString ().substring (0,driverString.length ()))))
     */
        Integer intg=new Integer (0);
        
        if ((patchSize!=p.sysex.length) && (patchSize!=0)) return false;
      
        StringBuffer driverString=new StringBuffer (sysexID);
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

