
package core; //TODO org.jsynthlib.midi;

/** This defines a callback for classes that want to get notified when the user
 *  changes the underlying midi driver
 * @author Joe Emenaker
 * @version $Id$
 */

public interface MidiDriverChangeListener {
	public void midiDriverChanged(MidiWrapper driver);
}