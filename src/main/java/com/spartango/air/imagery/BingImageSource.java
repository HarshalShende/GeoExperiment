package com.spartango.air.imagery;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**
 * Author: spartango
 * Date: 10/22/14
 * Time: 00:27.
 */
public class BingImageSource {

    public static final String BASE_URL = "http://dev.virtualearth.net/REST/v1/Imagery/Map/Aerial/";

    public static final double EARTH_RADIUS  = 6378137;
    public static final double MIN_LATITUDE  = -85.05112878;
    public static final double MAX_LATITUDE  = 85.05112878;
    public static final double MIN_LONGITUDE = -180;
    public static final double MAX_LONGITUDE = 180;

    private final String key;

    public BingImageSource() {
        key = "Am-IK0uuhbQgJP9Y7fCclql7VQzEI_g3k9e8llWInt-xHm4SddDDdMK3Ed-uIgyL";
    }

    public BingImageSource(String key) {
        this.key = key;
    }

    public Optional<BufferedImage> getImageAround(Point2D center, int zoom, int width, int height) {
        return getImageAround(center.getX(), center.getX(), zoom, width, height);
    }

    public Optional<BufferedImage> getImageAround(double latitude, double longitude, int zoom, int width, int height) {
        String url = buildCenterUrl(latitude, longitude, zoom, width, height);
        return getImage(url);
    }

    public Optional<BufferedImage> getImageWithBounds(double startLatitude,
                                                      double startLongitude,
                                                      double endLatitude,
                                                      double endLongitude,
                                                      int width,
                                                      int height) {
        String url = buildBoxUrl(startLatitude, startLongitude, endLatitude, endLongitude, width, height);
        return getImage(url);
    }

    protected Optional<BufferedImage> getImage(String url) {
        try {
            final BufferedImage image = ImageIO.read(new URL(url));
            return Optional.of(image);
        } catch (IOException e) {
            e.printStackTrace();  //TODO handle e
        }
        return Optional.empty();
    }


    private String buildCenterUrl(double latitude, double longitude, int zoom, int width, int height) {
        return BASE_URL
               + latitude + "," + longitude
               + "/"
               + zoom
               + "?mapSize=" + width + "," + height +
               "&key=" + key;
    }

    private String buildBoxUrl(double startLatitude,
                               double startLongitude,
                               double endLatitude,
                               double endLongitude,
                               int width,
                               int height) {
        return BASE_URL
               + "?mapArea=" +
               startLatitude + "," + startLongitude + "," + endLatitude + "," + endLongitude
               + "?mapSize=" + width + "," + height +
               "&key=" + key;
    }

    // Utilities from MS
    public static int mapWidth(int levelOfDetail) {
        return 256 << levelOfDetail;
    }

    private static double clip(double n, double minValue, double maxValue) {
        return Math.min(Math.max(n, minValue), maxValue);
    }

    public static double groundResolution(double latitude, int levelOfDetail) {
        latitude = clip(latitude, MIN_LATITUDE, MAX_LATITUDE);
        return Math.cos(latitude * Math.PI / 180) * 2 * Math.PI * EARTH_RADIUS / mapWidth(levelOfDetail);
    }

    public static double mapScale(double latitude, int levelOfDetail, int screenDpi) {
        return groundResolution(latitude, levelOfDetail) * screenDpi / 0.0254;
    }

    public static Point latLongToPixel(double latitude, double longitude, int levelOfDetail) {
        latitude = clip(latitude, MIN_LATITUDE, MAX_LATITUDE);
        longitude = clip(longitude, MIN_LONGITUDE, MAX_LONGITUDE);

        double x = (longitude + 180) / 360;
        double sinLatitude = Math.sin(latitude * Math.PI / 180);
        double y = 0.5 - Math.log((1 + sinLatitude) / (1 - sinLatitude)) / (4 * Math.PI);

        int mapSize = mapWidth(levelOfDetail);
        int pixelX = (int) clip(x * mapSize + 0.5, 0, mapSize - 1);
        int pixelY = (int) clip(y * mapSize + 0.5, 0, mapSize - 1);

        return new Point(pixelX, pixelY);
    }

    public static Point2D pixelToLatLong(int pixelX, int pixelY, int levelOfDetail) {
        double mapSize = mapWidth(levelOfDetail);
        double x = (clip(pixelX, 0, mapSize - 1) / mapSize) - 0.5;
        double y = 0.5 - (clip(pixelY, 0, mapSize - 1) / mapSize);

        double latitude = 90 - 360 * Math.atan(Math.exp(-y * 2 * Math.PI)) / Math.PI;
        double longitude = 360 * x;

        return new Point2D.Double(latitude, longitude);
    }
}
