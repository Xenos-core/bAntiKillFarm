package com.macronis.bantkillfarm;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CommandHandler {

    private final BAntiKillFarm plugin;
    private final MiniMessage miniMessage;
    private final DateTimeFormatter formatter;

    public CommandHandler(BAntiKillFarm plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    }

    public void executeWarning(UUID attacker, UUID victim, int killCount, int timeWindow) {
        String attackerName = getPlayerName(attacker);
        String victimName = getPlayerName(victim);

        logWarning(attackerName, victimName, killCount, timeWindow);

        TagResolver resolver = TagResolver.resolver(
            Placeholder.parsed("attacker", attackerName),
            Placeholder.parsed("victim", victimName),
            Placeholder.parsed("kill_count", String.valueOf(killCount)),
            Placeholder.parsed("time_window", String.valueOf(timeWindow))
        );

        for (String command : plugin.getConfigManager().getWarningCommands()) {
            executeCommand(command, resolver);
        }
    }

    public void executePunishment(UUID attacker, UUID victim, int killCount, int timeWindow) {
        String attackerName = getPlayerName(attacker);
        String victimName = getPlayerName(victim);

        logPunishment(attackerName, victimName, killCount, timeWindow);

        TagResolver resolver = TagResolver.resolver(
            Placeholder.parsed("attacker", attackerName),
            Placeholder.parsed("victim", victimName),
            Placeholder.parsed("kill_count", String.valueOf(killCount)),
            Placeholder.parsed("time_window", String.valueOf(timeWindow))
        );

        for (String command : plugin.getConfigManager().getPunishmentCommands()) {
            executeCommand(command, resolver);
        }
    }

    private String getPlayerName(UUID uuid) {
        var player = Bukkit.getPlayer(uuid);
        return player != null ? player.getName() : uuid.toString();
    }

    private void executeCommand(String command, TagResolver resolver) {
        if (command.startsWith("say ")) {
            String message = command.substring(4);
            Component component = miniMessage.deserialize(message, resolver);
            Bukkit.getScheduler().callSyncMethod(plugin, () -> {
                Bukkit.getServer().broadcast(component);
                return null;
            });
        } else {
            String processed = processPlaceholders(command, resolver);
            String actualCommand = processed.startsWith("/") ? processed.substring(1) : processed;
            Bukkit.getScheduler().callSyncMethod(plugin, () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), actualCommand);
                return null;
            });
        }
    }

    private String processPlaceholders(String text, TagResolver resolver) {
        return text
            .replace("{attacker}", "<attacker>")
            .replace("{victim}", "<victim>")
            .replace("{kill_count}", "<kill_count>")
            .replace("{time_window}", "<time_window>");
    }

    private void logWarning(String attacker, String victim, int killCount, int timeWindow) {
        if (!plugin.getConfigManager().isLoggingEnabled()) return;

        CompletableFuture.runAsync(() -> {
            String timestamp = LocalDateTime.now().format(formatter);
            String logEntry = String.format("[%s] [WARNING] %s killed %s (%d kills in %d min)",
                timestamp, attacker, victim, killCount, timeWindow);

            plugin.getLogger().warning(logEntry);
            writeToFile(logEntry);
        }, BAntiKillFarm.getAsyncExecutor());
    }

    private void logPunishment(String attacker, String victim, int killCount, int timeWindow) {
        if (!plugin.getConfigManager().isLoggingEnabled()) return;

        CompletableFuture.runAsync(() -> {
            String timestamp = LocalDateTime.now().format(formatter);
            String logEntry = String.format("[%s] [PUNISHMENT] %s punished for killing %s (%d kills in %d min)",
                timestamp, attacker, victim, killCount, timeWindow);

            plugin.getLogger().info(logEntry);
            writeToFile(logEntry);
        }, BAntiKillFarm.getAsyncExecutor());
    }

    private void writeToFile(String logEntry) {
        try {
            File logFile = new File(plugin.getDataFolder().getParentFile(), plugin.getConfigManager().getLogFile());
            logFile.getParentFile().mkdirs();
            try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
                writer.println(logEntry);
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Log write failed: " + e.getMessage());
        }
    }
}