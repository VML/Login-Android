package com.vml.login;

/**
 * Created by tway on 12/7/16.
 */

public interface LoginView {
    void enableSmartLock();

    void disableSmartLock();

    void hideOrText();

    void hideFacebookLogin();

    void showFacebookLogin();

    void hideGoogleLogin();

    void hideEmailForm();

    void showEmailOnlyForm();

    void showEmailAndPasswordForm();

    void showCreateAccountView();

    void showForgotPasswordView();

    void showProgress();

    void hideProgress();

    void loginSucceeded(LoginData d);

    String getEmailString();

    String getPasswordString();

    void showAlert(String message);

    void showUnavailableAlert();
}
