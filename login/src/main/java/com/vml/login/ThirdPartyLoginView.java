package com.vml.login;

/**
 * Created by tway on 12/5/16.
 */

public interface ThirdPartyLoginView {

    void showLogin(LoginResult listener);

    interface LoginResult {
        void onLoginResult(LoginData loginData);

        void onLoginError(String message);
    }
}
