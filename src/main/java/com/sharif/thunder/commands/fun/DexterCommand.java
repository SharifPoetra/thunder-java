package com.sharif.thunder.commands.fun;

import com.sharif.thunder.Thunder;
import com.sharif.thunder.commands.Argument;
import com.sharif.thunder.commands.FunCommand;
import com.sharif.thunder.utils.NetworkUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class DexterCommand extends FunCommand {
    private final Thunder thunder;

    public DexterCommand(Thunder thunder) {
        this.thunder = thunder;
        name = "dexter";
        help = "Draws an image avatar over the screen of Dexter from Pokémon";
        arguments = new Argument[]{
                new Argument("user", Argument.Type.USER, true)
        };
    }

    @Override
    protected void execute(Object[] args, MessageReceivedEvent event) {
        try {
            event.getChannel().sendTyping().queue();
            User user = (User) args[0];
            byte[] data = NetworkUtil.download("https://emilia-api.xyz/api/dexter?image=" + user.getEffectiveAvatarUrl(), "Bearer " + thunder.config.emiliaKey);
            event.getChannel().sendFile(data, "dexter.png").embed(
                    new EmbedBuilder()
                    .setColor(event.getMember().getColor())
                    .setImage("attachment://dexter.png")
                    .build()
            ).queue();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
