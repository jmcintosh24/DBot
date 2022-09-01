package com.github.jmcintosh24.dbot;

import com.google.gson.annotations.SerializedName;

/**
 * This class contains all the necessary steam profile data that will be passed in as JSON, and converted by the
 * GsonConverterFactory into a Java object.
 *
 * @author Jacob McIntosh
 * @version 8/31/2022
 */
public class PlayerInfo {
    private String steamid;

    @SerializedName("communityvisibilitystate")
    private int communityVisibilityState;

    @SerializedName("profilestate")
    private int profileState;

    @SerializedName("personaname")
    private String personaName;

    @SerializedName("commentpermission")
    private int commentPermission;

    @SerializedName("profileurl")
    private String profileUrl;

    private String avatar;

    @SerializedName("avatarmedium")
    private String avatarMedium;

    @SerializedName("avatarfull")
    private String avatarFull;

    @SerializedName("avatarhash")
    private String avatarHash;

    @SerializedName("lastlogoff")
    private String lastLogOff;

    @SerializedName("personastate")
    private int personaState;

    @SerializedName("accountname")
    private String accountName;

    @SerializedName("primaryclanid")
    private String primaryClanId;

    @SerializedName("timecreated")
    private String timeCreated;

    @SerializedName("personastateflags")
    private int personaStateFlags;

    @SerializedName("loccountrycode")
    private String locCountryCode;

    public String getSteamid() {
        return steamid;
    }

    public int getCommunityVisibilityState() {
        return communityVisibilityState;
    }

    public int getProfileState() {
        return profileState;
    }

    public String getPersonaName() {
        return personaName;
    }

    public int getCommentPermission() {
        return commentPermission;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getAvatarMedium() {
        return avatarMedium;
    }

    public String getAvatarFull() {
        return avatarFull;
    }

    public String getAvatarHash() {
        return avatarHash;
    }

    public String getLastLogOff() {
        return lastLogOff;
    }

    public int getPersonaState() {
        return personaState;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getPrimaryClanId() {
        return primaryClanId;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public int getPersonaStateFlags() {
        return personaStateFlags;
    }

    public String getLocCountryCode() {
        return locCountryCode;
    }
}
