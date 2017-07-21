package io.github.dave5080;


import net.cydhra.asci.AdvancedCommand;
import net.cydhra.asci.CommandRegistry;
import net.cydhra.asci.builder.CommandBuilder;
import net.cydhra.asci.data.Argument;
import net.cydhra.asci.handler.ICommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PvPCommand {

    private PvPCommand(){}

    private static DataManager dm = DataManager.getManager();

    static ICommandHandler give = new ICommandHandler() {
        @Override
        public boolean execute(AdvancedCommand command, CommandSender sender, String label, String... args) {

            return true;
        }
    };

   static ICommandHandler sub = new ICommandHandler() {
        @Override
        public boolean execute(AdvancedCommand command, CommandSender sender, String label, String... args) {
            return true;
        }
    };

    static ICommandHandler set = new ICommandHandler() {
        @Override
        public boolean execute(AdvancedCommand command, CommandSender sender, String label, String... args) {
            return true;
        }
    };

    static ICommandHandler stats = new ICommandHandler() {
        @Override
        public boolean execute(AdvancedCommand command, CommandSender sender, String label, String... args) {
            if (args != null && args.length >= 0){
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
    };

    private static void help(CommandSender sender){

    }

    static AdvancedCommand pvpranked =
            new CommandBuilder()
                    .withName("pvpranked")
                    .withDescription("An example command")
                    .withPermission("pvpranked.admin")
                    .withHelpPrinter()
                    .addHandler(give)
                    .addHandler(set)
                    .addHandler(sub)
                    .build();

    static AdvancedCommand pvpstats =
            new CommandBuilder()
                    .withName("pvpstats")
                    .withHelpPrinter()
                    .withPermission("")
                    .addArgument("example-argument", "an example argument", false, true, Argument.ArgumentType.UNDEFINED)
                    .addAlias("stats")
                    .addHandler(stats)
                    .build();


    private static String[] compileStats(Player p){
        String[] stats = new String[6];
        stats[0] = Customizations.DELIMITER.toString();
        stats[1] = Customizations.STATS_TAG.toString();
        stats[2] = Customizations.STATS_STRING.toString().replace("%kills%",dm.getKills(p).toString()).replace("%deaths%",dm.getKills(p).toString()).replace("%kd%",dm.getDeaths(p)==0?"0":Double.valueOf(dm.getKills(p)/dm.getDeaths(p)).toString());
        stats[3] = Customizations.POINTS_STRING.toString().replace("%points%",dm.getPvPoints(p).toString());
        stats[4] = PvPranked.getInstance().isUsingPex()?Customizations.RANK_STRING.toString().replace("%rank%", getFroupPrefix(p)):"";
        stats[5] = Customizations.DELIMITER.toString();
        return stats;
    }

    private static String getFroupPrefix(Player p){
        List<PermissionGroup> groups = PermissionsEx.getPermissionManager().getGroupList();
        groups.sort(new Comparator<PermissionGroup>() {
            @Override
            public int compare(PermissionGroup o1, PermissionGroup o2) {
                return o1.getRank() > o2.getRank() ? 1 : o1.getRank() < o2.getRank() ? -1: 0;
            }
        });
        Collections.reverse(groups);
        int point = dm.getPvPoints(p);
        for(PermissionGroup g : groups){
            if(point >= g.getRank())
                return g.getPrefix();
        }
        return null;
    }



    public static void register(JavaPlugin plugin){
        //CommandRegistry.registerCommand(plugin.getName(),pvpranked);
        CommandRegistry.registerCommand(plugin.getName(),pvpstats);
    }


}