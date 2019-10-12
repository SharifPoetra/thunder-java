package com.sharif.thunder.utils;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
  public static Random random = new Random();

  @SafeVarargs
  public static <T> T randomElement(T... items) {
    return items[random.nextInt(items.length)];
  }

  public static <T> T randomElement(List<T> items) {
    return items.get(Math.abs(ThreadLocalRandom.current().nextInt() % items.size()));
  }
}
