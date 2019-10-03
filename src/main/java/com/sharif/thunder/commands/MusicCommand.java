package com.sharif.thunder.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.exceptions.PermissionException;

public abstract class MusicCommand extends Command {
    protected final Thunder thunder;
    protected boolean bePlaying;
    protected boolean beListening;

    public MusicCommand(Thunder thunder) {
        this.thunder = thunder;
        this.guildOnly = true;
        this.category = new Category("Music");
    }

    @Override
    protected void execute(CommandEvent event) {
        thunder.getPlayerManager()
                .setUpHandler(event.getGuild()); // no point constantly checking for this later
        if (bePlaying
                && !((AudioHandler) event.getGuild().getAudioManager().getSendingHandler())
                        .isMusicPlaying(event.getJDA())) {
            event.reply(event.getClient().getError() + " There must be music playing to use that!");
            return;
        }
        if (beListening) {
            VoiceChannel current = event.getGuild().getSelfMember().getVoiceState().getChannel();
            GuildVoiceState userState = event.getMember().getVoiceState();
            if (!userState.inVoiceChannel() || userState.isDeafened()) {
                event.replyError("You must be listening in a voice channel to use that!");
                return;
            }
            if (!event.getGuild().getSelfMember().getVoiceState().inVoiceChannel()) {
                try {
                    event.getGuild().getAudioManager().openAudioConnection(userState.getChannel());
                } catch (PermissionException ex) {
                    event.reply(
                            event.getClient().getError()
                                    + " I am unable to connect to **"
                                    + userState.getChannel().getName()
                                    + "**!");
                    return;
                }
            }
        }

        doCommand(event);
    }

    public abstract void doCommand(CommandEvent event);
}
