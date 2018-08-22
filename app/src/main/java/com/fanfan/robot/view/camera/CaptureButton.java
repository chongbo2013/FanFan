package com.fanfan.robot.view.camera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2018/3/14/014.
 */

public class CaptureButton extends View {

    public static final int STATE_IDLE = 0x001;        //空闲状态
    public static final int STATE_PRESS = 0x002;       //按下状态
    public static final int STATE_LONG_PRESS = 0x003;  //长按状态
    public static final int STATE_RECORDERING = 0x004; //录制状态
    public static final int STATE_BAN = 0x005;         //禁止状态

    private int progress_color = 0xEE16AE16;            //进度条颜色
    private int outside_color = 0xEEDCDCDC;             //外圆背景色 半透明灰色
    private int inside_color = 0xFFFFFFFF;              //内圆背景色 白色

    private int button_size;                //按钮大小
    private float button_radius;            //按钮半径
    private float button_outside_radius;    //外圆半径
    private float button_inside_radius;     //内圆半径

    private float strokeWidth;              //进度条宽度
    private int inside_reduce_size;         //长按内圆缩小的Size
    private int outside_add_size;           //长按外圆半径变大的Size

    private Paint mPaint;

    //中心坐标
    private float center_X;
    private float center_Y;

    private RectF rectF;

    private float progress;                 //录制视频的进度
    private int recorded_time;              //记录当前录制的时间
    private int max_duration;               //录制视频最大时间长度
    private int min_duration;               //最短录制时间限制

    private int state;              //当前按钮状态
    private int button_state;       //按钮可执行的功能状态（拍照,录制,两者）

    private LongPressRunnable longPressRunnable;    //长按后处理的逻辑Runnable
    private RecordCountDownTimer timer;             //计时器

    private CaptureListener mCaptureLisenter;        //按钮回调接口

    private float event_Y;  //Touch_Event_Down时候记录的Y值

    public CaptureButton(Context context) {
        super(context);
    }

