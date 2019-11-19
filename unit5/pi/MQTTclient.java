package ComputerPiSharedCode;

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

	public final static String broker = "tcp://broker.hivemq.com:1883";
	public final static boolean conf = true;
	public final static String pi_topic = "ttm4160_To_Pi";
	public final static String pc_topic = "ttm4160_To_PC";
	
	private Scheduler scheduler;
	private MqttClient client;
	private BlockingDeque<MqttMessage> payloadQueue = new LinkedBlockingDeque<>();

	private FreePoolHandler freePoolHandler = new FreePoolHandler(2);
	
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
		String[] splitPayload = payload.split(",");
		String freepool = splitPayload[splitPayload.length - 1];

		/*
		for (String payloadPart : splitPayload) {
			payload += payloadPart + ",";
		}
		*/
		payload = payload.substring(0, payload.length() - (freepool.length() + 1));

		System.out.println("Done parsong payload. Frepool: " + freepool);
		System.out.println("Payload: " + payload + "\n");

		freePoolHandler.handleFreepoolArrived(freepool);

		if (topic.equals(pc_topic)) {
			System.out.println("Message arrived to PC; dont handle it!");
			return;
		}

		String eventId = "" + mess.getId();
		scheduler.addToQueueLast(MESSAGE_RECEIVED);
		scheduler.addDisplayMessage(eventId, payload);
		mess.setPayload(payload.getBytes());
		addToQueueLast(mess);
	}

	public synchronized void addToQueueLast(MqttMessage message) {
		payloadQueue.addLast(message);
	}

	public void sendMessage(String topic, MqttMessage mess) {
		System.out.println("Sending to topic: " + topic);
		try {
			System.out.println("Sending message if freepool is available.");
			String freepool = freePoolHandler.getFreepoolOrQueue(mess);
			if (freepool != null) {
				System.out.println("Acquired freepool! " + freepool);
				byte[] payloadWithFreepool = (new String(mess.getPayload()) + "," + freepool).getBytes();
				System.out.println("Payload with freepool:");
				System.out.println(new String(payloadWithFreepool));
				mess.setPayload(payloadWithFreepool);
				client.publish(topic, mess);
			} else {
				System.out.println("No freepool available; waiting.");
			}
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

	public MqttMessage takePayload() {
		try {
			return payloadQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

}
