package com.sharif.thunder.api.controller;

import com.sharif.thunder.Thunder;
import com.sharif.thunder.api.model.Statistics;
import java.util.function.Predicate;
import net.dv8tion.jda.api.entities.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
public class StatisticsController {

  @Autowired
  private Thunder thunder;
  @Setter
  @Getter
  private Predicate<User> isBot = user -> user.isBot();
  @Setter
  @Getter
  private Runtime rt = Runtime.getRuntime();
  @Setter
  @Getter
  private long guildCount = thunder.getJDA().getGuilds().size();
  @Setter
  @Getter
  private long channelCount =
      thunder.getJDA().getVoiceChannels().size() + thunder.getJDA().getTextChannels().size();
  @Setter
  @Getter
  private long textChannelCount = thunder.getJDA().getTextChannels().size();
  @Setter
  @Getter
  private long voiceChannelCount = thunder.getJDA().getVoiceChannels().size();
  @Setter
  @Getter
  private long userCount = thunder.getJDA().getUserCache().size();
  @Setter
  @Getter
  private long botCount = thunder.getJDA().getUserCache().stream().filter(isBot).count();
  @Setter
  @Getter
  private long totalMemory = rt.totalMemory() / (1024 * 1024);
  @Setter
  @Getter
  private long freeMemory = rt.freeMemory() / (1024 * 1024);
  @Setter
  @Getter
  private long usedMemory = totalMemory - freeMemory;

  @RequestMapping(value = "/", method = RequestMethod.GET)
  public Statistics statistics() {

    return new Statistics(
        guildCount,
        channelCount,
        textChannelCount,
        voiceChannelCount,
        userCount,
        botCount,
        totalMemory,
        freeMemory,
        usedMemory);
  }
}
