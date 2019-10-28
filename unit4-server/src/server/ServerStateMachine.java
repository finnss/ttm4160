package server;

import datastorage.ChooseFileName;
import datastorage.WriteFile;
import org.json.JSONObject;
import runtime.EventWindow;
import runtime.IStateMachineData;
import runtime.SchedulerData;

import java.awt.*;
import java.io.File;

class ServerStateMachine implements IStateMachineData {

    public static final String INPUT_ACQUIRED = "InputAcquired", START_WRITING = "StartWriting",
            STOP_WRITING = "StopWriting", CHOOSE_FILE_NAME = "ChooseFileName";

    public static final String[] EVENTS = {START_WRITING, STOP_WRITING, CHOOSE_FILE_NAME};

    private enum STATES {WAIT_STATE}
    protected STATES state = STATES.WAIT_STATE;

    SchedulerData scheduler;
    UdpClient udpClient;

    private boolean shouldWrite = false;
    private File file = null;
    private WriteFile fileWriter;

    @Override
    public int fire(String event, Object object, SchedulerData scheduler) {
        if(state==STATES.WAIT_STATE) {
            if (event.equals(INPUT_ACQUIRED)) {
                JSONObject payload = (JSONObject) object;
                System.out.println("Received payload in state machine");
                System.out.println(payload.toString());

                udpClient.sendTimestamp(payload);

                if (shouldWrite) {
                    String filePayload = "";

                    filePayload += payload.get("currentTimestamp") + "; ";
                    filePayload += payload.get("communicationTechnology") + "; ";
                    filePayload += payload.get("latitude") + "; ";
                    filePayload += payload.get("longitude") + "; ";
                    filePayload += payload.get("signalStrength") + "; ";
                    filePayload += payload.get("roundTripDelay") + "\r\n";

                    System.out.println("filePayload: " + filePayload);

                    fileWriter.writeStringToFile(filePayload);
                }
                state = STATES.WAIT_STATE;
                return EXECUTE_TRANSITION;

            } else if (event.equals(CHOOSE_FILE_NAME)) {
                new ChooseFileName(true, scheduler);
            } else if (event.equals(START_WRITING)) {
                fileWriter = new WriteFile(file, scheduler);
                String qgisHeader = "Time Slot; Network Type; Latitude; Longitude; Signal Strength; Round Trip Delay\r\n";
                fileWriter.writeStringToFile(qgisHeader);
                shouldWrite = true;

                state = STATES.WAIT_STATE;
                return EXECUTE_TRANSITION;

            } else if (event.equals(STOP_WRITING)) {
                shouldWrite = false;
                fileWriter.closeFile();

                state = STATES.WAIT_STATE;
                return EXECUTE_TRANSITION;
            } else if (event.equals("ChooseFilenameSave")) {
                file = (File) object;

                state = STATES.WAIT_STATE;
                return EXECUTE_TRANSITION;
            } else if (event.equals("ChooseFilenameCancel")) {
                file = null;

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

    public static void main(String args[]) {

        ServerStateMachine stateMachine = new ServerStateMachine();
        SchedulerData s = new SchedulerData(stateMachine);

        Thread th;
        Runnable r;
        UdpClient udpClient = new UdpClient(s);
        r = () -> udpClient.start();

        th = new Thread(r);
        th.start();

        stateMachine.udpClient = udpClient;
        stateMachine.scheduler = s;

        EventWindow gui = new EventWindow(EVENTS, s);
        gui.show();

        s.start();
    }
}