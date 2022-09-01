package com.github.jmcintosh24.dbot;

/**
 * This class acts as a container for the response that will be passed in as JSON, and converted by the
 * GsonConverterFactory into a Java object.
 *
 * @author Jacob McIntosh
 * @version 8/31/2022
 */
public class PlayerSummariesResponse {
    private PlayerSummaries response;

    public PlayerSummaries getResponse() {
        return response;
    }
}
