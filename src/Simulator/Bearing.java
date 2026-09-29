package Simulator;

public enum Bearing {
    North,
    South,
    East,
    West;

    // safe String -> Bearing conversion
    public static Bearing fromWire(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("eBearing: Bearing cannot be null/empty.");
        }
        for (Bearing bearing : Bearing.values()) {
            if (bearing.name().equalsIgnoreCase(value.trim())) {
                return bearing;
            }
        }
        throw new IllegalArgumentException("eBearing: Unknown bearing: '" + value + "'");
    }
}