package com.github.jmcintosh24.dbot;

import com.google.gson.annotations.*;

public class Game {
    @SerializedName("appid")
    private int appId;

    @SerializedName("playtime_forever")
    private int playtimeForever;

    @SerializedName("playtime_windows_forever")
    private int playtimeWindowsForever;

    @SerializedName("playtime_mac_forever")
    private int playtimeMacForever;

    @SerializedName("playtime_linux_forever")
    private int playtimeLinuxForever;

    public int getAppId() {
        return appId;
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

}
