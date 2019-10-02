package com.sharif.thunder.audio;

import com.sharif.thunder.Thunder;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.api.entities.Guild;

public class PlayerManager extends DefaultAudioPlayerManager {
  private final Thunder thunder;
  
  public PlayerManager(Thunder thunder) {
    this.thunder = thunder;
  }
    
  public void init() {
    AudioSourceManagers.registerRemoteSources(this);
    AudioSourceManagers.registerLocalSource(this);
    source(YoutubeAudioSourceManager.class).setPlaylistPageCount(10);
  }
    
  public Thunder getBot() {
    return thunder;
  }
    
  public boolean hasHandler(Guild guild) {
    return guild.getAudioManager().getSendingHandler()!=null;
  }
    
  public AudioHandler setUpHandler(Guild guild) {
    AudioHandler handler;
    if(guild.getAudioManager().getSendingHandler()==null) {
      AudioPlayer player = createPlayer();
     // player.setVolume(100);
      handler = new AudioHandler(this, guild, player);
      player.addListener(handler);
      guild.getAudioManager().setSendingHandler(handler);
    } else 
      handler = (AudioHandler) guild.getAudioManager().getSendingHandler();
      return handler;
    }
}