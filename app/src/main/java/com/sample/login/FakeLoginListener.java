package com.sample.login;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;

import com.vml.login.FormListener;
import com.vml.login.LoginData;
import com.vml.login.LoginDataStore;
import com.vml.login.LoginLayout;

/**
 * Created by tway on 12/9/16.
 */
class FakeLoginListener implements FormListener {
    private Activity activity;
    private LoginLayout loginLayout;

    public FakeLoginListener(Activity activity, LoginLayout loginLayout) {
        this.activity = activity;
        this.loginLayout = loginLayout;
    }

    @Override
    public void onLoginFormSubmitted(String email, String password) {
        if (password == null || password.isEmpty()) {
            fakeEmailLoginLink(email);
        } else {
            fakePasswordAuthWithDelay(email, password);
        }
    }

    private void fakeEmailLoginLink(String email) {
        //TODO: email login link here
        loginLayout.hideDialog();
        loginLayout.showAlert("We sent you a link to log in.  Please check your inbox.");
    }

    @Override
    public void onCreateAccountFormSubmitted(String email, String password) {

    }

    @Override
    public void onLoginCompleted(LoginData loginData) {
        LoginDataStore.of(activity.getApplicationContext()).put(loginData);
        activity.finish();
    }

    @Override
    public void onForgotPasswordFormSubmitted(String username) {

    }

    @Override
    public boolean onViewCreateAccountButtonClicked() {
        return false;
    }

    @Override
    public boolean onViewForgotPasswordButtonClicked() {
        return false;
    }

    private void fakePasswordAuthWithDelay(final String email, final String password) {
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        if (email.equals("test") && password.equals("test")) {
                            String token = "fakeToken123";
                            loginLayout.loginSucceeded(getFakeLoginData(token, email));
                        } else {
                            loginLayout.showAlert(activity.getString(R.string.login_failed_message));
                        }
                    }
                },
                2000
        );
    }

    private LoginData getFakeLoginData(String token, String email) {
        return new LoginData(LoginData.Method.EMAIL, token, null, email, null, null);
    }

    public void handleNewIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            validateLinkAndLogin(intent.getData());
        }
    }

    private void validateLinkAndLogin(Uri link) {
        //TODO: validate link and login
        String token = "fakeToken123";
        String email = "whatever@mail.com";
        onLoginCompleted(getFakeLoginData(token, email));
    }

}
