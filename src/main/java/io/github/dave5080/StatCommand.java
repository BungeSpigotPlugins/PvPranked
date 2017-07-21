package io.github.dave5080;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Dave5080 on 19/07/2017.
 */
public class StatCommand implements CommandExecutor{
    public DataManager dm = DataManager.getManager();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args != null && args.length > 0){
            Player p = Bukkit.getPlayer(args[0]);
            if(p != null && p.isOnline()){
                sender.sendMessage(Customizations.SOME_STATS.toString().replace("%player%",args[0]));
                sender.sendMessage(compileStats(p));
                return true;
            }
            sender.sendMessage(Customizations.PLAYER_NOT_ONLINE.toString().replace("%player%",args[0]));
            return true;
        }
        if(sender instanceof Player){
            sender.sendMessage(Customizations.YOUR_STATS.toString());
            sender.sendMessage(compileStats((Player)sender));
            return true;
        }
        help(sender);
        return true;
    }

    private String[] compileStats(Player p){
        String[] stats = new String[6];
        stats[0] = Customizations.DELIMITER.toString();
        stats[1] = Customizations.STATS_TAG.toString();
        stats[2] = Customizations.STATS_STRING.toString().replace("%kills%",dm.getKills(p).toString()).replace("%deaths%",dm.getKills(p).toString()).replace("%kd%",dm.getDeaths(p)==0?"0":Double.valueOf(dm.getKills(p)/dm.getDeaths(p)).toString());
        stats[3] = Customizations.POINTS_STRING.toString().replace("%points%",dm.getPvPoints(p).toString());
        stats[4] = PvPranked.getInstance().isUsingPex()?Customizations.RANK_STRING.toString().replace("%rank%", getFroupPrefix(p)):"";
        stats[5] = Customizations.DELIMITER.toString();
        return stats;
    }

    private String getFroupPrefix(Player p){
        List<PermissionGroup> groups = new ArrayList<>(PermissionsEx.getPermissionManager().getGroupList());
        groups.sort(new Comparator<PermissionGroup>() {
            @Override
            public int compare(PermissionGroup o1, PermissionGroup o2) {
                return o1.getOptionInteger("pvp","*",700) > o2.getOptionInteger("pvp","*",700) ? 1 : o1.getOptionInteger("pvp","*",700)<o2.getOptionInteger("pvp","*",700) ? -1 : 0;
            }
        });
        Collections.reverse(groups);
        int point = dm.getPvPoints(p);
        for(PermissionGroup g : groups){
            if(point >= g.getOptionInteger("pvp","*",700))
                return g.getPrefix();
        }
        String pex = PermissionsEx.getPermissionManager().getDefaultGroups("*").get(0).getPrefix();
        return ChatColor.translateAlternateColorCodes('&',pex);
    }

    private static void help(CommandSender sender){

    }
}
