package com.fanfan.robot.app.common.base;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.fanfan.robot.app.common.glide.GlideRoundTransform;
import com.fanfan.robot.service.PlayService;
import com.fanfan.robot.other.cache.MusicCache;
import com.fanfan.robot.R;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @description: 基础类
 * @author: Andruby
 * @time: 2016/9/3 16:19
 */
public abstract class BaseFragment extends Fragment {

    protected BaseActivity mContext;
    protected Handler mHandler = new Handler();
    protected View rootView;

    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = (BaseActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getLayoutId() != 0) {
            rootView = inflater.inflate(getLayoutId(), container, false);
        } else {
            try {
                throw new Exception("layout is empty");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        unbinder = ButterKnife.bind(this, rootView);
        initView(rootView);
        initData();
        setListener(rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 返回当前界面布局文件
     */
    protected abstract int getLayoutId();

    /**
     * 此方法描述的是： 初始化所有view
     */
    protected void initView(View view) {
        final RelativeLayout fragmentBg = view.findViewById(R.id.fragment_bg);
        if (fragmentBg != null) {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.mipmap.fragment_bg)
                    .error(R.mipmap.fragment_bg)
                    .priority(Priority.HIGH)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .transform(new GlideRoundTransform());
            Glide.with(this)
                    .asBitmap()
                    .load(R.mipmap.fragment_bg)
                    .apply(options)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            Drawable drawable = new BitmapDrawable(resource);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                fragmentBg.setBackground(drawable);
                            }
                        }
                    });
        }
    }

    /**
     * 此方法描述的是： 初始化所有数据的方法
     */
    protected abstract void initData();

    /**
     * 此方法描述的是： 设置所有事件监听
     */
    protected abstract void setListener(View view);

    /**
     * 显示toast
     *
     * @param resId
     */
    public void showToast(final int resId) {
        showToast(getString(resId));
    }


    public <T extends View> T obtainView(int resId) {
        return (T) rootView.findViewById(resId);
    }

    /**
     * 显示toast
     *
     * @param resStr
     * @return Toast对象，便于控制toast的显示与关闭
     */
    public Toast showToast(final String resStr) {

        if (TextUtils.isEmpty(resStr)) {
            return null;
        }

        Toast toast = null;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(mContext, resStr,
                        Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        return toast;
    }

    protected PlayService getPlayService() {
        PlayService playService = MusicCache.get().getPlayService();
        if (playService == null) {
            throw new NullPointerException("play service is null");
        }
        return playService;
    }

}
