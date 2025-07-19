package com.computerwhz.mcreportinggui.commands;

import com.computerwhz.mcreportinggui.MCReportingGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.TabExecutor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class reportblacklist implements TabExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (strings.length == 0) {
            commandSender.sendMessage("You must provide arguments!");
            return false; // or true if you handled it
        }

        if (strings[0].equals("add")){
            Player player = Bukkit.getPlayer(strings[1]);
            MCReportingGUI.getInstance().addToBlacklist(player.getUniqueId());
            commandSender.sendMessage(player.getDisplayName() + " was added to the /report blacklist");
        } else if (strings[0].equals("list")) {
            List<UUID> blackListedPlayers = MCReportingGUI.getInstance().listBlacklisted();
            commandSender.sendMessage("List of players blacklisted from using /report");
            for (UUID p : blackListedPlayers){
                Player player = Bukkit.getPlayer(p);
                commandSender.sendMessage(player.getDisplayName());
            }

        } else if (strings[0].equals("remove")) {
            Player player = Bukkit.getPlayer(strings[1]);
            MCReportingGUI.getInstance().removeFromBlacklist(player.getUniqueId());
            commandSender.sendMessage(player.getDisplayName() + " was removed from the /report blacklist");
        }
        return true;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.add("add");
            completions.add("remove");
            completions.add("list");
            return filter(completions, args[0]);
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    completions.add(player.getName());
                }
                return filter(completions, args[1]);
            }

            if (args[0].equalsIgnoreCase("remove")) {
                for (UUID uuid : MCReportingGUI.getInstance().listBlacklisted()) {
                    OfflinePlayer offline = Bukkit.getOfflinePlayer(uuid);
                    completions.add(offline.getName() != null ? offline.getName() : uuid.toString());
                }
                return filter(completions, args[1]);
            }
        }

        return List.of();
    }

    private List<String> filter(List<String> options, String input) {
        return options.stream()
                .filter(s -> s.toLowerCase().startsWith(input.toLowerCase()))
                .toList();
    }






}
