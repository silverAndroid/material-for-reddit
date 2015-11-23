package com.reddit.material;

import java.io.Serializable;

/**
 * Created by Rushil Perera on 10/29/2015.
 */
public class Post implements Serializable, VotingHelper {

    private String domain;
    private String bannedBy;
    private String subreddit;
    private String suggestedSort;
    private String userReports;
    private String linkFlairText;
    private String id;
    //    private String fromKind;
    private int gilded;
    private boolean clicked;
    //    private String reportReasons;
    private String author;
    private String media;
    private int score;
    //    private String approvedBy;
    private boolean over18;
    private boolean hidden;
    private String previewImageURL;
    private int numComments;
    private String thumbnailURL;
    private String subredditID;
    private boolean hideScore;
    private double edited;
    private boolean saved;
    //    private String removalReason;
    private boolean stickied;
    private String from;
    private String fromID;
    private String permalink;
    private boolean locked;
    private String url;
    private boolean quarantine;
    private String title;
    private long createdUTC;
    private String distinguished;
    private String modReports;
    private boolean visited;
    private int vote;
    private final String selfText;
//    private String numReports;

    public Post(String domain, String bannedBy, String subreddit, String suggestedSort, String userReports, String
            linkFlairText, int gilded, boolean clicked, String author, String media, int score, boolean over18,
                boolean hidden, String previewImageURL, int numComments, String thumbnailURL, String subredditID,
                boolean hideScore, boolean saved, boolean stickied, String from, String fromID, String permalink,
                boolean locked, String name, String url, boolean quarantine, String title, long createdUTC, String
                        distinguished, String modReports, boolean visited, int vote, String selfText) {
        this.domain = domain;
        this.bannedBy = bannedBy;
        this.subreddit = subreddit;
        this.suggestedSort = suggestedSort;
        this.userReports = userReports;
        this.linkFlairText = linkFlairText;
        this.id = name;
        this.gilded = gilded;
        this.clicked = clicked;
        this.author = author;
        this.media = media;
        this.score = score;
        this.over18 = over18;
        this.hidden = hidden;
        this.previewImageURL = previewImageURL;
        this.numComments = numComments;
        this.thumbnailURL = thumbnailURL;
        this.subredditID = subredditID;
        this.hideScore = hideScore;
        this.vote = vote;
        this.selfText = selfText;
        this.edited = 0.0;
        this.saved = saved;
        this.stickied = stickied;
        this.from = from;
        this.fromID = fromID;
        this.permalink = permalink;
        this.locked = locked;
        this.url = url;
        this.quarantine = quarantine;
        this.title = title;
        this.createdUTC = createdUTC;
        this.distinguished = distinguished;
        this.modReports = modReports;
        this.visited = visited;
    }

    public Post(String domain, String banned_by, String subreddit, String suggestedSort, String user_reports, String
            linkFlairText, int gilded, boolean clicked, String author, String media, int score, boolean over_18,
                boolean hidden, String previewImageURL, int num_comments, String thumbnail, String subreddit_id,
                boolean hide_score, double edited, boolean saved, boolean stickied, String from, String fromID,
                String permalink, boolean locked, String name, String url, boolean quarantine, String title, long
                        created_utc, String distinguished, String mod_reports, boolean visited, int vote, String
                        selfText) {
        this.domain = domain;
        bannedBy = banned_by;
        this.subreddit = subreddit;
        this.suggestedSort = suggestedSort;
        userReports = user_reports;
        this.linkFlairText = linkFlairText;
        this.id = name;
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
        this.selfText = selfText;
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

    public String getID() {
        return id;
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

    @Override
    public void vote(int dir) {
        ConnectionSingleton.getInstance().vote(id, dir);
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public String getSelfText() {
        return selfText;
    }
}
