package com.github.jmcintosh24.dbot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.EnumSet;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

/**
 * The main class for the Discord bot. Creates and manages all slash commands.
 *
 * @author Jacob McIntosh
 * @version 8/21/2021
 */
public class DBot extends ListenerAdapter {

    private static Steam steam;

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
                        .addOption(USER, "user", "The user to get the date info on.", true),
                Commands.slash("numgames", "Get the number of games that a steam user has. You must " +
                                "provide a valid 64-bit steamid.")
                        .addOption(STRING, "steamid64", "The steam user to get the info on.", true),
                Commands.slash("mostplayed", "Get the most played game of a steam user. You must provide a valid" +
                                " 64-bit steamid")
                        .addOption(STRING, "steamid64", "The steam user to get the info on.", true)
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
                    count(event);
                    break;
                case "joindate":
                    joinDate(event);
                    break;
                case "numgames":
                    numGames(event);
                    break;
                case "mostplayed":
                    mostPlayed(event);
                    break;

                default:
                    reply(event, "That is not a valid command :(");
            }
        }
    }

    /**
     * Responds with a block of text describing all the commands and how to use them.
     *
     * @param event - the event that needs a response
     */
    public void help(SlashCommandInteractionEvent event) {
        reply(event, """
                /help - Display a list of all commands.
                /ping - Get the ping of the bot in ms.
                /count - Get the total number of members in this server.
                /joindate [member] - Get the date that a member joined this server.
                /numgames [64-bit steamid] - Get the number of games that a steam user has. You must
                provide a valid 64-bit steamid.""");
    }

    /**
     * Responds with the current ping in ms by seeing how long it takes to reply to an event.
     *
     * @param event - the event that needs a response
     */
    public void ping(SlashCommandInteractionEvent event) {
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
    public void count(SlashCommandInteractionEvent event) {
        try {
            reply(event, "There are " + event.getGuild().getMemberCount() + " members in this server.");
        } catch (NullPointerException e) {
            reply(event, "There was an error getting the member count.");
        }
    }

    /**
     * Responds with the month and day that a certain user joined the server from an OffsetDateTime object.
     *
     * @param event - the event that needs a response
     */
    public void joinDate(SlashCommandInteractionEvent event) {
        try {
            Member user = event.getOption("user").getAsMember();
            //If the user's join date is properly recorded, then carry out the date retrieval
            if (user.hasTimeJoined()) {
                OffsetDateTime timeJoined = user.getTimeJoined();

                int day = timeJoined.getDayOfMonth();
                String month = timeJoined.getMonth().toString();
                int year = timeJoined.getYear();

                reply(event, user.getEffectiveName() + " joined " + event.getGuild().getName() + " on "
                        + month + " " + day + ", " + year);
            } else {
                reply(event, "Sorry, the date info on " + user.getEffectiveName() + "is not available :(");
            }
        } catch (IllegalStateException | NullPointerException e) {
            reply(event, "Sorry, there was an error finding information on the given user :(");
        }
    }

    /**
     * Responds with the total number of games that the given steam user has. Includes free to play games.
     *
     * @param event - the event that needs a response
     */
    public void numGames(SlashCommandInteractionEvent event) {
        try {
            reply(event, "That user owns " + steam.getNumGames(event.getOption("steamid64").getAsString()
            ) + " games. (Including F2P Games)");
        } catch (IOException e) {
            reply(event, "There was an error when calling the Steam Web API");
        }
    }

    /**
     * Responds with the most played game that a steam user has. First, the Game array is fetched from the GamesLibrary
     * object. Then, every Game object is added to a GameTree. Finally, the mostPlayedGame method is called on the
     * GameTree.
     *
     * @param event - the event that needs a response
     */
    public void mostPlayed(SlashCommandInteractionEvent event) {
        try {
            Game[] array = steam.getGamesList(event.getOption("steamid64").getAsString());

            GameTree tree = new GameTree();
            for (Game game : array) {
                tree.add(game);
            }
            Game mostPlayed = tree.getMostPlayedGame();
            reply(event, "That users most played game is " + mostPlayed.getName());
        } catch (IOException e) {
            reply(event, "There was an error when calling the Steam Web API");
        }
    }

    /**
     * A helper method that sends a message to the discord chat.
     *
     * @param event   - the event that needs a response
     * @param message - the message to be sent
     */
    public void reply(SlashCommandInteractionEvent event, String message) {
        event.reply(message)
                .setEphemeral(true)
                .queue();
    }
}