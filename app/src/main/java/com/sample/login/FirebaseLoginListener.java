package com.sample.login;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.vml.login.FormListener;
import com.vml.login.LoginData;
import com.vml.login.LoginDataStore;
import com.vml.login.LoginLayout;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by tway on 11/29/16.
 */
class FirebaseLoginListener implements FormListener {
    private Activity activity;
    private LoginLayout loginLayout;

    public FirebaseLoginListener(Activity activity, LoginLayout loginLayout) {
        this.activity = activity;
        this.loginLayout = loginLayout;
        FirebaseApp.initializeApp(activity);
    }

    private String getRandomPassword() {
        return new BigInteger(130, new SecureRandom()).toString(32);
    }

    @Override
    public void onLoginFormSubmitted(final String username, String password) {
        if (password == null || password.isEmpty()) {
            sendAutoPasswordResetEmail(username);
        } else {
            FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            handleAuthResult(task);
                        }
                    });
        }
    }

    private void sendAutoPasswordResetEmail(final String username) {
        FirebaseAuth.getInstance().sendPasswordResetEmail(username).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loginLayout.hideDialog();
                    loginLayout.showAlert("We sent you a link to log in.  Please check your inbox.");
                } else {
                    if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        FirebaseAuth.getInstance()
                                .createUserWithEmailAndPassword(username, getRandomPassword())
                                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        sendAutoPasswordResetEmail(username);
                                    }
                                });
                    } else if (task.getException() instanceof FirebaseException) {
                        loginLayout.showAlert("Invalid email");
                    } else {
                        loginLayout.showAlert(task.getException().getMessage());
                    }

                }
            }
        });
    }

    @Override
    public void onCreateAccountFormSubmitted(String username, String password) {
        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        handleAuthResult(task);
                    }
                });
    }

    @Override
    public void onLoginCompleted(LoginData loginData) {
        AuthCredential credential = null;
        switch (loginData.getMethod()) {
            case GOOGLE:
                credential = GoogleAuthProvider.getCredential(loginData.getAccessToken(), null);
                break;
            case FACEBOOK:
                credential = FacebookAuthProvider.getCredential(loginData.getAccessToken());
                break;
        }
        if (credential != null) FirebaseAuth.getInstance().signInWithCredential(credential);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            if (loginData.getDisplayName() == null || loginData.getPhotoUrl() == null) {
                loginData = new LoginData(
                        loginData.getMethod(),
                        loginData.getAccessToken(),
                        loginData.getId(),
                        loginData.getEmail(),
                        user.getDisplayName(),
                        user.getPhotoUrl() == null ? null : user.getPhotoUrl().toString()
                );
            } else {
                UserProfileChangeRequest.Builder profileBuilder = new UserProfileChangeRequest.Builder();
                if (loginData.getDisplayName() != null)
                    profileBuilder = profileBuilder.setDisplayName(loginData.getDisplayName());
                if (loginData.getPhotoUrl() != null)
                    profileBuilder = profileBuilder.setPhotoUri(Uri.parse(loginData.getPhotoUrl()));
                user.updateProfile(profileBuilder.build());
            }
        }

        LoginDataStore.of(activity).put(loginData);
        activity.finish();
    }

    @Override
    public void onForgotPasswordFormSubmitted(String username) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(username).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                handlePasswordResetResult(task);
            }
        });
    }

    @Override
    public boolean onViewCreateAccountButtonClicked() {
        return false;
    }

    @Override
    public boolean onViewForgotPasswordButtonClicked() {
        return false;
    }

    private void handlePasswordResetResult(Task<Void> task) {
        if (task.isSuccessful()) {
            loginLayout.hideDialog();
            loginLayout.showAlert("We sent you a link to log in.  Please check your inbox.");
        } else {
            String message = task.getException().getMessage();
            loginLayout.showAlert(message);
        }
    }

    private void handleAuthResult(Task<AuthResult> task) {
        if (task.isSuccessful()) {
            FirebaseUser user = task.getResult().getUser();
            loginLayout.loginSucceeded(mapToLoginData(user));
        } else {
            String message = task.getException().getMessage();
            loginLayout.showAlert(message);
        }
    }

    private LoginData mapToLoginData(FirebaseUser user) {
        LoginData data = new LoginData(
                LoginData.Method.EMAIL,
                user.getToken(false).getResult().getToken(),
                user.getUid(),
                user.getEmail(),
                user.getDisplayName(),
                user.getPhotoUrl() == null ? null : user.getPhotoUrl().toString()
        );
        return data;
    }

    public void handleNewIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            final String code = intent.getData().getQueryParameter("oobCode");
            if (code != null) resetPasswordAndSignIn(code);
        }
    }

    public void resetPasswordAndSignIn(final String code) {
        loginLayout.showProgress();
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.verifyPasswordResetCode(code).addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    final String email = task.getResult();
                    final String password = "abc123"; //TODO use random number
                    auth.confirmPasswordReset(code, password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    FirebaseUser user = task.getResult().getUser();
                                    loginLayout.loginSucceeded(mapToLoginData(user));
                                }
                            });
                        }
                    });
                } else {
                    String message = task.getException().getMessage();
                    loginLayout.showAlert(message);
                }
            }
        });
    }
}
