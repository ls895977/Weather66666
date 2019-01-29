package com.naran.ui.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lykj.aextreme.afinal.common.BaseFragment;


public abstract class BaseFgt extends BaseFragment {
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setTranslucentStatus(false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * 初始化头,默认返回按钮
     *
     * @param title 标题
     * @param right 右边图标
     */
    protected void initHeaderBack(int title, int right) {
//        setHeaderLeft(R.mipmap.icon_back);
        setHeaderRight(right);
        setTitle(title);
    }
    /**
     * 初始化头,默认返回按钮
     *
     * @param title 标题
     * @param right 右边图标
     */
    protected void initHeaderBackLefit(int title, int right) {
        setHeaderRight(right);
        setTitle(title);
    }

    /**
     * 初始化头,默认返回按钮
     *
     * @param title 标题
     * @param right 右边文字
     */
    protected void initHeaderBackTxt(int title, int right) {
//        setHeaderLeft(R.mipmap.icon_back);
        setHeaderRightTxt(right);
        setTitle(title);
    }
    /**
     * 设置标题
     */
    protected void setTitleback(int title){
        setTitle(title);
    }
}
