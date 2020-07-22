/*
 *   Copyright 2019-2020 SharifPoetra
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
package com.sharif.thunder.commands;

import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.sharif.thunder.BotConfig;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.utils.FormatUtil;
import com.sharif.thunder.utils.OtherUtil;
import com.sharif.thunder.utils.SenderUtil;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class Command {

    private static final BotConfig config = new BotConfig();

    @Getter
    protected String name = "null";
    @Getter
    protected String help = "no help description provided";
    @Getter
    protected String[] aliases = new String[0];
    @Getter
    protected Argument[] arguments = new Argument[0];
    @Getter
    protected Command[] children = new Command[0];
    @Getter
    protected Permission[] userPermissions = new Permission[0];
    @Getter
    protected Permission[] botPermissions = new Permission[0];
    @Getter
    protected Category category = null;
    @Getter
    protected int cooldown = 0;
    protected boolean ownerOnly = false;
    protected boolean usesTopicTags = true;
    protected boolean guildOnly = false;
    @Getter
    protected boolean hidden = false;

    private static final String BOT_PERM = "%s I need the %s permission in this %s!";
    private static final String USER_PERM = "%s You must have the %s permission in this %s to use that!";

    protected abstract void execute(Object[] args, MessageReceivedEvent event);

    public final void run(String args, MessageReceivedEvent event) {

        // get a prefixes
        String prefix = Thunder.getDatabase().guildSettings.getSettings(event.getGuild()).getPrefix();
        if (prefix == null) prefix = config.getPrefix();

        if ("help".equalsIgnoreCase(args)) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(event.getGuild().getSelfMember().getColor());
            eb.setAuthor("Available help for " + name + " command:", null, event.getGuild().getSelfMember().getUser().getEffectiveAvatarUrl());
            eb.addField("Usage:", "`" + prefix + name + Argument.arrayToString(arguments) + "`", true);
            if (aliases.length > 0) {
                eb.addField("Aliases", "`" + String.join("`, `", aliases) + "`", true);
            }
            eb.setDescription("\n\n*" + help + "*\n");

            if (children.length > 0) {
                StringBuilder subSb = new StringBuilder();
                for (Command child : children) {
                    subSb.append("`").append(prefix).append(name).append(" ").append(child.name).append(Argument.arrayToString(child.arguments)).append("` - ").append(child.help).append("\n");
                }
                eb.addField("Subcommands:", subSb.toString(), true);
            }
            StringBuilder footerSb = new StringBuilder();
            footerSb.append("\n\nFor additional help, contact " + event.getJDA().getUserById(config.getOwnerId()).getName() + "#" + event.getJDA().getUserById(config.getOwnerId()).getDiscriminator() + "");
            eb.setFooter(footerSb.toString());
            event.getChannel().sendMessage(eb.build()).queue();
            return;
        }

        // child check
        if (args != null) {
            String[] argv = FormatUtil.cleanSplit(args);
            for (Command child : children) {
                if (child.isCommandFor(argv[0])) {
                    child.run(argv[1], event);
                    return;
                }
            }
        }

        // owner check
        if (ownerOnly && !(event.getAuthor().getId().equals(Long.toString(config.getOwnerId())))) {
            SenderUtil.reply(event, config.getError() + " That command is only for the bot owner!");
            return;
        }

        // category check
        if (category != null && !category.test(event)) {
            SenderUtil.reply(event, category.getFailureResponse());
            return;
        }

        // is allowed check
        if (event.getMessage().isFromType(ChannelType.TEXT) && !isAllowed(event.getTextChannel())) {
            SenderUtil.reply(event, config.getError() + " That command cannot be used in this channel!");
            return;
        }

        // availability check
        if (event.getChannelType() == ChannelType.TEXT) {
            // bot perms
            for (Permission p : botPermissions) {
                if (p.isChannel()) {
                    if (p.name().startsWith("VOICE")) {
                        VoiceChannel vc = event.getMember().getVoiceState().getChannel();
                        if (vc == null) {
                            SenderUtil.reply(event, config.getError() + " You must be in a voice channel to use that!");
                            return;
                        } else if (!event.getGuild().getSelfMember().hasPermission(vc, p)) {
                            SenderUtil.reply(event, String.format(BOT_PERM, config.getError(), p.getName(), "Voice Channel"));
                            return;
                        }
                    } else {
                        if (!event.getGuild().getSelfMember().hasPermission(event.getTextChannel(), p)) {
                            SenderUtil.reply(event, String.format(BOT_PERM, config.getError(), p.getName(), "Channel"));
                            return;
                        }
                    }
                } else {
                    if (!event.getGuild().getSelfMember().hasPermission(p)) {
                        SenderUtil.reply(event, String.format(BOT_PERM, config.getError(), p.getName(), "Guild"));
                        return;
                    }
                }
            }

            // user perms
            for (Permission p : userPermissions) {
                if (p.isChannel()) {
                    if (!event.getMember().hasPermission(event.getTextChannel(), p)) {
                        SenderUtil.reply(event, String.format(USER_PERM, config.getError(), p.getName(), "Channel"));
                        return;
                    }
                } else {
                    if (!event.getMember().hasPermission(p)) {
                        SenderUtil.reply(event, String.format(USER_PERM, config.getError(), p.getName(), "Guild"));
                        return;
                    }
                }
            }
        } else if (guildOnly) {
            SenderUtil.reply(event, config.getError() + " This command cannot be used in Direct messages");
            return;
        }

        // parse arguments
        Object[] parsedArgs = new Object[arguments.length];
        String workingSet = args;
        for (int i = 0; i < arguments.length; i++) {
            String separatorRegex = arguments[i].separator == null ? null : "(?i)\\s+" + arguments[i].separator.replace("|", "\\|") + "\\s+";
            if (workingSet == null) {
                if (arguments[i].required) {
                    SenderUtil.reply(event, String.format(config.getError() + " **Too few arguments provided**\nTry using `" + prefix + "help %s` for more information.", name));
                    return;
                } else continue;
            }
            switch (arguments[i].type) {
                case INTEGER:
                {
                    String[] parts = FormatUtil.cleanSplit(workingSet);
                    long num;
                    try {
                        num = Long.parseLong(parts[0]);
                        if (num < arguments[i].min || num > arguments[i].max) {
                            SenderUtil.reply(event, String.format(config.getError() + " **Invalid Value:**\n`%s` must be an integer between %s and %s", arguments[i].name, arguments[i].min, arguments[i].max));
                            return;
                        }
                    } catch (NumberFormatException e) {
                        SenderUtil.reply(event, String.format(config.getError() + " **Invalid Value:**\n`%s` must be an integer between %s and %s", arguments[i].name, arguments[i].min, arguments[i].max));
                        return;
                    }
                    parsedArgs[i] = num;
                    workingSet = parts[1];
                    break;
                }

                case SHORTSTRING:
                {
                    String[] parts = FormatUtil.cleanSplit(workingSet);
                    parsedArgs[i] = parts[0];
                    if (parts[0].length() < arguments[i].min || parts[0].length() > arguments[i].max) {
                        SenderUtil.reply(event, String.format(config.getError() + " **Invalid Value:**\n`%s` must be between %s and %s characters", arguments[i].name, arguments[i].min, arguments[i].max));
                        return;
                    }
                    workingSet = parts[1];
                    break;
                }

                case LONGSTRING:
                {
                    String[] parts;
                    if (separatorRegex == null) parts = new String[] {workingSet, null};
                    else parts = FormatUtil.cleanSplit(workingSet, separatorRegex);
                    if (parts[0].length() < arguments[i].min || parts[0].length() > arguments[i].max) {
                        SenderUtil.reply(event, String.format(config.getError() + " **Invalid Value:**\n`%s` must be between %s and %s characters", arguments[i].name, arguments[i].min, arguments[i].max));
                        return;
                    }
                    parsedArgs[i] = parts[0];
                    workingSet = parts[1];
                    break;
                }

                case TIME:
                {
                    String[] parts;
                    if (separatorRegex == null) parts = new String[] {workingSet, null};
                    else parts = FormatUtil.cleanSplit(workingSet, separatorRegex);
                    String timestr = parts[0].replaceAll("(?is)^((\\s*-?\\s*\\d+\\s*(d(ays?)?|h((ou)?rs?)?|m(in(ute)?s?)?|s(ec(ond)?s?)?)\\s*,?\\s*(and)?)*).*", "$1");
                    if (timestr.equals("")) {
                        SenderUtil.reply(event, String.format(config.getError() + " **Invalid Value:**\nNo amount of time could be parsed from \"%s\"", parts[0]));
                        return;
                    }
                    if (timestr.length() < parts[0].length()) {
                        parts[1] = workingSet.substring(timestr.length()).trim();
                        if (parts[1].equals("")) parts[1] = null;
                    }
                    timestr = timestr.replaceAll("(?i)(\\s|,|and)", "").replaceAll("(?is)(-?\\d+|[a-z]+)", "$1 ").trim();
                    String[] vals = timestr.split("\\s+");
                    long timeinseconds = 0;
                    try {
                        for (int j = 0; j < vals.length; j += 2) {
                            long num = Long.parseLong(vals[j]);
                            if (vals[j + 1].toLowerCase().startsWith("m")) num *= 60;
                            else if (vals[j + 1].toLowerCase().startsWith("h")) num *= 60 * 60;
                            else if (vals[j + 1].toLowerCase().startsWith("d")) num *= 60 * 60 * 24;
                            timeinseconds += num;
                        }
                    } catch (Exception e) {
                        SenderUtil.reply(event, String.format(config.getError() + " **Invalid Value:**\nNo amount of time could be parsed from \"%s\"", parts[0]));
                        return;
                    }
                    if (timeinseconds < arguments[i].min || timeinseconds > arguments[i].max) {
                        SenderUtil.reply(event, String.format(config.getError() + " **Invalid Value:**\n`%s` must be in at least %s, and no longer than %s", arguments[i].name, FormatUtil.secondsToTime(arguments[i].min), FormatUtil.secondsToTime(arguments[i].max)));
                        return;
                    }
                    parsedArgs[i] = timeinseconds;
                    workingSet = parts[1];
                    break;
                }

                case MEMBER:
                {
                    String[] parts;
                    if (separatorRegex == null) parts = new String[] {workingSet, null};
                    else parts = FormatUtil.cleanSplit(workingSet, separatorRegex);
                    if (parts[0].matches(FinderUtil.USER_MENTION + ".+")) {
                        parts[0] = parts[0].replaceAll("(" + FinderUtil.USER_MENTION + ").+", "$1");
                        parts[1] = workingSet.substring(parts[0].length()).trim();
                    }
                    List<Member> mlist = null;
                    if (event.getChannelType() != ChannelType.PRIVATE)
                        mlist = FinderUtil.findMembers(parts[0], event.getGuild());
                    if (mlist == null || mlist.isEmpty())
                        mlist = FinderUtil.findMembers(parts[0], event.getGuild());
                    if (mlist.isEmpty()) {
                        SenderUtil.reply(event, String.format(config.getWarning() + " **No %s found matching \"%s\"**", "members", parts[0]));
                        return;
                    } else if (mlist.size() > 1) {
                        SenderUtil.reply(event, FormatUtil.listOfMembers(mlist, parts[0]));
                        return;
                    }
                    parsedArgs[i] = mlist.get(0);
                    workingSet = parts[1];
                    break;
                }

                case USER:
                {
                    String[] parts;
                    if (separatorRegex == null) parts = new String[] {workingSet, null};
                    else parts = FormatUtil.cleanSplit(workingSet, separatorRegex);
                    if (parts[0].matches(FinderUtil.USER_MENTION + ".+")) {
                        parts[0] = parts[0].replaceAll("(" + FinderUtil.USER_MENTION + ").+", "$1");
                        parts[1] = workingSet.substring(parts[0].length()).trim();
                    }
                    List<User> ulist = null;
                    if (event.getChannelType() != ChannelType.PRIVATE)
                        ulist = FinderUtil.findUsers(parts[0], event.getJDA());
                    if (ulist == null || ulist.isEmpty())
                        ulist = FinderUtil.findUsers(parts[0], event.getJDA());
                    if (ulist.isEmpty()) {
                        SenderUtil.reply(event, String.format(config.getWarning() + " **No %s found matching \"%s\"**", "users", parts[0]));
                        return;
                    } else if (ulist.size() > 1) {
                        SenderUtil.reply(event, FormatUtil.listOfUsers(ulist, parts[0]));
                        return;
                    }
                    parsedArgs[i] = ulist.get(0);
                    workingSet = parts[1];
                    break;
                }

                case LOCALUSER:
                {
                    if (event.getChannelType() == ChannelType.PRIVATE) {
                        SenderUtil.reply(event, String.format(config.getError() + " **Invalid Value:**\n`%s` cannot be included via Direct Message", arguments[i].name));
                        return;
                    }
                    String[] parts;
                    if (separatorRegex == null) parts = new String[] {workingSet, null};
                    else parts = FormatUtil.cleanSplit(workingSet, separatorRegex);
                    if (parts[0].matches(FinderUtil.USER_MENTION + ".+")) {
                        parts[0] = parts[0].replaceAll("(" + FinderUtil.USER_MENTION + ").+", "$1");
                        parts[1] = workingSet.substring(parts[0].length()).trim();
                    }
                    List<User> ulist = FinderUtil.findUsers(parts[0], event.getJDA());
                    if (ulist.isEmpty()) {
                        SenderUtil.reply(event, String.format(config.getWarning() + " **No %s found matching \"%s\"**", "users", parts[0]));
                        return;
                    } else if (ulist.size() > 1) {
                        SenderUtil.reply(event, FormatUtil.listOfUsers(ulist, parts[0]));
                        return;
                    }
                    parsedArgs[i] = ulist.get(0);
                    workingSet = parts[1];
                    break;
                }

                case BANNEDUSER:
                {
                    if (event.getChannelType() == ChannelType.PRIVATE) {
                        SenderUtil.reply(event, String.format(config.getError() + " **Invalid Value:**\n`%s` cannot be included via Direct Message", arguments[i].name));
                        return;
                    }
                    String[] parts;
                    if (separatorRegex == null) parts = new String[] {workingSet, null};
                    else parts = FormatUtil.cleanSplit(workingSet, separatorRegex);
                    if (parts[0].matches(FinderUtil.USER_MENTION + ".+")) {
                        parts[0] = parts[0].replaceAll("(" + FinderUtil.USER_MENTION + ").+", "$1");
                        parts[1] = workingSet.substring(parts[0].length()).trim();
                    }
                    List<User> ulist = FinderUtil.findBannedUsers(parts[0], event.getGuild());
                    if (ulist == null) {
                        SenderUtil.reply(event, config.getError() + "I cannot check the list of banned users.");
                        return;
                    }
                    if (ulist.isEmpty()) {
                        SenderUtil.reply(event, String.format(config.getWarning() + " **No %s found matching \"%s\"**", "users", parts[0]));
                        return;
                    } else if (ulist.size() > 1) {
                        SenderUtil.reply(event, FormatUtil.listOfUsers(ulist, parts[0]));
                        return;
                    }
                    parsedArgs[i] = ulist.get(0);
                    workingSet = parts[1];
                    break;
                }

                case TEXTCHANNEL:
                {
                    if (event.getChannelType() == ChannelType.PRIVATE) {
                        SenderUtil.reply(event, String.format(config.getError() + " **Invalid Value:**\n`%s` cannot be included via Direct Message", arguments[i].name));
                        return;
                    }
                    String[] parts = FormatUtil.cleanSplit(workingSet);
                    List<TextChannel> tclist = FinderUtil.findTextChannels(parts[0], event.getGuild());
                    if (tclist.isEmpty()) {
                        SenderUtil.reply(event, String.format(config.getWarning() + " **No %s found matching \"%s\"**", "text channels", parts[0]));
                        return;
                    } else if (tclist.size() > 1) {
                        SenderUtil.reply(event, FormatUtil.listOfChannels(tclist, parts[0]));
                        return;
                    }
                    parsedArgs[i] = tclist.get(0);
                    workingSet = parts[1];
                    break;
                }

                case ROLE:
                {
                    if (event.getChannelType() == ChannelType.PRIVATE) {
                        SenderUtil.reply(event, String.format(config.getError() + " **Invalid Value:**\n`%s` cannot be included via Direct Message", arguments[i].name));
                        return;
                    }
                    String[] parts;
                    if (separatorRegex == null) parts = new String[] {workingSet, null};
                    else parts = FormatUtil.cleanSplit(workingSet, separatorRegex);
                    List<Role> rlist = FinderUtil.findRoles(parts[0], event.getGuild());
                    if (rlist.isEmpty()) {
                        SenderUtil.reply(event, String.format(config.getWarning() + " **No %s found matching \"%s\"**", "roles", parts[0]));
                        return;
                    } else if (rlist.size() > 1) {
                        SenderUtil.reply(event, FormatUtil.listOfRoles(rlist, parts[0]));
                        return;
                    }
                    parsedArgs[i] = rlist.get(0);
                    workingSet = parts[1];
                    break;
                }
                case GUILD:
                {
                    String[] parts;
                    if (separatorRegex == null) parts = new String[] {workingSet, null};
                    else parts = FormatUtil.cleanSplit(workingSet, separatorRegex);
                    List<Guild> glist = OtherUtil.findGuild(parts[0], event.getJDA());
                    if (glist.isEmpty()) {
                        SenderUtil.reply(event, String.format(config.getWarning() + " **No %s found matching \"%s\"**", "servers", parts[0]));
                        return;
                    } else if (glist.size() > 1) {
                        SenderUtil.reply(event, FormatUtil.listOfGuilds(glist, parts[0]));
                        return;
                    }
                    parsedArgs[i] = glist.get(0);
                    workingSet = parts[1];
                    break;
                }
            }
        }

        // run
        try {
            execute(parsedArgs, event);
        } catch (Throwable t) {
            throw t;
        }
    }

    public boolean isCommandFor(String input) {
        if (name.equalsIgnoreCase(input)) return true;
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(input)) return true;
        }
        return false;
    }

    public boolean isAllowed(TextChannel channel) {
        if (!usesTopicTags) return true;
        if (channel == null) return true;
        String topic = channel.getTopic();
        if (topic == null || topic.isEmpty()) return true;
        topic = topic.toLowerCase();
        String lowerName = name.toLowerCase();
        if (topic.contains("{" + lowerName + "}")) return true;
        if (topic.contains("{-" + lowerName + "}")) return false;
        String lowerCat = category == null ? null : category.getName().toLowerCase();
        if (lowerCat != null) {
            if (topic.contains("{" + lowerCat + "}")) return true;
            if (topic.contains("{-" + lowerCat + "}")) return false;
        }
        return !topic.contains("{-all}");
    }

    // Category class
    public static class Category {
        @Getter
        private final String name;
        @Getter
        private final String failureResponse;
        private final Predicate<MessageReceivedEvent> predicate;

        public Category(String name) {
            this.name = name;
            this.failureResponse = null;
            this.predicate = null;
        }

        public Category(String name, Predicate<MessageReceivedEvent> predicate) {
            this.name = name;
            this.failureResponse = null;
            this.predicate = predicate;
        }

        public Category(String name, String failureResponse, Predicate<MessageReceivedEvent> predicate) {
            this.name = name;
            this.failureResponse = failureResponse;
            this.predicate = predicate;
        }

        public boolean test(MessageReceivedEvent event) {
            return predicate == null || predicate.test(event);
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Category)) return false;
            Category other = (Category) obj;
            return Objects.equals(name, other.name) && Objects.equals(predicate, other.predicate) && Objects.equals(failureResponse, other.failureResponse);
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + Objects.hashCode(this.name);
            hash = 17 * hash + Objects.hashCode(this.failureResponse);
            hash = 17 * hash + Objects.hashCode(this.predicate);
            return hash;
        }
    }
}
