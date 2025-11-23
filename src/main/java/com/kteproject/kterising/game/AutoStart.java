package com.kteproject.kterising.game;

import com.kteproject.kterising.KteRising;
import com.kteproject.kterising.utils.ChatUtil;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

public class AutoStart implements Listener {

    static ScheduledTask countdownTask;
    private static boolean isCountdownStarted;
    private static int countdownSeconds;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (Game.match) return;

        int playerCount = Bukkit.getOnlinePlayers().size();
        if (playerCount >= KteRising.getConfiguration().getInt("autostart-configuration.need-player-count")) {
            startCountdown();
        } else {
            stopCountdown();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (Game.match) return;

        int playerCount = Bukkit.getOnlinePlayers().size();
        if (playerCount >= KteRising.getConfiguration().getInt("autostart-configuration.need-player-count")) {
            startCountdown();
        } else {
            stopCountdown();
        }
    }

    public static void startCountdown() {
        if (isCountdownStarted) return;

        int neededPlayers = KteRising.getConfiguration().getInt("autostart-configuration.need-player-count");
        if (Bukkit.getOnlinePlayers().size() < neededPlayers) return;

        isCountdownStarted = true;
        countdownSeconds = KteRising.getConfiguration().getInt("autostart-configuration.autostart-countdown");

        countdownTask = KteRising.getInstance().getServer().getGlobalRegionScheduler().runAtFixedRate(
                KteRising.getInstance(),
                (ScheduledTask task) -> {
                    int online = Bukkit.getOnlinePlayers().size();
                    if (online < neededPlayers) {
                        stopCountdown();
                        return;
                    }

                    countdownSeconds--;

                    for (Player p : Bukkit.getOnlinePlayers()) {
                        ChatUtil.sendActionBar(
                                p,
                                "action-bar.autostart-countdown",
                                Map.of("seconds", String.valueOf(countdownSeconds)));
                    }

                    if (countdownSeconds <= 0) {
                        stopCountdown();
                        Game.startGame();
                    }
                },
                1,
                20
        );
    }

    public static void stopCountdown() {
        if (countdownTask != null) {
            countdownTask.cancel();
            countdownTask = null;
        }
        isCountdownStarted = false;
    }

}
