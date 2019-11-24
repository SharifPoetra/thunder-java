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
package com.sharif.thunder.utils;

import com.sharif.thunder.BotConfig;
import java.util.ArrayList;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SenderUtil {
  private static final BotConfig config = new BotConfig();

  public static void sendDM(User user, String message) {
    try {
      user.openPrivateChannel().queue(pc -> pc.sendMessage(message).queue());
    } catch (Exception ignore) {
    }
  }

  public static void reply(MessageReceivedEvent event, String message) {
    try {
      event.getChannel().sendMessage(message).queue();
    } catch (Exception ignore) {
    }
  }

  public static void replySuccess(MessageReceivedEvent event, String message) {
    try {
      event.getChannel().sendMessage(config.getSuccess() + " " + message).queue();
    } catch (Exception ignore) {
    }
  }

  public static void replyWarning(MessageReceivedEvent event, String message) {
    try {
      event.getChannel().sendMessage(config.getWarning() + " " + message).queue();
    } catch (Exception ignore) {
    }
  }

  public static void replyError(MessageReceivedEvent event, String message) {
    try {
      event.getChannel().sendMessage(config.getWarning() + " " + message).queue();
    } catch (Exception ignore) {
    }
  }

  // send help (warn if can't send)
  public static void sendHelp(MessageReceivedEvent event, String message) {
    ArrayList<String> bits = splitMessage(message);
    for (int i = 0; i < bits.size(); i++) {
      event.getChannel().sendMessage(bits.get(i)).queue();
    }
  }

  private static ArrayList<String> splitMessage(String stringtoSend) {
    ArrayList<String> msgs = new ArrayList<>();
    if (stringtoSend != null) {
      stringtoSend =
          stringtoSend
              .replace("@everyone", "@\u200Beveryone")
              .replace("@here", "@\u200Bhere")
              .trim();
      while (stringtoSend.length() > 2000) {
        int leeway = 2000 - (stringtoSend.length() % 2000);
        int index = stringtoSend.lastIndexOf("\n", 2000);
        if (index < leeway) index = stringtoSend.lastIndexOf(" ", 2000);
        if (index < leeway) index = 2000;
        String temp = stringtoSend.substring(0, index).trim();
        if (!temp.equals("")) msgs.add(temp);
        stringtoSend = stringtoSend.substring(index).trim();
      }
      if (!stringtoSend.equals("")) msgs.add(stringtoSend);
    }
    return msgs;
  }
}
