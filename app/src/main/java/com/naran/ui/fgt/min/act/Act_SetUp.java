package com.naran.ui.fgt.min.act;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.lykj.aextreme.afinal.utils.ACache;
import com.lykj.aextreme.afinal.utils.Debug;
import com.lykj.aextreme.afinal.utils.MyToast;
import com.makeramen.roundedimageview.RoundedImageView;
import com.naran.ui.Act_Main;
import com.naran.ui.base.BaseAct;
import com.naran.ui.base.MessageEvent;
import com.naran.ui.common.ComantUtils;
import com.naran.ui.connector.OkHttpUtil;
import com.naran.ui.fgt.min.dialog.Dlg_Photograph;
import com.naran.ui.permission.RxPermissions;
import com.naran.ui.utils.LoginUtil;
import com.naran.ui.utils.StringUtil;
import com.naran.weather.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.functions.Consumer;
import okhttp3.Request;
import okhttp3.RequestBody;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * 设置界面
 */
public class Act_SetUp extends BaseAct implements Dlg_Photograph.OnClick {

    private TextView tv_setup, name, phone;
    private LinearLayout myLiner;
    private RoundedImageView hader;
    private Dlg_Photograph photograph;
    private RxPermissions rxPermissions;
    private ACache aCache;

    @Override
    public int initLayoutId() {
        return R.layout.act_setup;
    }

    @Override
    public void initView() {
        hideHeader();
        if (!EventBus.getDefault().isRegistered(this)) {
            //首先要在你要接受EventBus的界面注册，这一步很重要
            EventBus.getDefault().register(this);
        }
        getViewAndClick(R.id.button_mine_back);
        tv_setup = getView(R.id.tv_setup);
        myLiner = getView(R.id.setup_linear);
        getViewAndClick(R.id.setup_exit);
        photograph = new Dlg_Photograph(this, Act_SetUp.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void initData() {
        aCache = ACache.get(this);
        rxPermissions = new RxPermissions(this);
        updateUI();
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

    @Override
    public void updateUI() {
        myLiner.removeAllViews();
        if (ComantUtils.MyLanguage) {//蒙
            tv_setup.setVisibility(View.GONE);
            View mengLinear = LayoutInflater.from(context).inflate(R.layout.view_setupmeng, null);
            getViewAndClick(mengLinear, R.id.setupmeng_phonto);
            getViewAndClick(mengLinear, R.id.setupmeng_Languagesettings);
            getViewAndClick(mengLinear, R.id.setupmeng_favorite);
            hader = getView(mengLinear, R.id.imageView_head);
            name = getView(mengLinear, R.id.setup_name);
            myLiner.addView(mengLinear);
        } else {//汉
            tv_setup.setVisibility(View.VISIBLE);
            View cnLinear = LayoutInflater.from(context).inflate(R.layout.view_cn_setup, null);
            getViewAndClick(cnLinear, R.id.setupmeng_phonto1);
            getViewAndClick(cnLinear, R.id.setupmeng_Languagesettings1);
            getViewAndClick(cnLinear, R.id.setupmeng_favorite1);
            hader = getView(cnLinear, R.id.imageView_head1);
            name = getView(cnLinear, R.id.setup_name);
            myLiner.addView(cnLinear);
        }
        setHaderImg();
    }

    @Override
    public void onViewClick(View v) {
        switch (v.getId()) {
            case R.id.setup_exit://退出
                LoginUtil.getInstance().isLogined = false;
                aCache.clear();
                startActClear(Act_Main.class);
                MyToast.show(context, "退出成功！");
                break;
            case R.id.button_mine_back://返回
                finish();
                break;
            case R.id.setupmeng_phonto://蒙=拍照
            case R.id.setupmeng_phonto1://汉=拍照
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
            case R.id.setupmeng_Languagesettings://蒙=语言设置
            case R.id.setupmeng_Languagesettings1://汉=语言设置
                Intent intent = new Intent();
                intent.putExtra("status", "Set");
                startAct(intent, Act_LanguageSettings.class);
                break;
            case R.id.setupmeng_favorite://蒙=感兴趣领域
            case R.id.setupmeng_favorite1://汉=感兴趣领域
                startAct(Act_Favorite.class);
                break;
        }
    }

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
            uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        startActivityForResult(intent, 2);
    }

    Uri uri;
    private File file = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1://文件中选择照片
                if (data == null) {
                    return;
                }
                uri = data.getData();
                file = new File(ComantUtils.getPath(uri, Act_SetUp.this));
                Avatar();
                break;
            case 2://拍照完成回调
                if (android.os.Build.VERSION.SDK_INT < 24) {
                    file = currentImageFile;
                } else {
                    file = new File(ComantUtils.getPath(uri, Act_SetUp.this));
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
        Luban.with(this)
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
                            postImage(StringUtil.encodeBase64File(file.getPath()));
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

    private void postImage(String base64Img) {
        Map<String, String> map = new HashMap<>();
        map.put("btyestr", base64Img);
        map.put("imgname", "head" + System.currentTimeMillis() + ".png");
        map.put("userid", LoginUtil.getInstance().userInfoModel.getID() + "");
        OkHttpUtil.postAsync(OkHttpUtil.UploadHeadImgs, map, new OkHttpUtil.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                Toast.makeText(context, request.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Toast.makeText(context, "上传成功！", Toast.LENGTH_SHORT).show();
                Log.e("requestSuccess", "result = " + result);
                JSONObject jb = new JSONObject(result);
                LoginUtil.getInstance().userInfoModel.setHeadImg(jb.optString("HeadImg"));
                setHaderImg();

            }
        });
    }

    public void setHaderImg() {
        if (name != null) {
            name.setText(LoginUtil.getInstance().userInfoModel.getUserName());
        }
        if (StringUtil.isEmpty(LoginUtil.getInstance().userInfoModel.getHeadImg())) {
            return;
        }
        hader.setScaleType(ImageView.ScaleType.CENTER_CROP);
        hader.setCornerRadius((float) 10);
        hader.setBorderColor(Color.DKGRAY);
        hader.mutateBackground(true);
        hader.setOval(true);
        hader.setTileModeX(Shader.TileMode.REPEAT);
        hader.setTileModeY(Shader.TileMode.REPEAT);
        Glide.with(context).load(LoginUtil.getInstance().userInfoModel.getHeadImg()).into(hader);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在界面销毁的地方要解绑
        EventBus.getDefault().unregister(this);
    }
}
