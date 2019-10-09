package piClient;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import runtime.IStateMachine;
import runtime.Scheduler;
import runtime.Timer;
import sensehat.LEDMatrixTicker;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static piClient.MQTTclient.broker;
import static piClient.MQTTclient.conf;
import static piClient.MQTTclient.topic;

public class LEDMatrixStateMachine implements IStateMachine {

    public static final String TIMER_1 = "t1", MESSAGE_RECEIVED = "MessageReceived";

    private enum STATES {LISTEN_STATE, WRITING_STATE, READING_BUFFER}

    private Timer t1 = new Timer("t1");

    protected STATES state = STATES.LISTEN_STATE;

    private final int WRITE_INTERVAL = 200;

    private LEDMatrixTicker ticker;
    private MQTTclient mqttClient;
    LinkedList<String> buffer = new LinkedList();

    public int readBuffer(Scheduler scheduler) {
        if (buffer.size() == 1) {
            String toProcess = buffer.getFirst();

            ticker.StartWriting(toProcess);
            ticker.WritingStep();
            t1.start(scheduler, WRITE_INTERVAL);
            state = STATES.WRITING_STATE;
        } else {
            state = STATES.LISTEN_STATE;
        }
        return EXECUTE_TRANSITION;
    }

    @Override
    public int fire(String event, Scheduler scheduler) {
        if(state==STATES.LISTEN_STATE) {
            if (event.equals(MESSAGE_RECEIVED)) {
                MqttMessage mess = mqttClient.takePayload();
                String payload = new String(mess.getPayload());

                System.out.println("Got payload!");
                System.out.println(payload);

                buffer.add(0, payload);

                return readBuffer(scheduler);

            } else {
                System.out.println("Unexpected event received");
            }
        } else if(state==STATES.WRITING_STATE) {
            if (event.equals("LEDMatrixTickerWait")) {
                t1.start(scheduler, WRITE_INTERVAL);
                state = STATES.WRITING_STATE;
                return EXECUTE_TRANSITION;
            } else if (event.equals(TIMER_1)) {
                ticker.WritingStep();
                state = STATES.WRITING_STATE;
                return EXECUTE_TRANSITION;
            } else if (event.equals(MESSAGE_RECEIVED)) {
                MqttMessage mess = mqttClient.takePayload();
                String payload = new String(mess.getPayload());
                buffer.add(0, payload);
                state = STATES.WRITING_STATE;
                return EXECUTE_TRANSITION;
            } else if (event.equals("LEDMatrixTickerFinished")) {
                return readBuffer(scheduler);
            }
            else {
                System.out.println("Unexpected event received");
            }
        }
        return DISCARD_EVENT;
    }

    public void setTicker(LEDMatrixTicker ticker) {
        this.ticker = ticker;
    }

    public void setMqttClient(MQTTclient mqttClient) {
        this.mqttClient = mqttClient;
    }

    public static void main (String[] args) {
        // Initialize state machine and scheduler
        LEDMatrixStateMachine stateMachine = new LEDMatrixStateMachine();
        Scheduler s = new Scheduler(stateMachine);
        s.start();

        // Set up LED Matrix Ticket
        LEDMatrixTicker ticker = new LEDMatrixTicker(s);
        stateMachine.setTicker(ticker);

        // Set up MQTT client
        String myAddress = "pi";

        MQTTclient mqttClient = new MQTTclient(broker, myAddress, conf, s);
        mqttClient.subscribe(topic);

        stateMachine.setMqttClient(mqttClient);
    }
}
