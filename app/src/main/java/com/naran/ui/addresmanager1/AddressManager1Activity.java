package com.naran.ui.addresmanager1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.naran.ui.addressmanager.OnAddressClickListener;
import com.naran.ui.addressmanager.OnAddressClickTask;
import com.naran.ui.addressmanager.TextArticleTitle;
import com.naran.ui.connector.OkHttpUtil;
import com.naran.ui.utils.LoginUtil;
import com.naran.weather.R;
import com.yanzhenjie.recyclerview.swipe.SwipeMenu;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItemClickListener;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuRecyclerView;
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Request;

import static com.naran.ui.connector.OkHttpUtil.ADeleteMyArea;

/**
 * 定位地址查询
 */
public class AddressManager1Activity extends Activity implements AddressChangeListener {
    private SwipeMenuRecyclerView recyclerView;
    private RelativeLayout btnAdd;
    private AddressAdapter1 addressAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<TextArticleTitle> textArticleTitles = new ArrayList<>();
    private Map<String, String> map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_manager1);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        AddressChangeTask.getInstance().addOnAddressChangeListener(this);
        initViews();
        getFirstData();
    }

    private void initViews() {
        btnAdd = (RelativeLayout) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(AddressManager1Activity.this, AddAddress1Activity.class);
                startActivityForResult(intent, 10);
            }
        });
        recyclerView = (SwipeMenuRecyclerView) findViewById(R.id.recyclerView);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setSwipeMenuCreator(swipeMenuCreator);//创建
        recyclerView.setSwipeMenuItemClickListener(mMenuItemClickListener);//返回事件
        addressAdapter = new AddressAdapter1(textArticleTitles, this, 0);
        AddressChangeTask.getInstance().addOnAddressChangeListener(this);
        recyclerView.setAdapter(addressAdapter);
    }

    /**
     * 菜单创建器，在Item要创建菜单的时候调用。
     */
    private SwipeMenuCreator swipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu swipeLeftMenu, SwipeMenu swipeRightMenu, int position) {
            int width = getResources().getDimensionPixelSize(R.dimen.dp_70);
            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            SwipeMenuItem deleteItem = new SwipeMenuItem(AddressManager1Activity.this).setBackground(R.drawable.selector_red)
                    .setImage(R.mipmap.ic_action_delete)
                    .setText("删除")
                    .setTextColor(Color.WHITE)
                    .setWidth(width)
                    .setHeight(height);
            swipeRightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
        }
    };
    /**
     * RecyclerView的Item的Menu点击监听。
     */
    private SwipeMenuItemClickListener mMenuItemClickListener = new SwipeMenuItemClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            menuBridge.closeMenu();
            int direction = menuBridge.getDirection(); // 左侧还是右侧菜单。
            int menuPosition = menuBridge.getPosition(); // 菜单在RecyclerView的Item中的Position。
            if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                ADeleteMyArea(textArticleTitles.get(position).getItemID());
            }
        }
    };

    protected RecyclerView.ItemDecoration createItemDecoration() {
        return new DefaultItemDecoration(ContextCompat.getColor(this, R.color.divider_color));
    }

    private void getFirstData() {
        textArticleTitles.clear();
        map.put("userid", LoginUtil.userInfoModel.getID() + "");
        OkHttpUtil.postAsync(OkHttpUtil.AGetMyAllAreaTwo, map, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Toast.makeText(AddressManager1Activity.this, "获取地区数据失败！", Toast.LENGTH_SHORT).show();
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
                    tat.setTitle(dataArray.optJSONObject(i).optString("CHineseAreaName"));
                    tat.setCurrentTemperature(dataArray.optJSONObject(i).optString("CurrentTemperature"));
                    tat.setWeatherPhenomenonID(dataArray.optJSONObject(i).optInt("WeatherPhenomenonID"));
                    tat.setWeatherPhenomenon(dataArray.optJSONObject(i).optString("WeatherPhenomenonCn"));
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
                Toast.makeText(AddressManager1Activity.this, "删除失败", Toast.LENGTH_SHORT).show();
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
    public void onAddressChange(int tag, TextArticleTitle tat) {
        if (tag == 0) {
            finish();
        } else if (tag == 1) {
            ADeleteMyArea(tat.getItemID());
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
