package com.bjdwd.config;

/**
 * Created by dell on 2017/3/23.
 */

public interface ISystemConfig {

    public void setUserName(String username);

    public String getUserName();

    public void setUserAccount(String useraccount);

    public String getUserAccount();

    public void setPassword(String password);

    public String getPassword();

    public int getUserType();

    public void setUserType(int usertype);

    public int getUserId();

    public void setUserId(int userid);

    public int getDepartmentId();

    public void setDepartmentId(int departmentid);

    public int getParentDepartmentId();

    public void setParentDepartmentId(int parentpepartmentid);

    public boolean isFirstUse();

    public void setFirstUse(boolean isFirstUse);

    public boolean isFirstLogin();

    public void setFirstLogin(boolean isFirstLogin);

    public boolean isFirstSync();

    public void setFirstSync(boolean isFirstSync);

    public String getHost();

    public void setHost(String host);

    public int getWebPort();

    public void setWebPort(int port);

    public String getToken();

    public void setToken(String token);

}
