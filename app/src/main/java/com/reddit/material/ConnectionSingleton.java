package com.reddit.material;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;
import com.koushikdutta.ion.Response;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Rushil Perera on 10/28/2015.
 */
public class ConnectionSingleton {

    private static ConnectionSingleton instance;
    private final Context context;

    private ConnectionSingleton(Context context) {
        this.context = context;
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
                .setHeader("User-Agent", ConstantMap.getInstance().getConstant("user_agent"))
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
                                                .getString("subreddit"), postJSON.isNull
                                                ("suggested_sort") ? null : postJSON.getString("suggested_sort"), postJSON
                                                .getJSONArray("user_reports").toString(), postJSON.isNull("link_flair_text") ? null :
                                                postJSON.getString("link_flair_text"), postJSON.getString("id"), postJSON.getInt
                                                ("gilded"), postJSON.getBoolean("clicked"), postJSON
                                                .getString("author"), postJSON.isNull("media") ? null : postJSON.getJSONObject
                                                ("media").toString(), postJSON.getInt("score"), postJSON.getBoolean("over_18"),
                                                postJSON.getBoolean("hidden"), postJSON.isNull("preview") ? null : postJSON
                                                .getJSONObject("preview").getJSONArray("images").getJSONObject(0).getJSONObject
                                                        ("source").getString("url"), postJSON.getInt("num_comments"), postJSON
                                                .getString("thumbnail"), postJSON.getString("subreddit_id"), postJSON.getBoolean
                                                ("hide_score"), postJSON.getBoolean("saved"), postJSON.getBoolean
                                                ("stickied"), postJSON.isNull("from") ? null : postJSON.getJSONObject("from")
                                                .toString(), postJSON.isNull("from_id") ? null :
                                                postJSON.getJSONObject("from_id").toString(), postJSON.getString("permalink"),
                                                postJSON.getBoolean("locked"), postJSON.getString("name"), postJSON.getString
                                                ("url"), postJSON.getBoolean("quarantine"), postJSON
                                                .getString("title"), (long) postJSON.getDouble("created_utc"), postJSON.isNull
                                                ("distinguished") ? null : postJSON.getString("distinguished"), postJSON
                                                .getJSONArray("mod_reports").toString(), postJSON.getBoolean("visited"));
                                    else
                                        post = new Post(postJSON.getString("domain"), postJSON.getString("banned_by"), postJSON
                                                .getString("subreddit"), postJSON.isNull
                                                ("suggested_sort") ? null : postJSON.getString("suggested_sort"), postJSON
                                                .getJSONArray("user_reports").toString(), postJSON.isNull("link_flair_text") ?
                                                null : postJSON.getString("link_flair_text"), postJSON.getString("id"), postJSON
                                                .getInt("gilded"), postJSON.getBoolean
                                                ("clicked"), postJSON.getString("author"), postJSON.isNull("media") ? null :
                                                postJSON.getJSONObject("media").toString(), postJSON.getInt("score"), postJSON
                                                .getBoolean("over_18"), postJSON.getBoolean("hidden"), postJSON.isNull("preview")
                                                ? null : postJSON.getJSONObject("preview").getJSONArray("images").getJSONObject
                                                (0).getJSONObject("source").getString("url"), postJSON.getInt("num_comments"),
                                                postJSON.getString("thumbnail"), postJSON.getString("subreddit_id"), postJSON
                                                .getBoolean("hide_score"), postJSON.getDouble("edited"),
                                                postJSON.getBoolean("saved"), postJSON.getBoolean
                                                ("stickied"), postJSON.isNull("from") ? null : postJSON.getJSONObject("from")
                                                .toString(), postJSON.isNull("from_id") ? null :
                                                postJSON.getJSONObject("from_id").toString(), postJSON.getString("permalink"),
                                                postJSON.getBoolean("locked"), postJSON.getString("name"), postJSON.getString
                                                ("url"), postJSON.getBoolean("quarantine"), postJSON
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

    public void getSubreddits() {
        //loading default subreddits
        getSubreddits("");
    }

    public void getSubreddits(String modhash) {
        Log.d("getSubreddits()", Authentication.getInstance().getCookie());
        final ArrayList<Subreddit> subredditsList = new ArrayList<>();
        Ion.with(getContext()).load(modhash.isEmpty() ? "https://www.reddit.com/subreddits/default/.json?limit=50" :
                "http://www.reddit.com/reddits/mine.json?modhash=" + modhash)
                .setHeader("User-Agent", ConstantMap.getInstance().getConstant("user_agent"))
                .addHeader("Cookie", Authentication.getInstance().getCookie())
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        JsonArray subredditsArray = result.getAsJsonObject("data").getAsJsonArray("children");
                        Menu menu = MainActivity.getInstance().getNavigationView().getMenu();
                        SubMenu subredditsMenu = menu.addSubMenu("Subreddits");
                        subredditsMenu.add("all");
                        for (JsonElement subredditsElement : subredditsArray) {
                            if (subredditsElement.getAsJsonObject().getAsJsonPrimitive("kind").getAsString().equals("t5")) {
                                JsonObject subredditJSON = subredditsElement.getAsJsonObject().getAsJsonObject("data");
                                Subreddit subreddit = new Subreddit(subredditJSON.getAsJsonPrimitive("display_name")
                                        .getAsString(), subredditJSON.getAsJsonPrimitive("description").getAsString(),
                                        subredditJSON.getAsJsonPrimitive("public_description").getAsString(), subredditJSON
                                        .getAsJsonPrimitive("comment_score_hide_mins").getAsInt(), subredditJSON
                                        .getAsJsonPrimitive("subreddit_type").getAsString(), subredditJSON.getAsJsonPrimitive
                                        ("over18").getAsBoolean(), subredditJSON.getAsJsonPrimitive("subscribers").getAsInt()
                                        , subredditJSON.getAsJsonPrimitive("created_utc").getAsLong());
                                subredditsList.add(subreddit);
                            }
                        }
                        Collections.sort(subredditsList);
                        for (Subreddit subreddit : subredditsList) {
                            subredditsMenu.add(subreddit.getName());
                        }
                    }
                });
    }

    public void getPostData(String permalink) {
        final ArrayList<Comment> comments = new ArrayList<>();
        Ion.with(getContext())
                .load("https://www.reddit.com" + permalink + "/.json")
                .setHeader("User-Agent", ConstantMap.getInstance().getConstant("user_agent"))
                .asJsonArray().setCallback(new FutureCallback<JsonArray>() {
            @Override
            public void onCompleted(Exception e, JsonArray result) {
                JsonArray commentsJSON = result.get(1).getAsJsonObject().getAsJsonObject("data").getAsJsonArray
                        ("children");
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
                .setHeader("User-Agent", ConstantMap.getInstance().getConstant("user_agent"))
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
                        .setHeader("User-Agent", ConstantMap.getInstance().getConstant("user_agent"))
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
                    .setHeader("User-Agent", ConstantMap.getInstance().getConstant("user_agent"))
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
                .setHeader("User-Agent", ConstantMap.getInstance().getConstant("user_agent"))
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
                .setHeader("User-Agent", ConstantMap.getInstance().getConstant("user_agent"))
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
                            .setHeader("User-Agent", ConstantMap.getInstance().getConstant("user_agent"))
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

    public void login(final Context context) {
        new AlertDialog.Builder(context, R.style.DialogTheme)
                .setTitle("Login to Reddit")
                .setView(R.layout.layout_login)
                .setPositiveButton("Login", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog view = (Dialog) dialog;
                        EditText usernameEditText = (EditText) view.findViewById(R.id.username);
                        EditText passwordEditText = (EditText) view.findViewById(R.id.password);
                        String username = usernameEditText.getText().toString();
                        String password = passwordEditText.getText().toString();
                        if (checkUsername(username) && checkPassword(password)) {
                            login(username, password, context);
                        } else {
                            if (!checkUsername(username))
                                usernameEditText.setError("Username must have between 3 and 20 characters");
                            if (!checkPassword(password))
                                passwordEditText.setError("Password must have at least 6 characters!");
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void login(String username, String password, final Context context) {
        Log.d("login", "login");
        final String url;
        Log.d("user", username);
        Log.d("passwd", password);
        AsyncHttpClient loginClient = new AsyncHttpClient();
        final PersistentCookieStore cookieStore = new PersistentCookieStore(getContext());
        loginClient.setCookieStore(cookieStore);
        loginClient.setUserAgent(ConstantMap.getInstance().getConstant("user_agent"));
        HashMap<String, String> bodyParameters = new HashMap<>();
        bodyParameters.put("user", username);
        bodyParameters.put("passwd", password);
        bodyParameters.put("api_type", "json");
        RequestParams params = new RequestParams(bodyParameters);
        loginClient.post(getContext(), url = "https://www.reddit.com/api/login/" + username, params, new
                JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        final String uh;
                        Log.d("url", url);
                        try {
                            PreferenceManager.getDefaultSharedPreferences(getContext()).edit().putString("modhash", uh =
                                    response.getJSONObject("json").getJSONObject("data").getString("modhash")).apply();
                            Log.d("uh", uh);
                            new AlertDialog.Builder(context, R.style.DialogTheme)
                                    .setTitle(R.string.permission_title)
                                    .setMessage(R.string.permission_message)
                                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            authorizeLogin(cookieStore, uh);
                                        }
                                    })
                                    .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void authorizeLogin(PersistentCookieStore cookieStore, String uh) {
        Log.d("authorize", "authorize");
        final String state;
        AsyncHttpClient authorizeClient = new AsyncHttpClient();
        authorizeClient.setCookieStore(cookieStore);
        authorizeClient.setUserAgent(ConstantMap.getInstance().getConstant("user_agent"));
        final HashMap<String, String> authorizeParams = new HashMap<>();
        authorizeParams.put("client_id", APIKey.getInstance().getAPIKey(APIKey.REDDIT_CLIENT_ID_KEY));
        authorizeParams.put("redirect_uri", "http://silverandroid.me/");
        authorizeParams.put("scope", "mysubreddits");
        authorizeParams.put("state", state = UUID.randomUUID().toString());
        authorizeParams.put("response_type", "code");
        authorizeParams.put("duration", "permanent");
        authorizeParams.put("uh", uh);
        authorizeParams.put("authorize", "Allow");
        RequestParams requestParams = new RequestParams(authorizeParams);
        authorizeClient.post(getContext(), "https://www.reddit.com/api/v1/authorize", requestParams, new
                JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                            throwable) {
                        Log.d("location", headers[6].getValue());
                        try {
                            LinkedHashMap<String, List<String>> parameters = splitQuery(new java.net.URL(headers[6]
                                    .getValue()));
                            if (state.equals(parameters.get("state").get(0)))
                                retrieveAccessTokens(parameters.get("code"), authorizeParams.get("redirect_uri"));
                        } catch (UnsupportedEncodingException | MalformedURLException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void retrieveAccessTokens(List<String> code, String redirectURL) {
        Log.d("retrieve", "retrieve");
        Ion.with(getContext()).load("POST", "https://www.reddit.com/api/v1/access_token")
                .setHeader("User-Agent", ConstantMap.getInstance().getConstant("user_agent"))
                .basicAuthentication(APIKey.getInstance().getAPIKey(APIKey.REDDIT_CLIENT_ID_KEY), "")
                .setBodyParameter("grant_type", "authorization_code")
                .setBodyParameter("code", code.get(0))
                .setBodyParameter("redirect_uri", redirectURL)
                .asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                Log.d("result", result.getResult().toString());
            }
        });
    }

    private boolean checkUsername(String username) {
        int length = username.length();
        return length >= 3 && length <= 20;
    }

    private boolean checkPassword(String password) {
        int length = password.length();
        return length >= 5;
    }

    private LinkedHashMap<String, List<String>> splitQuery(java.net.URL url) throws UnsupportedEncodingException {
        final LinkedHashMap<String, List<String>> query_pairs = new LinkedHashMap<>();
        final String[] pairs = url.getQuery().split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            if (!query_pairs.containsKey(key)) {
                query_pairs.put(key, new LinkedList<String>());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1),
                    "UTF-8") : null;
            query_pairs.get(key).add(value);
        }
        return query_pairs;
    }
}
