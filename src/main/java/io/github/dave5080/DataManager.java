package io.github.dave5080;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionGroup;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Dave5080 on 14/07/2017.
 */
public class DataManager {

    private PvPranked plugin;
    private String driver = "org.h2.Driver";
    private String url;

    private String createDB = "CREATE TABLE PVPER(name varchar(255), pvpoints int, kills int, deaths int);";
    private String getVal = "SELECT %s FROM PVPER WHERE %s = ?;";
    private String insVal = "INSERT INTO PVPER(name, pvpoints, kills, deaths) VALUES(?,?,?,?);";
    private String upVAL = "UPDATE PVPER SET %s = ? WHERE %s = ?;";

    private Connection conn;
    private static DataManager manager;
    public static DataManager getManager(){
        if(manager == null)
            manager = new DataManager();
        return manager;
    }
    private DataManager(){
        plugin = PvPranked.getInstance();
        try {
            String db = plugin.getConfig().getString("database-type");
            switch (db.toLowerCase()) {
                case "mysql":
                    Class.forName("com.mysql.jdbc.Driver");
                    url = String.format("jdbc:mysql://%s:&s/&s?user=&s&password=%s",plugin.getConfig().getString("database.ip"),plugin.getConfig().getString("database.port"),plugin.getConfig().getString("database.name"),plugin.getConfig().getString("database.user"),plugin.getConfig().getString("database.password"));
                    plugin.getPluginLogger().info("Using mysql database to store data.");
                    break;
                case "h2":
                    Class.forName("org.h2.Driver");
                    url = String.format("jdbc:h2:%susers",plugin.getDataFolder().getAbsolutePath());
                    plugin.getPluginLogger().info("Using h2 database to store data.");
                    break;
                default:
                    plugin.getPluginLogger().severe(ChatColor.RED + "This plugin support only h2 and mysql database, can't load: " + db);
                    Bukkit.getPluginManager().disablePlugin(plugin);
                    break;
            }


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Statement newDB;

        try{
            conn = DriverManager.getConnection(url);
            newDB = conn.createStatement();
            newDB.execute(createDB);
        } catch (SQLException e) {
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                plugin.getPluginLogger().severe(ChatColor.RED + e.getMessage());
            }
        }
    }

    private Integer getValue(String column, String name){
        PreparedStatement getValue;
        try {
            conn = DriverManager.getConnection(url);
            getValue = conn.prepareStatement(String.format(getVal,column,"name"));
            getValue.setString(1,name);
            ResultSet set = getValue.executeQuery();

            if(set == null || !set.next()) {
                insValue(name);
                set = getValue.executeQuery();
            }
            return set.getInt(column);
        } catch (SQLException e) {
            plugin.getPluginLogger().severe(ChatColor.RED + e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                plugin.getPluginLogger().severe(ChatColor.RED + e.getMessage());
            }
        }
        return 0;
    }

    private void setValue(String column, String name, int value){
        try {
            if(getValue(column,name)==null) {
                insValue(name);
            } else {
                PreparedStatement setValue;

                conn = DriverManager.getConnection(url);
                setValue = conn.prepareStatement(String.format(upVAL, column, "name"));
                setValue.setString(2, name);
                setValue.setInt(1, value);
                setValue.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.getPluginLogger().severe(ChatColor.RED + e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                plugin.getPluginLogger().severe(ChatColor.RED + e.getMessage());
            }
        }
    }

    public void insValue(String name){
        PreparedStatement setValue;
        try{
            conn = DriverManager.getConnection(url);
            setValue = conn.prepareStatement(insVal);
            setValue.setString(1,name);
            setValue.setInt(2,plugin.getConfig().getInt("starting-points"));
            setValue.setInt(3,0);
            setValue.setInt(4,0);
            setValue.executeUpdate();
        } catch (SQLException e) {
            plugin.getPluginLogger().severe(ChatColor.RED + e.getMessage());
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                plugin.getPluginLogger().severe(ChatColor.RED + e.getMessage());
            }
        }
    }

    public Integer getPvPoints(Player p){
        return getValue("pvpoints",p.getName());
    }

    public Integer getKills(Player p){
        return getValue("kills",p.getName());
    }

    public Integer getDeaths(Player p){
        return getValue("deaths",p.getName());
    }

    public void setKills(Player p, int k){
        setValue("kills",p.getName(),k);
    }

    public void setPvPoint(Player p,int point){
        setValue("pvpoints",p.getName(),point);
        if(plugin.isUsingPex())
        updateRank(p);
    }

    public void setDeath(Player p, int d){
        setValue("deaths",p.getName(),d);
    }
  
    public void addKills(Player p, int k){
        setKills(p, getKills(p)+k);
    }

    public void addPvPoint(Player p, int point){
        setPvPoint(p, getPvPoints(p)+point);
    }

    public void addDeath(Player p, int d){
        setDeath(p, getDeaths(p)+d);
    }

    public void subPvPoint(Player p, int i){
        i = getPvPoints(p)-i;
        if(plugin.getConfig().getBoolean("allow-negative-points") && i<0)
            i = 0;
        setPvPoint(p,i);

    }


    public void subKills(Player p, int i){
        i = getKills(p)-i > 0 ? getPvPoints(p)-i : 0;
        setPvPoint(p,i);
    }


    public void subDeaths(Player p, int i){
        i = getDeaths(p)-i > 0 ? getPvPoints(p)-i : 0;
        setPvPoint(p,i);
    }

    public void updateRank(Player p){
        List<PermissionGroup> groups = new ArrayList<>(PermissionsEx.getPermissionManager().getGroupList());
        groups.sort(new Comparator<PermissionGroup>() {
            @Override
            public int compare(PermissionGroup o1, PermissionGroup o2) {
                return o1.getOptionInteger("pvp","*",700) > o2.getOptionInteger("pvp","*",700) ? 1 : o1.getOptionInteger("pvp","*",700)<o2.getOptionInteger("pvp","*",700) ? -1 : 0;
            }
        });
        PermissionUser user = PermissionsEx.getUser(p);
        int points = getPvPoints(p);
        for(PermissionGroup g : groups){
            if(points >= g.getOptionInteger("pvp","*",700))
                user.addGroup(g);
        }
        Collections.reverse(groups);
        for(PermissionGroup g : groups){
            if(points < g.getRank())
                user.removeGroup(g);
        }
    }
}
