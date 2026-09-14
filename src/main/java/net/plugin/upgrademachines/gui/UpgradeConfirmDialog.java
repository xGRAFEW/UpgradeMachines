package net.plugin.upgrademachines.gui;

import io.papermc.paper.dialog.Dialog;
import io.papermc.paper.registry.data.dialog.ActionButton;
import io.papermc.paper.registry.data.dialog.DialogBase;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.type.DialogType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickCallback;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.plugin.upgrademachines.util.EconomyHook;
import net.plugin.upgrademachines.util.MachineType;
import net.plugin.upgrademachines.util.UpgradeExecutor;
import net.plugin.upgrademachines.util.UpgradeManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Builds/shows the native Minecraft "Dialog" screen (Player#showDialog, part of vanilla's
 * Dialogs feature) as a third confirm-UI style alongside the inventory GUI and action-bar -
 * see gui.style: "dialog" in config.yml. Unlike those two, this is a real client-rendered
 * screen with a title, message, and Confirm/Cancel buttons: no resource pack needed for a
 * proper-looking box, and the buttons are full-size clickable UI elements rather than small
 * inventory slots or a two-tap-the-block trick - the closest of the three to a native mobile UI.
 */
public final class UpgradeConfirmDialog {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacySection();

    private UpgradeConfirmDialog() {}

    public static void show(Player player, Block block, MachineType type, int current, int target, int max,
                             UpgradeManager manager, EconomyHook economy) {
        double moneyPrice = manager.getMoneyPrice(type, target);
        Material itemMaterial = manager.getItemPriceMaterial(type);
        int itemAmount = manager.getItemPriceAmount(type, target);
        boolean chargeMoney = moneyPrice > 0 && economy.isEnabled();
        boolean chargeItem = itemMaterial != null && itemAmount > 0;

        Material blockMaterial = block.getType();
        String effect = manager.describeEffect(type, target);
        String price = manager.formatPrice(chargeMoney, chargeItem, moneyPrice, itemAmount, itemMaterial, economy);

        Component title = legacy(manager.formatText(manager.getDialogTitleFormat(), type, blockMaterial, current, target, max, effect, price));
        Component confirmLabel = legacy(manager.formatText(manager.getDialogConfirmLabel(), type, blockMaterial, current, target, max, effect, price));
        Component cancelLabel = legacy(manager.formatText(manager.getDialogCancelLabel(), type, blockMaterial, current, target, max, effect, price));

        // One PlainMessageDialogBody per line renders with a large gap between each - joining
        // into a single multi-line message (the client treats \n as a normal line break here,
        // same as in signs/books) keeps the lines close together like a paragraph instead.
        List<String> bodyLines = manager.formatTextList(manager.getDialogBodyFormat(), type, blockMaterial, current, target, max, effect, price);
        List<DialogBody> body = List.of(DialogBody.plainMessage(legacy(String.join("\n", bodyLines))));

        // Single-use: once the player clicks either button the callback can't fire again for this dialog instance.
        ClickCallback.Options singleUse = ClickCallback.Options.builder().uses(1).build();

        // Wider than the default button width so the extra summary line (added below) fits.
        ActionButton confirmButton = ActionButton.builder(confirmLabel)
                .width(300)
                .action(DialogAction.customClick((view, audience) -> UpgradeExecutor.upgrade(player, block, type, manager, economy), singleUse))
                .build();
        ActionButton cancelButton = ActionButton.builder(cancelLabel)
                .width(300)
                .action(DialogAction.customClick((view, audience) -> player.sendMessage("§7ยกเลิกการอัพเกรดแล้ว"), singleUse))
                .build();

        DialogBase base = DialogBase.builder(title)
                .body(body)
                .canCloseWithEscape(true)
                .build();

        // Confirm is the sole entry in the main action grid (stays where it was); cancel goes in
        // exitAction, which Minecraft renders as a separate button below the main grid instead
        // of stacked right under it - matches what was asked for: confirm in place, cancel moved
        // further down on its own.
        Dialog dialog = Dialog.create(factory -> factory.empty()
                .base(base)
                .type(DialogType.multiAction(List.of(confirmButton)).exitAction(cancelButton).columns(1).build()));

        player.showDialog(dialog);
    }

    private static Component legacy(String text) {
        return LEGACY.deserialize(text);
    }
}
