package com.reddit.material;

/**
 * Created by Rushil Perera on 11/10/2015.
 */
public class Subreddit extends Thing {

    private final String name;
    private final String description;
    private final String publicDescription;
    private final int commentScoreHideMins;
    private final String subredditVisibility;
    private final boolean over18;
    private final long subscribers;
    private final long createdUTC;
    private final boolean isSubscriber;
    private String title;

    public Subreddit(String name, String title, String id, String description, String publicDescription, int
            commentScoreHideMins, String subredditVisibility, boolean over18, long subscribers, long createdUTC,
                     boolean isSubscriber) {
        super(id);
        this.title = title;
        this.isSubscriber = isSubscriber;
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

    public long getSubscribers() {
        return subscribers;
    }

    public long getCreatedUTC() {
        return createdUTC;
    }

    public String getTitle() {
        return title;
    }

    public boolean isSubscriber() {
        return isSubscriber;
    }
}
