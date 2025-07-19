package com.computerwhz.mcreportinggui.commands;

import com.computerwhz.mcreportinggui.gui.GUIManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class report implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        new GUIManager().OpenReportGUI((Player) commandSender);
        return true;
    }
}
