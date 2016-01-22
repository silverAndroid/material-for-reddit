package com.reddit.material;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.customtabs.CustomTabsIntent;
import android.util.Log;

import com.reddit.material.libraries.google.CustomTabActivityHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

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

    public static NormalComment generateNormalComment(JSONObject object) {
        try {
            NormalComment comment;
            comment = new NormalComment(object.getString("name"), object.getString
                    ("subreddit_id"), object.getString("link_id"), object.getBoolean("saved"), object.getInt
                    ("gilded"), object.getBoolean("archived"), object.getString("author"), object.getInt("score"),
                    object.getString("body_html"), object.optDouble("edited", -1.0), object.getBoolean
                    ("score_hidden"), object.getLong("created_utc"), object.isNull("author_flair_text") ? "" : object
                    .getString("author_flair_text"), object.isNull("likes") ? 0 : object.getBoolean("likes") ? 1 : -1);
            comment.setReplies(object.optJSONObject("replies") == null ? null : object
                    .getJSONObject("replies").getJSONObject("data").getJSONArray("children"));
            comment.setUserReports(object.getJSONArray("user_reports"));
            return comment;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UnloadedComments generateUnloadedComments(JSONObject object) {
        try {
            UnloadedComments comments;
            comments = new UnloadedComments(object.getString("name"), object.getInt("count"), object.getJSONArray
                    ("children"));
            Log.i(TAG, "generateUnloadedComments: " + object.toString());
            return comments;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Subreddit generateSubreddit(JSONObject object) {
        try {
            Subreddit subreddit;
            subreddit = new Subreddit(object.getString("display_name"), object.getString("title"), object.getString
                    ("name"), object.getString("description_html"), object.getString("public_description_html"), object
                    .isNull("comment_score_hide_mins") ? 0 : object.getInt("comment_score_hide_mins"), object.getString
                    ("subreddit_type"), !object.isNull("over18") && object.getBoolean("over18"), object.isNull
                    ("subscribers") ? 0 : object.getLong("subscribers"), object.getLong("created_utc"), !object.isNull
                    ("user_is_subscriber") && object.getBoolean("user_is_subscriber"));
            return subreddit;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Image generateImage(JSONObject object) {
        Image image;
        try {
            image = new Image(object.getString("title"), object.getString("link"),
                    object.getInt("width"), object.getInt("height"));
            String url = image.getUrl();
            String[] linkArray = url.split("/");
            String[] lowResArray = linkArray[3].split("\\.");
            String lowResURL = "https://i.imgur.com/" + lowResArray[0] + "t." + lowResArray[1];
            image.setLowResURL(lowResURL);
            return image;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void linkClicked(Activity activity, String url) {
        linkClicked(activity, url, false);
    }

    public static void linkClicked(Activity activity, String url, boolean forceBrowser) {
        if (forceBrowser)
            loadThroughBrowser(activity, url);
        else {
            if (ConstantMap.getInstance().isYoutube(url)) {
                Intent intent = new Intent(activity, YouTubeActivity.class);
                intent.putExtra("url", url);
                activity.startActivity(intent);
            } else if (ConstantMap.getInstance().isGIF(url)) {
                Intent intent = new Intent(activity, VideoActivity.class);
                intent.putExtra("url", url);
                activity.startActivity(intent);
            } else if (ConstantMap.getInstance().isGallery(url)) {
                Intent intent = new Intent(activity, ImageGalleryActivity.class);
                intent.putExtra("albumURL", url);
                activity.startActivity(intent);
            } else if (ConstantMap.getInstance().isImage(url)) {
                Intent intent = new Intent(activity, ImageActivity.class);
                intent.putExtra("url", url);
                activity.startActivity(intent);
            } else if (ConstantMap.getInstance().isReddit(url)) {
                String redditPath = url.replaceFirst("https?://(www\\.)?redd.?it(.com)?/", "");
                Log.i(TAG, "linkClicked: " + redditPath);
                String[] redditPathArray = redditPath.split("/");
                if (redditPathArray.length > 2) {
                    String redditCategory = redditPathArray[2];
                    if (redditCategory.equals("comments")) {
                        Intent intent = new Intent(activity, CommentActivity.class);
                        intent.putExtra("permalink", "/" + redditPath);
                        activity.startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.putExtra("subreddit", redditPath.substring(2));
                    activity.startActivity(intent);
                }
            } else {
                if (Pattern.matches("/?(r|u)/.*", url)) {
                    url = "https://www.reddit.com" + (url.startsWith("/") ? "" : "/") + url;
                    linkClicked(activity, url, false);
                    return;
                }
                loadThroughBrowser(activity, url);
            }
        }
    }

    private static void loadThroughBrowser(Activity activity, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            builder.setToolbarColor(activity.getResources().getColor(R.color.colorPrimary, null));
        } else {
            builder.setToolbarColor(activity.getResources().getColor(R.color.colorPrimary));
        }
        builder.setShowTitle(true);
        CustomTabActivityHelper.openCustomTab(activity, builder.build(), Uri.parse(url), null);
    }
}
