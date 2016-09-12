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


/**
 * Created With Android Studio
 * User hangox
 * Date 06/09/2016
 * Time 9:44 AM
 * 一个根据给定形状裁剪的ImageView
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

    /**
     * 多边形图像
     */
    private Drawable mClipModelDrawable;



    /**
     * 最终的图片
     */
    private Bitmap mFinalBitmap;

    /**
     * 最终图片的Canvas
     */
    private Canvas mFinalCanvas;

    /**
     * 显示图片用的矩阵
     */
    private Matrix mFinalMatrix = new Matrix();


    /**
     * 最终图片的画笔
     */
    private Paint mFinalPaint;

    /**
     * 可绘画区域
     * 留条后路,看看以后有没有必要支持padding
     */
    private Rect mCanDrawRect = new Rect();

    /**
     * 图像的边界
     */
    private Rect mImageRect = new Rect();

    /**
     * 裁剪图
     */
    private Bitmap mClipModelBitmap;

    /**
     * 裁剪形状图片的放大倍数
     */
    private float mClipSmallScale;

    /**
     * 裁剪形状大图片的放大倍数
     */
    private float mClipBigScale;

    /**
     * view大小
     */
    private int mViewSize = Integer.MIN_VALUE;
    /**
     * 裁剪图的Canvas
     */
    private Canvas mClipCanvas;


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
        mFinalPaint.setAntiAlias(true);//抗锯齿

    }


    /**
     * 是否裁剪图片
     *
     * @return
     */
    private boolean isClipPicture() {
        return mClipModelDrawable != null;
    }


    /**
     * 是否画边界
     *
     * @return
     */
    private boolean isDrawBorder() {
        //边界不为0,颜色不为透明,边界图不为空的时候绘画边界
        return isClipPicture()
                && mBorderColor != Color.TRANSPARENT
                && mBorderSize != 0;
    }


    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        boolean isSizeChanged = width != oldw || height != oldh;
        boolean isValid = width > 0 && height > 0;

        if (isValid && isClipPicture()) { //必须有效的数据
            if(isSizeChanged) {
                createClipBitmap(width, height);
                createFinalBitmap(width, height);
            }

            //设定边界
            mCanDrawRect.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
            updateImageRect();
            //算边框的放大缩小
            if(mClipModelBitmap != null){
                updateImageScaleSize();
                mClipBigScale = mCanDrawRect.width() * 1F / mClipModelBitmap.getWidth();
            }
            calculateDrawableData();

        }
    }

    private void updateImageRect() {
        //设定图片绘制的边界
        mImageRect.set(mCanDrawRect);
        mImageRect.inset(mBorderSize, mBorderSize);//偏移边距
    }

    private void updateImageScaleSize(){
        mClipSmallScale = mImageRect.width() * 1F / mClipModelBitmap.getWidth();
    }

    /**
     * 计算图片的数据
     */
    private void calculateDrawableData() {
        if (getDrawable() == null) return;
        //计算图片的放大比率
        Drawable d = getDrawable();
        float imageSize = mViewSize - 2F * mBorderSize;
        float size = Math.min(d.getIntrinsicWidth(), d.getIntrinsicHeight());
        float scale = imageSize / size;
        float translateX = mBorderSize;
        float translateY = mBorderSize;
        if (d.getIntrinsicHeight() > d.getIntrinsicWidth()) {
            translateY += (imageSize - scale * d.getIntrinsicHeight()) / 2F;
        } else {
            translateX += (imageSize - scale * d.getIntrinsicWidth()) / 2F;
        }
        mFinalMatrix.setScale(scale, scale);
        mFinalMatrix.postTranslate(translateX, translateY);
    }

    /**
     * 创建最终绘制上去的图片
     * @param width
     * @param height
     */
    private void createFinalBitmap(int width, int height) {
        if (mFinalBitmap != null && !mFinalBitmap.isRecycled()) mFinalBitmap.recycle();
        //创建图片画板
        mFinalBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mFinalCanvas = new Canvas(mFinalBitmap);
    }

    /**
     * 创建裁剪用的图片
     * @param width
     * @param height
     */
    private void createClipBitmap(int width, int height) {
        int cWidth, cHeight;
        cWidth = width;
        cHeight = height;
        int size = cWidth > cHeight ? cWidth : cHeight;
        if(mClipModelBitmap != null && !mClipModelBitmap.isRecycled()) mClipModelBitmap.recycle();
        mClipModelBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        //记录下原来的大小,待会恢复回去
        Rect originBounds = new Rect(mClipModelDrawable.getBounds());
        mClipModelDrawable.setBounds(0, 0,cWidth, cHeight);
        //注意这里不会对clip 图片进行重新定位,请传入方形的图片
        mClipCanvas = new Canvas(mClipModelBitmap);
        mClipModelDrawable.draw(mClipCanvas);
        //恢复
        mClipModelDrawable.setBounds(originBounds);
        setClipImageTint(mBorderColor);
    }


    private void setClipImageTint(int borderColor) {
        //把图片变为边界颜色
        mClipCanvas.drawColor(borderColor, PorterDuff.Mode.SRC_IN);
    }



    @Override
    protected void onDraw(Canvas oCanvas) {
        if (isClipPicture()) {
            if (getDrawable() != null) {
                Canvas canvas = mFinalCanvas;
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                //画图像
                canvas.save();
                Drawable d = getDrawable();
                canvas.clipRect(mImageRect);//裁剪为正方形
                canvas.concat(mFinalMatrix);
                d.draw(canvas);
                canvas.restore();
                //扣形状
                canvas.save();
                canvas.translate(mBorderSize, mBorderSize);
                canvas.scale(mClipSmallScale, mClipSmallScale);
                mFinalPaint.setXfermode(mDstInXfermode);
                canvas.drawBitmap(mClipModelBitmap, 0, 0, mFinalPaint);
                canvas.restore();
                if (isDrawBorder()) {
                    //画边界
                    canvas.save();
                    mFinalPaint.setXfermode(mDstOverInXfermode);
                    canvas.scale(mClipBigScale, mClipBigScale);
                    canvas.drawBitmap(mClipModelBitmap, 0, 0, mFinalPaint);
                    canvas.restore();
                }
                mFinalPaint.setXfermode(null);
                //绘制背景
                if (getBackground() != null) {
                    getBackground().draw(oCanvas);
                }
                oCanvas.drawBitmap(mFinalBitmap, 0, 0, mFinalPaint);
            }
        }else{
            super.onDraw(oCanvas);
        }

    }


    /**
     * 直接改变View的大小
     * @param size
     */
    public void setSize(int size){
        mViewSize = size;
    }


    /**
     * 设置裁剪用的ImageDrawable
     *
     * @param drawable
     */
    public void setClipImageDrawable(Drawable drawable) {
        mClipModelDrawable = drawable;
        createClipBitmap(mViewSize, mViewSize);
        postInvalidate();
    }

    /**
     * 设置边界颜色
     *
     * @param color
     */
    public void setBorderColor(int color) {
        if (mBorderColor != color) {
            mBorderColor = color;
            setClipImageTint(color);
            postInvalidate();
        }
    }


    /**
     * 设置边界高度
     *
     * @param size
     */
    public void setBorderSize(int size) {
        if (size != mBorderSize) {
            mBorderSize = size;
            updateImageRect();
            calculateDrawableData();
            if(mClipModelBitmap != null){
                updateImageScaleSize();
            }
            postInvalidate();
        }
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public int getBorderSize() {
        return mBorderSize;
    }

    /**
     * 设置边界颜色
     *
     * @param resId
     */
    public void setBorderColorRes(int resId) {
        setBorderColor(getResources().getColor(resId));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if(mViewSize == Integer.MIN_VALUE) {
            mViewSize = Math.min(width, height);
        }
        setMeasuredDimension(mViewSize, mViewSize);

    }
}
