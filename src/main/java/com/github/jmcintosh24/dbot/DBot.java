package com.github.jmcintosh24.dbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.util.*;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

/**
 * The main class for the Discord bot. Creates and manages all slash commands.
 *
 * @author Jacob McIntosh
 * @version 8/31/2021
 */
public class DBot extends ListenerAdapter {

    private static Steam steam;

    /* This HashMap pairs steam ids with GameTrees. It is used so that a game tree does not have to be re-made
    when the same user's info is requested.
     */
    private static HashMap<String, GameTree> users = new HashMap<>(); // id : GameTree

    public static void main(String[] args) throws LoginException {
        //Creates the Java Discord API object
        //The discord bot token passed into main as the first argument is given to the JDABuilder
        JDA jda = JDABuilder.createLight(args[0], EnumSet.noneOf(GatewayIntent.class))
                .addEventListeners(new DBot())
                .setActivity(Activity.playing("Type /help"))
                .build();

        //Creates an instance of the Steam class, which handles interactions with the Steam Web API
        //The Steam Web API passed into main as the second argument is given to this object
        steam = new Steam(args[1]);

        //This object holds all the bots commands
        CommandListUpdateAction commands = jda.updateCommands();

        initializeCommands(commands);

        commands.queue();
    }

    /*
     Initializes all commands that the bot will recognize.
     */
    private static void initializeCommands(CommandListUpdateAction commands) {

        commands.addCommands(
                Commands.slash("help", "Display a list of all commands."),
                Commands.slash("ping", "Get the ping of this bot in ms."),
                Commands.slash("count", "Get the total number of members in this server."),
                Commands.slash("joindate", "Get the date that a member joined this server.")
                        .addOption(USER, "user", "The desired steam user.", true),
                Commands.slash("numgames", "Get the number of games that a steam user has")
                        .addOption(STRING, "steamid64", "The desired steam user.", true),
                Commands.slash("mostplayed", "Get the most played game of a steam user")
                        .addOption(STRING, "steamid64", "The desired steam user.", true),
                Commands.slash("displayname", "Get the display name of the given steam user")
                        .addOption(STRING, "steamid64", "The desired steam user.", true)
        );

        OptionData steamUser = new OptionData(STRING, "steamid64", "The desired steam user.",
                true);
        OptionData game = new OptionData(STRING, "game", "The game you want the stats on.",
                true);

        commands.addCommands(
                Commands.slash("usergamestats", "Get a users stats on a specified game.")
                        .addOptions(steamUser, game)
        );


        //Creates five steam user options, which is the maximum amount the shared games command will accept
        OptionData steamUser1 = new OptionData(STRING, "firststeamid64", "The first steam user", true);
        OptionData steamUser2 = new OptionData(STRING, "secondsteamid64", "The second steam user", true);
        OptionData steamUser3 = new OptionData(STRING, "thirdsteamid64", "The first steam user", false);
        OptionData steamUser4 = new OptionData(STRING, "fourthsteamid64", "The second steam user", false);
        OptionData steamUser5 = new OptionData(STRING, "fifthsteamid64", "The first steam user", false);

        commands.addCommands(
                Commands.slash("sharedgames", "Get a list of all the shared games of the given users.")
                        .addOptions(steamUser1, steamUser2, steamUser3, steamUser4, steamUser5)
        );

    }

