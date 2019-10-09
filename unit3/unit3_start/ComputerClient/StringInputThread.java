package ComputerClient;

import runtime.Scheduler;

import java.util.Scanner;
import java.util.concurrent.Semaphore;

import static ComputerClient.StringInputStateMachine.INPUT_ACQUIRED;

public class StringInputThread extends Thread {

    Scanner input;
    private static final int MAX_AVAILABLE = 1;
    Semaphore semaphore;
    Scheduler scheduler;

    public StringInputThread(Scheduler scheduler) {
        this.scheduler = scheduler;
        input = new Scanner(System.in);
        semaphore = new Semaphore(MAX_AVAILABLE, true);
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void releaseSemaphore() {
        semaphore.release();
    }

    private String readInput() {
        return input.nextLine();
    }

    public void run() {
        try {
            while (true) {
                semaphore.acquire();
                String input = readInput();
                scheduler.addToQueueLast(INPUT_ACQUIRED);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
