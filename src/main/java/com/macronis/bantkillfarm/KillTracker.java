package com.macronis.bantkillfarm;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class KillTracker {

    private final BAntiKillFarm plugin;
    private final Map<UUID, PlayerKillData> killData;
    private final AtomicInteger totalKills;
    private final AtomicInteger detections;
    private final AtomicInteger warnings;

    public KillTracker(BAntiKillFarm plugin) {
        this.plugin = plugin;
        this.killData = new ConcurrentHashMap<>();
        this.totalKills = new AtomicInteger(0);
        this.detections = new AtomicInteger(0);
        this.warnings = new AtomicInteger(0);
    }

    public void recordKill(UUID attacker, UUID victim) {
        long now = System.currentTimeMillis();
        long windowMillis = plugin.getConfigManager().getTimeWindow() * 60 * 1000L;

        PlayerKillData data = killData.compute(attacker, (uuid, existingData) -> {
            return existingData == null ? new PlayerKillData(attacker) : existingData;
        });

        data.addKill(now, victim);
        data.cleanOldKills(now, windowMillis);
        totalKills.incrementAndGet();

        int killCount = data.getKillCountInWindow(now, windowMillis);
        int threshold = plugin.getConfigManager().getKillThreshold();
        int warningThreshold = plugin.getConfigManager().getWarningThreshold();

        if (killCount >= threshold && data.canPunish(now, plugin.getConfigManager().getCooldown() * 1000L)) {
            data.setLastPunishTime(now);
            detections.incrementAndGet();

            plugin.getCommandHandler().executePunishment(
                attacker, victim, killCount, plugin.getConfigManager().getTimeWindow()
            );
        } else if (killCount >= warningThreshold && data.canWarn(now, plugin.getConfigManager().getCooldown() * 1000L)) {
            data.setLastWarnTime(now);
            warnings.incrementAndGet();

            plugin.getCommandHandler().executeWarning(
                attacker, victim, killCount, plugin.getConfigManager().getTimeWindow()
            );
        }
    }

    public void removePlayer(UUID playerUuid) {
        PlayerKillData removed = killData.remove(playerUuid);
        if (removed != null) removed.clear();
    }

    public void cleanup() {
        long now = System.currentTimeMillis();
        long windowMillis = plugin.getConfigManager().getTimeWindow() * 60 * 1000L;
        long expiryMillis = windowMillis * 2;

        int removed = 0;
        var iterator = killData.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            PlayerKillData data = entry.getValue();
            data.cleanOldKills(now, windowMillis);
            if (data.isExpired(now, expiryMillis)) {
                data.clear();
                iterator.remove();
                removed++;
            }
        }

        if (removed > 0) {
            plugin.getLogger().info("Cleanup removed " + removed + " inactive player(s)");
        }
    }

    public void clearAll() {
        killData.values().forEach(PlayerKillData::clear);
        killData.clear();
    }

    public Stats getStats() {
        return new Stats(killData.size(), totalKills.get(), detections.get(), warnings.get());
    }

    public record Stats(int trackedPlayers, int totalKills, int detections, int warnings) {}

    public static class PlayerKillData {
        private final UUID playerId;
        private final ConcurrentHashMap<Long, UUID> kills;
        private volatile long lastPunishTime;
        private volatile long lastWarnTime;

        public PlayerKillData(UUID playerId) {
            this.playerId = playerId;
            this.kills = new ConcurrentHashMap<>();
            this.lastPunishTime = 0;
            this.lastWarnTime = 0;
        }

        public void addKill(long timestamp, UUID victim) { kills.put(timestamp, victim); }

        public void cleanOldKills(long now, long windowMillis) {
            long cutoff = now - windowMillis;
            kills.entrySet().removeIf(entry -> entry.getKey() < cutoff);
        }

        public int getKillCountInWindow(long now, long windowMillis) {
            long cutoff = now - windowMillis;
            return (int) kills.keySet().stream().filter(t -> t >= cutoff).count();
        }

        public boolean canPunish(long now, long cooldownMillis) {
            return (now - lastPunishTime) >= cooldownMillis;
        }

        public boolean canWarn(long now, long cooldownMillis) {
            return (now - lastWarnTime) >= cooldownMillis && (now - lastPunishTime) >= cooldownMillis;
        }

        public void setLastPunishTime(long time) { this.lastPunishTime = time; }
        public void setLastWarnTime(long time) { this.lastWarnTime = time; }

        public boolean isExpired(long now, long expiryMillis) {
            if (kills.isEmpty()) return (now - lastPunishTime) >= expiryMillis;
            long oldestKill = kills.keySet().stream().min(Long::compareTo).orElse(now);
            return (now - oldestKill) >= expiryMillis;
        }

        public void clear() { kills.clear(); }
    }
}