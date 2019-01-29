package com.naran.ui.fgt;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.naran.ui.Act_Login;
import com.naran.ui.base.BaseFgt;
import com.naran.ui.base.MessageEvent;
import com.naran.ui.common.ComantUtils;
import com.naran.ui.fgt.min.act.Act_SetUp;
import com.naran.ui.utils.LoginUtil;
import com.naran.weather.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class Fgt_Min extends BaseFgt implements View.OnClickListener {
    private LinearLayout myLiner;

    @Override
    public int initLayoutId() {
        return R.layout.fgt_min;
    }

    @Override
    public void initView() {
        hideHeader();
        myLiner = getView(R.id.my_min);
        getViewAndClick(R.id.imageView_menu_set);
    }

    @Override
    public void initData() {
        if (!EventBus.getDefault().isRegistered(this)) {
            //首先要在你要接受EventBus的界面注册，这一步很重要
            EventBus.getDefault().register(this);
        }
        updateUI();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.type == 1) {
            updateUI();
        }
    }


    @Override
    public void requestData() {

    }

    @Override
    public void updateUI() {
        myLiner.removeAllViews();
        if (ComantUtils.MyLanguage) {//蒙
            View view = LayoutInflater.from(context).inflate(R.layout.view_min_meng, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(params);
            getViewAndClick(view, R.id.meng_WeatherAssistant);
            getViewAndClick(view, R.id.meng_AboutWeather);
            getViewAndClick(view, R.id.meng_HistoricalWeather);
            getViewAndClick(view, R.id.meng_SocialConcern);
            getViewAndClick(view, R.id.meng_LivestockConcern);
            getViewAndClick(view, R.id.meng_Winwincooperation);
            myLiner.addView(view);
        } else {//汉
            View view = LayoutInflater.from(context).inflate(R.layout.view_min_cn, null);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            view.setLayoutParams(params);
            getViewAndClick(view, R.id.WeatherAssistant);
            getViewAndClick(view, R.id.AboutWeather);
            getViewAndClick(view, R.id.HistoricalWeather);
            getViewAndClick(view, R.id.SocialConcern);
            getViewAndClick(view, R.id.LivestockConcern);
            getViewAndClick(view, R.id.Winwincooperation);
            myLiner.addView(view);
        }
    }

    @Override
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_menu_set://设置按钮
                if (LoginUtil.getInstance().isLogined) {
                    startAct(Act_SetUp.class);
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("status", "set");
                    intent.setClass(context, Act_Login.class);
                    startActivity(intent);
                }
                break;
            //汉
            case R.id.WeatherAssistant://天气助手
                Log.e("aa", "------汉---天气助手");
                break;
            case R.id.AboutWeather://关于天气
                Log.e("aa", "------汉---关于天气");
                break;
            case R.id.HistoricalWeather://历史天气
                Log.e("aa", "------汉---历史天气");
                break;
            case R.id.SocialConcern://社会关注
                Log.e("aa", "------汉---社会关注");
                break;
            case R.id.LivestockConcern://畜牧关注
                Log.e("aa", "------汉---畜牧关注");
                break;
            case R.id.Winwincooperation://合作共赢
                Log.e("aa", "------汉---合作共赢");
                break;
            //蒙
            case R.id.meng_WeatherAssistant://天气助手
                Log.e("aa", "------蒙---天气助手");
                break;
            case R.id.meng_AboutWeather://关于天气
                Log.e("aa", "------蒙---关于天气");
                break;
            case R.id.meng_HistoricalWeather://历史天气
                Log.e("aa", "------蒙---历史天气");
                break;
            case R.id.meng_SocialConcern://社会关注
                Log.e("aa", "------蒙---社会关注");
                break;
            case R.id.meng_LivestockConcern://畜牧关注
                Log.e("aa", "------蒙---畜牧关注");
                break;
            case R.id.meng_Winwincooperation://合作共赢
                Log.e("aa", "------蒙---合作共赢");
                break;
        }
    }

    @Override
    public void sendMsg(int flag, Object obj) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在界面销毁的地方要解绑
        EventBus.getDefault().unregister(this);
    }
}
