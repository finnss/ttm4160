package ComputerClient;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import piClient.MQTTclient;
import runtime.IStateMachine;
import runtime.Scheduler;
import static piClient.MQTTclient.broker;
import static piClient.MQTTclient.conf;
import static piClient.MQTTclient.topic;

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
                MqttMessage message = mqttClient.takePayload();
                mqttClient.sendMessage(topic, message);
                state = STATES.WAIT_STATE;
                return EXECUTE_TRANSITION;
            } else {
                try {
                    System.out.println("Initial fire event");
                    getInput();
                    state = STATES.WAIT_STATE;
                    return EXECUTE_TRANSITION;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return DISCARD_EVENT;
    }

    public void getInput() throws InterruptedException {
        System.out.println("Sending unblock signal");
        inputThread.releaseSemaphore();
    }

    public static void main (String[] args) {
        StringInputStateMachine stateMachine = new StringInputStateMachine();
        Scheduler s = new Scheduler(stateMachine);

        // Set up MQTT client
        String myAddress = "pc";
        MQTTclient mqttClient = new MQTTclient(broker, myAddress, conf, s);
        mqttClient.subscribe(topic);
        stateMachine.mqttClient = mqttClient;

        StringInputThread inputThread = new StringInputThread(s, mqttClient);

        stateMachine.inputThread = inputThread;

        inputThread.start();
        s.start();
    }
}
