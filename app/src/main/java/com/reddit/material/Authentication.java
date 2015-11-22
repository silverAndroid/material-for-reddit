package com.reddit.material;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Rushil Perera on 11/10/2015.
 */
public class Authentication {

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
        PreferenceManager.getDefaultSharedPreferences(context).edit().remove("refreshToken").commit();
    }
}
