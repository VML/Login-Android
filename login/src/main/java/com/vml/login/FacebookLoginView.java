package com.vml.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by tway on 12/5/16.
 */

public class FacebookLoginView implements ThirdPartyLoginView, FacebookCallback<LoginResult> {

    private CallbackManager callbackManager;
    private Activity activity;
    LoginResult listener;

    public FacebookLoginView(Activity activity, String facebookAppId) {
        this.activity = activity;
        FacebookSdk.setApplicationId(facebookAppId);
        FacebookSdk.sdkInitialize(activity.getApplicationContext());
        this.callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, this);

    }

    @Override
    public void showLogin(LoginResult listener) {
        this.listener = listener;
        LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("public_profile", "email", "user_birthday"));
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public static LoginData mapToLoginData(String token, JSONObject f) {
        try {
            LoginData loginData = new LoginData(
                    LoginData.Method.FACEBOOK,
                    token,
                    f.has("id") ? f.getString("id") : null,
                    f.has("email") ? f.getString("email") : null,
                    f.has("first_name") && f.has("last_name") ? f.getString("first_name") + " " + f.getString("last_name") : null,
                    f.has("picture") ? f.getString("picture") : null
            );
            return loginData;
        } catch (JSONException e){
            Log.e("", e.getMessage());
            return null;
        }
    }

    @Override
    public void onSuccess(final com.facebook.login.LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                listener.onLoginResult(mapToLoginData(loginResult.getAccessToken().getToken(), object));
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields","id,name,email,gender,birthday");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    public void onCancel() {
        listener.onLoginResult(null);
    }

    @Override
    public void onError(FacebookException error) {
        listener.onLoginError(error.getMessage());
    }
}
