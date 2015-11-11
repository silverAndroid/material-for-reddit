package com.reddit.material;

import android.support.annotation.NonNull;

import java.util.Comparator;

/**
 * Created by Rushil Perera on 11/10/2015.
 */
public class Subreddit implements Comparable<Subreddit> {
    private final String name;
    private final String description;
    private final String publicDescription;
    private final int commentScoreHideMins;
    private final String subredditVisibility;
    private final boolean over18;
    private final int subscribers;
    private final long createdUTC;

    public Subreddit(String name, String description, String publicDescription, int commentScoreHideMins, String
            subredditVisibility, boolean over18, int subscribers, long createdUTC) {
        this.name = name.toLowerCase();
        this.description = description;
        this.publicDescription = publicDescription;
        this.commentScoreHideMins = commentScoreHideMins;
        this.subredditVisibility = subredditVisibility;
        this.over18 = over18;
        this.subscribers = subscribers;
        this.createdUTC = createdUTC;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPublicDescription() {
        return publicDescription;
    }

    public int getCommentScoreHideMins() {
        return commentScoreHideMins;
    }

    public String getSubredditVisibility() {
        return subredditVisibility;
    }

    public boolean isOver18() {
        return over18;
    }

    public int getSubscribers() {
        return subscribers;
    }

    public long getCreatedUTC() {
        return createdUTC;
    }

    @Override
    public int compareTo(@NonNull Subreddit another) {
        return getName().compareTo(another.getName());
    }
}
