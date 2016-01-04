package com.reddit.material;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rushil Perera on 1/1/2016.
 */
public class Util {

    private static final String TAG = "Util";

    public static Post generatePost(JSONObject object) {
        try {
            Post post;
            post = new Post(object.getString("domain"), object.getString("banned_by"), object.getString("subreddit"),
                    object.isNull("suggested_sort") ? null : object.getString("suggested_sort"), object.getJSONArray
                    ("user_reports").toString(), object.isNull("link_flair_text") ? null : object.getString
                    ("link_flair_text"), object.getInt("gilded"), object.getBoolean("clicked"), object.getString
                    ("author"), object.isNull("media") ? null : object.getJSONObject("media").toString(), object.getInt
                    ("score"), object.getBoolean("over_18"), object.getBoolean("hidden"), object.isNull("preview") ?
                    null : object.getJSONObject("preview").getJSONArray("images").getJSONObject(0).getJSONObject
                    ("source").getString("url"), object.getInt("num_comments"), object.getString("thumbnail"), object
                    .getString("subreddit_id"), object.getBoolean("hide_score"), object.optDouble("edited", -1.0),
                    object.getBoolean("saved"), object.getBoolean("stickied"), object.isNull("from") ? null : object
                    .getJSONObject("from").toString(), object.isNull("from_id") ? null : object.getJSONObject
                    ("from_id").toString(), object.getString("permalink"), object.getBoolean("locked"), object
                    .getString("name"), object.getString("url"), object.getBoolean("quarantine"), object.getString
                    ("title"), (long) object.getDouble("created_utc"), object.isNull("distinguished") ? null : object
                    .getString("distinguished"), object.getJSONArray("mod_reports").toString(), object.getBoolean
                    ("visited"), object.isNull("likes") ? 0 : object.getBoolean("likes") ? 1 : -1, object.isNull
                    ("selftext_html") ? "" : object.getString("selftext_html"));
            return post;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Comment generateComment(JSONObject object) {
        try {
            Comment comment;
            comment = new Comment(object.getString("subreddit_id"), object.getString("link_id"), object.getBoolean
                    ("saved"), object.getString("name"), object.getInt("gilded"), object.getBoolean("archived"),
                    object.getString("author"), object.getInt("score"), object.getString("body_html"), object.optDouble
                    ("edited", -1.0), object.getBoolean("score_hidden"), object.getLong("created_utc"), object.isNull
                    ("author_flair_text") ? "" : object.getString("author_flair_text"), object.isNull("likes") ? 0 :
                    object.getBoolean("likes") ? 1 : -1, null);
            comment.setReplies(object.optJSONObject("replies") == null ? null : object
                    .getJSONObject("replies").getJSONObject("data").getJSONArray("children"));
            comment.setUserReports(object.getJSONArray("user_reports"));
            return comment;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Subreddit generateSubreddit(JSONObject object) {
        try {
            Subreddit subreddit;
            subreddit = new Subreddit(object.getString("display_name"), object.getString("title"), object.getString
                    ("name"), object.getString("public_description"), object.getString("public_description"), object
                    .isNull("comment_score_hide_mins") ? 0 : object.getInt("comment_score_hide_mins"), object.getString
                    ("subreddit_type"), !object.isNull("over18") && object.getBoolean("over18"), object.isNull
                    ("subscribers") ? 0 : object.getLong("subscribers"), object.getLong("created_utc"));
            return subreddit;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
