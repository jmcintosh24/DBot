package com.github.jmcintosh24.dbot;

import com.google.gson.annotations.SerializedName;

public class GamesLibrary {
    @SerializedName("game_count")
    private int gamesCount;

    @SerializedName("games")
    private Game[] library;

    public int getGames_count() {
        return gamesCount;
    }

    public Game[] getLibrary() {
        return library;
    }
}
