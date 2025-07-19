package com.computerwhz.mcreportinggui.gui;

import com.computerwhz.mcreportinggui.MCReportingGUI;
import com.computerwhz.mcreportinggui.ReportCooldownHandler;
import com.computerwhz.mcreportinggui.ReportHandler;
import com.computerwhz.mcreportinggui.ReportType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.function.Consumer;

public class GUIManager implements Listener {

    private final Set<UUID> waitingForChat = new HashSet<>();
    private final Map<UUID, Consumer<String>> chatHandlers = new HashMap<>();
    public static final ReportCooldownHandler cooldownHandler = new ReportCooldownHandler();


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!(event.getInventory().getHolder() instanceof ReportingGUIInventoryHolder)) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (event.getCurrentItem() != null){
            if (event.getInventory().getSize() == 36){
                if (clickedItem.getType() == Material.BARRIER){
                    CloseGUI(player);
                } else if (clickedItem.getType() == Material.PLAYER_HEAD) {
                    OpenPlayerChooseGUI(player, 1);
                } else if (clickedItem.getType() == Material.BEDROCK) {
                    StartBugReport(player);
                } else if (clickedItem.getType() == Material.LECTERN) {
                    StartOtherReport(player);
                }
            } else if (event.getInventory().getSize() == 54) {
                if (event.getCurrentItem().getType() == Material.PLAYER_HEAD){
                    String targetName = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
                    Player reported = Bukkit.getPlayerExact(targetName);

                    StartPlayerReport((Player) event.getWhoClicked(), reported);
                } else if (event.getCurrentItem().getType() == Material.BARRIER) {
                    CloseGUI(player);
                }
            }
        }


    }

    @EventHandler
    public void OnMessageSend(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!waitingForChat.contains(uuid)) return;

        event.setCancelled(true); // Prevent the message from showing publicly

        waitingForChat.remove(uuid);
        Consumer<String> handler = chatHandlers.remove(uuid);

        if (handler != null) {
            handler.accept(event.getMessage());
        }
    }

    public void OpenReportGUI(Player player){
        if (!cooldownHandler.isOnCooldown(player.getUniqueId())){
            Inventory reportGUI = Bukkit.createInventory(new ReportingGUIInventoryHolder(), 36, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Report");
            ItemStack playerReportItem = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta playerReportMeta = playerReportItem.getItemMeta();
            Objects.requireNonNull(playerReportMeta).setDisplayName(ChatColor.RED + "Report Player");
            List<String> playerReportLore = new ArrayList<>();
            playerReportLore.add("Report a player for breaking the rules");
            playerReportMeta.setLore(playerReportLore);
            playerReportItem.setItemMeta(playerReportMeta);

            ItemStack bugReportItem = new ItemStack(Material.BEDROCK);
            ItemMeta bugReportMeta = bugReportItem.getItemMeta();
            Objects.requireNonNull(bugReportMeta).setDisplayName(ChatColor.GREEN + "Report Bug");
            List<String> bugReportLore = new ArrayList<>();
            bugReportLore.add("Report a server bug or exploit");
            bugReportLore.add("(Include as much detail as possible)");
            bugReportMeta.setLore(bugReportLore);
            bugReportItem.setItemMeta(bugReportMeta);

            ItemStack otherReportItem = new ItemStack(Material.LECTERN);
            ItemMeta otherReportMeta = otherReportItem.getItemMeta();
            Objects.requireNonNull(otherReportMeta).setDisplayName(ChatColor.RESET + "Report Other Issue");
            List<String> otherReportLore = new ArrayList<>();
            otherReportLore.add("Report another issue");
            otherReportLore.add("Report something outside of the other categories");
            otherReportMeta.setLore(otherReportLore);
            otherReportItem.setItemMeta(otherReportMeta);

            ItemStack exitItem = new ItemStack(Material.BARRIER);
            ItemMeta exitMeta = exitItem.getItemMeta();
            Objects.requireNonNull(exitMeta).setDisplayName(ChatColor.RED.toString() + ChatColor.BOLD + "Exit");
            List<String> exitLore = new ArrayList<>();
            exitLore.add("Exit the report GUI");
            exitMeta.setLore(exitLore);
            exitItem.setItemMeta(exitMeta);

            reportGUI.setItem(11, playerReportItem);
            reportGUI.setItem(13, bugReportItem);
            reportGUI.setItem(15, otherReportItem);
            reportGUI.setItem(35, exitItem);
            player.openInventory(reportGUI);
        }
        else {
            player.sendMessage(ChatColor.RED + "You must wait" + cooldownHandler.getTimeLeft(player.getUniqueId()) + "seconds to file another report");
        }

    }

    public void OpenPlayerChooseGUI(Player viewer, int page) {
        if (MCReportingGUI.getInstance().config.getBoolean("reporttypes.playerreport.enabled")){
            int itemsPerPage = 36;
            int startIndex = (page - 1) * itemsPerPage;

            List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

            int totalPages = (int) Math.ceil(players.size() / (double) itemsPerPage);

            Inventory gui = Bukkit.createInventory(
                    new ReportingGUIInventoryHolder(),
                    54,
                    ChatColor.DARK_RED + "" + ChatColor.BOLD + "Report Player - Page " + page
            );

            for (int i = 0; i < itemsPerPage; i++) {
                int index = startIndex + i;
                if (index >= players.size()) break;

                Player target = players.get(index);

                ItemStack head = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta meta = (SkullMeta) head.getItemMeta();
                if (meta != null) {
                    meta.setOwningPlayer(target);
                    meta.setDisplayName(ChatColor.GREEN + target.getName());
                    head.setItemMeta(meta);
                }

                gui.setItem(i, head);
            }

            // Navigation buttons
            if (page > 1) {
                ItemStack prev = new ItemStack(Material.ARROW);
                ItemMeta prevMeta = prev.getItemMeta();
                prevMeta.setDisplayName(ChatColor.YELLOW + "Previous Page");
                prev.setItemMeta(prevMeta);
                gui.setItem(45, prev);
            }

            if (page < totalPages) {
                ItemStack next = new ItemStack(Material.ARROW);
                ItemMeta nextMeta = next.getItemMeta();
                nextMeta.setDisplayName(ChatColor.YELLOW + "Next Page");
                next.setItemMeta(nextMeta);
                gui.setItem(53, next);
            }

            viewer.openInventory(gui);
        }
        else {
            viewer.closeInventory();
            viewer.sendMessage(ChatColor.RED + "Sorry player reporting is disabled");
        }

    }

    public void StartPlayerReport(Player reporter, Player reported) {
        if (MCReportingGUI.getInstance().config.getBoolean("reporttypes.playerreport.enabled")) {
            if (!cooldownHandler.isOnCooldown(reporter.getUniqueId())){
                if (!reported.hasPermission("MCReportingGUI.admin")){
                    waitingForChat.add(reporter.getUniqueId());
                    chatHandlers.put(reporter.getUniqueId(), (String message) -> {
                        // This runs when the reporter sends their next message
                        if (Objects.equals(message, "cancel")){
                            reporter.sendMessage(ChatColor.RED + "Your report was canceled");
                        }
                        else {
                            new ReportHandler().Report(reporter, reported, message, ReportType.playerReport);
                            reporter.sendMessage(ChatColor.GREEN + "Your report has been submitted.");
                            cooldownHandler.setCooldown(reporter.getUniqueId());
                        }
                    });
                    reporter.closeInventory();
                    reporter.sendMessage(ChatColor.YELLOW + "You are reporting " + reported.getDisplayName() + "\nPlease type your report in chat.\nType cancel to cancel" + ChatColor.RED + "\nWARNING making a false report is against the rules and may result in consequences" );
                }
                else {
                    reporter.closeInventory();
                    reporter.sendMessage(ChatColor.RED + "You cannot report this user");

                }

            }
            else {
                reporter.sendMessage(ChatColor.RED + "You must wait" + cooldownHandler.getTimeLeft(reporter.getUniqueId()) + "seconds to file another report");
            }
        }

        else {
            reporter.sendMessage(ChatColor.RED + "Sorry player reporting is disabled");
        }

    }


    public void StartBugReport(Player reporter){
        if (MCReportingGUI.getInstance().config.getBoolean("reporttypes.bugreport.enabled")){
            if (!cooldownHandler.isOnCooldown(reporter.getUniqueId())) {
                waitingForChat.add(reporter.getUniqueId());
                chatHandlers.put(reporter.getUniqueId(), (String message) -> {
                    // This runs when the reporter sends their next message
                    if (Objects.equals(message, "cancel")) {
                        reporter.sendMessage(ChatColor.RED + "Your report was canceled");
                    } else {
                        new ReportHandler().Report(reporter, null, message, ReportType.bugReport);
                        reporter.sendMessage(ChatColor.GREEN + "Your report has been submitted.");
                        cooldownHandler.setCooldown(reporter.getUniqueId());
                    }
                });
                reporter.closeInventory();
                reporter.sendMessage(ChatColor.YELLOW + "Please describe the bug in chat. Be as detailed as possible\nType cancel to cancel");
            }
            else {
                reporter.sendMessage(ChatColor.RED + "You must wait " + cooldownHandler.getTimeLeft(reporter.getUniqueId()) + " seconds to file another report");
            }
        }
        else {
            reporter.sendMessage(ChatColor.RED + "Sorry bug reporting is disabled");
        }

    }

    public void StartOtherReport(Player reporter){
        if (MCReportingGUI.getInstance().config.getBoolean("reporttypes.otherreport.enabled")){
            if (!cooldownHandler.isOnCooldown(reporter.getUniqueId())){
                waitingForChat.add(reporter.getUniqueId());
                chatHandlers.put(reporter.getUniqueId(), (String message) -> {
                    // This runs when the reporter sends their next message
                    if (Objects.equals(message, "cancel")){
                        reporter.sendMessage(ChatColor.RED + "Your report was canceled");
                    }
                    else {
                        new ReportHandler().Report(reporter, null, message, ReportType.otherReport);
                        reporter.sendMessage(ChatColor.GREEN + "Your report has been submitted.");
                        cooldownHandler.setCooldown(reporter.getUniqueId());
                    }
                });
                reporter.closeInventory();
                reporter.sendMessage(ChatColor.YELLOW + "Please explain your other issue\nType cancel to cancel");
            }
            else {
                reporter.sendMessage(ChatColor.RED + "You must wait" + cooldownHandler.getTimeLeft(reporter.getUniqueId()) + "seconds to file another report");
            }
        }
        else{
            reporter.sendMessage(ChatColor.RED + "Sorry Other reporting is disabled");
        }

    }

    public void CloseGUI(Player player){
        player.closeInventory();
    }

}
