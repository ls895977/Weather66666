package com.naran.ui.fgt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.bigkoo.svprogresshud.SVProgressHUD;
import com.bumptech.glide.Glide;
import com.lykj.aextreme.afinal.utils.Debug;
import com.lykj.aextreme.afinal.utils.MyToast;
import com.naran.ui.Act_Login;
import com.naran.ui.base.BaseFgt;
import com.naran.ui.base.MessageEvent;
import com.naran.ui.common.ComantUtils;
import com.naran.ui.connector.OkHttpUtil;
import com.naran.ui.fgt.min.act.Act_SetUp;
import com.naran.ui.fgt.min.dialog.Dlg_Photograph;
import com.naran.ui.permission.RxPermissions;
import com.naran.ui.utils.LoginUtil;
import com.naran.ui.utils.StringUtil;
import com.naran.ui.view.AudioUtils.AudioRecordButton;
import com.naran.ui.view.AudioUtils.PermissionHelper;
import com.naran.ui.view.AudioUtils.Record;
import com.naran.weather.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;
import okhttp3.Request;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 灾情通报
 */
public class Fgt_Notify extends BaseFgt implements Dlg_Photograph.OnClick {
    private LinearLayout myLiner;
    private LocationClient mLocationClient = null;
    private AudioRecordButton mEmTvBtn;
    private String voicIDs = "";
    private ImageView call, notify1, notify2, notify4, notify5, notify6;
    private EditText position;
    private EditText edittext1, edittext2, edittext3, edittext5, edittext6, edittext7;
    private RxPermissions rxPermissions;
    private List<File> files = new ArrayList<>();
    public SVProgressHUD mSVProgressHUD;

    @Override
    public int initLayoutId() {
        return R.layout.fgt_notify;
    }

    private Handler handler;

    @Override
    public void initView() {
        hideHeader();
        handler = new Handler();
        myLiner = getView(R.id.baseLayout);
        mSVProgressHUD = new SVProgressHUD(context);
    }

    private Dlg_Photograph photograph;

    @Override
    public void initData() {
        rxPermissions = new RxPermissions(getActivity());
        photograph = new Dlg_Photograph(context, Fgt_Notify.this);
        if (!EventBus.getDefault().isRegistered(this)) {
            //首先要在你要接受EventBus的界面注册，这一步很重要
            EventBus.getDefault().register(this);
        }
        openQuanxian();
    }

