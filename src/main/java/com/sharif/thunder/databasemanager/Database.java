/*
 * Copyright 2019 SharifPoetra
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
package com.sharif.thunder.databasemanager;

import com.sharif.thunder.database.DatabaseConnector;
import com.sharif.thunder.databasemanager.managers.*;

public class Database extends DatabaseConnector {

  public final GuildSettingsManager guildSettings;

  public Database(String host, String user, String pass) throws Exception {
    super(host, user, pass);

    guildSettings = new GuildSettingsManager(this);

    init();
  }
}
