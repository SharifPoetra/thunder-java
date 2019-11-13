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
package com.sharif.thunder.datasources;

import com.sharif.thunder.DataSource;
import com.sharif.thunder.utils.SenderUtil;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

public class Reminders extends DataSource {

  public Reminders() {
    this.filename = "discordbot.reminders";
    this.size = 4;
    this.generateKey = item -> item[USERID] + "|" + item[CHANNELID] + "|" + item[EXPIRETIME];
  }

  public List<String[]> getRemindersForUser(String userId) {
    ArrayList<String[]> list = new ArrayList<>();
    synchronized (data) {
      data.values().stream()
          .filter((item) -> (item[USERID].equals(userId)))
          .forEach(
              (item) -> {
                list.add(item.clone());
              });
    }
    return list;
  }

  public List<String[]> getExpiredReminders() {
    long now = OffsetDateTime.now().toInstant().toEpochMilli();
    ArrayList<String[]> list = new ArrayList<>();
    synchronized (data) {
      for (String[] item : data.values())
        if (now > Long.parseLong(item[EXPIRETIME])) list.add(item.clone());
    }
    return list;
  }

  public void removeReminder(String[] reminder) {
    remove(generateKey.apply(reminder));
  }

  public void checkReminders(JDA jda) {
    if (jda.getStatus() != JDA.Status.CONNECTED) return;
    List<String[]> list = getExpiredReminders();
    list.stream()
        .map(
            (item) -> {
              removeReminder(item);
              return item;
            })
        .forEach(
            (item) -> {
              TextChannel chan = jda.getTextChannelById(item[Reminders.CHANNELID]);
              System.out.println(chan);
              if (chan == null) {
                User user = jda.getUserById(item[Reminders.USERID]);
                if (user != null) SenderUtil.sendDM(user, "\u23F0 " + item[Reminders.MESSAGE]);
                System.out.println(user);
              } else {
                SenderUtil.sendMsg(
                    chan,
                    "\u23F0 <@" + item[Reminders.USERID] + "> \u23F0 " + item[Reminders.MESSAGE]);
              }
            });
  }

  public static final int USERID = 0;
  public static final int CHANNELID = 1;
  public static final int EXPIRETIME = 2;
  public static final int MESSAGE = 3;
}
