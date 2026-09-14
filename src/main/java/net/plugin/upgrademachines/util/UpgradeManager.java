package net.plugin.upgrademachines.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Nameable;
import org.bukkit.block.BlockState;
import org.bukkit.block.TileState;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * Sets (or clears, if 0) the upgrade level on the given block state. Returns the clamped
     * level applied. Also sets the block's own vanilla custom name (the same one used on a
     * dropped/held item - see getItemDisplayName) via the Nameable interface every
     * Furnace/Hopper/Dispenser/Dropper/Crafter implements, so the block's own inventory title
     * (e.g. opening the Crafter to craft) updates immediately - without this, upgrading in
     * place left the container's title showing its old/vanilla name until the block was broken
     * and placed again (breaking is what indirectly sets it today, via the renamed item's
     * display name carrying over to the newly placed block, a vanilla mechanic).
     */
    public int setLevel(BlockState state, int level, MachineType type) {
        if (!(state instanceof TileState tile)) return 0;
        int max = getMaxLevel(type);
        int clamped = Math.max(0, Math.min(level, max));
        if (clamped == 0) {
            tile.getPersistentDataContainer().remove(keys.level);
        } else {
            tile.getPersistentDataContainer().set(keys.level, PersistentDataType.INTEGER, clamped);
        }
        if (tile instanceof Nameable nameable) {
            nameable.setCustomName(clamped > 0 ? getItemDisplayName(type, state.getType(), clamped) : null);
        }
        tile.update();
        return clamped;
    }

    public int getMaxLevel(MachineType type) {
        return plugin.getConfig().getInt(type.getConfigKey() + ".max-level", 5);
    }

    /**
     * Display name for one specific block material at a specific level - used in the GUI,
     * messages, and on dropped/held items. Each of display-names.<MATERIAL> and the shared
     * display-name can be set in config.yml as either a single string (same name at every
     * level) or a YAML list of strings indexed by level (index 0 = not upgraded, same
     * convention as the rate lists like smelt-batch-size) so every level of every machine
     * can have its own name. Resolution order: per-material name/level-list, then the shared
     * display-name name/level-list, then the built-in Thai name.
     */
    public String getDisplayName(MachineType type, Material material, int level) {
        String perMaterial = resolveConfiguredName(type.getConfigKey() + ".display-names." + material.name(), level);
        if (perMaterial != null) return perMaterial;
        String shared = resolveConfiguredName(type.getConfigKey() + ".display-name", level);
        return shared != null ? shared : type.getDisplayName();
    }

    /** Reads a name that may be a single string or a level-indexed list; null if unset/blank/out of range. */
    private String resolveConfiguredName(String path, int level) {
        if (plugin.getConfig().isList(path)) {
            List<String> names = plugin.getConfig().getStringList(path);
            if (level < 0 || level >= names.size()) return null;
            String value = names.get(level);
            return (value == null || value.isBlank()) ? null : value;
        }
        String value = plugin.getConfig().getString(path);
        return (value == null || value.isBlank()) ? null : value;
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

    /** The raw numeric rate value for a level (batch size, extra transfers+1, etc.) - the same number {rate} resolves to everywhere. */
    public int getRate(MachineType type, int level) {
        return switch (type) {
            case FURNACE -> getFurnaceBatchSize(level);
            case HOPPER -> 1 + getHopperExtraTransfers(level);
            case DISPENSER, DROPPER -> 1 + getExtraItems(type, level);
            case CRAFTER -> 1 + getCrafterExtraCrafts(level);
        };
    }

    /**
     * Description of what a level actually does - used in the confirm GUI, /upgrade info, and
     * on upgraded item lore. The wording is configurable via <configKey>.effect-format (with a
     * {rate} placeholder for the numeric value); the number itself still comes from the type's
     * own rate list and can't be reworded away from a plain integer.
     */
    public String describeEffect(MachineType type, int level) {
        String format = plugin.getConfig().getString(type.getConfigKey() + ".effect-format", defaultEffectFormat(type));
        return translateColors(format).replace("{rate}", String.valueOf(getRate(type, level)));
    }

    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");

    /** Applies &-color codes, including hex colors written as &#RRGGBB (e.g. "&#4D60FFHello"). */
    public static String translateColors(String text) {
        Matcher matcher = HEX_COLOR_PATTERN.matcher(text);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, Matcher.quoteReplacement(toLegacyHex(matcher.group(1))));
        }
        matcher.appendTail(sb);
        return ChatColor.translateAlternateColorCodes('&', sb.toString());
    }

    /** Builds the vanilla legacy hex-color sequence (§x§R§R§G§G§B§B) for a 6-digit hex string. */
    private static String toLegacyHex(String hex) {
        StringBuilder sb = new StringBuilder().append(ChatColor.COLOR_CHAR).append('x');
        for (char c : hex.toCharArray()) sb.append(ChatColor.COLOR_CHAR).append(c);
        return sb.toString();
    }

    private static String defaultEffectFormat(MachineType type) {
        return switch (type) {
            case FURNACE -> "เผาทีละ: {rate} ชิ้น";
            case HOPPER -> "ส่งทีละ: {rate} ชิ้น";
            case DISPENSER -> "ยิงทีละ: {rate} ชิ้น";
            case DROPPER -> "ดรอปทีละ: {rate} ชิ้น";
            case CRAFTER -> "คราฟทีละ: {rate} ครั้ง";
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

    /** The three sneak+right-click confirm flows a machine can use - see gui.style in config.yml. */
    public enum ConfirmStyle { INVENTORY, ACTIONBAR, DIALOG }

    /** Which confirm flow sneak+right-click should use. Unrecognized/missing values fall back to INVENTORY (the original behavior). */
    public ConfirmStyle getConfirmStyle() {
        String raw = plugin.getConfig().getString("gui.style", "inventory");
        if ("actionbar".equalsIgnoreCase(raw)) return ConfirmStyle.ACTIONBAR;
        if ("dialog".equalsIgnoreCase(raw)) return ConfirmStyle.DIALOG;
        return ConfirmStyle.INVENTORY;
    }

    /** Action-bar text shown on the first sneak+right-click in actionbar style - a second one within the timeout confirms. Same placeholders as everything else. */
    public String getActionBarPromptFormat() {
        return plugin.getConfig().getString("gui.actionbar.prompt-format",
                "&e→ &fคลิกขวาอีกครั้งเพื่ออัปเกรดเป็น: &a{level-roman} &fในราคา: &6{price}");
    }

    /** How long (seconds) a pending actionbar confirmation stays valid before a second click starts a fresh prompt instead. */
    public int getActionBarTimeoutSeconds() {
        return Math.max(1, plugin.getConfig().getInt("gui.actionbar.timeout-seconds", 6));
    }

    /** Title of the native Dialog screen (style: "dialog") - a real client-rendered box with buttons, no resource pack needed. */
    public String getDialogTitleFormat() {
        return plugin.getConfig().getString("gui.dialog.title-format", "&8อัพเกรด {name}");
    }

    /** Body lines of the Dialog screen, joined with line breaks into one compact paragraph (see UpgradeConfirmDialog). */
    public List<String> getDialogBodyFormat() {
        return getStringListOrDefault("gui.dialog.body", List.of(
                "&7ระดับ: &f{current-roman} &7→ &a{level-roman}&7/{max-roman}",
                "&7ผล: &f{effect}",
                "&7ราคา: &f{price}"));
    }

    public String getDialogConfirmLabel() {
        return plugin.getConfig().getString("gui.dialog.confirm-label", "&a&lยืนยันอัพเกรด\n&7Lv.{level-roman} - {price}");
    }

    public String getDialogCancelLabel() {
        return plugin.getConfig().getString("gui.dialog.cancel-label", "&c&lยกเลิก");
    }

    /** How the confirm/cancel buttons are arranged on the Dialog screen - see gui.dialog.button-layout in config.yml. */
    public enum DialogButtonLayout { SIDE_BY_SIDE, STACKED, SEPARATED }

    /** Unrecognized/missing values fall back to SEPARATED (confirm in the main area, cancel set apart below it). */
    public DialogButtonLayout getDialogButtonLayout() {
        String raw = plugin.getConfig().getString("gui.dialog.button-layout", "separated");
        if ("side-by-side".equalsIgnoreCase(raw)) return DialogButtonLayout.SIDE_BY_SIDE;
        if ("stacked".equalsIgnoreCase(raw)) return DialogButtonLayout.STACKED;
        return DialogButtonLayout.SEPARATED;
    }

    /** Pixel width of the confirm button - widen this in config.yml if confirm-label gets clipped. */
    public int getDialogConfirmButtonWidth() {
        return Math.max(1, plugin.getConfig().getInt("gui.dialog.confirm-button-width", 200));
    }

    /** Pixel width of the cancel button - widen this in config.yml if cancel-label gets clipped. Independent of the confirm button's width. */
    public int getDialogCancelButtonWidth() {
        return Math.max(1, plugin.getConfig().getInt("gui.dialog.cancel-button-width", 200));
    }

    /**
     * Substitutes {name}/{level}/{current}/{max}/{rate}/{effect}/{price} (plain Arabic numbers)
     * plus {level-roman}/{current-roman}/{max-roman} (Roman numeral versions, e.g. "IV" instead
     * of "4" - level 0 has no Roman numeral so it prints as "0") into the template, then applies
     * &-color codes (including hex colors written as &#RRGGBB) to the whole result - done in
     * this order, not before, so colors embedded in a configured display-name (via {name}) get
     * translated too, not just colors in the surrounding template. {rate} is the same bare
     * number {effect}'s wording is built around (via effect-format) - handy when a template
     * wants just the number without effect-format's surrounding text.
     */
    public String formatText(String template, MachineType type, Material material, int current, int target, int max, String effect, String price) {
        String result = template
                .replace("{name}", getDisplayName(type, material, target))
                .replace("{level-roman}", toRoman(target))
                .replace("{current-roman}", toRoman(current))
                .replace("{max-roman}", toRoman(max))
                .replace("{level}", String.valueOf(target))
                .replace("{current}", String.valueOf(current))
                .replace("{max}", String.valueOf(max))
                .replace("{rate}", String.valueOf(getRate(type, target)))
                .replace("{effect}", effect == null ? "" : effect)
                .replace("{price}", price == null ? "" : price);
        return translateColors(result);
    }

    private static final int[] ROMAN_VALUES = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private static final String[] ROMAN_SYMBOLS = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

    /** Converts to a Roman numeral (1-3999). Roman numerals have no zero/negative, so those print as-is. */
    public static String toRoman(int number) {
        if (number <= 0) return String.valueOf(number);
        StringBuilder sb = new StringBuilder();
        int remaining = number;
        for (int i = 0; i < ROMAN_VALUES.length; i++) {
            while (remaining >= ROMAN_VALUES[i]) {
                remaining -= ROMAN_VALUES[i];
                sb.append(ROMAN_SYMBOLS[i]);
            }
        }
        return sb.toString();
    }

    /** Same as {@link #formatText} but applied to every line of a lore list. */
    public List<String> formatTextList(List<String> templates, MachineType type, Material material, int current, int target, int max, String effect, String price) {
        List<String> result = new ArrayList<>(templates.size());
        for (String line : templates) result.add(formatText(line, type, material, current, target, max, effect, price));
        return result;
    }

    public String getGuiTitleFormat() {
        return plugin.getConfig().getString("gui.title-format", "&8{name} &f{effect} &7- {price}");
    }

    public String getGuiInfoNameFormat() {
        return plugin.getConfig().getString("gui.info-item.name-format", "&b&l{name} &f→ &bLv.{level-roman}");
    }

    public List<String> getGuiInfoLoreFormat() {
        return getStringListOrDefault("gui.info-item.lore", List.of(
                "&7ระดับ: &f{current-roman} &7→ &a{level-roman}&7/{max-roman}", "&7ผล: &f{effect}", "", "&7ราคา: &f{price}"));
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

    /**
     * Text used for {price} when there's nothing to pay. Colorized on its own (rather than
     * forcing a color inside the {price} value itself) so whatever color precedes {price} in a
     * template only applies when there IS a price - free stays its own distinct color.
     */
    public String getPriceFreeText() {
        return translateColors(plugin.getConfig().getString("gui.price-free", "&aฟรี"));
    }

    /** Text placed between the money and item parts of {price} when both are charged. */
    public String getPriceSeparator() {
        return translateColors(plugin.getConfig().getString("gui.price-separator", "&7 + "));
    }

    /** Builds the {price} value (money + item cost summary, or the free text) - shared by every confirm UI. */
    public String formatPrice(boolean chargeMoney, boolean chargeItem, double moneyPrice, int itemAmount, Material itemMaterial, EconomyHook economy) {
        if (!chargeMoney && !chargeItem) return getPriceFreeText();
        StringBuilder summary = new StringBuilder();
        if (chargeMoney) summary.append(economy.format(moneyPrice));
        if (chargeMoney && chargeItem) summary.append(getPriceSeparator());
        if (chargeItem) summary.append(itemAmount).append("x ").append(itemMaterial.name());
        return summary.toString();
    }

    /** Name format for the item dropped when breaking an upgraded block (also used when applying it back on place). */
    public String getItemNameFormat() {
        return plugin.getConfig().getString("gui.item-name-format", "&b{name} &f→ &bLv.{level-roman}");
    }

    public List<String> getItemLoreFormat() {
        return getStringListOrDefault("gui.item-lore-format", List.of("&7{effect}"));
    }

    /** The fully formatted name for a level, built from item-name-format - shared by the dropped item and the block's own custom name. */
    public String getItemDisplayName(MachineType type, Material material, int level) {
        String effect = describeEffect(type, level);
        int max = getMaxLevel(type);
        return formatText(getItemNameFormat(), type, material, level, level, max, effect, "");
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
