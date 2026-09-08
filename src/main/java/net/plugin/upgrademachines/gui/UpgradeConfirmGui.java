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

import java.util.ArrayList;
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

        String title = "§8" + type.getDisplayName() + " §f" + manager.describeEffect(type, target)
                + " §7- " + priceSummary(chargeMoney, chargeItem, moneyPrice, itemAmount, itemMaterial, economy);

        UpgradeConfirmHolder holder = new UpgradeConfirmHolder(block, type);
        Inventory inv = Bukkit.createInventory(holder, SIZE, title);
        holder.setInventory(inv);

        ItemStack filler = namedItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        for (int i = 0; i < SIZE; i++) inv.setItem(i, filler);

        List<String> infoLore = new ArrayList<>();
        infoLore.add("§7ระดับ: §f" + current + " §7→ §aLv." + target + "§7/" + max);
        infoLore.add("§7ผล: §f" + manager.describeEffect(type, target));
        infoLore.add("");
        if (chargeMoney || chargeItem) {
            infoLore.add("§7ราคา:");
            if (chargeMoney) infoLore.add("  §f" + economy.format(moneyPrice));
            if (chargeItem) infoLore.add("  §f" + itemAmount + "x " + itemMaterial.name());
        } else {
            infoLore.add("§7ราคา: §aฟรี");
        }
        inv.setItem(INFO_SLOT, namedItem(block.getType(),
                "§b§l" + type.getDisplayName() + " §f→ §bLv." + target, infoLore));

        inv.setItem(CONFIRM_SLOT, namedItem(Material.LIME_STAINED_GLASS_PANE, "§a§lยืนยันอัพเกรด",
                List.of("§7คลิกเพื่อยืนยันการอัพเกรด")));
        inv.setItem(CANCEL_SLOT, namedItem(Material.RED_STAINED_GLASS_PANE, "§c§lยกเลิกการอัพเกรด",
                List.of("§7ปิดกล่องนี้โดยไม่เสียเงิน/ไอเทม")));

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
