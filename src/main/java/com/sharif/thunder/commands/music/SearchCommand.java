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
package com.sharif.thunder.commands.music;

import com.jagrosh.jdautilities.menu.OrderedMenu;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.audio.QueuedTrack;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.MusicCommand;
import com.sharif.thunder.utils.FormatUtil;
import com.sharif.thunder.utils.SenderUtil;

import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class SearchCommand extends MusicCommand {
    protected String searchPrefix = "ytsearch:";
    private final OrderedMenu.Builder builder;
    private final String searchingEmoji;
    private static String input;

    public SearchCommand(Thunder thunder, String searchingEmoji) {
        super(thunder);
        this.searchingEmoji = searchingEmoji;
        this.name = "search";
        this.aliases = new String[]{"ytsearch"};
        this.arguments = new Argument[]{new Argument("query", Argument.Type.LONGSTRING, true)};
        this.help = "searches Youtube for a provided query.";
        this.guildOnly = true;
        this.beListening = true;
        this.bePlaying = false;
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS};
        builder = new OrderedMenu.Builder()
                .allowTextInput(true)
                .useNumbers()
                .useCancelButton(true)
                .setEventWaiter(thunder.getWaiter())
                .setTimeout(1, TimeUnit.MINUTES);
    }

    @Override
    public void doCommand(Object[] args, MessageReceivedEvent event) {
        input = (String) args[0];
        event.getChannel().sendMessage(searchingEmoji + " Searching... `[" + input + "]`").queue(m -> thunder.getPlayerManager().loadItemOrdered(event.getGuild(), searchPrefix + input, new ResultHandler(m, event)));
    }

    private class ResultHandler implements AudioLoadResultHandler {
        private final Message m;
        private final MessageReceivedEvent event;

        private ResultHandler(Message m, MessageReceivedEvent event) {
            this.m = m;
            this.event = event;
        }

        @Override
        public void trackLoaded(AudioTrack track) {
            if (thunder.getConfig().isTooLong(track)) {
                m.editMessage(FormatUtil.filterEveryone(thunder.getConfig().getWarning() + " This track (**" + track.getInfo().title + "**) is longer than the allowed maximum: `" + FormatUtil.formatTime(track.getDuration()) + "` > `" + thunder.getConfig().getMaxTime() + "`")).queue();
                return;
            }
            AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            assert handler != null;
            int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor())) + 1;
            m.editMessage(FormatUtil.filterEveryone(thunder.getConfig().getSuccess() + " Added **" + track.getInfo().title + "** (`" + FormatUtil.formatTime(track.getDuration()) + "`) " + (pos == 0 ? "to begin playing" : " to the queue at position " + pos))).queue();
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            builder
                    .setColor(event.getGuild().getSelfMember().getColor())
                    .setText(FormatUtil.filterEveryone(thunder.getConfig().getSuccess() + " Search results for `" + input + "`:"))
                    .setChoices()
                    .setSelection((msg, i) -> {
                        AudioTrack track = playlist.getTracks().get(i - 1);
                        if (thunder.getConfig().isTooLong(track)) {
                            SenderUtil.replyWarning(event, "This track (**" + track.getInfo().title + "**) is longer than the allowed maximum: `" + FormatUtil.formatTime(track.getDuration()) + "` > `" + thunder.getConfig().getMaxTime() + "`");
                            return;
                        }
                        AudioHandler handler = (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
                        assert handler != null;
                        int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor())) + 1;
                        SenderUtil.replySuccess(event, "Added **" + track.getInfo().title + "** (`" + FormatUtil.formatTime(track.getDuration()) + "`) " + (pos == 0 ? "to begin playing" : " to the queue at position " + pos));
                    })
                    .setCancel((msg) -> {
                    })
                    .setUsers(event.getAuthor());
            for (int i = 0; i < 5 && i < playlist.getTracks().size(); i++) {
                AudioTrack track = playlist.getTracks().get(i);
              builder.addChoices("`[" + FormatUtil.formatTime(track.getDuration()) + "]` [**" + track.getInfo().title + "**](" + track.getInfo().uri + ")");
            }
          builder.build().display(m);
        }

        @Override
        public void noMatches() {
            m.editMessage(FormatUtil.filterEveryone( thunder.getConfig().getWarning() + " No results found for `" + input + "`.")).queue();
        }

        @Override
        public void loadFailed(FriendlyException throwable) {
            if (throwable.severity == Severity.COMMON) {
              m.editMessage(thunder.getConfig().getError() + " Error loading: " + throwable.getMessage()).queue();
            } else m.editMessage(thunder.getConfig().getError() + " Error loading track.").queue();
        }
    }
}
