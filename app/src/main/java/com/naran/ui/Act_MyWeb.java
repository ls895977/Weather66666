package com.naran.ui;

import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.lykj.aextreme.afinal.common.BaseActivity;
import com.lykj.aextreme.afinal.utils.Debug;
import com.naran.weather.R;

public class Act_MyWeb extends BaseActivity {
    private String url;
    private WebView myWebView;

    @Override
    public int initLayoutId() {
        return R.layout.act_myweb;
    }

    @Override
    public void initView() {
        hideHeader();
        setOnClickListener(R.id.back);
        myWebView = getView(R.id.myWeb1);
    }

    @Override
    public void initData() {
        url = getIntent().getStringExtra("result");
        Debug.e("-------------myWebView==" + url);
        myWebView.loadUrl(url);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);  //支持js
        webSettings.setUseWideViewPort(false);  //将图片调整到适合webview的大小
        webSettings.setSupportZoom(true);  //支持缩放
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //关闭webview中缓存
        webSettings.setAllowFileAccess(true);  //设置可以访问文件
        webSettings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setLoadsImagesAutomatically(true);  //支持自动加载图片
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
        }
    }
}
