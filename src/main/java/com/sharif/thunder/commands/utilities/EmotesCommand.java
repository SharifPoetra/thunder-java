/*
 *   Copyright 2019 SharifPoetra
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.sharif.thunder.commands.utilities;

import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.UtilitiesCommand;
import com.sharif.thunder.utils.SenderUtil;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Emote;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.TimeUtil;

public class EmotesCommand extends UtilitiesCommand {

  private final Thunder thunder;
  private static final Pattern emojiPatten = Pattern.compile("<:.*:(\\d+)>");
  private static final Pattern animatedEmojiPatten = Pattern.compile("<a:.*:(\\d+)>");
  private static String input;

  public EmotesCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "emote";
    this.arguments = new Argument[] {new Argument("emotes", Argument.Type.SHORTSTRING, true)};
    this.aliases = new String[] {"emotes", "emoji", "charinfo"};
    this.help = "shows detailed information about an emote, emoji, or character.";
  }

  @Override
  protected void execute(Object[] args, MessageReceivedEvent event) {

    input = (String) args[0];

    if (input.isEmpty()) {
      SenderUtil.replyError(event, "You must specify the emote to see the information about it!");
      return;
    }
    Matcher staticMatcher = emojiPatten.matcher(input);
    Matcher animMatcher = animatedEmojiPatten.matcher(input);

    if (staticMatcher.matches()) {
      custom(staticMatcher, event);
    } else if (animMatcher.matches()) {
      animated(animMatcher, event);
    } else {
      unicode(event);
    }
  }

  private void animated(Matcher matcher, MessageReceivedEvent event) {
    EmbedBuilder eb = new EmbedBuilder();
    String id = matcher.replaceFirst("$1");
    String url = "https://cdn.discordapp.com/emojis/" + id + ".gif";
    eb.setThumbnail(url);
    eb.setColor(event.getGuild().getSelfMember().getColor());

    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withZone(TimeZone.getTimeZone("UTC").toZoneId());

    String date = TimeUtil.getTimeCreated(MiscUtil.parseSnowflake(id)).format(formatter);

    Emote emote = event.getJDA().getEmoteById(id);
    if (emote == null) {
      eb.setTitle("Emoji info");
      eb.setDescription(String.format("Name: `%s`\n" + "Created at: `%s`\n" + "ID: `%s`\n" + "Guild: `%s`\n" + "URL: %s", "???", date, id, "???", url));
    } else {
      eb.setTitle("Emoji info");
      eb.setDescription(String.format("Name: `%s`\n" + "Created at: `%s`\n" + "ID: `%s`\n" + "Guild: `%s`\n" + "URL: %s", emote.getName(), date, id, emote.getGuild(), url));
    }
    event.getChannel().sendMessage(eb.build()).queue();
  }

  private void custom(Matcher matcher, MessageReceivedEvent event) {
    EmbedBuilder eb = new EmbedBuilder();
    String id = matcher.replaceFirst("$1");
    String url = "https://cdn.discordapp.com/emojis/" + id + ".png";
    eb.setThumbnail(url);
    eb.setColor(event.getGuild().getSelfMember().getColor());

    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withZone(TimeZone.getTimeZone("UTC").toZoneId());

    String date = TimeUtil.getTimeCreated(MiscUtil.parseSnowflake(id)).format(formatter);

    Emote emote = event.getJDA().getEmoteById(id);
    if (emote == null) {
      eb.setTitle("Emoji info");
      eb.setDescription(String.format("Name: `%s`\n" + "Created at: `%s`\n" + "ID: `%s`\n" + "Guild: `%s`\n" + "URL: %s", "???", date, id, "???", url));
    } else {
      eb.setTitle("Emoji info");
      eb.setDescription(String.format("Name: `%s`\n" + "Created at: `%s`\n" + "ID: `%s`\n" + "Guild: `%s`\n" + "URL: %s", emote.getName(), date, id, emote.getGuild(), url));
    }
    event.getChannel().sendMessage(eb.build()).queue();
  }

  private void unicode(MessageReceivedEvent event) {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setColor(event.getGuild().getSelfMember().getColor());
    if (input.codePoints().count() > 10) {
      SenderUtil.replyError(event, "Invalid emote, or input is too long");
    } else {
      StringBuilder builder = new StringBuilder("Emoji/Character info:");
      input.codePoints().forEachOrdered(code -> {
        char[] chars = Character.toChars(code);
        String hex = Integer.toHexString(code).toUpperCase();
        while (hex.length() < 4) hex = "0" + hex;
        builder.append("\n`\\u").append(hex).append("`   ");
        if (chars.length > 1) {
          String hex0 = Integer.toHexString(chars[0]).toUpperCase();
          String hex1 = Integer.toHexString(chars[1]).toUpperCase();
          while (hex0.length() < 4) hex0 = "0" + hex0;
          while (hex1.length() < 4) hex1 = "0" + hex1;
          builder.append("[`\\u").append(hex0).append("\\u").append(hex1).append("`]  ");
        }
        builder.append(String.valueOf(chars)).append("   _").append(Character.getName(code)).append("_");
      });
      eb.setDescription(builder.toString());
      event.getChannel().sendMessage(eb.build()).queue();
    }
  }
}
