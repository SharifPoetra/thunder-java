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
package com.sharif.thunder.commands.`fun`

import com.sharif.thunder.Thunder
import com.sharif.thunder.commands.Argument
import com.sharif.thunder.commands.FunCommand
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class SayCommand(private val thunder: Thunder) : FunCommand() {
    init {
        name = "say"
        help = "let the bot copy and resend your message."
        aliases = arrayOf("echo")
        arguments = arrayOf(Argument("text", Argument.Type.LONGSTRING, true))
        botPermissions = arrayOf(Permission.MESSAGE_MANAGE)
    }

    override fun execute(args: Array<Any>, event: MessageReceivedEvent) {
        var msg = args[0] as String
        if (event.member!!.hasPermission(Permission.MESSAGE_MANAGE)) {
            event.message.delete().queue()
        }
        if (!event.member!!.hasPermission(Permission.MESSAGE_MENTION_EVERYONE)) {
            msg = msg.replace("@everyone", "@\u200beveryone").replace("@here", "@\u200bhere")
        }
        event.channel.sendMessage(msg).queue()
    }
}
