package io.github.dave5080;


import io.github.dave5080.listeners.onKillPlayer;
import io.github.dave5080.listeners.onPlayerJoin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginLogger;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

/**
 * Created by Dave5080 on 14/07/2017.
 */
public class PvPranked extends JavaPlugin{

    private PluginLogger logger;
    private DataManager dm;

    @Override
    public void onEnable() {
        //Metrics metrics = new Metrics(this);
        logger = new PluginLogger(getInstance());
        getConfig("config");
        if(isUsingPex()&&Bukkit.getPluginManager().getPlugin("PermissionsEx")==null) {
            Bukkit.getPluginManager().disablePlugin(this);
            throw new RuntimeException("PermissionEx is required to start " + getDescription().getName());
        }
        dm = DataManager.getManager();
        Bukkit.getPluginManager().registerEvents(new onPlayerJoin(), getInstance());
        Bukkit.getPluginManager().registerEvents(new onKillPlayer(), getInstance());
        //PvPCommand.register(getInstance());
        getCommand("stats").setExecutor(new StatCommand());

    }

    @Override
    public void onDisable(){

    }

    public static PvPranked getInstance(){
        return (PvPranked) Bukkit.getPluginManager().getPlugin("PvPranked");
    }

    public YamlConfiguration getConfig(String name){
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
        return Customizations.PREFIX.toString();
    }

    public boolean isUsingPex(){
            return getConfig().getBoolean("using-pex");
    }
}
