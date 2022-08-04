package com.github.jmcintosh24.dbot;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SteamAPI {
    @GET("IPlayerService/GetOwnedGames/v0001")
    Call<OwnedGamesResponse> getGames(@Query("key") String apikey, @Query("steamid") String id,
                                @Query("include_played_free_games") boolean bool);
}
