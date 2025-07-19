package com.computerwhz.mcreportinggui;

import com.computerwhz.mcreportinggui.commands.report;
import com.computerwhz.mcreportinggui.gui.GUIManager;
import com.eduardomcb.discord.webhook.WebhookManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class MCReportingGUI extends JavaPlugin {

    private static MCReportingGUI instance;
    public File configFile;
    public FileConfiguration config;


    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        configFile = new File(getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);

        if (!config.getBoolean("enabled")){
            Bukkit.getLogger().info("Plugin Disabled in config.yml");
            getServer().getPluginManager().disablePlugin(this);
        }

        if (config.getBoolean("discordwebhook.enabled")){
            Bukkit.getLogger().info("Discord webhook enabled");
            Bukkit.getLogger().info("Logging to webhook URL " + config.getString("discordwebhook.webhookURL"));
        }
        else {
            Bukkit.getLogger().warning("Discord Webhook disabled");
        }

        getServer().getPluginManager().registerEvents(new GUIManager(), this);
        Objects.requireNonNull(getCommand("report")).setExecutor(new report());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("MC Reporting GUI Shutting down");
    }

    public static MCReportingGUI getInstance(){
        return instance;
    }
}
