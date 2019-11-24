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
package com.sharif.thunder.commands.interaction;

import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.InteractionCommand;
import com.sharif.thunder.utils.*;
import java.util.*;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PatCommand extends InteractionCommand {
  private final Thunder thunder;
  private String text;
  private String[] msg = {"There there!", "Adorable~", "*pat pat*"};

  public PatCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "pat";
    this.help = "Pats the specified user.";
    this.arguments = new Argument[] {new Argument("user", Argument.Type.USER, true)};
    this.botPermissions =
        new Permission[] {Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS};
  }

  @Override
  public void execute(Object[] args, MessageReceivedEvent event) {
    try {
      User user = (User) args[0];
      event
          .getChannel()
          .sendMessage("Please wait...")
          .queue(
              message -> {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", "Bearer " + thunder.getConfig().getEmiliaKey());
                byte[] image = UnirestUtil.getBytes("https://emilia.shrf.xyz/api/pat", headers);
                message.delete().queue();
                event
                    .getChannel()
                    .sendFile(image, "pat.gif")
                    .embed(
                        new EmbedBuilder()
                            .setAuthor(
                                event.getAuthor().getName()
                                    + " pets "
                                    + user.getName()
                                    + "! "
                                    + RandomUtil.randomElement(msg),
                                null,
                                event.getAuthor().getEffectiveAvatarUrl())
                            .setColor(event.getGuild().getSelfMember().getColor())
                            .setImage("attachment://pat.gif")
                            .build())
                    .queue();
              });
    } catch (IllegalArgumentException ex) {
      SenderUtil.replyError(
          event, "Shomething went wrong while fetching the API! Please try again.");
      System.out.println(ex);
    }
  }
}
