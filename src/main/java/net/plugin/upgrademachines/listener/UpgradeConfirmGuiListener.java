package net.plugin.upgrademachines.listener;

import net.plugin.upgrademachines.gui.UpgradeConfirmGui;
import net.plugin.upgrademachines.gui.UpgradeConfirmHolder;
import net.plugin.upgrademachines.util.EconomyHook;
import net.plugin.upgrademachines.util.MachineType;
import net.plugin.upgrademachines.util.UpgradeManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

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
            confirmUpgrade(player, holder.getBlock(), holder.getType());
        } else if (slot == UpgradeConfirmGui.CANCEL_SLOT) {
            player.closeInventory();
            player.sendMessage("§7ยกเลิกการอัพเกรดแล้ว");
        }
    }

    private void confirmUpgrade(Player player, Block block, MachineType type) {
        if (!player.hasPermission("upgrademachine.use")) {
            player.sendMessage("§cคุณไม่มีสิทธิ์อัพเกรดเครื่องจักร");
            return;
        }
        if (MachineType.fromBlock(block.getType()) != type) {
            player.sendMessage("§cบล็อกนี้เปลี่ยนไปแล้ว ยกเลิกการอัพเกรด");
            return;
        }

        BlockState state = block.getState();
        int current = manager.getLevel(state);
        int max = manager.getMaxLevel(type);
        int target = Math.min(max, current + 1);
        if (target == current) {
            player.sendMessage("§eบล็อกนี้อยู่ที่ระดับสูงสุดแล้ว (Lv. " + current + "/" + max + ")");
            return;
        }

        double moneyPrice = manager.getMoneyPrice(type, target);
        Material itemMaterial = manager.getItemPriceMaterial(type);
        int itemAmount = manager.getItemPriceAmount(type, target);
        boolean chargeMoney = moneyPrice > 0 && economy.isEnabled();
        boolean chargeItem = itemMaterial != null && itemAmount > 0;

        if (chargeMoney && !economy.has(player, moneyPrice)) {
            player.sendMessage("§cเงินไม่พอ! ต้องการ §f" + economy.format(moneyPrice) + "§c เพื่ออัพเกรดเป็น Lv." + target);
            return;
        }
        if (chargeItem && !player.getInventory().containsAtLeast(new ItemStack(itemMaterial), itemAmount)) {
            player.sendMessage("§cวัตถุดิบไม่พอ! ต้องการ §f" + itemAmount + "x " + itemMaterial.name() + "§c เพื่ออัพเกรดเป็น Lv." + target);
            return;
        }

        if (chargeMoney) economy.withdraw(player, moneyPrice);
        if (chargeItem) player.getInventory().removeItem(new ItemStack(itemMaterial, itemAmount));

        int applied = manager.setLevel(state, target, type);
        StringBuilder msg = new StringBuilder("§aอัพเกรด " + manager.getDisplayName(type, block.getType()) + " สำเร็จ! ระดับปัจจุบัน: §f" + applied + "§a/" + max);
        if (chargeMoney || chargeItem) {
            msg.append(" §7(จ่าย: ");
            if (chargeMoney) msg.append(economy.format(moneyPrice));
            if (chargeMoney && chargeItem) msg.append(" + ");
            if (chargeItem) msg.append(itemAmount).append("x ").append(itemMaterial.name());
            msg.append(")");
        }
        player.sendMessage(msg.toString());
    }
}
