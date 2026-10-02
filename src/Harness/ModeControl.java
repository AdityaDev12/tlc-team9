package Harness;

import Simulator.Bearing;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Queue;

public class ModeControl {
    private final DayMode dayMode;
    private final NightMode nightMode;
    private final EMSMode emsMode;
    private final PedestrianMode pedestrianMode;
    private final Clock clock;

    // conditions
    private boolean pedRequest;

    private final Queue<Bearing> pendingEMS = new LinkedList<>();
    private boolean nextNS = true;

    public ModeControl(
            DayMode dayMode,
            NightMode nightMode,
            EMSMode emsMode,
            PedestrianMode pedestrianMode,
            Clock clock) {
        this.dayMode = dayMode;
        this.nightMode = nightMode;
        this.emsMode = emsMode;
        this.pedestrianMode = pedestrianMode;
        this.clock = clock;

        pedRequest = false;

    }
    public synchronized void setPedRequest(boolean request) {
        pedRequest = request;
    }
    public synchronized void setEmsRequest(boolean request, Bearing bearing) {
        System.out.println("ModeControl: setEmsRequest(" + request + ", " + bearing + ")");
        if (request && bearing != null) {
            pendingEMS.add(bearing);
            System.out.println("ModeControl: EMS request added: " + bearing);
            System.out.println("Modecontrol: Pending EMS: " + pendingEMS);
        } else if (!request && bearing != null) {
            pendingEMS.remove(bearing);
            System.out.println("ModeControl: EMS request removed: " + bearing);
        }
        System.out.println("ModeControl: Pending EMS: " + pendingEMS);
        // only disable EMS preemption when no ems requests left
        boolean anyEMS = !pendingEMS.isEmpty();
        dayMode.setEmsRequested(anyEMS);
        nightMode.setEmsRequested(anyEMS);
    }
    public synchronized boolean hasPedRequest() {
        return pedRequest;
    }

    public synchronized boolean hasEmsRequest() {
        return !pendingEMS.isEmpty();
    }

    public synchronized Bearing getNextEmsBearing() {

        return pendingEMS.poll();


    }

    // Main TLC mode selection loop
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                // EMS always has priority
                if (hasEmsRequest()) {
                     Bearing bearing = getNextEmsBearing();
                    if (bearing != null) {
                        System.out.println("ModeControl: Starting EMS for: " + bearing);
                        emsMode.request(bearing);
                        emsMode.run();
                        setEmsRequest(false, bearing); // ems finished serving request
                    }
                    continue;
                }
                // pedestrian requests have priority over day/night modes
                if (hasPedRequest()) {
                    dayMode.setPedestrianRed();
                    nightMode.setPedestrianRed();
                    boolean completed = pedestrianMode.run();
                    if (completed) {
                        setPedRequest(false);
                    }
                    continue;
                }
                // select day or night based on clock
                if (clock.isDayTime()) {
                    if (nextNS) {
                        System.out.println("ModeControl: Staring NS Day cycle.");
                        boolean completed = dayMode.runNS();
                        if (completed) {
                            nextNS = false;
                        }
                        System.out.println("ModeControl: NS cycle finished.");
                    }else {
                        System.out.println("ModeControl: Staring EW Day cycle.");
                        boolean completed = dayMode.runEW();
                        if(completed) {
                            nextNS = true;
                        }
                        System.out.println("ModeControl: EW cycle finished.");
                    }
                } else {
                    nightMode.run();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
