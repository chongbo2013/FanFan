package com.fanfan.robot.view.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by android on 2017/12/21.
 */

public class DetectionFaceView extends View {

    private Paint mPaint;
    private Matrix matrix = new Matrix();
    private RectF mRectF = new RectF();

    private Camera.Face[] mFaces;
    private int mDisplayOrientation;

    public void setFaces(Camera.Face[] faces, int displayOrientation) {
        this.mFaces = faces;
        this.mDisplayOrientation = displayOrientation;
        invalidate();
    }

    public DetectionFaceView(Context context) {
        super(context);
        init(context);
    }

    public DetectionFaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DetectionFaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setStrokeWidth(5f);
        mPaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mFaces == null || mFaces.length < 0) {
            return;
        }

        matrix.setScale(-1, 1);//缩放
        matrix.postRotate(mDisplayOrientation);//旋转

        matrix.postScale(getWidth() / 2000f, getHeight() / 2000f);//缩放
        matrix.postTranslate(getWidth() / 2f, getHeight() / 2f);//平移

        canvas.save();
//        matrix.postRotate(0);
//        canvas.rotate(-0);//平移旋转坐标系至任意原点任意角度
        for (int i = 0; i < mFaces.length; i++) {
            mRectF.set(mFaces[i].rect);
            matrix.mapRect(mRectF);//Matrix 的值映射到RecF
//            Print.e("mRectF : (" + mRectF.left + ", " + mRectF.top + "), (" + mRectF.right + ", " + mRectF.bottom + ")" + ", mDisplayOrientation : " + mDisplayOrientation);
            canvas.drawRect(mRectF, mPaint);
        }
        canvas.restore();
    }

    public void clear() {
        mFaces = null;
        invalidate();
    }
}
