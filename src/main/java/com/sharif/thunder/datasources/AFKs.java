package com.sharif.thunder.datasources;

import com.sharif.thunder.DataSource;

public class AFKs extends DataSource {

    public AFKs() {
        this.filename = "discordbot.afks";
        this.generateKey =
                (item) -> {
                    return item[USERID];
                };
        this.size = 2;
    }

    public static final int USERID = 0;
    public static final int MESSAGE = 1;
}
