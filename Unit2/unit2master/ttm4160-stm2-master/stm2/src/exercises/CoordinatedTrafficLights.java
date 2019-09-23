package exercises;

import com.pi4j.io.gpio.*;
import exercises.trafficlight.PedButtonListener;
import exercises.trafficlight.TrafficLightControllerMachine;
import runtime.IStateMachine;
import runtime.Scheduler;
import runtime.Timer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static exercises.trafficlight.TrafficLightControllerMachine.EXTERNAL_SYNC;

public class CoordinatedTrafficLights implements IStateMachine {

    public static final String TIMER_1 = "t1", TIMER_2 = "t2", TIMER_3 = "t3", TIMER_4 = "t4", TIMER_5 = "t5", TIMER_6 = "t6";

    private enum STATES {START_STATE, OFFSET_WAIT, CYCLE_1_WAIT, CYCLE_2_WAIT}

    private Timer t1 = new Timer("t1");
    private Timer t2 = new Timer("t2");
    private Timer t3 = new Timer("t3");


    private final int
            CYCLE_TIME = 60000,
            OFFSET = 30000;

    protected STATES state = STATES.START_STATE;

    PrintWriter out1, out2, out3;

    @Override
    public int fire(String event, Scheduler scheduler) {
        if(state==STATES.START_STATE) {
            t1.start(scheduler, CYCLE_TIME);
            t2.start(scheduler, OFFSET);
            state = STATES.OFFSET_WAIT;
            return EXECUTE_TRANSITION;
        } else if(state==STATES.OFFSET_WAIT) {
            if (event.equals(TIMER_2)) {
                t3.start(scheduler, CYCLE_TIME);
                state = STATES.CYCLE_1_WAIT;
                return EXECUTE_TRANSITION;
            }
        } else if(state==STATES.CYCLE_1_WAIT) {
            if (event.equals(TIMER_1)) {
                // Send trigger 1/2
                if (out1 != null && out2 != null) {
                    out1.println(EXTERNAL_SYNC);
                    out2.println(EXTERNAL_SYNC);
                } else {
                    System.out.println("Connections 1 and/or 2 have not been set up yet.");
                }
                t1.start(scheduler, CYCLE_TIME);
                state = STATES.CYCLE_2_WAIT;
                return EXECUTE_TRANSITION;
            }
        } else if(state==STATES.CYCLE_2_WAIT) {
            if (event.equals(TIMER_3)) {
                // Send trigger 3
                if (out3 != null) {
                    out3.println(EXTERNAL_SYNC);
                } else {
                    System.out.println("Connection 3 has not been set up yet.");
                }
                t3.start(scheduler, CYCLE_TIME);
                state = STATES.CYCLE_1_WAIT;
                return EXECUTE_TRANSITION;
            }
        }
        return DISCARD_EVENT;
    }


    public static void main(String[] args) {
        CoordinatedTrafficLights synchronizer = new CoordinatedTrafficLights();
        Scheduler s = new Scheduler(synchronizer);
        s.start();

        int portNumber = 4325;

        try {
            ServerSocket serverSocket = new ServerSocket( portNumber);
            System.out.println("Listening on port " + portNumber + "...");
            // portNumber: An integer containing a port number 3 above 1024 to be accessed by the clients
            Socket clientSocket1 = serverSocket.accept();
            // Wait for a call from a client
            Socket clientSocket2 = serverSocket.accept();
            // Wait for a call from another client
            Socket clientSocket3 = serverSocket.accept();
            // Wait for a call from another client
            PrintWriter out1 = new PrintWriter(clientSocket1.getOutputStream(), true);
            synchronizer.out1 = out1;
            BufferedReader in1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
            PrintWriter out2 = new PrintWriter(clientSocket2.getOutputStream(), true);
            synchronizer.out2 = out2;
            BufferedReader in2 = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));
            PrintWriter out3 = new PrintWriter(clientSocket3.getOutputStream(), true);
            synchronizer.out3 = out3;
            BufferedReader in3 = new BufferedReader(new InputStreamReader(clientSocket3.getInputStream()));

            out1.println("Hello client 1"); // Write string toClient1 to  the first client
            out2.println("Hello client 2"); // Write string toClient1 to 13 the first client
            out3.println("Hello client 3"); // Write string toClient1 to 13 the first client

            String fromClient1, fromClient2 = null, fromClient3 = null;
            while (
                    (fromClient1 = in1.readLine()) != null ||
                    (fromClient2 = in2.readLine()) != null ||
                    (fromClient3 = in3.readLine()) != null) {
                // doSomething(fromClient1, fromClient2, fromClient3);
                System.out.println("Message received at server from one of the clients:");
                System.out.println(
                          fromClient1 != null ? "Client 1: " + fromClient1
                        : fromClient2 != null ? "Client 2: " + fromClient2
                        : "Client 3: " + fromClient3);
                System.out.println();
            }
            // Continuously read the input from the clients,7write a received string to variable fromClient1 orfromClient2,
            // and carry out doSomething(fromClient1, 18 fromClient2) if a string is received from one of the clients.
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                            + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }

    }
}
