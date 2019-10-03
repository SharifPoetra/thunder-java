package com.sharif.thunder.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.commands.MusicCommand;

public class StopCommand extends MusicCommand {
    public StopCommand(Thunder thunder) {
        super(thunder);
        this.name = "stop";
        this.help = "stops the current song and clears the queue.";
        this.bePlaying = false;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler =
                (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        handler.stopAndClear();
        event.getGuild().getAudioManager().closeAudioConnection();
        event.reply(
                event.getClient().getSuccess()
                        + " The player has stopped and the queue has been cleared.");
    }
}
