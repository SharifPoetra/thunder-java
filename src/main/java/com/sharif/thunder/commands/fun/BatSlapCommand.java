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
package com.sharif.thunder.commands.fun;

import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.FunCommand;
import com.sharif.thunder.utils.NetworkUtil;
import com.sharif.thunder.utils.SenderUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BatSlapCommand extends FunCommand {

  private final Thunder thunder;

  public BatSlapCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "batslap";
    this.help = "Slap someone with batslap template.";
    this.arguments = new Argument[] {new Argument("user", Argument.Type.USER, true)};
  }

  public void execute(Object[] args, MessageReceivedEvent event) {
    try {
      event.getChannel().sendTyping().queue();
      User user = (User) args[0];
      byte[] data = NetworkUtil.download("https://emilia.shrf.xyz/api/batslap?slapper=" + event.getAuthor().getEffectiveAvatarUrl() + "&slapped=" + user.getEffectiveAvatarUrl(), "Bearer " + thunder.getConfig().getEmiliaKey());
      event.getChannel().sendFile(data, "batslap.png").embed(new EmbedBuilder()
        .setAuthor(user.getName() + " has been batslapped by " + event.getAuthor().getName(), null, null)
        .setColor(event.getMember().getColor())
        .setImage("attachment://batslap.png")
        .build()).queue();
    } catch (Exception ex) {
      SenderUtil.replyError(event, "Shomething went wrong while fetching the API! Please try again.");
      System.out.println(ex);
    }
  }
}
