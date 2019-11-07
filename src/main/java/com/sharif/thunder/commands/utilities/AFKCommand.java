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

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.commands.UtilitiesCommand;
import com.sharif.thunder.datasources.AFKs;

public class AFKCommand extends UtilitiesCommand {
  private final AFKs afks;

  public AFKCommand(AFKs afks) {
    this.afks = afks;
    this.name = "afk";
    this.help = "relays mentions via DM; can autoreply message.";
    this.arguments = "[message]";
  }

  @Override
  protected void execute(CommandEvent event) {
    String message = event.getArgs() == null ? null : event.getArgs();
    afks.set(new String[] {event.getAuthor().getId(), message});
    event.reply("⌨️ | " + event.getAuthor().getAsMention() + ", I've set you to AFK mode.");
  }
}
