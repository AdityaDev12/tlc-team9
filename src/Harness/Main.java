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
        try {
            Mux mux = new Mux(); // connects to simulator

            // interface TLC to GUI
            TrafficSensor trafficSensor = new TrafficSensor(mux);
            TrafficLights trafficLights = new TrafficLights(mux);
            Antenna antenna = new Antenna(mux);
            Pedestrian pedestrian = new Pedestrian(mux);
            Timer timer = new Timer();
            Clock clock = new Clock();

            // operating modes
            DayMode dayMode = new DayMode(
                    trafficSensor, trafficLights, timer
            );
            NightMode nightMode = new NightMode(
                    trafficSensor, trafficLights, timer
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

            // start TLC
            /**
             * Need: Receive events from Sim,
             * update TLC interface object,
             * allow ModeControl selection,
             * execute selection
             */


            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println();
                System.out.println("=================================");
                System.out.println(" Traffic Light Controller Harness");
                System.out.println("=================================");
                System.out.println("1. Traffic Light Command");
                System.out.println("2. Pedestrian Signal Command");
                System.out.println("3. EMS Priority Command");
                System.out.println("Type 'quit' to exit.");
                System.out.print("> ");

                String choice = scanner.nextLine().trim();

                if (choice.equalsIgnoreCase("quit")) {
                    break;
                }

                switch (choice) {
                    case "1" : // Traffic light command
                        System.out.println();
                        System.out.println("Format:");
                        System.out.println("SET_LIGHT_STATE:lightID:color:shape:position");
                        System.out.println("Example:");
                        System.out.println("SET_LIGHT_STATE:2:Green:Square:North");
                        System.out.print("> ");
                        String lightCommand = scanner.nextLine().trim();
                        try {
                            InstructionMessage message = InstructionMessage.parse(lightCommand);
                            mux.sendInstruction(message);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid traffic light command.");
                            System.out.println("HarnessMain: " + e.getMessage());
                        }
                        break;

                    case "2": // Pedestrian command - all signals activate, all traffic lights turn red
                        System.out.println();
                        System.out.println("Pedestrian commands not implemented yet");
                        break;

                    case "3": // EMS command - antenna blinks, all traffic lights turn red
                        System.out.println();
                        System.out.println("EMS commands not implemented yet");
                        break;

                    default:
                        System.out.println("Invalid selection.");
                }
            }
            scanner.close();
            mux.close();

        } catch (IOException e) {
            System.err.println("ERROR: HarnessMain unable to connect TLC to Simulator");
            e.printStackTrace();
        }
    }
}