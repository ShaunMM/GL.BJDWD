package com.bjdwd;

import android.app.Activity;
import android.app.Application;

import com.bjdwd.tools.CrashHandler;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.download.DownloadQueue;
import com.yanzhenjie.nohttp.rest.RequestQueue;
//import org.xutils.x;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by BYJ on 2017/3/23.
 */
public class BJDWDApplication extends Application {
    public static ExecutorService upLoadPool = Executors.newCachedThreadPool();
    public static RequestQueue queue = null;//请求队列
    public static DownloadQueue downloadQueue;//下载队列

    public static ExecutorService getExecutorService() {
        return upLoadPool;
    }

    @Override
    public void onCreate() {
        //初始化NoHttp
        NoHttp.initialize(this);
        //初始化请求队列
        queue = NoHttp.newRequestQueue();
        //初始化下载队列
        downloadQueue = NoHttp.newDownloadQueue();

        super.onCreate();
//        x.Ext.init(this);
//        x.Ext.setDebug(false);//可以用它控制输出cuowulogfalse代表不输出

        //全局异常捕获初始化
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    private static List<Activity> activityList = new LinkedList();

    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    public static void removeActivity(Activity a) {
        activityList.remove(a);
    }

    public static void exit() {
        for (Activity activity : activityList) {
            if (activity != null) {
                activity.finish();
            }
        }
        System.exit(0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
