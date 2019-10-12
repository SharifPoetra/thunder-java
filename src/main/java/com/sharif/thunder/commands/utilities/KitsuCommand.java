package com.sharif.thunder.commands.utilities;

import com.google.gson.JsonObject;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.UtilitiesCommand;
import com.sharif.thunder.handler.RequestHandler;
import com.sharif.thunder.handler.entity.RequestProperty;
import com.sharif.thunder.utils.OtherUtil;
import net.dv8tion.jda.api.EmbedBuilder;

public class KitsuCommand extends UtilitiesCommand {

  private static final String BASE_URL = "https://kitsu.io/api/edge/anime?filter[text]=";
  private final Thunder thunder;

  public KitsuCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "kitsu";
    this.help = "fetches anime or manga from kitsu.io.";
    this.arguments = "<title> | show <title> | character <name>";
  }

  @Override
  protected void execute(CommandEvent event) {
    try {
      final String url = BASE_URL + OtherUtil.scrub(event.getArgs(), true) + "&page[limit]=1";
      JsonObject json =
          new RequestHandler(
                  url,
                  new RequestProperty("Accept", "application/vnd.api+json"),
                  new RequestProperty("Content-Type", "application/vnd.api+json"))
              .getJsonObject();
      if (json == null || json.getAsJsonArray("data").size() < 1) {
        event.replyWarning("No results found for **" + event.getArgs() + "**");
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
              .setColor(event.getSelfMember().getColor())
              .setFooter(
                  "Requested by " + event.getMember().getEffectiveName(),
                  event.getMember().getUser().getEffectiveAvatarUrl());
      event.reply(embed.build());
    } catch (Exception ex) {
      event.replyWarning(
          "Shomething went wrong when trying to fetch the kitsu API, Please try with another queries.");
      System.out.println("Shomething went wrong when trying to fetch the kitsu API: " + ex);
    }
  }
}
