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
package com.sharif.thunder.commands.utilities;

import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.UtilitiesCommand;
import com.sharif.thunder.utils.SenderUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AvatarCommand extends UtilitiesCommand {

  private final Thunder thunder;
  private static Member member;

  public AvatarCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "avatar";
    this.help = "Gets a user's avatar";
    this.aliases = new String[] {"ava"};
    this.arguments = new Argument[] {new Argument("user", Argument.Type.MEMBER, false)};
  }

  @Override
  public void execute(Object[] args, MessageReceivedEvent event) {
    try {
      member = (Member) args[0];
      if (member == null) {
        member = event.getMember();
      }
      event.getChannel().sendMessage(thunder.getConfig().getSearching() + " Please wait...").queue(message -> {
        message.delete().queue();
        EmbedBuilder eb = new EmbedBuilder()
                .setAuthor(member.getUser().getName() + "'s avatar")
                .setColor(member.getColor())
                .setImage(member.getUser().getEffectiveAvatarUrl() + "?size=2048");
        event.getChannel().sendMessage(eb.build()).queue();
      });
    } catch (Exception e) {
      SenderUtil.replyError(event, "Something went wrong: `" + e.getMessage() + "` please try again later!");
    }
  }
}
