package com.bjdwd.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.bjdwd.config.ISystemConfig;
import com.bjdwd.config.SystemConfigFactory;

/**
 * Created by dell on 2017/3/23.
 */
public class GuideActivity extends Activity {

    private ISystemConfig systemConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        systemConfig = SystemConfigFactory.getInstance(this).getSystemConfig();
        if (systemConfig.isFirstUse()) {
            openActivity(LoginActivity.class);
        } else {
            openActivity(MainActivity.class);
        }
    }

    private void openActivity(Class<?> cls) {
        openActivity(cls, null);
    }

    private void openActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        finish();
        systemConfig.setFirstUse(true);
    }
}
