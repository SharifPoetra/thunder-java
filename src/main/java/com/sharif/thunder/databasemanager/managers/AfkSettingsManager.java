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
import java.time.zone.ZoneRulesException;
import java.util.Collection;
import java.util.Collections;
import net.dv8tion.jda.api.entities.User;
import lombok.Getter;

public class AfkSettingsManager extends DataManager {
  public static final int MESSAGE_MAX_LENGTH = 1200;

  public static final SQLColumn<Long> USER_ID = new LongColumn("USER_ID", false, 0L, true);
  public static final SQLColumn<String> MESSAGE = new StringColumn("MESSAGE", true, null, MESSAGE_MAX_LENGTH);

  // Cache
  private final FixedCache<Long, AfkSettings> cache = new FixedCache<>(1000);
  private final AfkSettings blankSettings = new AfkSettings();

  public AfkSettingsManager(DatabaseConnector connector) {
    super(connector, "AFK_SETTINGS");
  }

  // Getters
  public AfkSettings getUser(User user) {
    if (cache.contains(user.getIdLong())) return cache.get(user.getIdLong());
    AfkSettings settings = read(selectAll(USER_ID.is(user.getIdLong())), rs -> rs.next() ? new AfkSettings(rs) : blankSettings);
    cache.put(user.getIdLong(), settings);
    return settings;
  }

  public boolean hasUser(User user) {
    return read(selectAll(USER_ID.is(user.getIdLong())), rs -> {
      return rs.next();
    });
  }
  
  public boolean removeUser(User user) {
    invalidateCache(user);
    return readWrite(selectAll(USER_ID.is(user.getIdLong())), rs -> {
      if (rs.next()) {
        rs.deleteRow();
        return true;
      }
      return false;
    });
  }

  public void setMessage(User user, String message) {
    invalidateCache(user);
    readWrite(select(USER_ID.is(user.getIdLong()), USER_ID, MESSAGE), rs -> {
      if (rs.next()) {
        MESSAGE.updateValue(rs, message);
        rs.updateRow();
      } else {
        rs.moveToInsertRow();
        USER_ID.updateValue(rs, user.getIdLong());
        MESSAGE.updateValue(rs, message);
        rs.insertRow();
      }
    });
  }

  private void invalidateCache(User user) {
    invalidateCache(user.getIdLong());
  }

  private void invalidateCache(long userId) {
    cache.pull(userId);
  }

  public class AfkSettings {
    @Getter
    private final String message;

    private AfkSettings() {
      this.message = null;
    }

    private AfkSettings(ResultSet rs) throws SQLException {
      this.message = MESSAGE.getValue(rs);
    }
  }
}
