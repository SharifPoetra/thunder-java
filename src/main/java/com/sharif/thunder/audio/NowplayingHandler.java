/*
 *   Copyright 2019 SharifPoetra
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.sharif.thunder.audio;

import com.sharif.thunder.Thunder;
import com.sharif.thunder.entities.Pair;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class NowplayingHandler {
  private final Thunder thunder;
  private final HashMap<Long, Pair<Long, Long>> lastNP; // guild -> channel,message

  public NowplayingHandler(Thunder thunder) {
    this.thunder = thunder;
    this.lastNP = new HashMap<>();
  }

  public void init() {
    thunder.getThreadpool().scheduleWithFixedDelay(() -> updateAll(), 0, 5, TimeUnit.SECONDS);
  }

  public void setLastNPMessage(Message m) {
    lastNP.put(m.getGuild().getIdLong(), new Pair<>(m.getTextChannel().getIdLong(), m.getIdLong()));
  }

  public void clearLastNPMessage(Guild guild) {
    lastNP.remove(guild.getIdLong());
  }

  private void updateAll() {
    Set<Long> toRemove = new HashSet<Long>();
    for (long guildId : lastNP.keySet()) {
      Guild guild = thunder.getJDA().getGuildById(guildId);
      if (guild == null) {
        toRemove.add(guildId);
        continue;
      }
      Pair<Long, Long> pair = lastNP.get(guildId);
      TextChannel tc = guild.getTextChannelById(pair.getKey());
      if (tc == null) {
        toRemove.add(guildId);
        continue;
      }
      AudioHandler handler = (AudioHandler) guild.getAudioManager().getSendingHandler();
      Message msg = handler.getNowPlaying(thunder.getJDA());
      if (msg == null) {
        msg = handler.getNoMusicPlaying(thunder.getJDA());
        toRemove.add(guildId);
      }
      try {
        tc.editMessageById(pair.getValue(), msg).queue(m -> {}, t -> lastNP.remove(guildId));
      } catch (Exception e) {
        toRemove.add(guildId);
      }
    }
    toRemove.forEach(id -> lastNP.remove(id));
  }

  // "event"-based methods
  /*public void onTrackStart(AudioTrack track, AudioHandler handler) {
  }*/

  public void onMessageDelete(Guild guild, long messageId) {
    Pair<Long, Long> pair = lastNP.get(guild.getIdLong());
    if (pair == null) return;
    if (pair.getValue() == messageId) lastNP.remove(guild.getIdLong());
  }
}
