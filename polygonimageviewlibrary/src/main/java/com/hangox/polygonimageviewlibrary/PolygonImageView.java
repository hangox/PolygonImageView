package com.hangox.polygonimageviewlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.hangox.xlog.XLog;

/**
 * Created With Android Studio
 * User hangox
 * Date 06/09/2016
 * Time 9:44 AM
 */

public class PolygonImageView extends ImageView {

    //使用到的两个蒙版模式
    private Xfermode mDstInXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private Xfermode mDstOverInXfermode  = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);


    /**
     * 边框颜色
     */
    private int mBorderColor;

    /**
     * 边框大小
     */
    private int mBorderSize;

    private float mImageScale;
    /**
     * 多边形图像
     */
    private Drawable mClipModelDrawable;

    /**
     * 是否更新过图像,更新过之后要重新生成图像
     */
    private boolean isInvalidated;

    /**
     * 最终的图片
     */
    private Bitmap mFinalBitmap;

    /**
     * 最终图片的Canvas
     */
    private Canvas mFinalCanvas;

    private Matrix mMatrix = new Matrix();
    private float mScaleSize;

    private Paint mFinalPaint;

    private Rect mCanDrawRect = new Rect();

    private Rect mImageRect = new Rect();

    private Bitmap mClipModelBitmap;


    public PolygonImageView(Context context) {
        super(context);
        setUp(context, null, 0, 0);
    }

    public PolygonImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp(context, attrs, 0, 0);
    }

    public PolygonImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PolygonImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setUp(context, attrs, defStyleAttr, defStyleRes);
    }

    private void setUp(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray t = context.obtainStyledAttributes(attrs, R.styleable.PolygonImageView, defStyleAttr, defStyleRes);
        mBorderSize = t.getDimensionPixelSize(R.styleable.PolygonImageView_piv_borderWidth, 0);
        mClipModelDrawable = t.getDrawable(R.styleable.PolygonImageView_piv_polygonImage);
        mBorderColor = t.getColor(R.styleable.PolygonImageView_piv_borderColor, Color.WHITE);
        t.recycle();

        setScaleType(ScaleType.CENTER_CROP);

        mFinalPaint = new Paint();
        mFinalPaint.setAntiAlias(true);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        createMaskCanvas(w, h, oldw, oldh);
    }

    private void createMaskCanvas(int width, int height, int oldw, int oldh) {
        boolean sizeChanged = width != oldw || height != oldh;
        boolean isValid = width > 0 && height > 0;
        if (isValid && (mFinalCanvas == null || sizeChanged)) {
            //draw clipImage;
            if(mClipModelBitmap != null && !mClipModelBitmap.isRecycled()) mClipModelBitmap.recycle();
            mClipModelBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mClipModelDrawable.setBounds(0, 0, width, height);
            Canvas canvas = new Canvas(mClipModelBitmap);
            mClipModelDrawable.draw(canvas);
            //把图片变为边界颜色
            canvas.drawColor(mBorderColor, PorterDuff.Mode.SRC_IN);
            //创建图片画板
            mFinalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            mFinalCanvas = new Canvas(mFinalBitmap);
        }
        XLog.v("width:" + width + " " + "height:" + height);

    }

    public void invalidate() {
        isInvalidated = true;
        super.invalidate();
    }


    @Override
    protected void onDraw(Canvas oCanvas) {

        if (getDrawable() != null) {
            Canvas canvas = mFinalCanvas;
            //画图像
            canvas.save();
            Drawable d = getDrawable();
            canvas.clipRect(mImageRect);//裁剪为正方形
            canvas.concat(mMatrix);
            d.draw(canvas);
            canvas.restore();


            //画形状
            canvas.save();
            canvas.translate(mBorderSize,mBorderSize);
            float scale = mImageRect.width() * 1F / mClipModelBitmap.getWidth();
            canvas.scale(scale,scale);
            mFinalPaint.setXfermode(mDstInXfermode);
            canvas.drawBitmap(mClipModelBitmap, 0, 0, mFinalPaint);
            canvas.restore();
            //画边界
            canvas.save();
            mFinalPaint.setXfermode(mDstOverInXfermode);
            canvas.drawBitmap(mClipModelBitmap, 0, 0, mFinalPaint);
            canvas.restore();

            mFinalPaint.setXfermode(null);
            oCanvas.drawBitmap(mFinalBitmap, 0, 0, mFinalPaint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int dimen = Math.min(width, height);

        XLog.v(width + ":" + height);
        setMeasuredDimension(dimen, dimen);
        //设定边界
        mCanDrawRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
        mImageRect.set(mCanDrawRect);
        mImageRect.inset(mBorderSize, mBorderSize);
        if (getDrawable() != null) {
            //计算图片的放大比率
            Drawable d = getDrawable();
            float imageSize = dimen - 2F * mBorderSize;
            float size = Math.min(d.getIntrinsicWidth(), d.getIntrinsicHeight());
            float scale = imageSize / size;
            float translateX = mBorderSize;
            float translateY = mBorderSize;
            if (d.getIntrinsicHeight() > d.getIntrinsicWidth()) {
                translateY += (imageSize - scale * d.getIntrinsicHeight()) / 2F;
            } else {
                translateX += (imageSize - scale * d.getIntrinsicWidth()) / 2F;
            }
            mMatrix.setScale(scale, scale);
            mMatrix.postTranslate(translateX, translateY);
        }

        mScaleSize = 1 - 2F * mBorderSize / width;
    }
}
