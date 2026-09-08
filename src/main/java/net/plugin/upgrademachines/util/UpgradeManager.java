package net.plugin.upgrademachines.util;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Reads/writes the upgrade level straight onto the block's own
 * PersistentDataContainer (every Furnace/Hopper/Dispenser/Crafter is a
 * TileState, so no external storage/database is needed) and resolves
 * the configured rate values per level.
 */
public class UpgradeManager {

    private final JavaPlugin plugin;
    private final UpgradeKeys keys;

    public UpgradeManager(JavaPlugin plugin, UpgradeKeys keys) {
        this.plugin = plugin;
        this.keys = keys;
    }

    public int getLevel(BlockState state) {
        if (!(state instanceof TileState tile)) return 0;
        Integer lvl = tile.getPersistentDataContainer().get(keys.level, PersistentDataType.INTEGER);
        return lvl == null ? 0 : lvl;
    }

    /** Sets (or clears, if 0) the upgrade level on the given block state. Returns the clamped level applied. */
    public int setLevel(BlockState state, int level, MachineType type) {
        if (!(state instanceof TileState tile)) return 0;
        int max = getMaxLevel(type);
        int clamped = Math.max(0, Math.min(level, max));
        if (clamped == 0) {
            tile.getPersistentDataContainer().remove(keys.level);
        } else {
            tile.getPersistentDataContainer().set(keys.level, PersistentDataType.INTEGER, clamped);
        }
        tile.update();
        return clamped;
    }

    public int getMaxLevel(MachineType type) {
        return plugin.getConfig().getInt(type.getConfigKey() + ".max-level", 5);
    }

    /** How many items a single cook cycle produces/consumes at this level (1 = vanilla). */
    public int getFurnaceBatchSize(int level) {
        return getIntFromList("furnace.smelt-batch-size", level, 1);
    }

    public int getHopperExtraTransfers(int level) {
        return getIntFromList("hopper.extra-transfers", level, 0);
    }

    public int getDispenserExtraItems(int level) {
        return getIntFromList("dispenser.extra-items", level, 0);
    }

    public int getCrafterExtraCrafts(int level) {
        return getIntFromList("crafter.cooldown-reduction-ticks", level, 0);
    }

    // ---- Prices ----

    /** Money cost to reach the given level (0 if unset or Vault not present is handled by the caller). */
    public double getMoneyPrice(MachineType type, int level) {
        return getDoubleFromList(type.getConfigKey() + ".price.money", level, 0);
    }

    /** Item material required to reach the given level, or null if no item price is configured. */
    public Material getItemPriceMaterial(MachineType type) {
        String name = plugin.getConfig().getString(type.getConfigKey() + ".price.item.material", "");
        if (name == null || name.isBlank()) return null;
        return Material.matchMaterial(name);
    }

    /** How many of the item-price material are required to reach the given level. */
    public int getItemPriceAmount(MachineType type, int level) {
        return getIntFromList(type.getConfigKey() + ".price.item.amounts", level, 0);
    }

    private double getDoubleFromList(String path, int index, double def) {
        List<Double> list = plugin.getConfig().getDoubleList(path);
        if (index < 0 || index >= list.size()) return def;
        return list.get(index);
    }

    private int getIntFromList(String path, int index, int def) {
        List<Integer> list = plugin.getConfig().getIntegerList(path);
        if (index < 0 || index >= list.size()) return def;
        return list.get(index);
    }
}
