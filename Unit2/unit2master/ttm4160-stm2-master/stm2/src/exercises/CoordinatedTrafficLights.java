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
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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
                t1.start(scheduler, CYCLE_TIME);
                state = STATES.CYCLE_2_WAIT;
                return EXECUTE_TRANSITION;
            }
        } else if(state==STATES.CYCLE_2_WAIT) {
            if (event.equals(TIMER_3)) {
                // Send trigger 3
                t3.start(scheduler, CYCLE_TIME);
                state = STATES.CYCLE_1_WAIT;
                return EXECUTE_TRANSITION;
            }
        }
    }


    public static void main(String[] args) {
        IStateMachine synchronizer = new CoordinatedTrafficLights();
        Scheduler s = new Scheduler(synchronizer);
        s.start();

        List<String> hostNames = new ArrayList();
        hostNames.add("");
        hostNames.add("");
        hostNames.add("");
        int portNumber = 3131313;

        for (String hostname : hostNames) {
            try {
                Socket clientSocket = new Socket(hostname, portNumber);
                // hostname: A string containing the computer name or IP address of the server
                // portNumber: An integer containing a port number 4 above 1024 supported by the server
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out.println("toServer"); // Write string toServer to the 8server

                while ((fromServer = in.readLine()) != null) {
                    doSomething(fromServer);
                } // Continuously read the input from the connection, 12 write a received string to variable fromServer,
                // and carry out doSomething(fromServer) afterwards.
            } catch (IOException e) {
                System.out.println("Exception caught when trying to 16 listen on port "
                        + portNumber + " or listening for a connection");
                System.out.println(e.getMessage());
            }
        }

    }
}
