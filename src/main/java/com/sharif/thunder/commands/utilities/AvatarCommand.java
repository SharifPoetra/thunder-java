package com.sharif.thunder.commands.utilities;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.utils.FinderUtil;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.UtilitiesCommand;
import java.util.List;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

public class AvatarCommand extends UtilitiesCommand {

  private final Thunder thunder;

  public AvatarCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "avatar";
    this.aliases = new String[] {"ava"};
    this.arguments = "<user>";
  }

  @Override
  public void execute(CommandEvent event) {
    try {
      event
          .getChannel()
          .sendMessage(thunder.getConfig().getSearching() + " Please wait...")
          .queue(
              message -> {
                String args = event.getArgs();
                if (args.isEmpty()) args = event.getAuthor().getAsTag();
                message.delete().queue();
                List<Member> list = FinderUtil.findMembers(args, event.getGuild());
                EmbedBuilder eb =
                    new EmbedBuilder()
                        .setAuthor(list.get(0).getUser().getName() + "'s avatar")
                        .setColor(list.get(0).getColor())
                        .setImage(list.get(0).getUser().getEffectiveAvatarUrl() + "?size=2048");
                event.reply(eb.build());
              });
    } catch (Exception e) {
      event.replyError("Something went wrong: `" + e.getMessage() + "` please try again later!");
    }
  }
}
