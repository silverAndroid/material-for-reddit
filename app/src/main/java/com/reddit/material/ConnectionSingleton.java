package com.reddit.material;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rushil Perera on 10/28/2015.
 */
public class ConnectionSingleton {

    private static ConnectionSingleton instance;
    private final Context context;
    private final Gson jsonConverter;

    private ConnectionSingleton(Context context) {
        this.context = context;
        jsonConverter = new Gson();
    }

    public static void createInstance(Context context) {
        if (!hasInstance())
            instance = new ConnectionSingleton(context);
    }

    public static ConnectionSingleton getInstance() {
        return instance;
    }

    public static boolean hasInstance() {
        return instance != null;
    }

    public void getSubredditData(String subreddit) {
        final ArrayList<Post> posts = new ArrayList<>();
        Ion.with(context)
                .load(subreddit.isEmpty() ? "https://www.reddit.com/.json" : "https://www.reddit.com/r/" + subreddit
                        + "/.json")
                .setHeader("User-Agent", "User-Agent: android:com.reddit.material:v1.0.0 (by /u/silverAndroid)")
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        // do stuff with the result or error
                        if (e == null) {
                            JsonArray postsJson = result.getAsJsonObject("data").getAsJsonArray("children");
                            for (JsonElement postsElement : postsJson) {
                                JsonObject postJson = postsElement.getAsJsonObject().getAsJsonObject("data");
                                JSONObject postJSON;
                                try {
                                    postJSON = new JSONObject(postJson.getAsJsonObject().toString());
                                    Post post;
                                    if (Double.isNaN(postJSON.optDouble("edited")))
                                        post = new Post(postJSON.getString("domain"), postJSON.getString("banned_by"), postJSON
                                                .getString("subreddit"), postJSON.isNull("selftext_html") ? null : postJSON
                                                .getString("selftext_html"), postJSON.getString("selftext"), postJSON.isNull
                                                ("suggested_sort") ? null : postJSON.getString("suggested_sort"), postJSON
                                                .getJSONArray("user_reports").toString(), postJSON.isNull("link_flair_text") ? null :
                                                postJSON.getString("link_flair_text"), postJSON.getString("id"), postJSON.getInt
                                                ("gilded"), postJSON.getBoolean("archived"), postJSON.getBoolean("clicked"), postJSON
                                                .getString("author"), postJSON.isNull("media") ? null : postJSON.getJSONObject
                                                ("media").toString(), postJSON.getInt("score"), postJSON.getBoolean("over_18"),
                                                postJSON.getBoolean("hidden"), postJSON.isNull("preview") ? null : postJSON
                                                .getJSONObject("preview").getJSONArray("images").getJSONObject(0).getJSONObject
                                                        ("source").getString("url"), postJSON.getInt("num_comments"), postJSON
                                                .getString("thumbnail"), postJSON.getString("subreddit_id"), postJSON.getBoolean
                                                ("hide_score"), postJSON.getBoolean("edited"), postJSON.getInt("downs"), postJSON
                                                .isNull("secure_media_embed") ? null : postJSON.getJSONObject
                                                ("secure_media_embed").toString(), postJSON.getBoolean("saved"), postJSON.has
                                                ("post_hint") ? postJSON.getString("post_hint") : "", postJSON.getBoolean
                                                ("stickied"), postJSON.isNull("from") ? null : postJSON.getJSONObject("from")
                                                .toString(), postJSON.getBoolean("is_self"), postJSON.isNull("from_id") ? null :
                                                postJSON.getJSONObject("from_id").toString(), postJSON.getString("permalink"),
                                                postJSON.getBoolean("locked"), postJSON.getString("name"), postJSON.getString
                                                ("url"), postJSON.isNull("author_flair_text") ? null : postJSON.getString
                                                ("author_flair_text"), postJSON.getBoolean("quarantine"), postJSON
                                                .getString("title"), (long) postJSON.getDouble("created_utc"), postJSON.isNull
                                                ("distinguished") ? null : postJSON.getString("distinguished"), postJSON
                                                .getJSONArray("mod_reports").toString(), postJSON.getBoolean("visited"));
                                    else
                                        post = new Post(postJSON.getString("domain"), postJSON.getString("banned_by"), postJSON
                                                .getString("subreddit"), postJSON.isNull("selftext_html") ? null : postJSON
                                                .getString("selftext_html"), postJSON.getString("selftext"), postJSON.isNull
                                                ("suggested_sort") ? null : postJSON.getString("suggested_sort"), postJSON
                                                .getJSONArray("user_reports").toString(), postJSON.isNull("link_flair_text") ?
                                                null : postJSON.getString("link_flair_text"), postJSON.getString("id"), postJSON
                                                .getInt("gilded"), postJSON.getBoolean("archived"), postJSON.getBoolean
                                                ("clicked"), postJSON.getString("author"), postJSON.isNull("media") ? null :
                                                postJSON.getJSONObject("media").toString(), postJSON.getInt("score"), postJSON
                                                .getBoolean("over_18"), postJSON.getBoolean("hidden"), postJSON.isNull("preview")
                                                ? null : postJSON.getJSONObject("preview").getJSONArray("images").getJSONObject
                                                (0).getJSONObject("source").getString("url"), postJSON.getInt("num_comments"),
                                                postJSON.getString("thumbnail"), postJSON.getString("subreddit_id"), postJSON
                                                .getBoolean("hide_score"), postJSON.getDouble("edited"), postJSON.getInt("downs")
                                                , postJSON.isNull("secure_media_embed") ? null : postJSON.getJSONObject
                                                ("secure_media_embed").toString(), postJSON.getBoolean("saved"), postJSON.has
                                                ("post_hint") ? postJSON.getString("post_hint") : "", postJSON.getBoolean
                                                ("stickied"), postJSON.isNull("from") ? null : postJSON.getJSONObject("from")
                                                .toString(), postJSON.getBoolean("is_self"), postJSON.isNull("from_id") ? null :
                                                postJSON.getJSONObject("from_id").toString(), postJSON.getString("permalink"),
                                                postJSON.getBoolean("locked"), postJSON.getString("name"), postJSON.getString
                                                ("url"), postJSON.isNull("author_flair_text") ? null : postJSON.getString
                                                ("author_flair_text"), postJSON.getBoolean("quarantine"), postJSON
                                                .getString("title"), (long) postJSON.getDouble("created_utc"), postJSON.isNull
                                                ("distinguished") ? null : postJSON.getString("distinguished"), postJSON
                                                .getJSONArray("mod_reports").toString(), postJSON.getBoolean("visited"));
                                    posts.add(post);
                                } catch (JSONException e1) {
                                    Toast.makeText(getContext(), e1.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        } else
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        SubredditFragment.getInstance().getAdapter().addPosts(posts);
                        Log.d("refresh", "refreshing finished");
                        SubredditFragment.getInstance().getSwipeRefreshLayout().setRefreshing(false);
                    }
                });
    }

