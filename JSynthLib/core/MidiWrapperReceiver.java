package core; //TODO org.jsynthlib.midi;

import javax.sound.midi.*;

// emenaker 2003.03.25

public class MidiWrapperReceiver implements Receiver {
	
	private int portnumber;
	private MidiWrapper driver;
	
	// Private default constructor... so they *have* to give us a port number
	private MidiWrapperReceiver() {
	}
	 
	public MidiWrapperReceiver(MidiWrapper driver, int portnumber) {
		this.driver = driver;
		this.portnumber = portnumber;
	}

	public void send(MidiMessage msg, long something) throws IllegalStateException {
		if(driver != null) {
			try {
				driver.send(portnumber,msg);
			} catch(Exception e) {
				System.err.println(e);
				e.printStackTrace();
			}
		}
	}

	public void close() {
		// TODO Define the MidiWrapperTransmitter.close() method
	}
}
