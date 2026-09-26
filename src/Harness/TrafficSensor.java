package Harness;

import Simulator.Bearing;

import java.util.EnumMap;
import java.util.Map;

public class TrafficSensor {
    private Mux mux;
    private final Map<Bearing, Boolean> vehiclePresent = new EnumMap<>(Bearing.class);

    public TrafficSensor(Mux mux) {
        this.mux = mux;
        for (Bearing bearing : Bearing.values()) {
            vehiclePresent.put(bearing, false);
        }
    }

    public void vehicleDetected(Bearing bearing) {
        vehiclePresent.put(bearing, true);
    }

    public void vehicleCleared(Bearing bearing) {
        vehiclePresent.put(bearing, false);
    }

    public boolean isVehiclePresent(Bearing bearing) {
        return vehiclePresent.get(bearing);
    }
}