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
package com.sharif.thunder.commands.owner;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.commands.OwnerCommand;
import net.dv8tion.jda.api.entities.Activity;

public class BotStatusCommand extends OwnerCommand {

  public BotStatusCommand() {
    this.name = "botstatus";
    this.help = "Set the game I'm currently playing";
    this.arguments = "<status>";
    this.hidden = true;
  }

  @Override
  public void execute(CommandEvent event) {
    String args = event.getArgs();
    if (args.isEmpty()) {
      event.replyError("You must specify what my status to be!");
      return;
    }
    event.getJDA().getPresence().setActivity(Activity.playing(args));
    event.replySuccess("Successfully set my current playing status to " + args);
  }
}
