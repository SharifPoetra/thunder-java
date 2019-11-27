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

import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.OwnerCommand;
import com.sharif.thunder.utils.SenderUtil;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvalCommand extends OwnerCommand {

  private final Thunder thunder;

  public EvalCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "eval";
    this.help = "evaluates nashorn code";
    this.arguments = new Argument[] {new Argument("code", Argument.Type.LONGSTRING, true)};
    this.aliases = new String[] {"e"};
    this.hidden = true;
  }

  @Override
  protected void execute(Object[] args, MessageReceivedEvent event) {
    Logger log = LoggerFactory.getLogger(EvalCommand.class);
    String toEval = (String) args[0];
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("Nashorn");
    try {
      engine.put("bot", thunder);
      engine.put("event", event);
      engine.put("jda", event.getJDA());
      engine.put("guild", event.getGuild());
      engine.put("channel", event.getChannel());
      engine.put("log", log);
      SenderUtil.replySuccess(
          event, "Evaluated Successfully:\n```\n" + engine.eval(toEval) + " ```");
    } catch (Exception e) {
      SenderUtil.replyError(event, "An exception was thrown:\n```\n" + e.getMessage() + " ```");
    }
  }
}
