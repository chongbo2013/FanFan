package com.fanfan.robot.view.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.seabreeze.log.Print;

import org.opencv.core.Rect;

import static com.fanfan.robot.app.common.Constants.unusual;

/**
 * Created by android on 2017/12/21.
 */

public class DetectOpenFaceView extends View {

    private Paint mPaint;

    private Rect[] mFaces;
    private int mDisplayOrientation;

    private Matrix matrix = new Matrix();
    private RectF mRectF = new RectF();

    private float wRatio;
    private float hRatio;

    public void setFaces(Rect[] faces, int displayOrientation) {
        this.mFaces = faces;
        this.mDisplayOrientation = displayOrientation;
        invalidate();
    }

    public DetectOpenFaceView(Context context) {
        super(context);
        init(context);
    }

    public DetectOpenFaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DetectOpenFaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStrokeWidth(5f);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mFaces == null || mFaces.length < 0) {
            return;
        }

        hRatio = (float) (getHeight() / 640.0);
        wRatio = (float) (getWidth() / 480.0);
        canvas.save();

        for (int i = 0; i < mFaces.length; i++) {
            Rect face = mFaces[i];

            float x;
            float y;
            float width;
            float height;
            if (unusual) {
                x = getWidth() - face.x * wRatio;
            } else {
                x = face.x * wRatio;
            }
            y = face.y * hRatio;
            width = face.width * wRatio;
            height = face.height * hRatio;

            if (unusual) {
                mRectF.set(x - width, y, x, y + height);
            }else{
                mRectF.set(x + width, y, x, y + height);
            }
            matrix.mapRect(mRectF);//Matrix 的值映射到RecF
            Print.e("mRectF : (" + mRectF.left + ", " + mRectF.top + "), (" + mRectF.right + ", " + mRectF.bottom + ")" + ", mDisplayOrientation : " + mDisplayOrientation);

            canvas.drawRect(mRectF, mPaint);
        }
        canvas.restore();
    }

    public void clear() {
        mFaces = null;
        invalidate();
    }

}
