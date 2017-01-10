package com.vml.login;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by tway on 11/30/16.
 */

public class LoginDataStore {

    Context context;
    SharedPreferences prefs;

    public LoginDataStore(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    static String METHOD = "method";
    static String PREFS_NAME = "prefsName";
    static String ACCESS_TOKEN = "accessToken";
    static String DISPLAY_NAME = "displayName";
    static String EMAIL = "email";
    static String PHOTO_URL = "photoUrl";
    static String ID = "id";

    private void put(String method, String accessToken, String id, String email, String displayName, String photoUrl) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(METHOD, method);
        editor.putString(ACCESS_TOKEN, accessToken);
        editor.putString(DISPLAY_NAME, displayName);
        editor.putString(EMAIL, email);
        editor.putString(PHOTO_URL, photoUrl);
        editor.putString(ID, id);
        editor.apply();
    }

    public void put(LoginData data) {
        put(
                data.getMethod() == null ? null : data.getMethod().toString(),
                data.getAccessToken(),
                data.getId(),
                data.getEmail(),
                data.getDisplayName(),
                data.getPhotoUrl()
        );
    }

    public LoginData get() {
        return new LoginData(
                get(METHOD) == null ? null : LoginData.Method.valueOf(get(METHOD)),
                get(ACCESS_TOKEN),
                get(ID),
                get(EMAIL),
                get(DISPLAY_NAME),
                get(PHOTO_URL)
        );
    }

    public void delete() {
        put(null, null, null, null, null, null);
    }

    public boolean exists() {
        return get().getAccessToken() != null;
    }

    private String get(String name) {
        return prefs.getString(name, null);
    }

    public static LoginDataStore of(Context context) {
        return new LoginDataStore(context);
    }
}
