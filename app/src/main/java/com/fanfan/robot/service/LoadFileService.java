package com.fanfan.robot.service;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.fanfan.robot.app.common.Constants;
import com.fanfan.robot.app.common.base.BaseService;
import com.fanfan.novel.pointdown.DownloadManager;
import com.fanfan.novel.pointdown.DownloadRunnable;
import com.fanfan.novel.pointdown.db.DownloadDBDao;
import com.fanfan.novel.pointdown.event.ProgressEvent;
import com.fanfan.novel.pointdown.model.Progress;
import com.fanfan.robot.other.cache.LoadFileCache;
import com.fanfan.robot.other.event.LoadStartEvent;
import com.fanfan.robot.listener.load.ProgressListener;
import com.fanfan.robot.listener.music.Actions;
import com.fanfan.robot.R;
import com.fanfan.youtu.api.base.Constant;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.text.NumberFormat;

public class LoadFileService extends BaseService {

    public static final int ChannelId = 22;

    private NotificationManager mManager;
    private NotificationCompat.Builder mBuilder;

    private NumberFormat numberFormat;

    private DownloadRunnable task;

    @Override
    public void onCreate() {
        super.onCreate();

        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mBuilder = createNotification(this);
        mManager.notify(ChannelId, mBuilder.build());

        numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMinimumFractionDigits(2);

        LoadFileCache.get().setFileService(this);
        EventBus.getDefault().post(new LoadStartEvent(null, -1, true));
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LoadBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case Actions.ACTION_LOAD_DOWNLOAD:
                    download();
                    break;
                case Actions.ACTION_LOAD_REMOVE:
                    remove();
                    break;
                case Actions.ACTION_LOAD_RESTART:
                    restart();
                    break;
            }
        }
        return START_NOT_STICKY;
    }

    public void restart() {
        if (task != null)
            task.restart();
    }

    public void remove() {
        if (task != null) {
            task.remove();
            task = null;
        }
    }

    public void download() {

        Progress progress = DownloadDBDao.getInstance().get(Constant.APK_URL);

        if (progress != null) {
            task = DownloadManager.request(progress);
        }

        if (task == null) {
            task = DownloadManager.request(Constant.APK_URL, Constants.DOWNLOAD_PATH, "robot.apk");
        }

        switch (task.progress.status) {
            case Progress.PAUSE:
            case Progress.NONE:
            case Progress.ERROR:
                task.start();
                break;
            case Progress.LOADING:
                task.pause();
                break;
            case Progress.FINISH:
                mManager.cancel(ChannelId);
                if (mListener != null) {
                    mListener.onProgress(progress);
                    mListener.onFinish(new File(progress.folder, progress.fileName), progress);
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        LoadFileCache.get().setFileService(null);
        super.onDestroy();
    }

    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onResultEvent(ProgressEvent event) {
        if (event.isOk()) {
            Progress progress = event.getBean();

            switch (progress.status) {
                case Progress.NONE:
                    if (mListener != null) {
                        mListener.onStart(progress);
                    }
                    break;
                case Progress.LOADING:

                    mBuilder.setContentText(String.format(getString(R.string.download_progress), (int) (progress.fraction * 100)));
                    mBuilder.setProgress(100, (int) (progress.fraction * 100), false);
                    mBuilder.setDefaults(0);
                    mManager.notify(ChannelId, mBuilder.build());

                    if (mListener != null) {
                        mListener.onProgress(progress);
                    }
                    break;
                case Progress.PAUSE:
                    task.start();
                    if (mListener != null) {
                        mListener.onProgress(progress);
                    }
                    break;
                case Progress.WAITING:
                    if (mListener != null) {
                        mListener.onProgress(progress);
                    }
                    break;
                case Progress.ERROR:
                    mBuilder.setContentText(getString(R.string.download_fail));
                    mBuilder.setProgress(100, 0, false);
                    mManager.notify(ChannelId, mBuilder.build());

                    if (mListener != null) {
                        mListener.onProgress(progress);
                        mListener.onError(progress);
                    }
                    break;
                case Progress.FINISH:
                    mManager.cancel(ChannelId);
                    if (mListener != null) {
                        mListener.onProgress(progress);
                        mListener.onFinish(new File(progress.folder, progress.fileName), progress);
                    }
                    break;
            }
        } else {
            Print.e("LoadFileService error");
        }
    }

    private ProgressListener mListener;

    public void setOnLoadEventListener(ProgressListener listener) {
        mListener = listener;
    }

    /**
     * 通知创建
     *
     * @param context
     * @return
     */
    private static NotificationCompat.Builder createNotification(Context context) {
        final String CHANNEL_ID = "0", CHANNEL_NAME = "ALLEN_NOTIFICATION";
        NotificationCompat.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            NotificationManager manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(notificationChannel);
        }
        builder = new NotificationCompat.Builder(context, CHANNEL_ID);

        builder.setAutoCancel(true);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(context.getString(R.string.app_name));
        builder.setTicker(context.getString(R.string.downloading));
        builder.setContentText(String.format(context.getString(R.string.download_progress), 0));

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
        return builder;
    }

    public class LoadBinder extends Binder {
        public LoadFileService getService() {
            return LoadFileService.this;
        }
    }
}
