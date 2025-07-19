package com.computerwhz.mcreportinggui.commands;

import com.computerwhz.mcreportinggui.MCReportingGUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class report implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        Player player = (Player) commandSender;

        if (!MCReportingGUI.getInstance().isBlacklisted(player.getUniqueId())){
            MCReportingGUI.getGuiManagerInstance().OpenReportGUI((Player) commandSender);
            return true;
        }
        else {
            Bukkit.getLogger().warning("Blacklisted player " + player.getDisplayName() + " attempted to use /report");
            commandSender.sendMessage("You are currently unable to create a report");
            return true;
        }
    }
}
