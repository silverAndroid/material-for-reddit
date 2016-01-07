package com.reddit.material;

import java.io.Serializable;

/**
 * Created by Rushil Perera on 1/6/2016.
 */
public class Image implements Serializable {

    private final String title;
    private final String url;
    private final int width;
    private final int height;
    private String lowResURL;

    public Image(String title, String url, int width, int height) {
        this.title = title;
        this.url = url;
        this.width = width;
        this.height = height;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getLowResURL() {
        return lowResURL;
    }

    public void setLowResURL(String lowResURL) {
        this.lowResURL = lowResURL;
    }
}
