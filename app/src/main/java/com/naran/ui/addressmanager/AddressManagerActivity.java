package com.naran.ui.addressmanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.naran.ui.addresmanager1.AddAddress1Activity;
import com.naran.ui.addresmanager1.AddressManager1Activity;
import com.naran.ui.connector.OkHttpUtil;
import com.naran.ui.utils.LoginUtil;
import com.naran.weather.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

import static com.naran.ui.common.connector.OkHttpUtil.ADeleteMyArea;

public class AddressManagerActivity extends Activity implements OnAddressClickListener {


    private RecyclerView recyclerView;
    private RelativeLayout btnAdd;
    private AddressAdapter addressAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<TextArticleTitle> textArticleTitles = new ArrayList<>();
    private Map<String, String> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manager);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        initViews();
        OnAddressClickTask.getInstance().addListener(this);
        getFirstData();
    }

    private void initViews() {
        btnAdd = (RelativeLayout) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(AddressManagerActivity.this, AddAddressActivity.class);
                startActivityForResult(intent, 10);
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) mLayoutManager)
                .setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        addressAdapter = new AddressAdapter(textArticleTitles, this, 0);
        recyclerView.setAdapter(addressAdapter);
    }

    private void getFirstData() {

        textArticleTitles.clear();
        map.put("userid", LoginUtil.userInfoModel.getID() + "");
        OkHttpUtil.postAsync(OkHttpUtil.AGetMyAllAreaTwo, map, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Toast.makeText(AddressManagerActivity.this, "获取地区数据失败！", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void requestSuccess(String result) throws Exception {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray dataArray = jsonObject.optJSONArray("data");
                // popupwindow
                for (int i = 0; i < dataArray.length(); i++) {
                    TextArticleTitle tat = new TextArticleTitle();
                    tat.setContent(dataArray.optJSONObject(i).optString("AreaNO"));
                    tat.setAreaOn(dataArray.optJSONObject(i).optString("AreaNO"));
                    tat.setItemID(dataArray.optJSONObject(i).optInt("AreaID"));
                    tat.setTitle(dataArray.optJSONObject(i).optString("MongolianAreaName"));
                    tat.setCurrentTemperature(dataArray.optJSONObject(i).optString("CurrentTemperature"));
                    tat.setWeatherPhenomenonID(dataArray.optJSONObject(i).optInt("WeatherPhenomenonID"));
                    tat.setWeatherPhenomenon(dataArray.optJSONObject(i).optString("WeatherPhenomenon"));
                    tat.setShowDelete(false);
                    textArticleTitles.add(tat);
                }

                addressAdapter.notifyDataSetChanged();
            }
        });
    }

    private void ADeleteMyArea(final int areaid) {

        map.put("userid", LoginUtil.userInfoModel.getID() + "");
        map.put("areaid", areaid + "");
        OkHttpUtil.postAsync(ADeleteMyArea, map, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Toast.makeText(AddressManagerActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                // popupwindow
                for (int i = 0; i < textArticleTitles.size(); i++) {
                    if (areaid == textArticleTitles.get(i).getItemID()) {
                        textArticleTitles.remove(textArticleTitles.get(i));
                    }
                }
                addressAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onRestart() {
        super.onRestart();
        getFirstData();
    }

    @Override
    public void onClick(int tag, TextArticleTitle textArticleTitle) {
        if (tag == 1 || tag == 2) {
            return;
        }
        if (tag == -2) { // 删除地区
            for (int i = 0; i < textArticleTitles.size(); i++) {
                    textArticleTitles.get(i).setShowDelete(false);
            }
            for (int i = 0; i < textArticleTitles.size(); i++) {
                if (textArticleTitle.getId() == textArticleTitles.get(i).getId()) {
                    textArticleTitles.get(i).setShowDelete(true);
                }
            }
            addressAdapter.notifyDataSetChanged();
        }
        if (tag == 10) {//删除
            ADeleteMyArea(textArticleTitle.getItemID());
        }
        if (tag == -1) {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == 10) {
            getFirstData();
        }
    }
}
