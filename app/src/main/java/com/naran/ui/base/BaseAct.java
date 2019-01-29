package com.naran.ui.base;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import com.lykj.aextreme.afinal.common.BaseActivity;
public abstract class BaseAct extends BaseActivity {
    protected InputMethodManager inputMethodManager;
    public Bundle mSavedInstanceState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTranslucentStatus(false);
        mSavedInstanceState = savedInstanceState;
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {
            Intent intent = getIntent();
            String action = intent.getAction();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && action.equals(Intent.ACTION_MAIN)) {
                finish();
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 初始化头,默认返回按钮
     *
     * @param title 标题
     * @param right 右边图标
     */
    protected void initHeaderBack(@StringRes int title, @DrawableRes int right) {
//        setHeaderLeft(R.mipmap.icon_leftback);
        setHeaderRight(right);
//        setToolbarBack(R.color.MyTitleColor);
        setTitle(title);
    }
    protected void  initchatTitle(CharSequence title, @DrawableRes int right){
//        setHeaderLeft(R.mipmap.icon_leftback);
        setHeaderRight(right);
//        setToolbarBack(R.color.MyTitleColor);
        setTitle(title);
    }
    protected void setLeftTitle() {
//        setHeaderLeft(R.mipmap.icon_leftback);
//        setToolbarBack(R.color.MyTitleColor);
        setTitle("");
    }

    protected void setTitleback(int title) {
//        setHeaderLeft(R.mipmap.icon_leftback);
        setTitle(title);
//        setToolbarBack(R.color.MyTitleColor);
    }

    /**
     * 初始化头,默认返回按钮
     *
     * @param title 标题
     * @param right 右边文字
     */
    protected void initHeaderBackTxt(@StringRes int title, @StringRes int right) {
//        setHeaderLeft(R.mipmap.icon_leftback);
        setHeaderRightTxt(right);
        setTitle(title);
//        setToolbarBack(R.color.MyTitleColor);
    }

    public void setTitle1(CharSequence title) {
        setTitle(title);
//        setHeaderLeft(R.mipmap.icon_leftback);
//        setToolbarBack(R.color.MyTitleColor);
    }
}
