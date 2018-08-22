package com.fanfan.robot.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.widget.EditText;

import com.fanfan.novel.utils.system.ScreenUtil;
import com.fanfan.novel.utils.gif.FaceData;
import com.fanfan.novel.utils.gif.GifOpenHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by android on 2017/12/19.
 */

public class ChatTextView extends EditText {

    private static final int DELAYED = 300;

    private class SpanInfo {
        private ArrayList<Bitmap> mapList;
        private int start, end, frameCount, currentFrameIndex, delay;

        public SpanInfo() {
            mapList = new ArrayList<Bitmap>();
            start = end = frameCount = currentFrameIndex = delay = 0;
        }
    }

    private ArrayList<SpanInfo> spanInfoList = null;
    private Handler handler; // 用于处理从子线程TextView传来的消息
    private String myText; // 存储textView应该显示的文本

    public ChatTextView(Context context) {
        super(context);
        setFocusableInTouchMode(false);
    }

    public ChatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusableInTouchMode(false);
    }

    public ChatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFocusableInTouchMode(false);
    }


    private boolean isGif;
    public TextRunnable rTextRunnable;

    public void setSpanText(Handler handler, final String text, boolean isGif) {
        this.handler = handler; // 获得UI的Handler
        this.isGif = isGif;
        spanInfoList = new ArrayList<SpanInfo>();
        if (parseText(text)) {// 对String对象进行解析
            if (parseMessage(this)) {
                startPost();
            }
        } else {
            setText(myText);
        }
    }


    private boolean parseText(String inputStr) {
        myText = inputStr;
        Pattern mPattern = Pattern.compile("\\[[^\\]]+\\]");
        Matcher mMatcher = mPattern.matcher(inputStr);
        boolean hasGif = false;
        while (mMatcher.find()) {
            String faceName = mMatcher.group();
            Integer faceId = null;
            if ((faceId = FaceData.gifFaceInfo.get(faceName)) != null) {
                if (isGif) {
                    parseGif(faceId, mMatcher.start(), mMatcher.end());
                } else {
                    parseBmp(faceId, mMatcher.start(), mMatcher.end());
                }
            }
            hasGif = true;
        }
        return hasGif;
    }


    private void parseGif(int resourceId, int start, int end) {
        GifOpenHelper helper = new GifOpenHelper();
        helper.read(getContext().getResources().openRawResource(resourceId));
        SpanInfo spanInfo = new SpanInfo();
        spanInfo.currentFrameIndex = 0;
        spanInfo.frameCount = helper.getFrameCount();
        spanInfo.start = start;
        spanInfo.end = end;
        spanInfo.mapList.add(helper.getImage());
        for (int i = 1; i < helper.getFrameCount(); i++) {
            spanInfo.mapList.add(helper.nextBitmap());
        }
        spanInfo.delay = helper.nextDelay();
        spanInfoList.add(spanInfo);
    }

    private void parseBmp(int resourceId, int start, int end) {
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), resourceId);
        ImageSpan imageSpan = new ImageSpan(getContext(), bitmap);
        SpanInfo spanInfo = new SpanInfo();
        spanInfo.currentFrameIndex = 0;
        spanInfo.frameCount = 1;
        spanInfo.start = start;
        spanInfo.end = end;
        spanInfo.delay = 100;
        spanInfo.mapList.add(bitmap);
        spanInfoList.add(spanInfo);
    }

    public boolean parseMessage(ChatTextView chatTextView) {
        if (chatTextView.myText != null && !chatTextView.myText.equals("")) {
            SpannableString sb = new SpannableString("" + chatTextView.myText); // 获得要显示的文本
            int gifCount = 0;
            SpanInfo info = null;
            for (int i = 0; i < chatTextView.spanInfoList.size(); i++) { // for循环，处理显示多个图片的问题
                info = chatTextView.spanInfoList.get(i);
                if (info.mapList.size() > 1) {
                    gifCount++;
                }
                Bitmap bitmap = info.mapList.get(info.currentFrameIndex);
                info.currentFrameIndex = (info.currentFrameIndex + 1) % (info.frameCount);
                int size = ScreenUtil.dip2px(chatTextView.getContext(), 30);
                if (gifCount != 0) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                } else {
                    bitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                }
                ImageSpan imageSpan = new ImageSpan(chatTextView.getContext(), bitmap);
                if (info.end <= sb.length()) {
                    sb.setSpan(imageSpan, info.start, info.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                } else {
                    break;
                }

            }
            chatTextView.setText(sb);
            if (gifCount != 0) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    public void startPost() {
        rTextRunnable = new TextRunnable(this); // 生成Runnable对象
        handler.post(rTextRunnable); // 利用UI线程的Handler 将r添加进消息队列中。
    }

    public static final class TextRunnable implements Runnable {
        private final WeakReference<ChatTextView> mWeakReference;

        public TextRunnable(ChatTextView f) {
            mWeakReference = new WeakReference<ChatTextView>(f);
        }

        @Override
        public void run() {
            ChatTextView chatTextView = mWeakReference.get();
            if (chatTextView != null) {
                if (chatTextView.parseMessage(chatTextView)) {
                    chatTextView.handler.postDelayed(this, DELAYED);
                }
            }
        }
    }

    public String getMyText() {
        return myText;
    }



}
