package com.naran.ui.fgt;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.naran.ui.base.BaseFgt;
import com.naran.ui.base.MessageEvent;
import com.naran.ui.common.ComantUtils;
import com.naran.ui.fgt.home.adapter.ViewPagerFragmentAdapter;
import com.naran.ui.fgt.home.fgt.FgtCnHome;
import com.naran.ui.fgt.home.fgt.FgtMuHome;
import com.naran.ui.view.NoScrollViewPager;
import com.naran.weather.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class Fgt_Home extends BaseFgt implements View.OnClickListener {
    private NoScrollViewPager myViewPager;
    private List<BaseFgt> HomeFgtData = new ArrayList<>();
    private ViewPagerFragmentAdapter viewPagerFragmentAdapter;

    @Override
    public int initLayoutId() {
        return R.layout.fgt_home;
    }

    @Override
    public void initView() {
        hideHeader();
        if (!EventBus.getDefault().isRegistered(this)) {
//            //首先要在你要接受EventBus的界面注册，这一步很重要
            EventBus.getDefault().register(this);
        }
        myViewPager = getView(R.id.myViewPager);
    }

    @Override
    public void initData() {
        HomeFgtData.add(new FgtCnHome());
        HomeFgtData.add(new FgtMuHome());
        viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(getFragmentManager(), HomeFgtData);
        myViewPager.setAdapter(viewPagerFragmentAdapter);
        updateUI();
    }

    @Override
    public void requestData() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.type == 1) {
            updateUI();
        }
    }

    @Override
    public void updateUI() {
        if (ComantUtils.MyLanguage) {//蒙.
            myViewPager.setCurrentItem(1);
        } else {//汉
            myViewPager.setCurrentItem(0);
        }
    }

    @Override
    public void onViewClick(View v) {

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
