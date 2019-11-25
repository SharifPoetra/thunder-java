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
package com.sharif.thunder.commands.fun;

import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.FunCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SayCommand extends FunCommand {
  private final Thunder thunder;

  public SayCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "say";
    this.help = "let the bot copy and resend your message.";
    this.aliases = new String[] {"echo"};
    this.arguments = new Argument[] {new Argument("text", Argument.Type.LONGSTRING, true)};
    this.botPermissions = new Permission[] {Permission.MESSAGE_MANAGE};
  }

  @Override
  protected void execute(Object[] args, MessageReceivedEvent event) {
    String msg = (String) args[0];
    if (event.getMember().hasPermission(Permission.MESSAGE_MANAGE)) {
      event.getMessage().delete().queue();
    }
    if (!event.getMember().hasPermission(Permission.MESSAGE_MENTION_EVERYONE)) {
      msg = msg.replace("@everyone", "@\u200beveryone").replace("@here", "@\u200bhere");
    }
    event.getChannel().sendMessage(msg).queue();
  }
}
