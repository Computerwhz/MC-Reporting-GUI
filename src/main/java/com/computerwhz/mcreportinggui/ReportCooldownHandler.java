package com.computerwhz.mcreportinggui;
import java.util.HashMap;
import java.util.UUID;

public class ReportCooldownHandler {

        private final HashMap<UUID, Long> cooldowns = new HashMap<>();
        private final long cooldownTimeSeconds = MCReportingGUI.getInstance().config.getLong("reportcooldowntime"); // 30-second cooldown

        public boolean isOnCooldown(UUID playerUUID) {
            if (!cooldowns.containsKey(playerUUID)) return false;

            long lastUsed = cooldowns.get(playerUUID);
            long secondsPassed = (System.currentTimeMillis() - lastUsed) / 1000;

            return secondsPassed < cooldownTimeSeconds;
        }
        public long getTimeLeft(UUID playerUUID) {
        long lastUsed = cooldowns.getOrDefault(playerUUID, 0L);
        long secondsPassed = (System.currentTimeMillis() - lastUsed) / 1000;
        long timeLeft = cooldownTimeSeconds - secondsPassed;

        // Return at least 1 if still on cooldown
        return isOnCooldown(playerUUID) ? Math.max(1, timeLeft) : 0;
        }


      public void setCooldown(UUID playerUUID) {
            cooldowns.put(playerUUID, System.currentTimeMillis());
        }
}



