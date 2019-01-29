package com.naran.ui.fgt;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lykj.aextreme.afinal.utils.Debug;
import com.naran.ui.Act_Login;
import com.naran.ui.base.BaseFgt;
import com.naran.ui.base.MessageEvent;
import com.naran.ui.common.ComantUtils;
import com.naran.ui.connector.OkHttpUtil;
import com.naran.ui.fgt.min.act.Act_SetUp;
import com.naran.ui.utils.LoginUtil;
import com.naran.weather.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;

/**
 * 我有话说
 */
public class Fgt_FeedBack extends BaseFgt {
    private TextView btnSend;
    private Map<String, String> map = new HashMap<>();
    private EditText editText;
    private RelativeLayout msgLayout, sendLayout;

    @Override
    public int initLayoutId() {
        return R.layout.fgt_feedback;
    }

    @Override
    public void initView() {
        hideHeader();
        if (!EventBus.getDefault().isRegistered(this)) {
            //首先要在你要接受EventBus的界面注册，这一步很重要
            EventBus.getDefault().register(this);
        }
        btnSend = getViewAndClick(R.id.send);
        msgLayout = getView(R.id.msgLayout);
        sendLayout = getView(R.id.sendLayout);
        editText = getView(R.id.editText);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.type == 1) {
            updateUI();
        }
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
        if (ComantUtils.MyLanguage) {//蒙
            btnSend.setBackgroundResource(R.drawable.post);
        } else {//汉
            btnSend.setBackgroundResource(R.drawable.posthitad);
        }
    }

    @Override
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                if (LoginUtil.getInstance().isLogined) {
                    postCommit();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("status", "Notify");
                    intent.setClass(context, Act_Login.class);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void sendMsg(int flag, Object obj) {

    }

    /**
     * 提交内容
     */
    public void postCommit() {
        if (editText.getText().toString().equals("")) {
            Toast.makeText(getActivity(), "请输入内容！", Toast.LENGTH_SHORT).show();
            return;
        }
        sendLayout.setVisibility(View.VISIBLE);
        map.put("addUserId", LoginUtil.userInfoModel.getID() + "");
        map.put("areaNo", LoginUtil.userInfoModel.getAreaNO());
        map.put("content", editText.getText().toString());
        OkHttpUtil.postAsync(OkHttpUtil.AddFeedBack, map, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                sendLayout.setVisibility(View.GONE);
            }
            @Override
            public void requestSuccess(String result) {
                editText.setText("");
                Toast.makeText(getActivity(), "反馈成功！", Toast.LENGTH_SHORT).show();
                sendLayout.setVisibility(View.GONE);
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        //在界面销毁的地方要解绑
        EventBus.getDefault().unregister(this);
    }
}
