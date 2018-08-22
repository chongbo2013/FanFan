package com.fanfan.novel.utils.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.fanfan.robot.app.common.glide.GlideRoundTransform;

/**
 * Created by android on 2018/2/6.
 */

public class ImageLoader {

    public static void loadLargeImage(final Context context, final ImageView imageView, @Nullable final Object model, final int error) {
        final RequestOptions requestOptions = new RequestOptions()
                .placeholder(error)
                .error(error)
                .dontAnimate()
                .centerCrop()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        if (model != null) {
            Glide.with(context)
                    .asBitmap()
                    .load(model)
                    .apply(requestOptions)
                    .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            int imageHeight = resource.getHeight();
                            if (imageHeight > 4096) {
                                imageHeight = 4096;
                                ViewGroup.LayoutParams para = imageView.getLayoutParams();
                                para.width = ViewGroup.LayoutParams.MATCH_PARENT;
                                para.height = imageHeight;
                                imageView.setLayoutParams(para);

                                Glide.with(context)
                                        .load(model)
                                        .apply(requestOptions)
                                        .into(imageView);
                            } else {
                                ViewGroup.LayoutParams para = imageView.getLayoutParams();
                                para.width = -1;
                                para.height = -1;
                                //      loadImage(context, imageView, model, error);
                                RequestOptions options = new RequestOptions()
                                        .centerCrop()
                                        .placeholder(error)
                                        .error(error)
                                        // .priority(priority)
                                        .skipMemoryCache(true)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .transform(new GlideRoundTransform());

                                Glide.with(context)
                                        .load(model)
                                        .apply(options)
                                        .into(imageView);
                            }
                        }
                    });
        } else {
            Glide.with(context)
                    .load(model)
                    .apply(requestOptions)
                    .into(imageView);
        }
    }

    public static void loadImageAsGif(Context context, ImageView imageView, @Nullable Object model) {
        Glide.with(context)
                .asGif()
                .load(model)
                .apply(new RequestOptions().skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE))
                .into(imageView);
    }

    public static void loadImage(Context context, ImageView imageView, @Nullable Object model) {
        loadImage(context, imageView, model, -1);
    }

    /**
     * 列表展示
     *
     * @param fragment
     * @param imageView
     * @param model
     */
    public static void loadImage(Fragment fragment, ImageView imageView, @Nullable Object model, int error) {
        loadImage(fragment.getContext(), imageView, model, error);
    }

    /**
     * 列表展示
     *
     * @param context
     * @param imageView
     * @param model
     */
    public static void loadImage(Context context, ImageView imageView, @Nullable Object model, int error) {
        loadImage(context, imageView, model, error, error, false, DiskCacheStrategy.RESOURCE);
    }

    public static void loadImage(Context context, ImageView imageView, @Nullable Object model,
                                 int placeholder, int error, boolean skip, @NonNull DiskCacheStrategy strategy) {
        loadImage(context, imageView, model, placeholder, error, Priority.NORMAL, skip, strategy, new GlideRoundTransform());
    }

    public static void loadImage(Context context, final ImageView imageView, @Nullable Object model, @NonNull RequestOptions requestOptions) {
        requestOptions
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(context)
                .asBitmap()
                .load(model)
                .apply(requestOptions)
                .into(imageView);
    }

    /**
     * 自定义宽高
     *
     * @param context         上下文
     * @param imageView       图片展示控件
     * @param model           加载资源
     * @param width
     * @param height
     * @param requestListener 监听ImageView的尺寸
     *                        <p>
     *                        如果width和height都大于0，则使用layout中的尺寸。
     *                        如果width和height都是WRAP_CONTENT，则使用屏幕尺寸。
     *                        如果width和height中至少有一个值<=0并且不是WRAP_CONTENT，那么就会在布局的时候添加一个OnPreDrawListener监听ImageView的尺寸
     */
    public static void loadImage(Context context, ImageView imageView, @Nullable Object model,
                                 int width, int height, @Nullable RequestListener requestListener) {
        RequestOptions requestOptions = new RequestOptions()
                .override(width, height)
                .fitCenter()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(context)
                .asBitmap()
                .load(model)
                .apply(requestOptions)
                .listener(requestListener)
                .into(imageView);
    }

    /**
     * @param context     上下文
     * @param imageView   图片展示控件
     * @param model       加载资源
     * @param skip        是否跳过内存，true跳过
     * @param placeholder 占位符，请求图片加载中
     * @param duration    动画时间
     */
    public static void loadImage(Context context, final ImageView imageView, @Nullable Object model,
                                 boolean skip, int placeholder, int duration) {
        Glide.with(context)
                .load(model)
                .apply(new RequestOptions().skipMemoryCache(skip).placeholder(placeholder))
                .transition(new DrawableTransitionOptions().crossFade(duration))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        imageView.setImageDrawable(resource);
                    }
                });
    }

    /**
     * @param context   上下文
     * @param imageView 图片展示控件
     * @param model     加载资源
     * @param skip      是否跳过内存，true跳过
     * @param duration  动画时间
     */
    public static void loadImage(Context context, ImageView imageView, @Nullable Object model,
                                 boolean skip, int duration) {
        Glide.with(context)
                .load(model)
                .apply(new RequestOptions().skipMemoryCache(skip).diskCacheStrategy(DiskCacheStrategy.NONE))
                .transition(new DrawableTransitionOptions().crossFade(duration))
                .into(imageView);
    }

    /**
     * @param context        上下文
     * @param imageView      图片展示控件
     * @param model          加载资源
     * @param placeholder    请求图片加载中
     * @param error          请求图片加载错误
     * @param priority       优先级，设置图片加载的顺序
     * @param skip           是否跳过内存，true跳过
     * @param strategy       磁盘缓存策略
     * @param transformation 图片变换
     *                       <p>
     *                       DiskCacheStrategy.ALL           使用DATA和RESOURCE缓存远程数据，仅使用RESOURCE来缓存本地数据。
     *                       DiskCacheStrategy.NONE          不使用磁盘缓存
     *                       DiskCacheStrategy.DATA          在资源解码前就将原始数据写入磁盘缓存
     *                       DiskCacheStrategy.RESOURCE      在资源解码后将数据写入磁盘缓存，即经过缩放等转换后的图片资源。
     *                       DiskCacheStrategy.AUTOMATIC     根据原始图片数据和资源编码策略来自动选择磁盘缓存策略。
     *                       <p>
     *                       Priority.LOW
     *                       Priority.NORMAL
     *                       Priority.HIGH
     *                       Priority.IMMEDIATE
     */
    public static void loadImage(Context context, ImageView imageView, @Nullable Object model,
                                 int placeholder, int error, @NonNull Priority priority, boolean skip,
                                 @NonNull DiskCacheStrategy strategy,
                                 @NonNull Transformation<Bitmap> transformation) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(placeholder)
                .error(error)
                .priority(priority)
                .skipMemoryCache(skip)
                .diskCacheStrategy(strategy)
                .transform(transformation);

        Glide.with(context)
                .load(model)
                .apply(options)
                .into(imageView);

    }

    /**
     * 清理内存中的缓存。
     *
     * @param context
     */
    public static void clearMemory(Context context) {
        Glide.get(context).clearMemory();
    }

    /**
     * 清理硬盘中的缓存
     *
     * @param context
     */
    public static void clearDiskCache(Context context) {
        Glide.get(context).clearDiskCache();
    }
}
