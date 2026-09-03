package Harness;

import Communication.InstructionMessage;
import Communication.TLCCommand;
import Simulator.LightCol;
import Simulator.LightShape;
import Simulator.Position;

public class Main {

    private static Mux mux;

    public static void main(String[] args) {
        System.out.println("Starting Harness...");
        try {
            mux = new Mux();
            mux.listenForEvents();

            System.out.println("Running continuous signal cycle. Ctrl+C to stop.\n");

            while (true) {
                runPhase("VERTICAL (N/S)", Position.North, Position.South, Position.East, Position.West);
                runPhase("HORIZONTAL (E/W)", Position.East, Position.West, Position.North, Position.South);
            }

        } catch (Exception e) {
            System.err.println("ERROR: Main");
            e.printStackTrace();
        }
    }

    private static void runPhase(String label, Position goA, Position goB,
                                 Position stopA, Position stopB) throws InterruptedException {
        System.out.println("=== " + label + " GREEN ===");
        setDirection(goA, LightCol.Green);
        setDirection(goB, LightCol.Green);
        setDirection(stopA, LightCol.Red);
        setDirection(stopB, LightCol.Red);
        Thread.sleep(9000);

        System.out.println("=== " + label + " YELLOW ===");
        setDirection(goA, LightCol.Yellow);
        setDirection(goB, LightCol.Yellow);
        Thread.sleep(3000);

        System.out.println("=== " + label + " RED (all-clear) ===");
        setDirection(goA, LightCol.Red);
        setDirection(goB, LightCol.Red);
        Thread.sleep(1500);
    }

    private static void setDirection(Position position, LightCol color) {
        for (int laneId = 0; laneId < 3; laneId++) {
            mux.sendInstruction(new InstructionMessage(TLCCommand.SET_LIGHT_STATE, laneId, color, LightShape.Circle, position));
        }
    }
}