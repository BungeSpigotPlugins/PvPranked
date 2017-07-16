package io.github.dave5080;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Dave5080 on 15/07/2017.
 */
public class PvPCommand implements CommandExecutor{
    private PvPRanked plugin;
    private DataManager dm;

    public PvPCommand(){
        plugin = PvPRanked.getInstance();
        dm = DataManager.getManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player p;

        if(args != null && args.length>0 && checkString(args[0])) {
            switch (args[0].toLowerCase()) {
                case "give":
                    if(args != null && args.length>1&&checkString(args[1])){
                        if(args.length>2 && checkString(args[2])){
                            help(sender);
                            return true;

                        }
                        p = Bukkit.getPlayer(args[1]);
                        if(p.isOnline()){
                            if(checkString(args[2])){
                                try{
                                    Integer i = Integer.parseInt(args[2]);
                                    if(i > 0) {
                                        dm.addPvPoint(p, i);
                                        sender.sendMessage(serialize(plugin.getConfig().getString("customization.updated-points"),p));
                                    } else
                                        sender.sendMessage(plugin.getPrefix()+ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("customization.not-positive").replace("%number%",i.toString())));
                                } catch (NumberFormatException exc){
                                    sender.sendMessage(plugin.getPrefix()+ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("customization.unvalid-string").replace("%string%",args[2])));
                                }
                            }else
                                help(sender);
                        } else
                            sender.sendMessage(serialize(plugin.getConfig().getString("cutomization.player-notonline"),p));
                    } else
                        help(sender);
                    break;
                case "sub":
                    if(args!=null&&args.length>1&&checkString(args[1])){
                        p = Bukkit.getPlayer(args[1]);
                        if(p.isOnline()){
                            if(checkString(args[2])){
                                try{
                                    Integer i = Integer.parseInt(args[2]);
                                    if(i > 0) {
                                        dm.subPvPoint(p, i);
                                        sender.sendMessage(serialize(plugin.getConfig().getString("cutomization.updated-points"),p));
                                    } else
                                        sender.sendMessage(plugin.getPrefix()+ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("customization.not-positive").replace("%number%",i.toString())));
                                } catch (NumberFormatException exc){
                                    sender.sendMessage(plugin.getPrefix()+ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("customization.unvalid-string").replace("%string%",args[2])));
                                }
                            }else
                                help(sender);
                        } else
                            sender.sendMessage(serialize(plugin.getConfig().getString("cutomization.player-notonline"),p));
                    } else
                        help(sender);
                    break;
                case "set":
                    if(args!=null&&args.length>1&&checkString(args[1])){
                        p = Bukkit.getPlayer(args[1]);
                        if(p.isOnline()){
                            if(checkString(args[2])){
                                try{
                                    Integer i = Integer.parseInt(args[2]);
                                    if(i > 0) {
                                        dm.setPvPoint(p, i);
                                        sender.sendMessage(plugin.getPrefix() + ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("customization.updated-points").replace("%player%",p.getName()).replace("%points%",((Integer)dm.getPvPoints(p)).toString())));
                                    } else
                                        sender.sendMessage(plugin.getPrefix()+ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("customization.not-positive").replace("%number%",i.toString())));
                                } catch (NumberFormatException exc){
                                    sender.sendMessage(plugin.getPrefix()+ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("customization.unvalid-string").replace("%string%",args[2])));
                                }
                            }else
                                help(sender);
                        } else
                            sender.sendMessage(serialize(plugin.getConfig().getString("cutomization.player-notonline"),p));
                    } else
                        help(sender);
                    break;
                case "stats":
                    if(args!=null&&args.length>1&&checkString(args[1])){
                        p = Bukkit.getPlayer(args[1]);
                        if(p != null){
                            sender.sendMessage(serialize(plugin.getConfig().getString("some-stats"),p));
                            sender.sendMessage(compileStats(p));
                        } else
                            sender.sendMessage(serialize(plugin.getConfig().getString("cutomization.player-notonline"),p));
                    } else
                        help(sender);
                    break;
                case "help":
                default:
                    help(sender);
            }
        } else{
               if(sender instanceof Player){
                   p = (Player) sender;
                   p.sendMessage(serialize(plugin.getPrefix()+plugin.getConfig().getString("your-stats"),null));
                   p.sendMessage(compileStats(p));
                   p.playSound(p.getLocation(),plugin.getConfig().getString("stats-sound"),1,0.5F);
               } else
                   sender.sendMessage(serialize(plugin.getPrefix()+plugin.getConfig().getString("no-player"),null));
        }
        return true;
    }

    private String[] compileHelp(){
        String[] help = new String[2];
        return help;
    }

    private boolean checkString(String s){
        return s != null && !s.equalsIgnoreCase("") && !s.equalsIgnoreCase(" ");
    }

    private String[] compileStats(Player p){
        String[] stats = new String[5];
        stats[0] = serialize(plugin.getConfig().getString("delimiter"),p);
        stats[1] = serialize(plugin.getConfig().getString("stats-tag"),p);
        stats[2] = serialize(plugin.getConfig().getString("stats-string"),p);
        stats[3] = serialize(plugin.getConfig().getString("points-string"),p);
        stats[4] = serialize(plugin.getConfig().getString("delimiter"),p);
        return stats;
    }

    private String serialize(String s, Player p){
        Integer kills = dm.getKills(p), deaths = dm.getDeaths(p), points = dm.getPvPoints(p);
        double kd = deaths != 0 ? kills/deaths : 0;
        if(p != null)
            s = s.replace("%player%",p.getName());
        s = s.replace("%kills%", kills.toString());
        s = s.replace("%deaths%",deaths.toString());
        s = s.replace("%kd%",String.valueOf(kd));
        s = s.replace("%points%", points.toString());
        s = s.replace("%pointname%",plugin.getConfig().getString("point-name"));
        return ChatColor.translateAlternateColorCodes('&',s);
    }

    private void help(CommandSender sender){
        if(sender.hasPermission("pvpranked.admin"))
            sender.sendMessage(compileHelp());
    }
}
