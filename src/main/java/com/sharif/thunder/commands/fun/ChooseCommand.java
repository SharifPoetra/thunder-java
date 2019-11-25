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
import com.sharif.thunder.utils.SenderUtil;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class ChooseCommand extends FunCommand {

  private final Thunder thunder;

  public ChooseCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "choose";
    this.help = "make a decision.";
    this.arguments = new Argument[] {new Argument("items", Argument.Type.LONGSTRING, true)};
  }

  @Override
  protected void execute(Object[] args, MessageReceivedEvent event) {
    String items = (String) args[0];
    String[] item = items.split(" ");
    if (item.length == 1) {
      SenderUtil.replyWarning(event, "You only gave me one option, `" + item[0] + "`");
    } else {
      SenderUtil.replySuccess(
          event, "I choose `" + item[(int) (Math.random() * item.length)] + "`");
    }
  }
}
