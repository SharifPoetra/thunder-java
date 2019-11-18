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

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.InteractionCommand;
import com.sharif.thunder.utils.*;
import java.util.*;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

public class CryCommand extends InteractionCommand {
  private final Thunder thunder;
  private String[] msg = {
    "is crying... :c", "needs a hug...", "is crying... there there...", "cries... :'c"
  };

  public CryCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "cry";
    this.help = "Express your emotions.";
    this.botPermissions =
        new Permission[] {Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_EMBED_LINKS};
  }

  @Override
  public void execute(CommandEvent event) {
    try {
      event
          .getChannel()
          .sendMessage("Please wait...")
          .queue(
              message -> {
                Map<String, String> headers = new HashMap<>();
                headers.put("authorization", "Bearer " + thunder.getConfig().getEmiliaKey());
                byte[] image = UnirestUtil.getBytes("https://emilia.shrf.xyz/api/cry", headers);
                List<Member> list = FinderUtil.findMembers(event.getArgs(), event.getGuild());
                message.delete().submit();
                event
                    .getChannel()
                    .sendFile(image, "cry.gif")
                    .embed(
                        new EmbedBuilder()
                            .setAuthor(
                                event.getMember().getUser().getName()
                                    + " "
                                    + RandomUtil.randomElement(msg),
                                null,
                                event.getAuthor().getEffectiveAvatarUrl())
                            .setColor(event.getSelfMember().getColor())
                            .setImage("attachment://cry.gif")
                            .build())
                    .queue();
              });
    } catch (IllegalArgumentException ex) {
      event.replyError("Shomething went wrong while fetching the API! Please try again.");
      System.out.println(ex);
    }
  }
}
