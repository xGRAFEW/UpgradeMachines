package net.plugin.upgrademachines.listener;

import net.plugin.upgrademachines.util.UpgradeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

/**
 * Vanilla hoppers move exactly 1 item every 8 ticks. This listens for that
 * vanilla transfer and, for upgraded hoppers, immediately moves a few extra
 * matching items along the same path - so the hopper still ticks at the
 * normal vanilla speed, it just carries more per trip.
 *
 * Extra transfers are done with plain Inventory add/remove calls (not the
 * hopper's internal mechanic), so this does not re-trigger
 * InventoryMoveItemEvent and cannot recurse.
 */
public class HopperUpgradeListener implements Listener {

    private final JavaPlugin plugin;
    private final UpgradeManager manager;

    public HopperUpgradeListener(JavaPlugin plugin, UpgradeManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onMoveItem(InventoryMoveItemEvent event) {
        InventoryHolder holder = event.getInitiator().getHolder();
        if (!(holder instanceof Hopper hopper)) return;

        BlockState state = hopper.getBlock().getState();
        int level = manager.getLevel(state);
        if (level <= 0) return;

        int extra = manager.getHopperExtraTransfers(level);
        if (extra <= 0) return;

        Inventory source = event.getSource();
        Inventory destination = event.getDestination();
        ItemStack template = event.getItem().clone();
        template.setAmount(1);

        // Run next tick so the vanilla transfer for this event finishes first.
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (int i = 0; i < extra; i++) {
                if (!transferOneItem(source, destination, template)) break;
            }
        });
    }

    private boolean transferOneItem(Inventory source, Inventory destination, ItemStack template) {
        for (int slot = 0; slot < source.getSize(); slot++) {
            ItemStack stack = source.getItem(slot);
            if (stack == null || stack.getType() == Material.AIR) continue;
            if (!stack.isSimilar(template)) continue;

            ItemStack single = stack.clone();
            single.setAmount(1);

            Map<Integer, ItemStack> leftover = destination.addItem(single);
            if (!leftover.isEmpty()) return false; // destination full/can't accept

            stack.setAmount(stack.getAmount() - 1);
            source.setItem(slot, stack.getAmount() <= 0 ? null : stack);
            return true;
        }
        return false;
    }
}
