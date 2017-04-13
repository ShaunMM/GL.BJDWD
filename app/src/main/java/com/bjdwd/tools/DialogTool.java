package com.bjdwd.tools;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by dell on 2017/3/23.
 */
public class DialogTool {
    private static ProgressDialog progressDialog;

    public static void setprogressDialog(Context context, String str) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(str + "....");
        progressDialog.setIndeterminate(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    public static void cancelprogressDialog() {
        progressDialog.dismiss();
    }
}
