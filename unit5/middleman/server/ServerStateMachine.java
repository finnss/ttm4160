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

import static server.MQTTclient.*;


public class ServerStateMachine extends Thread implements IStateMachineData {

    public static final String MESSAGE_RECEIVED = "MESSAGE_RECEIVED";

    private enum STATES {WAIT_STATE}

    protected STATES state = STATES.WAIT_STATE;

    Scheduler scheduler;
    MQTTclient mqttClient;

    @Override
    public int fire(String event, Object object, Scheduler scheduler) {
        if (state == STATES.WAIT_STATE) {
            if (event.equals(MESSAGE_RECEIVED)) {
                try {
                    JSONObject payload = null;
                    payload = new JSONObject((String) object);
                    System.out.println("Received payload in state machine");
                    System.out.println(payload.toString());

                    // Forward received bathroom to
                    HTTPClient.postRequest((JSONObject) payload, "bathrooms");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // mqttClient.sendMessage(payload.toString());

                state = STATES.WAIT_STATE;
                return EXECUTE_TRANSITION;
            } else {
                System.out.println("Unexpected Event received in Wait State.");
                    /*
                try {
                    String bathrooms = getBathrooms();
                    System.out.println("Bathrooms: " + bathrooms);

                    addBathroom(new JSONObject("{\"location\":{\"lat\":\"someLat\",\"long\":\"someLong\"},\"availability\":[false,false,false],\"roomName\":\"someName\"}"));
                    System.out.println("Added through state machine");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                */
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

        // Initial bathrooms and reservations
        try {
            System.out.println("Making initial test data");
            String bathroomsRaw = new String(Files.readAllBytes(Paths.get("server/bathrooms.json")), StandardCharsets.UTF_8);
            JSONArray bathrooms = new JSONArray(bathroomsRaw);
            for (int i = 0; i < bathrooms.length(); i++) {
                HTTPClient.postRequest((JSONObject) bathrooms.get(i), "bathrooms");
            }

            String reservationsRaw = new String(Files.readAllBytes(Paths.get("server/reservations.json")), StandardCharsets.UTF_8);
            JSONArray reservations = new JSONArray(reservationsRaw);
            for (int i = 0; i < reservations.length(); i++) {
                HTTPClient.postRequest((JSONObject) reservations.get(i), "reservations");
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        stm.mqttClient = mqttClient;
        stm.scheduler = s;
        s.run();
    }
}