/**
 * The main class for the Discord bot. Creates and manages all slash commands.
 *
 * @author Jacob McIntosh
 * @version 8/1/2021
 */
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

public class DBot extends ListenerAdapter {

    private static Steam steam;

    public static void main(String[] args) throws LoginException {
        //Creates the Java Discord API object
        JDA jda = JDABuilder.createLight(args[0], EnumSet.noneOf(GatewayIntent.class))
                .addEventListeners(new DBot())
                .setActivity(Activity.playing("Type /help"))
                .build();

        //Creates an instance of the Steam class, which handles interactions with the Steam Web API
        steam = new Steam(args[1]);

        //This object holds all the bots commands
        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(
                Commands.slash("help", "Display a list of all commands."),
                Commands.slash("ping", "Get the ping of this bot in ms."),
                Commands.slash("count", "Get the total number of members in this server."),
                Commands.slash("joindate", "Get the date that a member joined this server.")
                        .addOption(USER, "user", "The user to get the date info on.", true),
                Commands.slash("numgames", "Get the number of games that a steam user has. You must " +
                                "provide a valid 64-bit steamid.")
                        .addOption(STRING, "steamid64", "The steam user to get the info on.", true)
        );

        commands.queue();
    }

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
                default:
                    reply(event, "That is not a valid command :(");
            }
        }
    }

    public void help(SlashCommandInteractionEvent event) {
        reply(event, """
                /help - Display a list of all commands.
                /ping - Get the ping of the bot in ms.
                /count - Get the total number of members in this server.
                /joindate [member] - Get the date that a member joined this server.
                /numgames [64-bit steamid] - Get the number of games that a steam user has. You must provide a valid
                64-bit steamid.""");
    }

    public void ping(SlashCommandInteractionEvent event) {
        long time = System.currentTimeMillis();
        event.reply("Ping")
                .setEphemeral(true)
                .flatMap(v ->
                        event.getHook().editOriginalFormat("Ping: %d ms", System.currentTimeMillis() - time)
                ).queue();
    }

    public void count(SlashCommandInteractionEvent event) {
        try {
            reply(event, "There are " + event.getGuild().getMemberCount() + " members in this server.");
        } catch (NullPointerException e) {
            reply(event, "There was an error getting the member count.");
        }
    }

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

    public void numGames(SlashCommandInteractionEvent event) {
        try {
            GamesLibrary library = steam.getGamesLibrary(event.getOption("steamid64").getAsString());
            reply(event, "That user owns " + library.getGames_count() + " games. (Including F2P Games)");
        } catch (IOException e) {
            reply(event, "There was an error when calling the Steam Web API");
        }

    }

    public void reply(SlashCommandInteractionEvent event, String message) {
        event.reply(message)
                .setEphemeral(true)
                .queue();
    }
}