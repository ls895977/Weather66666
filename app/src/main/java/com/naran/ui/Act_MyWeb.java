package com.naran.ui;

import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
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
        //支持javascript
        webSettings.setJavaScriptEnabled(true);
// 设置可以支持缩放
        webSettings.setSupportZoom(true);
// 设置出现缩放工具
        webSettings.setBuiltInZoomControls(true);
//扩大比例的缩放
        webSettings.setUseWideViewPort(true);
//自适应屏幕
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setLoadWithOverviewMode(true);
        myWebView.setWebChromeClient(new WebChromeClient());



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
