package net.plugin.upgrademachines.util;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Thin, null-safe wrapper around Vault's Economy service. If Vault (or an
 * economy plugin behind it, e.g. EssentialsX) isn't installed, every method
 * here just no-ops / reports "sufficient funds" - so configured money
 * prices are effectively skipped rather than ever blocking an upgrade.
 */
public class EconomyHook {

    private Economy economy;

    public EconomyHook(JavaPlugin plugin) {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("ไม่พบ Vault - ราคาอัพเกรดแบบเงินจะถูกข้าม (ฟรี) จนกว่าจะติดตั้ง Vault + ปลั๊กอินระบบเงิน");
            return;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().warning("พบ Vault แต่ยังไม่มีปลั๊กอินระบบเงินลงทะเบียนไว้ - ราคาอัพเกรดแบบเงินจะถูกข้าม (ฟรี)");
            return;
        }
        this.economy = rsp.getProvider();
    }

    public boolean isEnabled() {
        return economy != null;
    }

    public boolean has(OfflinePlayer player, double amount) {
        if (economy == null) return true;
        return economy.has(player, amount);
    }

    public void withdraw(OfflinePlayer player, double amount) {
        if (economy == null) return;
        economy.withdrawPlayer(player, amount);
    }

    public String format(double amount) {
        return economy != null ? economy.format(amount) : String.valueOf(amount);
    }
}
