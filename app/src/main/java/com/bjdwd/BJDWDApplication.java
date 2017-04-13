package com.bjdwd;

import android.app.Application;

import com.bjdwd.tools.CrashHandler;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.rest.RequestQueue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by dell on 2017/3/23.
 */

public class BJDWDApplication extends Application {
    public static ExecutorService upLoadPool = Executors.newCachedThreadPool();
    public static RequestQueue queue = null;

    public static ExecutorService getExecutorService() {
        return upLoadPool;
    }

    @Override
    public void onCreate() {
        //初始化NoHttp
        NoHttp.initialize(this);
        //初始化请求队列
        queue = NoHttp.newRequestQueue();
        super.onCreate();

        //全局异常捕获初始化
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }
}
