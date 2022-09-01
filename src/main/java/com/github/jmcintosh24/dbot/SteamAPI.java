package com.github.jmcintosh24.dbot;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * This interface is implemented by Retrofit.
 *
 * @author Jacob McIntosh
 * @version 8/31/2022
 */
public interface SteamAPI {
    @GET("IPlayerService/GetOwnedGames/v0001")
    Call<OwnedGamesResponse> getGames(@Query("key") String apikey, @Query("steamid") String id,
                                @Query("include_played_free_games") boolean bool1, @Query("include_appinfo") boolean bool2);

    @GET("ISteamUser/GetPlayerSummaries/v0002")
    Call<PlayerSummariesResponse> getSummaries(@Query("key") String apikey, @Query("steamids") String id);
}
