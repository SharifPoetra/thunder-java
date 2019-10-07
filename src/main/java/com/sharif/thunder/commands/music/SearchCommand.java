package com.sharif.thunder.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.menu.OrderedMenu;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.audio.QueuedTrack;
import com.sharif.thunder.commands.MusicCommand;
import com.sharif.thunder.utils.FormatUtil;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

public class SearchCommand extends MusicCommand {
    protected String searchPrefix = "ytsearch:";
    private final OrderedMenu.Builder builder;
    private final String searchingEmoji;

    public SearchCommand(Thunder thunder, String searchingEmoji) {
        super(thunder);
        this.searchingEmoji = searchingEmoji;
        this.name = "search";
        this.aliases = new String[] {"ytsearch"};
        this.arguments = "<query>";
        this.help = "searches Youtube for a provided query.";
        this.beListening = true;
        this.bePlaying = false;
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS};
        builder =
                new OrderedMenu.Builder()
                        .allowTextInput(true)
                        .useNumbers()
                        .useCancelButton(true)
                        .setEventWaiter(thunder.getWaiter())
                        .setTimeout(1, TimeUnit.MINUTES);
    }

    @Override
    public void doCommand(CommandEvent event) {
        if (event.getArgs().isEmpty()) {
            event.replyError("Please include a query.");
            return;
        }
        event.reply(
                searchingEmoji + " Searching... `[" + event.getArgs() + "]`",
                m ->
                        thunder.getPlayerManager()
                                .loadItemOrdered(
                                        event.getGuild(),
                                        searchPrefix + event.getArgs(),
                                        new ResultHandler(m, event)));
    }

    private class ResultHandler implements AudioLoadResultHandler {
        private final Message m;
        private final CommandEvent event;

        private ResultHandler(Message m, CommandEvent event) {
            this.m = m;
            this.event = event;
        }

        @Override
        public void trackLoaded(AudioTrack track) {
            if (thunder.getConfig().isTooLong(track)) {
                m.editMessage(
                                FormatUtil.filterEveryone(
                                        event.getClient().getWarning()
                                                + " This track (**"
                                                + track.getInfo().title
                                                + "**) is longer than the allowed maximum: `"
                                                + FormatUtil.formatTime(track.getDuration())
                                                + "` > `"
                                                + thunder.getConfig().getMaxTime()
                                                + "`"))
                        .queue();
                return;
            }
            AudioHandler handler =
                    (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
            int pos = handler.addTrack(new QueuedTrack(track, event.getAuthor())) + 1;
            m.editMessage(
                            FormatUtil.filterEveryone(
                                    event.getClient().getSuccess()
                                            + " Added **"
                                            + track.getInfo().title
                                            + "** (`"
                                            + FormatUtil.formatTime(track.getDuration())
                                            + "`) "
                                            + (pos == 0
                                                    ? "to begin playing"
                                                    : " to the queue at position " + pos)))
                    .queue();
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            builder.setColor(event.getSelfMember().getColor())
                    .setText(
                            FormatUtil.filterEveryone(
                                    event.getClient().getSuccess()
                                            + " Search results for `"
                                            + event.getArgs()
                                            + "`:"))
                    .setChoices(new String[0])
                    .setSelection(
                            (msg, i) -> {
                                AudioTrack track = playlist.getTracks().get(i - 1);
                                if (thunder.getConfig().isTooLong(track)) {
                                    event.replyWarning(
                                            "This track (**"
                                                    + track.getInfo().title
                                                    + "**) is longer than the allowed maximum: `"
                                                    + FormatUtil.formatTime(track.getDuration())
                                                    + "` > `"
                                                    + thunder.getConfig().getMaxTime()
                                                    + "`");
                                    return;
                                }
                                AudioHandler handler =
                                        (AudioHandler)
                                                event.getGuild()
                                                        .getAudioManager()
                                                        .getSendingHandler();
                                int pos =
                                        handler.addTrack(new QueuedTrack(track, event.getAuthor()))
                                                + 1;
                                event.replySuccess(
                                        "Added **"
                                                + track.getInfo().title
                                                + "** (`"
                                                + FormatUtil.formatTime(track.getDuration())
                                                + "`) "
                                                + (pos == 0
                                                        ? "to begin playing"
                                                        : " to the queue at position " + pos));
                            })
                    .setCancel((msg) -> {})
                    .setUsers(event.getAuthor());
            for (int i = 0; i < 5 && i < playlist.getTracks().size(); i++) {
                AudioTrack track = playlist.getTracks().get(i);
                builder.addChoices(
                        "`["
                                + FormatUtil.formatTime(track.getDuration())
                                + "]` [**"
                                + track.getInfo().title
                                + "**]("
                                + track.getInfo().uri
                                + ")");
            }
            builder.build().display(m);
        }

        @Override
        public void noMatches() {
            m.editMessage(
                            FormatUtil.filterEveryone(
                                    event.getClient().getWarning()
                                            + " No results found for `"
                                            + event.getArgs()
                                            + "`."))
                    .queue();
        }

        @Override
        public void loadFailed(FriendlyException throwable) {
            if (throwable.severity == Severity.COMMON)
                m.editMessage(
                                event.getClient().getError()
                                        + " Error loading: "
                                        + throwable.getMessage())
                        .queue();
            else m.editMessage(event.getClient().getError() + " Error loading track.").queue();
        }
    }
}
