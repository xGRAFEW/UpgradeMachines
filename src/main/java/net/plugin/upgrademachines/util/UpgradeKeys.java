package net.plugin.upgrademachines.util;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Central place for the plugin's PersistentDataContainer keys.
 */
public class UpgradeKeys {

    /** Stored on the block (Furnace/Hopper/Dispenser/Crafter) itself - the upgrade level. */
    public final NamespacedKey level;

    public UpgradeKeys(JavaPlugin plugin) {
        this.level = new NamespacedKey(plugin, "upgrade_level");
    }
}
