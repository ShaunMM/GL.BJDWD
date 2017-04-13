package com.bjdwd.tools;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by dell on 2017/3/23.
 */
public class ShowToastTool {
    public static void showToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }

}
