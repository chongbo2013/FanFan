package com.fanfan.robot.adapter.recycler.video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanfan.robot.model.VideoBean;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.novel.utils.bitmap.WatermarkUtils;
import com.fanfan.robot.R;

import java.util.List;

/**
 * Created by android on 2018/1/8.
 */

public class VideoVerticalAdapter extends RecyclerView.Adapter<VideoVerticalAdapter.ViewHolder> implements View.OnClickListener {

    private Context mContext;
    private List<VideoBean> mDatas;
    private LayoutInflater inflater;

    private Bitmap watermark;

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public VideoVerticalAdapter(Context context, List<VideoBean> datas) {
        this.mContext = context;
        this.mDatas = datas;
        this.inflater = LayoutInflater.from(mContext);

        watermark = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.watermark_play);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_video_gallery, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        VideoBean bean = mDatas.get(position);
        holder.itemView.setTag(position);
        holder.tvShowtitle.setText(bean.getShowTitle());
        if (bean.getVideoImage() != null) {
            Bitmap src = BitmapFactory.decodeFile(bean.getVideoImage());
            Bitmap bitmap = WatermarkUtils.createWaterMaskCenter(src, watermark);
            holder.ivVideoImage.setImageBitmap(bitmap);
        } else {
            ImageLoader.loadImage(mContext, holder.ivVideoImage, bean.getVideoImage(), R.mipmap.video_image);
        }
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onItemClick(view, (int) view.getTag());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvShowtitle;
        private ImageView ivVideoImage;

        public ViewHolder(View itemView) {
            super(itemView);
            tvShowtitle = itemView.findViewById(R.id.tv_showtitle);
            ivVideoImage = itemView.findViewById(R.id.iv_video_image);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

}
