package server;

import runtime.Scheduler;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class MQTTclient implements MqttCallback {

	public final static String broker = "tcp://broker.hivemq.com:1883";
	public final static boolean conf = true;
	public final static String pi_topic = "ttm4160_Led_Pi";
	public final static String pc_topic = "ttm4160_Led_PC";
	
	private Scheduler scheduler;
	private MqttClient client;
	private BlockingDeque<MqttMessage> payloadQueue = new LinkedBlockingDeque<>();

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
		System.out.println("Delivery complete.\n");
	}
	
	public void messageArrived(String topic, MqttMessage mess) {
		System.out.println("messageArrived");
		String payload = new String(mess.getPayload());

		/*
		if (topic.equals(pc_topic)) {
			System.out.println("Message arrived to PC; dont handle it!");
			return;
		}
		*/

		scheduler.addToQueueLast("MESSAGE_RECEIVED", payload);
		mess.setPayload(payload.getBytes());
	}

	public void sendMessage(String topic, MqttMessage mess) {
		System.out.println("Sending to topic: " + topic);
		try {
			client.publish(topic, mess);
		} catch (MqttException e) {
			System.err.println("MQTT Exception: " + e);
			scheduler.addToQueueLast("MQTTError");
		}
	}

	public void subscribe(String topicFilter) {
		try {
			System.out.println("Subscribing to " + topicFilter + "...");
			client.subscribe(topicFilter);
		} catch (MqttException e) {
			System.err.println("MQTT Exception: " + e);
			scheduler.addToQueueLast("MQTTError");
		}
	}

}
