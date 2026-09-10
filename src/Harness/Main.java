package Harness;
import Communication.InstructionMessage;
import Communication.SimulatorEvent;
import Communication.TLCCommand;
import Simulator.*;

import java.io.IOException;
import java.util.Scanner;

/**
 * Temporary test, creates mux
 */

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting Harness...");
        try {
            Mux mux = new Mux(); // connects to simulator

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
            System.err.println("ERROR: HarnessMain");
            e.printStackTrace();
        }
    }
}