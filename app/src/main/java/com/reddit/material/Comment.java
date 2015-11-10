package com.reddit.material;

import com.google.gson.JsonArray;

/**
 * Created by Rushil Perera on 11/8/2015.
 */
public class Comment {
    private final String subredditID;
    private final String linkID;
    private final boolean saved;
    private final String id;
    private final int gilded;
    private final boolean archived;
    private final String author;
    private final int score;
    private final String body;
    private final boolean edited;
    private final String bodyHTML;
    private final boolean scoreHidden;
    private final long createdUTC;
    private final String authorFlairText;
    private JsonArray replies;
    private JsonArray userReports;

    public Comment(String subredditID, String linkID, boolean saved, String id, int gilded, boolean archived, String
            author, int score, String body, boolean edited, String bodyHTML, boolean scoreHidden, long createdUTC,
                   String authorFlairText) {
        this.subredditID = subredditID;
        this.linkID = linkID;
        this.saved = saved;
        this.id = id;
        this.gilded = gilded;
        this.archived = archived;
        this.author = author;
        this.score = score;
        this.body = body;
        this.edited = edited;
        this.bodyHTML = bodyHTML;
        this.scoreHidden = scoreHidden;
        this.createdUTC = createdUTC;
        this.authorFlairText = authorFlairText;
    }

    public String getSubredditID() {
        return subredditID;
    }

    public String getLinkID() {
        return linkID;
    }

    public boolean isSaved() {
        return saved;
    }

    public String getId() {
        return id;
    }

    public int getGilded() {
        return gilded;
    }

    public boolean isArchived() {
        return archived;
    }

    public String getAuthor() {
        return author;
    }

    public int getScore() {
        return score;
    }

    public String getBody() {
        return body;
    }

    public boolean isEdited() {
        return edited;
    }

    public String getBodyHTML() {
        return bodyHTML;
    }

    public boolean isScoreHidden() {
        return scoreHidden;
    }

    public long getCreatedUTC() {
        return createdUTC;
    }

    public String getAuthorFlairText() {
        return authorFlairText;
    }

    public void setReplies(JsonArray replies) {
        this.replies = replies;
    }

    public JsonArray getReplies() {
        return replies;
    }

    public void setUserReports(JsonArray userReports) {
        this.userReports = userReports;
    }

    public JsonArray getUserReports() {
        return userReports;
    }
}
