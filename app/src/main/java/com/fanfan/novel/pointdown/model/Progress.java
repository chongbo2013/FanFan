package com.fanfan.novel.pointdown.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.SystemClock;

import com.fanfan.novel.utils.system.FileUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/31/031.
 */

public class Progress implements Serializable {

    public static final int NONE = 0;         //无状态
    public static final int WAITING = 1;      //等待
    public static final int LOADING = 2;      //下载中
    public static final int PAUSE = 3;        //暂停
    public static final int ERROR = 4;        //错误
    public static final int FINISH = 5;       //完成

    public static final String URL = "url";
    public static final String FOLDER = "folder";
    public static final String FILE_NAME = "fileName";
    public static final String FRACTION = "fraction";
    public static final String TOTAL_SIZE = "totalSize";
    public static final String CURRENT_SIZE = "currentSize";
    public static final String STATUS = "status";
    public static final String DATE = "date";
    public static final String REQUEST = "request";

    public String url;                              //网址
    public String folder;                           //保存文件夹
    public String fileName;                         //保存的文件名
    public float fraction;                          //下载的进度，0-1
    public long totalSize;                          //总字节长度, byte
    public long currentSize;                        //本次下载的大小, byte
    public transient long speed;                    //网速，byte/s
    public int status;                              //当前状态
    public long date;                               //创建时间
    public Request request;   //网络请求
    public Throwable exception;                     //当前进度出现的异常

    private transient long tempSize;                //每一小段时间间隔的网络流量
    private transient long lastRefreshTime;         //最后一次刷新的时间
    private transient List<Long> speedBuffer;       //网速做平滑的缓存，避免抖动过快

    public Progress() {
        lastRefreshTime = SystemClock.elapsedRealtime();
        totalSize = -1;
        date = System.currentTimeMillis();
        speedBuffer = new ArrayList<>();
    }

    public static Progress changeProgress(final Progress progress, long writeSize, long totalSize, final Action action) {
        progress.totalSize = totalSize;
        progress.currentSize += writeSize;
        progress.tempSize += writeSize;

        long currentTime = SystemClock.elapsedRealtime();
        boolean isNotify = (currentTime - progress.lastRefreshTime) >= 300;
        if (isNotify || progress.currentSize == totalSize) {
            long diffTime = currentTime - progress.lastRefreshTime;
            if (diffTime == 0) diffTime = 1;
            progress.fraction = progress.currentSize * 1.0f / totalSize;
            progress.speed = progress.bufferSpeed(progress.tempSize * 1000 / diffTime);
            progress.lastRefreshTime = currentTime;
            progress.tempSize = 0;
            if (action != null) {
                action.call(progress);
            }
        }
        return progress;
    }

    /**
     * 平滑网速，避免抖动过大
     */
    private long bufferSpeed(long speed) {
        speedBuffer.add(speed);
        if (speedBuffer.size() > 10) {
            speedBuffer.remove(0);
        }
        long sum = 0;
        for (float speedTemp : speedBuffer) {
            sum += speedTemp;
        }
        return sum / speedBuffer.size();
    }

    /**
     * 转换进度信息
     */
    public void from(Progress progress) {
        totalSize = progress.totalSize;
        currentSize = progress.currentSize;
        fraction = progress.fraction;
        speed = progress.speed;
        lastRefreshTime = progress.lastRefreshTime;
        tempSize = progress.tempSize;
    }

    public interface Action {
        void call(Progress progress);
    }

    public static ContentValues buildContentValues(Progress progress) {
        ContentValues values = new ContentValues();
        values.put(URL, progress.url);
        values.put(FOLDER, progress.folder);
        values.put(FILE_NAME, progress.fileName);
        values.put(FRACTION, progress.fraction);
        values.put(TOTAL_SIZE, progress.totalSize);
        values.put(CURRENT_SIZE, progress.currentSize);
        values.put(STATUS, progress.status);
        values.put(DATE, progress.date);
        values.put(REQUEST,  FileUtil.toByteArray(progress.request));
        return values;
    }

    public static ContentValues buildUpdateContentValues(Progress progress) {
        ContentValues values = new ContentValues();
        values.put(FRACTION, progress.fraction);
        values.put(TOTAL_SIZE, progress.totalSize);
        values.put(CURRENT_SIZE, progress.currentSize);
        values.put(STATUS, progress.status);
        values.put(DATE, progress.date);
        return values;
    }

    public static Progress parseCursorToBean(Cursor cursor) {
        Progress progress = new Progress();
        progress.url = cursor.getString(cursor.getColumnIndex(Progress.URL));
        progress.folder = cursor.getString(cursor.getColumnIndex(Progress.FOLDER));
        progress.fileName = cursor.getString(cursor.getColumnIndex(Progress.FILE_NAME));
        progress.fraction = cursor.getFloat(cursor.getColumnIndex(Progress.FRACTION));
        progress.totalSize = cursor.getLong(cursor.getColumnIndex(Progress.TOTAL_SIZE));
        progress.currentSize = cursor.getLong(cursor.getColumnIndex(Progress.CURRENT_SIZE));
        progress.status = cursor.getInt(cursor.getColumnIndex(Progress.STATUS));
        progress.date = cursor.getLong(cursor.getColumnIndex(Progress.DATE));
        progress.request = (Request) FileUtil.toObject(cursor.getBlob(cursor.getColumnIndex(Progress.REQUEST)));
        return progress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Progress progress = (Progress) o;
        return url != null ? url.equals(progress.url) : progress.url == null;

    }

    @Override
    public int hashCode() {
        return url != null ? url.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Progress{" +//
                "fraction=" + fraction +//
                ", totalSize=" + totalSize +//
                ", currentSize=" + currentSize +//
                ", speed=" + speed +//
                ", status=" + status +//
                ", folder=" + folder +//
                ", fileName=" + fileName +//
                ", url=" + url +//
                '}';
    }
}
