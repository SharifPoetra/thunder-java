package com.sharif.thunder.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jlyrics.Lyrics;
import com.jagrosh.jlyrics.LyricsClient;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.commands.MusicCommand;
import java.util.concurrent.ExecutionException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;

public class LyricsCommand extends MusicCommand {
    private final LyricsClient client = new LyricsClient();
    private Lyrics lyrics;

    public LyricsCommand(Thunder thunder) {
        super(thunder);
        this.name = "lyrics";
        this.arguments = "[song name]";
        this.help = "shows the lyrics to the currently-playing song.";
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS};
        this.bePlaying = false;
    }

    @Override
    public void doCommand(CommandEvent event) {
        event.getChannel().sendTyping().queue();
        String title;
        if (event.getArgs().isEmpty())
            title =
                    ((AudioHandler) event.getGuild().getAudioManager().getSendingHandler())
                            .getPlayer()
                            .getPlayingTrack()
                            .getInfo()
                            .title;
        else title = event.getArgs();
        try {
            lyrics = client.getLyrics(title).get();
        } catch (InterruptedException | ExecutionException e) {
            event.replyError(
                    "Shomething went wrong when trying fetching the lyrics: " + e.getMessage());
        }
        if (lyrics == null) {
            event.replyError("Lyrics for `" + title + "` could not be found!");
            return;
        }

        EmbedBuilder eb =
                new EmbedBuilder()
                        .setAuthor(lyrics.getAuthor())
                        .setColor(event.getSelfMember().getColor())
                        .setTitle(lyrics.getTitle(), lyrics.getURL());
        if (lyrics.getContent().length() > 15000) {
            event.replyWarning(
                    "Lyrics for `" + title + "` found but likely not correct: " + lyrics.getURL());
        } else if (lyrics.getContent().length() > 2000) {
            String content = lyrics.getContent().trim();
            while (content.length() > 2000) {
                int index = content.lastIndexOf("\n\n", 2000);
                if (index == -1) index = content.lastIndexOf("\n", 2000);
                if (index == -1) index = content.lastIndexOf(" ", 2000);
                if (index == -1) index = 2000;
                event.reply(eb.setDescription(content.substring(0, index).trim()).build());
                content = content.substring(index).trim();
                eb.setAuthor(null).setTitle(null, null);
            }
            event.reply(eb.setDescription(content).build());
        } else event.reply(eb.setDescription(lyrics.getContent()).build());
    }
}
