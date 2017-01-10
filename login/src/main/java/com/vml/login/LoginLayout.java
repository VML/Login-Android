package com.vml.login;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.credentials.Credential;

/**
 * Created by tway on 11/17/16.
 */

public class LoginLayout extends LinearLayout implements LoginView, View.OnClickListener, DialogInterface.OnDismissListener, View.OnTouchListener {
    private Button logInButton;
    private Button viewCreateAccountButton;
    private Button googleButton;
    private Button facebookButton;
    private Button viewForgotPasswordButton;
    private Button continueButton;
    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressDialog progressDialog;
    private LinearLayout passwordLoginLayout;
    private TextView orTextView;
    private AlertDialog inputDialog;
    private SmartLockView smartLockView;
    private boolean smartLockAlreadyShown;
    private GoogleLoginView googleLoginView;
    private FacebookLoginView facebookLoginView;
    private LoginPresenter presenter = new LoginPresenter();

    public LoginLayout(Context context) {
        super(context);
        initAttributes(context, null);
    }

    public LoginLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttributes(context, attrs);
    }

    public LoginLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributes(context, attrs);
    }

    private void initAttributes(Context context, AttributeSet attrs) {
        setOrientation(VERTICAL);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoginLayout);
        presenter.setShowGoogleLogin(a.getBoolean(R.styleable.LoginLayout_showGoogleLogin, true));
        presenter.setShowFacebookLogin(a.getBoolean(R.styleable.LoginLayout_showFacebookLogin, true));
        presenter.setShowPasswordLogin(a.getBoolean(R.styleable.LoginLayout_showPasswordLogin, true));
        presenter.setShowEmailLogin(a.getBoolean(R.styleable.LoginLayout_showEmailLogin, true));
        presenter.setShowSmartLock(a.getBoolean(R.styleable.LoginLayout_showSmartLock, true));
        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        LayoutInflater.from(getContext()).inflate(R.layout.login_view, this, true);

        emailEditText = (EditText) findViewById(R.id.email);
        passwordEditText = (EditText) findViewById(R.id.login_password);
        logInButton = (Button) findViewById(R.id.login_button);
        googleButton = (Button) findViewById(R.id.google_button);
        facebookButton = (Button) findViewById(R.id.facebook_button);
        viewCreateAccountButton = (Button) findViewById(R.id.view_signup_button);
        viewForgotPasswordButton = (Button) findViewById(R.id.forgot_password_button);
        passwordLoginLayout = (LinearLayout) findViewById(R.id.password_login_layout);
        continueButton = (Button) findViewById(R.id.continue_button);
        orTextView = (TextView) findViewById(R.id.or);

        viewCreateAccountButton.setOnClickListener(this);
        viewForgotPasswordButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
        emailEditText.setOnTouchListener(this);

        presenter.attachView(this);
    }

    @Override
    public void enableSmartLock() {
        if (!isInEditMode() && smartLockView == null)
            smartLockView = new SmartLockView((AppCompatActivity) getContext());
    }

    @Override
    public void disableSmartLock() {
        smartLockView = null;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == googleButton.getId()) presenter.thirdPartyLoginButtonClicked(googleLoginView);
        if (id == facebookButton.getId()) presenter.thirdPartyLoginButtonClicked(facebookLoginView);
        if (id == viewCreateAccountButton.getId()) presenter.viewCreateAccountButtonClicked();
        if (id == viewForgotPasswordButton.getId()) presenter.viewForgotPasswordButtonClicked();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (smartLockView == null
                || motionEvent.getAction() != MotionEvent.ACTION_UP
                || smartLockAlreadyShown
                || emailEditText.getText().length() != 0) return false;
        showSmartLockGetIfEnabled();
        return true;
    }

    @Override
    public void hideOrText() {
        orTextView.setVisibility(GONE);
    }

    @Override
    public void hideFacebookLogin() {
        facebookButton.setVisibility(GONE);
    }

    @Override
    public void hideGoogleLogin() {
        googleButton.setVisibility(GONE);
    }

    public void setFormListener(FormListener formListener) {
        presenter.setFormListener(formListener);
    }

    @Override
    public void hideEmailForm() {
        emailEditText.setVisibility(GONE);
        continueButton.setVisibility(GONE);
        passwordLoginLayout.setVisibility(GONE);
    }

    @Override
    public void showEmailOnlyForm() {
        passwordLoginLayout.setVisibility(GONE);
        continueButton.setVisibility(VISIBLE);

        FormUtil.initFormListeners(emailEditText, "", null, continueButton, getWindow(),
                new FormUtil.SubmitListener() {
                    @Override
                    public void onFormSubmit(String email, String password) {
                        presenter.loginFormSubmitted(getEmailString(), getPasswordString());
                    }
                }
        );
    }

    @Override
    public void showEmailAndPasswordForm() {
        emailEditText.setVisibility(VISIBLE);
        passwordLoginLayout.setVisibility(VISIBLE);
        continueButton.setVisibility(GONE);

        FormUtil.initFormListeners(emailEditText, "", passwordEditText, logInButton, getWindow(),
                new FormUtil.SubmitListener() {
                    @Override
                    public void onFormSubmit(String email, String password) {
                        presenter.loginFormSubmitted(getEmailString(), getPasswordString());
                    }
                }
        );
    }

    private Window getWindow() {
        return isInEditMode() ? null : ((Activity) getContext()).getWindow();
    }

    @Override
    public void loginSucceeded(LoginData d) {
        presenter.setLoginData(d);
        if (smartLockView != null) {
            smartLockView.put(d.getEmail(), getPasswordString(), d.getDisplayName(), new SmartLockView.PutListener() {
                @Override
                public void onPutResult() {
                    presenter.finishLogin();
                }
            });
        }
    }

    @Override
    public String getEmailString() {
        return emailEditText.getText().toString();
    }

    @Override
    public String getPasswordString() {
        return passwordEditText.getText().toString();
    }

    @Override
    public void showAlert(String message) {
        hideProgress();
        DialogUtil.showAlert(message, getContext());
    }

    @Override
    public void showUnavailableAlert() {
        showAlert(getContext().getString(R.string.currently_unavailable));
    }

    public void hideDialog() {
        if (inputDialog != null) inputDialog.dismiss();
    }

    private void showSmartLockGetIfEnabled() {
        if (smartLockView == null) return;
        showProgress();
        smartLockView.get(new SmartLockView.GetListener() {
            @Override
            public void onGetResult(Credential credential, boolean didUserChoose) {
                extractDataFromSmartLockCredential(credential, didUserChoose);
            }
        });
        smartLockAlreadyShown = true;
    }

    private void extractDataFromSmartLockCredential(Credential credential, boolean didUserChoose) {
        hideProgress();
        if (credential == null) {
            emailEditText.requestFocusFromTouch();
            return;
        }

        if (credential.getId() != null) emailEditText.setText(credential.getId());
        if (credential.getPassword() != null && passwordLoginLayout.getVisibility() == VISIBLE) {
            passwordEditText.setText(credential.getPassword());
            if (didUserChoose) presenter.loginFormSubmitted(getEmailString(), getPasswordString());
        }
        if (credential.getProfilePictureUri() != null) {
            presenter.setPhotoUrl(credential.getProfilePictureUri().toString());
        }
        if (credential.getName() != null) {
            presenter.setDisplayName(credential.getName());
        }
    }

    @Override
    public void showCreateAccountView() {
        inputDialog = DialogUtil.showInput(R.string.create_account, R.layout.create_account_view, getContext(), this);

        FormUtil.initFormListeners(
                (EditText) inputDialog.findViewById(R.id.email),
                emailEditText.getText().toString(),
                (EditText) inputDialog.findViewById(R.id.signup_password),
                inputDialog.getButton(AlertDialog.BUTTON_POSITIVE),
                inputDialog.getWindow(),
                new FormUtil.SubmitListener() {
                    @Override
                    public void onFormSubmit(String email, String password) {
                        presenter.createAccountFormSubmitted(email, password);
                    }
                }
        );
    }

    @Override
    public void showForgotPasswordView() {
        inputDialog = DialogUtil.showInput(R.string.forgot_password_title, R.layout.forgot_password_view, getContext(), this);

        FormUtil.initFormListeners(
                (EditText) inputDialog.findViewById(R.id.email),
                emailEditText.getText().toString(),
                null,
                inputDialog.getButton(AlertDialog.BUTTON_POSITIVE),
                inputDialog.getWindow(),
                new FormUtil.SubmitListener() {
                    @Override
                    public void onFormSubmit(String email, String password) {
                        presenter.forgotPasswordFormSubmitted(email);
                    }
                }
        );
    }

    public void setGoogleLoginView(GoogleLoginView googleLoginView) {
        this.googleLoginView = googleLoginView;
    }

    public void setFacebookLoginView(FacebookLoginView facebookLoginView) {
        this.facebookLoginView = facebookLoginView;
    }

    @Override
    public void showProgress() {
        hideKeyboard();
        progressDialog = DialogUtil.showProgress(getContext());
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null) progressDialog.dismiss();
    }


    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (smartLockView != null) smartLockView.handleActivityResult(requestCode, resultCode, data);
        if (googleLoginView != null) googleLoginView.handleActivityResult(requestCode, resultCode, data);
        if (facebookLoginView != null) facebookLoginView.handleActivityResult(requestCode, resultCode, data);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(emailEditText.getWindowToken(), 0);
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        hideKeyboard();
        inputDialog = null;
    }

    public void onDestroy() {
        if (presenter != null) presenter.detatchView();
        if (smartLockView != null) smartLockView.disconnect();
        smartLockView = null;
        inputDialog = null;
        progressDialog = null;
        googleLoginView = null;
        facebookLoginView = null;
    }
}
