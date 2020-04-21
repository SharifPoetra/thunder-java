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

import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.OwnerCommand;
import com.sharif.thunder.utils.SenderUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class RestartCommand extends OwnerCommand {

  private final Thunder thunder;

  public RestartCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "restart";
    this.help = "Kills the current instance and launches a fresh instance of this bot.";
    this.hidden = true;
  }

  @Override
  protected void execute(Object[] args, MessageReceivedEvent event) {
    SenderUtil.replySuccess(event, "Restarting the bot one moment...");
    System.exit(11);
  }
}
