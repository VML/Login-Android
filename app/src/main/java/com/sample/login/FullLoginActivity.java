package com.sample.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.vml.login.FacebookLoginView;
import com.vml.login.GoogleLoginView;
import com.vml.login.LoginLayout;

/**
 * A login screen
 */
public class FullLoginActivity extends AppCompatActivity {

    LoginLayout loginLayout;
    FakeLoginListener formListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_login);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        loginLayout = (LoginLayout) findViewById(R.id.login_view);
        formListener = new FakeLoginListener(this, loginLayout);
        loginLayout.setFormListener(formListener);
        loginLayout.setGoogleLoginView(new GoogleLoginView(this, "123"));
        loginLayout.setFacebookLoginView(new FacebookLoginView(this, "123"));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        formListener.handleNewIntent(intent);
    }

    @Override
    protected void onDestroy() {
        loginLayout.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: onBackPressed(); return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loginLayout.handleActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}

