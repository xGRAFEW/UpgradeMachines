package net.plugin.upgrademachines.util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Performs the actual "check permission/price, charge, raise level by 1" transaction for a
 * machine - shared by every confirm UI (the inventory GUI's confirm button, and the action-bar
 * quick-confirm) so the checks and resulting chat messages only exist in one place.
 */
public final class UpgradeExecutor {

    private UpgradeExecutor() {}

    public static void upgrade(Player player, Block block, MachineType type, UpgradeManager manager, EconomyHook economy) {
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
            player.sendMessage("§eบล็อกนี้อยู่ที่ระดับสูงสุดแล้ว (Lv. " + UpgradeManager.toRoman(current) + "/" + UpgradeManager.toRoman(max) + ")");
            return;
        }

        double moneyPrice = manager.getMoneyPrice(type, target);
        Material itemMaterial = manager.getItemPriceMaterial(type);
        int itemAmount = manager.getItemPriceAmount(type, target);
        boolean chargeMoney = moneyPrice > 0 && economy.isEnabled();
        boolean chargeItem = itemMaterial != null && itemAmount > 0;

        if (chargeMoney && !economy.has(player, moneyPrice)) {
            player.sendMessage("§cเงินไม่พอ! ต้องการ §f" + economy.format(moneyPrice) + "§c เพื่ออัพเกรดเป็น Lv." + UpgradeManager.toRoman(target));
            return;
        }
        if (chargeItem && !player.getInventory().containsAtLeast(new ItemStack(itemMaterial), itemAmount)) {
            player.sendMessage("§cวัตถุดิบไม่พอ! ต้องการ §f" + itemAmount + "x " + itemMaterial.name() + "§c เพื่ออัพเกรดเป็น Lv." + UpgradeManager.toRoman(target));
            return;
        }

        if (chargeMoney) economy.withdraw(player, moneyPrice);
        if (chargeItem) player.getInventory().removeItem(new ItemStack(itemMaterial, itemAmount));

        int applied = manager.setLevel(state, target, type);
        StringBuilder msg = new StringBuilder("§aอัพเกรด " + manager.getDisplayName(type, block.getType(), applied) + " สำเร็จ! ระดับปัจจุบัน: §f"
                + UpgradeManager.toRoman(applied) + "§a/" + UpgradeManager.toRoman(max));
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
