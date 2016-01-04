package com.reddit.material;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Animatable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import com.reddit.material.libraries.facebook.ZoomableDraweeView;

import org.json.JSONArray;
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

    private static final String TAG = "ConnectionSingleton";
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
        AsyncHttpClient subredditClient = new AsyncHttpClient();
        final PersistentCookieStore cookieStore = new PersistentCookieStore(context);
        subredditClient.setCookieStore(cookieStore);
        subredditClient.setUserAgent(ConstantMap.getInstance().getConstant("user_agent"));
        subredditClient.get(context, subreddit.isEmpty() ? "https://www.reddit.com/.json" : "https://www.reddit" +
                ".com/r/" + subreddit + "/.json", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray postsJson;
                try {
                    postsJson = response.getJSONObject("data").getJSONArray("children");
                    for (int i = 0; i < postsJson.length(); i++) {
                        JSONObject postJSON = postsJson.getJSONObject(i).getJSONObject("data");
                        Post post;
                        post = Util.generatePost(postJSON);
                        posts.add(post);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                SubredditFragment.getInstance().getAdapter().addPosts(posts);
                if (SubredditFragment.getInstance().getProgressBar().getVisibility() == View.GONE)
                    SubredditFragment.getInstance().getSwipeRefreshLayout().setRefreshing(false);
                else
                    SubredditFragment.getInstance().getProgressBar().setVisibility(View.GONE);
            }
        });
    }

    public void getSubreddits() {
        String accessToken = Authentication.getInstance().getAccessToken();
        final ArrayList<Subreddit> subredditsList = new ArrayList<>();
        Ion.with(context).load(accessToken.isEmpty() ? "https://www.reddit.com/subreddits/default/.json?limit=50" :
                "https://oauth.reddit.com/subreddits/mine/.json")
                .setHeader("User-Agent", ConstantMap.getInstance().getConstant("user_agent"))
                .addHeader("Authorization", "bearer " + accessToken)
                .asJsonObject().withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if (result.getResult().has("error")) {
                            refreshAccessToken();
                            getSubreddits();
                            return;
                        }
                        JsonArray subredditsArray = result.getResult().getAsJsonObject("data").getAsJsonArray
                                ("children");
                        Menu menu = MainActivity.getInstance().getNavigationView().getMenu();
                        SubMenu subredditsMenu = menu.addSubMenu("Subreddits");
                        if (Authentication.getInstance().isLoggedIn())
                            subredditsMenu.add("frontpage");
                        subredditsMenu.add("all");
                        for (JsonElement subredditsElement : subredditsArray) {
                            if (subredditsElement.getAsJsonObject().getAsJsonPrimitive("kind").getAsString().equals("t5")) {
                                JsonObject subredditJSON = subredditsElement.getAsJsonObject().getAsJsonObject("data");
                                Subreddit subreddit;
                                try {
                                    subreddit = Util.generateSubreddit(new JSONObject(subredditJSON.toString()));
                                    subredditsList.add(subreddit);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                        Collections.sort(subredditsList);
                        for (Subreddit subreddit : subredditsList) {
                            subredditsMenu.add(subreddit.getName());
                        }
                        MainActivity.getInstance().onNavigationItemSelected(subredditsMenu.getItem(0));
                    }
                });
    }

    public void reloadSubreddits() {
        MainActivity.getInstance().getNavigationView().getMenu().removeItem(0);
        MainActivity.getInstance().invalidateOptionsMenu();
        getSubreddits();
    }

    public void getLinkData(String permalink) {
        final ArrayList<Comment> comments = new ArrayList<>();
        final Post[] post = new Post[1];
        AsyncHttpClient subredditClient = new AsyncHttpClient();
        final PersistentCookieStore cookieStore = new PersistentCookieStore(context);
        subredditClient.setCookieStore(cookieStore);
        subredditClient.setUserAgent(ConstantMap.getInstance().getConstant("user_agent"));
//        permalink = "/r/sircmpwn/comments/3t925b/testing_commenting";
        subredditClient.get(context, "https://www.reddit.com" + permalink.replace("/?ref=search_posts", "") + "/.json",
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        try {
                            JSONArray commentsJSON = response.getJSONObject(1).getJSONObject("data").getJSONArray
                                    ("children");
                            for (int i = 0; i < commentsJSON.length(); i++) {
                                String kind = commentsJSON.getJSONObject(i).getString("kind");
                                JSONObject commentJSON = commentsJSON.getJSONObject(i).getJSONObject("data");
                                if (kind.equals("t1")) {
                                    Comment comment = Util.generateComment(commentJSON);
                                    comments.add(comment);
                                } else if (kind.equals("t3")) {
                                    post[0] = Util.generatePost(commentJSON);
                                    CommentActivity.getAdapter().setPost(post[0]);
                                }
                            }
                            CommentActivity.getAdapter().addComments(comments);

                            if (CommentActivity.getProgressBar().getVisibility() == View.GONE)
                                CommentActivity.getSwipeRefreshLayout().setRefreshing(false);
                            else
                                CommentActivity.getProgressBar().setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                            errorResponse) {
                        Log.e(TAG, "onFailure: " + errorResponse.toString(), throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                            throwable) {
                        Log.e(TAG, "onFailure: " + responseString, throwable);
                        Log.d(TAG, "onFailure: " + this.getRequestURI().toString());
                    }
                });
    }

    public void loadImage(String url, final SimpleDraweeView imageView, final ProgressBar loading) {

        ControllerListener<ImageInfo> listener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                loading.setVisibility(View.GONE);
            }
        };

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(2560, 2560))
                .setProgressiveRenderingEnabled(true)
                .setLocalThumbnailPreviewsEnabled(true)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setAutoRotateEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setTapToRetryEnabled(true)
                .setOldController(imageView.getController())
                .setControllerListener(listener)
                .build();

        imageView.setController(controller);
    }

    public void loadImage(final String url, final ZoomableDraweeView imageView, final ProgressBar loading) {
        if (url.contains("https://www.flickr.com")) {
            boolean modifyURL = true;
            for (String constant : ConstantMap.getInstance().getConstantMap().keySet())
                if (url.endsWith(constant)) {
                    modifyURL = false;
                    break;
                }
            if (modifyURL) {
                Ion.with(context).load("https://www.flickr.com/services/rest/?method=flickr.photos" +
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
                        loadImage(modifiedURL.getUrl(), imageView, loading);
                    }
                });
            }
            return;
        } else if (url.contains("http://imgur.com")) {
            Ion.with(context).load("https://api.imgur.com/3/image/" + url.split("/")[3])
                    .setHeader("User-Agent", ConstantMap.getInstance().getConstant("user_agent"))
                    .addHeader("Authorization", "Client-ID " + APIKey.getInstance().getAPIKey(APIKey
                            .IMGUR_CLIENT_ID_KEY))
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            String url = result.getAsJsonObject("data").getAsJsonPrimitive("link").getAsString();
                            loadImage(url, imageView, loading);
                        }
                    });
            return;
        }

        ControllerListener listener = new BaseControllerListener() {
            @Override
            public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
                loading.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                loading.setVisibility(View.GONE);
            }
        };

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(2560, 2560))
                .setProgressiveRenderingEnabled(true)
                .setLocalThumbnailPreviewsEnabled(true)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setAutoRotateEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setTapToRetryEnabled(true)
                .setOldController(imageView.getController())
                .setControllerListener(listener)
                .build();
        imageView.setController(controller);
    }

    public void loadGIF(String url, final VideoView videoView, final ProgressBar loading, final String tag) {
        url = url.replace(".gifv", ".gif");
        final String modifiedURL = url;
        Ion.with(context).load("http://gfycat.com/cajax/checkUrl/" + url)
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
                } else
                    uploadGIF(modifiedURL, videoView, loading);
            }
        });
    }

    public void uploadGIF(String url, final VideoView videoView, final ProgressBar loading) {
        Ion.with(context).load("http://upload.gfycat.com/transcode?fetchUrl=" + url)
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

    public void loadVideo(String url, final VideoView videoView, final ProgressBar loading) {
        videoView.setVideoURI(Uri.parse(url));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                loading.setVisibility(View.GONE);
                videoView.start();
            }
        });
    }

    public void comment(final String text, final String id, final EditText editMessage) {
        AsyncHttpClient commentClient = new AsyncHttpClient();
        commentClient.addHeader("Authorization", "bearer " + Authentication.getInstance().getAccessToken());
        commentClient.setUserAgent(ConstantMap.getInstance().getConstant("user_agent"));
        HashMap<String, String> bodyParams = new HashMap<>(3);
        bodyParams.put("api_type", "json");
        bodyParams.put("text", text);
        bodyParams.put("thing_id", id);
        RequestParams params = new RequestParams(bodyParams);
        commentClient.post(context, "https://oauth.reddit.com/api/comment/.json", params, new JsonHttpResponseHandler
                () {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONObject commentsJSON = response.getJSONObject("json").getJSONObject("data").getJSONArray
                            ("things").getJSONObject(0);
                    String kind = commentsJSON.getString("kind");
                    JSONObject commentJSON = commentsJSON.getJSONObject("data");
                    if (kind.equals("t1")) {
                        Comment comment = Util.generateComment(commentJSON);
                        editMessage.getText().clear();
                        CommentActivity.getAdapter().addComment(comment);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void search(String query) {
        final ArrayList<Thing> results = new ArrayList<>();
        final boolean[] subredditsComplete = {false};
        final boolean[] postsComplete = {false};

        AsyncHttpClient subredditsClient = new AsyncHttpClient();
        subredditsClient.setUserAgent(ConstantMap.getInstance().getConstant("user_agent"));
        HashMap<String, String> bParams = new HashMap<>();
        bParams.put("sort", "relevance");
        bParams.put("q", query);
        RequestParams parameters = new RequestParams(bParams);
        subredditsClient.get(context, "https://www.reddit.com/subreddits/search.json", parameters, new
                JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d(TAG, "onSuccess: " + response.toString());
                        try {
                            JSONArray subredditsArray = response.getJSONObject("data").getJSONArray("children");
                            for (int i = 0; i < subredditsArray.length(); i++) {
                                JSONObject subredditJSON = subredditsArray.getJSONObject(i);
                                String kind = subredditJSON.getString("kind");
                                if (kind.equals("t5")) {
                                    Subreddit subreddit = Util.generateSubreddit(subredditJSON.getJSONObject("data"));
                                    results.add(subreddit);
                                }
                            }
                            subredditsComplete[0] = true;
                            completeSearch(postsComplete[0], subredditsComplete[0], results);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            subredditsComplete[0] = true;
                            completeSearch(postsComplete[0], subredditsComplete[0], results);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject
                            errorResponse) {
                        Log.e(TAG, "onFailure: " + errorResponse.toString(), throwable);
                        subredditsComplete[0] = true;
                        completeSearch(postsComplete[0], subredditsComplete[0], results);
                    }
                });

        AsyncHttpClient linksClient = new AsyncHttpClient();
        linksClient.setUserAgent(ConstantMap.getInstance().getConstant("user_agent"));
        HashMap<String, String> bodyParams = new HashMap<>();
        bodyParams.put("q", query);
        bodyParams.put("sort", "relevance");
        bodyParams.put("t", "all");
        RequestParams params = new RequestParams(bodyParams);
        linksClient.get(context, "https://www.reddit.com/search.json", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess: " + response.toString());
                try {
                    JSONArray linksArray = response.getJSONObject("data").getJSONArray("children");
                    for (int i = 0; i < linksArray.length(); i++) {
                        JSONObject result = linksArray.getJSONObject(i);
                        String kind = result.getString("kind");
                        if (kind.equals("t3")) {
                            Post post = Util.generatePost(result.getJSONObject("data"));
                            results.add(post);
                        }
                    }

                    postsComplete[0] = true;
                    completeSearch(postsComplete[0], subredditsComplete[0], results);
                } catch (JSONException e) {
                    e.printStackTrace();
                    postsComplete[0] = true;
                    completeSearch(postsComplete[0], subredditsComplete[0], results);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "onFailure: " + errorResponse.toString(), throwable);
                postsComplete[0] = true;
                completeSearch(postsComplete[0], subredditsComplete[0], results);
            }
        });
    }

    private void completeSearch(boolean subredditsComplete, boolean postsComplete, ArrayList<Thing> results) {
        if (subredditsComplete && postsComplete) {
            SearchResultsActivity.getAdapter().addItems(results);
            SearchResultsActivity.hideProgressBar();
        }
    }

    public void login(final Context context) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context, R.style.DialogTheme)
                .setTitle("Login to Reddit")
                .setView(R.layout.layout_login_dialog)
                .setPositiveButton("Login", null)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button login = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Dialog view = (Dialog) dialog;
                        EditText usernameEditText = (EditText) view.findViewById(R.id.username);
                        EditText passwordEditText = (EditText) view.findViewById(R.id.password);
                        String username = usernameEditText.getText().toString();
                        String password = passwordEditText.getText().toString();
                        Log.d("check", String.valueOf(checkUsername(username) && checkPassword(password)));
                        if (checkUsername(username) && checkPassword(password)) {
                            login(username, password, context);
                            alertDialog.dismiss();
                        } else {
                            if (!checkUsername(username))
                                usernameEditText.setError("Username must have between 3 and 20 characters");
                            if (!checkPassword(password))
                                passwordEditText.setError("Password must have at least 6 characters!");
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

    private void login(String username, String password, final Context context) {
        AsyncHttpClient loginClient = new AsyncHttpClient();
        final PersistentCookieStore cookieStore = new PersistentCookieStore(context);
        loginClient.setCookieStore(cookieStore);
        loginClient.setUserAgent(ConstantMap.getInstance().getConstant("user_agent"));
        HashMap<String, String> bodyParameters = new HashMap<>();
        bodyParameters.put("user", username);
        bodyParameters.put("passwd", password);
        bodyParameters.put("api_type", "json");
        RequestParams params = new RequestParams(bodyParameters);
        loginClient.post(context, "https://www.reddit.com/api/login/" + username, params, new
                JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        final String uh;
                        try {
                            PreferenceManager.getDefaultSharedPreferences(context).edit().putString("modhash", uh =
                                    response.getJSONObject("json").getJSONObject("data").getString("modhash")).apply();
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
                                            //TODO: Add dialog that lets user know that process couldn't continue and be respectful of the choice to allow permission
                                        }
                                    }).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void authorizeLogin(PersistentCookieStore cookieStore, String uh) {
        final String state;
        AsyncHttpClient authorizeClient = new AsyncHttpClient();
        authorizeClient.setCookieStore(cookieStore);
        authorizeClient.setUserAgent(ConstantMap.getInstance().getConstant("user_agent"));
        final HashMap<String, String> authorizeParams = new HashMap<>();
        authorizeParams.put("client_id", APIKey.getInstance().getAPIKey(APIKey.REDDIT_CLIENT_ID_KEY));
        authorizeParams.put("redirect_uri", "http://silverandroid.me/");
        authorizeParams.put("scope", "mysubreddits,vote,submit");
        authorizeParams.put("state", state = UUID.randomUUID().toString());
        authorizeParams.put("response_type", "code");
        authorizeParams.put("duration", "permanent");
        authorizeParams.put("uh", uh);
        authorizeParams.put("authorize", "Allow");
        RequestParams requestParams = new RequestParams(authorizeParams);
        authorizeClient.post(context, "https://www.reddit.com/api/v1/authorize", requestParams, new
                JsonHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable
                            throwable) {
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
        Ion.with(context).load("POST", "https://www.reddit.com/api/v1/access_token")
                .setHeader("User-Agent", ConstantMap.getInstance().getConstant("user_agent"))
                .basicAuthentication(APIKey.getInstance().getAPIKey(APIKey.REDDIT_CLIENT_ID_KEY), "")
                .setBodyParameter("grant_type", "authorization_code")
                .setBodyParameter("code", code.get(0))
                .setBodyParameter("redirect_uri", redirectURL)
                .asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                Authentication.getInstance().saveAccessToken(result.getResult().getAsJsonPrimitive("access_token")
                        .getAsString());
                Authentication.getInstance().saveRefreshToken(result.getResult().getAsJsonPrimitive("refresh_token")
                        .getAsString());
                MainActivity.getInstance().getNavigationView().getMenu().getItem(0).setTitle("Log out");
                reloadSubreddits();
            }
        });
    }

    private void refreshAccessToken() {
        Ion.with(context).load("POST", "https://www.reddit.com/api/v1/access_token")
                .setHeader("User-Agent", ConstantMap.getInstance().getConstant("user_agent"))
                .basicAuthentication(APIKey.getInstance().getAPIKey(APIKey.REDDIT_CLIENT_ID_KEY), "")
                .setBodyParameter("grant_type", "refresh_token")
                .setBodyParameter("refresh_token", Authentication.getInstance().getRefreshToken())
                .asJsonObject().withResponse().setCallback(new FutureCallback<Response<JsonObject>>() {
            @Override
            public void onCompleted(Exception e, Response<JsonObject> result) {
                Log.d("result", result.getResult().toString());
                Authentication.getInstance().saveAccessToken(result.getResult().getAsJsonPrimitive("access_token")
                        .getAsString());
            }
        });
    }

    public void vote(String id, int dir) {
        Log.d("id", id);
        String accessToken = Authentication.getInstance().getAccessToken();
        Ion.with(context).load("POST", "https://oauth.reddit.com/api/vote.json")
                .setHeader("User-Agent", ConstantMap.getInstance().getConstant("user_agent"))
                .addHeader("Authorization", "bearer " + accessToken)
                .setBodyParameter("dir", String.valueOf(dir))
                .setBodyParameter("id", id)
                .asJsonObject().withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        Log.d("result", result.getResult().toString());
                    }
                });
        Log.d("id", String.valueOf(dir));
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