    public void getPostData(String permalink) {
        final ArrayList<Comment> comments = new ArrayList<>();
        Ion.with(getContext())
                .load("https://www.reddit.com" + permalink + "/.json")
                .setHeader("User-Agent", "User-Agent: android:com.reddit.material:v1.0.0 (by /u/silverAndroid)")
                .asJsonArray().setCallback(new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                JsonArray commentsJSON = result.get(1).getAsJsonObject().getAsJsonObject("data").getAsJsonArray
                        ("children");
                int i = 0;
                for (JsonElement commentsElement : commentsJSON) {
                    String kind = commentsElement.getAsJsonObject().getAsJsonPrimitive("kind").getAsString();
                    JsonObject commentJSON = commentsElement.getAsJsonObject().getAsJsonObject("data");
                    if (kind.equals("t1")) {
                        Comment comment = new Comment(commentJSON.getAsJsonPrimitive("subreddit_id").getAsString(),
                                commentJSON.getAsJsonPrimitive("link_id").getAsString(), commentJSON.getAsJsonPrimitive
                                ("saved").getAsBoolean(), commentJSON.getAsJsonPrimitive("id").getAsString(), commentJSON
                                .getAsJsonPrimitive("gilded").getAsInt(), commentJSON.getAsJsonPrimitive("archived")
                                .getAsBoolean(), commentJSON.getAsJsonPrimitive("author").getAsString(), commentJSON
                                .getAsJsonPrimitive("score").getAsInt(), commentJSON.getAsJsonPrimitive("body")
                                .getAsString(), commentJSON.getAsJsonPrimitive("edited").getAsBoolean(), commentJSON
                                .getAsJsonPrimitive("body_html").getAsString(), commentJSON.getAsJsonPrimitive
                                ("score_hidden").getAsBoolean(), commentJSON.getAsJsonPrimitive("created_utc").getAsLong
                                (), commentJSON.get("author_flair_text").isJsonNull() ? "" : commentJSON
                                .getAsJsonPrimitive("author_flair_text").getAsString());
                        comment.setReplies(commentJSON.get("replies").isJsonPrimitive() ? new JsonArray() : commentJSON
                                .getAsJsonObject("replies").getAsJsonObject("data").getAsJsonArray("children"));
                        comment.setUserReports(commentJSON.getAsJsonArray("user_reports"));
                        comments.add(comment);
                    }
                }
                CommentsActivity.getAdapter().addComments(comments);
            }
        });
    }

    public void loadImage(String url, final ImageView imageView, final ProgressBar loading) {
        Ion.with(getContext()).load(url)
                .setHeader("User-Agent", "User-Agent: android:com.reddit.material:v1.0.0 (by /u/silverAndroid)")
                .progressBar(loading).progressHandler(new ProgressCallback() {
            @Override
            public void onProgress(final long downloaded, final long total) {
                loading.post(new Runnable() {
                    @Override
                    public void run() {
                        loading.setProgress((int) ((double) (downloaded) / (double) (total) * 100));
                    }
                });
            }
        }).intoImageView(imageView).setCallback(new FutureCallback<ImageView>() {
            @Override
            public void onCompleted(Exception e, ImageView result) {
                if (e != null) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    public void loadImage(final String url, final SubsamplingScaleImageView imageView, final ProgressBar
            loading, final String tag) {
        if (url.contains("https://www.flickr.com")) {
            boolean modifyURL = true;
            for (String constant : ConstantMap.getInstance().getConstantMap().keySet())
                if (url.endsWith(constant)) {
                    modifyURL = false;
                    break;
                }
            if (modifyURL) {
                Ion.with(getContext()).load("https://www.flickr.com/services/rest/?method=flickr.photos" +
                        ".search&format=json&api_key=9e79c7a853db58eec8122a6e11e58713&user_id=" + url
                        .split("/")[4] + "&nojsoncallback=1")
                        .setHeader("User-Agent", "User-Agent: android:com.reddit.material:v1.0.0 (by /u/silverAndroid)")
                        .group("flickr").asJsonObject().setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        JsonArray photos = result.getAsJsonObject("photos").getAsJsonArray("photo");
                        JsonObject urlJSON;
                        URL modifiedURL = new URL(url);
                        for (JsonElement photo : photos) {
                            if ((urlJSON = photo.getAsJsonObject()).getAsJsonPrimitive("id").getAsLong() == Long
                                    .parseLong(url.split("/")[5].trim())) {
                                long farmID = urlJSON.get("farm").getAsLong();
                                long serverID = urlJSON.get("server").getAsLong();
                                long id = urlJSON.get("id").getAsLong();
                                String secretID = urlJSON.get("secret").getAsString();
                                modifiedURL.setUrl(String.format("https://farm%d.staticflickr.com/%d/%d_%s.jpg", farmID,
                                        serverID, id, secretID));
                                break;
                            }
                        }
                        loadImage(modifiedURL.getUrl(), imageView, loading, tag);
                    }
                });
            }
            return;
        } else if (url.contains("http://imgur.com")) {
            Ion.with(getContext()).load("https://api.imgur.com/3/image/" + url.split("/")[3])
                    .setHeader("User-Agent", "User-Agent: android:com.reddit.material:v1.0.0 (by /u/silverAndroid)")
                    .addHeader("Authorization", "Client-ID " + APIKey.getInstance().getAPIKey(APIKey
                            .IMGUR_CLIENT_ID_KEY))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            String url = result.getAsJsonObject("data").getAsJsonPrimitive("link").getAsString();
                            loadImage(url, imageView, loading, tag);
                        }
                    });
            return;
        }
        Ion.with(getContext()).load(url)
                .setHeader("User-Agent", "User-Agent: android:com.reddit.material:v1.0.0 (by /u/silverAndroid)")
                .progressHandler(new ProgressCallback() {
                    @Override
                    public void onProgress(final long downloaded, final long total) {
                        loading.post(new Runnable() {
                            @Override
                            public void run() {
                                loading.setProgress((int) ((double) (downloaded) / (double) (total) * 100));
                            }
                        });
                    }
                }).group(tag).asBitmap().setCallback(new FutureCallback<Bitmap>() {
            @Override
            public void onCompleted(Exception e, Bitmap result) {
                if (result != null)
                    imageView.setImage(ImageSource.bitmap(result));
                if (e != null) {
                    if (e.getMessage() == null || e.getMessage().equals(""))
                        Toast.makeText(getContext(), "Image failed to load!", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("failed_url", url);
                }
                loading.setVisibility(View.GONE);
            }
        });
    }

    public void loadGIF(String url, final VideoView videoView, final ProgressBar loading, final String tag) {
        Log.d("url_before", url);
        url = url.replace(".gifv", ".gif");
        Log.d("url_after", url);
        final String modifiedURL = url;
        Ion.with(getContext()).load("http://gfycat.com/cajax/checkUrl/" + url)
                .setHeader("User-Agent", "User-Agent: android:com.reddit.material:v1.0.0 (by /u/silverAndroid)")
                .group(tag)
                .asJsonObject().setCallback(new FutureCallback<JsonObject>() {
            @Override
            public void onCompleted(Exception e, JsonObject result) {
                if (result.getAsJsonPrimitive("urlKnown").getAsBoolean()) {
                    videoView.setVideoURI(Uri.parse(result.getAsJsonPrimitive("mp4Url").getAsString()));
                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            loading.setVisibility(View.GONE);
                            videoView.start();
                            mp.setLooping(true);
                        }
                    });
                } else {
                    Ion.with(getContext()).load("http://upload.gfycat.com/transcode?fetchUrl=" + modifiedURL)
                            .setHeader("User-Agent", "User-Agent: android:com.reddit.material:v1.0.0 (by " +
                                    "/u/silverAndroid)")
                            .asJsonObject().
                            setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    videoView.setVideoURI(Uri.parse(result.getAsJsonPrimitive("mp4Url").getAsString()));
                                    videoView.start();
                                    videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            loading.setVisibility(View.GONE);
                                            mp.setLooping(true);
                                        }
                                    });
                                }
                            });
                }
            }
        });
    }

    private Context getContext() {
        return context;
    }
}
