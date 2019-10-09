package ComputerClient;

import piClient.MQTTclient;
import runtime.IStateMachine;
import runtime.Scheduler;

import java.util.concurrent.Semaphore;

public class StringInputStateMachine implements IStateMachine {

    StringInputThread inputThread;

    public static final String INPUT_ACQUIRED = "InputAcquired";
    public MQTTclient mqttClient;

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

        // Set up MQTT client
        String broker = "tcp://broker.hivemq.com:1883";
        String myAddress = "pc";
        boolean conf = true;
        String topic = "ttm4160_Led";
        MQTTclient mqttClient = new MQTTclient(broker, myAddress, conf, s);
        mqttClient.subscribe(topic);
        stateMachine.mqttClient = mqttClient;

        s.start();
    }
}
