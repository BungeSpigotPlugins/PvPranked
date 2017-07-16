package io.github.dave5080;


import io.github.dave5080.listeners.onKillPlayer;
import io.github.dave5080.listeners.onPlayerJoin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

/**
 * Created by Dave5080 on 14/07/2017.
 */
public class PvPRanked extends JavaPlugin{

    private PluginLogger logger;
    private DataManager dm;
    private Metrics metrics;

    @Override
    public void onEnable(){
        logger = new PluginLogger(getInstance());
        getConfig("config");
        dm = DataManager.getManager();
        Bukkit.getPluginManager().registerEvents(new onPlayerJoin(),getInstance());
        Bukkit.getPluginManager().registerEvents(new onKillPlayer(),getInstance());
        getCommand("pvpranked").setExecutor(new PvPCommand());
        if(getConfig().getBoolean("metrics")){
            try {
                (metrics = new Metrics(getInstance())).start();
            } catch (IOException e) {
                logger.severe(ChatColor.RED+e.getMessage());
            }
        }
    }

    @Override
    public void onDisable(){

    }

    public static PvPRanked getInstance(){
        return (PvPRanked) Bukkit.getPluginManager().getPlugin("PvPRanked");
    }

    private YamlConfiguration getConfig(String name){
        name = name +".yml";
        File file = new File(getDataFolder()+File.separator+name);
        if (!file.exists()){
            file.getParentFile().mkdir();
            saveResource("config.yml",false);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public PluginLogger getPluginLogger(){
        return logger;
    }

    public String getPrefix(){
        return ChatColor.translateAlternateColorCodes('&',this.getConfig().getString("prefix"))+ChatColor.RESET+" ";
    }
}
