package net.plugin.upgrademachines.util;

import org.bukkit.Material;

public enum MachineType {
    FURNACE("furnace", "เตาเผา"),
    HOPPER("hopper", "ฮอปเปอร์"),
    DISPENSER("dispenser", "ดิสเพนเซอร์"),
    DROPPER("dropper", "ดรอปเปอร์"),
    CRAFTER("crafter", "คราฟเตอร์");

    private final String configKey;
    private final String displayName;

    MachineType(String configKey, String displayName) {
        this.configKey = configKey;
        this.displayName = displayName;
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Maps a block Material to its MachineType, or null if unsupported. */
    public static MachineType fromBlock(Material material) {
        return switch (material) {
            case FURNACE, BLAST_FURNACE, SMOKER -> FURNACE;
            case HOPPER -> HOPPER;
            case DISPENSER -> DISPENSER;
            case DROPPER -> DROPPER;
            case CRAFTER -> CRAFTER;
            default -> null;
        };
    }
}