    public CaptureButton(Context context, int size) {
        super(context);
        button_size = size;
        button_radius = size / 2.0f;
        button_outside_radius = button_radius;
        button_inside_radius = button_radius * 0.75f;

        strokeWidth = size / 15;
        outside_add_size = size / 15;
        inside_reduce_size = size / 15;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        progress = 0;

        longPressRunnable = new LongPressRunnable();

        state = STATE_IDLE;

        button_state = CameraView.BUTTON_STATE_BOTH;

        max_duration = 10 * 1000;

        center_X = (button_size + outside_add_size * 2) / 2;
        center_Y = (button_size + outside_add_size * 2) / 2;

        rectF = new RectF(
                center_X - (button_radius + outside_add_size - strokeWidth / 2),
                center_Y - (button_radius + outside_add_size - strokeWidth / 2),
                center_X + (button_radius + outside_add_size - strokeWidth / 2),
                center_Y + (button_radius + outside_add_size - strokeWidth / 2));

        timer = new RecordCountDownTimer(max_duration, max_duration / 360);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(button_size + outside_add_size * 2, button_size + outside_add_size * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setStyle(Paint.Style.FILL);

        mPaint.setColor(outside_color);
        canvas.drawCircle(center_X, center_Y, button_outside_radius, mPaint);

        mPaint.setColor(inside_color);
        canvas.drawCircle(center_X, center_Y, button_inside_radius, mPaint);

        if (state == STATE_RECORDERING) {
            mPaint.setColor(progress_color);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(strokeWidth);
            canvas.drawArc(rectF, -90, progress, false, mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getPointerCount() > 1 || state != STATE_IDLE)
                    break;
                event_Y = event.getY();
                state = STATE_PRESS;
                //判断按钮状态是否为可录制状态
                if ((button_state == CameraView.BUTTON_STATE_ONLY_RECORDER || button_state == CameraView.BUTTON_STATE_BOTH)) {
                    postDelayed(longPressRunnable, 500);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mCaptureLisenter != null && state == STATE_RECORDERING &&
                        (button_state == CameraView.BUTTON_STATE_ONLY_RECORDER || button_state == CameraView.BUTTON_STATE_BOTH)) {
                    //记录当前Y值与按下时候Y值的差值，调用缩放回调接口
                    mCaptureLisenter.recordZoom(event_Y = event.getY());
                }
                break;
            case MotionEvent.ACTION_UP:
                //根据当前按钮的状态进行相应的处理
                removeCallbacks(longPressRunnable);
                switch (state) {
                    //当前是按下
                    case STATE_PRESS:
                        if (mCaptureLisenter != null &&
                                (button_state == CameraView.BUTTON_STATE_ONLY_CAPTURE || button_state == CameraView.BUTTON_STATE_BOTH)) {
                            ValueAnimator inside_anim = ValueAnimator.ofFloat(button_inside_radius, button_inside_radius * 0.75f, button_inside_radius);
                            inside_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    button_inside_radius = (float) animation.getAnimatedValue();
                                    invalidate();
                                }
                            });
                            inside_anim.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    mCaptureLisenter.takePictures();
                                    state = STATE_BAN;
                                }
                            });
                            inside_anim.setDuration(100);
                            inside_anim.start();
                        } else {
                            state = STATE_IDLE;
                        }
                        break;
                    //当前是长按状态
                    case STATE_RECORDERING:
                        timer.cancel();
                        recordEnd();
                        break;
                }
                break;
        }
        return true;
    }


    /**
     * 内外圆动画
     *
     * @param outside_start
     * @param outside_end
     * @param inside_start
     * @param inside_end
     */
    private void startRecordAnimation(float outside_start, float outside_end, float inside_start, float inside_end) {
        //首先创建ValueAnimator，一般是通过调用ValueAnimator的ofXXX方法进行的
        ValueAnimator outside_anim = ValueAnimator.ofFloat(outside_start, outside_end);
        ValueAnimator inside_anim = ValueAnimator.ofFloat(inside_start, inside_end);
        //监听增加值的变化
        outside_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                button_inside_radius = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        inside_anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                button_inside_radius = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        //AnimatorSet就是将多个属性动画集合在一起使用
        AnimatorSet set = new AnimatorSet();

        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (state == STATE_LONG_PRESS) {
                    if (mCaptureLisenter != null)
                        mCaptureLisenter.recordStart();

                    state = STATE_RECORDERING;
                    timer.start();
                }
            }
        });
        set.playTogether(outside_anim, inside_anim);
        set.setDuration(100);
        set.start();
    }

    /**
     * 更新进度
     *
     * @param millisUntilFinished
     */
    private void updateProgress(long millisUntilFinished) {
        recorded_time = (int) (max_duration - millisUntilFinished);
        progress = 360f - millisUntilFinished / (float) max_duration * 360f;
        invalidate();
    }

    /**
     * 录制完成
     */
    private void recordEnd() {
        if (mCaptureLisenter != null) {
            if (recorded_time < min_duration) {
                mCaptureLisenter.recordShort(recorded_time);
            } else {
                mCaptureLisenter.recordEnd(recorded_time);
            }
        }
        resetRecordAnim();
    }

    /**
     * 重置按钮状态
     */
    private void resetRecordAnim() {
        state = STATE_BAN;
        progress = 0;
        invalidate();
        startRecordAnimation(button_outside_radius, button_radius, button_inside_radius, button_radius * 0.75f);
    }


    private class LongPressRunnable implements Runnable {

        @Override
        public void run() {
            state = STATE_LONG_PRESS;

            startRecordAnimation(button_outside_radius, button_outside_radius + outside_add_size,
                    button_inside_radius, button_inside_radius - inside_reduce_size);
        }
    }


    private class RecordCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public RecordCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            updateProgress(millisUntilFinished);
        }

        @Override
        public void onFinish() {
            updateProgress(0);
            recordEnd();
        }
    }

    public interface CaptureListener {

        void takePictures();

        void recordShort(long time);

        void recordStart();

        void recordEnd(long time);

        void recordZoom(float zoom);

        void recordError();

    }

    //**********************************************************************************************

    public void setMax_duration(int duration) {
        max_duration = duration;
        timer = new RecordCountDownTimer(duration, duration / 360);
    }

    public void setMin_duration(int duration) {
        this.min_duration = duration;
    }

    public void setCaptureLisenter(CaptureListener lisenter) {
        mCaptureLisenter = lisenter;
    }

    public void setButtonFeatures(int state) {
        button_state = state;
    }

    public boolean isIdle() {
        return state == STATE_IDLE ? true : false;
    }

    public void resetState() {
        state = STATE_IDLE;
    }
}
