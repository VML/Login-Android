package com.vml.login;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by tway on 11/28/16.
 */

public class FormUtil {

    public static void initFormListeners(final EditText emailEditText, final String email, final EditText passwordEditText, final Button submitButton, final Window window, final SubmitListener listener) {

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setSubmitButtonState(emailEditText, passwordEditText, submitButton);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        TextView.OnEditorActionListener actionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (submitButton.isEnabled()) submitButton.callOnClick();
                    return true;
                }
                return false;
            }
        };

        emailEditText.addTextChangedListener(textWatcher);
        emailEditText.setOnEditorActionListener(actionListener);
        if (passwordEditText != null) {
            passwordEditText.addTextChangedListener(textWatcher);
            passwordEditText.setOnEditorActionListener(actionListener);
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText == null ? null : passwordEditText.getText().toString();
                listener.onFormSubmit(email, password);
            }
        });

        if (window != null) {
            emailEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    }
                }
            });
        }

        emailEditText.setText(email);
        emailEditText.selectAll();
        setSubmitButtonState(emailEditText, passwordEditText, submitButton);
    }

    public interface SubmitListener {
        void onFormSubmit(String email, String password);
    }

    public static void setSubmitButtonState(EditText emailEditText, EditText passwordEditText, final Button submitButton) {
        boolean isEnabled = passwordEditText == null ? emailEditText.getText().length() > 0
                : emailEditText.getText().length() > 0 && passwordEditText.getText().length() > 0;
        submitButton.setEnabled(isEnabled);
    }
}
