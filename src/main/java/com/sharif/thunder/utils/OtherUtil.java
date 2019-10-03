package com.sharif.thunder.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.api.entities.Message;
import okhttp3.*;

public class OtherUtil {

    public static void deleteMessageAfter(Message message, long delay) {
        message.delete().queueAfter(delay, TimeUnit.MILLISECONDS);
    }

    public static String loadResource(Object clazz, String name) {
        try (BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(clazz.getClass().getResourceAsStream(name)))) {
            StringBuilder sb = new StringBuilder();
            reader.lines().forEach(line -> sb.append("\r\n").append(line));
            return sb.toString().trim();
        } catch (IOException ex) {
            return null;
        }
    }

    public static InputStream imageFromUrl(String url) {
        if (url == null) return null;
        try {
            URL u = new URL(url);
            URLConnection urlConnection = u.openConnection();
            urlConnection.setRequestProperty(
                    "user-agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
            return urlConnection.getInputStream();
        } catch (IOException | IllegalArgumentException ignore) {
        }
        return null;
    }

    public static BufferedImage makeWave(Color c) {
        BufferedImage bi = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB_PRE);
        Graphics2D g2d = bi.createGraphics();
        g2d.setComposite(AlphaComposite.SrcOver);
        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, 128, 128);

        float radius = 28 + (float) (Math.random() * 4);
        float[] dist = {0.0f, .03f, .08f, .3f, 1.0f};
        Color[] colors = {
            new Color(255, 255, 255, 255),
            new Color(255, 255, 255, 255),
            new Color(c.getRed(), c.getGreen(), c.getBlue(), 15),
            new Color(c.getRed(), c.getGreen(), c.getBlue(), 5),
            new Color(c.getRed(), c.getGreen(), c.getBlue(), 0)
        };
        int times = 2 + (int) (Math.random() * 3);
        for (int j = 0; j < times; j++) {
            double accel = 0;
            int height = 64;
            for (int i = -(int) radius; i < 128 + (int) radius; i++) {
                accel += (Math.random() * 2) - 1;
                if (accel > 2.1) accel -= .3;
                if (accel < -2.1) accel += .3;
                if (height < 48) accel += .7;
                if (height > 80) accel -= .7;
                height += (int) accel;
                Point2D center = new Point2D.Float(i, height);

                RadialGradientPaint p =
                        new RadialGradientPaint(
                                center,
                                radius,
                                dist,
                                colors,
                                MultipleGradientPaint.CycleMethod.NO_CYCLE);
                g2d.setPaint(p);
                g2d.fillRect(0, 0, 128, 128);
            }
        }
        return bi;
    }
}
