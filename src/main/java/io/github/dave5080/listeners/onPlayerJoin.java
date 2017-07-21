package io.github.dave5080.listeners;

import io.github.dave5080.DataManager;
import io.github.dave5080.PvPranked;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Created by Dave5080 on 15/07/2017.
 */
public class onPlayerJoin implements Listener {
    private PvPranked plugin;
    private DataManager dm;

    public onPlayerJoin(){
        plugin = PvPranked.getInstance();
        dm = DataManager.getManager();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event){
        if (dm.getPvPoints(event.getPlayer())==null){
            dm.insValue(event.getPlayer().getName());
        }
    }
}
