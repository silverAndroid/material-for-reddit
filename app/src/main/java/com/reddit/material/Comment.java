package com.reddit.material;

import org.json.JSONArray;

/**
 * Created by Rushil Perera on 11/8/2015.
 */
public class Comment implements VotingHelper {
    private final String subredditID;
    private final String linkID;
    private final boolean saved;
    private final String id;
    private final int gilded;
    private final boolean archived;
    private final String author;
    private final int score;
    private final String body;
    private final double edited;
    private final String bodyHTML;
    private final boolean scoreHidden;
    private final long createdUTC;
    private final String authorFlairText;
    private final int vote;
    private String parentID;
    private JSONArray replies;
    private JSONArray userReports;
    private int depth;

    public Comment(String subredditID, String linkID, boolean saved, String id, int gilded, boolean archived, String
            author, int score, String body, double edited, String bodyHTML, boolean scoreHidden, long createdUTC,
                   String authorFlairText, int vote) {
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
        this.vote = vote;
    }

    public Comment(String subredditID, String linkID, boolean saved, String id, int gilded, boolean archived, String
            author, int score, String body, double edited, String bodyHTML, boolean scoreHidden, long createdUTC,
                   String authorFlairText, int vote, String parentID) {
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
        this.vote = vote;
        this.parentID = parentID;
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

    public String getID() {
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

    public double isEdited() {
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

    public JSONArray getReplies() {
        return replies;
    }

    public void setReplies(JSONArray replies) {
        this.replies = replies;
    }

    public JSONArray getUserReports() {
        return userReports;
    }

    public void setUserReports(JSONArray userReports) {
        this.userReports = userReports;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getVote() {
        return vote;
    }

    @Override
    public void vote(int dir) {
        ConnectionSingleton.getInstance().vote(id, dir);
    }

    public String getParentID() {
        return parentID;
    }
}
