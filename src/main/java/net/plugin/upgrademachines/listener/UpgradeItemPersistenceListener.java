package net.plugin.upgrademachines.listener;

import net.plugin.upgrademachines.util.MachineType;
import net.plugin.upgrademachines.util.UpgradeKeys;
import net.plugin.upgrademachines.util.UpgradeManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

/**
 * Keeps a machine's upgrade level attached to it across break/place:
 * breaking an upgraded block drops a renamed item (name + lore show its
 * current effect) with the level embedded in the item's own
 * PersistentDataContainer, and placing that item back applies the level
 * straight onto the new block.
 *
 * Limitation: only covers normal player block breaking (BlockBreakEvent) -
 * blocks destroyed by explosions or other means drop vanilla items and lose
 * their upgrade level.
 */
public class UpgradeItemPersistenceListener implements Listener {

    private final UpgradeKeys keys;
    private final UpgradeManager manager;

    public UpgradeItemPersistenceListener(UpgradeKeys keys, UpgradeManager manager) {
        this.keys = keys;
        this.manager = manager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onBreak(BlockBreakEvent event) {
        if (!event.isDropItems()) return;

        Block block = event.getBlock();
        MachineType type = MachineType.fromBlock(block.getType());
        if (type == null) return;

        BlockState state = block.getState();
        int level = manager.getLevel(state);
        if (level <= 0) return;

        event.setDropItems(false);
        block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), createUpgradedItem(block.getType(), type, level));
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        MachineType type = MachineType.fromBlock(event.getBlockPlaced().getType());
        if (type == null) return;

        ItemStack hand = event.getItemInHand();
        if (!hand.hasItemMeta()) return;

        Integer level = hand.getItemMeta().getPersistentDataContainer().get(keys.level, PersistentDataType.INTEGER);
        if (level == null || level <= 0) return;

        manager.setLevel(event.getBlockPlaced().getState(), level, type);
    }

    private ItemStack createUpgradedItem(Material material, MachineType type, int level) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§b" + manager.getDisplayName(type) + " §f→ §bLv." + level);
        meta.setLore(List.of("§7" + manager.describeEffect(type, level)));
        meta.getPersistentDataContainer().set(keys.level, PersistentDataType.INTEGER, level);
        item.setItemMeta(meta);
        return item;
    }
}
