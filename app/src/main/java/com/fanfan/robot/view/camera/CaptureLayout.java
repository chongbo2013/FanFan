package com.fanfan.robot.view.camera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/3/14/014.
 */

public class CaptureLayout extends FrameLayout implements CaptureButton.CaptureListener, View.OnClickListener {


    private int layout_width;
    private int layout_height;
    private int button_size;
    private int iconLeft = 0;
    private int iconRight = 0;

    private CaptureButton btnCapture;      //拍照按钮
    private TypeButton btnConfirm;         //确认按钮
    private TypeButton btnCancel;          //取消按钮
    private ReturnButton btnReturn;        //返回按钮
    private TextView txtTip;               //提示文本

    private ImageView ivCustomLeft;            //左边自定义按钮
    private ImageView ivCustomRight;            //右边自定义按钮

    private static final int cancelID = 0x1;
    private static final int confirmID = 0x2;
    private static final int returnID = 0x3;
    private static final int customLeftID = 0X4;
    private static final int customRightID = 0x5;

    private CaptureButton.CaptureListener mCaptureLisenter;    //拍照按钮监听
    private TypeListener mTypeListener;
    private ClickListener leftClickListener;
    private ClickListener rightClickListener;
    private ReturnListener mReturnListener;

    private boolean isFirst = true;

    public CaptureLayout(@NonNull Context context) {
        this(context, null);
    }

