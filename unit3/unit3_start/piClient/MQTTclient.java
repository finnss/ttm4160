package piClient;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import runtime.Scheduler;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static piClient.LEDMatrixStateMachine.MESSAGE_RECEIVED;

public class MQTTclient implements MqttCallback {
	
	private Scheduler scheduler;
	private MqttClient client;
	private BlockingDeque<MqttMessage> payloadQueue = new LinkedBlockingDeque<MqttMessage>();
	
	public MQTTclient(String broker, String myAddress, boolean conf, Scheduler s) {
		scheduler = s;
		MemoryPersistence pers = new MemoryPersistence();
		try {
			client = new MqttClient(broker,myAddress,pers);
			MqttConnectOptions opts = new MqttConnectOptions();
			opts.setCleanSession(true);
			client.connect(opts);
			client.setCallback(this);
			scheduler.addToQueueLast("MQTTReady");
		}
		catch (MqttException e) {
			System.err.println("MQTT Exception: " + e);
			scheduler.addToQueueLast("MQTTError");
		}		
	}

	
	public void connectionLost(Throwable e) {
		System.err.println("Connection lost: " + e);
		scheduler.addToQueueLast("ConnectionLost");
		try {
			client.disconnect();
		} catch (MqttException e1) {
			System.err.println("MQTT Exception: " + e);
			scheduler.addToQueueLast("MQTTError");
		}
	}
	
	public void deliveryComplete(IMqttDeliveryToken token) {
		System.out.println("Delivery complete.");
	}
	
	public void messageArrived(String topic, MqttMessage mess) {
		System.out.println("messageArrived");
		String eventId = "" + mess.getId();
		scheduler.addToQueueLast(MESSAGE_RECEIVED);
		scheduler.addDisplayMessage(eventId, new String(mess.getPayload()));
		payloadQueue.addLast(mess);
	}

	public void sendMessage(String topic, MqttMessage mess) {
		try {
			System.out.println("Sending message");
			client.publish(topic, mess);
		} catch (MqttException e) {
			System.err.println("MQTT Exception: " + e);
			scheduler.addToQueueLast("MQTTError");
		}
	}

	public void subscribe(String topicFilter) {
		try {
			client.subscribe(topicFilter);
		} catch (MqttException e) {
			System.err.println("MQTT Exception: " + e);
			scheduler.addToQueueLast("MQTTError");
		}
	}

	public MqttMessage takePayload() {
		try {
			return payloadQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
