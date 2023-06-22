package com.miyuki.autoforge;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AutoForge extends JavaPlugin {
    public static AutoForge plugin;

    @Override
    public void onEnable() {
        plugin = this;
        Bukkit.getPluginManager().registerEvents(new Listeners(),this);

        getLogger().info("Load AutoFurnace Success!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
