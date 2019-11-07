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

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.sharif.thunder.commands.AdministrationCommand;
import com.sharif.thunder.datasources.InVCRoles;
import com.sharif.thunder.utils.FormatUtil;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;

public class SetInVCRoleCommand extends AdministrationCommand {

  private final InVCRoles inVcRoles;

  public SetInVCRoleCommand(InVCRoles inVcRoles) {
    this.inVcRoles = inVcRoles;
    this.name = "setinvcrole";
    this.help = "set the role to be given to member when a they're joining the voice channel.";
    this.arguments = "<rolename|NONE>";
    this.aliases = new String[] {"invcrole", "sivcr"};
    this.botPermissions = new Permission[] {Permission.MANAGE_ROLES};
    this.userPermissions = new Permission[] {Permission.ADMINISTRATOR};
  }

  @Override
  protected void execute(CommandEvent event) {
    if (event.getArgs().isEmpty()) {
      event.replyError("Please include a role name or NONE");
      return;
    }
    if (event.getArgs().equalsIgnoreCase("none")) {
      inVcRoles.remove(event.getGuild().getId());
      event.replySuccess("Voice join role cleared.");
    } else {
      List<Role> list = FinderUtil.findRoles(event.getArgs(), event.getGuild());
      if (list.isEmpty()) event.replyWarning("No Roles found matching \"" + event.getArgs() + "\"");
      else if (list.size() > 1) event.replyWarning(FormatUtil.listOfRoles(list, event.getArgs()));
      else {
        inVcRoles.set(new String[] {event.getGuild().getId(), list.get(0).getId()});
        event.replySuccess(
            "The member will be given **"
                + list.get(0).getName()
                + "** role when they're joining a voice channel.");
      }
    }
  }
}
