package com.reddit.material;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by Rushil Perera on 12/23/2015.
 */
public class Thing implements Serializable, Comparable<Thing> {

    private String id;

    public Thing(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }

    @Override
    public int compareTo(@NonNull Thing another) {
        if (another instanceof Subreddit)
            return 1;
        else if (another instanceof Post)
            return -1;
        return 0;
    }
}
