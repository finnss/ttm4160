package ComputerPiSharedCode;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.*;

public class FreePoolHandler extends Thread {

    private LinkedList<MqttMessage> blockedMessages;
    private LinkedList<String> availableFreepools;
    private int numberOfFreepools;

    private List<String> freepoolNames;

    public FreePoolHandler(int numberOfFreepools) {
        this.blockedMessages = new LinkedList<>();
        this.availableFreepools = new LinkedList<>();
        this.freepoolNames = new ArrayList<>();
        this.numberOfFreepools = numberOfFreepools;

        for (int i = 0; i < numberOfFreepools; i++) {
            String freepoolName = "FREEPOOL_" + (i + 1);
            availableFreepools.add(freepoolName);
            freepoolNames.add(freepoolName);
            System.out.println("Adding available freepool: " + freepoolName);
        }
    }

    public String getFreepoolOrQueue(MqttMessage message) {
        if (availableFreepools.size() == 0) {
            blockedMessages.add(message);
            return null; // Returning null means no freepool was available
        } else {
            String takenFreepool = availableFreepools.removeFirst();
            return takenFreepool;
        }
    }

    public void handleFreepoolArrived(String freepool) {
        if (!freepoolNames.contains(freepool)) {
            throw new IllegalArgumentException("Freepool with invalid name was given: " + freepool);
        }

        // TODO: Is this logic sound? It's here to remove redundant frepools caused by both communicating
        // parties starting with the same freepools
        if (!availableFreepools.contains(freepool)) {
            availableFreepools.add(freepool);
        }
        if (blockedMessages.size() > 0) {

        }
    }


    /*
    private String readInput() {
        System.out.println("Please write input to be displayed on the pi!");
        System.out.print("> ");
        return input.nextLine();
    }
    */


    public void run() {
        System.out.println("Running freepoolhandler");
        System.out.println(numberOfFreepools);
    }
}
