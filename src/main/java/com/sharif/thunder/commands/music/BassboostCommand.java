package com.sharif.thunder.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.commands.MusicCommand;

public class BassboostCommand extends MusicCommand {
    public BassboostCommand(Thunder thunder) {
        super(thunder);
        this.name = "bassboost";
        this.help = "toggles bassboost mode.";
        this.guildOnly = true;
    }

    @Override
    protected void execute(CommandEvent event) {
        AudioHandler handler =
                (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        handler.setBassboost(!handler.isBassboost());
        event.replySuccess(
                "Bassboost mode is now `"
                        + (!handler.isBassboost() ? "disabled" : "enabled")
                        + "`.");
    }

    @Override
    public void doCommand(CommandEvent event) {
        /* Intentionally Empty */
    }
}
