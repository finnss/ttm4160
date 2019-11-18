package PiSTM;

import com.pi4j.io.gpio.*;
import runtime.IStateMachine;
import runtime.Scheduler;
import runtime.Timer;

import ComputerPiSharedCode.MQTTclient;
import sun.net.ConnectionResetException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import adc.Mcp3008Interface;



public class PiSTM implements IStateMachine {
	
	private MQTTclient mqttClient;
    LinkedList<String> buffer = new LinkedList();

	public static final String RECEVE_SENSOR_DATA = "Receive sensor data", RECEIVE_ACK = "Receive Ack",
	TIMER_1 = "t1", TIMER_2  = "t2";

	public static final String[] EVENTS = {RECEVE_SENSOR_DATA, RECEIVE_ACK};

	private enum STATES {S0, S1, S2}

	private Timer t1 = new Timer("t1");
	private Timer t1 = new Timer("t2");

	private final int
		SENSOR_UPDATE_FREQUENCY = 1000,
		TIMEOUT = 200;

	protected STATES state = STATES.S0;

	static int numberOfToilets = 1;
	private boolean occupied[];
	private int numberOfOccupiedToilets = 0;
	private int numberOfOccupiedToiletsTemp = 0;
	
	
	private static int LOWER_TRESHOLD = 450, 
						HIGHER_TRESHOLD = 500;

	public PiSTM() {
		// initial transition
		t1.start(scheduler, SENSOR_UPDATE_FREQUENCY);  //scheduler?
	}

	public int fire(String event, Scheduler scheduler) {
		if(state==STATES.S0) {
			if(event.equals(TIMER_1)) {
				
				int sensorValues[];
				for(int i = 0; i < numberOfToilets; i++){
					sensorValues[i] = mcp3008ReadChannel(i);
					if(sensorValues[i] < LOWER_TRESHOLD && occupied[i] == true){
						occupied[i] = false;
					}
					else if(sensorValues[i] > HIGHER_TRESHOLD && occupied == false){
						occupied[i] = true;
					}
					if (occupied[i] == true){
						numberOfOccupiedToiletsTemp = numberOfOccupiedToilets + 1;
					}
				}
				if(numberOfOccupiedToilets != numberOfOccupiedToiletsTemp){
					//TODO
						//SEND Number OF TOILET AVALIBLE
						console.print(String.format(" | %04d", sensorValues[0])); // print 4 digits with leading zeros

						t2.start(scheduler, TIMEOUT);
						state = STATES.S1;
						return EXECUTE_TRANSITION;
				}
				
				//state = STATES.S0;
				//return EXECUTE_TRANSITION;
			}
		} else if(state==STATES.S1) {
			if (event.equals(TIMER_2)) {
				state = STATES.S0;
				return EXECUTE_TRANSITION;
			}
			if(event.equals(RECEIVE_ACK)){
				numberOfOccupiedToilets = numberOfOccupiedToiletsTemp;
				state = STATES.S0;
				return EXECUTE_TRANSITION;
			}
		} 
		return DISCARD_EVENT;
	}



	public static void main(String[] args) {
		PiSTM stm = new PiSTM();
		Scheduler s = new Scheduler(stm);
		
		console.promptForExit();

		//Initialize ADC
		mcp3008Init();
		
		for (int i = 0; i < numberOfToilets; i++){
		occupied[i] = false;
	}

		s.start();


		// Set up MQTT client
        String myAddress = "pi";

        MQTTclient mqttClient = new MQTTclient(broker, myAddress, conf, s);
        mqttClient.subscribe(pi_topic);

        stateMachine.setMqttClient(mqttClient);
	}


}
