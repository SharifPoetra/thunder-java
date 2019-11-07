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

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.FunCommand;

public class ChooseCommand extends FunCommand {

  private final Thunder thunder;

  public ChooseCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "choose";
    this.help = "make a decision.";
    this.arguments = "<item> <item> ...";
  }

  @Override
  protected void execute(CommandEvent event) {

    if (event.getArgs().isEmpty()) {
      event.replyWarning("You didn't give me a choices!");
    } else {
      String[] items = event.getArgs().split("\\s+");
      if (items.length == 1) {
        event.replyWarning("You only gave me one option, `" + items[0] + "`");
      } else {
        event.replySuccess("I choose `" + items[(int) (Math.random() * items.length)] + "`");
      }
    }
  }
}
