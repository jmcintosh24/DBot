package com.github.jmcintosh24.dbot;

import com.google.gson.annotations.*;

/**
 * This class contains all the necessary Game data that will be passed in as JSON, and converted by the
 * GsonConverterFactory into a Java object.
 *
 * @author Jacob McIntosh
 * @version 8/21/2022
 */
public class Game {
    @SerializedName("appid")
    private int appId;

    private String name;

    @SerializedName("playtime_forever")
    private int playtimeForever;

    @SerializedName("playtime_windows_forever")
    private int playtimeWindowsForever;

    @SerializedName("playtime_mac_forever")
    private int playtimeMacForever;

    @SerializedName("playtime_linux_forever")
    private int playtimeLinuxForever;

    @SerializedName("rtime_last_played")
    private int timeLastPlayed;

    public int getAppId() {
        return appId;
    }

    public String getName() {
        return name;
    }

    public int getPlaytimeForever() {
        return playtimeForever;
    }

    public int getPlaytimeWindowsForever() {
        return playtimeWindowsForever;
    }

    public int getPlaytimeMacForever() {
        return playtimeMacForever;
    }

    public int getPlaytimeLinuxForever() {
        return playtimeLinuxForever;
    }

    public int getTimeLastPlayed() {
        return timeLastPlayed;
    }

}
