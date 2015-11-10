package com.reddit.material;

import java.util.HashMap;

/**
 * Created by Rushil Perera on 11/5/2015.
 */
public class ConstantMap {

    private static final String IMGUR = "imgur";
    private static final String FLICKR = "flickr";
    private static final String GFYCAT = "gfycat";
    private static final String BMP = ".bmp";
    private static final String PNG = ".png";
    private static final String JPEG = ".jpg";
    private static final String GIF = ".gif";
    private static final String GIFV = ".gifv";
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
    }

    public static ConstantMap getInstance() {
        if (instance == null)
            instance = new ConstantMap();
        return instance;
    }

    public boolean isImage(String string) {
        return string.contains(PNG) || string.contains(JPEG) || string.contains(BMP) || string.contains(IMGUR);
    }

    public boolean isGIF(String string) {
        return string.contains(GFYCAT) || string.contains(GIF) || string.contains(GIFV);
    }

    public HashMap<String, String> getConstantMap() {
        return constantMap;
    }
}