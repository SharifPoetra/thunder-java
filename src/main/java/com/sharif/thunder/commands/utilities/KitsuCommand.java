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
import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.UtilitiesCommand;
import com.sharif.thunder.handler.RequestHandler;
import com.sharif.thunder.handler.entity.RequestProperty;
import com.sharif.thunder.utils.OtherUtil;
import com.sharif.thunder.utils.SenderUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
      // TODO: rewrite this to use UnirestUtil
      JsonObject json =
          new RequestHandler(
                  url,
                  new RequestProperty("Accept", "application/vnd.api+json"),
                  new RequestProperty("Content-Type", "application/vnd.api+json"))
              .getJsonObject();
      if (json == null || json.getAsJsonArray("data").size() < 1) {
        SenderUtil.replyWarning(event, "No results found for **" + title + "**");
        return;
      }
      JsonObject data =
          json.getAsJsonArray("data").get(0).getAsJsonObject().get("attributes").getAsJsonObject();

      final String ageRating =
          data.get("ageRating").getAsString() + ": " + data.get("ageRatingGuide").getAsString();
      final String episodes = data.get("episodeCount").getAsString();
      final String episodeLength = data.get("episodeLength").getAsString() + " minutes";
      final String totalLength = data.get("totalLength").getAsInt() / 60 + " hours";
      final String type = data.get("showType").getAsString();
      final String approvalRating = data.get("averageRating").getAsString() + "%";
      final String status = data.get("status").getAsString();
      final String startDate = data.get("startDate").getAsString();
      final String endDate = data.get("endDate").getAsString();

      EmbedBuilder embed =
          new EmbedBuilder()
              .setTitle(
                  data.get("canonicalTitle").getAsString()
                      + " | "
                      + data.get("titles").getAsJsonObject().get("ja_jp").getAsString(),
                  (data.get("youtubeVideoId").isJsonNull())
                      ? ""
                      : "https://www.youtube.com/watch?v=" + data.get("youtubeVideoId").toString())
              .setImage(data.get("posterImage").getAsJsonObject().get("medium").getAsString())
              .setDescription(data.get("synopsis").getAsString())
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
}
