package com.reddit.material;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Rushil Perera on 11/10/2015.
 */
public class Authentication {

    private static final String TAG = "Authentication";
    private static Authentication instance;
    private final Context context;

    private Authentication(Context context) {
        this.context = context;
    }

    public static void newInstance(Context context) {
        instance = new Authentication(context);
    }

    public static Authentication getInstance() {
        return instance;
    }

    public boolean isLoggedIn() {
        return PreferenceManager.getDefaultSharedPreferences(context).contains("accessToken");
    }

    public String getAccessToken() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("accessToken", "");
    }

    public void saveAccessToken(String accessToken) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("accessToken", accessToken).commit();
    }

    public String getRefreshToken() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("refreshToken", "");
    }

    public void saveRefreshToken(String refreshToken) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("refreshToken", refreshToken).commit();
    }

    public void logout() {
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove("accessToken").commit();
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove("refreshToken").apply();
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
        AsyncHttpClient accessTokenRetriever = new AsyncHttpClient();
        accessTokenRetriever.setBasicAuth(APIKey.getInstance().getAPIKey(APIKey.REDDIT_CLIENT_ID_KEY), "");
        accessTokenRetriever.setUserAgent(ConstantMap.getInstance().getUserAgent());
        RequestParams params = new RequestParams();
        params.put("grant_type", "authorization_code");
        params.put("code", code.get(0));
        params.put("redirect_uri", redirectURL);
        accessTokenRetriever.post("https://www.reddit.com/api/v1/access_token", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    saveAccessToken(response.getString("access_token"));
                    saveRefreshToken(response.getString("refresh_token"));
                    MainActivity.getInstance().getNavigationView().getMenu().getItem(0).setTitle("Log out");
                    MainActivity.getInstance().reloadSubreddits();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void refreshAccessToken() {
        AsyncHttpClient accessTokenRefresher = new AsyncHttpClient();
        accessTokenRefresher.setBasicAuth(APIKey.getInstance().getAPIKey(APIKey.REDDIT_CLIENT_ID_KEY), "");
        accessTokenRefresher.setUserAgent(ConstantMap.getInstance().getUserAgent());
        RequestParams params = new RequestParams();
        params.put("grant_type", "refresh_token");
        params.put("refresh_token", getRefreshToken());
        accessTokenRefresher.post("https://www.reddit.com/api/v1/access_token", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "onSuccess: " + response.toString());
                try {
                    saveAccessToken(response.getString("access_token"));
                    MainActivity.getInstance().getSubreddits();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
