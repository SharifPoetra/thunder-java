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
package com.sharif.thunder.commands.`fun`
import com.sharif.thunder.Thunder
import com.sharif.thunder.commands.Argument
import com.sharif.thunder.commands.FunCommand
import com.sharif.thunder.utils.SenderUtil
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class ChooseCommand(thunder: Thunder) : FunCommand() {
    private val thunder: Thunder
    init {
        this.thunder = thunder
        this.name = "choose"
        this.help = "make a decision."
        this.arguments = arrayOf<Argument>(Argument("items", Argument.Type.LONGSTRING, true))
    }

    protected override fun execute(args: Array<Any>, event: MessageReceivedEvent) {
    val items = args[0] as String
    val item = items.split((" ").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
    if (item.size == 1) {
    SenderUtil.replyWarning(event, "You only gave me one option, `" + item[0] + "`")
    } else {
    SenderUtil.replySuccess(event, "I choose `" + item[(Math.random() * item.size).toInt()] + "`")
    }
        }
}
