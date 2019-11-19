package PiSTM;

import com.pi4j.io.gpio.*;
import runtime.IStateMachine;
import runtime.Scheduler;
import runtime.Timer;

import com.pi4j.util.Console;

//import ComputerPiSharedCode.MQTTclient;
//import org.eclipse.paho.cient.mqttv3.MqttMessage; //NB
//import static ComputerPiSharedCode.MQTTclient.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import adc.Mcp3008Interface;



public class PiSTM implements IStateMachine {
	
	//private MQTTclient mqttClient;
   	// LinkedList<String> buffer = new LinkedList();

	public static final String RECEVE_SENSOR_DATA = "Receive sensor data", MESSAGE_RECEIVED = "Receive Ack",
	TIMER_1 = "t1", TIMER_2  = "t2", INIT = "init";

	public static final String[] EVENTS = {RECEVE_SENSOR_DATA, RECEIVE_ACK};

	private enum STATES {INIT, S0, S1, S2}

	private Timer t1 = new Timer("t1");
	private Timer t2 = new Timer("t2");

	private final int
		SENSOR_UPDATE_FREQUENCY = 1000,
		TIMEOUT = 500;

	protected STATES state = STATES.INIT;
	
	protected static final Console console = new Console();

	static int numberOfToilets = 8;
	private boolean occupied[] = new boolean[numberOfToilets];
	private int numberOfOccupiedToilets = 0;
	private int numberOfOccupiedToiletsTemp = 0;
	
	
	private int sensorValues[] = new int[numberOfToilets];
	private static int LOWER_TRESHOLD = 450, 
						HIGHER_TRESHOLD = 500;
	public PiSTM() {
	//Something
	}


	public int fire(String event, Scheduler scheduler) {
		if(state==STATES.INIT){
			if(event.equals(INIT)){
				for (int i = 0; i < numberOfToilets; i++){
					occupied[i] = false;
					sensorValues[i] = 0;
				}
				t1.start(scheduler, SENSOR_UPDATE_FREQUENCY);
				state=STATES.S0;
				return EXECUTE_TRANSITION;
			}
		}
		
		if(state==STATES.S0){
			if(event.equals(TIMER_1)) {
				
			console.print(String.format("Timer 1 trigger\n ")); 
				numberOfOccupiedToiletsTemp = 0;
				for(int i = 0; i < numberOfToilets; i++){
					try{
					sensorValues[i] =adc.Mcp3008Interface.mcp3008ReadChannel(i);
					}
					catch(Exception IOException){
						console.print(String.format("IOException\n ")); 
					}
					if(sensorValues[i] < LOWER_TRESHOLD && occupied[i] == true){
						occupied[i] = false;
						console.print(String.format("Set toilet %d to avalible\n", i)); 
					}
					else if(sensorValues[i] > HIGHER_TRESHOLD && occupied[i] == false){
						occupied[i] = true;
						console.print(String.format("Set toilet %d to occupied\n ", i)); 
					}
					if (occupied[i] == true){
						console.print(String.format("Toilet %d occupied\n ", i)); 
						numberOfOccupiedToiletsTemp = numberOfOccupiedToiletsTemp + 1;
					}
				}
				if(numberOfOccupiedToilets != numberOfOccupiedToiletsTemp){
					//TODO
						//SEND Number OF TOILET AVALIBLE HERE
						
						//MqttMessage msg = new MqttMessage(numberOfOccupiedToiletsTemp.toByteArray());
						//mqttClient.sendMessage(pc_topic,msg);				


						console.print(String.format("sending data, o:%d, O_t: %d\n", numberOfOccupiedToilets, numberOfOccupiedToiletsTemp));

						t2.start(scheduler, TIMEOUT);
						state = STATES.S1;
						return EXECUTE_TRANSITION;
				}
				t1.start(scheduler, SENSOR_UPDATE_FREQUENCY);
				state = STATES.S0;
				return EXECUTE_TRANSITION;
			}
		} else if(state==STATES.S1) {
			if (event.equals(TIMER_2)) {
				console.print(String.format("Timeout from server\n"));
				t1.start(scheduler, SENSOR_UPDATE_FREQUENCY);
				state = STATES.S0;
				return EXECUTE_TRANSITION;
			}
			if(event.equals(MESSAGE_RECEIVED)){
		//		MqttMessage mess = mqttClient.takePayload();
		//		String payload = new String(mess.getPayload());
		//		console.print(String.format("Received message from server %s\n", payload));
				
				numberOfOccupiedToilets = numberOfOccupiedToiletsTemp;
				t1.start(scheduler, SENSOR_UPDATE_FREQUENCY);
				state = STATES.S0;
				return EXECUTE_TRANSITION;
			}
		} 
		return DISCARD_EVENT;
	}

	public void setMqttClient(MQTTclient mqttClient) {
		this.mqttClient = mqttClient;
	}

	public static void main(String[] args) {
		PiSTM stm = new PiSTM();
		Scheduler s = new Scheduler(stm);
		Mcp3008Interface adc = new Mcp3008Interface();
		//Initialize ADC
		
		console.promptForExit();
		try{
			adc.mcp3008Init();
		}
		catch(Exception InterruptedException){
			console.print(String.format("ADC init exception\n ")); // print 4 digits with leading zeros
		}
		s.start();

		s.addToQueueLast(INIT);


		// Set up MQTT client
        String myAddress = "pi";

  //      MQTTclient mqttClient = new MQTTclient(broker, myAddress, conf, s);
  //      mqttClient.subscribe(pi_topic);

  //      stm.setMqttClient(mqttClient);
	}


}
