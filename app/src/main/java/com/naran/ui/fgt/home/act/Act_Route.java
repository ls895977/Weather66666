package com.naran.ui.fgt.home.act;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.naran.ui.base.BaseAct;
import com.naran.ui.connector.OkHttpUtil;
import com.naran.ui.fgt.home.act.adapter.AreaAdapter;
import com.naran.ui.fgt.home.act.adapter.ProvinceAdapter;
import com.naran.ui.fgt.home.act.adapter.ProvinceAreaAdapter;
import com.naran.ui.fgt.home.act.bean.ProvinceAreaModel;
import com.naran.ui.fgt.home.act.bean.ProvinceModel;
import com.naran.weather.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.Request;

/**
 * 定位查天气
 */
public class Act_Route extends BaseAct {

    private List<ProvinceModel> provinceModels = new ArrayList<>();
    private List<ProvinceAreaModel> provinceAreaModels = new ArrayList<>();
    private List<ProvinceAreaModel> areaModels = new ArrayList<>();
    private EditText start, end;
    private LinearLayout dateLayout;
    private DatePicker datePicker;
    private TextView date;
    private Button leftRight;
    private Button search;
    private RecyclerView provinceModelRecycler, provinceAreaModelRecycler, areaRecycler;
    private RecyclerView.LayoutManager mLayoutManager, layoutManager, areaLayoutManager;
    private ProvinceAdapter provinceAdapter;
    private ProvinceAreaAdapter provinceAreaAdapter;
    private AreaAdapter areaAdapter;
    private LinearLayout datePickerlayout;
    private int proviceID = -1;
    private String proviceName;
    private boolean isStart = true;
    private LinearLayout provinceAreaModelRecyclerLayout;
    private LinearLayout areaRecyclerLayout;
    @Override
    public int initLayoutId() {
        return R.layout.act_route;
    }
    @Override
    public void initView() {
        hideHeader();
        getViewAndClick(R.id.back);
        areaRecycler = getView(R.id.areaRecycler);
        areaLayoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) areaLayoutManager).setOrientation(LinearLayoutManager.VERTICAL);
        areaRecycler.setLayoutManager(areaLayoutManager);
        provinceAreaModelRecyclerLayout = getView(R.id.provinceAreaModelRecyclerLayout);
        areaRecyclerLayout = getView(R.id.areaRecyclerLayout);
        areaAdapter = new AreaAdapter(areaModels, this);
        provinceAdapter = new ProvinceAdapter(provinceModels, this);
        provinceAreaAdapter = new ProvinceAreaAdapter(provinceAreaModels, this);
        provinceModelRecycler = getView(R.id.provinceModelRecycler);
        provinceAreaModelRecycler = getView(R.id.provinceAreaModelRecycler);
        mLayoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) mLayoutManager)
                .setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager)
                .setOrientation(LinearLayoutManager.VERTICAL);
        provinceAreaModelRecycler.setLayoutManager(mLayoutManager);
        provinceModelRecycler.setLayoutManager(layoutManager);
        provinceAreaModelRecycler.setAdapter(provinceAreaAdapter);
        provinceModelRecycler.setAdapter(provinceAdapter);
        areaRecycler.setAdapter(areaAdapter);
        search = getView(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("imgUrl", "http://121.41.123.152:8088/weather/index.html?starts=" + start.getText().toString() + "&ends=" + end.getText().toString() + "&start=" + start.getText().toString() + "&end=" + end.getText().toString() + "&date=2017.11.02%2014:10&_cmpt");
                intent.setClass(Act_Route.this, Act_WebView.class);
                startActivity(intent);
            }
        });
        leftRight = getView(R.id.leftRight);
        leftRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String temp = start.getText().toString();
                start.setText(end.getText().toString());
                end.setText(temp);
            }
        });
        date = getView(R.id.date);
        datePickerlayout = getView(R.id.datePickerLayout);
        datePicker = getView(R.id.datePicker);
        datePicker.init(this.datePicker.getYear(), this.datePicker.getMonth(), this.datePicker.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {

                date.setText(i + "-" + (i1 + 1) + "-" + i2 + "");
                datePickerlayout.setVisibility(View.GONE);
            }
        });
        date.setText(this.datePicker.getYear() + "-" + (this.datePicker.getMonth() + 1) + "-" + this.datePicker.getDayOfMonth());
        dateLayout = getView(R.id.dateLayout);
        dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerlayout.setVisibility(View.VISIBLE);
            }
        });
        start = getView(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStart = true;
                provinceModelRecycler.setVisibility(View.VISIBLE);
            }
        });
        end = getView(R.id.end);
        end.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                isStart = false;
                provinceModelRecycler.setVisibility(View.VISIBLE);
                return true;
            }
        });
        OkHttpUtil.getAsync(OkHttpUtil.getProvince, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                provinceAdapter.notifyDataSetChanged();
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                if (result != null) {
                    JSONObject jb = new JSONObject(result);
                    JSONArray ja = jb.optJSONArray("data");
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject o = ja.optJSONObject(i);
                        ProvinceModel provinceModel = new ProvinceModel();
                        provinceModel.setProvinceID(o.optInt("ProvinceID"));
                        provinceModel.setProvinceName(o.optString("ProvinceName"));
                        provinceModel.setSortNo(o.optInt("SortNo"));
                        provinceModels.add(provinceModel);
                    }
                    provinceAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void initData() {
        initDatePicker();
    }
    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        String now = sdf.format(new Date());

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

    public void setProviceID(int proviceID, final String proviceName) {

        provinceAreaModels.clear();
        provinceAreaAdapter.Loading();
        provinceAreaAdapter.notifyDataSetChanged();
        provinceAreaModelRecyclerLayout.setVisibility(View.VISIBLE);
        this.proviceID = proviceID;
        Log.e("proviceID", "proviceID = " + proviceID);
        Map<String, String> map = new HashMap<>();
        map.put("ParentID", proviceID + "");
        map.put("CnType", 2 + "");
        OkHttpUtil.postAsync(OkHttpUtil.getProvinceArea, map, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {


            }
            @Override
            public void requestSuccess(String result) throws Exception {
                if (result != null) {
                    provinceAreaAdapter.Loadend();
                    JSONObject jb = new JSONObject(result);
                    JSONArray ja = jb.optJSONArray("data");
                    if(ja.length()==0){
                        setProviceName(proviceName);
                    }else {
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject o = ja.optJSONObject(i);
                            ProvinceAreaModel provinceAreaModel = new ProvinceAreaModel();
                            provinceAreaModel.setSortNo(o.optInt("SortNo"));
                            provinceAreaModel.setCnAreaID(o.optInt("CnAreaID"));
                            provinceAreaModel.setCnAreaName(o.optString("CnAreaName"));
                            provinceAreaModel.setCnType(o.optInt("CnType"));
                            provinceAreaModel.setParentID(o.optInt("ParentID"));
                            provinceAreaModels.add(provinceAreaModel);
                        }
                        provinceAreaAdapter.notifyDataSetChanged();
                    }


                }
            }
        });
    }
    public void setProviceName(String proviceName) {
        provinceAreaModelRecyclerLayout.setVisibility(View.GONE);
        provinceModelRecycler.setVisibility(View.GONE);
        areaRecyclerLayout.setVisibility(View.GONE);
        this.proviceName = proviceName;
        if (isStart) {
            start.setText(proviceName);
        } else {
            end.setText(proviceName);
        }
    }
    public void setProviceAreaID(int proviceID, final String proviceName) {
        areaModels.clear();
        areaAdapter.Loading();
        areaAdapter.notifyDataSetChanged();
        provinceAreaModelRecyclerLayout.setVisibility(View.GONE);
        areaRecyclerLayout.setVisibility(View.VISIBLE);
        this.proviceID = proviceID;
        Log.e("proviceID", "proviceID = " + proviceID);
        Map<String, String> map = new HashMap<>();
        map.put("ParentID", proviceID + "");
        map.put("CnType", 1 + "");
        OkHttpUtil.postAsync(OkHttpUtil.getProvinceArea, map, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {


            }
            @Override
            public void requestSuccess(String result) throws Exception {
                if (result != null) {
                    areaAdapter.Loadend();
                    JSONObject jb = new JSONObject(result);
                    JSONArray ja = jb.optJSONArray("data");
                    if(ja.length()==0){
                        setProviceName(proviceName);
                    }else {
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject o = ja.optJSONObject(i);
                            ProvinceAreaModel provinceAreaModel = new ProvinceAreaModel();
                            provinceAreaModel.setSortNo(o.optInt("SortNo"));
                            provinceAreaModel.setCnAreaID(o.optInt("CnAreaID"));
                            provinceAreaModel.setCnAreaName(o.optString("CnAreaName"));
                            provinceAreaModel.setCnType(o.optInt("CnType"));
                            provinceAreaModel.setParentID(o.optInt("ParentID"));
                            areaModels.add(provinceAreaModel);
                        }
                        areaAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

}
