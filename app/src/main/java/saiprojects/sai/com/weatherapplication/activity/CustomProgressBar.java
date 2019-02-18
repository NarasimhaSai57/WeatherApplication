package saiprojects.sai.com.weatherapplication.activity;

import android.app.ProgressDialog;
import android.content.Context;

public class CustomProgressBar {

    ProgressDialog progressDialog;

    public CustomProgressBar(Context ctx) {
        progressDialog = new ProgressDialog(ctx, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);

        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
    }

    public void showCustomDialog() {
        if (progressDialog != null && !progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void setCustomMessage(String msg) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setMessage(msg);
        }
    }

    public void closeCustomDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void setCustomCancelable(boolean value)
    {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setCancelable(value);
        }
    }

    public boolean isProgressBarShowing() {
        return ((progressDialog != null && progressDialog.isShowing())? true : false);
    }
}
