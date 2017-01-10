package com.vml.login;

/**
 * Created by tway on 12/7/16.
 */

public class LoginPresenter {
    private LoginView view;
    private FormListener formListener;
    private LoginData loginData;
    private String photoUrl;
    private String displayName;
    private boolean isShowGoogleLogin = true;
    private boolean isShowFacebookLogin = true;
    private boolean isShowEmailLogin = true;
    private boolean isShowPasswordLogin = true;
    private boolean isShowSmartLock = true;

    public void attachView(LoginView view) {
        this.view = view;
        updateViewState();
    }

    private void updateViewState() {
        if (view == null) return;
        if (!isShowGoogleLogin) view.hideGoogleLogin();
        if (!isShowFacebookLogin) view.hideFacebookLogin();
        if ((!isShowGoogleLogin && !isShowFacebookLogin)
                || (!isShowPasswordLogin && !isShowEmailLogin)) view.hideOrText();

        if (isShowEmailLogin && isShowPasswordLogin) view.showEmailAndPasswordForm();
        else if (isShowEmailLogin) view.showEmailOnlyForm();
        else view.hideEmailForm();

        if (isShowSmartLock) view.enableSmartLock();
        else view.disableSmartLock();
    }

    public void setShowGoogleLogin(boolean showGoogleLogin) {
        isShowGoogleLogin = showGoogleLogin;
        updateViewState();
    }

    public void setShowFacebookLogin(boolean showFacebookLogin) {
        isShowFacebookLogin = showFacebookLogin;
        updateViewState();
    }

    public void setShowEmailLogin(boolean showEmailLogin) {
        isShowEmailLogin = showEmailLogin;
        updateViewState();
    }

    public void setShowPasswordLogin(boolean showPasswordLogin) {
        isShowPasswordLogin = showPasswordLogin;
        updateViewState();
    }

    public void setShowSmartLock(boolean showSmartLock) {
        isShowSmartLock = showSmartLock;
        updateViewState();
    }


    public void setFormListener(FormListener formListener) {
        this.formListener = formListener;
    }

    public void finishLogin() {
        view.hideProgress();
        if (formListener != null) formListener.onLoginCompleted(loginData);
    }

    public void viewCreateAccountButtonClicked() {
        boolean isSkipView = formListener != null && formListener.onViewCreateAccountButtonClicked();
        if (!isSkipView) view.showCreateAccountView();
    }

    public void viewForgotPasswordButtonClicked() {
        boolean isSkipView = formListener != null && formListener.onViewForgotPasswordButtonClicked();
        if (!isSkipView) view.showForgotPasswordView();
    }

    public void loginFormSubmitted(String email, String password) {
        view.showProgress();
        if (formListener != null) formListener.onLoginFormSubmitted(email, password);
    }

    public void createAccountFormSubmitted(String email, String password) {
        view.showProgress();
        if (formListener != null) formListener.onCreateAccountFormSubmitted(email, password);
    }

    public void forgotPasswordFormSubmitted(String email) {
        view.showProgress();
        if (formListener != null) formListener.onForgotPasswordFormSubmitted(email);
    }

    public void thirdPartyLoginButtonClicked(ThirdPartyLoginView thirdPartyLoginView) {
        if (thirdPartyLoginView == null) view.showUnavailableAlert();
        else {
            thirdPartyLoginView.showLogin(new ThirdPartyLoginView.LoginResult() {
                @Override
                public void onLoginResult(LoginData loginData) {
                    if (loginData != null) view.loginSucceeded(loginData);
                }

                @Override
                public void onLoginError(String message) {
                    view.showAlert(message);
                }
            });
        }
    }

    public void setLoginData(LoginData d) {
        this.loginData = new LoginData(
                d.getMethod(),
                d.getAccessToken(),
                d.getId(),
                d.getEmail() != null ? d.getEmail() : view.getEmailString(),
                d.getDisplayName() != null ? d.getDisplayName() : getDisplayName(),
                getPhotoUrl() != null ? getPhotoUrl() : d.getPhotoUrl()
        );
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void detatchView() {
        formListener = null;
        view = null;
    }


}
