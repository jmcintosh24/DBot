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
import java.time.OffsetDateTime;
import java.util.EnumSet;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

public class DBot extends ListenerAdapter {
    public static void main(String[] args) throws LoginException {
        //Creates the Java Discord API object
        JDA jda = JDABuilder.createLight(args[0], EnumSet.noneOf(GatewayIntent.class))
                .addEventListeners(new DBot())
                .setActivity(Activity.playing("Type /help"))
                .build();

        //This object holds all the bots commands
        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(
                Commands.slash("help", "Display a list of all commands."),
                Commands.slash("ping", "Get the ping of the bot in ms."),
                Commands.slash("count", "Get the total number of members in this server."),
                Commands.slash("jointime", "Get the date that a member joined this server.")
                        .addOption(USER, "user", "The user to get the date info on.", true)
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
                case "jointime":
                    jointime(event);
                    break;
                default:
                    event.reply("That is not a valid command :(").setEphemeral(true).queue();
            }
        }
    }

    public void help(SlashCommandInteractionEvent event) {
        event.reply("""
                        /help - Display a list of all commands.
                        /ping - Get the ping of the bot in ms.
                        /count - Get the total number of members in this server.
                        /jointime [member] - Get the date that a member joined this server."""
                ).setEphemeral(true)
                .queue();
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
            event.reply("There are " + event.getGuild().getMemberCount() + " members in this server.")
                    .setEphemeral(true)
                    .queue();
        } catch (NullPointerException e) {
            event.reply("There was an error getting the member count.")
                    .setEphemeral(true)
                    .queue();
        }
    }

    public void jointime(SlashCommandInteractionEvent event) {
        try {
            Member user = event.getOption("user").getAsMember();
            //If the user's join date is properly recorded, then carry out the date retrieval
            if (user.hasTimeJoined()) {
                OffsetDateTime timeJoined = user.getTimeJoined();

                int day = timeJoined.getDayOfMonth();
                String month = timeJoined.getMonth().toString();
                int year = timeJoined.getYear();

                event.reply(user.getEffectiveName() + " joined " + event.getGuild().getName() + " on " + month +
                                " " + day + ", " + year)
                        .setEphemeral(true)
                        .queue();
            } else {
                event.reply("Sorry, the date info on " + user.getEffectiveName() + "is not available :(");
            }
        } catch (IllegalStateException | NullPointerException e) {
            event.reply("There was an error finding information on the given user :(")
                    .setEphemeral(true)
                    .queue();
        }
    }
}