package com.sharif.thunder.datasources;

import com.sharif.thunder.DataSource;

public class InVCRoles extends DataSource {

  public InVCRoles() {
    this.filename = "discordbot.invcroles";
    this.generateKey =
        (item) -> {
          return item[GUILDID];
        };
    this.size = 2;
  }

  public static final int GUILDID = 0;
  public static final int ROLEID = 1;
}
