package net.plugin.upgrademachines.gui;

import net.plugin.upgrademachines.util.MachineType;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/** Identifies the sneak+right-click confirm/cancel GUI and carries which block/type it acts on. */
public class UpgradeConfirmHolder implements InventoryHolder {

    private final Block block;
    private final MachineType type;
    private Inventory inventory;

    public UpgradeConfirmHolder(Block block, MachineType type) {
        this.block = block;
        this.type = type;
    }

    void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public Block getBlock() {
        return block;
    }

    public MachineType getType() {
        return type;
    }
}
