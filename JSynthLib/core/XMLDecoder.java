package core;

/**
 * @author ribrdb
 *
 */
public interface XMLDecoder {

	public Object getParameter(String name);
	public Object getParameter(String message, String name);
	public Object getParameter(String group, String message, String name);
	
	public void setParameter(String name, Object value);
	public void setParameter(String message, String name, Object value);
	public void setParameter(String group, String message, String name, Object value);
	
}
