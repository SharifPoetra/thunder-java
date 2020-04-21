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
package com.sharif.thunder.commands.administration;

import com.sharif.thunder.commands.AdministrationCommand;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.Command;
import com.sharif.thunder.datasources.InVCRoles;
import com.sharif.thunder.utils.SenderUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SetInVCRoleCommand extends AdministrationCommand {

  private final InVCRoles inVcRoles;

  public SetInVCRoleCommand(InVCRoles inVcRoles) {
    this.inVcRoles = inVcRoles;
    this.name = "setinvcrole";
    this.help = "set the role to be given to member when they're joining the voice channel.";
    this.arguments = new Argument[] {
        new Argument("role", Argument.Type.ROLE, true)
    };
    this.aliases = new String[] {"invcrole", "sivcr"};
    this.botPermissions = new Permission[] {Permission.MANAGE_ROLES};
    this.userPermissions = new Permission[] {Permission.ADMINISTRATOR};
    this.children = new Command[] {new DisableCommand()};
  }

  @Override
  protected void execute(Object[] args, MessageReceivedEvent event) {
    Role role = (Role) args[0];
    inVcRoles.set(new String[] {event.getGuild().getId(), role.getId()});
    SenderUtil.replySuccess(event, "The member will be given `" + role.getName() + "` role when they're joining a voice channel.");
  }

  private class DisableCommand extends AdministrationCommand {

    private DisableCommand() {
      this.name = "disable";
      this.help = "disable the role to be given to member when they're joining the voice channel.";
      this.botPermissions = new Permission[] {Permission.MANAGE_ROLES};
      this.userPermissions = new Permission[] {Permission.ADMINISTRATOR};
    }

    @Override
    protected void execute(Object[] args, MessageReceivedEvent event) {
      if (!inVcRoles.has(event.getGuild().getId())) {
        SenderUtil.replyWarning(event, "There's no in voice role set.");
        return;
      } else {
        inVcRoles.remove(event.getGuild().getId());
        SenderUtil.replySuccess(event, "Voice join role cleared.");
      }
    }
  }
}