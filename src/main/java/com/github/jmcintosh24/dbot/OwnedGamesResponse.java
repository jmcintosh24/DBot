package com.github.jmcintosh24.dbot;

/**
 * This class contains the response that will be passed in as JSON, and converted by the GsonConverterFactory into a
 * Java object.
 *
 * @author Jacob McIntosh
 * @version 8/21/2022
 */
public class OwnedGamesResponse {
    private GamesLibrary response;

    public GamesLibrary getResponse() {
        return response;
    }
}
