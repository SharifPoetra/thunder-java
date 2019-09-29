package com.sharif.thunder.audio;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sharif.thunder.queue.Queueable;
import com.sharif.thunder.utils.FormatUtil;
import net.dv8tion.jda.api.entities.User;

public class QueuedTrack implements Queueable {
  private final AudioTrack track;
    
  public QueuedTrack(AudioTrack track, User owner) {
    this(track, owner.getIdLong());
  }
    
  public QueuedTrack(AudioTrack track, long owner) {
    this.track = track;
    this.track.setUserData(owner);
  }
    
  @Override
  public long getIdentifier() {
    return track.getUserData(Long.class);
  }
    
  public AudioTrack getTrack() {
    return track;
  }

  @Override
    public String toString() {
      return "`[" + FormatUtil.formatTime(track.getDuration()) + "]` **" + track.getInfo().title + "** - <@" + track.getUserData(Long.class) + ">";
    }
}