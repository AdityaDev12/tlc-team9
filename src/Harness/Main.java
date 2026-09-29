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

            // TLC interface objects
            TrafficSensor trafficSensor = new TrafficSensor(mux, Bearing.West);//temp bearing for testing
            TrafficLights trafficLights = new TrafficLights(mux);
            Antenna antenna = new Antenna(mux);
            Pedestrian pedestrian = new Pedestrian(mux);
            EMSVehicle emsVehicle = new EMSVehicle(mux);
            Timer timer = new Timer();
            Clock clock = new Clock();

            // operating modes
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

            // mode control
            ModeControl modeControl = new ModeControl (
                    dayMode, nightMode, emsMode, pedestrianMode, clock
            );

            System.out.println("Traffic Light Controller initialized.");

            // start TLC
            /**
             * Need:
             * receive events from Sim through Mux,
             * send events to correct interface object
             * notify ModeControl of ped/emsRequest
             * enable ModeControl selection of mode
             * execute selected mode
             */

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
}