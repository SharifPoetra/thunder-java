package com.sharif.thunder.api.controller;

import com.sharif.thunder.api.model.Statistics;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

  @Autowired private JDA jda;
  @Setter @Getter private Predicate<User> isBot = user -> user.isBot();
  @Setter @Getter private Runtime rt = Runtime.getRuntime();
  @Setter @Getter private long guildCount = jda.getGuildCache().size();

  @Setter @Getter
  private long channelCount = jda.getVoiceChannelCache().size() + jda.getTextChannelCache().size();

  @Setter @Getter private long textChannelCount = jda.getTextChannelCache().size();
  @Setter @Getter private long voiceChannelCount = jda.getVoiceChannelCache().size();
  @Setter @Getter private long userCount = jda.getUserCache().size();

  @Setter @Getter private long botCount = jda.getUserCache().stream().filter(isBot).count();

  @Setter @Getter private long totalMemory = rt.totalMemory() / (1024 * 1024);
  @Setter @Getter private long freeMemory = rt.freeMemory() / (1024 * 1024);
  @Setter @Getter private long usedMemory = totalMemory - freeMemory;

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
