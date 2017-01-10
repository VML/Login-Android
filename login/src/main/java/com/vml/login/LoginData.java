package com.vml.login;

/**
 * Created by tway on 11/21/16.
 */

public class LoginData {

    private String accessToken;
    private String email;
    private String displayName;
    private String photoUrl;
    private String id;
    private Method method;

    public enum Method {EMAIL, GOOGLE, FACEBOOK}

    public LoginData(Method method, String accessToken, String id, String email, String displayName, String photoUrl) {
        this.method = method;
        this.accessToken = accessToken;
        this.email = email;
        this.displayName = displayName;
        this.photoUrl = photoUrl;
        this.id = id;
    }

    public Method getMethod() {
        return method;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getId() {
        return id;
    }
}
