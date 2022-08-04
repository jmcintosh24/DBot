package com.github.jmcintosh24.dbot;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

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

    public GamesLibrary getGamesLibrary(String user) throws IOException {
        Call<OwnedGamesResponse> call = steamAPI.getGames(apikey, user, true);
        return call.execute().body().getResponse();
    }
}
