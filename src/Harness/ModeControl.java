package Harness;

import Simulator.Bearing;

public class ModeControl {
    private final DayMode dayMode;
    private final NightMode nightMode;
    private final EMSMode emsMode;
    private final PedestrianMode pedestrianMode;
    private final Clock clock;

    // conditions
    private boolean pedRequest;
    private boolean emsRequest;
    private volatile Bearing emsBearing;
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
        emsRequest = false;
        emsBearing = null;

    }
    public synchronized void setPedRequest(boolean request) {
        pedRequest = request;
    }
    public synchronized void setEmsRequest(boolean request, Bearing bearing) {
        emsRequest = request;
        emsBearing = bearing;
        if (request && bearing != null) {
            emsMode.request(bearing);
        }
    }
    public synchronized boolean hasPedRequest() {
        return pedRequest;
    }

    public synchronized boolean hasEmsRequest() {
        return emsRequest;
    }
    public synchronized Bearing getEmsBearing() {
        return emsBearing;
    }

    // Main TLC mode selection loop
    public void run() {
        while(!Thread.currentThread().isInterrupted()) {
            try {
                // EMS always has priority
                if (hasEmsRequest() && getEmsBearing() != null) {
                    emsMode.run();
                    continue;
                }
                // pedestrian requests have priority over day/night modes
                if (hasPedRequest()) {
                    pedestrianMode.handlePedRequest();
                    setPedRequest(false);
                    continue;
                }
                // select day or night based on clock
                if (clock.isDayTime()) {
                    if (nextNS) {
                        System.out.println("ModeControl: Staring NS Day cycle.");
                        dayMode.runNS();
                        nextNS = false;
                        System.out.println("ModeControl: NS cycle finished.");
                    }else {
                        System.out.println("ModeControl: Staring EW Day cycle.");
                        dayMode.runEW();
                        nextNS = true;
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
