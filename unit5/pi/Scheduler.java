package runtime;

import java.lang.Thread;
import java.util.HashMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import com.pi4j.util.Console;
public class Scheduler extends Thread {

	protected static final Console console = new Console();
	/* This simplified scheduler only has one single state machine */
	private IStateMachine stm;
	private BlockingDeque<String> inputQueue = new LinkedBlockingDeque<String>();
	private HashMap<String, String> displayMessages = new HashMap<>();
	private String name;

	public Scheduler(IStateMachine stm) {
		this.stm = stm;
		this.name = "Scheduler";
	}

	public Scheduler(IStateMachine stm, String name) {
		this.stm = stm;
		this.name = name;
	}

	public void run() {
		boolean running = true;
		console.print(String.format("Starting STM\n ")); // print 4 digits with leading zeros
		while(running) {
			try {
				// wait for a new event arriving in the queue
				String event = inputQueue.take();

				console.print(String.format("Run STM\n ")); // print 4 digits with leading zeros
				// execute a transition
				//log(name + ": firing state machine with event: " + event);
				int result = stm.fire(event, this);
				if(result==IStateMachine.DISCARD_EVENT) {
				//	log(name + ": Discarded Event: " + event);
				} else if(result==IStateMachine.TERMINATE_SYSTEM) {
				//	log(name + ": Terminating System... Good bye!");
					running = false;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void addDisplayMessage(String eventId, String payload) {
		displayMessages.put(eventId, payload);
	}

	/**
	 * Normal events are enqueued at the end of the queue.
	 * @param event - the name of the event
	 */
	public synchronized void addToQueueLast(String eventId) {
		inputQueue.addLast(eventId);
	}

	/**
	 * Timeouts are added at the first place of the queue.
	 * @param event - the name of the timer
	 */
	public synchronized void addToQueueFirst(String timerId) {
		inputQueue.addFirst(timerId);
	}

	private void log(String message) {
		System.out.println(message);
	}
}
