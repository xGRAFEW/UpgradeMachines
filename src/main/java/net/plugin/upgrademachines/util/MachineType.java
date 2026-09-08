package net.plugin.upgrademachines.util;

import org.bukkit.Material;

public enum MachineType {
    FURNACE("furnace"),
    HOPPER("hopper"),
    DISPENSER("dispenser"),
    CRAFTER("crafter");

    private final String configKey;

    MachineType(String configKey) {
        this.configKey = configKey;
    }

    public String getConfigKey() {
        return configKey;
    }

    /** Maps a block Material to its MachineType, or null if unsupported. */
    public static MachineType fromBlock(Material material) {
        return switch (material) {
            case FURNACE, BLAST_FURNACE, SMOKER -> FURNACE;
            case HOPPER -> HOPPER;
            case DISPENSER -> DISPENSER;
            case CRAFTER -> CRAFTER;
            default -> null;
        };
    }
}
