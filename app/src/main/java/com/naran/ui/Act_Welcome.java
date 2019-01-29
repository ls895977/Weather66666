package com.naran.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lykj.aextreme.afinal.utils.ACache;
import com.lykj.aextreme.afinal.utils.Debug;
import com.naran.ui.base.BaseAct;
import com.naran.ui.bean.LoginBean;
import com.naran.ui.common.ComantUtils;
import com.naran.ui.connector.OkHttpUtil;
import com.naran.ui.fgt.min.act.Act_LanguageSettings;
import com.naran.ui.fgt.min.act.Act_SetUp;
import com.naran.ui.modle.UserInfoModel;
import com.naran.ui.utils.LoginUtil;
import com.naran.weather.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

public class Act_Welcome extends BaseAct implements ViewPager.OnPageChangeListener {
    private ViewPager viewPager;
    private LinearLayout linearLayout;
    private List<View> views;
    private ImageView img3;

    @Override
    public int initLayoutId() {
        return R.layout.act_welcome;
    }

    @Override
    public void initView() {
        hideHeader();
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        linearLayout = (LinearLayout) findViewById(R.id.cursorLayout);
        views = new ArrayList<>();
        View view1 = getLayoutInflater().inflate(R.layout.view_welcome, null);
        ImageView img1 = (ImageView) view1.findViewById(R.id.cursorImg);
        img1.setImageDrawable(getResources().getDrawable(R.drawable.notic));
        View view2 = getLayoutInflater().inflate(R.layout.view_welcome, null);
        ImageView img2 = (ImageView) view2.findViewById(R.id.cursorImg);
        img2.setImageDrawable(getResources().getDrawable(R.drawable.noticone));
        View view3 = getLayoutInflater().inflate(R.layout.view_welcome, null);
        img3 = (ImageView) view3.findViewById(R.id.cursorImg);
        img3.setImageDrawable(getResources().getDrawable(R.drawable.notictow));
        views.add(view1);
        views.add(view2);
        views.add(view3);
    }

    private ACache aCache;
    private int versionCode = -1;
    private SharedPreferences shared;

    @Override
    public void initData() {
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo;
        shared = getSharedPreferences("firstTime", Context.MODE_PRIVATE);
        try {
            packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (versionCode != -1) {
            String isFirstTime = shared.getString("firstTime", "");
            SharedPreferences.Editor editor = shared.edit();
            editor.putString("firstTime", "" + versionCode);
            editor.commit();
        }
        isCanUpdate();
        aCache = ACache.get(context);
        if (aCache.getAsString("language") != null) {
            if (aCache.getAsString("language").equals("mu")) {
                ComantUtils.MyLanguage = true;
            } else if (aCache.getAsString("language").equals("cn")) {
                ComantUtils.MyLanguage = false;
            }
            postLogin();
            return;
        }
        for (int i = 0; i < views.size(); i++) {
            View view = new View(this);
            if (i == 0) {
                view.setBackgroundColor(Color.BLUE);
            } else {
                view.setBackgroundColor(Color.YELLOW);
            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(10, 10);
            layoutParams.setMargins(8, 0, 8, 0);
            view.setLayoutParams(layoutParams);
            linearLayout.addView(view);
        }
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void requestData() {

    }

    @Override
    public void updateUI() {

    }

    @Override
    public void onViewClick(View v) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < views.size(); i++) {
            if (i == position) {
                View view = linearLayout.getChildAt(i);
                view.setBackgroundColor(Color.BLUE);
            } else {
                View view = linearLayout.getChildAt(i);
                view.setBackgroundColor(Color.YELLOW);
            }
        }
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("status", "Welcome");
                intent.setClass(Act_Welcome.this, Act_LanguageSettings.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class MyPagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    /**
     * 登录
     */
    public void postLogin() {
        if (aCache.getAsString("UserName") == null) {//判断是否登录过
            startAct(Act_Main.class);
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("userName", aCache.getAsString("UserName"));
        map.put("passWord", aCache.getAsString("PassWord"));
        OkHttpUtil.postAsync(OkHttpUtil.Login, map, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                startAct(Act_Main.class);
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
                startAct(Act_Main.class);
                finish();
            }
        });
    }

    private void isCanUpdate() {
        PackageManager packageManager = this.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            OkHttpUtil.getAsync(OkHttpUtil.update, new OkHttpUtil.DataCallBack() {
                @Override
                public void requestFailure(Request request, IOException e) {

                }

                @Override
                public void requestSuccess(String result) throws Exception {
                    int onLineVersionCode = Integer.parseInt(new JSONObject(result).optString("EditionNo"));
                    if (versionCode != -1 && onLineVersionCode > versionCode) {

                        //postNotification(new JSONObject(result).optString("DownloadUrl"));
                        dialog(new JSONObject(result).optString("DownloadUrl"));
//                        UpdateManager updateManager = new UpdateManager(Main1Activity.this);
//                        updateManager.showNoticeDialog(new JSONObject(result).optString("DownloadUrl"));

                    }
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void dialog(final String downloadUrl) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Act_Welcome.this);
        builder.setMessage("检测到更新，是否更新？");
        builder.setTitle("提示");
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse(downloadUrl);
                intent.setData(content_url);
                startActivity(intent);
                dialog.dismiss();
//                Intent intent = new Intent(Main1Activity.this, UpdateService.class);
//                intent.putExtra("apkUrl", downloadUrl);
//                startService(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
