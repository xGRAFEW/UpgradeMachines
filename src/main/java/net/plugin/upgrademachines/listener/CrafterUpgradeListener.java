package net.plugin.upgrademachines.listener;

import org.bukkit.event.block.CrafterCraftEvent;
import net.plugin.upgrademachines.util.UpgradeManager;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Crafter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Crafter (added in 1.21) is a newer block and its plugin API is less
 * mature/stable than furnace/hopper/dispenser, and there is no reliably
 * exposed way to shrink its internal cooldown directly. So instead this
 * listens for a completed craft and, for upgraded crafters, immediately
 * repeats the craft a few extra times using whatever ingredients are still
 * sitting in the same slots - matching the "extra output per trigger"
 * pattern used for hopper/dispenser above.
 *
 * This is best-effort / experimental: test it on your exact server version.
 */
public class CrafterUpgradeListener implements Listener {

    private final JavaPlugin plugin;
    private final UpgradeManager manager;

    public CrafterUpgradeListener(JavaPlugin plugin, UpgradeManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onCraft(CrafterCraftEvent event) {
        BlockState state = event.getBlock().getState();
        if (!(state instanceof Crafter)) return;

        int level = manager.getLevel(state);
        if (level <= 0) return;

        int extraCrafts = manager.getCrafterExtraCrafts(level);
        if (extraCrafts <= 0) return;

        ItemStack result = event.getResult().clone();
        if (result.getType().isAir()) return;

        Block block = event.getBlock();

        Bukkit.getScheduler().runTask(plugin, () -> {
            BlockState freshState = block.getState();
            if (!(freshState instanceof Crafter freshCrafter)) return;

            Inventory craftInv = freshCrafter.getInventory();
            for (int i = 0; i < extraCrafts; i++) {
                if (!consumeOneOfEachSlot(craftInv)) break;
                ejectResult(block, result.clone());
            }
        });
    }

    /** Consumes 1 item from every currently-filled slot (mirrors the grid that was just used). */
    private boolean consumeOneOfEachSlot(Inventory craftInv) {
        boolean consumedAny = false;
        for (int slot = 0; slot < craftInv.getSize(); slot++) {
            ItemStack item = craftInv.getItem(slot);
            if (item == null || item.getType().isAir()) continue;
            item.setAmount(item.getAmount() - 1);
            craftInv.setItem(slot, item.getAmount() <= 0 ? null : item);
            consumedAny = true;
        }
        return consumedAny;
    }

    /** Tries adjacent containers first (like a crafter normally feeds a hopper/chest), else drops the item. */
    private void ejectResult(Block crafterBlock, ItemStack result) {
        for (BlockFace face : new BlockFace[]{BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP}) {
            Block rel = crafterBlock.getRelative(face);
            if (rel.getState() instanceof InventoryHolder holder) {
                var leftover = holder.getInventory().addItem(result);
                if (leftover.isEmpty()) return;
                result = leftover.values().iterator().next();
            }
        }
        var loc = crafterBlock.getLocation().add(0.5, 1.0, 0.5);
        loc.getWorld().dropItemNaturally(loc, result);
    }
}
