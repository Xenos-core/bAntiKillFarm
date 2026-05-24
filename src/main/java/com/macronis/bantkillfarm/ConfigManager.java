package com.macronis.bantkillfarm;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final BAntiKillFarm plugin;
    private FileConfiguration config;

    private int timeWindowMinutes;
    private int killThreshold;
    private int punishmentCooldownSeconds;
    private int warningThreshold;
    private List<String> punishmentCommands;
    private List<String> warningCommands;
    private boolean loggingEnabled;
    private String logFile;
    private int cleanupIntervalMinutes;

    public ConfigManager(BAntiKillFarm plugin) {
        this.plugin = plugin;
    }

    public void load() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        this.timeWindowMinutes = config.getInt("detection.time-window-minutes", 10);
        this.killThreshold = config.getInt("detection.kill-threshold", 5);
        this.punishmentCooldownSeconds = config.getInt("detection.punishment-cooldown-seconds", 60);
        this.warningThreshold = config.getInt("detection.warning-threshold", 3);
        this.punishmentCommands = config.getStringList("commands.punishment");
        this.warningCommands = config.getStringList("commands.warning");
        this.loggingEnabled = config.getBoolean("logging.enabled", true);
        this.logFile = config.getString("logging.file", "logs/bantkillfarm.log");
        this.cleanupIntervalMinutes = config.getInt("performance.cleanup-interval-minutes", 30);

        if (punishmentCommands.isEmpty()) {
            punishmentCommands = new ArrayList<>();
            punishmentCommands.add("kick {attacker}");
        }

        if (warningCommands.isEmpty()) {
            warningCommands = new ArrayList<>();
            warningCommands.add("say <yellow>[AntiKillFarm]</yellow> <red>{attacker}</red> <white>stop kill farming or you will be punished!</white>");
        }
    }

    public int getTimeWindow() { return timeWindowMinutes; }
    public int getKillThreshold() { return killThreshold; }
    public int getCooldown() { return punishmentCooldownSeconds; }
    public int getWarningThreshold() { return warningThreshold; }
    public List<String> getPunishmentCommands() { return punishmentCommands; }
    public List<String> getWarningCommands() { return warningCommands; }
    public boolean isLoggingEnabled() { return loggingEnabled; }
    public String getLogFile() { return logFile; }
    public int getCleanupInterval() { return cleanupIntervalMinutes; }
}