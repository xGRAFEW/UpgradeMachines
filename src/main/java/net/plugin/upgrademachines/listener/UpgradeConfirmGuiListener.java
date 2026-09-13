package net.plugin.upgrademachines.listener;

import net.plugin.upgrademachines.gui.UpgradeConfirmGui;
import net.plugin.upgrademachines.gui.UpgradeConfirmHolder;
import net.plugin.upgrademachines.util.EconomyHook;
import net.plugin.upgrademachines.util.UpgradeExecutor;
import net.plugin.upgrademachines.util.UpgradeManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

/** Handles clicks in the sneak+right-click upgrade confirm/cancel GUI built by UpgradeConfirmGui. */
public class UpgradeConfirmGuiListener implements Listener {

    private final UpgradeManager manager;
    private final EconomyHook economy;

    public UpgradeConfirmGuiListener(UpgradeManager manager, EconomyHook economy) {
        this.manager = manager;
        this.economy = economy;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof UpgradeConfirmHolder) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof UpgradeConfirmHolder holder)) return;
        event.setCancelled(true);

        if (event.getClickedInventory() == null || !event.getClickedInventory().equals(event.getView().getTopInventory())) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player player)) return;

        int slot = event.getSlot();
        if (slot == UpgradeConfirmGui.CONFIRM_SLOT) {
            player.closeInventory();
            UpgradeExecutor.upgrade(player, holder.getBlock(), holder.getType(), manager, economy);
        } else if (slot == UpgradeConfirmGui.CANCEL_SLOT) {
            player.closeInventory();
            player.sendMessage("§7ยกเลิกการอัพเกรดแล้ว");
        }
    }
}
