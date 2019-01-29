package com.naran.ui.utils;


import com.naran.ui.modle.UserInfoModel;

/**
 * Created by darhandarhad on 2017/5/7.
 */

public class LoginUtil {

    public boolean isLogined = false;
    public static UserInfoModel userInfoModel;
    private LoginUtil() {
    }

    private static LoginUtil instance;

    public static LoginUtil getInstance() {
        if (instance == null) {
            instance = new LoginUtil();
        }
        return instance;
    }
    public static void setUserInfoModel(UserInfoModel userInfoModel) {
        LoginUtil.userInfoModel = userInfoModel;
    }
}
