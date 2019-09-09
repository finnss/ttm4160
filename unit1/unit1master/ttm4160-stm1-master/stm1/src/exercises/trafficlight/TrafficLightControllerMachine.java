package exercises.trafficlight;

import runtime.EventWindow;
import runtime.IStateMachine;
import runtime.Scheduler;
import runtime.Timer;

public class TrafficLightControllerMachine implements IStateMachine {
	
	private static final String PEDESTRIAN_BUTTON_PRESSED = "Pedestrian Button",
			TIMER_1 = "t1", TIMER_2 = "t2", TIMER_3 = "t3", TIMER_4 = "t4", TIMER_5 = "t5", TIMER_6 = "t6";
	
	public static final String[] EVENTS = {PEDESTRIAN_BUTTON_PRESSED};
	
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
	
	private TrafficLight cars = new TrafficLight("Cars", true);
	private TrafficLight pedestrians = new TrafficLight("Pedestrians", false);

	private boolean pedestrianButtonIsPressed = false;
	
	public TrafficLightControllerMachine() {
		// initial transition
		cars.setVisible(true);
		pedestrians.setVisible(true);
		cars.showGreen();
		pedestrians.showRed();		
	}

	public int fire(String event, Scheduler scheduler) {
		if(state==STATES.S0) {
			if(event.equals(PEDESTRIAN_BUTTON_PRESSED)) {
				cars.showYellow();
				t1.start(scheduler, CAR_YELLOW_TIME);
				state = STATES.S1;
				return EXECUTE_TRANSITION;
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
			} else if (event.equals(PEDESTRIAN_BUTTON_PRESSED)) {
				pedestrianButtonIsPressed = true;
				state = STATES.S6;
				return EXECUTE_TRANSITION;
			}
		}
		return DISCARD_EVENT;
	}
	
	public static void main(String[] args) {
		IStateMachine stm = new TrafficLightControllerMachine();
		Scheduler s = new Scheduler(stm);
		
		EventWindow w = new EventWindow(EVENTS, s);
		w.show();

		s.start();
	}

}
