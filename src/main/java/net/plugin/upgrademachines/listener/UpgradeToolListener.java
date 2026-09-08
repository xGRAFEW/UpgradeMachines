package net.plugin.upgrademachines.listener;

import net.plugin.upgrademachines.gui.UpgradeConfirmGui;
import net.plugin.upgrademachines.util.EconomyHook;
import net.plugin.upgrademachines.util.MachineType;
import net.plugin.upgrademachines.util.UpgradeManager;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 * Sneak + right-click a supported block to open a confirm/cancel GUI for
 * raising its level by 1 (see UpgradeConfirmGui / UpgradeConfirmGuiListener
 * for the actual price check + level change). No wand or other item is
 * needed. A plain (non-sneaking) right-click is left untouched so the block
 * still opens/works normally.
 */
public class UpgradeToolListener implements Listener {

    private final UpgradeManager manager;
    private final EconomyHook economy;

    public UpgradeToolListener(UpgradeManager manager, EconomyHook economy) {
        this.manager = manager;
        this.economy = economy;
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!event.getPlayer().isSneaking()) return;

        Block block = event.getClickedBlock();
        if (block == null) return;

        MachineType type = MachineType.fromBlock(block.getType());
        if (type == null) return;

        Player player = event.getPlayer();

        // We're handling this ourselves - don't let it also open the block normally.
        event.setCancelled(true);

        if (!player.hasPermission("upgrademachine.use")) {
            player.sendMessage("§cคุณไม่มีสิทธิ์อัพเกรดเครื่องจักร");
            return;
        }

        BlockState state = block.getState();
        int current = manager.getLevel(state);
        int max = manager.getMaxLevel(type);
        if (current >= max) {
            player.sendMessage("§eบล็อกนี้อยู่ที่ระดับสูงสุดแล้ว (Lv. " + current + "/" + max + ")");
            return;
        }

        int target = current + 1;
        player.openInventory(UpgradeConfirmGui.build(block, type, current, target, max, manager, economy));
    }
}
