package io.github.dave5080;

import org.bukkit.ChatColor;

/**
 * Created by Dave5080 on 19/07/2017.
 */
public enum Customizations {
    PREFIX("prefix"),
    STARTING_POINTS("starting-points"),
    DATABASE_TYPE("database-type"),
    DATABASE_NAME("database.name"),
    DATABASE_IP("database.ip"),
    DATABASE_PORT("database.port"),
    DATABASE_USER("database.user"),
    DATABASE_PASSWORD("database.password"),
    PLAYER_NOT_ONLINE("customizations.player-not-online"),
    UNVALID_STRING("customizations.unvalid-string"),
    NOT_POSITIVE("customizations.not-positive"),
    UPDATED_POINTS("customizations.updated-points"),
    LOSE_POINTS("customizations.lose-points"),
    WIN_POINTS("customizations.win-points"),
    YOUR_STATS("customizations.your-stats"),
    SOME_STATS("customizations.some-stats"),
    DELIMITER("customizations.delimiter"),
    POINT_NAME("customizations.point-name"),
    STATS_TAG("customizations.stats-tag"),
    STATS_STRING("customizations.stats-string"),
    POINTS_STRING("customizations.points-string"),
    RANK_STRING("customizations.rank-string"),
    NOT_A_PLAYER("customizations.not-a-player"),
    STATS_SOUND("customizations.stats-sound");

    private String s;
    Customizations(String value){
        s = value;
    }

    public String toString(){
        return ChatColor.translateAlternateColorCodes('&',PvPranked.getInstance().getConfig().getString(s));
    }
}
