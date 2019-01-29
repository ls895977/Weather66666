package com.naran.ui.view;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by MENK021 on 2017/1/1.
 */
public class TvNaranButton extends android.support.v7.widget.AppCompatTextView{
    private Typeface typeface;
    public TvNaranButton(Context context) {
        super(context);
        init(context);
    }

    public TvNaranButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TvNaranButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    private void init(Context context){
        typeface = Typeface.createFromAsset(context.getAssets(),"fonts/fontUI.ttf");
        this.setTypeface(typeface);
    }
}
