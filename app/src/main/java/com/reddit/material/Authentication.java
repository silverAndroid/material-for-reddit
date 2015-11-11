package com.reddit.material;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by Rushil Perera on 11/10/2015.
 */
public class Authentication {

    private final Context context;
    private static Authentication instance;

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
        return PreferenceManager.getDefaultSharedPreferences(context).contains("modhash");
    }

    public String getModHash() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("modhash", "");
    }

    public String getCookie() {
        return PreferenceManager.getDefaultSharedPreferences(context).getString("cookie", "");
    }
}
