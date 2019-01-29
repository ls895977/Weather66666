package com.naran.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lykj.aextreme.afinal.utils.ACache;
import com.lykj.aextreme.afinal.utils.Debug;
import com.naran.ui.base.BaseAct;
import com.naran.ui.bean.LoginBean;
import com.naran.ui.connector.OkHttpUtil;
import com.naran.ui.fgt.min.act.Act_SetUp;
import com.naran.ui.modle.UserInfoModel;
import com.naran.ui.utils.LoginUtil;
import com.naran.weather.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

import static com.naran.ui.utils.LoginUtil.userInfoModel;


public class Act_Login extends BaseAct {

    private EditText phone, pasword;
    private String status = "";

    @Override
    public int initLayoutId() {
        return R.layout.act_login;
    }
    @Override
    public void initView() {
        hideHeader();
        getViewAndClick(R.id.login_back);
        getViewAndClick(R.id.login);
        getViewAndClick(R.id.regist);
        phone = getView(R.id.phoneNumber);
        pasword = getView(R.id.passWord);
    }

    @Override
    public void initData() {
        aCache = ACache.get(this);
        if (getIntent().getStringExtra("status") != null) {
            status = getIntent().getStringExtra("status");
        }
    }

    @Override
    public void requestData() {

    }

    @Override
    public void updateUI() {

    }

    @Override
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.login_back:
                finish();
                break;
            case R.id.login://登录
                postLogin();
                break;
            case R.id.regist://注册
                startActivityForResult(Act_Register.class,10);
                break;
        }
    }

    private ACache aCache;

    public void postLogin() {
        String ph = phone.getText().toString().trim();
        if (ph.equals("")) {
            Toast.makeText(context, "请输入账号!", Toast.LENGTH_SHORT).show();
            return;
        }
        String pwd = pasword.getText().toString().trim();
        if (pwd.equals("")) {
            Toast.makeText(context, "请输密码!", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("userName", ph);
        map.put("passWord", pwd);
        OkHttpUtil.postAsync(OkHttpUtil.Login, map, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Toast.makeText(context, "连接失败!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                aCache.put("login", result);
                Gson gson = new Gson();
                LoginBean bean = gson.fromJson(result, LoginBean.class);
                UserInfoModel userInfoModel = new UserInfoModel();
                userInfoModel.setRoleID(bean.getData().get(0).getRoleID());
                userInfoModel.setUserName(bean.getData().get(0).getUserName());
                userInfoModel.setID(bean.getData().get(0).getID());
                userInfoModel.setFireWarning(bean.getData().get(0).getFireWarning() + "");
                userInfoModel.setParentID(bean.getData().get(0).getParentID());
                userInfoModel.setFullName(bean.getData().get(0).getFullName());
                userInfoModel.setCHineseAreaName(bean.getData().get(0).getCHineseAreaName());
                userInfoModel.setHeadImg(bean.getData().get(0).getHeadImg());
                userInfoModel.setAreaNO(bean.getData().get(0).getAreaNO());
                userInfoModel.setContact(bean.getData().get(0).getContact());
                userInfoModel.setEdition(bean.getData().get(0).getEdition());
                userInfoModel.setParentUserName(bean.getData().get(0).getUserName());
                userInfoModel.setState(bean.getData().get(0).getState());
                LoginUtil.getInstance().isLogined = true;
                LoginUtil.setUserInfoModel(userInfoModel);
                aCache.put("UserName", phone.getText().toString().trim());
                aCache.put("PassWord", pasword.getText().toString().trim());
                if (status.equals("set")) {
                    startAct(Act_SetUp.class);
                    finish();
                } else if (status.equals("Notify")) {
                    finish();
                } else if (status.equals("home")) {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Debug.e("---------requestCode-"+requestCode+"-----------resultCode-"+resultCode);
        if(requestCode==10&&resultCode==10){
            phone.setText(aCache.getAsString("UserName"));
            pasword.setText(aCache.getAsString("PassWord"));
        }
    }
}
