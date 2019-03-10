package com.naran.ui.addresmanager1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.cunoraz.gifview.library.GifView;
import com.lykj.aextreme.afinal.utils.Debug;
import com.naran.ui.addressmanager.TextArticleTitle;
import com.naran.weather.R;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * Created by darhandarhad on 2018/12/23.
 */

public class AddressAdapter1 extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TextArticleTitle> textArticleTitles;
    private Context context;
    private int tag;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public TextView tempercher;
        public TextView content;
        public GifImageView gifView;
        public RelativeLayout itemViewItem;
        public ViewHolder(View v) {
            super(v);
            gifView = (GifImageView) v.findViewById(R.id.gif_gif);
            mTextView = (TextView) v.findViewById(R.id.title);
            tempercher = (TextView) v.findViewById(R.id.tempercher);
            content = (TextView) v.findViewById(R.id.content);
            itemViewItem= (RelativeLayout) v.findViewById(R.id.my_minitem);
        }
    }

    public AddressAdapter1(List<TextArticleTitle> titleAndUrls, Context context, int tag) {
        this.textArticleTitles = titleAndUrls;
        this.context = context;
        this.tag = tag;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_view_address1, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (position != this.getItemCount()) {
            ((ViewHolder) holder).mTextView
                    .setText(textArticleTitles.get(position).getTitle());
            ((ViewHolder) holder).tempercher
                    .setText(textArticleTitles.get(position).getCurrentTemperature() + "℃");
            ((ViewHolder) holder).content
                    .setText(textArticleTitles.get(position).getWeatherPhenomenon());
            ((ViewHolder) holder).mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  Log.e("aa","---------------"+textArticleTitles.get(position).getAreaOn())  ;
                    AddressChangeTask.getInstance().fireMsg(0, textArticleTitles.get(position));
                }
            });
            ((ViewHolder) holder).gifView.setBackgroundResource(getImageResourceId("g" + textArticleTitles.get(position).getWeatherPhenomenonID()));
//            Glide.with(context)
//                    .load(getImageResourceId("g" + textArticleTitles.get(position).getWeatherPhenomenonID())).asGif()
//                    .into(((AddressAdapter1.ViewHolder) holder).gifView);


        }
    }

    @Override
    public int getItemCount() {
        return textArticleTitles.size();
    }

    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        return 0;

    }

    public int getImageResourceId(String name) {
        R.drawable drawables = new R.drawable();
        //默认的id
        int resId = R.drawable.w0;
        try {
            //根据字符串字段名，取字段//根据资源的ID的变量名获得Field的对象,使用反射机制来实现的
            java.lang.reflect.Field field = R.drawable.class.getField(name);
            //取值
            resId = (Integer) field.get(drawables);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resId;
    }
}
