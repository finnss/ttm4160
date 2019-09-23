package exercises.trafficlight;

import com.pi4j.io.gpio.event.*;
import runtime.Scheduler;

import static exercises.trafficlight.TrafficLightControllerMachine.PEDESTRIAN_BUTTON_PRESSED;

public class PedButtonListener implements GpioPinListenerDigital {

    private Scheduler scheduler;

    public PedButtonListener(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @Override
    public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
        // display pin state on console
        System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = "
                + event.getState());

        scheduler.addToQueueLast(PEDESTRIAN_BUTTON_PRESSED);
    }
}
