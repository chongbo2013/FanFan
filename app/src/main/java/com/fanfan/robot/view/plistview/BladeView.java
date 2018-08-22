package com.fanfan.robot.view.plistview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/3/19/019.
 */

public class BladeView extends View {

    public static String[] b = {"#", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
            "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    private int choose;
    private boolean showBkg;

    private Paint paint;

    private int mWidth;
    private int mHeight;
    private int mSingleHeight;

    private OnBladeClickListener mOnItemClickListener;

    private PopupWindow mPopupWindow;
    private TextView mPopupText;
    private Handler mHandler;

    public BladeView(Context context) {
        super(context);
        init();
    }

    public BladeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BladeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        choose = -1;
        showBkg = false;
        paint = new Paint();
        mHandler = new Handler();

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        mSingleHeight = mHeight / b.length;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showBkg) {
            canvas.drawColor(Color.parseColor("#00000000"));
        }

        for (int i = 0; i < b.length; i++) {
            paint.setColor(Color.parseColor("#ff2f2f2f"));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setFakeBoldText(true);
            paint.setTextSize(30);
            paint.setAntiAlias(true);
            paint.setTextAlign(Paint.Align.CENTER);
            if (i == choose) {
                paint.setColor(Color.parseColor("#3399ff"));
            }
            float xPos = mWidth / 2 - paint.measureText(b[i]) / 2;
            float yPos = mSingleHeight * i + mSingleHeight / 2;
            canvas.drawText(b[i], xPos, yPos, paint);
            paint.reset();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getY();
        final int oldChoose = choose;
        final int c = (int) (y / getHeight() * b.length);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                showBkg = true;
                if (oldChoose != c) {
                    if (c > 0 && c < b.length) {
                        performItemClicked(c);
                        choose = c;
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (oldChoose != c) {
                    if (c > 0 && c < b.length) {
                        performItemClicked(c);
                        choose = c;
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                showBkg = false;
                choose = -1;
                dismissPopup();
                invalidate();
                break;
        }
        return true;
    }

    private void performItemClicked(int item) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onBladeClick(b[item]);
            showPopup(item);
        }
    }

    private void showPopup(int item) {
        if (mPopupWindow == null) {
            mHandler.removeCallbacks(dismissRunnable);
            mPopupText = new TextView(getContext());
            mPopupText.setBackgroundColor(Color.GRAY);
            mPopupText.setTextColor(Color.CYAN);
            mPopupText.setTextSize(50);
            mPopupText.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);

            mPopupWindow = new PopupWindow(mPopupText, 300, 300);
        }

        String text = "";
        if (item == 0) {
            text = "#";
        } else {
            text = Character.toString((char) ('A' + item - 1));
        }
        mPopupText.setText(text);
        if (mPopupWindow.isShowing()) {
            mPopupWindow.update();
        } else {
            mPopupWindow.showAtLocation(getRootView(),
                    Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
        }
    }

    private void dismissPopup() {
        mHandler.postDelayed(dismissRunnable, 800);
    }

    Runnable dismissRunnable = new Runnable() {

        @Override
        public void run() {
            if (mPopupWindow != null) {
                mPopupWindow.dismiss();
            }
        }
    };

    public interface OnBladeClickListener {
        void onBladeClick(String s);
    }

    public void setOnBladeClickListener(OnBladeClickListener listener) {
        mOnItemClickListener = listener;
    }
}
