package com.github.jmcintosh24.dbot;

import com.google.gson.annotations.SerializedName;

/**
 * This class contains all the necessary Game Library data that will be passed in as JSON, and converted by the
 * GsonConverterFactory into a Java object.
 *
 * @author Jacob McIntosh
 * @version 8/21/2022
 */
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
