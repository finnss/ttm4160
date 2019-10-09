package ComputerClient;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import runtime.IStateMachine;
import runtime.Scheduler;

import java.util.concurrent.Semaphore;

public class StringInputStateMachine implements IStateMachine {

    StringInputThread inputThread;

    public static final String INPUT_ACQUIRED = "InputAcquired";

    private enum STATES {WAIT_STATE}
    protected STATES state = STATES.WAIT_STATE;

    @Override
    public int fire(String event, Scheduler scheduler) {
        if(state==STATES.WAIT_STATE) {
            if (event.equals(INPUT_ACQUIRED)) {
                state = STATES.WAIT_STATE;
                return EXECUTE_TRANSITION;
            } else {
                System.out.println("Unexpected event received");
            }
        }
        return DISCARD_EVENT;
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
