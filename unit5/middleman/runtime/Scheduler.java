package runtime;

import java.lang.Thread;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class Scheduler extends Thread {
	
	class QueueElement {
		String s;
		Object d;
		
		QueueElement(String si, Object di) {
			s = si;
			d = di;
		}
	}

	/* This simplified scheduler only has one single state machine */
	private IStateMachineData stm;
	private BlockingDeque<QueueElement> inputQueue = new LinkedBlockingDeque<QueueElement>();
	private String name;

	public Scheduler(IStateMachineData stm) {
		this.stm = stm;
		this.name = "Scheduler";
	}

	public Scheduler(IStateMachineData stm, String name) {
		this.stm = stm;
		this.name = name;
	}

	public void run() {
		boolean running = true;
		while(running) {
			try {
				// wait for a new event arriving in the queue
				QueueElement event = inputQueue.take();

				// execute a transition
				log(name + ": firing state machine with event: " + event.s);
				int result = stm.fire(event.s, event.d, this);
				if(result==IStateMachineData.DISCARD_EVENT) {
					log(name + ": Discarded Event: " + event.s);
				} else if(result==IStateMachineData.TERMINATE_SYSTEM) {
					log(name + ": Terminating System... Good bye!");
					running = false;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Normal events are enqueued at the end of the queue.
	 * @param event - the name of the event
	 */
	public synchronized void addToQueueLast(String eventId, Object d) {
		inputQueue.addLast(new QueueElement(eventId,d)); 
	}

	public synchronized void addToQueueLast(String eventId) {
		inputQueue.addLast(new QueueElement(eventId, null));
	}
	/**
	 * Timeouts are added at the first place of the queue.
	 * @param event - the name of the timer
	 */
	public synchronized void addToQueueFirst(String timerId) {
		inputQueue.addFirst(new QueueElement(timerId, null));
	}

	private void log(String message) {
		System.out.println(message);
	}
}
