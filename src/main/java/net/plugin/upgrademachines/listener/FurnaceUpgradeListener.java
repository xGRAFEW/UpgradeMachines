package net.plugin.upgrademachines.listener;

import net.plugin.upgrademachines.util.UpgradeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Batch smelting for upgraded Furnace / Blast Furnace / Smoker blocks
 * (all three implement the same {@link Furnace} interface).
 *
 * Each cook cycle still takes the normal vanilla amount of time and burns
 * the normal amount of fuel - we just let one completed cook consume and
 * produce several items at once instead of 1, by topping up the extra
 * items right after vanilla's own single-item smelt for that cycle.
 */
public class FurnaceUpgradeListener implements Listener {

    private final JavaPlugin plugin;
    private final UpgradeManager manager;

    public FurnaceUpgradeListener(JavaPlugin plugin, UpgradeManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(ignoreCancelled = true)
    public void onSmelt(FurnaceSmeltEvent event) {
        Block block = event.getBlock();
        BlockState state = block.getState();
        if (!(state instanceof Furnace)) return;

        int level = manager.getLevel(state);
        if (level <= 0) return;

        int batchSize = manager.getFurnaceBatchSize(level);
        int extra = batchSize - 1; // vanilla already handles the 1st item itself
        if (extra <= 0) return;

        ItemStack sourceTemplate = event.getSource().clone();
        sourceTemplate.setAmount(1);

        ItemStack resultTemplate = event.getResult().clone();
        int perCraftYield = Math.max(1, resultTemplate.getAmount());
        resultTemplate.setAmount(perCraftYield);

        // Apply the extra consumption/production next tick, after vanilla
        // has finished applying its own single-item smelt for this event.
        Bukkit.getScheduler().runTask(plugin, () -> {
            BlockState freshState = block.getState();
            if (!(freshState instanceof Furnace freshFurnace)) return;

            FurnaceInventory inv = freshFurnace.getInventory();
            for (int i = 0; i < extra; i++) {
                if (!consumeOneSmeltingSlot(inv, sourceTemplate)) break;
                if (!depositResult(inv, resultTemplate, block)) break;
            }
        });
    }

    private boolean consumeOneSmeltingSlot(FurnaceInventory inv, ItemStack template) {
        ItemStack smelting = inv.getSmelting();
        if (smelting == null || smelting.getType() == Material.AIR) return false;
        if (!smelting.isSimilar(template)) return false;
        if (smelting.getAmount() <= 0) return false;

        smelting.setAmount(smelting.getAmount() - 1);
        inv.setSmelting(smelting.getAmount() <= 0 ? null : smelting);
        return true;
    }

    private boolean depositResult(FurnaceInventory inv, ItemStack template, Block block) {
        ItemStack result = inv.getResult();
        if (result == null || result.getType() == Material.AIR) {
            inv.setResult(template.clone());
            return true;
        }
        if (!result.isSimilar(template)) {
            // Output slot holds something else (shouldn't normally happen) - stop here.
            return false;
        }
        int max = result.getMaxStackSize();
        if (result.getAmount() + template.getAmount() > max) {
            // Output slot full - drop the overflow above the furnace instead of losing it.
            var loc = block.getLocation().add(0.5, 1.0, 0.5);
            loc.getWorld().dropItemNaturally(loc, template.clone());
            return true;
        }
        result.setAmount(result.getAmount() + template.getAmount());
        inv.setResult(result);
        return true;
    }
}
