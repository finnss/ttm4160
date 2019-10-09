package ComputerClient;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import piClient.MQTTclient;
import runtime.Scheduler;

import java.util.Scanner;
import java.util.concurrent.Semaphore;

import static ComputerClient.StringInputStateMachine.INPUT_ACQUIRED;

public class StringInputThread extends Thread {

    Scanner input;
    private static final int MAX_AVAILABLE = 1;
    Semaphore semaphore;
    Scheduler scheduler;
    MQTTclient mqttClient;

    public StringInputThread(Scheduler scheduler, MQTTclient mqttClient) {
        this.scheduler = scheduler;
        this.mqttClient = mqttClient;

        input = new Scanner(System.in);
        semaphore = new Semaphore(MAX_AVAILABLE, true);

        try {
            // Initial self-block
            System.out.println("Blocking self...");
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void releaseSemaphore() {
        System.out.println("Unblocking self...");
        semaphore.release();
    }

    private String readInput() {
        System.out.println("Please write input to be displayed on the pi!");
        System.out.print("> ");
        return input.nextLine();
    }

    public void run() {
        try {
            while (true) {
                System.out.println("Waiting for semaphore...");
                semaphore.acquire();
                System.out.println("Semaphore acquired!");
                String input = readInput();
                scheduler.addToQueueLast(INPUT_ACQUIRED);
                mqttClient.addToQueueLast(new MqttMessage(input.getBytes()));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
