package net.plugin.upgrademachines.listener;

import net.plugin.upgrademachines.util.MachineType;
import net.plugin.upgrademachines.util.UpgradeManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Every vanilla redstone pulse, a dispenser/dropper fires BlockDispenseEvent
 * once and ejects exactly 1 item. For upgraded ones, right after the normal
 * eject we pull a few extra matching items straight out of the block's
 * inventory and drop them out the front. Dispenser and Dropper share this
 * one listener since both are plain Container blocks with identical
 * ejection semantics for our purposes - only the configured rate differs.
 *
 * NOTE (limitation): the extras are ejected as plain dropped items, not
 * replayed through the dispenser's special-item behaviour - so things like
 * bucket placement, spawn eggs, or arrow-shooting physics are only exact
 * for the original 1 item; the bonus items just pop out. This keeps the
 * plugin simple and safe across all item types.
 */
public class DispenserUpgradeListener implements Listener {

    private final JavaPlugin plugin;
    private final UpgradeManager manager;

    public DispenserUpgradeListener(JavaPlugin plugin, UpgradeManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDispense(BlockDispenseEvent event) {
        Block block = event.getBlock();
        BlockState state = block.getState();
        MachineType type = MachineType.fromBlock(state.getType());
        if (type != MachineType.DISPENSER && type != MachineType.DROPPER) return;

        int level = manager.getLevel(state);
        if (level <= 0) return;

        int extra = manager.getExtraItems(type, level);
        if (extra <= 0) return;

        ItemStack template = event.getItem().clone();
        template.setAmount(1);

        Bukkit.getScheduler().runTask(plugin, () -> {
            BlockState freshState = block.getState();
            if (!(freshState instanceof Container container)) return;

            Inventory inv = container.getInventory();
            BlockData data = block.getBlockData();
            BlockFace facing = (data instanceof Directional directional) ? directional.getFacing() : BlockFace.UP;
            Location dropLoc = block.getRelative(facing).getLocation().add(0.5, 0.5, 0.5);

            for (int i = 0; i < extra; i++) {
                if (!removeOne(inv, template)) break;
                dropLoc.getWorld().dropItemNaturally(dropLoc, template.clone());
            }
        });
    }

    private boolean removeOne(Inventory inv, ItemStack template) {
        for (int slot = 0; slot < inv.getSize(); slot++) {
            ItemStack stack = inv.getItem(slot);
            if (stack == null || stack.getType() == Material.AIR) continue;
            if (!stack.isSimilar(template)) continue;
            stack.setAmount(stack.getAmount() - 1);
            inv.setItem(slot, stack.getAmount() <= 0 ? null : stack);
            return true;
        }
        return false;
    }
}