    /**
     * Overrides the default  method response to a slash command. Uses a switch statement to check all the possible
     * valid commands, and then passes the given event into the corresponding commands method.
     *
     * @param event - the event created by the slash command
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getGuild() != null) {
            switch (event.getName()) {
                case "help":
                    help(event);
                    break;
                case "ping":
                    ping(event);
                    break;
                case "count":
                    try {
                        count(event);
                    } catch (NullPointerException e) {
                        reply(event, "There was an error getting the member count.");
                    }
                    break;
                case "joindate":
                    try {
                        joinDate(event);
                    } catch (IllegalStateException | NullPointerException e) {
                        reply(event, "Sorry, there was an error finding information on the given user.");
                    }
                    break;
                case "numgames":
                    try {
                        numGames(event);
                    } catch (IOException | NullPointerException e) {
                        reply(event, "There was an error when finding that users number of games.");
                    }
                    break;
                case "mostplayed":
                    try {
                        mostPlayed(event);
                    } catch (IOException | NullPointerException e) {
                        reply(event, "There was an error when finding the most played game for that user.");
                    }
                    break;
                case "usergamestats":
                    try {
                        userGameStats(event);
                    } catch (IOException | NullPointerException e) {
                        reply(event, "There was an error when finding stats for that user.");
                    }
                    break;
                case "displayname":
                    try {
                        displayName(event);
                    } catch (IOException | NullPointerException e) {
                        reply(event, "There was an error when finding that users display name.");
                    }
                    break;
                case "sharedgames":
                    try {
                        sharedGames(event);
                    } catch (IOException | NullPointerException e) {
                        reply(event, "There was an error when finding the shared games for those users.");
                    }
                    break;
            }
        }
    }

    /**
     * Responds with a block of text describing all the commands and how to use them.
     *
     * @param event - the event that needs a response
     */
    public static void help(SlashCommandInteractionEvent event) {
        reply(event, """
                ====Basic Commands====
                /help - Display a list of all commands.
                /ping - Get the ping of the bot in ms.
                /count - Get the total number of members in this server.
                /joindate [member] - Get the date that a member joined this server.
                                
                ====Steam Commands=====
                /displayname [steamid64] - Get a users Steam display name.
                /numgames [steamid64] - Get the number of games that a Steam user has.
                /mostplayed [steamid64] - Get the most played game of a Steam user.
                /usergamestats [steamid64] [game] - Get a Steam users stats on a specified game.
                /sharedgames [steamid64] [steamid64] [steamid64] [steamid64] [steamid64] - Find all the shared games of
                the given steam users. A maximum of five users is supported.
                """
        );
    }

    /**
     * Responds with the current ping in ms by seeing how long it takes to reply to an event.
     *
     * @param event - the event that needs a response
     */
    public static void ping(SlashCommandInteractionEvent event) {
        long time = System.currentTimeMillis();
        event.reply("Ping")
                .setEphemeral(true)
                .flatMap(v ->
                        event.getHook().editOriginalFormat("Ping: %d ms", System.currentTimeMillis() - time)
                ).queue();
    }

    /**
     * Responds with the total number of members in the server.
     *
     * @param event - the event that needs a response
     */
    public static void count(SlashCommandInteractionEvent event) throws NullPointerException {
        reply(event, "There are " + event.getGuild().getMemberCount() + " members in this server.");
    }

    /**
     * Responds with the month and day that a certain user joined the server from an OffsetDateTime object.
     *
     * @param event - the event that needs a response
     */
    public static void joinDate(SlashCommandInteractionEvent event) throws IllegalStateException, NullPointerException {

        Member user = event.getOption("user").getAsMember();

        //If the user's join date is properly recorded, then carry out the date retrieval
        if (user.hasTimeJoined()) {
            OffsetDateTime timeJoined = user.getTimeJoined();

            int day = timeJoined.getDayOfMonth();
            String month = timeJoined.getMonth().toString();
            int year = timeJoined.getYear();

            reply(event, user.getEffectiveName() + " joined " + event.getGuild().getName() + " on "
                    + month + " " + day + ", " + year);
        } else
            reply(event, "Sorry, the date info on " + user.getEffectiveName() + "is not available :(");

    }

    /**
     * Responds with the total number of games that the given steam user has. Includes free to play games.
     *
     * @param event - the event that needs a response
     */
    public static void numGames(SlashCommandInteractionEvent event) throws IOException, NullPointerException {
        String userName = steam.getUserPersona(event.getOption("steamid64").getAsString());

        reply(event, userName + " owns " + steam.getNumGames(event.getOption("steamid64").getAsString()
        ) + " games. (Including F2P Games)");
    }

