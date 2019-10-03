package com.sharif.thunder.commands.music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.audio.AudioHandler;
import com.sharif.thunder.audio.QueuedTrack;
import com.sharif.thunder.commands.MusicCommand;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

public class RemoveCommand extends MusicCommand {
    public RemoveCommand(Thunder thunder) {
        super(thunder);
        this.name = "remove";
        this.help = "removes a song from the queue.";
        this.arguments = "<position|ALL>";
        this.aliases = new String[] {"delete"};
        this.beListening = true;
        this.bePlaying = true;
    }

    @Override
    public void doCommand(CommandEvent event) {
        AudioHandler handler =
                (AudioHandler) event.getGuild().getAudioManager().getSendingHandler();
        if (handler.getQueue().isEmpty()) {
            event.replyError("There is nothing in the queue!");
            return;
        }
        if (event.getArgs().equalsIgnoreCase("all")) {
            int count = handler.getQueue().removeAll(event.getAuthor().getIdLong());
            if (count == 0) event.replyWarning("You don't have any songs in the queue!");
            else event.replySuccess("Successfully removed your " + count + " entries.");
            return;
        }
        int pos;
        try {
            pos = Integer.parseInt(event.getArgs());
        } catch (NumberFormatException e) {
            pos = 0;
        }
        if (pos < 1 || pos > handler.getQueue().size()) {
            event.replyError(
                    "Position must be a valid integer between 1 and "
                            + handler.getQueue().size()
                            + "!");
            return;
        }
        boolean isDJ = event.getMember().hasPermission(Permission.MANAGE_SERVER);
        QueuedTrack qt = handler.getQueue().get(pos - 1);
        if (qt.getIdentifier() == event.getAuthor().getIdLong()) {
            handler.getQueue().remove(pos - 1);
            event.replySuccess("Removed **" + qt.getTrack().getInfo().title + "** from the queue");
        } else if (isDJ) {
            handler.getQueue().remove(pos - 1);
            User u;
            try {
                u = event.getJDA().getUserById(qt.getIdentifier());
            } catch (Exception e) {
                u = null;
            }
            event.replySuccess(
                    "Removed **"
                            + qt.getTrack().getInfo().title
                            + "** from the queue (requested by "
                            + (u == null ? "someone" : "**" + u.getName() + "**")
                            + ")");
        } else {
            event.replyError(
                    "You cannot remove **"
                            + qt.getTrack().getInfo().title
                            + "** because you don't have `Manage Server` permission and you're not the requester of that song!");
        }
    }
}
