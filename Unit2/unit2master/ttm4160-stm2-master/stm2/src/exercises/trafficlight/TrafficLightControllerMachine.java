package exercises.trafficlight;

import com.pi4j.io.gpio.*;
import runtime.IStateMachine;
import runtime.Scheduler;
import runtime.Timer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static exercises.trafficlight.TrafficLightPI.TrafficLightsTypes;

public class TrafficLightControllerMachine implements IStateMachine {

	public static final String PEDESTRIAN_BUTTON_PRESSED = "Pedestrian Button", EXTERNAL_SYNC = "External sync",
	TIMER_1 = "t1", TIMER_2 = "t2", TIMER_3 = "t3", TIMER_4 = "t4", TIMER_5 = "t5", TIMER_6 = "t6";

	public static final String[] EVENTS = {PEDESTRIAN_BUTTON_PRESSED, EXTERNAL_SYNC};

	private enum STATES {S0, S1, S2, S3, S4, S5, S6}

	private Timer t1 = new Timer("t1");
	private Timer t2 = new Timer("t2");
	private Timer t3 = new Timer("t3");
	private Timer t4 = new Timer("t4");
	private Timer t5 = new Timer("t5");
	private Timer t6 = new Timer("t6");

	private final int
		CYCLE_TIME = 60000,
		CAR_YELLOW_TIME = 4000,
		BOTH_RED_TIME_1 = 4000,
		BOTH_RED_TIME_2 = 5000,
		CAR_RED_YELLOW_TIME = 2000,
		CAR_GREEN_TIME = 20000,
		PEDESTRIAN_GREEN_TIME = CYCLE_TIME - CAR_YELLOW_TIME - BOTH_RED_TIME_1 - BOTH_RED_TIME_2 - CAR_RED_YELLOW_TIME - CAR_GREEN_TIME;

	protected STATES state = STATES.S0;

	private TrafficLightPI cars = new TrafficLightPI(TrafficLightsTypes[0], true);
	private TrafficLightPI pedestrians = new TrafficLightPI(TrafficLightsTypes[1], false);

	private boolean pedestrianButtonIsPressed = false;

	public TrafficLightControllerMachine() {
		// initial transition
		cars.showGreen();
		pedestrians.showRed();
	}

	public int fire(String event, Scheduler scheduler) {
		if(state==STATES.S0) {
			if(event.equals(EXTERNAL_SYNC)) {
				if (pedestrianButtonIsPressed) {
					pedestrianButtonIsPressed = false;
					cars.showYellow();
					t1.start(scheduler, CAR_YELLOW_TIME);
					state = STATES.S1;
					return EXECUTE_TRANSITION;
				} else {
					state = STATES.S0;
					return EXECUTE_TRANSITION;
				}
			}
		} else if(state==STATES.S1) {
			if (event.equals(TIMER_1)) {
				cars.showRed();
				t2.start(scheduler, BOTH_RED_TIME_1);
				state = STATES.S2;
				return EXECUTE_TRANSITION;
			}
		} else if(state==STATES.S2) {
			if (event.equals(TIMER_2)) {
				pedestrians.showGreen();
				t3.start(scheduler, PEDESTRIAN_GREEN_TIME);
				state = STATES.S3;
				return EXECUTE_TRANSITION;
			}
		} else if(state==STATES.S3) {
			if (event.equals(TIMER_3)) {
				pedestrians.showRed();
				t4.start(scheduler, BOTH_RED_TIME_2);
				state = STATES.S4;
				return EXECUTE_TRANSITION;
			}
		} else if(state==STATES.S4) {
			if (event.equals(TIMER_4)) {
				cars.showRedYellow();
				t5.start(scheduler, CAR_RED_YELLOW_TIME);
				state = STATES.S5;
				return EXECUTE_TRANSITION;
			} else if (event.equals(PEDESTRIAN_BUTTON_PRESSED)) {
				pedestrianButtonIsPressed = true;
				state = STATES.S4;
				return EXECUTE_TRANSITION;
			}
		} else if(state==STATES.S5) {
			if (event.equals(TIMER_5)) {
				cars.showGreen();
				t6.start(scheduler, CAR_GREEN_TIME);
				state = STATES.S6;
				return EXECUTE_TRANSITION;
			} else if (event.equals(PEDESTRIAN_BUTTON_PRESSED)) {
				pedestrianButtonIsPressed = true;
				state = STATES.S5;
				return EXECUTE_TRANSITION;
			}
		} else if(state==STATES.S6) {
			if (event.equals(TIMER_6)) {
				state = STATES.S0;
				return EXECUTE_TRANSITION;
			} else if (event.equals(PEDESTRIAN_BUTTON_PRESSED)) {
				pedestrianButtonIsPressed = true;
				state = STATES.S6;
				return EXECUTE_TRANSITION;
			}
		}
		return DISCARD_EVENT;
	}



	public static void main(String[] args) {
		TrafficLightControllerMachine stm = new TrafficLightControllerMachine();
		Scheduler s = new Scheduler(stm);

		final GpioController gpio = GpioFactory.getInstance();
		final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_11, PinPullResistance.PULL_DOWN);
		PedButtonListener buttonListener = new PedButtonListener(s);
		myButton.addListener(buttonListener);

		s.start();

		int portNumber = 4325;
		String hostname = "192.168.0.186";

		try {
			Socket clientSocket = new Socket(hostname, portNumber);
			// hostname: A string containing the computer name or IP address of the server
			// portNumber: An integer containing a port number 4 above 1024 supported by the server
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out.write("toServer"); // Write string toServer to the server
			out.flush();

			System.out.println("(probably) Successfully connected");

			String fromServer;
			while ((fromServer = in.readLine()) != null) {
				System.out.println("Received from server: " + fromServer);
				if (fromServer.equals(EXTERNAL_SYNC)) {
					s.addToQueueLast(EXTERNAL_SYNC);
				}
				else {
					System.out.println("Received different signal than expected.");
				}
			} // Continuously read the input from the connection, 12 write a received string to variable fromServer,
			// and carry out doSomething(fromServer) afterwards.
		} catch (IOException e) {
			System.out.println("Exception caught when attempting to connect to port "
					+ portNumber + ", or other IO exception:");
			System.out.println(e.getMessage());
		}
	}


}
