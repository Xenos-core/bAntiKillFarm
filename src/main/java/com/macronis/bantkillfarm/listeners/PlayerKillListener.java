package com.macronis.bantkillfarm.listeners;

import com.macronis.bantkillfarm.BAntiKillFarm;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerKillListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer == null) return;
        if (killer.equals(victim)) return;
        if (killer.hasPermission("bantkillfarm.bypass")) return;

        CompletableFuture.runAsync(
            () -> BAntiKillFarm.getInstance().getKillTracker().recordKill(killer.getUniqueId(), victim.getUniqueId()),
            BAntiKillFarm.getAsyncExecutor()
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        CompletableFuture.runAsync(
            () -> BAntiKillFarm.getInstance().getKillTracker().removePlayer(event.getPlayer().getUniqueId()),
            BAntiKillFarm.getAsyncExecutor()
        );
    }
}