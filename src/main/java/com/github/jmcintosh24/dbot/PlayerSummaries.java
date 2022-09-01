package com.github.jmcintosh24.dbot;

/**
 * This class holds the array of steam user data that will be passed in as JSON, and converted by the
 * GsonConverterFactory into a Java object.
 *
 * @author Jacob McIntosh
 * @version 8/31/2022
 */
public class PlayerSummaries {
    private PlayerInfo[] players;

    public PlayerInfo[] getPlayers() {
        return players;
    }
}
