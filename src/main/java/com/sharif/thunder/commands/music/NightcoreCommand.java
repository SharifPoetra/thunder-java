package com.sharif.thunder.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.commands.MusicCommand;

public class NightcoreCommand extends MusicCommand {

    private float f;

    public NightcoreCommand(Thunder thunder) {
        super(thunder);
        this.name = "nightcore";
        this.help = "toggles nightcore mode and changes it's speed.";
        this.arguments = "<0.1 - 3.0>";
    }

    @Override
    protected void execute(CommandEvent event) {

        try {
            f = Float.parseFloat(event.getArgs());
        } catch (NumberFormatException e) {
            event.replyError("The given argument must be a number.");
            return;
        }

        if (f < 0.1f || f > 3.0f) {
            event.replyError("Out of range 0.1 - 3.0");
            return;
        }

        AudioHandler handler =
                (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        handler.setNightcore(f);

        if (f == 1.0f) {
            event.reply("Nightcore mode disabled.");
        } else {
            event.reply("Nightcore mode enabled, speed: " + f + "");
        }
    }

    @Override
    public void doCommand(CommandEvent event) {
        /* Intentionally Empty */
    }
}
