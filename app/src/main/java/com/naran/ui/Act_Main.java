package com.naran.ui;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.lykj.aextreme.afinal.utils.Debug;
import com.naran.ui.base.BaseAct;
import com.naran.ui.base.BaseFgt;
import com.naran.ui.base.MessageEvent;
import com.naran.ui.common.ComantUtils;
import com.naran.ui.fgt.Fgt_FeedBack;
import com.naran.ui.fgt.Fgt_Home;
import com.naran.ui.fgt.Fgt_Min;
import com.naran.ui.fgt.Fgt_Notify;
import com.naran.ui.fgt.home.adapter.ViewPagerFragmentAdapter;
import com.naran.ui.view.NoScrollViewPager;
import com.naran.weather.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class Act_Main extends BaseAct {
    private TextView[] title = new TextView[4];
    private List<BaseFgt> fgtData = new ArrayList<>();
    @Override
    public int initLayoutId() {
        return R.layout.act_main;
    }

    @Override
    public void initView() {
        hideHeader();
        title[0] = getViewAndClick(R.id.home);
        title[1] = getViewAndClick(R.id.feed);
        title[2] = getViewAndClick(R.id.notify);
        title[3] = getViewAndClick(R.id.mine);
        title[0].setSelected(true);
    }

    @Override
    public void initData() {
        if (!EventBus.getDefault().isRegistered(this)) {
            //首先要在你要接受EventBus的界面注册，这一步很重要
            EventBus.getDefault().register(this);
        }
        fgtData.add(new Fgt_Home());
        fgtData.add(new Fgt_FeedBack());
        fgtData.add(new Fgt_Notify());
        fgtData.add(new Fgt_Min());
        getSupportFragmentManager().beginTransaction().add(R.id.myFrame, fgtData.get(0)).add(R.id.myFrame, fgtData.get(1)).hide(fgtData.get(1)).show(fgtData.get(0)).commit();
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
        if (ComantUtils.MyLanguage) {//蒙
            setMongolianUI();
        } else {//汉
            setChineseUI();
        }
    }

    @Override
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.home:
                setCurrent(0);
                break;
            case R.id.feed:
                setCurrent(1);
                break;
            case R.id.notify:
                setCurrent(2);
                break;
            case R.id.mine:
                setCurrent(3);
                break;
        }
    }

    /**
     * 蒙语设置
     */
    private Typeface typeface;
    public void setMongolianUI() {
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/fontUI.ttf");
        //首页
        title[0].setText(getResources().getString(R.string.home_title1));
        title[0].setGravity(View.TEXT_ALIGNMENT_CENTER);
        title[0].setTypeface(typeface);
        title[0].setTextSize(40);
        title[0].setGravity(Gravity.CENTER);
        title[0].setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        //我有话说
        title[1].setText(getResources().getString(R.string.feed_title1));
        title[1].setGravity(View.TEXT_ALIGNMENT_CENTER);
        title[1].setTypeface(typeface);
        title[1].setTextSize(40);
        title[1].setGravity(Gravity.CENTER);
        title[1].setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        //灾情
        title[2].setText(getResources().getString(R.string.notify_title1));
        title[2].setGravity(View.TEXT_ALIGNMENT_CENTER);
        title[2].setTypeface(typeface);
        title[2].setTextSize(40);
        title[2].setGravity(Gravity.CENTER);
        title[2].setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        //我的
        title[3].setText(getResources().getString(R.string.min_title1));
        title[3].setGravity(View.TEXT_ALIGNMENT_CENTER);
        title[3].setTypeface(typeface);
        title[3].setTextSize(40);
        title[3].setGravity(Gravity.CENTER);
        title[3].setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
    }

    /**
     * 汉语设置
     */
    public void setChineseUI() {
        //首页
        title[0].setText(getResources().getString(R.string.home_title));
        title[0].setGravity(View.TEXT_ALIGNMENT_CENTER);
        title[0].setGravity(Gravity.CENTER);
        title[0].setTextSize(16);
        Drawable drawableTop1 = getResources().getDrawable(R.drawable.home);
        title[0].setCompoundDrawablesWithIntrinsicBounds(null, drawableTop1, null, null);
        //我有话说
        title[1].setText(getResources().getString(R.string.feed_title));
        title[1].setGravity(View.TEXT_ALIGNMENT_CENTER);
        title[1].setGravity(Gravity.CENTER);
        title[1].setTextSize(16);
        Drawable drawableTop2 = getResources().getDrawable(R.drawable.feedback);
        title[1].setCompoundDrawablesWithIntrinsicBounds(null, drawableTop2, null, null);
        //灾情
        title[2].setText(getResources().getString(R.string.notify_title));
        title[2].setGravity(View.TEXT_ALIGNMENT_CENTER);
        title[2].setGravity(Gravity.CENTER);
        title[2].setTextSize(16);
        Drawable drawableTop3 = getResources().getDrawable(R.drawable.notify);
        title[2].setCompoundDrawablesWithIntrinsicBounds(null, drawableTop3, null, null);
        //我的
        title[3].setText(getResources().getString(R.string.min_title));
        title[3].setGravity(View.TEXT_ALIGNMENT_CENTER);
        title[3].setGravity(Gravity.CENTER);
        title[3].setTextSize(16);
        Drawable drawableTop4 = getResources().getDrawable(R.drawable.min);
        title[3].setCompoundDrawablesWithIntrinsicBounds(null, drawableTop4, null, null);
    }

    public int currentTabIndex = 0;

    public void setCurrent(int indext) {
        if (currentTabIndex != indext) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fgtData.get(currentTabIndex));
            if (!fgtData.get(indext).isAdded()) {
                trx.add(R.id.myFrame, fgtData.get(indext));
            }
            trx.show(fgtData.get(indext)).commit();
        }
        title[currentTabIndex].setSelected(false);
        title[indext].setSelected(true);
        currentTabIndex = indext;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在界面销毁的地方要解绑
        EventBus.getDefault().unregister(this);
    }
}
