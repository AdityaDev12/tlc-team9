package Harness;
import Communication.InstructionMessage;
import Communication.SimulatorEvent;
import Communication.TLCCommand;
import Simulator.*;

import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;

/**
 * Temporary test, creates mux
 */

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Traffic Light Controller...");
        Mux mux = null;
        try {
            mux = new Mux(); // connects to simulator
            final Mux connectedMux = mux; // final reference for event-receiver

            // creates TLC interface objects
            TrafficSensor trafficSensor = new TrafficSensor(connectedMux, Bearing.West);//temp bearing for testing
            TrafficLights trafficLights = new TrafficLights(connectedMux);
            Antenna antenna = new Antenna(connectedMux);
            Pedestrian pedestrian = new Pedestrian(connectedMux);
            EMSVehicle emsVehicle = new EMSVehicle(connectedMux);
            Timer timer = new Timer();
            Clock clock = new Clock();

            // creates operating modes
            DayMode dayMode = new DayMode(
                    trafficLights, timer, emsVehicle
            );
            NightMode nightMode = new NightMode(
                    trafficSensor, trafficLights, timer, emsVehicle
            );
            EMSMode emsMode = new EMSMode(
                    antenna, trafficLights, timer
            );
            PedestrianMode pedestrianMode = new PedestrianMode(
                    pedestrian, trafficLights, timer
            );

            // creates mode control
            ModeControl modeControl = new ModeControl (
                    dayMode, nightMode, emsMode, pedestrianMode, clock
            );

            System.out.println("Traffic Light Controller initialized.");

            /**
             * Need:
             * [IN REVISION] receive events from Sim through Mux,
             * [ONGOING] send events to correct interface object
             * [ONGOING] notify ModeControl of ped/emsRequest
             * enable ModeControl selection of mode
             * execute selected mode
             */

            // listens for events from Simulator
            Thread eventReceiver = new Thread(() -> {
                try {
                    while (true) {
                        SimulatorEvent event = connectedMux.receiveEvent();
                        if (event == null) {
                            System.out.println("Simulator connection closed.");
                            break;
                        }
                        handleSimulatorEvent(
                                event, trafficSensor, pedestrian, emsVehicle, modeControl
                        );
                    }
                } catch (IOException e) {
                    System.err.println("ERROR: HarnessMain lost connection to Simulator");
                    e.printStackTrace();;
                }
            }, "SimulatorEventReceiver");
        eventReceiver.setDaemon(true);
        eventReceiver.start();
        } catch (IOException e) {
            System.err.println("ERROR: HarnessMain unable to connect TLC to Simulator");
            e.printStackTrace();

        } finally {
            if (mux != null) {
                try {
                    mux.close();
                } catch (IOException e) {
                    System.err.println("ERROR: Unable to close Mux connection.");
                }
            }
        }
    }

    public static void handleSimulatorEvent(
            SimulatorEvent event,
            TrafficSensor trafficSensor,
            Pedestrian pedestrian,
            EMSVehicle emsVehicle,
            ModeControl modeControl) {
        switch (event.getCommand()) {
            case VEHICLE_DETECTED:
                System.out.println("HMain: Vehicle detected: " + event.getTarget());
                /**
                 * temporary; for one trafficsensor object configured for bearing.West
                 * need to map lane IDs to individual sensors for updates
                 */
                trafficSensor.vehicleDetected();
                break;

            case PEDESTRIAN_BUTTON_PRESSED:
                System.out.println("HMain: Pedestrian button pressed: " + event.getTarget());
                pedestrian.pedRequest();
                modeControl.setPedRequest(true);
                break;

            case EMS_PRIORITY_REQUEST:
                Bearing requestBearing = event.getBearing();
                System.out.println("HMain: EMS priority requested: " + event.getTarget());
                emsVehicle.incomingEMSVehicle(requestBearing);
                modeControl.setEmsRequest(true);
                break;

            case EMS_PRIORITY_CANCEL:
                Bearing cancelBearing = event.getBearing();
                System.out.println("HMain: EMS priority cleared: " + event.getTarget());
                emsVehicle.incomingEMSVehicle(cancelBearing);
                modeControl.setEmsRequest(false);
                break;

            case UPDATE_PEDESTRIAN_SIGNAL:
                System.out.println("HMain: Pedestrian signal update: " + event.getTarget() + " = " + event.getValue());
                break;

            case RESUME_NORMAL:
                System.out.println("HMain: Simulator requested normal operation.");
                break;

            case SET_LIGHT_STATE:
                /**
                 * SET_LIGHT_STATE is TLC -> Sim instruction
                 * now sent back as an event
                 */
                System.out.println("HMain: Received unexpected SET_LIGHT_STATE event");
                break;
        }
    }
}