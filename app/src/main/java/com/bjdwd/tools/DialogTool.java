package com.bjdwd.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.bjdwd.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by dell on 2017/3/23.
 */
public class DialogTool {
    private static ProgressDialog progressDialog;

    public static void setprogressDialog(Context context, String str) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context,ProgressDialog.THEME_HOLO_DARK);
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(str + "....");
            progressDialog.setIndeterminate(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setCancelable(false);
        }

        try {
            if (progressDialog != null) {
                progressDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("------>", e + "");
        }
    }

    public static void cancelprogressDialog() {
        try {
            if (progressDialog != null) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("------>sdfsdfsfsf", e + "");
        }

    }
}
