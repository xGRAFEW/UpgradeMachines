package net.plugin.upgrademachines.listener;

import net.plugin.upgrademachines.gui.UpgradeConfirmGui;
import net.plugin.upgrademachines.util.EconomyHook;
import net.plugin.upgrademachines.util.MachineType;
import net.plugin.upgrademachines.util.UpgradeExecutor;
import net.plugin.upgrademachines.util.UpgradeManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Sneak + right-click a supported block to start an upgrade by 1 level. No wand or other item
 * is needed. A plain (non-sneaking) right-click is left untouched so the block still
 * opens/works normally. Two confirm styles are supported (see gui.style in config.yml):
 * - "inventory" (default): opens the confirm/cancel GUI built by UpgradeConfirmGui.
 * - "actionbar": shows an action-bar prompt with the price/effect; sneak+right-clicking the
 *   SAME block again within gui.actionbar.timeout-seconds confirms it (no GUI at all) - meant
 *   to be quicker to use on mobile/touch, where lining up taps on small GUI slots is awkward.
 * Either way the actual price check + level change happens in UpgradeExecutor.
 */
public class UpgradeToolListener implements Listener {

    private final UpgradeManager manager;
    private final EconomyHook economy;
    private final Map<UUID, PendingConfirm> pendingConfirms = new HashMap<>();

    private record PendingConfirm(Block block, long expiresAtMillis) {}

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

        if (manager.isActionBarStyle()) {
            onActionBarInteract(player, block, type);
            return;
        }

        BlockState state = block.getState();
        int current = manager.getLevel(state);
        int max = manager.getMaxLevel(type);
        if (current >= max) {
            player.sendMessage("§eบล็อกนี้อยู่ที่ระดับสูงสุดแล้ว (Lv. " + UpgradeManager.toRoman(current) + "/" + UpgradeManager.toRoman(max) + ")");
            return;
        }

        int target = current + 1;
        player.openInventory(UpgradeConfirmGui.build(block, type, current, target, max, manager, economy));
    }

    /** A second sneak+right-click on the same pending block within the timeout confirms it; any other click (re)shows the prompt. */
    private void onActionBarInteract(Player player, Block block, MachineType type) {
        PendingConfirm pending = pendingConfirms.get(player.getUniqueId());
        if (pending != null && pending.block().equals(block) && pending.expiresAtMillis() > System.currentTimeMillis()) {
            pendingConfirms.remove(player.getUniqueId());
            UpgradeExecutor.upgrade(player, block, type, manager, economy);
            return;
        }

        BlockState state = block.getState();
        int current = manager.getLevel(state);
        int max = manager.getMaxLevel(type);
        if (current >= max) {
            player.sendMessage("§eบล็อกนี้อยู่ที่ระดับสูงสุดแล้ว (Lv. " + UpgradeManager.toRoman(current) + "/" + UpgradeManager.toRoman(max) + ")");
            return;
        }
        int target = current + 1;

        double moneyPrice = manager.getMoneyPrice(type, target);
        Material itemMaterial = manager.getItemPriceMaterial(type);
        int itemAmount = manager.getItemPriceAmount(type, target);
        boolean chargeMoney = moneyPrice > 0 && economy.isEnabled();
        boolean chargeItem = itemMaterial != null && itemAmount > 0;

        String effect = manager.describeEffect(type, target);
        String price = manager.formatPrice(chargeMoney, chargeItem, moneyPrice, itemAmount, itemMaterial, economy);
        String prompt = manager.formatText(manager.getActionBarPromptFormat(), type, block.getType(), current, target, max, effect, price);

        long timeoutMillis = manager.getActionBarTimeoutSeconds() * 1000L;
        pendingConfirms.put(player.getUniqueId(), new PendingConfirm(block, System.currentTimeMillis() + timeoutMillis));
        player.sendActionBar(prompt);
    }
}
