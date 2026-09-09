package net.plugin.upgrademachines.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads/writes the upgrade level straight onto the block's own
 * PersistentDataContainer (every Furnace/Hopper/Dispenser/Dropper/Crafter is
 * a TileState, so no external storage/database is needed) and resolves the
 * configured rate values per level.
 */
public class UpgradeManager {

    private final JavaPlugin plugin;
    private final UpgradeKeys keys;

    public UpgradeManager(JavaPlugin plugin, UpgradeKeys keys) {
        this.plugin = plugin;
        this.keys = keys;
    }

    public int getLevel(BlockState state) {
        if (!(state instanceof TileState tile)) return 0;
        Integer lvl = tile.getPersistentDataContainer().get(keys.level, PersistentDataType.INTEGER);
        return lvl == null ? 0 : lvl;
    }

    /** Sets (or clears, if 0) the upgrade level on the given block state. Returns the clamped level applied. */
    public int setLevel(BlockState state, int level, MachineType type) {
        if (!(state instanceof TileState tile)) return 0;
        int max = getMaxLevel(type);
        int clamped = Math.max(0, Math.min(level, max));
        if (clamped == 0) {
            tile.getPersistentDataContainer().remove(keys.level);
        } else {
            tile.getPersistentDataContainer().set(keys.level, PersistentDataType.INTEGER, clamped);
        }
        tile.update();
        return clamped;
    }

    public int getMaxLevel(MachineType type) {
        return plugin.getConfig().getInt(type.getConfigKey() + ".max-level", 5);
    }

    /** Display name shown in GUI/messages/item names - configurable via display-name, falls back to the built-in Thai name. */
    public String getDisplayName(MachineType type) {
        return plugin.getConfig().getString(type.getConfigKey() + ".display-name", type.getDisplayName());
    }

    /** How many items a single cook cycle produces/consumes at this level (1 = vanilla). */
    public int getFurnaceBatchSize(int level) {
        return getIntFromList("furnace.smelt-batch-size", level, 1);
    }

    public int getHopperExtraTransfers(int level) {
        return getIntFromList("hopper.extra-transfers", level, 0);
    }

    /** Extra items ejected per trigger, on top of the normal 1 - shared config shape for Dispenser/Dropper. */
    public int getExtraItems(MachineType type, int level) {
        return getIntFromList(type.getConfigKey() + ".extra-items", level, 0);
    }

    public int getCrafterExtraCrafts(int level) {
        return getIntFromList("crafter.cooldown-reduction-ticks", level, 0);
    }

    /** Short Thai description of what a level actually does - used in the confirm GUI and on upgraded item lore. */
    public String describeEffect(MachineType type, int level) {
        return switch (type) {
            case FURNACE -> "เผาทีละ: " + getFurnaceBatchSize(level) + " ชิ้น";
            case HOPPER -> "ส่งทีละ: " + (1 + getHopperExtraTransfers(level)) + " ชิ้น";
            case DISPENSER -> "ยิงทีละ: " + (1 + getExtraItems(type, level)) + " ชิ้น";
            case DROPPER -> "ดรอปทีละ: " + (1 + getExtraItems(type, level)) + " ชิ้น";
            case CRAFTER -> "คราฟทีละ: " + (1 + getCrafterExtraCrafts(level)) + " ครั้ง";
        };
    }

    // ---- Prices ----

    /** Money cost to reach the given level (0 if unset or Vault not present is handled by the caller). */
    public double getMoneyPrice(MachineType type, int level) {
        return getDoubleFromList(type.getConfigKey() + ".price.money", level, 0);
    }

    /** Item material required to reach the given level, or null if no item price is configured. */
    public Material getItemPriceMaterial(MachineType type) {
        String name = plugin.getConfig().getString(type.getConfigKey() + ".price.item.material", "");
        if (name == null || name.isBlank()) return null;
        return Material.matchMaterial(name);
    }

    /** How many of the item-price material are required to reach the given level. */
    public int getItemPriceAmount(MachineType type, int level) {
        return getIntFromList(type.getConfigKey() + ".price.item.amounts", level, 0);
    }

