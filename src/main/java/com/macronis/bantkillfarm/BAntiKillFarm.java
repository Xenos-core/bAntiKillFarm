package com.macronis.bantkillfarm;

import com.macronis.bantkillfarm.listeners.PlayerKillListener;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class BAntiKillFarm extends JavaPlugin {

    private static BAntiKillFarm instance;
    private static ScheduledExecutorService asyncExecutor;
    private ConfigManager configManager;
    private KillTracker killTracker;
    private CommandHandler commandHandler;
    private static final AtomicInteger startupCounter = new AtomicInteger(0);

    @Override
    public void onEnable() {
        instance = this;
        asyncExecutor = Executors.newScheduledThreadPool(
            Runtime.getRuntime().availableProcessors(),
            r -> {
                Thread t = new Thread(r, "bAntiKillFarm-Async-" + startupCounter.incrementAndGet());
                t.setDaemon(true);
                t.setPriority(Thread.MIN_PRIORITY);
                return t;
            }
        );

        loadPlugin();
        displayStartupBanner();

        getServer().getPluginManager().registerEvents(new PlayerKillListener(), this);

        getCommand("bantkillfarm").setExecutor((sender, command, label, args) -> {
            if (!sender.hasPermission("bantkillfarm.admin")) {
                sender.sendMessage(Component.text("No permission!").color(NamedTextColor.RED));
                return true;
            }
            if (args.length == 0) {
                sender.sendMessage(Component.text("Usage: /bantkillfarm <reload|status>").color(NamedTextColor.RED));
                return true;
            }
            switch (args[0].toLowerCase()) {
                case "reload" -> handleReload(sender);
                case "status" -> handleStatus(sender);
                default -> sender.sendMessage(Component.text("Usage: /bantkillfarm <reload|status>").color(NamedTextColor.RED));
            }
            return true;
        });

        scheduleCleanupTask();
    }

    @Override
    public void onDisable() {
        if (killTracker != null) killTracker.clearAll();
        shutdownExecutor();
        instance = null;
    }

    private void loadPlugin() {
        saveDefaultConfig();
        reloadConfig();
        configManager = new ConfigManager(this);
        configManager.load();
        killTracker = new KillTracker(this);
        commandHandler = new CommandHandler(this);
    }

    private void handleReload(org.bukkit.command.CommandSender sender) {
        CompletableFuture.runAsync(() -> {
            loadPlugin();
            Bukkit.getScheduler().callSyncMethod(this, () -> {
                sender.sendMessage(Component.text("[bAntiKillFarm] Configuration reloaded!").color(NamedTextColor.GREEN));
                return null;
            });
        }, asyncExecutor);
    }

    private void handleStatus(org.bukkit.command.CommandSender sender) {
        CompletableFuture.supplyAsync(() -> killTracker.getStats(), asyncExecutor)
            .thenAccept(stats -> Bukkit.getScheduler().callSyncMethod(this, () -> {
                sender.sendMessage(Component.text("=== bAntiKillFarm Status ===").color(NamedTextColor.GOLD));
                sender.sendMessage(Component.text("Tracked: ").color(NamedTextColor.YELLOW)
                    .append(Component.text(String.valueOf(stats.trackedPlayers())).color(NamedTextColor.WHITE)));
                sender.sendMessage(Component.text("Total Kills: ").color(NamedTextColor.YELLOW)
                    .append(Component.text(String.valueOf(stats.totalKills())).color(NamedTextColor.WHITE)));
                sender.sendMessage(Component.text("Detections: ").color(NamedTextColor.YELLOW)
                    .append(Component.text(String.valueOf(stats.detections())).color(NamedTextColor.WHITE)));
                sender.sendMessage(Component.text("Warnings: ").color(NamedTextColor.YELLOW)
                    .append(Component.text(String.valueOf(stats.warnings())).color(NamedTextColor.WHITE)));
                return null;
            }));
    }

    private void scheduleCleanupTask() {
        int interval = configManager.getCleanupInterval();
        asyncExecutor.scheduleAtFixedRate(() -> {
            try {
                killTracker.cleanup();
            } catch (Exception e) {
                getLogger().warning("Cleanup failed: " + e.getMessage());
            }
        }, interval, interval, TimeUnit.MINUTES);
    }

    private void shutdownExecutor() {
        if (asyncExecutor != null && !asyncExecutor.isShutdown()) {
            asyncExecutor.shutdown();
            try {
                if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    asyncExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                asyncExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void displayStartupBanner() {
        Component[] lines = {
            Component.text("╔══════════════════════════════════════════╗").color(TextColor.fromHexString("#FF6B35")),
            Component.text("║        bAntiKillFarm v1.0.4              ║").color(TextColor.fromHexString("#FF6B35")),
            Component.text("║        By: Macronis                      ║").color(TextColor.fromHexString("#FF6B35")),
            Component.text("║        Status: ENABLED                   ║").color(NamedTextColor.GREEN),
            Component.text("║        Async Mode: ACTIVE                ║").color(NamedTextColor.GREEN),
            Component.text("╚══════════════════════════════════════════╝").color(TextColor.fromHexString("#FF6B35"))
        };

        for (Component line : lines) {
            Bukkit.getConsoleSender().sendMessage(line);
        }

        Bukkit.getConsoleSender().sendMessage(Component.text(" "));
        Bukkit.getConsoleSender().sendMessage(Component.text("  Detection Window: ").color(NamedTextColor.GRAY)
            .append(Component.text(configManager.getTimeWindow() + " minutes").color(NamedTextColor.AQUA)));
        Bukkit.getConsoleSender().sendMessage(Component.text("  Kill Threshold: ").color(NamedTextColor.GRAY)
            .append(Component.text(String.valueOf(configManager.getKillThreshold())).color(NamedTextColor.AQUA)));
        Bukkit.getConsoleSender().sendMessage(Component.text("  Warning Threshold: ").color(NamedTextColor.GRAY)
            .append(Component.text(String.valueOf(configManager.getWarningThreshold())).color(NamedTextColor.AQUA)));
        Bukkit.getConsoleSender().sendMessage(Component.text("  Punishment Cooldown: ").color(NamedTextColor.GRAY)
            .append(Component.text(configManager.getCooldown() + " seconds").color(NamedTextColor.AQUA)));
        Bukkit.getConsoleSender().sendMessage(Component.text(" "));
        Bukkit.getConsoleSender().sendMessage(Component.text("  Plugin loaded successfully!").color(NamedTextColor.GREEN).decorate(TextDecoration.BOLD));
    }

    public static BAntiKillFarm getInstance() { return instance; }
    public static ScheduledExecutorService getAsyncExecutor() { return asyncExecutor; }
    public ConfigManager getConfigManager() { return configManager; }
    public KillTracker getKillTracker() { return killTracker; }
    public CommandHandler getCommandHandler() { return commandHandler; }
}