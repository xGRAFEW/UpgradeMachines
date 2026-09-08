package net.plugin.upgrademachines.listener;

import net.plugin.upgrademachines.util.EconomyHook;
import net.plugin.upgrademachines.util.MachineType;
import net.plugin.upgrademachines.util.UpgradeManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Sneak + right-click a supported block to raise its level by 1 (capped at
 * the configured max, charging that level's configured price in money
 * and/or items). No wand or other item is needed - this works with an
 * empty hand or anything held. A plain (non-sneaking) right-click is left
 * untouched so the block still opens/works normally.
 */
public class UpgradeToolListener implements Listener {

    private final JavaPlugin plugin;
    private final UpgradeManager manager;
    private final EconomyHook economy;

    public UpgradeToolListener(JavaPlugin plugin, UpgradeManager manager, EconomyHook economy) {
        this.plugin = plugin;
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

        upgrade(player, state, type, current, max);
    }

    private void upgrade(Player player, BlockState state, MachineType type, int current, int max) {
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
        StringBuilder msg = new StringBuilder("§aอัพเกรด " + type.name() + " สำเร็จ! ระดับปัจจุบัน: §f" + applied + "§a/" + max);
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
