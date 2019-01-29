package com.naran.ui.fgt.home.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.MotionEvent;

import com.naran.ui.base.BaseFgt;
import java.util.List;

public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {

    List<BaseFgt> HomeFgtData ;
    public ViewPagerFragmentAdapter(FragmentManager fm , List<BaseFgt> HomeFgtData1) {
        super(fm);
        HomeFgtData=HomeFgtData1;
    }

    @Override
    public Fragment getItem(int position) {
        return HomeFgtData.get(position);
    }

    @Override
    public int getCount() {
        return HomeFgtData.size();
    }



}