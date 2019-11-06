package server;

import org.json.JSONArray;
import org.json.JSONException;
import runtime.IStateMachineData;
import runtime.Scheduler;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static server.HTTPClient.addBathroom;
import static server.HTTPClient.getBathrooms;
import static server.MQTTclient.*;


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
        if (state == STATES.WAIT_STATE) {
            if (event.equals(INPUT_ACQUIRED)) {
                JSONObject payload = (JSONObject) object;
                System.out.println("Received payload in state machine");
                System.out.println(payload.toString());

                // mqttClient.sendMessage(payload.toString());

                state = STATES.WAIT_STATE;
                return EXECUTE_TRANSITION;
            } else {
                try {
                    System.out.println("Unexpected Event received in Wait State.");

                    String bathrooms = getBathrooms();
                    System.out.println("Bathrooms: " + bathrooms);

                    addBathroom(new JSONObject("{\"location\":{\"lat\":\"someLat\",\"long\":\"someLong\"},\"availability\":[false,false,false],\"roomName\":\"someName\"}"));
                    System.out.println("Added through state machine");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Event received while in unexpected state.");
        }
        return DISCARD_EVENT;
    }

    public static void main(String[] args) {
        ServerStateMachine stm = new ServerStateMachine();
        Scheduler s = new Scheduler(stm);

        // Set up MQTT client
        String myAddress = "pc";
        MQTTclient mqttClient = new MQTTclient(broker, myAddress, conf, s);
        mqttClient.subscribe(pc_topic);

        // Initial bathrooms
        try {
            System.out.println("Reading initial bathrooms");
            String bathroomsRaw = new String(Files.readAllBytes(Paths.get("server/bathrooms.json")), StandardCharsets.UTF_8);
            System.out.println(bathroomsRaw);
            JSONArray bathrooms = new JSONArray(bathroomsRaw);
            for (int i = 0; i < bathrooms.length(); i++) {
                System.out.println("Bathroom nr" + i);
                System.out.println(bathrooms.get(i));
                HTTPClient.addBathroom((JSONObject) bathrooms.get(i));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        stm.mqttClient = mqttClient;
        stm.scheduler = s;
        s.run();
    }
}