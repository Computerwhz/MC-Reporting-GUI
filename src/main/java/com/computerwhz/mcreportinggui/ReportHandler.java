package com.computerwhz.mcreportinggui;

import com.eduardomcb.discord.webhook.WebhookClient;
import com.eduardomcb.discord.webhook.WebhookManager;
import com.eduardomcb.discord.webhook.models.Author;
import com.eduardomcb.discord.webhook.models.Embed;
import com.eduardomcb.discord.webhook.models.Field;
import com.eduardomcb.discord.webhook.models.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.bukkit.inventory.meta.SkullMeta;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ReportHandler {
    public void Report(Player reporter, Player reported, String message, ReportType reportType) {

        String webHookURL = MCReportingGUI.getInstance().config.getString("discordwebhook.webhookURL");
        WebhookManager webhookManager = new WebhookManager();
        webhookManager.setChannelUrl(webHookURL);


        webhookManager.setListener(new WebhookClient.Callback() {
            @Override
            public void onSuccess(String response) {
                Bukkit.getLogger().info("Message sent successfully");
            }

            @Override
            public void onFailure(int statusCode, String errorMessage) {
                Bukkit.getLogger().severe("Code: " + statusCode + " error: " + errorMessage);
            }

        });

        if (reportType == ReportType.playerReport) {
                // Send discord report
                if (reported != null) {
                    LocalDateTime timeNow = LocalDateTime.now();
                    String formatedTimeNow = timeNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    List<Field> fields = new ArrayList<>();
                    fields.add(new Field("Report Created by", reporter.getDisplayName(), false));
                    fields.add(new Field("Reported PLayer", reported.getDisplayName(), false));

                    Embed reportEmbed = new Embed();
                    reportEmbed.setColor(0xff0000);
                    reportEmbed.setTitle("Player Reported");
                    reportEmbed.setFields(fields.toArray(new Field[0]));
                    reportEmbed.setDescription(message);
                    reportEmbed.setTimestamp(formatedTimeNow);

                    webhookManager.setMessage(new Message().setUsername("Player Report"));
                    webhookManager.setEmbeds(new Embed[] { reportEmbed });
                    webhookManager.exec();
                } else {
                    Bukkit.getLogger().severe("Reported player Null");
                }

                Bukkit.getLogger().info("\u001B[31m" + "PLAYER REPORTED\n" + reported.getDisplayName() + " was reported by " + reporter.getDisplayName() + "\n" + message + "\u001B[0m");


        } else if (reportType == ReportType.bugReport) {
            LocalDateTime timeNow = LocalDateTime.now();
            String formatedTimeNow = timeNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            List<Field> fields = new ArrayList<>();
            fields.add(new Field("Report Created by", reporter.getDisplayName(), false));

            Embed reportEmbed = new Embed();
            reportEmbed.setColor(0x00ff00);
            reportEmbed.setTitle("Bug Reported");
            reportEmbed.setFields(fields.toArray(new Field[0]));
            reportEmbed.setDescription(message);
            reportEmbed.setTimestamp(formatedTimeNow);

            webhookManager.setMessage(new Message().setUsername("Bug Report"));
            webhookManager.setEmbeds(new Embed[] { reportEmbed });
            webhookManager.exec();

            Bukkit.getLogger().info("\u001B[31m" + "BUG REPORTED\n" + "reported by " + reporter.getDisplayName() + "\n" + message + "\u001B[0m");

        } else if (reportType == ReportType.otherReport) {
            LocalDateTime timeNow = LocalDateTime.now();
            String formatedTimeNow = timeNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            List<Field> fields = new ArrayList<>();
            fields.add(new Field("Report Created by", reporter.getDisplayName(), false));

            Embed reportEmbed = new Embed();
            reportEmbed.setColor(0xffffff);
            reportEmbed.setTitle("Other Report");
            reportEmbed.setFields(fields.toArray(new Field[0]));
            reportEmbed.setDescription(message);
            reportEmbed.setTimestamp(formatedTimeNow);

            webhookManager.setMessage(new Message().setUsername("Other Report"));
            webhookManager.setEmbeds(new Embed[] { reportEmbed });
            webhookManager.exec();

            Bukkit.getLogger().info("\u001B[31m" + "OTHER REPORTED\n" + "reported by " + reporter.getDisplayName() + "\n" + message + "\u001B[0m");
        }
    }
}
