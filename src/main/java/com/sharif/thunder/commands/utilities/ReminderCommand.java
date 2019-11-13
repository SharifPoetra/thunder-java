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

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.commands.UtilitiesCommand;
import com.sharif.thunder.datasources.Reminders;
import com.sharif.thunder.utils.FormatUtil;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import net.dv8tion.jda.api.entities.ChannelType;

public class ReminderCommand extends UtilitiesCommand {

  private final Reminders reminders;

  public ReminderCommand(Reminders reminders) {
    this.reminders = reminders;
    this.name = "reminder";
    this.help = "sets a reminder";
    this.aliases = new String[] {"remind", "remindme"};
    this.cooldown = 30;
    this.arguments = "<time> <message>";
    this.children = new Command[] {new RemindList(), new RemindRemove()};
  }

  @Override
  protected void execute(CommandEvent event) {
    long seconds = (long) Long.parseLong(event.getArgs().split(" ")[0]);
    String message = (String) event.getArgs().split(" ")[1];
    String[] reminder = new String[reminders.getSize()];
    reminder[Reminders.USERID] = event.getAuthor().getId();
    reminder[Reminders.CHANNELID] =
        event.getChannelType() == ChannelType.PRIVATE ? "private" : event.getTextChannel().getId();
    reminder[Reminders.EXPIRETIME] =
        (OffsetDateTime.now().toInstant().toEpochMilli() + (seconds * 1000)) + "";
    reminder[Reminders.MESSAGE] =
        message + "  ~set " + OffsetDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
    reminders.set(reminder);
    event.replySuccess("Reminder set to expire in " + FormatUtil.secondsToTime(seconds));
  }

  private class RemindList extends Command {
    private RemindList() {
      this.name = "list";
      this.help = "shows a list of your current reminders";
    }

    @Override
    protected void execute(CommandEvent event) {
      List<String[]> list = reminders.getRemindersForUser(event.getAuthor().getId());
      if (list.isEmpty()) {
        event.replyWarning("You don't have any reminders set!");
        return;
      }
      StringBuilder builder = new StringBuilder("**" + list.size() + "** reminders:");
      for (int i = 0; i < list.size(); i++) {
        builder.append("\n`").append(i).append(".` ");
        String channelid = list.get(i)[Reminders.CHANNELID];
        builder.append(
            channelid.equals("private") ? "Direct Message - \"" : "<#" + channelid + "> - \"");
        String message =
            list.get(i)[Reminders.MESSAGE].length() > 20
                ? list.get(i)[Reminders.MESSAGE].substring(0, 20) + "..."
                : list.get(i)[Reminders.MESSAGE];
        builder
            .append(message)
            .append("\" in ")
            .append(
                FormatUtil.secondsToTime(
                    (Long.parseLong(list.get(i)[Reminders.EXPIRETIME])
                            - OffsetDateTime.now().toInstant().toEpochMilli())
                        / 1000));
      }
      event.replySuccess(builder.toString());
    }
  }

  private class RemindRemove extends Command {
    private RemindRemove() {
      this.name = "remove";
      this.aliases = new String[] {"delete", "cancel", "clear"};
      this.help = "cancels a reminder from the list";
      this.arguments = "<index>";
    }

    @Override
    protected void execute(CommandEvent event) {
      long index = (long) Long.parseLong(event.getArgs().split(" ")[0]);
      List<String[]> list = reminders.getRemindersForUser(event.getAuthor().getId());
      if (list.isEmpty()) {
        event.replyError("You cannot clear a reminder because you do not have any set!");
        return;
      }
      if (index + 1 > list.size()) {
        event.replyError(
            "There is no reminder at that index! Please enter a number 0 - "
                + (list.size() - 1)
                + ", or try `"
                + "~"
                + "remind list`");
        return;
      }
      reminders.removeReminder(list.get((int) index));
      event.replySuccess("You have removed a reminder.");
    }
  }
}
