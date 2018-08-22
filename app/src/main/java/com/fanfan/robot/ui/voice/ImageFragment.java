package com.fanfan.robot.ui.voice;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanfan.robot.app.common.base.BaseDialogFragment;
import com.fanfan.robot.R;
import com.fanfan.robot.model.ImageBean;
import com.fanfan.robot.ui.naviga.NavigationActivity;
import com.fanfan.robot.view.MyScrollView;
import com.fanfan.robot.view.PinchImageView;
import com.fanfan.robot.view.subscaleview.ImageSource;
import com.fanfan.robot.view.subscaleview.SubsamplingScaleImageView;

import butterknife.BindView;

/**
 * Created by android on 2018/3/1.
 */

public class ImageFragment extends BaseDialogFragment {

    @BindView(R.id.tv_titlebar_name)
    TextView mTvTitlebarName;
    @BindView(R.id.rl_top)
    RelativeLayout mRlTop;
    @BindView(R.id.tv_info)
    TextView mTvInfo;
    @BindView(R.id.scrollview)
    MyScrollView mScrollview;
    @BindView(R.id.relativeLayout)
    RelativeLayout mRelativeLayout;
    @BindView(R.id.pinch_image_view)
    PinchImageView mPinchImageView;
    @BindView(R.id.subsampling_scale_imageview)
    SubsamplingScaleImageView mScaleImageView;

    public static final String IMAGE_ID = "image_id";

    public static ImageFragment newInstance(ImageBean imageBean) {
        ImageFragment imageFragment = new ImageFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(IMAGE_ID, imageBean);
        imageFragment.setArguments(bundle);
        return imageFragment;
    }

    private ImageBean mBean;

    private boolean isShow = true;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_image;
    }

    @Override
    protected void initData() {
        mBean = (ImageBean) getArguments().getSerializable(IMAGE_ID);
        if (mBean == null) {
            return;
        }

        mRelativeLayout.getBackground().setAlpha(255);
        mScrollview.getBackground().mutate().setAlpha(100);
        mRlTop.getBackground().mutate().setAlpha(100);

        mTvTitlebarName.setText(mBean.getTop());
        mTvInfo.setText(mBean.getBottom());

        if (mBean.getImgUrl() != null) {
//            ImageLoader.loadLargeImage(mContext, mPinchImageView, mBean.getImgUrl(), R.mipmap.video_image);
            mScaleImageView.setImage(ImageSource.uri(mBean.getImgUrl()));
        }
        mPinchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow) {
                    isShow = false;
                    setView(mRlTop, false);
                    setView(mScrollview, false);
                } else {
                    isShow = true;
                    setView(mRlTop, true);
                    setView(mScrollview, true);
                }
            }
        });
        mScaleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow) {
                    isShow = false;
                    setView(mRlTop, false);
                    setView(mScrollview, false);
                } else {
                    isShow = true;
                    setView(mRlTop, true);
                    setView(mScrollview, true);
                }
            }
        });
    }

    @Override
    protected void setListener(View rootView) {

    }

    @Override
    public void onDestroyView() {
        if (getActivity() instanceof ProblemConsultingActivity) {
            ((ProblemConsultingActivity) getActivity()).isShow(false);
        }
        if (getActivity() instanceof NavigationActivity) {
            ((NavigationActivity) getActivity()).isShow(false);
        }
        super.onDestroyView();
    }

    private void setView(final View view, final boolean isShow) {
        AlphaAnimation alphaAnimation;
        if (isShow) {
            alphaAnimation = new AlphaAnimation(0, 1);
        } else {
            alphaAnimation = new AlphaAnimation(1, 0);
        }
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setDuration(500);
        view.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(isShow ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
