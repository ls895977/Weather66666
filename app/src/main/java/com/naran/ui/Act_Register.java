package com.naran.ui;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

/**
 * 注册
 */
public class Act_Register extends BaseAct {
    private ACache aCache;
    private EditText etUserName;
    private EditText etPassWord;
    private RelativeLayout progressLayout;
    private LinearLayout checkBoxLayout;
    private String selectedItem = "";

    @Override
    public int initLayoutId() {
        return R.layout.act_register;
    }

    @Override
    public void initView() {
        hideHeader();
        setOnClickListener(R.id.back);
        setOnClickListener(R.id.regist);
    }

    @Override
    public void initData() {
        aCache = ACache.get(this);
        checkBoxLayout = (LinearLayout) findViewById(R.id.checkBoxLayout);
        etUserName = (EditText) findViewById(R.id.phoneNumber);
        etPassWord = (EditText) findViewById(R.id.passWord);
        progressLayout = (RelativeLayout) findViewById(R.id.progressLayout);
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
            case R.id.back:
                finish();
                break;
            case R.id.regist://注册
                register();
                break;
        }
    }

    private String ph, pwd;

    public void register() {
        ph = etUserName.getText().toString().trim();
        if (ph.equals("")) {
            Toast.makeText(context, "请输入手机号!", Toast.LENGTH_SHORT).show();
            return;
        }
        pwd = etPassWord.getText().toString().trim();
        if (pwd.equals("")) {
            Toast.makeText(context, "请输密码!", Toast.LENGTH_SHORT).show();
            return;
        }
        progressLayout.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("userName", etUserName.getText().toString().trim());
        map.put("passWord", etPassWord.getText().toString().trim());
        for (int i = 0; i < checkBoxLayout.getChildCount(); i++) {
            LinearLayout linearLayout = (LinearLayout) checkBoxLayout.getChildAt(i);
            for (int j = 0; j < linearLayout.getChildCount(); j++) {
                CheckBox checkBox = (CheckBox) linearLayout.getChildAt(j);
                if (checkBox.isChecked()) {
                    int temp = i * 3 + j + 1;
                    if (selectedItem.equals("")) {
                        selectedItem = temp + "";
                    } else {
                        selectedItem = selectedItem + "," + temp;
                    }
                }
            }
        }
        map.put("InterestIds", selectedItem);
        OkHttpUtil.postAsync(OkHttpUtil.register, map, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Toast.makeText(context, "连接失败!", Toast.LENGTH_SHORT).show();
                progressLayout.setVisibility(View.GONE);
            }

            @Override
            public void requestSuccess(String result) throws Exception {

                progressLayout.setVisibility(View.GONE);
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.optString("result").equals("")) {
                    Toast.makeText(context, "注册成功！", Toast.LENGTH_SHORT).show();
                    aCache.put("UserName", ph);
                    aCache.put("PassWord", pwd);
                    setResult(10);
                    finish();
                } else {
                    Toast.makeText(context, jsonObject.optString("result"), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
