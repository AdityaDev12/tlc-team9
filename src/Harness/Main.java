package Harness;
import Communication.SimulatorEvent;
import Simulator.*;

import java.io.IOException;

/**
 * Constructs the TLC,
 * connects to Mux,
 * receives Simulator events,
 * translates events into calls on TLC interface objects,
 * passes requests to ModeControl
 */

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Traffic Light Controller...");
        Mux mux = null;
        try {
            mux = new Mux(); // connects to simulator
            final Mux connectedMux = mux; // final reference for event-receiver

            // creates TLC interface objects
            TrafficSensor trafficSensorEW = new TrafficSensor(connectedMux, Bearing.East);//temp bearing for testing
            TrafficLights trafficLights = new TrafficLights(connectedMux);
            Antenna antenna = new Antenna(connectedMux);
            Pedestrian pedestrian = new Pedestrian(connectedMux);
            EMSVehicle emsVehicle = new EMSVehicle(connectedMux);
            TLCTimer timer = new TLCTimer();
            Clock clock = new Clock();

            // creates operating modes
            DayMode dayMode = new DayMode(
                    trafficLights, timer, emsVehicle
            );
            NightMode nightMode = new NightMode(
                    trafficSensorEW, trafficLights, timer, emsVehicle
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

            // starts TLC Mode Control thread
            Thread modeController = new Thread(modeControl::run, "TLC-ModeControl");
            modeController.start();

            // receives events from Simulator
            Thread eventReceiver = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        SimulatorEvent event = connectedMux.receiveEvent();
                        if (event == null) {
                            System.out.println("Simulator connection closed.");
                            break;
                        }
                        handleSimulatorEvent(
                                event, trafficSensorEW, pedestrian, emsVehicle, antenna, modeControl
                        );
                    }
                } catch (IOException e) {
                    System.err.println("ERROR: HMain TLC lost connection to Simulator");
                    e.printStackTrace();
                } finally {
                    modeController.interrupt();
                }
            }, "TLC-SimulatorEventReceiver");
        eventReceiver.start();
        eventReceiver.join(); // keep Main alive while TLC runs
        } catch (IOException e) {
            System.err.println("ERROR: HMain unable to connect TLC to Simulator");
            e.printStackTrace();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (mux != null) {
                try {
                    mux.close();
                } catch (IOException e) {
                    System.err.println("ERROR: HMain unable to close Mux connection.");
                }
            }
        }
    }

    public static void handleSimulatorEvent(
            SimulatorEvent event,
            TrafficSensor trafficSensor,
            Pedestrian pedestrian,
            EMSVehicle emsVehicle,
            Antenna antenna,
            ModeControl modeControl) {
        switch (event.getCommand()) {
            case VEHICLE_DETECTED:
                System.out.println("Main: Vehicle detected: " + event.getTarget());
                /**
                 * temporary; for one trafficsensor object configured for bearing.West
                 * need to map lane IDs to individual sensors for updates
                 */
                trafficSensor.vehicleDetected();
                break;

            case VEHICLE_CLEARED:
                System.out.println("Main: Vehicle cleared: " + event.getTarget());
                trafficSensor.vehicleGone();
                break;

            case PEDESTRIAN_BUTTON_PRESSED:
                System.out.println("Main: Pedestrian button pressed: " + event.getTarget());
                modeControl.setPedRequest(true);
                break;

            case EMS_PRIORITY_REQUEST:
                Bearing requestBearing = event.getBearing();
                System.out.println("Main: EMS priority requested: " + requestBearing);
                emsVehicle.incomingEMSVehicle(requestBearing);
                antenna.emsRequest(requestBearing);
                modeControl.setEmsRequest(true, requestBearing);
                break;

            case EMS_PRIORITY_CANCEL:
                Bearing cancelBearing = event.getBearing();
                System.out.println("Main: EMS priority cleared: " + cancelBearing);
                emsVehicle.leavingEMSVehicle(cancelBearing);
                antenna.emsCleared(cancelBearing);
                modeControl.setEmsRequest(false, cancelBearing);
                break;

            case UPDATE_PEDESTRIAN_SIGNAL:
                System.out.println("Main: Pedestrian signal update: " + event.getTarget() + " = " + event.getValue());
                break;

            case RESUME_NORMAL:
                System.out.println("Main: Simulator requested normal operation.");
                modeControl.setEmsRequest(false, null);
                break;

            case SET_LIGHT_STATE:
                System.out.println("Main: Received unexpected SET_LIGHT_STATE event");
                break;
        }
    }
}