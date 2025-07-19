package com.computerwhz.mcreportinggui;

import com.computerwhz.mcreportinggui.commands.report;
import com.computerwhz.mcreportinggui.commands.reportblacklist;
import com.computerwhz.mcreportinggui.gui.GUIManager;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public final class MCReportingGUI extends JavaPlugin {

    private static MCReportingGUI instance;
    private static GUIManager guiManagerInstance;
    public File configFile;
    public FileConfiguration config;
    public  File blackListFile;
    public FileConfiguration blacklist;


    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        saveDefaultConfig();
        configFile = new File(getDataFolder(), "config.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        blackListFile = new File(getDataFolder(), "blacklist.yml");
        if (!blackListFile.exists()) {
            saveResource("blacklist.yml", false);
        }
        blacklist = YamlConfiguration.loadConfiguration(blackListFile);

        guiManagerInstance = new GUIManager();

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
        reportblacklist reportblacklist = new reportblacklist();
        Objects.requireNonNull(getCommand("reportblacklist")).setExecutor(reportblacklist);
        Objects.requireNonNull(getCommand("reportblacklist")).setTabCompleter(reportblacklist);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("MC Reporting GUI Shutting down");
    }
    public void addToBlacklist(UUID uuid) {
        List<String> list = blacklist.getStringList("blacklist");

        if (!list.contains(uuid.toString())) {
            list.add(uuid.toString());
            blacklist.set("blacklist", list);
            saveBlacklist();
        }
    }

    public void removeFromBlacklist(UUID uuid) {
        List<String> list = blacklist.getStringList("blacklist");

        if (list.contains(uuid.toString())) {
            list.remove(uuid.toString());
            blacklist.set("blacklist", list);
            saveBlacklist();
        }
    }

    public boolean isBlacklisted(UUID uuid) {
        List<String> list = blacklist.getStringList("blacklist");
        return list.contains(uuid.toString());
    }

    public List<UUID> listBlacklisted() {
        List<String> list = blacklist.getStringList("blacklist");
        List<UUID> result = new ArrayList<>();

        for (String uuidString : list) {
            try {
                result.add(UUID.fromString(uuidString));
            } catch (IllegalArgumentException ignored) {
                // Skip malformed UUIDs
            }
        }

        return result;
    }



    public void saveBlacklist() {
        try {
            blacklist.save(new File(getDataFolder(), "blacklist.yml"));
        } catch (IOException e) {
            getLogger().severe("Could not save blacklist.yml" + e.getMessage());;
        }
    }


    public static MCReportingGUI getInstance(){
        return instance;
    }
    public static GUIManager getGuiManagerInstance(){
        return guiManagerInstance;
    }



}
