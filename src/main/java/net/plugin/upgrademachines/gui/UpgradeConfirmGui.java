package net.plugin.upgrademachines.gui;

import net.plugin.upgrademachines.util.EconomyHook;
import net.plugin.upgrademachines.util.MachineType;
import net.plugin.upgrademachines.util.UpgradeManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/** Builds the confirm/cancel GUI shown when a player sneak + right-clicks a supported machine. */
public final class UpgradeConfirmGui {

    public static final int CONFIRM_SLOT = 11;
    public static final int INFO_SLOT = 13;
    public static final int CANCEL_SLOT = 15;
    private static final int SIZE = 27;

    private UpgradeConfirmGui() {}

    public static Inventory build(Block block, MachineType type, int current, int target, int max,
                                   UpgradeManager manager, EconomyHook economy) {
        double moneyPrice = manager.getMoneyPrice(type, target);
        Material itemMaterial = manager.getItemPriceMaterial(type);
        int itemAmount = manager.getItemPriceAmount(type, target);
        boolean chargeMoney = moneyPrice > 0 && economy.isEnabled();
        boolean chargeItem = itemMaterial != null && itemAmount > 0;

        String effect = manager.describeEffect(type, target);
        String price = priceSummary(chargeMoney, chargeItem, moneyPrice, itemAmount, itemMaterial, economy);

        String title = manager.formatText(manager.getGuiTitleFormat(), type, current, target, max, effect, price);

        UpgradeConfirmHolder holder = new UpgradeConfirmHolder(block, type);
        Inventory inv = Bukkit.createInventory(holder, SIZE, title);
        holder.setInventory(inv);

        ItemStack filler = namedItem(manager.getGuiFillerMaterial(), " ", null);
        for (int i = 0; i < SIZE; i++) inv.setItem(i, filler);

        String infoName = manager.formatText(manager.getGuiInfoNameFormat(), type, current, target, max, effect, price);
        List<String> infoLore = manager.formatTextList(manager.getGuiInfoLoreFormat(), type, current, target, max, effect, price);
        inv.setItem(INFO_SLOT, namedItem(block.getType(), infoName, infoLore));

        String confirmName = manager.formatText(manager.getGuiConfirmButtonName(), type, current, target, max, effect, price);
        List<String> confirmLore = manager.formatTextList(manager.getGuiConfirmButtonLore(), type, current, target, max, effect, price);
        inv.setItem(CONFIRM_SLOT, namedItem(manager.getGuiConfirmButtonMaterial(), confirmName, confirmLore));

        String cancelName = manager.formatText(manager.getGuiCancelButtonName(), type, current, target, max, effect, price);
        List<String> cancelLore = manager.formatTextList(manager.getGuiCancelButtonLore(), type, current, target, max, effect, price);
        inv.setItem(CANCEL_SLOT, namedItem(manager.getGuiCancelButtonMaterial(), cancelName, cancelLore));

        return inv;
    }

    private static String priceSummary(boolean chargeMoney, boolean chargeItem, double moneyPrice,
                                        int itemAmount, Material itemMaterial, EconomyHook economy) {
        if (!chargeMoney && !chargeItem) return "§aฟรี";
        StringBuilder summary = new StringBuilder("§f");
        if (chargeMoney) summary.append(economy.format(moneyPrice));
        if (chargeMoney && chargeItem) summary.append(" §7+ §f");
        if (chargeItem) summary.append(itemAmount).append("x ").append(itemMaterial.name());
        return summary.toString();
    }

    private static ItemStack namedItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
