package com.github.jmcintosh24.dbot;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * Handles all interactions with the Steam Web API. This class utilizes Retrofit, which makes HTTP calls in Java a
 * much easier process.
 *
 * @author Jacob McIntosh
 * @version 8/31/2022
 */
public class Steam {
    private Retrofit retrofit;
    private SteamAPI steamAPI;
    private String apikey;

    public Steam(String apikey) {
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.steampowered.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        steamAPI = retrofit.create(SteamAPI.class);

        this.apikey = apikey;
    }

    public Game[] getGamesList(String user) throws IOException, NullPointerException {
        Call<OwnedGamesResponse> call = steamAPI.getGames(apikey, user, true, true);
        GamesLibrary library = call.execute().body().getResponse();

        return library.getLibrary();
    }

    public String[] getGameNamesList(String user) throws IOException, NullPointerException {
        Call<OwnedGamesResponse> call = steamAPI.getGames(apikey, user, true, true);
        GamesLibrary library = call.execute().body().getResponse();

        Game[] games = library.getLibrary();
        String[] names = new String[games.length];

        for (int i = 0; i < games.length; i++)
            names[i] = games[i].getName();

        return names;
    }

    public int getNumGames(String user) throws IOException, NullPointerException {
        Call<OwnedGamesResponse> call = steamAPI.getGames(apikey, user, true, true);
        GamesLibrary library = call.execute().body().getResponse();

        return library.getGames_count();
    }

    public String getUserPersona(String user) throws IOException, NullPointerException {
        Call<PlayerSummariesResponse> call = steamAPI.getSummaries(apikey, user);
        PlayerInfo info = call.execute().body().getResponse().getPlayers()[0];

        return info.getPersonaName();
    }
}
