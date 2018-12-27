package com.il360.shenghecar.util;

/**
 * Created by lepc on 2018/8/15.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 高度自适应ImageView，宽度始终充满显示区域，高度成比例缩放
 */
public class AutoHeightImageView extends ImageView {

    public AutoHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        if(drawable != null){
            int width = drawable.getMinimumWidth();
            int height = drawable.getMinimumHeight();
            float scale = (float)height/width;

            int widthMeasure = MeasureSpec.getSize(widthMeasureSpec);
            int heightMeasure = (int)(widthMeasure*scale);

            heightMeasureSpec =  MeasureSpec.makeMeasureSpec(heightMeasure, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}