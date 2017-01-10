package com.sample.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.picasso.Picasso;
import com.vml.login.LoginData;
import com.vml.login.LoginDataStore;

public class MainActivity extends AppCompatActivity {


    private TextView displayNameTextView;
    private ImageView imageView;
    private Button fullLoginButton;
    private Button basicLoginButton;
    private Button logoutButton;
    private Button passwordlessLoginButton;
    private RelativeLayout loggedInLayout;
    private LinearLayout loggedOutLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (LeakCanary.isInAnalyzerProcess(getApplication())) {
            return;
        }
        LeakCanary.install(getApplication());
        setContentView(R.layout.activity_main);

        fullLoginButton = (Button) findViewById(R.id.full_login_button);
        fullLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, FullLoginActivity.class));
            }
        });

        passwordlessLoginButton = (Button) findViewById(R.id.passwordless_login_button);
        passwordlessLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PasswordlessLoginActivity.class));
            }
        });

        basicLoginButton = (Button) findViewById(R.id.basic_login_button);
        basicLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, BasicLoginActivity.class));
            }
        });

        logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginDataStore.of(getApplicationContext()).delete();
                initViewState();
            }
        });

        loggedInLayout = (RelativeLayout) findViewById(R.id.logged_in_layout);
        loggedOutLayout = (LinearLayout) findViewById(R.id.logged_out_layout);

        imageView = (ImageView) findViewById(R.id.avatar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViewState();
    }

    private void initViewState() {
        if (LoginDataStore.of(this).exists()) {
            loggedInLayout.setVisibility(View.VISIBLE);
            loggedOutLayout.setVisibility(View.GONE);
            LoginData data = LoginDataStore.of(this).get();
            displayNameTextView = (TextView) findViewById(R.id.display_name);
            String displayName = data.getDisplayName() == null ? "" : data.getDisplayName() + "\n";
            displayName += data.getEmail();
            displayNameTextView.setText(displayName);
            Picasso.with(this).load(data.getPhotoUrl()).into(imageView);
        } else {
            loggedInLayout.setVisibility(View.GONE);
            loggedOutLayout.setVisibility(View.VISIBLE);
        }
    }
}
