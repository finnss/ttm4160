package exercises.trafficlight;

import com.pi4j.io.gpio.*;
import java.util.*;
import java.lang.*;

public class TrafficLightPI implements TrafficLight {

    private static String CAR = "Car", PED = "Ped";

    private static final String[] TrafficLightsTypes = {CAR, PED};

    private GpioPinDigitalOutput LedRed = null;
    private GpioPinDigitalOutput LedYellow = null;
    private GpioPinDigitalOutput LedGreen = null;
    private GpioPinDigitalOutput LedRedPed = null;
    private GpioPinDigitalOutput Buzzer = null;

    public TrafficLightPI(String type, Boolean on){
        // create gpio controller instance
        final GpioController gpio = GpioFactory.getInstance();
        
        //initialization cars
        if (type == TrafficLightsTypes[0]) {
            LedRed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, "LedCarRed", PinState.LOW);
            LedYellow = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "LedCarYellow", PinState.LOW);
            LedGreen = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_13, "LedCarGreen", PinState.LOW);

        //initialization pedestrians    
        } else {
            LedRedPed = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "LedPedRed", PinState.LOW);
            LedYellow = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_12, "LedPedYellow", PinState.LOW);
            LedGreen = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_14, "LedPedGreen", PinState.LOW);

            Buzzer = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_10, "Buzzer", PinState.LOW);
        }

        if (on) {
            LedGreen.toggle();
        } else {
            LedRed.toggle();
        }

    }

    public void showGreen() {
        LedRed.low();
        if(LedYellow!=null) {LedYellow.low();}
        LedGreen.high();
	}

	public void showRed() {
		LedRed.high();
        if(LedYellow!=null) {LedYellow.low();}
        LedGreen.low();
	}

	public void showRedYellow() {
		if(LedYellow==null) {
			throw new UnsupportedOperationException("Traffic light has no yellow light.");
		}else{
            LedRed.high();
            LedYellow.high();
            LedGreen.low();
        }
	}

	public void showYellow() {
		if(LedYellow==null) {
			throw new UnsupportedOperationException("Traffic light has no yellow light.");
		}else{
            LedRed.low();
            LedYellow.high();
            LedGreen.low();
        }
	}

	public void switchAllOff() {
        LedGreen.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        LedRed.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        LedYellow.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
	}


}