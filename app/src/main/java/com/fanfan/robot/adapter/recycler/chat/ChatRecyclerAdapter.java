package com.fanfan.robot.adapter.recycler.chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fanfan.robot.app.common.ChatConst;
import com.fanfan.robot.app.common.glide.CustomShapeTransformation;
import com.fanfan.robot.model.ChatMessageBean;
import com.fanfan.novel.utils.system.FileUtil;
import com.fanfan.novel.utils.bitmap.ImageLoader;
import com.fanfan.robot.R;
import com.fanfan.robot.view.BubbleImageView;
import com.fanfan.robot.view.ChatTextView;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zhangyuanyuan on 2017/9/21.
 */

public class ChatRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {

    public static final int FROM_USER_MSG = 0;//接收文本消息类型
    public static final int TO_USER_MSG = 1;//发送文本消息类型
    public static final int FROM_USER_IMG = 2;
    public static final int TO_USER_IMG = 3;

    private Context context;
    private List<ChatMessageBean> cmbs = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private Handler mHandler;

    private OnChatItemClickListener mOnChatItemClickListener = null;

    private OnClickimageListener mOnClickimageListener;

    private Animation an;
    private AccelerateDecelerateInterpolator interpolator;

    public ChatRecyclerAdapter(Context context, List<ChatMessageBean> cmbs) {
        this.context = context;
        this.cmbs = cmbs;
        mLayoutInflater = LayoutInflater.from(context);
        interpolator = new AccelerateDecelerateInterpolator();
        mHandler = new Handler();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case FROM_USER_MSG:
                view = mLayoutInflater.inflate(R.layout.layout_msgfrom_list_item, parent, false);
                holder = new FromUserMsgViewHolder(view);
                break;
            case TO_USER_MSG:
                view = mLayoutInflater.inflate(R.layout.layout_msgto_list_item, parent, false);
                holder = new ToUserMsgViewHolder(view);
                break;
            case FROM_USER_IMG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_imagefrom_list_item, parent, false);
                holder = new FromUserImageViewHolder(view);
                break;
            case TO_USER_IMG:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_imageto_list_item, parent, false);
                holder = new ToUserImgViewHolder(view);
                break;
        }
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessageBean cmb = cmbs.get(position);
        int itemViewType = getItemViewType(position);
        switch (itemViewType) {
            case FROM_USER_MSG:
                fromMsgUserLayout((FromUserMsgViewHolder) holder, cmb, position);
                break;
            case TO_USER_MSG:
                toMsgUserLayout((ToUserMsgViewHolder) holder, cmb, position);
                break;
            case FROM_USER_IMG:
                fromImgUserLayout((FromUserImageViewHolder) holder, cmb, position);
                break;
            case TO_USER_IMG:
                toImgUserLayout((ToUserImgViewHolder) holder, cmb, position);
                break;
        }
    }


    @Override
    public int getItemCount() {
        return cmbs != null ? cmbs.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        return cmbs.get(position).getMessagetype();
    }


    public void setmOnChatItemClickListener(OnChatItemClickListener onChatItemClickListener) {
        this.mOnChatItemClickListener = onChatItemClickListener;
    }

    public void setmOnClickimageListener(OnClickimageListener onClickimageListener) {
        this.mOnClickimageListener = onClickimageListener;
    }

    private void fromMsgUserLayout(final FromUserMsgViewHolder holder, ChatMessageBean cmb, int position) {
        if (position != 0) {
            String showTime = getTime(cmb.getTime(), cmbs.get(position - 1).getTime());
            if (showTime != null) {
                holder.chatTime.setVisibility(View.VISIBLE);
                holder.chatTime.setText(showTime);
            } else {
                holder.chatTime.setVisibility(View.GONE);
            }
        } else {
            String showTime = getTime(cmb.getTime(), null);
            holder.chatTime.setVisibility(View.VISIBLE);
            holder.chatTime.setText(showTime);
        }
        if (cmb.getUserHeadIcon() != null) {
            ImageLoader.loadImage(context, holder.otherUserIcon, cmb.getUserHeadIcon());
        } else {
            ImageLoader.loadImage(context, holder.otherUserIcon, R.mipmap.ic_head);
        }
        holder.chatContent.setSpanText(mHandler, cmb.getMessageContent(), true);
        holder.itemView.setTag(position);
    }

    private void toMsgUserLayout(ToUserMsgViewHolder holder, ChatMessageBean cmb, int position) {
        if (position != 0) {
            String showTime = getTime(cmb.getTime(), cmbs.get(position - 1).getTime());
            if (showTime != null) {
                holder.toChatTime.setVisibility(View.VISIBLE);
                holder.toChatTime.setText(showTime);
            } else {
                holder.toChatTime.setVisibility(View.GONE);
            }
        } else {
            String showTime = getTime(cmb.getTime(), null);
            holder.toChatTime.setVisibility(View.VISIBLE);
            holder.toChatTime.setText(showTime);
        }
        if (cmb.getUserHeadIcon() != null) {
            ImageLoader.loadImage(context, holder.myUserIcon, cmb.getUserHeadIcon());
        } else {
            ImageLoader.loadImageAsGif(context, holder.myUserIcon, R.mipmap.ic_head_s);
        }
        holder.toChatContent.setSpanText(mHandler, cmb.getMessageContent(), true);
        holder.itemView.setTag(position);
    }

    private void fromImgUserLayout(final FromUserImageViewHolder holder, final ChatMessageBean cmb, final int position) {
        if (position != 0) {
            String showTime = getTime(cmb.getTime(), cmbs.get(position - 1).getTime());
            if (showTime != null) {
                holder.chatTime.setVisibility(View.VISIBLE);
                holder.chatTime.setText(showTime);
            } else {
                holder.chatTime.setVisibility(View.GONE);
            }
        } else {
            String showTime = getTime(cmb.getTime(), null);
            holder.chatTime.setVisibility(View.VISIBLE);
            holder.chatTime.setText(showTime);
        }
        if (cmb.getUserHeadIcon() != null) {
            ImageLoader.loadImage(context, holder.otherUserIcon, cmb.getUserHeadIcon());
        } else {
            ImageLoader.loadImage(context, holder.otherUserIcon, R.mipmap.ic_head);
        }
        final String imageSrc = cmb.getImageLocal() == null ? "" : cmb.getImageLocal();
        final String imageUrlSrc = cmb.getImageUrl() == null ? "" : cmb.getImageUrl();
        File file = new File(imageSrc);
        final boolean hasLocal = !imageSrc.equals("") && FileUtil.isFileExists(file);
        if (hasLocal) {
            ImageLoader.loadImage(context, holder.imageMsg, imageSrc, -1, -1, Priority.NORMAL, true,
                    DiskCacheStrategy.NONE, new CustomShapeTransformation(context, R.drawable.chatfrom_bg_focused));
        } else {
            ImageLoader.loadImage(context, holder.imageMsg, imageUrlSrc, -1, -1, Priority.NORMAL, true,
                    DiskCacheStrategy.NONE, new CustomShapeTransformation(context, R.drawable.chatfrom_bg_focused));
        }
        holder.imageMsg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mOnClickimageListener != null) {
                    if (hasLocal) {
                        mOnClickimageListener.onClickimg(imageSrc);
                    } else {
                        mOnClickimageListener.onClickimg(imageSrc);
                    }
                }
            }

        });

    }

    private void toImgUserLayout(final ToUserImgViewHolder holder, final ChatMessageBean cmb, final int position) {
        switch (cmb.getSendState()) {
            case ChatConst.SENDING:
                an = AnimationUtils.loadAnimation(context, R.anim.update_loading_progressbar_anim);
                LinearInterpolator lin = new LinearInterpolator();
                an.setInterpolator(lin);
                an.setRepeatCount(-1);
                holder.sendFailImg.setBackgroundResource(R.mipmap.xsearch_loading);
                holder.sendFailImg.startAnimation(an);
                an.startNow();
                holder.sendFailImg.setVisibility(View.VISIBLE);
                break;

            case ChatConst.COMPLETED:
                holder.sendFailImg.clearAnimation();
                holder.sendFailImg.setVisibility(View.GONE);
                break;

            case ChatConst.SENDERROR:
                holder.sendFailImg.clearAnimation();
                holder.sendFailImg.setBackgroundResource(R.mipmap.msg_state_fail_resend_pressed);
                holder.sendFailImg.setVisibility(View.VISIBLE);
                holder.sendFailImg.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
//                        if (sendErrorListener != null) {
//                            sendErrorListener.onClick(position);
//                        }
                    }
                });
                break;
            default:
                break;
        }
        if (position != 0) {
            String showTime = getTime(cmb.getTime(), cmbs.get(position - 1).getTime());
            if (showTime != null) {
                holder.toChatTime.setVisibility(View.VISIBLE);
                holder.toChatTime.setText(showTime);
            } else {
                holder.toChatTime.setVisibility(View.GONE);
            }
        } else {
            String showTime = getTime(cmb.getTime(), null);
            holder.toChatTime.setVisibility(View.VISIBLE);
            holder.toChatTime.setText(showTime);
        }
        if (cmb.getUserHeadIcon() != null) {
            ImageLoader.loadImage(context, holder.myUserIcon, cmb.getUserHeadIcon());
        } else {
            ImageLoader.loadImageAsGif(context, holder.myUserIcon, R.mipmap.ic_head_s);
        }
        holder.imageGroup.setVisibility(View.VISIBLE);
        final String imageSrc = cmb.getImageLocal() == null ? "" : cmb.getImageLocal();
        final String imageUrl = cmb.getImageUrl() == null ? "" : cmb.getImageUrl();
        if (imageSrc != null && !imageSrc.equals("")) {
            ImageLoader.loadImage(context, holder.toImageMsg, imageSrc);
        } else if (imageUrl != null) {
            ImageLoader.loadImage(context, holder.toImageMsg, imageUrl);
        }
        holder.toImageMsg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mOnClickimageListener != null) {
                    if (imageSrc != null && !imageSrc.equals("")) {
                        mOnClickimageListener.onClickimg(imageSrc);
                    } else if (imageUrl != null) {
                        mOnClickimageListener.onClickimg(imageUrl);
                    }
                }
            }

        });
    }

    public ChatMessageBean getItem(int position) {
        if (position >= cmbs.size())
            return null;
        return cmbs.get(position);
    }


    public List<ChatMessageBean> getDatas() {
        return cmbs;
    }

    /**
     * 添加一项
     *
     * @param t
     */
    public void addItem(ChatMessageBean t) {
        cmbs.add(t);
        notifyDataSetChanged();

    }

    /**
     * 清除所有
     */
    public void clear() {
        if (cmbs == null || cmbs.size() <= 0)
            return;
        for (Iterator it = cmbs.iterator(); it.hasNext(); ) {

            ChatMessageBean t = (ChatMessageBean) it.next();
            int position = cmbs.indexOf(t);
            it.remove();
            notifyItemRemoved(position);
        }
    }

    /**
     * 移除某一项
     *
     * @param t
     */
    public void removeItem(ChatMessageBean t) {
        int position = cmbs.indexOf(t);
        cmbs.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * 刷新
     *
     * @param list
     */
    public void refreshData(List<ChatMessageBean> list) {
        clear();
        if (list != null && list.size() > 0) {

            int size = list.size();
            for (int i = 0; i < size; i++) {
                cmbs.add(i, list.get(i));
                notifyItemInserted(i);
            }
        }
    }

    /**
     * 加载更多
     *
     * @param list
     */
    public void loadMoreData(List<ChatMessageBean> list) {
        if (list != null && list.size() > 0) {
            int size = list.size();
            int begin = cmbs.size();
            for (int i = 0; i < size; i++) {
                cmbs.add(list.get(i));
                notifyItemInserted(i + begin);
            }
        }
    }


    @SuppressLint("SimpleDateFormat")
    public String getTime(String time, String before) {
        String show_time = null;
        if (before != null) {
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date now = df.parse(time);
                java.util.Date date = df.parse(before);
                long l = now.getTime() - date.getTime();
                long day = l / (24 * 60 * 60 * 1000);
                long hour = (l / (60 * 60 * 1000) - day * 24);
                long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
                if (min >= 1) {
                    show_time = time.substring(11);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            show_time = time.substring(11);
        }
        String getDay = getDay(time);
        if (show_time != null && getDay != null)
            show_time = getDay + " " + show_time;
        return show_time;
    }

    @SuppressLint("SimpleDateFormat")
    public static String returnTime() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    @SuppressLint("SimpleDateFormat")
    public String getDay(String time) {
        String showDay = null;
        String nowTime = returnTime();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            java.util.Date now = df.parse(nowTime);
            java.util.Date date = df.parse(time);
            long l = now.getTime() - date.getTime();
            long day = l / (24 * 60 * 60 * 1000);
            if (day >= 365) {
                showDay = time.substring(0, 10);
            } else if (day >= 1 && day < 365) {
                showDay = time.substring(5, 10);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return showDay;
    }

    @Override
    public void onClick(View view) {
        if (mOnChatItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnChatItemClickListener.onChatItemClick(view, (int) view.getTag());
        }
    }


    class FromUserMsgViewHolder extends RecyclerView.ViewHolder {

        private TextView chatTime;
        private ImageView otherUserIcon;
        private ChatTextView chatContent;

        public FromUserMsgViewHolder(View view) {
            super(view);
            chatTime = view.findViewById(R.id.chat_time);
            otherUserIcon = view.findViewById(R.id.other_user_icon);
            chatContent = view.findViewById(R.id.chat_content);
        }
    }

    class ToUserMsgViewHolder extends RecyclerView.ViewHolder {

        private TextView toChatTime;
        private ImageView myUserIcon;
        private ChatTextView toChatContent;

        public ToUserMsgViewHolder(View view) {
            super(view);
            toChatTime = view.findViewById(R.id.to_chat_time);
            myUserIcon = view.findViewById(R.id.my_user_icon);
            toChatContent = view.findViewById(R.id.to_chat_content);
        }
    }

    class FromUserImageViewHolder extends RecyclerView.ViewHolder {
        private TextView chatTime;
        private ImageView otherUserIcon;
        private BubbleImageView imageMsg;

        public FromUserImageViewHolder(View view) {
            super(view);
            chatTime = view.findViewById(R.id.chat_time);
            otherUserIcon = view.findViewById(R.id.other_user_icon);
            imageMsg = view.findViewById(R.id.image_message);
        }
    }

    class ToUserImgViewHolder extends RecyclerView.ViewHolder {

        private TextView toChatTime;
        private ImageView myUserIcon;
        private LinearLayout imageGroup;
        private BubbleImageView toImageMsg;
        private ImageView sendFailImg;

        public ToUserImgViewHolder(View view) {
            super(view);
            toChatTime = view.findViewById(R.id.to_chat_time);
            myUserIcon = view.findViewById(R.id.my_user_icon);
            sendFailImg = view.findViewById(R.id.mysend_fail_img);
            imageGroup = view.findViewById(R.id.to_image_group);
            toImageMsg = view.findViewById(R.id.to_image_message);
        }
    }


    public interface OnChatItemClickListener {
        void onChatItemClick(View view, int position);
    }

    public interface OnClickimageListener {
        void onClickimg(String imagePath);
    }

}
