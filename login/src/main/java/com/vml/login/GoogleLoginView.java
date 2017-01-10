package com.vml.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by tway on 11/23/16.
 */

public class GoogleLoginView implements ThirdPartyLoginView {
    private static int CODE = 111;
    private LoginResult listener;
    private AppCompatActivity activity;
    private GoogleSignInOptions gso;
    private GoogleApiClient apiClient;

    public GoogleLoginView(AppCompatActivity activity, String clientId) {
        this.activity = activity;
        this.gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientId)
                .requestEmail()
                .build();
    }

    @Override
    public void showLogin(LoginResult listener) {
        this.listener = listener;

        if (apiClient == null) {
            apiClient = new GoogleApiClient.Builder(activity)
                    .enableAutoManage(activity, null)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        activity.startActivityForResult(signInIntent, CODE);
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result != null && listener != null) {
                if (result.isSuccess()) listener.onLoginResult(mapToLoginData(result.getSignInAccount()));
                else if (result.getStatus().getStatusMessage() != null) listener.onLoginError(result.getStatus().getStatusMessage());
                else listener.onLoginError("An unknown error occurred.  Google Sign In may not be configured.");
            }
        }
    }

    public static LoginData mapToLoginData(GoogleSignInAccount account) {
        LoginData loginData = new LoginData(
                LoginData.Method.GOOGLE,
                account.getIdToken(),
                account.getId(),
                account.getEmail(),
                account.getDisplayName(),
                account.getPhotoUrl() == null ? null : account.getPhotoUrl().toString()
        );
        return loginData;
    }
}
