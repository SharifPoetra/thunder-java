/*
 * Copyright 2019-2020 SharifPoetra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.sharif.thunder.databasemanager.managers;

import com.sharif.thunder.database.DataManager;
import com.sharif.thunder.database.DatabaseConnector;
import com.sharif.thunder.database.SQLColumn;
import com.sharif.thunder.database.columns.LongColumn;
import com.sharif.thunder.database.columns.StringColumn;
import com.sharif.thunder.entities.FixedCache;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.zone.ZoneRulesException;
import java.util.Collection;
import java.util.Collections;
import net.dv8tion.jda.api.entities.Guild;
import lombok.Getter;

public class GuildSettingsManager extends DataManager {
  public static final int PREFIX_MAX_LENGTH = 40;
  private static final ZoneId DEFAULT_TIMEZONE = ZoneId.of("GMT-4");

  public static final SQLColumn<Long> GUILD_ID = new LongColumn("GUILD_ID", false, 0L, true);
  public static final SQLColumn<String> PREFIX = new StringColumn("PREFIX", true, null, PREFIX_MAX_LENGTH);
  public static final SQLColumn<String> TIMEZONE = new StringColumn("TIMEZONE", true, null, 32);

  // Cache
  private final FixedCache<Long, GuildSettings> cache = new FixedCache<>(1000);
  private final GuildSettings blankSettings = new GuildSettings();

  public GuildSettingsManager(DatabaseConnector connector) {
    super(connector, "GUILD_SETTINGS");
  }

  // Getters
  public GuildSettings getSettings(Guild guild) {
    if (cache.contains(guild.getIdLong())) return cache.get(guild.getIdLong());
    GuildSettings settings = read(selectAll(GUILD_ID.is(guild.getIdLong())), rs -> rs.next() ? new GuildSettings(rs) : blankSettings);
    cache.put(guild.getIdLong(), settings);
    return settings;
  }

  public boolean hasSettings(Guild guild) {
    return read(selectAll(GUILD_ID.is(guild.getIdLong())), rs -> {
      return rs.next();
    });
  }

  public void setPrefix(Guild guild, String prefix) {
    invalidateCache(guild);
    readWrite(select(GUILD_ID.is(guild.getIdLong()), GUILD_ID, PREFIX), rs -> {
      if (rs.next()) {
        PREFIX.updateValue(rs, prefix);
        rs.updateRow();
      } else {
        rs.moveToInsertRow();
        GUILD_ID.updateValue(rs, guild.getIdLong());
        PREFIX.updateValue(rs, prefix);
        rs.insertRow();
      }
    });
  }

  public void setTimezone(Guild guild, ZoneId zone) {
    invalidateCache(guild);
    readWrite(select(GUILD_ID.is(guild.getIdLong()), GUILD_ID, TIMEZONE), rs -> {
      if (rs.next()) {
        TIMEZONE.updateValue(rs, zone.getId());
        rs.updateRow();
      } else {
        rs.moveToInsertRow();
        GUILD_ID.updateValue(rs, guild.getIdLong());
        TIMEZONE.updateValue(rs, zone.getId());
        rs.insertRow();
      }
    });
  }

  private void invalidateCache(Guild guild) {
    invalidateCache(guild.getIdLong());
  }

  private void invalidateCache(long guildId) {
    cache.pull(guildId);
  }

  public class GuildSettings {
    @Getter
    private final String prefix;
    @Getter
    private final ZoneId timezone;

    private GuildSettings() {
      this.prefix = null;
      this.timezone = DEFAULT_TIMEZONE;
    }

    private GuildSettings(ResultSet rs) throws SQLException {
      this.prefix = PREFIX.getValue(rs);
      String str = TIMEZONE.getValue(rs);
      ZoneId zid;
      if (str == null) zid = DEFAULT_TIMEZONE;
      else
        try {
          zid = ZoneId.of(str);
        } catch (ZoneRulesException ex) {
          zid = DEFAULT_TIMEZONE;
        }
      this.timezone = zid;
    }
  }
}
