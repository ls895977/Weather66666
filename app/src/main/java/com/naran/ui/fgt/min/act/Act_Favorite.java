package com.naran.ui.fgt.min.act;

import android.view.View;
import android.widget.LinearLayout;

import com.naran.ui.base.BaseAct;
import com.naran.ui.common.ComantUtils;
import com.naran.ui.fgt.min.act.favorite.View_Cn_Favorite;
import com.naran.ui.fgt.min.act.favorite.View_MU_Favorite;
import com.naran.weather.R;

/**
 * 兴趣爱好
 */
public class Act_Favorite extends BaseAct {
    private LinearLayout myLinear;

    @Override
    public int initLayoutId() {
        return R.layout.act_favorite;
    }

    @Override
    public void initView() {
        hideHeader();
        myLinear = getView(R.id.myLinear_favorite);
    }

    @Override
    public void initData() {
        updateUI();
    }

    @Override
    public void requestData() {

    }

    @Override
    public void updateUI() {
        myLinear.removeAllViews();
        if (ComantUtils.MyLanguage) {//蒙
            myLinear.addView(new View_MU_Favorite(this, this));
        } else {//汉
            myLinear.addView(new View_Cn_Favorite(this, this));
        }
    }

    @Override
    public void onViewClick(View v) {

    }
}