    private double getDoubleFromList(String path, int index, double def) {
        List<Double> list = plugin.getConfig().getDoubleList(path);
        if (index < 0 || index >= list.size()) return def;
        return list.get(index);
    }

    private int getIntFromList(String path, int index, int def) {
        List<Integer> list = plugin.getConfig().getIntegerList(path);
        if (index < 0 || index >= list.size()) return def;
        return list.get(index);
    }

    // ---- Configurable GUI/item text (config.yml "gui" section) ----

    /** Applies &-color codes then substitutes {name}/{level}/{current}/{max}/{effect}/{price} in one template string. */
    public String formatText(String template, MachineType type, int current, int target, int max, String effect, String price) {
        return ChatColor.translateAlternateColorCodes('&', template)
                .replace("{name}", getDisplayName(type))
                .replace("{level}", String.valueOf(target))
                .replace("{current}", String.valueOf(current))
                .replace("{max}", String.valueOf(max))
                .replace("{effect}", effect == null ? "" : effect)
                .replace("{price}", price == null ? "" : price);
    }

    /** Same as {@link #formatText} but applied to every line of a lore list. */
    public List<String> formatTextList(List<String> templates, MachineType type, int current, int target, int max, String effect, String price) {
        List<String> result = new ArrayList<>(templates.size());
        for (String line : templates) result.add(formatText(line, type, current, target, max, effect, price));
        return result;
    }

    public String getGuiTitleFormat() {
        return plugin.getConfig().getString("gui.title-format", "&8{name} &f{effect} &7- {price}");
    }

    public String getGuiInfoNameFormat() {
        return plugin.getConfig().getString("gui.info-item.name-format", "&b&l{name} &f→ &bLv.{level}");
    }

    public List<String> getGuiInfoLoreFormat() {
        return getStringListOrDefault("gui.info-item.lore", List.of(
                "&7ระดับ: &f{current} &7→ &a{level}&7/{max}", "&7ผล: &f{effect}", "", "&7ราคา: &f{price}"));
    }

    public Material getGuiConfirmButtonMaterial() {
        return getMaterialOrDefault("gui.confirm-button.material", Material.LIME_STAINED_GLASS_PANE);
    }

    public String getGuiConfirmButtonName() {
        return plugin.getConfig().getString("gui.confirm-button.name", "&a&lยืนยันอัพเกรด");
    }

    public List<String> getGuiConfirmButtonLore() {
        return getStringListOrDefault("gui.confirm-button.lore", List.of("&7คลิกเพื่อยืนยันการอัพเกรด"));
    }

    public Material getGuiCancelButtonMaterial() {
        return getMaterialOrDefault("gui.cancel-button.material", Material.RED_STAINED_GLASS_PANE);
    }

    public String getGuiCancelButtonName() {
        return plugin.getConfig().getString("gui.cancel-button.name", "&c&lยกเลิกการอัพเกรด");
    }

    public List<String> getGuiCancelButtonLore() {
        return getStringListOrDefault("gui.cancel-button.lore", List.of("&7ปิดกล่องนี้โดยไม่เสียเงิน/ไอเทม"));
    }

    public Material getGuiFillerMaterial() {
        return getMaterialOrDefault("gui.filler.material", Material.GRAY_STAINED_GLASS_PANE);
    }

    /** Name format for the item dropped when breaking an upgraded block (also used when applying it back on place). */
    public String getItemNameFormat() {
        return plugin.getConfig().getString("gui.item-name-format", "&b{name} &f→ &bLv.{level}");
    }

    public List<String> getItemLoreFormat() {
        return getStringListOrDefault("gui.item-lore-format", List.of("&7{effect}"));
    }

    private List<String> getStringListOrDefault(String path, List<String> def) {
        List<String> list = plugin.getConfig().getStringList(path);
        return list.isEmpty() ? def : list;
    }

    private Material getMaterialOrDefault(String path, Material def) {
        String name = plugin.getConfig().getString(path);
        if (name == null || name.isBlank()) return def;
        Material mat = Material.matchMaterial(name);
        return mat != null ? mat : def;
    }
}
