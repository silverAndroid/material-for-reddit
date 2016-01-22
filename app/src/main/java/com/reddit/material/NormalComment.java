package com.reddit.material;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;

/**
 * Created by Rushil Perera on 1/13/2016.
 */
public class NormalComment extends Comment implements VotingHelper {

    private final String linkID;
    private final boolean archived;
    private final String bodyHTML;
    private final boolean scoreHidden;
    private final String authorFlairText;
    private JSONArray userReports;
    private int vote;
    private double edited;
    private boolean saved;
    private long createdUTC;
    private String author;
    private String subredditID;
    private int score;
    private int gilded;

    public NormalComment(String id, String subredditID, String linkID, boolean saved, int gilded, boolean archived,
                         String author, int score, String bodyHTML, double edited, boolean scoreHidden, long
                                 createdUTC, String authorFlairText, int vote) {
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

    public void setScore(int score) {
        this.score = score;
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

    public JSONArray getUserReports() {
        return userReports;
    }

    public void setUserReports(JSONArray userReports) {
        this.userReports = userReports;
    }

    public int getVote() {
        return vote;
    }

    @Override
    public void vote(int dir) {
        String accessToken = Authentication.getInstance().getAccessToken();
        AsyncHttpClient votingClient = new AsyncHttpClient();
        votingClient.setUserAgent(ConstantMap.getInstance().getUserAgent());
        votingClient.addHeader("Authorization", "bearer " + accessToken);
        RequestParams params = new RequestParams();
        params.put("dir", dir);
        params.put("id", getID());
        votingClient.post("https://oauth.reddit.com/api/vote.json", params, new JsonHttpResponseHandler());
    }
}
