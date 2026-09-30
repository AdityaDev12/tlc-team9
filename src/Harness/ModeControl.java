package Harness;

import Simulator.Bearing;

public class ModeControl {
    private DayMode dayMode;
    private NightMode nightMode;
    private EMSMode emsMode;
    private PedestrianMode pedestrianMode;
    private Clock clock;

    // conditions
    private boolean pedRequest;
    private boolean emsRequest;

    private volatile Bearing emsBearing;

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
                if (emsRequest && emsBearing != null) {
                    emsMode.run();
                    continue;
                }
                // pedestrian requests have priority over day/night modes
                if (pedRequest) {
                    pedestrianMode.handlePedRequest();
                    pedRequest = false;
                    continue;
                }
                // select day or night based on clock
                if (clock.isDayTime()) {
                    dayMode.run();
                } else {
                    nightMode.run();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