    @Override
    public void requestData() {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.type == 1) {
            updateUI();
        }
    }

    private View view;

    @Override
    public void updateUI() {
        files.clear();
        myLiner.removeAllViews();
        if (ComantUtils.MyLanguage) {//蒙
            view = LayoutInflater.from(context).inflate(R.layout.view_mu_notify, null);
        } else {//汉
            view = LayoutInflater.from(context).inflate(R.layout.view_cn_notify, null);
        }
        mEmTvBtn = getView(view, R.id.em_tv_btn);//语音
        call = getViewAndClick(view, R.id.imageView_notify_call);//电话
        getViewAndClick(view, R.id.button_notify1_submit);//提交按钮
        getViewAndClick(view, R.id.disaster);//拍照
        position = getView(view, R.id.imageView_notify_position);//定位的地址
        edittext1 = getView(view, R.id.edittext_notify1_1);//死亡人数
        edittext2 = getView(view, R.id.edittext_notify1_2);//受伤人数
        edittext3 = getView(view, R.id.edittext_notify1_3);//受影响人数
        edittext5 = getView(view, R.id.edittext_notify1_5);//房屋受损情况
        edittext6 = getView(view, R.id.edittext_notify1_6);//畜牧受损情况
        edittext7 = getView(view, R.id.edittext_notify1_7);//其他影响
        notify1 = getView(view, R.id.imageView_notify_1_chinese);//图1
        notify2 = getView(view, R.id.imageView_notify_2_chinese);//图2
        notify4 = getView(view, R.id.imageView_notify_4_chinese);//图3
        notify5 = getView(view, R.id.imageView_notify_5_chinese);//图4
        notify6 = getView(view, R.id.imageView_notify_6_chinese);//图5
        myLiner.addView(view);
        speechProcessing();//语音
    }

    @Override
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_notify_call://电话
                rxPermissions
                        .request(Manifest.permission.CALL_PHONE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) {
                                if (aBoolean) {
                                    showCall();
                                } else {
                                    Toast.makeText(context, "请打开打电话权限", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            case R.id.button_notify1_submit://提交按钮
                if (LoginUtil.getInstance().isLogined) {
                    postFileImage();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra("status", "Notify");
                    intent.setClass(context, Act_Login.class);
                    startActivity(intent);
                }
                break;
            case R.id.disaster://拍照=选择照片
                if (files.size() == 5) {
                    Toast.makeText(context, "图片已达上限，请上传后重试！", Toast.LENGTH_SHORT).show();
                    return;
                }
                rxPermissions
                        .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean aBoolean) {
                                if (aBoolean) {
                                    photograph.show();
                                } else {
                                    Toast.makeText(context, "请打开读写存储卡权限", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
        }
    }

    @Override
    public void sendMsg(int flag, Object obj) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //在界面销毁的地方要解绑
        EventBus.getDefault().unregister(this);
    }

    /**
     * 打电话
     */
    private void showCall() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "04792242891"));
        startActivity(intent);
    }

    /**
     * 定位
     */
    private void initLocation() {
        mLocationClient = new LocationClient(context);
        //声明LocationClient类
        mLocationClient.registerLocationListener(bdLocationListener);
        //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        option.setAddrType("all"); //加上这个配置后才可以取到详细地址信息
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");
        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setScanSpan(1);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);
        //可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);
        //可选，默认false,设置是否使用gps
        option.setLocationNotify(true);
        //可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);
        //可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    BDLocationListener bdLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            Debug.e("----------定位====" + location.getCoorType());
            if (location == null) {
                Toast.makeText(context, "无法定位", Toast.LENGTH_SHORT).show();
                return;
            }
            //获取定位结果
            final StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());    //获取定位时间
            sb.append("\nerror code : ");
            sb.append(location.getLocType());    //获取类型类型
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());    //获取纬度信息
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());    //获取经度信息
            sb.append("\nradius : ");
            sb.append(location.getRadius());    //获取定位精准度
            String address = "";
            Debug.e("----------定位====" + sb);
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                // GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());    // 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());    //获取卫星数
                sb.append("\nheight : ");
                sb.append(location.getAltitude());    //获取海拔高度信息，单位米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());    //获取方向信息，单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息
                address = location.getAddrStr();
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                // 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());    //获取运营商信息
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
                address = location.getAddrStr();
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {
                // 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
                address = location.getAddrStr();
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());    //位置语义化信息
            List<Poi> list = location.getPoiList();    // POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            final String add = address;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    position.setText(add);
                }
            });
            Log.i("BaiduLocationApiDem", sb.toString());
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
            Debug.e("--------onConnectHotSpotMessage---定位回调--" + s);
        }
    };
    /**
     * 输入语音处理
     */
    PermissionHelper mHelper;

    public void speechProcessing() {
        mEmTvBtn.setHasRecordPromission(false);
//        授权处理
        mHelper = new PermissionHelper(this);
        mHelper.requestPermissions("请授予[录音]，[读写]权限，否则无法录音",
                new PermissionHelper.PermissionListener() {
                    @Override
                    public void doAfterGrand(String... permission) {
                        mEmTvBtn.setHasRecordPromission(true);
                        mEmTvBtn.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {
                            @Override
                            public void onFinished(float seconds, String filePath) {
                                // 录音
                                Record recordModel = new Record();
                                recordModel.setSecond((int) seconds <= 0 ? 1 : (int) seconds);
                                recordModel.setPath(filePath);
                                recordModel.setPlayed(false);
                                // 录音结束
                                ///// 音频文件发送到后台
                                Map<String, String> map = new HashMap<>();
                                try {
                                    map.put("btyestr", StringUtil.voic2Base64File(filePath));
                                    map.put("imgname", "voic" + System.currentTimeMillis() + ".amr");
                                    map.put("Position", "123-456");
                                    OkHttpUtil.postAsync(OkHttpUtil.IP + "/AUploadImgs", map, new OkHttpUtil.DataCallBack() {
                                        @Override
                                        public void requestFailure(Request request, IOException e) {
                                            MyToast.show(context, request.toString());
                                        }

                                        @Override
                                        public void requestSuccess(String result) throws Exception {
                                            JSONObject jb = new JSONObject(result);
                                            if (voicIDs.equals("")) {
                                                voicIDs = jb.optString("ImgID");
                                            } else {
                                                voicIDs += "," + jb.optString("ImgID");
                                            }
                                            MyToast.show(context, "语音上传成功！");
                                        }
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }

                    @Override
                    public void doAfterDenied(String... permission) {
                        mEmTvBtn.setHasRecordPromission(false);
                        MyToast.show(context, "请授权,否则无法录音");
                    }
                }, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * 上传图片信息
     */
    private String ids = "";

    private void postFileImage() {
        if (files.size() == 0) {
            MyToast.show(context, "你還未選擇一張圖片呢！");
            return;
        }
        mSVProgressHUD.showWithStatus("正在提交内容...请稍后！");
        if (files.size() > 0) {
            notify1.setDrawingCacheEnabled(true);
            Bitmap bitmap1 = notify1.getDrawingCache();
            Map<String, String> map = new HashMap<>();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 40, bos);
            byte[] bytes = bos.toByteArray();
            String base64Img = Base64.encodeToString(bytes, Base64.DEFAULT);
            map.put("btyestr", base64Img);
            map.put("imgname", "darhad" + System.currentTimeMillis() + ".png");
            map.put("Position", "123-456");
            OkHttpUtil.postAsync("http://121.41.123.152:8088/interface.asmx/AUploadImgs", map, new OkHttpUtil.DataCallBack() {
                @Override
                public void requestFailure(Request request, IOException e) {
                    Toast.makeText(getActivity(), request.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void requestSuccess(String result) throws Exception {
                    Debug.e("-----00-----" + result);
                    JSONObject jb = new JSONObject(result);

                    if (ids.equals("")) {
                        ids = jb.optString("ImgID");
                    } else {
                        ids += "," + jb.optString("ImgID");
                    }
                    notify1.setDrawingCacheEnabled(false);
                }
            });
        }
        if (files.size() > 1) {
            notify2.setDrawingCacheEnabled(true);
            Bitmap bitmap1 = notify2.getDrawingCache();
            Map<String, String> map = new HashMap<>();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 40, bos);
            byte[] bytes = bos.toByteArray();
            String base64Img = Base64.encodeToString(bytes, Base64.DEFAULT);
            map.put("btyestr", base64Img);
            map.put("imgname", "darhad" + System.currentTimeMillis() + ".png");
            map.put("Position", "123-456");
            OkHttpUtil.postAsync("http://121.41.123.152:8088/interface.asmx/AUploadImgs", map, new OkHttpUtil.DataCallBack() {
                @Override
                public void requestFailure(Request request, IOException e) {
                    Toast.makeText(getActivity(), request.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void requestSuccess(String result) throws Exception {
                    Debug.e("-----111-----" + result);
                    JSONObject jb = new JSONObject(result);
                    if (ids.equals("")) {
                        ids = jb.optString("ImgID");
                    } else {
                        ids += "," + jb.optString("ImgID");
                    }
                    notify2.setDrawingCacheEnabled(false);
                }
            });
        }
        if (files.size() > 2) {
            notify4.setDrawingCacheEnabled(true);
            Bitmap bitmap1 = notify4.getDrawingCache();
            Map<String, String> map = new HashMap<>();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 40, bos);
            byte[] bytes = bos.toByteArray();
            String base64Img = Base64.encodeToString(bytes, Base64.DEFAULT);
            map.put("btyestr", base64Img);
            map.put("imgname", "darhad" + System.currentTimeMillis() + ".png");
            map.put("Position", "123-456");
            OkHttpUtil.postAsync("http://121.41.123.152:8088/interface.asmx/AUploadImgs", map, new OkHttpUtil.DataCallBack() {
                @Override
                public void requestFailure(Request request, IOException e) {
                    Toast.makeText(getActivity(), request.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void requestSuccess(String result) throws Exception {
                    Debug.e("-----222-----" + result);
                    JSONObject jb = new JSONObject(result);
                    if (ids.equals("")) {
                        ids = jb.optString("ImgID");
                    } else {
                        ids += "," + jb.optString("ImgID");
                    }
                }
            });
        }
        if (files.size() > 3) {
            notify5.setDrawingCacheEnabled(true);
            Bitmap bitmap1 = notify5.getDrawingCache();

            Map<String, String> map = new HashMap<>();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 40, bos);
            byte[] bytes = bos.toByteArray();
            String base64Img = Base64.encodeToString(bytes, Base64.DEFAULT);
            map.put("btyestr", base64Img);
            map.put("imgname", "darhad" + System.currentTimeMillis() + ".png");
            map.put("Position", "123-456");
            OkHttpUtil.postAsync("http://121.41.123.152:8088/interface.asmx/AUploadImgs", map, new OkHttpUtil.DataCallBack() {
                @Override
                public void requestFailure(Request request, IOException e) {
                    Toast.makeText(getActivity(), request.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void requestSuccess(String result) throws Exception {
                    Debug.e("-----333-----" + result);
                    JSONObject jb = new JSONObject(result);
                    if (ids.equals("")) {
                        ids = jb.optString("ImgID");
                    } else {
                        ids += "," + jb.optString("ImgID");
                    }
                }
            });
        }
        if (files.size() > 4) {
            notify6.setDrawingCacheEnabled(true);
            Bitmap bitmap1 = notify6.getDrawingCache();
            Map<String, String> map = new HashMap<>();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap1.compress(Bitmap.CompressFormat.JPEG, 40, bos);
            byte[] bytes = bos.toByteArray();
            String base64Img = Base64.encodeToString(bytes, Base64.DEFAULT);
            map.put("btyestr", base64Img);
            map.put("imgname", "darhad" + System.currentTimeMillis() + ".png");
            map.put("Position", "123-456");
            OkHttpUtil.postAsync("http://121.41.123.152:8088/interface.asmx/AUploadImgs", map, new OkHttpUtil.DataCallBack() {
                @Override
                public void requestFailure(Request request, IOException e) {
                    Toast.makeText(getActivity(), request.toString(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void requestSuccess(String result) throws Exception {
                    Debug.e("-----44-----" + result);
                    JSONObject jb = new JSONObject(result);
                    if (ids.equals("")) {
                        ids = jb.optString("ImgID");
                    } else {
                        ids += "," + jb.optString("ImgID");
                    }
                    notify6.setDrawingCacheEnabled(false);
                }
            });
        }
        postMessage();
    }

    /**
     * 提交文本信息内容
     */
    private void postMessage() {
        Map<String, String> map = new HashMap<>();
        if (edittext1.getText().toString().equals("")) {
            map.put("DeathNumber", "0");
        } else {
            map.put("DeathNumber", edittext1.getText().toString());
        }
        if (edittext1.getText().toString().equals("")) {
            map.put("InjuredNumber", "0");
        } else {
            map.put("InjuredNumber", edittext2.getText().toString());
        }
        if (edittext1.getText().toString().equals("")) {
            map.put("InfluenceNumber", "0");
        } else {
            map.put("InfluenceNumber", edittext3.getText().toString());
        }
        if (edittext1.getText().toString().equals("")) {
            map.put("HouseDamaged", "0");
        } else {
            map.put("HouseDamaged", edittext5.getText().toString());
        }
        if (edittext1.getText().toString().equals("")) {
            map.put("livestockDamaged", "0");
        } else {
            map.put("livestockDamaged", edittext6.getText().toString());
        }
        if (edittext1.getText().toString().equals("")) {
            map.put("OtherInfluence", "0");
        } else {
            map.put("OtherInfluence", edittext7.getText().toString());
        }
        map.put("FullName", LoginUtil.getInstance().userInfoModel.getUserName());
        Debug.e("=======FullName=====" + LoginUtil.getInstance().userInfoModel.getUserName());
        map.put("PhoneNumber", LoginUtil.getInstance().userInfoModel.getUserName());
        Debug.e("=======PhoneNumber=====" + LoginUtil.getInstance().userInfoModel.getUserName());
        map.put("Position", position.getText().toString() + "|" + ids);
        Debug.e("=======Position=====" + position.getText().toString() + "|" + ids + "|" + voicIDs);
        OkHttpUtil.postAsync("http://121.41.123.152:8088/interface.asmx/AAddDisasterReporting", map, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Log.e("aa", "====requestFailure----" + request.toString());
                Toast.makeText(getActivity(), request.toString(), Toast.LENGTH_SHORT).show();
                Log.e("requestFailure", request.toString());
                mSVProgressHUD.dismiss();
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("aa", "====requestSuccess----" + result);
                mSVProgressHUD.dismiss();
                if (result.contains("索引超出了数组界限")) {
                    return;
                }
                JSONObject jb = new JSONObject(result);
                notify1.setImageDrawable(null);
                notify2.setImageDrawable(null);
                notify4.setImageDrawable(null);
                notify5.setImageDrawable(null);
                notify6.setImageDrawable(null);
                edittext1.setText("");
                edittext2.setText("");
                edittext3.setText("");
                edittext5.setText("");
                edittext6.setText("");
                edittext7.setText("");
                if (jb.optBoolean("result")) {
                    Toast.makeText(getActivity(), "上传成功！", Toast.LENGTH_SHORT).show();
                }
                files.clear();
            }
        });
    }

    /**
     * 点击拍照按钮或相册
     *
     * @param p
     */
    @Override
    public void onItem(int p) {
        switch (p) {
            case 1://拍照
                camera();
                photograph.dismiss();
                break;
            case 2://从手机相册选择
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 1);
                photograph.dismiss();
                break;
        }
    }

    /**
     * 调起拍照
     */
    private File currentImageFile;

    public void camera() {
        File dir = new File(Environment.getExternalStorageDirectory(), "myimage");//在sd下创建文件夹myimage；Environment.getExternalStorageDirectory()得到SD卡路径文件
        if (!dir.exists()) {    //exists()判断文件是否存在，不存在则创建文件
            dir.mkdirs();
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式在android中，创建文件时，文件名中不能包含“：”冒号
        String filename = df.format(new Date());
        currentImageFile = new File(dir, filename + ".jpg");
        if (!currentImageFile.exists()) {
            try {
                currentImageFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (android.os.Build.VERSION.SDK_INT < 24) {
            currentImageFile = new File(dir, filename + ".jpg");
            // 从文件中创建uri
            uri = Uri.fromFile(currentImageFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        } else {
            // 兼容Android 7.0 使用共享文件的形式
            ContentValues contentValues = new ContentValues();
            uri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        startActivityForResult(intent, 2);
    }

    Uri uri;
    private File file = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1://文件中选择照片
                if (data == null) {
                    return;
                }
                uri = data.getData();
                file = new File(ComantUtils.getPath(uri, getActivity()));
                Avatar();
                break;
            case 2://拍照完成回调
                if (android.os.Build.VERSION.SDK_INT < 24) {
                    file = currentImageFile;
                } else {
                    file = new File(ComantUtils.getPath(uri, getActivity()));
                }
                Avatar();
                break;
        }
    }

    public void Avatar() {
//新建一个File，传入文件夹目录
        File file1 = new File(Environment.getExternalStorageDirectory(), "mywork");
//判断文件夹是否存在，如果不存在就创建，否则不创建
        if (!file1.exists()) {
            //通过file的mkdirs()方法创建目录中包含却不存在的文件夹
            file1.mkdirs();
        }
        Luban.with(context)
                .load(file)
                .ignoreBy(20)
                .setTargetDir(file1.getPath())
                .filter(new CompressionPredicate() {
                    @Override
                    public boolean apply(String path) {
                        return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                    }
                })
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @SuppressLint("CheckResult")
                    @Override
                    public void onSuccess(final File file) {
                        try {
                            files.add(file);
                            for (int i = 0; i < files.size(); i++) {
                                switch (i) {
                                    case 0:
                                        Glide.with(context).load(files.get(i)).into(notify1);
                                        break;
                                    case 1:
                                        Glide.with(context).load(files.get(i)).into(notify2);
                                        break;
                                    case 2:
                                        Glide.with(context).load(files.get(i)).into(notify4);
                                        break;
                                    case 3:
                                        Glide.with(context).load(files.get(i)).into(notify5);
                                        break;
                                    case 4:
                                        Glide.with(context).load(files.get(i)).into(notify6);
                                        break;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Debug.e("onError--------" + e.getLocalizedMessage());
                        // TODO 当压缩过程出现问题时调用
                    }
                }).launch();
    }


    /**
     * 打开权限
     */
    public void openQuanxian() {
        rxPermissions
                .request(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) {
                        if (aBoolean) {
                            initLocation();//定位
                            updateUI();
                        } else {
                            Toast.makeText(context, "请打开定位权限，否则功能不能使用！", Toast.LENGTH_SHORT).show();
                            openQuanxian();
                        }
                    }
                });
    }
}
