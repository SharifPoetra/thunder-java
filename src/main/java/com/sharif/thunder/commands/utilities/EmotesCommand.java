package com.sharif.thunder.commands.utilities;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.UtilitiesCommand;
import net.dv8tion.jda.api.entities.Emote;

public class EmotesCommand extends UtilitiesCommand {

    protected final Thunder thunder;

    public EmotesCommand(Thunder thunder) {
        this.thunder = thunder;
        this.name = "emote";
        this.arguments = "<emotes>";
        this.aliases = new String[] {"emotes", "emoji", "charinfo"};
        this.help = "shows detailed information about an emote, emoji, or character.";
    }

    @Override
    protected void execute(CommandEvent event) {
        String str = event.getArgs();
        if (str.matches("<:.*:\\d+>")) {
            String id = str.replaceAll("<:.*:(\\d+)>", "$1");
            Emote emote = event.getJDA().getEmoteById(id);
            if (emote == null) {
                event.replyError(
                        "Unknown emote:\n"
                                + "ID: **"
                                + id
                                + "**\n"
                                + "Guild: Unknown\n"
                                + "URL: https://cdn.discordapp.com/emojis/"
                                + id
                                + ".png");
                return;
            }
            event.replySuccess(
                    "Emote **"
                            + emote.getName()
                            + ":**\n"
                            + "ID: **"
                            + emote.getId()
                            + "**\n"
                            + "Guild: "
                            + (emote.getGuild() == null
                                    ? "Unknown"
                                    : "**" + emote.getGuild().getName() + "**")
                            + "\n"
                            + "URL: "
                            + emote.getImageUrl());
            return;
        }
        if (str.codePoints().count() > 10) {
            event.replyError("Invalid emote, or input is too long");
            return;
        }
        StringBuilder builder = new StringBuilder("Emoji/Character info:");
        str.codePoints()
                .forEachOrdered(
                        code -> {
                            char[] chars = Character.toChars(code);
                            String hex = Integer.toHexString(code).toUpperCase();
                            while (hex.length() < 4) hex = "0" + hex;
                            builder.append("\n`\\u").append(hex).append("`   ");
                            if (chars.length > 1) {
                                String hex0 = Integer.toHexString(chars[0]).toUpperCase();
                                String hex1 = Integer.toHexString(chars[1]).toUpperCase();
                                while (hex0.length() < 4) hex0 = "0" + hex0;
                                while (hex1.length() < 4) hex1 = "0" + hex1;
                                builder.append("[`\\u")
                                        .append(hex0)
                                        .append("\\u")
                                        .append(hex1)
                                        .append("`]  ");
                            }
                            builder.append(String.valueOf(chars))
                                    .append("   _")
                                    .append(Character.getName(code))
                                    .append("_");
                        });
        event.replySuccess(builder.toString());
        return;
    }
}
