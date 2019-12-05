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
package com.sharif.thunder.commands.utilities;

import com.google.gson.JsonObject;
import com.google.gson.GsonBuilder;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.UtilitiesCommand;
import com.sharif.thunder.handler.RequestHandler;
import com.sharif.thunder.handler.entity.RequestProperty;
import com.sharif.thunder.utils.OtherUtil;
import com.sharif.thunder.utils.SenderUtil;
import com.sharif.thunder.utils.NetworkUtil;
import com.sharif.thunder.utils.GsonUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import java.util.List;

public class KitsuCommand extends UtilitiesCommand {

  private static final String BASE_URL = "https://kitsu.io/api/edge/anime?filter[text]=";
  private final Thunder thunder;

  public KitsuCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "kitsu";
    this.help = "fetches anime or manga from kitsu.io.";
    this.arguments = new Argument[] {new Argument("title", Argument.Type.LONGSTRING, true)};
  }

  @Override
  protected void execute(Object[] args, MessageReceivedEvent event) {
    try {
      String title = (String) args[0];
      final String url = BASE_URL + OtherUtil.scrub(title, true) + "&page[limit]=1";
      System.out.println(url);

      String kitsuData = new String(NetworkUtil.download(url));
      KitsuResponse response = GsonUtil.fromJSON(kitsuData, KitsuResponse.class);
      
      Data data = response.data[0];
      
      if (data == null) {
        SenderUtil.replyWarning(event, "No results found for **" + title + "**");
        return;
      }
      
      final String ageRating = data.attributes.ageRating + ": " + data.attributes.ageRatingGuide;
      final String episodes = data.attributes.episodeCount;
      final String episodeLength = data.attributes.episodeLength + " minutes";
      final String totalLength = data.attributes.totalLength / 60 + " hours";
      final String type = data.attributes.showType;
      final String approvalRating = data.attributes.averageRating;
      final String status = data.attributes.status;
      final String startDate = data.attributes.startDate;
      final String endDate = data.attributes.endDate;
      
      EmbedBuilder embed =
          new EmbedBuilder()
              .setTitle(data.attributes.canonicalTitle + " | " + data.attributes.titles.ja_jp, data.attributes.youtubeVideoId == null ? "" : "https://www.youtube.com/watch?v=" + data.attributes.youtubeVideoId)
              .setImage(data.attributes.posterImage.original)
              .setDescription(data.attributes.synopsis)
              .addField("Age Rating", ageRating, true)
              .addField("Episodes", episodes, true)
              .addField("Episode Length", episodeLength, true)
              .addField("Total Length", totalLength, true)
              .addField("Type", type, true)
              .addField("Kitsu Approval Rating", approvalRating, true)
              .addField("Status", status, true)
              .addField("Start Date", startDate, true)
              .addField("End Date", endDate, true)
              .setColor(event.getGuild().getSelfMember().getColor())
              .setFooter(
                  "Requested by " + event.getMember().getEffectiveName(),
                  event.getMember().getUser().getEffectiveAvatarUrl());
      event.getChannel().sendMessage(embed.build()).queue();
    } catch (Exception ex) {
      SenderUtil.replyWarning(
          event,
          "Shomething went wrong when trying to fetch the kitsu API, Please try with another queries.");
      System.out.println("Shomething went wrong when trying to fetch the kitsu API: " + ex);
    }
  }
  
  public class KitsuResponse {
    private Data[] data;
    private KitsuResponseMeta meta;
  }
  
  public class KitsuResponseMeta {
    private int count;
  }
  
  public class KitsuResponseLinks {
    private String first;
    private String next;
    private String last;
  }
  
  public class Data {
    private Attributes attributes;
  }
  
  public class Attributes {
    private String canonicalTitle;
    private String synopsis;
    private Titles titles;
    private String ageRating;
    private String ageRatingGuide;
    private String episodeCount;
    private int episodeLength;
    private int totalLength;
    private String showType;
    private String averageRating;
    private String status;
    private String startDate;
    private String endDate;
    private String youtubeVideoId;
    private PosterImage posterImage;
  }
  
  public class Titles {
    private String en;
    private String en_jp;
    private String ja_jp;
  }
  
  public class PosterImage {
    private String tiny;
    private String small;
    private String medium;
    private String large;
    private String original;
  }
  
}
