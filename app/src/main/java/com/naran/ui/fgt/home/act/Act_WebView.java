package com.naran.ui.fgt.home.act;

import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.naran.ui.base.BaseAct;
import com.naran.weather.R;

public class Act_WebView extends BaseAct {
    String url;
    WebView webView;
    private ProgressBar progressBar3;
    @Override
    public int initLayoutId() {
        return R.layout.activity_web_view;
    }

    @Override
    public void initView() {
        hideHeader();
        getViewAndClick(R.id.back);
        webView = getView(R.id.webview);
        progressBar3 = getView(R.id.progressBar3);
    }

    @Override
    public void initData() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar3.setVisibility(View.GONE);
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        url = getIntent().getStringExtra("imgUrl");
        webView.loadUrl(url);
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
