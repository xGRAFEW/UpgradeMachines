package net.plugin.upgrademachines;

import net.plugin.upgrademachines.command.UpgradeCommand;
import net.plugin.upgrademachines.listener.CrafterUpgradeListener;
import net.plugin.upgrademachines.listener.DispenserUpgradeListener;
import net.plugin.upgrademachines.listener.FurnaceUpgradeListener;
import net.plugin.upgrademachines.listener.HopperUpgradeListener;
import net.plugin.upgrademachines.listener.UpgradeConfirmGuiListener;
import net.plugin.upgrademachines.listener.UpgradeItemPersistenceListener;
import net.plugin.upgrademachines.listener.UpgradeToolListener;
import net.plugin.upgrademachines.util.EconomyHook;
import net.plugin.upgrademachines.util.UpgradeKeys;
import net.plugin.upgrademachines.util.UpgradeManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class UpgradeMachinesPlugin extends JavaPlugin {

    private UpgradeManager upgradeManager;
    private EconomyHook economyHook;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        UpgradeKeys keys = new UpgradeKeys(this);
        this.upgradeManager = new UpgradeManager(this, keys);
        this.economyHook = new EconomyHook(this);

        getServer().getPluginManager().registerEvents(new FurnaceUpgradeListener(this, upgradeManager), this);
        getServer().getPluginManager().registerEvents(new HopperUpgradeListener(this, upgradeManager), this);
        getServer().getPluginManager().registerEvents(new DispenserUpgradeListener(this, upgradeManager), this);
        getServer().getPluginManager().registerEvents(new CrafterUpgradeListener(this, upgradeManager), this);
        getServer().getPluginManager().registerEvents(new UpgradeToolListener(upgradeManager, economyHook), this);
        getServer().getPluginManager().registerEvents(new UpgradeConfirmGuiListener(upgradeManager, economyHook), this);
        getServer().getPluginManager().registerEvents(new UpgradeItemPersistenceListener(keys, upgradeManager), this);

        UpgradeCommand cmd = new UpgradeCommand(this, upgradeManager, economyHook);
        var pluginCommand = getCommand("upgrade");
        if (pluginCommand != null) {
            pluginCommand.setExecutor(cmd);
            pluginCommand.setTabCompleter(cmd);
        }

        getLogger().info("UpgradeMachines เปิดใช้งานแล้ว!");
    }

    @Override
    public void onDisable() {
        getLogger().info("UpgradeMachines ปิดใช้งานแล้ว");
    }

    public UpgradeManager getUpgradeManager() {
        return upgradeManager;
    }
}
