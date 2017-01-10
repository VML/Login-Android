package com.vml.login;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import static com.google.android.gms.internal.zzs.TAG;

/**
 * Created by tway on 3/23/16.
 */
public class SmartLockView {
    GoogleApiClient googleApiClient;
    AppCompatActivity activity;
    static int RC_GET = 1;
    static int RC_PUT = 2;
    static int RC_CONNECT = 3;
    GetListener getListener;
    PutListener putListener;
    boolean shouldUseHintPicker = false;

    public SmartLockView(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void disconnect() {
        if (googleApiClient != null) googleApiClient.disconnect();
    }

    public interface GetListener {
        void onGetResult(Credential credential, boolean didUserChoose);
    }

    private boolean didUserChoose;

    public void get(GetListener listener) {
        this.getListener = listener;
        getApiClient(new ApiClientListener() {
            @Override
            public void onConnectSuccess(final GoogleApiClient apiClient) {
                CredentialRequest credentialsRequest = new CredentialRequest.Builder()
                        .setPasswordLoginSupported(true)
                        .setAccountTypes(IdentityProviders.GOOGLE, IdentityProviders.FACEBOOK)
                        .build();

                didUserChoose = false;
                Auth.CredentialsApi
                        .request(apiClient, credentialsRequest)
                        .setResultCallback(new ResultCallback<CredentialRequestResult>() {
                            @Override
                            public void onResult(CredentialRequestResult result) {
                                Status status = result.getStatus();
                                if (status.getStatusCode() == CommonStatusCodes.SIGN_IN_REQUIRED) {
                                    if (shouldUseHintPicker) showHintPicker(apiClient);
                                    else handleCredentialResult(result);
                                } else if (status.hasResolution()) {
                                    startResolutionFromStatus(status, RC_GET);
                                } else {
                                    handleCredentialResult(result);
                                }
                            }
                        });
            }

            @Override
            public void onConnectError(Throwable e) {
                getListener.onGetResult(null, didUserChoose);
            }
        });
    }

    private void handleCredentialResult(CredentialRequestResult result) {
        Credential credential = result.getStatus().isSuccess()
                ? result.getCredential()
                : null;
        if (getListener != null) getListener.onGetResult(credential, didUserChoose);
    }

    private void showHintPicker(GoogleApiClient apiClient) {
        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder()
                        .setShowCancelButton(true)
                        .build())
                .setEmailAddressIdentifierSupported(true)
                .setAccountTypes(IdentityProviders.GOOGLE)
                .build();

        PendingIntent intent =
                Auth.CredentialsApi.getHintPickerIntent(apiClient, hintRequest);
        try {
            activity.startIntentSenderForResult(intent.getIntentSender(), RC_GET, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Could not start hint picker Intent", e);
        }
    }

    private void startResolutionFromStatus(Status status, int requestCode) {
        try {
            status.startResolutionForResult(activity, requestCode);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    public interface PutListener {
        void onPutResult();
    }

    public void put(final String username, final String password, final String name, PutListener listener) {
        putListener = listener;
        getApiClient(new ApiClientListener() {
            @Override
            public void onConnectSuccess(GoogleApiClient apiClient) {
                if (username != null && !username.isEmpty() && password != null && !password.isEmpty()) {
                    Credential.Builder builder = new Credential.Builder(username)
                            .setPassword(password);
                    if (name != null) builder.setName(name);
                    Credential credential = builder.build();

                    Auth.CredentialsApi
                            .save(apiClient, credential)
                            .setResultCallback(new ResultCallback<Status>() {
                                @Override
                                public void onResult(Status status) {
                                    if (status.hasResolution()) startResolutionFromStatus(status, RC_PUT);
                                    else if (putListener != null) putListener.onPutResult();
                                }
                            });
                } else {
                    if (putListener != null) putListener.onPutResult();
                }
            }

            @Override
            public void onConnectError(Throwable e) {
                if (putListener != null) putListener.onPutResult();
            }
        });
    }

    public void delete(final String username) {
        getApiClient(new ApiClientListener() {
            @Override
            public void onConnectSuccess(GoogleApiClient apiClient) {
                Credential credential = new Credential.Builder(username)
                        .build();

                Auth.CredentialsApi
                        .delete(apiClient, credential)
                        .setResultCallback(null);
            }

            @Override
            public void onConnectError(Throwable e) {

            }
        });
    }

    private interface ApiClientListener {
        void onConnectSuccess(GoogleApiClient apiClient);
        void onConnectError(Throwable e);
    }

    private void getApiClient(final ApiClientListener apiClientListener) {
        if (googleApiClient != null && googleApiClient.isConnected()) apiClientListener.onConnectSuccess(googleApiClient);
        else if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            apiClientListener.onConnectSuccess(googleApiClient);
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                        }
                    })
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {
                            if (connectionResult.hasResolution()) {
                                try {
                                    connectionResult.startResolutionForResult(activity, RC_CONNECT);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                apiClientListener.onConnectError(new Exception("connect failed"));
                            }
                        }
                    })
                    .addApi(Auth.CREDENTIALS_API)
                    .build();
            googleApiClient.connect();
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == RC_GET) {
                if (resultCode == Activity.RESULT_OK) {
                    Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                    getListener.onGetResult(credential, didUserChoose);
                } else {
                    getListener.onGetResult(null, didUserChoose);
                }

            } else if (requestCode == RC_PUT) {
                putListener.onPutResult();
            }
        } catch (NullPointerException n) {
            //fail silently if listeners are null
        }
    }
}
