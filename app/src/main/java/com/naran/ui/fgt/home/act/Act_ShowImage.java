package com.naran.ui.fgt.home.act;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Debug;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.naran.ui.base.BaseAct;
import com.naran.ui.view.TouchImageView;
import com.naran.weather.R;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Act_ShowImage extends BaseAct {
    String imgUrl = "";
    ImageView touchImageView;
    private Handler handler;
    ProgressBar progressBar;
    @Override
    public int initLayoutId() {
        return R.layout.act_show_image;
    }
    @Override
    public void initView() {
        hideHeader();
    }
    @Override
    public void initData() {
        handler = new Handler();
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        touchImageView = (ImageView)findViewById(R.id.touchImage);
        progressBar = (ProgressBar)findViewById(R.id.progressBar2);
        imgUrl = getIntent().getStringExtra("imgUrl");
        if(null == imgUrl|| imgUrl.equals("")){
            Toast.makeText(this, "图片路径错误！", Toast.LENGTH_SHORT).show();
            finish();
        }
        Glide.with(context).load(imgUrl).into(touchImageView);
        progressBar.setVisibility(View.GONE);
    }
    @Override
    public void requestData() {

    }

    @Override
    public void updateUI() {

    }

    @Override
    public void onViewClick(View v) {

    }
}
