/*
 * Created on Mar 18, 2003
 *
 * To change this generated comment go to 
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package core; //TODO org.jsynthlib;

/**
 * @author emenaker - 2003.03.18
 *
 * This is an extension to the Exception class that allows for us to keep track of
 * whether the user has been notified of the problem. There are sometimes cases where
 * an exception is thrown at a very low level and we need to pop up a dialog box or 
 * warning message at that low level AND we also want to re-throw the exception. To
 * prevent the higher-level code from popping up more dialog boxes, we can use this
 * exception class to determine if the user has already been made aware of a problem
 * and has been told, in general terms anyway, how that will probably affect the
 * function of the higher-level code. This way, the exception can be re-thrown to
 * notify higher-level code, and that code will be able to make a better decision
 * on whether or not to interact with the user about it. - emenaker 2003.03.18 
 * 
 */
public class NotifyingException extends Exception {
	private boolean userNotified = false;

	public NotifyingException(String message) {
		super(message);
	}
	
	/**
	 * Set whether or not the user has been notified of the cause of this exception.
	 * @param userNotified
	 */
	public void setNotified(boolean userNotified) {
		this.userNotified = userNotified;
	}

	/**
	 * 
	 * @return Whether or not the user has already been notified
	 */
	public boolean isNotified() {
		return(userNotified);
	}
}
