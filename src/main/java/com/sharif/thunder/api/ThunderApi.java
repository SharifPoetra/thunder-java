package com.sharif.thunder.api;

import static spark.Spark.*;
import com.sharif.thunder.Thunder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sharif.thunder.api.model.StatisticModel;
import net.dv8tion.jda.api.entities.User;
import com.google.gson.FieldNamingPolicy;
import java.util.function.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThunderApi {
  
  private static Thunder thunder;

  public ThunderApi(Thunder thunder) {
    this.thunder = thunder;
  }

  public ThunderApi start() {
    
    Gson gson = new GsonBuilder()
    .disableHtmlEscaping()
    .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
    .setPrettyPrinting()
    .serializeNulls()
    .create();
    
    Logger logger = LoggerFactory.getLogger(ThunderApi.class);

    port(3000);
    path("/api", () -> {
      before("/*", (q, a) -> logger.info("Received API-call."));
      get("/stats/", (req, res) -> {
        try {
          Predicate<User> isBot = user -> user.isBot();
          Runtime rt = Runtime.getRuntime();
          long guildCount = thunder.getJDA().getGuildCache().size();
          long channelCount = thunder.getJDA().getVoiceChannelCache().size() + thunder.getJDA().getTextChannelCache().size();
          long textChannelCount = thunder.getJDA().getTextChannelCache().size();
          long voiceChannelCount = thunder.getJDA().getVoiceChannelCache().size();
          long userCount = thunder.getJDA().getUserCache().size();
          long botCount = thunder.getJDA().getUserCache().stream().filter(isBot).count();
          long totalMemory = rt.totalMemory() / (1024 * 1024);
          long freeMemory = rt.freeMemory() / (1024 * 1024);
          long usedMemory = totalMemory - freeMemory;
          
          StatisticModel stats = new StatisticModel();
          stats.setGuildCount(guildCount);
          stats.setChannelCount(channelCount);
          stats.setTextChannelCount(textChannelCount);
          stats.setVoiceChannelCount(voiceChannelCount);
          stats.setUserCount(userCount);
          stats.setBotCount(botCount);
          stats.setTotalMemory(totalMemory);
          stats.setFreeMemory(freeMemory);
          stats.setUsedMemory(usedMemory);

          res.type("application/json");
          res.status(200);
          res.body(gson.toJson(stats));
          return res.body();
        } catch (Exception ex) {
          return ex.getMessage();
        }
      });
    });
    return this;
  }
}
