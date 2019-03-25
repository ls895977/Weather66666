package com.naran.ui.fgt.min.act;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lykj.aextreme.afinal.utils.ACache;
import com.naran.ui.Act_Main;
import com.naran.ui.base.BaseAct;
import com.naran.ui.base.MessageEvent;
import com.naran.ui.common.ComantUtils;
import com.naran.weather.R;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

public class Act_LanguageSettings extends BaseAct {
    private LinearLayout butoonLinear;
    private Locale curLocale;
    String status = "";
    ACache aCache;

    @Override
    public int initLayoutId() {
        return R.layout.act_languagesettings;
    }

    @Override
    public void initView() {
        hideHeader();
        butoonLinear = getView(R.id.languageLyout);
        getViewAndClick(R.id.btnMn);
        getViewAndClick(R.id.btnCn);
        tvVersion = (TextView) findViewById(R.id.versionCode);
        languageLyout = (LinearLayout) findViewById(R.id.languageLyout);
    }

    private LinearLayout languageLyout;
    private TextView tvVersion;

    @Override
    public void initData() {
        aCache = ACache.get(context);
        if (getIntent().getStringExtra("status") != null) {
            status = getIntent().getStringExtra("status");
        }
        getPremiss();
        tvVersion.setText("Version:"+getVerName(this));
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
            case R.id.btnMn://蒙
                aCache.put("language", "mu");
                ComantUtils.MyLanguage = true;
                if (status.equals("Welcome")) {//欢迎页过来
                    startActivity(new Intent().setClass(Act_LanguageSettings.this, Act_Main.class));
                } else {//设置界面
                    EventBus.getDefault().post(new MessageEvent("haha", 1));//更新ui
                }
                finish();
                break;
            case R.id.btnCn://中文
                aCache.put("language", "cn");
                ComantUtils.MyLanguage = false;
                if (status.equals("Welcome")) {//欢迎页过来
                    startActivity(new Intent().setClass(Act_LanguageSettings.this, Act_Main.class));
                } else {//设置界面
                    EventBus.getDefault().post(new MessageEvent("haha", 1));//更新ui
                }
                finish();
                break;
        }
    }
    private void getPremiss() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_CONTACT = 101;
            String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
            //验证是否许可权限
            for (String str : permissions) {
                if (Act_LanguageSettings.this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    //申请权限
                    ActivityCompat.requestPermissions(Act_LanguageSettings.this, permissions, REQUEST_CODE_CONTACT);
                    return;
                }
            }
        }
    }
    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
}
