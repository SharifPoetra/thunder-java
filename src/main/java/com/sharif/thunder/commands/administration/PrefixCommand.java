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
package com.sharif.thunder.commands.administration;

import com.sharif.thunder.commands.AdministrationCommand;
import com.sharif.thunder.commands.Command;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.utils.SenderUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class PrefixCommand extends AdministrationCommand {

  private static Thunder thunder;

  public PrefixCommand(Thunder thunder) {
    PrefixCommand.thunder = thunder;
    this.name = "prefix";
    this.help = "Set the prefix for the bot";
    this.aliases = new String[] {"setprefix"};
    this.arguments = new Argument[] {
      new Argument("newprefix", Argument.Type.SHORTSTRING, true)
    };
    this.userPermissions = new Permission[] {Permission.ADMINISTRATOR};
    this.children = new Command[] {new ResetPrefixCommand()};
  }
  
  @Override
  protected void execute(Object[] args, MessageReceivedEvent event) {
    try {
      String newprefix = (String) args[0];
      Thunder.getDatabase().guildSettings.setPrefix(event.getGuild(), newprefix);
      SenderUtil.replySuccess(event, "Successfully changed the prefix for this server to `" + Thunder.getDatabase().guildSettings.getSettings(event.getGuild()).getPrefix() + "`");
    } catch (Exception ex) {
      System.out.println("Something went wrong while trying to set server prefix " + ex);
      SenderUtil.replyError(event, "Something went wrong while trying to set this server prefix, Please try again later!");
    }
  }
  
  private class ResetPrefixCommand extends AdministrationCommand {

    private ResetPrefixCommand() {
      this.name = "reset";
      this.help = "Reset the prefix the bot to default";
      this.userPermissions = new Permission[] {Permission.ADMINISTRATOR};
    }
    
    @Override
    protected void execute(Object[] args, MessageReceivedEvent event) {
      try {
        Thunder.getDatabase().guildSettings.setPrefix(event.getGuild(), thunder.getConfig().getPrefix());
        SenderUtil.replySuccess(event, "The prefix has been reset to default `" + thunder.getConfig().getPrefix() + "`");
      } catch (Exception ex) {
        System.out.println("Something went wrong while trying to reset server prefix " + ex);
        SenderUtil.replyError(event, "Something went wrong while trying to reset this server prefix, Please try again later!");
      }
    }
  }
}
