package ComputerClient;

import runtime.IStateMachine;
import runtime.Scheduler;

import java.util.concurrent.Semaphore;

public class StringInputStateMachine implements IStateMachine {

    StringInputThread inputThread;

    public static final String INPUT_ACQUIRED = "InputAcquired";

    private enum STATES {LISTEN_STATE, WRITING_STATE}

    @Override
    public int fire(String event, Scheduler scheduler) {
        return 0;
    }

    public void getInput() throws InterruptedException {
        inputThread.releaseSemaphore();
    }

    public static void main (String[] args) {
        StringInputStateMachine stateMachine = new StringInputStateMachine();
        Scheduler s = new Scheduler(stateMachine);
        StringInputThread inputThread = new StringInputThread(s);

        stateMachine.inputThread = inputThread;
    }
}
