package io.github.dave5080.listeners;

import io.github.dave5080.DataManager;
import io.github.dave5080.PvPRanked;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * Created by Dave5080 on 15/07/2017.
 */
public class onKillPlayer implements Listener{
    private PvPRanked plugin;
    private DataManager dm;

    public onKillPlayer(){
        plugin = PvPRanked.getInstance();
        dm = DataManager.getManager();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerKillPlaer(PlayerDeathEvent event){
        if(event.getEntity().getKiller() instanceof Player){
            int killer = dm.getPvPoints(event.getEntity().getKiller());
            int death = dm.getPvPoints(event.getEntity());
            Integer pK = function(killer,death);
            Integer dK = function(death,killer);
            dm.subPvPoint(event.getEntity(),dK);
            dm.addPvPoint(event.getEntity().getKiller(),pK);
            event.getEntity().sendMessage(plugin.getPrefix()+ ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("lose-points").replace("%player%",event.getEntity().getKiller().getName()).replace("%point%",dK.toString())));
            event.getEntity().getKiller().sendMessage(plugin.getPrefix()+ ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("win-points").replace("%player%",event.getEntity().getName()).replace("%point%",dK.toString())));
        }
    }

    private int function(double A, double B){
        Double d = (10 + Math.abs(B-A))/Math.exp(A/B);
        return d.intValue();
    }
}
