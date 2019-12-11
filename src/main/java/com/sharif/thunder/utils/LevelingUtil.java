/*
 *   Copyright 2019 SharifPoetra
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.sharif.thunder.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class LevelingUtil {

  private static final List<Long> spamFilter = new ArrayList<>();

  public static long xpToNextLevel(int level) {
    return 10 * (((long) Math.pow(level, 2)) + 10 * level + 20);
  }

  private static long levelsToXp(int levels) {
    long xp = 0;

    for (int level = 0; level <= levels; level++) {
      xp += xpToNextLevel(level);
    }

    return xp;
  }

  public static int xpToLevels(long totalXp) {
    boolean calculating = true;
    int level = 0;

    while (calculating) {
      long xp = levelsToXp(level);

      if (totalXp < xp) {
        calculating = false;
      } else {
        level++;
      }
    }

    return level;
  }

  public static long remainingXp(long totalXp) {
    int level = xpToLevels(totalXp);

    if (level == 0) return totalXp;

    long xp = levelsToXp(level);

    return totalXp - xp + xpToNextLevel(level);
  }

  public static int randomXp(int min, int max) {
    Random random = new Random();

    return random.nextInt((max - min) + 1) + min;
  }

  public static void addUserToSpamFilter(long userId) {
    if (!isUserHasSpamFilter(userId)) {
      spamFilter.add(userId);
    }

    new Timer()
        .schedule(
            new TimerTask() {
              @Override
              public void run() {
                if (isUserHasSpamFilter(userId)) {
                  spamFilter.remove(userId);
                }
              }
            },
            1000 * 60);
  }

  public static boolean isUserHasSpamFilter(long userId) {
    return spamFilter.contains(userId);
  }
}
