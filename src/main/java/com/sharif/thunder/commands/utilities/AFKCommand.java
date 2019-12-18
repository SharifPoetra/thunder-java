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
package com.sharif.thunder.commands.utilities;

import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.UtilitiesCommand;
import com.sharif.thunder.datasources.AFKs;
import com.sharif.thunder.utils.SenderUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class AFKCommand extends UtilitiesCommand {
  private final AFKs afks;

  public AFKCommand(AFKs afks) {
    this.afks = afks;
    this.name = "afk";
    this.help = "relays mentions via DM; can autoreply message.";
    this.arguments = new Argument[] {new Argument("message", Argument.Type.LONGSTRING, false)};
  }

  @Override
  protected void execute(Object[] args, MessageReceivedEvent event) {
    String message = (String) args[0];
    afks.set(new String[] {event.getAuthor().getId(), message});
    SenderUtil.reply(event, "⌨️ | " + event.getAuthor().getAsMention() + ", I've set you to AFK mode.");
  }
}
