package core; //TODO org.jsynthlib.midi;

import javax.sound.midi.*;

// emenaker 2003.03.25

public class MidiWrapperTransmitter implements Transmitter, Runnable {
	
	// Sleep for 1/40 of second between checking for new input
	private static final int SLEEP_TIME = 25;
	
	private Receiver receiver;
	private MidiWrapper driver;	 
	private int portnumber;
	private boolean listening;

	private MidiWrapperTransmitter() {
	}
	
	/**
	 * Constructor
	 * 
	 * @param driver MidiWrapper that we're supposed to listen to
	 * @param portnumber Port number that we're supposed to listen to
	 */
	private MidiWrapperTransmitter(MidiWrapper driver, int portnumber) {
		this.driver = driver;
		this.portnumber = portnumber;
	}
	
	/**
	 * Starts the listening thread. This begins polling the underlying MidiWrapper for new
	 * messages. It does NOT empty the wrapper's input queue at the start! If there are messages
	 * already in the wrapper's queue, a delivery attempt will be made. If the receiver is set
	 * to null, the messages will be discarded. - emenaker 2003.03.25
	 *
	 */
	public void startListening() {
		Thread thread = new Thread(this);
		thread.start();
	}
	
	/**
	 * Stops the listening thread. No messages will be delivered to the receiver AND
	 * no messages will be removed from the queue of the underlying MidiWrapper - emenaker 2003.03.25
	 *
	 */
	public void stopListening() {
		listening = false;
	}
	
	/**
	 * This is the run() method so that we can implement Runnable. We need a thread
	 * that polls the input of the MidiWrapper and sends the messages on to a
	 * Receiver - emenaker 2003.03.25
	 */
	public void run() {
		// TODO put in code to start listening to the input port
		while(listening) {
			try {
				if(driver != null) {
					while(driver.messagesWaiting(portnumber) > 0) {
						MidiMessage msg = driver.readMessage(portnumber);
						if(receiver != null) {
							receiver.send(msg,0);
						}
					}
				}
				Thread.sleep(SLEEP_TIME);
			} catch(Exception e) {
			}
		}
	}
	
	/*
	 * Methods to implement javax.sound.midi.Transmitter - emenaker 2003.03.25
	 *
	 */
	 
	public Receiver getReceiver() {
		return(receiver);
	}

	public void setReceiver(Receiver receiver) {
		this.receiver = receiver;
	}

	public void close() {
		// TODO Define the MidiWrapperTransmitter.close() method
		stopListening();
	}

}
