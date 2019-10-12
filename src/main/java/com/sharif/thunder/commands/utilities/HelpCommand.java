package com.sharif.thunder.commands.utilities;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.Command.Category;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.UtilitiesCommand;
import com.sharif.thunder.utils.FormatUtil;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import net.dv8tion.jda.api.EmbedBuilder;

public class HelpCommand extends UtilitiesCommand {
  private final Thunder thunder;

  public HelpCommand(Thunder thunder) {
    this.thunder = thunder;
    this.name = "help";
    this.help = "show the list of a commands";
    this.arguments = "[command]";
    this.aliases = new String[] {"h", "cmds"};
  }

  @Override
  protected void execute(CommandEvent event) {
    if (event.getArgs().isEmpty()) {
      generateFullHelp(event);
      return;
    } else {
      StringBuilder builder = new StringBuilder();
      EmbedBuilder eb = new EmbedBuilder();
      eb.setColor(event.getSelfMember().getColor());
      for (Command command : event.getClient().getCommands()) {
        List<String> list = Arrays.asList(command.getAliases());
        if (event.getArgs().equals(command.getName()) || list.contains(event.getArgs())) {
          if (command.isHidden()) continue;
          if (command.isOwnerCommand()
              && !event.getAuthor().getId().equals(event.getClient().getOwnerId())) continue;
          StringBuilder usageSb =
              new StringBuilder()
                  .append("`")
                  .append(event.getClient().getPrefix())
                  .append(command.getName())
                  .append(
                      command.getArguments() == null ? "`" : " " + command.getArguments() + "`");
          eb.setAuthor("Available help for " + command.getName() + " command:");
          eb.setDescription(FormatUtil.capitalize(command.getHelp()));
          eb.addField("Usage: ", usageSb.toString(), true);
          if (command.getAliases().length > 0) {
            StringBuilder aliasesSb = new StringBuilder();
            for (String alias : command.getAliases()) {
              aliasesSb.append(" `").append(alias).append("`");
            }
            eb.addField("Aliases:", aliasesSb.toString(), true);
          }

          if (command.getChildren().length > 0) {
            StringBuilder subSb = new StringBuilder();
            for (Command children : command.getChildren()) {
              subSb
                  .append("`")
                  .append(event.getClient().getPrefix())
                  .append(command.getName())
                  .append(" ")
                  .append(children.getName())
                  .append(
                      children.getArguments() == null ? "`" : " " + children.getArguments() + "`")
                  .append(" - ")
                  .append(FormatUtil.capitalize(children.getHelp()));
            }
            eb.addField("Subcommands:", subSb.toString(), true);
          }

          StringBuilder footerSb = new StringBuilder();
          footerSb.append(
              "\n\nFor additional help, contact "
                  + event.getJDA().getUserById(event.getClient().getOwnerId()).getName()
                  + "#"
                  + event.getJDA().getUserById(event.getClient().getOwnerId()).getDiscriminator()
                  + "");
          eb.setFooter(footerSb.toString());

          // builder.append("**Available help for `" + command.getName() + "`:**\n")
          //         .append("Usage: `")
          //         .append(event.getClient().getPrefix())
          //         .append(command.getName())
          //         .append(
          //                 command.getArguments() == null
          //                         ? "`"
          //                         : " " + command.getArguments() + "`");
          // if (command.getAliases().length > 0) {
          //     builder.append("\nAliases:");
          //     for (String alias : command.getAliases()) {
          //         builder.append(" `").append(alias).append("`");
          //     }
          // }
          // builder.append("\n*" + FormatUtil.capitalize(command.getHelp()) + "*\n");
          // if (command.getChildren().length > 0) {
          //     for (Command children : command.getChildren()) {
          //         builder.append("\n**Subcommands:**")
          //                 .append("\n`")
          //                 .append(event.getClient().getPrefix())
          //                 .append(command.getName())
          //                 .append(" ")
          //                 .append(children.getName())
          //                 .append(
          //                         children.getArguments() == null
          //                                 ? "`"
          //                                 : " " + children.getArguments() + "`")
          //                 .append(" - ")
          //                 .append(FormatUtil.capitalize(children.getHelp()));
          //     }
          // }
          // builder.append(
          //         "\n\nFor additional help, contact **"
          //                 + event.getJDA()
          //                         .getUserById(event.getClient().getOwnerId())
          //                         .getName()
          //                 + "**#"
          //                 + event.getJDA()
          //                         .getUserById(event.getClient().getOwnerId())
          //                         .getDiscriminator()
          //                 + "");
          event.reply(eb.build());
        }
      }
    }
  }

  private void generateFullHelp(CommandEvent event) {
    EmbedBuilder eb = new EmbedBuilder();
    eb.setColor(event.getSelfMember().getColor());
    eb.setTitle("__**" + event.getSelfUser().getName() + "** commands:__");
    StringBuilder builder = new StringBuilder();
    Category category = null;
    for (Command command : event.getClient().getCommands()) {
      if (command.isHidden()) continue;
      if (command.isOwnerCommand()
          && !event.getAuthor().getId().equals(event.getClient().getOwnerId())) continue;

      if (!Objects.equals(category, command.getCategory())) {
        category = command.getCategory();
        builder
            .append("\n\n**__")
            .append(category == null ? "No Category" : category.getName())
            .append("__:**\n");
      }
      builder
          .append("`")
          .append(event.getClient().getPrefix())
          .append(command.getName())
          .append("`  ");
    }
    eb.setDescription(builder.toString());
    eb.setFooter("Do not include <> nor [] - <> means required and [] means optional.");
    event.reply(eb.build());
  }
}
