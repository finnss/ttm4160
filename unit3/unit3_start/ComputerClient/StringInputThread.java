package ComputerClient;

import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class StringInputThread extends Thread {

    Scanner input;
    private static final int MAX_AVAILABLE = 1;
    Semaphore semaphore;
    StringInputStateMachine stateMachine;

    public StringInputThread(StringInputStateMachine stateMachine) {
        this.stateMachine = stateMachine;
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

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
