package com.vml.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by tway on 12/5/16.
 */

public class DialogUtil {
    public static ProgressDialog showProgress(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context, R.style.lv_dialog_theme);
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        return progressDialog;
    }

    public static AlertDialog showAlert(String message, Context context) {
        return new AlertDialog.Builder(context, R.style.lv_dialog_theme)
                .setMessage(message == null ? "An unknown error occurred" : message)
                .setNegativeButton(android.R.string.ok, null)
                .show();
    }

    public static AlertDialog showInput(int titleResId, int viewResId, Context context, DialogInterface.OnDismissListener onDismissListener) {
        return new AlertDialog.Builder(context, R.style.lv_dialog_theme)
                .setPositiveButton(R.string.submit, null)
                .setNegativeButton(R.string.cancel, null)
                .setOnDismissListener(onDismissListener)
                .setTitle(titleResId)
                .setView(viewResId)
                .show();
    }
}
