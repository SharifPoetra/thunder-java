package com.sharif.thunder.commands.music;

import com.sharif.thunder.Thunder;

public class SCSearchCommand extends SearchCommand {
    public SCSearchCommand(Thunder thunder, String searchingEmoji) {
        super(thunder, searchingEmoji);
        this.searchPrefix = "scsearch:";
        this.name = "scsearch";
        this.help = "searches Soundcloud for a provided query.";
        this.aliases = new String[] {};
    }
}
