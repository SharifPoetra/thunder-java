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
import com.sharif.thunder.utils.NetworkUtil
import com.sharif.thunder.utils.SenderUtil
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

class BobRossCommand(private val thunder: Thunder) : FunCommand() {
    init {
        name = "bobross"
        help = "Draws a user's avatar over 'Bob Ross' canvas."
        arguments = arrayOf(Argument("user", Argument.Type.USER, true))
    }

    public override fun execute(args: Array<Any>, event: MessageReceivedEvent) {
        try {
            event.channel.sendTyping().queue()
            val user = args[0] as User
            val data = NetworkUtil.download("https://emilia-api.xyz/api/bob-ross?image=" + user.effectiveAvatarUrl, "Bearer " + thunder.config.emiliaKey)
            event.channel.sendFile(data, "bobross.png").embed(
                EmbedBuilder()
                    .setColor(event.member!!.color)
                    .setImage("attachment://bobross.png")
                    .build()
            ).queue()
        } catch (ex: Exception) {
            SenderUtil.replyError(event, "Shomething went wrong while fetching the API! Please try again.")
            println(ex)
        }
    }
}
