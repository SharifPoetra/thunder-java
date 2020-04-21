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
package com.sharif.thunder.commands.owner;

import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.OwnerCommand;
import com.sharif.thunder.utils.SenderUtil;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class BotStatusCommand extends OwnerCommand {

  public BotStatusCommand() {
    this.name = "botstatus";
    this.help = "Set the game I'm currently playing";
    this.arguments = new Argument[] {new Argument("status", Argument.Type.LONGSTRING, true)};
    this.hidden = true;
  }

  @Override
  public void execute(Object[] args, MessageReceivedEvent event) {
    String status = (String) args[0];
    event.getJDA().getPresence().setActivity(Activity.playing(status));
    SenderUtil.replySuccess(event, "Successfully set my current playing status to " + status);
  }
}
