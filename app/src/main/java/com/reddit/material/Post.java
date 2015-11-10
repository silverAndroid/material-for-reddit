package com.reddit.material;

import java.io.Serializable;

/**
 * Created by Rushil Perera on 10/29/2015.
 */
public class Post implements Serializable {

    private String domain;
    private String bannedBy;
    private String subreddit;
    private String selfTextHTML;
    private String selfText;
    private String suggestedSort;
    private String userReports;
    private String linkFlairText;
    private String id;
    //    private String fromKind;
    private int gilded;
    private boolean archived;
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
    private int downs;
    private String secureMediaEmbed;
    private boolean saved;
    //    private String removalReason;
    private String postHint;
    private boolean stickied;
    private String from;
    private boolean isSelf;
    private String fromID;
    private String permalink;
    private boolean locked;
    private String name;
    private String url;
    private String authorFlairText;
    private boolean quarantine;
    private String title;
    private long createdUTC;
    private String distinguished;
    private String modReports;
    private boolean visited;
//    private String numReports;

    public Post(String domain, String bannedBy, String subreddit, String selfTextHTML, String
            selfText, String suggestedSort, String userReports, String linkFlairText, String id, int gilded, boolean
                        archived, boolean clicked, String author, String media, int score, boolean over18, boolean
            hidden, String previewImageURL, int numComments, String thumbnailURL, String subredditID, boolean
            hideScore, boolean edited, int downs, String secureMediaEmbed, boolean saved, String postHint, boolean
            stickied, String from, boolean isSelf, String fromID, String permalink, boolean locked, String name,
                String url, String authorFlairText, boolean quarantine, String title, long createdUTC, String
                        distinguished, String modReports, boolean visited) {
        this.domain = domain;
        this.bannedBy = bannedBy;
        this.subreddit = subreddit;
        this.selfTextHTML = selfTextHTML;
        this.selfText = selfText;
        this.suggestedSort = suggestedSort;
        this.userReports = userReports;
        this.linkFlairText = linkFlairText;
        this.id = id;
        this.gilded = gilded;
        this.archived = archived;
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
        this.edited = 0.0;
        this.downs = downs;
        this.secureMediaEmbed = secureMediaEmbed;
        this.saved = saved;
        this.postHint = postHint;
        this.stickied = stickied;
        this.from = from;
        this.isSelf = isSelf;
        this.fromID = fromID;
        this.permalink = permalink;
        this.locked = locked;
        this.name = name;
        this.url = url;
        this.authorFlairText = authorFlairText;
        this.quarantine = quarantine;
        this.title = title;
        this.createdUTC = createdUTC;
        this.distinguished = distinguished;
        this.modReports = modReports;
        this.visited = visited;
    }

    public Post(String domain, String banned_by, String subreddit, String selfTextHTML, String selftext, String
            suggestedSort, String user_reports, String linkFlairText, String id, int gilded, boolean archived,
                boolean clicked, String author, String media, int score, boolean over_18, boolean hidden, String
                        previewImageURL, int num_comments, String thumbnail, String subreddit_id, boolean hide_score,
                double edited, int downs, String secureMediaEmbed, boolean saved, String postHint, boolean stickied,
                String from, boolean is_self, String fromID, String permalink, boolean locked, String name, String
                        url, String authorFlairText, boolean quarantine, String title, long created_utc, String
                        distinguished, String mod_reports, boolean visited) {
        this.domain = domain;
        bannedBy = banned_by;
        this.subreddit = subreddit;
        this.selfTextHTML = selfTextHTML;
        selfText = selftext;
        this.suggestedSort = suggestedSort;
        userReports = user_reports;
        this.linkFlairText = linkFlairText;
        this.id = id;
        this.gilded = gilded;
        this.archived = archived;
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
        this.downs = downs;
        this.secureMediaEmbed = secureMediaEmbed;
        this.saved = saved;
        this.postHint = postHint;
        this.stickied = stickied;
        this.from = from;
        isSelf = is_self;
        this.fromID = fromID;
        this.permalink = permalink;
        this.locked = locked;
        this.name = name;
        this.url = url;
        this.authorFlairText = authorFlairText;
        this.quarantine = quarantine;
        this.title = title;
        createdUTC = created_utc;
        this.distinguished = distinguished;
        modReports = mod_reports;
        this.visited = visited;
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

    public String getSelfTextHTML() {
        return selfTextHTML;
    }

    public String getSelfText() {
        return selfText;
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

    public String getId() {
        return id;
    }

    public int getGilded() {
        return gilded;
    }

    public boolean isArchived() {
        return archived;
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

    public int getDowns() {
        return downs;
    }

    public String getSecureMediaEmbed() {
        return secureMediaEmbed;
    }

    public boolean isSaved() {
        return saved;
    }

    public String getPostHint() {
        return postHint;
    }

    public boolean isStickied() {
        return stickied;
    }

    public String getFrom() {
        return from;
    }

    public boolean isSelf() {
        return isSelf;
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

    public String getName() {
        return name;
    }

    public String getURL() {
        return url;
    }

    public String getAuthorFlairText() {
        return authorFlairText;
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
}
