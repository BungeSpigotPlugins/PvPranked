package io.github.dave5080.listeners;

import io.github.dave5080.Customizations;
import io.github.dave5080.DataManager;
import io.github.dave5080.PvPranked;
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
    private PvPranked plugin;
    private DataManager dm;

    public onKillPlayer(){
        plugin = PvPranked.getInstance();
        dm = DataManager.getManager();
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerKillPlaer(PlayerDeathEvent event){
        if(event.getEntity().getKiller() instanceof Player){
            Player death = event.getEntity();
            Player killer = death.getKiller();
            int A = dm.getPvPoints(killer);
            int B = dm.getPvPoints(death);
            Integer val = A>B ? function(A,B,Math.min(A,B)) : function(A,B,Math.max(A,B));
            dm.addPvPoint(killer,val);
            dm.subPvPoint(death,val);
            dm.addKills(killer, 1);
            dm.addDeath(death,1);
            death.sendMessage(Customizations.LOSE_POINTS.toString().replace("%player%",killer.getDisplayName()).replace("%points%",val.toString()));
            killer.sendMessage(Customizations.WIN_POINTS.toString().replace("%player%",death.getDisplayName()).replace("%points%",val.toString()));
        }
    }


    private int function(double A, double B, double coeff){
        Double d = 1+((10 + Math.abs(B-A))/Math.exp((Math.abs(B-A)/coeff)));
        return d.intValue();
    }

}
