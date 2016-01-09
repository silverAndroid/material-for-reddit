package com.reddit.material;

import java.util.HashMap;

/**
 * Created by Rushil Perera on 11/5/2015.
 */
public class ConstantMap {

    private static final String IMGUR = "imgur";
    private static final String FLICKR = "flickr";
    private static final String GFYCAT = "gfycat";
    private static final String YOUTUBE_REGEX = ".*youtu\\.?be.*";
    private static final String REDDIT_REGEX = ".*redd\\.?it.*";
    private static final String IMGUR_GALLERY_REGEX = ".*imgur\\.com/((gallery)|a).*";
    private static final String BMP = ".bmp";
    private static final String PNG = ".png";
    private static final String JPEG = ".jpg";
    private static final String GIF = ".gif";
    private static final String GIFV = ".gifv";
    private static final String USER_AGENT = "android:com.reddit.material:v1.0.0 (by /u/silverAndroid)";
    private static HashMap<String, String> constantMap;
    private static ConstantMap instance;

    private ConstantMap() {
        constantMap = new HashMap<>();
        constantMap.put(IMGUR, null);
        constantMap.put(FLICKR, null);
        constantMap.put(GFYCAT, null);
        constantMap.put(BMP, null);
        constantMap.put(PNG, null);
        constantMap.put(JPEG, null);
        constantMap.put(GIF, null);
        constantMap.put("user_agent", USER_AGENT);
    }

    public static ConstantMap getInstance() {
        if (instance == null)
            instance = new ConstantMap();
        return instance;
    }

    public boolean isImage(String url) {
        return url != null && (url.contains(PNG) || url.contains(JPEG) || url.contains(BMP) || url.contains(IMGUR) ||
                url.contains(FLICKR));
    }

    public boolean isGIF(String url) {
        return url != null && (url.contains(GFYCAT) || url.contains(GIF) || url.contains(GIFV));
    }

    public boolean isYoutube(String url) {
        return url != null && (url.matches(YOUTUBE_REGEX));
    }

    public boolean isReddit(String url) {
        return url != null && (url.matches(REDDIT_REGEX));
    }

    public boolean isGallery(String url) {
        return url != null && (url.matches(IMGUR_GALLERY_REGEX));
    }

    public String getUserAgent() {
        return USER_AGENT;
    }

    public HashMap<String, String> getConstantMap() {
        return constantMap;
    }

    public String getConstant(String key) {
        return constantMap.get(key);
    }
}