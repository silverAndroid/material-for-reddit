package com.reddit.material;

import java.util.HashMap;

/**
 * Created by Rushil Perera on 11/5/2015.
 */
public class ConstantMap {

    private static final String IMGUR = "imgur";
    private static final String FLICKR = "flickr";
    private static final String GFYCAT = "gfycat";
    private static final String YOUTUBE = "youtube";
    private static final String YOUTUBE_SHORT = "youtu.be";
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

    public boolean isImage(String string) {
        return string.contains(PNG) || string.contains(JPEG) || string.contains(BMP) || string.contains(IMGUR) ||
                string.contains(FLICKR);
    }

    public boolean isGIF(String string) {
        return string.contains(GFYCAT) || string.contains(GIF) || string.contains(GIFV);
    }

    public boolean isYoutube(String string) {
        return string.contains(YOUTUBE) || string.contains(YOUTUBE_SHORT);
    }

    public HashMap<String, String> getConstantMap() {
        return constantMap;
    }

    public String getConstant(String key) {
        return constantMap.get(key);
    }
}