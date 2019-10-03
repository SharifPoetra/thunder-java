package com.sharif.thunder.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.commands.MusicCommand;

public class KaraokeCommand extends MusicCommand {
    public KaraokeCommand(Thunder thunder) {
        super(thunder);
        this.name = "karaoke";
        this.help = "toggles karaoke mode.";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        AudioHandler handler =
                (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        handler.setKaraoke(!handler.isKaraoke());
        event.replySuccess(
                "Karaoke mode is now `" + (!handler.isKaraoke() ? "disabled" : "enabled") + "`.");
    }

    @Override
    public void doCommand(CommandEvent event) {
        /* Intentionally Empty */
    }
}
