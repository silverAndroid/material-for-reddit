package com.reddit.material;

import org.json.JSONArray;

/**
 * Created by Rushil Perera on 11/8/2015.
 */
public class Comment extends Thing implements VotingHelper {

    private final String linkID;
    private final boolean archived;
    private final String bodyHTML;
    private final boolean scoreHidden;
    private final String authorFlairText;
    private String parentID;
    private JSONArray replies;
    private JSONArray userReports;
    private int depth;
    private int vote;
    private double edited;
    private boolean saved;
    private long createdUTC;
    private String author;
    private String subredditID;
    private int score;
    private int gilded;

    public Comment(String subredditID, String linkID, boolean saved, String id, int gilded, boolean archived, String
            author, int score, String bodyHTML, double edited, boolean scoreHidden, long createdUTC, String
                           authorFlairText, int vote, String parentID) {
        super(id);
        this.subredditID = subredditID;
        this.linkID = linkID;
        this.saved = saved;
        this.gilded = gilded;
        this.archived = archived;
        this.author = author;
        this.score = score;
        this.bodyHTML = bodyHTML;
        this.edited = edited;
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
        ConnectionSingleton.getInstance().vote(getID(), dir);
    }

    public String getParentID() {
        return parentID;
    }
}
