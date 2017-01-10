package com.vml.login;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class LoginPresenterTests {

    LoginPresenter presenter;
    @Mock LoginView view;
    @Mock FormListener formListener;
    @Mock ThirdPartyLoginView thirdPartyView;
    LoginData loginData = new LoginData(null, null, null, null, null, null);

    @Before
    public void beforeEach() {
        presenter = new LoginPresenter();
        presenter.setFormListener(formListener);
        presenter.attachView(view);
    }

    @Test
    public void testHideThirdPartyButtons() {
        verify(view, times(0)).hideGoogleLogin();
        presenter.setShowGoogleLogin(true);
        verify(view, times(0)).hideGoogleLogin();
        presenter.setShowGoogleLogin(false);
        verify(view, times(1)).hideGoogleLogin();

        verify(view, times(0)).hideFacebookLogin();
        presenter.setShowFacebookLogin(true);
        verify(view, times(0)).hideFacebookLogin();
        presenter.setShowFacebookLogin(false);
        verify(view, times(1)).hideFacebookLogin();
    }

    @Test
    public void testEmailFormOptions() {
        verify(view, times(1)).showEmailAndPasswordForm();
        presenter.setShowEmailLogin(true);
        verify(view, times(2)).showEmailAndPasswordForm();

        verify(view, times(0)).showEmailOnlyForm();
        presenter.setShowPasswordLogin(false);
        verify(view, times(1)).showEmailOnlyForm();

        verify(view, times(0)).hideEmailForm();
        presenter.setShowEmailLogin(false);
        verify(view, times(1)).hideEmailForm();
    }

    @Test
    public void testLoginFormSubmitted() {
        verify(formListener, times(0)).onLoginFormSubmitted("test", "test");

        presenter.loginFormSubmitted("test", "test");
        verify(formListener, times(1)).onLoginFormSubmitted("test", "test");
    }

    @Test
    public void showCreateAccountViewAfterClick() {
        when(formListener.onViewCreateAccountButtonClicked()).thenReturn(false);
        verify(view, times(0)).showCreateAccountView();
        presenter.viewCreateAccountButtonClicked();
        verify(view, times(1)).showCreateAccountView();
    }

    @Test
    public void dontShowCreateAccountViewAfterClick() {
        when(formListener.onViewCreateAccountButtonClicked()).thenReturn(true);
        verify(view, times(0)).showCreateAccountView();
        presenter.viewCreateAccountButtonClicked();
        verify(view, times(0)).showCreateAccountView();
    }

    @Test
    public void thirdPartyLoginSuccess() {
        verify(view, times(0)).loginSucceeded(any(LoginData.class));

        presenter.thirdPartyLoginButtonClicked(new ThirdPartyLoginView() {
            @Override
            public void showLogin(LoginResult listener) {
                listener.onLoginResult(loginData);
            }
        });

        verify(view, times(1)).loginSucceeded(any(LoginData.class));
        verify(view, times(0)).showAlert(anyString());
    }

    @Test
    public void thirdPartyLoginFail() {
        verify(view, times(0)).loginSucceeded(any(LoginData.class));

        presenter.thirdPartyLoginButtonClicked(new ThirdPartyLoginView() {
            @Override
            public void showLogin(LoginResult listener) {
                listener.onLoginError("adsfdf");
            }
        });

        verify(view, times(0)).loginSucceeded(any(LoginData.class));
        verify(view, times(1)).showAlert(anyString());
    }
}