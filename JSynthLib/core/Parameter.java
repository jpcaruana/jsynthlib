package core;

public interface Parameter {
    public String getName();

    /* For numeric parameters */
    public int getMin();
    public int getMax();
    public int get(IPatch p);
    public void set(IPatch p, int val);
    
    /* For list parameters */
    public String[] getValues();
    
    /* For String parameters */
    public String getString(IPatch p);
    public int getLength();
    public void set(IPatch p, String stringval);
}
