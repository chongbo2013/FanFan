package com.fanfan.robot.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.fanfan.robot.app.common.Constants;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.robot.R;
import com.seabreeze.log.Print;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by android on 2018/1/11.
 */

public class RangeClickImageView extends ImageView implements RequestListener<Bitmap> {

    private Context mContext;

    private int ivWidth;
    private int ivHeight;
    private int realWidth;
    private int realHeight;

    private float mLastMotionX, mLastMotionY;
    private static final int TOUCH_SLOP = 20;
    private boolean isMoved;

    private boolean isLongCheck;
    private boolean isShortClick;

    private List<Point> points;//点击范围的左上角距离屏幕左侧的宽度

    private int can = 100;

    private PointF mPointF;
    private PointF mCircle;

    private Paint mPaint;
    private Bitmap finishBitmap;

    private OnRangeClickListener onRangeClickListener;
    private OnRangeLongClickListener onRangeLongClickListener;
    private OnResourceReadyListener onResourceReadyListener;

    private LongPressRunnable longPressRunnable = new LongPressRunnable();

    public RangeClickImageView(Context context) {
        super(context);
        init(context);
    }

    public RangeClickImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RangeClickImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;

        mPointF = new PointF();
        mCircle = new PointF();

        points = new ArrayList<>();

        mPaint = new Paint();
        mPaint.setAntiAlias(false);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.STROKE);

        finishBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.range_finish);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Point point : points) {
            canvas.drawCircle(point.x, point.y, can, mPaint);
            if (point.isFinish) {
                canvas.drawBitmap(finishBitmap, point.x, point.y, mPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP) {
            //点击的时候获取点击的坐标及X、Y值
            float clickX = event.getX();
            float clickY = event.getY();

            mPointF.set(clickX, clickY);

            for (int i = 0; i < points.size(); i++) {
                Point point = points.get(i);
                mCircle.set(point.x, point.y);


                if (isPointInCircle(mPointF, mCircle, can)) {
                    Print.e("点击");
                    if (onRangeClickListener != null) {
                        onRangeClickListener.onClickImage(this, point.tag, point.x, point.y);
                    }
                    break;
                }
            }

        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        float clickX = event.getX();
        float clickY = event.getY();

        mPointF.set(clickX, clickY);

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            mCircle.set(point.x, point.y);


            if (isPointInCircle(mPointF, mCircle, can)) {


                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mLastMotionX = clickX;
                        mLastMotionY = clickY;

                        isShortClick = true;
                        isLongCheck = true;

                        isMoved = false;
                        postCheckForLongTouch(point.tag, (int) point.realX, (int) point.realY);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (isMoved)
                            break;

                        if (Math.abs(mLastMotionX - clickX) > TOUCH_SLOP || Math.abs(mLastMotionY - clickY) > TOUCH_SLOP) {
                            isMoved = true;

                            isShortClick = false;
                            isLongCheck = false;

                            removeLongTouch();
                        }
                        break;
                    case MotionEvent.ACTION_UP:

                        if (isShortClick) {
                            isShortClick = false;
                            if (onRangeClickListener != null) {
                                onRangeClickListener.onClickImage(this, point.tag, (int) point.realX, (int) point.realY);
                            }
                        }

                        removeLongTouch();
                        break;
                }

            } else {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        removeLongTouch();
                        break;
                }
            }
        }


        return true;
    }

    private void postCheckForLongTouch(Object tag, float x, float y) {
        longPressRunnable.setPressLocation(tag, x, y);
        postDelayed(longPressRunnable, 600);
    }

    private void removeLongTouch() {
        removeCallbacks(longPressRunnable);
    }

    private class LongPressRunnable implements Runnable {

        private Object tag;
        private int x, y;

        public void setPressLocation(Object tag, float x, float y) {
            this.tag = tag;
            this.x = (int) x;
            this.y = (int) y;
        }

        @Override
        public void run() {
            isShortClick = false;
            isLongCheck = false;
            if (onRangeLongClickListener != null) {
                onRangeLongClickListener.onLongClickImage(RangeClickImageView.this, tag, x, y);
            }
        }
    }

    private boolean isPointInCircle(PointF pointF, PointF circle, float radius) {
        return Math.pow((pointF.x - circle.x), 2) + Math.pow((pointF.y - circle.y), 2) <= Math.pow(radius, 2);
    }

    //向外暴露的方法设置ImageView的点击范围
    public void setClickRange(Object tag, float realX, float realY) {
        if (realWidth == 0 || realHeight == 0 || ivWidth == 0 || ivHeight == 0) {
            return;
        }
        Point point = new Point();
        point.tag = tag;
        point.x = getPointX(realX, realWidth, ivWidth);
        point.y = getPointY(realY, realHeight, ivHeight);
        point.realX = realX;
        point.realY = realY;
        Print.e(point);
        points.add(point);
    }

    public void setFinish(String tag) {
        for (Point point : points) {
            if (tag.equals(point.tag)) {
                point.isFinish = true;
            }
        }
        postInvalidate();
    }

    public void setDelete(String tag) {
        for (Point point : points) {
            if (tag.equals(point.tag)) {
                point.isFinish = false;
            }
        }
        postInvalidate();
    }

    public void setFileName(String fileName, int reqWidth, int reqHeight) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.image_navigation);
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(mContext.getAssets().open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bitmap == null) {
            return;
        }
        realWidth = bitmap.getWidth();
        realHeight = bitmap.getHeight();
        Print.e("realWidth " + realWidth);
        Print.e("realHeight " + realHeight);

        float inSampleSize = 0;
        if (realHeight > reqHeight || realWidth > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            float heightRatio = (float) realHeight / (float) reqHeight;
            float widthRatio = (float) realWidth / (float) reqWidth;
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;

        }
        Print.e(inSampleSize);

        ImageLoader.loadImage(getContext(), this, Constants.ASSEST_PATH + fileName,
                (int) (realWidth / inSampleSize), (int) (realHeight / inSampleSize), this);

    }

    @Override
    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
        return false;
    }

    @Override
    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
        ivWidth = resource.getWidth();
        ivHeight = resource.getHeight();
        Print.e("ivWidth " + ivWidth);
        Print.e("ivHeight " + ivHeight);

        if (onResourceReadyListener != null) {
            return onResourceReadyListener.onResourceReady(realWidth, realHeight, ivWidth, ivHeight);
        }
        return false;
    }

    public interface OnRangeClickListener {
        void onClickImage(View view, Object tag, int x, int y);
    }

    public interface OnRangeLongClickListener {
        void onLongClickImage(View view, Object tag, int x, int y);
    }

    public interface OnResourceReadyListener {
        boolean onResourceReady(int realWidth, int realHeight, int ivWidth, int ivHeight);
    }

    public void setOnRangeClickListener(OnRangeClickListener listener) {
        this.onRangeClickListener = listener;
    }

    public void setOnRangeLongClickListener(OnRangeLongClickListener listener) {
        this.onRangeLongClickListener = listener;
    }

    public void setOnResourceReadyListener(OnResourceReadyListener listener) {
        this.onResourceReadyListener = listener;
    }

    public class Point {
        Object tag;
        int x;
        int y;
        float realX;
        float realY;
        boolean isFinish;
    }

    public static int getPointX(double x, int realWidth, int ivWidth) {
        return (int) (x / realWidth * ivWidth);
    }

    public static int getPointY(double y, int realHeight, int ivHeight) {
        return (int) (y / realHeight * ivHeight);
    }
}
