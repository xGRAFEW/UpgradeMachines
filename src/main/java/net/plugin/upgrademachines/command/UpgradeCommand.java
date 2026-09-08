package net.plugin.upgrademachines.command;

import net.plugin.upgrademachines.util.EconomyHook;
import net.plugin.upgrademachines.util.MachineType;
import net.plugin.upgrademachines.util.UpgradeManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class UpgradeCommand implements CommandExecutor, TabCompleter {

    private final JavaPlugin plugin;
    private final UpgradeManager manager;
    private final EconomyHook economy;

    public UpgradeCommand(JavaPlugin plugin, UpgradeManager manager, EconomyHook economy) {
        this.plugin = plugin;
        this.manager = manager;
        this.economy = economy;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§e/upgrade wand §7- รับไม้อัพเกรด");
            sender.sendMessage("§e/upgrade info §7- ดูระดับอัพเกรดของบล็อกที่กำลังมอง");
            sender.sendMessage("§e/upgrade reload §7- โหลดค่า config ใหม่");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "wand" -> handleWand(sender);
            case "info" -> handleInfo(sender);
            case "reload" -> handleReload(sender);
            default -> sender.sendMessage("§cคำสั่งไม่ถูกต้อง ใช้ /upgrade");
        }
        return true;
    }

    private void handleWand(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cคำสั่งนี้ใช้ได้เฉพาะในเกม");
            return;
        }
        if (!sender.hasPermission("upgrademachine.admin")) {
            sender.sendMessage("§cคุณไม่มีสิทธิ์");
            return;
        }
        player.getInventory().addItem(createWand());
        player.sendMessage("§aได้รับไม้อัพเกรดแล้ว! คลิกขวาที่เตาเผา/ฮอปเปอร์/ดิสเพนเซอร์/คราฟเตอร์เพื่ออัพเกรด");
        player.sendMessage("§7(Shift + คลิกขวา = ลดระดับ)");
    }

    private void handleInfo(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cคำสั่งนี้ใช้ได้เฉพาะในเกม");
            return;
        }
        Block block = player.getTargetBlockExact(6);
        if (block == null) {
            sender.sendMessage("§cมองไม่เห็นบล็อกในระยะ");
            return;
        }
        MachineType type = MachineType.fromBlock(block.getType());
        if (type == null) {
            sender.sendMessage("§cบล็อกนี้ไม่รองรับการอัพเกรด");
            return;
        }
        BlockState state = block.getState();
        int lvl = manager.getLevel(state);
        int max = manager.getMaxLevel(type);
        sender.sendMessage("§b" + type.name() + " §7ระดับ: §f" + lvl + "§7/" + max);

        if (lvl < max) {
            int next = lvl + 1;
            double moneyPrice = manager.getMoneyPrice(type, next);
            Material itemMaterial = manager.getItemPriceMaterial(type);
            int itemAmount = manager.getItemPriceAmount(type, next);

            List<String> parts = new ArrayList<>();
            if (moneyPrice > 0 && economy.isEnabled()) parts.add(economy.format(moneyPrice));
            if (itemMaterial != null && itemAmount > 0) parts.add(itemAmount + "x " + itemMaterial.name());

            String priceText = parts.isEmpty() ? "ฟรี" : String.join(" + ", parts);
            sender.sendMessage("§7ราคาอัพเกรดเป็น Lv." + next + ": §f" + priceText);
        }
    }

    private void handleReload(CommandSender sender) {
        if (!sender.hasPermission("upgrademachine.admin")) {
            sender.sendMessage("§cคุณไม่มีสิทธิ์");
            return;
        }
        plugin.reloadConfig();
        sender.sendMessage("§aโหลดค่า config ใหม่แล้ว");
    }

    private ItemStack createWand() {
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§d§lไม้อัพเกรดเครื่องจักร");
        meta.setLore(List.of(
                "§7คลิกขวาที่บล็อก: §aเพิ่มระดับ",
                "§7Shift + คลิกขวา: §cลดระดับ",
                "§7รองรับ: เตาเผา, ฮอปเปอร์, ดิสเพนเซอร์, คราฟเตอร์"
        ));
        meta.getPersistentDataContainer().set(manager.getKeys().wandLevel, PersistentDataType.INTEGER, 1);
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> options = new ArrayList<>(List.of("wand", "info", "reload"));
            options.removeIf(s -> !s.startsWith(args[0].toLowerCase()));
            return options;
        }
        return List.of();
    }
}