    /**
     * Responds with the most played game that a steam user has.
     *
     * @param event - the event that needs a response
     */
    public static void mostPlayed(SlashCommandInteractionEvent event) throws IOException, NullPointerException {
        String id = event.getOption("steamid64").getAsString();
        String userName = steam.getUserPersona(id);

        if (!users.containsKey(id)) {
            createGameTree(event, id);
        }

        GameTree tree = users.get(id);

        Game mostPlayed = tree.getMostPlayedGame();

        reply(event, userName + "'s most played game is " + mostPlayed.getName());
    }

    /**
     * Responds with the user's stats on the given game.
     *
     * @param event - the event that needs a response
     */
    public static void userGameStats(SlashCommandInteractionEvent event) throws IOException, NullPointerException {
        List<OptionMapping> options = event.getOptions();

        String id = options.get(0).getAsString();
        String gameName = options.get(1).getAsString();
        String userName = steam.getUserPersona(id);

        if (!users.containsKey(id)) {
            createGameTree(event, id);
        }

        GameTree tree = users.get(id);

        Game game = tree.findGame(gameName);

        if (game != null) {
            DecimalFormat df = new DecimalFormat(".3");
            double hours = (double) game.getPlaytimeForever() / 60;

            String response = "Name : " + gameName + "\n"
                    + "Total Playtime: " + df.format(hours) + " hours \n";

            reply(event, response);
        } else
            reply(event, userName + " does not own " + gameName);
    }

    /**
     * Responds with the user's Steam display name.
     *
     * @param event - the event that needs a response
     */
    public static void displayName(SlashCommandInteractionEvent event) throws IOException, NullPointerException {
        String id = event.getOption("steamid64").getAsString();
        String name = steam.getUserPersona(id);
        reply(event, "That users name is: " + name);
    }

    /**
     * This method finds all the shared games of the given users. A minimum of two Steam ids must be provided, with a
     * maximum of five. The shared games are found by utilizing the retainAll method in the ArrayList class. Essentially,
     * the first steam user's library only retains the games that everybody else owns.
     *
     * @param event - the event that needs a response
     */
    public static void sharedGames(SlashCommandInteractionEvent event) throws IOException {
        List<OptionMapping> options = event.getOptions();
        ArrayList<String> steamids = new ArrayList<>();

        for (OptionMapping i : options)
            steamids.add(i.getAsString());

        ArrayList<ArrayList<String>> libraries = new ArrayList<>();

        for (String id : steamids)
            libraries.add(new ArrayList<>(Arrays.asList(steam.getGameNamesList(id))));

        for (int i = 1; i < libraries.size(); i++)
            libraries.get(0).retainAll(libraries.get(i));

        ArrayList<String> shared = libraries.get(0);

        Collections.sort(shared);

        String result = "";

        for (int i = 0; i < steamids.size(); i++) {
            if (i != steamids.size() - 1)
                result += steam.getUserPersona(steamids.get(i)) + ", ";
            else
                result += "and " + steam.getUserPersona(steamids.get(i)) + "'s shared games\n\n";
        }

        for (String s : shared)
            result += s + "\n";

        reply(event, result);
    }

    /**
     * A helper method that sends a message to the discord chat.
     *
     * @param event   - the event that needs a response
     * @param message - the message to be sent
     */
    public static void reply(SlashCommandInteractionEvent event, String message) {
        event.reply(message)
                .setEphemeral(true)
                .queue();
    }

    /**
     * A helper method to create a gameTree for a steam user
     *
     * @param event - the event that needs a response
     * @param id    - the steam user to create a gameTree for
     */
    public static void createGameTree(SlashCommandInteractionEvent event, String id) throws IOException, NullPointerException {
        Game[] array = steam.getGamesList(event.getOption("steamid64").getAsString());

        GameTree tree = new GameTree();
        for (Game game : array)
            tree.add(game);

        users.put(id, tree);
    }

}