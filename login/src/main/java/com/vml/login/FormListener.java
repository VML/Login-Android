package com.vml.login;

/**
 * Created by tway on 12/5/16.
 */
public interface FormListener {

    void onLoginFormSubmitted(String email, String password);

    void onCreateAccountFormSubmitted(String email, String password);

    void onForgotPasswordFormSubmitted(String email);

    boolean onViewCreateAccountButtonClicked();

    boolean onViewForgotPasswordButtonClicked();

    void onLoginCompleted(LoginData loginData);
}