    public CaptureLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CaptureLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layout_width = metrics.widthPixels;
        } else {
            layout_width = metrics.widthPixels / 2;
        }
        button_size = (int) (layout_width / 5f);
        layout_height = button_size + (button_size / 5) * 2 + 100;

        setWillNotDraw(false);

        btnCapture = new CaptureButton(getContext(), button_size);
        LayoutParams captureParams = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        captureParams.gravity = Gravity.CENTER;
        btnCapture.setLayoutParams(captureParams);
        btnCapture.setCaptureLisenter(this);

        btnCancel = new TypeButton(getContext(), TypeButton.TYPE_CANCEL, button_size);
        final LayoutParams cancelParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        cancelParam.gravity = Gravity.CENTER_VERTICAL;
        cancelParam.setMargins((layout_width / 4) - button_size / 2, 0, 0, 0);
        btnCancel.setLayoutParams(cancelParam);
        btnCancel.setId(cancelID);
        btnCancel.setOnClickListener(this);

        btnConfirm = new TypeButton(getContext(), TypeButton.TYPE_CONFIRM, button_size);
        LayoutParams confirmParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        confirmParams.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        confirmParams.setMargins(0, 0, (layout_width / 4) - button_size / 2, 0);
        btnConfirm.setLayoutParams(confirmParams);
        btnConfirm.setId(confirmID);
        btnConfirm.setOnClickListener(this);


        btnReturn = new ReturnButton(getContext(), (int) (button_size / 2.5f));
        LayoutParams returnParam = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        returnParam.gravity = Gravity.CENTER_VERTICAL;
        returnParam.setMargins(layout_width / 6, 0, 0, 0);
        btnReturn.setLayoutParams(returnParam);
        btnReturn.setId(returnID);
        btnReturn.setOnClickListener(this);

        ivCustomLeft = new ImageView(getContext());
        LayoutParams customLeftParam = new LayoutParams((int) (button_size / 2.5f), (int) (button_size / 2.5f));
        customLeftParam.gravity = Gravity.CENTER_VERTICAL;
        customLeftParam.setMargins(layout_width / 6, 0, 0, 0);
        ivCustomLeft.setLayoutParams(customLeftParam);
        ivCustomLeft.setId(customLeftID);
        ivCustomLeft.setOnClickListener(this);

        ivCustomRight = new ImageView(getContext());
        LayoutParams customRightParam = new LayoutParams((int) (button_size / 2.5f), (int) (button_size / 2.5f));
        customRightParam.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
        customRightParam.setMargins(0, 0, layout_width / 6, 0);
        ivCustomRight.setLayoutParams(customRightParam);
        ivCustomRight.setId(customRightID);
        ivCustomRight.setOnClickListener(this);

        txtTip = new TextView(getContext());
        LayoutParams txtParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        txtParam.gravity = Gravity.CENTER_HORIZONTAL;
        txtParam.setMargins(0, 0, 0, 0);
        txtTip.setText("轻触拍照，长按摄像");
        txtTip.setTextColor(0xFFFFFFFF);
        txtTip.setGravity(Gravity.CENTER);
        txtTip.setLayoutParams(txtParam);

        addView(btnCapture);
        addView(btnCancel);
        addView(btnConfirm);
        addView(btnReturn);
        addView(ivCustomLeft);
        addView(ivCustomRight);
        addView(txtTip);

        ivCustomRight.setVisibility(GONE);
        btnCancel.setVisibility(GONE);
        btnConfirm.setVisibility(GONE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(layout_width, layout_height);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case cancelID:
                if (mTypeListener != null) {
                    mTypeListener.cancel();
                }
                startAlphaAnimation();
//                resetCaptureLayout();
                break;
            case confirmID:
                if (mTypeListener != null) {
                    mTypeListener.confirm();
                }
                startAlphaAnimation();
//                resetCaptureLayout();
                break;
            case returnID:
                if (leftClickListener != null) {
                    leftClickListener.onClick();
                }
                break;
            case customLeftID:
                if (leftClickListener != null) {
                    leftClickListener.onClick();
                }
                break;
            case customRightID:
                if (rightClickListener != null) {
                    rightClickListener.onClick();
                }
                break;
        }
    }

    @Override
    public void takePictures() {
        if (mCaptureLisenter != null) {
            mCaptureLisenter.takePictures();
        }
    }

    @Override
    public void recordShort(long time) {
        if (mCaptureLisenter != null) {
            mCaptureLisenter.recordShort(time);
        }
        startAlphaAnimation();
    }

    @Override
    public void recordStart() {
        if (mCaptureLisenter != null) {
            mCaptureLisenter.recordStart();
        }
        startAlphaAnimation();
    }

    @Override
    public void recordEnd(long time) {
        if (mCaptureLisenter != null) {
            mCaptureLisenter.recordEnd(time);
        }
        startAlphaAnimation();
        startTypeBtnAnimator();
    }

    @Override
    public void recordZoom(float zoom) {
        if (mCaptureLisenter != null) {
            mCaptureLisenter.recordZoom(zoom);
        }
    }

    @Override
    public void recordError() {
        if (mCaptureLisenter != null) {
            mCaptureLisenter.recordError();
        }
    }


    //**********************************************************************************************
    public interface TypeListener {
        void cancel();

        void confirm();
    }

    public interface ClickListener {
        void onClick();
    }

    public interface ReturnListener {
        void onReturn();
    }

    //**********************************************************************************************

    public void setDuration(int duration) {
        btnCapture.setMax_duration(duration);
    }

    public void setButtonFeatures(int state) {
        btnCapture.setButtonFeatures(state);
    }

    public void setTip(String tip) {
        txtTip.setText(tip);
    }

    public void showTip() {
        txtTip.setVisibility(VISIBLE);
    }

    public void setIconSrc(int iconLeft, int iconRight) {
        this.iconLeft = iconLeft;
        this.iconRight = iconRight;
        if (this.iconLeft != 0) {
            ivCustomLeft.setImageResource(iconLeft);
            ivCustomLeft.setVisibility(VISIBLE);
            btnReturn.setVisibility(GONE);
        } else {
            ivCustomLeft.setVisibility(GONE);
            btnReturn.setVisibility(VISIBLE);
        }
        if (this.iconRight != 0) {
            ivCustomRight.setImageResource(iconRight);
            ivCustomRight.setVisibility(VISIBLE);
        } else {
            ivCustomRight.setVisibility(GONE);
        }
    }

    public void setTypeLisenter(TypeListener lisenter) {
        mTypeListener = lisenter;
    }

    public void setCaptureLisenter(CaptureButton.CaptureListener lisenter) {
        mCaptureLisenter = lisenter;
    }

    public void setReturnLisenter(ReturnListener lisenter) {
        mReturnListener = lisenter;
    }

    public void setLeftClickListener(ClickListener lisenter) {
        leftClickListener = lisenter;
    }

    public void setRightClickListener(ClickListener lisenter) {
        rightClickListener = lisenter;
    }

    public void setTextWithAnimation(String tip) {
        txtTip.setText(tip);
        ObjectAnimator animator_txt_tip = ObjectAnimator.ofFloat(txtTip, "alpha", 0f, 1f, 1f, 0f);
        animator_txt_tip.setDuration(2500);
        animator_txt_tip.start();
    }


    public void resetCaptureLayout() {
        btnCapture.resetState();
        btnCancel.setVisibility(GONE);
        btnConfirm.setVisibility(GONE);
        btnCapture.setVisibility(VISIBLE);
        if (this.iconLeft != 0) {
            ivCustomLeft.setVisibility(VISIBLE);
        } else {
            btnReturn.setVisibility(VISIBLE);
        }
        if (this.iconRight != 0)
            ivCustomRight.setVisibility(VISIBLE);
    }

    public void startAlphaAnimation() {
        if (isFirst) {
            ObjectAnimator txtTipAnimator = ObjectAnimator.ofFloat(txtTip, "alpha", 1f, 0f);
            txtTipAnimator.setDuration(500);
            txtTipAnimator.start();
            isFirst = false;
        }
    }

    /**
     * 拍照录制结果后的动画
     */
    public void startTypeBtnAnimator() {
        if (iconLeft != 0) {
            ivCustomLeft.setVisibility(GONE);
        } else {
            btnReturn.setVisibility(GONE);
        }
        if (iconRight != 0) {
            ivCustomRight.setVisibility(GONE);
        }
        btnCapture.setVisibility(GONE);
        btnCancel.setVisibility(VISIBLE);
        btnConfirm.setVisibility(VISIBLE);
        btnCancel.setClickable(false);
        btnConfirm.setClickable(false);

        ObjectAnimator cancelAnimator = ObjectAnimator.ofFloat(btnCancel, "translationX", layout_width / 4, 0);
        ObjectAnimator confirmAnimator = ObjectAnimator.ofFloat(btnConfirm, "translationX", -layout_width / 4, 0);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(cancelAnimator, confirmAnimator);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                btnCancel.setClickable(true);
                btnConfirm.setClickable(true);
            }
        });
        set.setDuration(200);
        set.start();
    }

}
