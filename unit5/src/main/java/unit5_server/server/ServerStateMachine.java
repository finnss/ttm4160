package main.java.unit5_server.server;

import org.json.JSONObject;
import main.java.unit5_server.runtime.EventWindow;
import main.java.unit5_server.runtime.Scheduler;
import main.java.unit5_server.runtime.IStateMachineData;

import static main.java.unit5_server.server.MQTTclient.*;

public class ServerStateMachine extends Thread implements IStateMachineData {

    public static final String INPUT_ACQUIRED = "InputAcquired", START_WRITING = "StartWriting",
            STOP_WRITING = "StopWriting", CHOOSE_FILE_NAME = "ChooseFileName";

    public static final String[] EVENTS = {START_WRITING, STOP_WRITING, CHOOSE_FILE_NAME};

    private enum STATES {WAIT_STATE}
    protected STATES state = STATES.WAIT_STATE;

    Scheduler scheduler;
    MQTTclient mqttClient;

    @Override
    public int fire(String event, Object object, Scheduler scheduler) {
        if(state==STATES.WAIT_STATE) {
            if (event.equals(INPUT_ACQUIRED)) {
                JSONObject payload = (JSONObject) object;
                System.out.println("Received payload in state machine");
                System.out.println(payload.toString());

                // mqttClient.sendMessage(payload.toString());

                state = STATES.WAIT_STATE;
                return EXECUTE_TRANSITION;
            }
            else {
                System.out.println("Unexpected Event received in Wait State.");
            }
        } else {
            System.out.println("Event received while in unexpected state.");
        }
        return DISCARD_EVENT;
    }

    public void run() {
        Scheduler s = new Scheduler(this);

        // Set up MQTT client
        String myAddress = "pc";
        MQTTclient mqttClient = new MQTTclient(broker, myAddress, conf, s);
        mqttClient.subscribe(pc_topic);

        this.mqttClient = mqttClient;
        this.scheduler = s;

        s.start();
    }
}