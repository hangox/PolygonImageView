package com.hangox.circleimageview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.hangox.polygonimageviewlibrary.PolygonImageView;

/**
 * Created With Android Studio
 * User hangox
 * Date 12/09/2016
 * Time 5:51 PM
 * 一个对PolygonImageView 的圆形的的简单封装
 */

public class CircleImageView extends PolygonImageView {
    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void setUp(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super.setUp(context, attrs, defStyleAttr, defStyleRes);
        ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.setColorFilter(Color.BLACK, PorterDuff.Mode.ADD);
        setClipImageDrawable(shapeDrawable);
    }
}

