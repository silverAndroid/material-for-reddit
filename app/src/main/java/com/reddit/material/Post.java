package com.reddit.material;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.io.Serializable;

/**
 * Created by Rushil Perera on 10/29/2015.
 */
public class Post extends Thing implements Serializable, VotingHelper {

    private final String selfTextHTML;
    private String domain;
    private String bannedBy;
    private String subreddit;
    private String suggestedSort;
    private String userReports;
    private String linkFlairText;
    //    private String fromKind;
    private boolean clicked;
    //    private String reportReasons;
    private String media;
    //    private String approvedBy;
    private boolean hidden;
    private String previewImageURL;
    private int numComments;
    private int vote;
    private double edited;
    private boolean saved;
    private long createdUTC;
    private String author;
    private String subredditID;
    private int score;
    private int gilded;
    private boolean over18;
    private String thumbnailURL;
    private boolean hideScore;
    //    private String removalReason;
    private boolean stickied;
    private String from;
    private String fromID;
    private String permalink;
    private boolean locked;
    private String url;
    private boolean quarantine;
    private String title;
    private String distinguished;
    private String modReports;
    private boolean visited;
//    private String numReports;

    public Post(String domain, String banned_by, String subreddit, String suggestedSort, String user_reports, String
            linkFlairText, int gilded, boolean clicked, String author, String media, int score, boolean over_18,
                boolean hidden, String previewImageURL, int num_comments, String thumbnail, String subreddit_id,
                boolean hide_score, double edited, boolean saved, boolean stickied, String from, String fromID,
                String permalink, boolean locked, String name, String url, boolean quarantine, String title, long
                        created_utc, String distinguished, String mod_reports, boolean visited, int vote, String
                        selfTextHTML) {
        super(name);
        this.domain = domain;
        bannedBy = banned_by;
        this.subreddit = subreddit;
        this.suggestedSort = suggestedSort;
        userReports = user_reports;
        this.linkFlairText = linkFlairText;
        this.gilded = gilded;
        this.clicked = clicked;
        this.author = author;
        this.media = media;
        this.score = score;
        over18 = over_18;
        this.hidden = hidden;
        this.previewImageURL = previewImageURL;
        numComments = num_comments;
        thumbnailURL = thumbnail;
        subredditID = subreddit_id;
        hideScore = hide_score;
        this.edited = edited;
        this.saved = saved;
        this.stickied = stickied;
        this.from = from;
        this.fromID = fromID;
        this.permalink = permalink;
        this.locked = locked;
        this.url = url;
        this.quarantine = quarantine;
        this.title = title;
        createdUTC = created_utc;
        this.distinguished = distinguished;
        modReports = mod_reports;
        this.visited = visited;
        this.vote = vote;
        this.selfTextHTML = selfTextHTML;
    }

    public String getDomain() {
        return domain;
    }

    public String getBannedBy() {
        return bannedBy;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public String getSuggestedSort() {
        return suggestedSort;
    }

    public String getUserReports() {
        return userReports;
    }

    public String getLinkFlairText() {
        return linkFlairText;
    }

    public int getGilded() {
        return gilded;
    }

    public boolean isClicked() {
        return clicked;
    }

    public String getAuthor() {
        return author;
    }

    public String getMedia() {
        return media;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isOver18() {
        return over18;
    }

    public boolean isHidden() {
        return hidden;
    }

    public String getPreviewImageURL() {
        return previewImageURL;
    }

    public int getNumComments() {
        return numComments;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public String getSubredditID() {
        return subredditID;
    }

    public boolean isHideScore() {
        return hideScore;
    }

    public double getEdited() {
        return edited;
    }

    public boolean isSaved() {
        return saved;
    }

    public boolean isStickied() {
        return stickied;
    }

    public String getFrom() {
        return from;
    }

    public String getFromID() {
        return fromID;
    }

    public String getPermalink() {
        return permalink;
    }

    public boolean isLocked() {
        return locked;
    }

    public String getURL() {
        return url;
    }

    public boolean isQuarantine() {
        return quarantine;
    }

    public String getTitle() {
        return title;
    }

    public long getCreatedUTC() {
        return createdUTC;
    }

    public String getDistinguished() {
        return distinguished;
    }

    public String getModReports() {
        return modReports;
    }

    public boolean isVisited() {
        return visited;
    }

    public String getSelfTextHTML() {
        return selfTextHTML;
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
        votingClient.post("https://oauth.reddit.com/api/vote.json", new JsonHttpResponseHandler());
    }

    public int getVote() {
        return vote;
    }
}
