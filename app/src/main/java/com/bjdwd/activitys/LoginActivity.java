package com.bjdwd.activitys;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bjdwd.BJDWDApplication;
import com.bjdwd.R;
import com.bjdwd.beans.LoginBackBean;
import com.bjdwd.config.ISystemConfig;
import com.bjdwd.config.SystemConfig;
import com.bjdwd.config.SystemConfigFactory;
import com.bjdwd.dbutils.HandleDBHelper;
import com.bjdwd.tools.DialogTool;
import com.bjdwd.tools.HttpTool;
import com.bjdwd.tools.JSONConvertTool;
import com.bjdwd.tools.ShowToastTool;
import com.bjdwd.tools.WiFiTool;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by dell on 2017/3/23.
 */
public class LoginActivity extends Activity implements View.OnClickListener {

    private EditText et_account;
    private EditText et_password;
    private Button bt_login;
    private String account;
    private String password;
    private HandleDBHelper handleDBHelper;
    private ISystemConfig systemConfig;
    private int loginCode = -1;

    private Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int n = msg.arg1;
            msg.getData();
            if (n == 1) {
                try {
                    DialogTool.cancelprogressDialog();
                    systemConfig.setFirstUse(true);
                    Intent intent = new Intent();
                    intent.setClass(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } catch (UnsupportedOperationException u) {
                    u.printStackTrace();
                }
            } else if (n == 0) {
                ShowToastTool.showToast(LoginActivity.this, "账号密码有误，请重新输入");
                et_account.setText("");
                et_password.setText("");
            } else if (n == 2) {
                DialogTool.cancelprogressDialog();
                ShowToastTool.showToast(LoginActivity.this, "数据写入数据库有误");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        systemConfig = SystemConfigFactory.getInstance(LoginActivity.this).getSystemConfig();
        if (systemConfig.isFirstUse()) {
            initView();
        } else {
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void initView() {
        et_account = (EditText) findViewById(R.id.et_account);
        et_password = (EditText) findViewById(R.id.et_password);
        et_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        bt_login = (Button) findViewById(R.id.bt_login);
        bt_login.setOnClickListener(this);
        handleDBHelper = HandleDBHelper.getInstance(getApplicationContext());
    }


    @Override
    public void onClick(View v) {
        clickLogin();
    }

    public void clickLogin() {
        account = et_account.getText().toString();
        password = et_password.getText().toString();

        if (WiFiTool.isWifi(LoginActivity.this)) {
            if (account.equals("") || password.equals("")) {
                ShowToastTool.showToast(LoginActivity.this, "请填写账号和密码");
            } else {
                if (account.equals("1234") && password.equals("1234")) {
                    upDataServerUrl();
                } else {
                    //登录提示
                    DialogTool.setprogressDialog(LoginActivity.this, "登录中");
                    BJDWDApplication.getExecutorService().execute(new Runnable() {
                        @Override
                        public void run() {
                            //向服务器发送账号和密码 请求登录   参数  account  password
                            Map<String, String> loginRequest = new HashMap();
                            loginRequest.put("password", password);
                            loginRequest.put("account", account);
                            String httpUrl = systemConfig.getHost() + systemConfig.getWebPort() + "/api" + "/login";
                            String loginReceipt = HttpTool.submitPostRequest(httpUrl, loginRequest, "utf-8");
                            try {
                                JSONObject jsonObject = new JSONObject(loginReceipt);
                                if (jsonObject.get("code").equals("403")) {
                                    loginCode = 0;
                                } else {
                                    boolean wrisok = false;
                                    wrisok = wrLoginInfoTb(loginReceipt);
                                    if (wrisok) {
                                        loginCode = 1;
                                    } else {
                                        loginCode = 2;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Message loginMessage = new Message();
                            loginMessage.arg1 = loginCode;
                            loginHandler.sendMessage(loginMessage);
                        }
                    });

                }
            }

        } else {
            ShowToastTool.showToast(LoginActivity.this, "无线网络未连接...");
        }

    }

    private void upDataServerUrl() {
        ShowToastTool.showToast(LoginActivity.this, "修改服务器地址");
        final EditText ed = new EditText(LoginActivity.this);
        new AlertDialog.Builder(LoginActivity.this).setTitle("请输入新的服务器地址")
                .setView(ed)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ShowToastTool.showToast(LoginActivity.this, "确定");
                        String serverAddress = ed.getText() + "";
                        if (!serverAddress.equals("") && !serverAddress.isEmpty()) {
                            //新的服务器地址写到配置文件里
                            systemConfig.setHost(serverAddress);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ShowToastTool.showToast(LoginActivity.this, "取消");
                    }
                })
                .show();

    }

    private boolean wrLoginInfoTb(String loginReceipt) {
        Gson gson = new Gson();
        LoginBackBean loginBackBean = new LoginBackBean();
        loginBackBean = gson.fromJson(loginReceipt, LoginBackBean.class);

        systemConfig.setToken(loginBackBean.getData().getToken());

        LoginBackBean.DataBean.UserBean userBean = new LoginBackBean.DataBean.UserBean();
        systemConfig.setUserAccount(userBean.getWorkNo());
        systemConfig.setUserName(userBean.getUsername());
        systemConfig.setUserId(userBean.getId());
        systemConfig.setUserType(userBean.getUserType());
        systemConfig.setParentDepartmentId(userBean.getParentDepartmentId());
        boolean isok = false;

        //写如数据库
        isok = handleDBHelper.insert("UserInfo", new String[]{"UserId", "Name", "Gender", "HeadPortraitPath",
                        "DepartmentId", "AddTime", "DepartmentName", "ParentDepartmentId", "WorkNo", "Username", "UserType", "Password"},
                new Object[]{userBean.getId(), userBean.getName(),
                        userBean.getGender(), userBean.getHeadPortraitPath(), userBean.getDepartmentId(), userBean.getAddTime(),
                        userBean.getDepartmentName(), userBean.getParentDepartmentId(),
                        userBean.getWorkNo(), userBean.getUsername(), userBean.getUserType(), userBean.getPassword()});
        return isok;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(false);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
