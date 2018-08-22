package com.fanfan.novel.pointdown;

import android.content.ContentValues;
import android.text.TextUtils;

import com.fanfan.novel.pointdown.db.DownloadDBDao;
import com.fanfan.novel.pointdown.event.ProgressEvent;
import com.fanfan.novel.pointdown.model.Progress;
import com.fanfan.novel.pointdown.model.Request;
import com.fanfan.novel.pointdown.run.PriorityRunnable;
import com.fanfan.novel.utils.system.FileUtil;
import com.fanfan.youtu.utils.UUIDGenerator;
import com.seabreeze.log.Print;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.ThreadPoolExecutor;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by Administrator on 2018/3/31/031.
 */

public class DownloadRunnable implements Runnable {

    private static final int BUFFER_SIZE = 1024 * 8;

    public Progress progress;
    private ThreadPoolExecutor executor;
    private PriorityRunnable priorityRunnable;

    public DownloadRunnable(Progress progress) {
        this.progress = progress;
        executor = DownloadManager.getInstance().getThreadPool().getExecutor();
    }

    @Override
    public void run() {
        Print.e("load run");
        String fileName = progress.fileName;
        if (TextUtils.isEmpty(fileName)) {
            postOnError(progress, new Exception());
            return;
        }

        String folder = progress.folder;
        if (TextUtils.isEmpty(folder)) {
            postOnError(progress, new Exception());
            return;
        }
        File folderFile = new File(folder);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }

        long startPosition = progress.currentSize;
        if (startPosition < 0) {
            postOnError(progress, new Exception());
            return;
        }

        File loadFile = new File(folder, fileName);
        if (startPosition > 0) {
            if (!loadFile.exists()) {
                postOnError(progress, new Exception());
                return;
            }
        }

        Response response;
        try {
            Request request = progress.request;
            request.setStartPosition(startPosition);
            response = request.execute();
        } catch (IOException e) {
            e.printStackTrace();
            postOnError(progress, new Exception());
            return;
        }

        int code = response.code();
        if (code == 404 || code >= 500) {
            postOnError(progress, new Exception());
            return;
        }
        ResponseBody body = response.body();
        if (body == null) {
            postOnError(progress, new Exception());
            return;
        }

        if (progress.totalSize == -1) {
            progress.totalSize = body.contentLength();
        }

        if (startPosition > 0 && !loadFile.exists()) {
            postOnError(progress, new Exception());
            return;
        }
        if (startPosition > progress.totalSize) {
            postOnError(progress, new Exception());
            return;
        }
        if (startPosition == 0 && loadFile.exists()) {
            FileUtil.delFileOrFolder(loadFile);
        }
        if (startPosition == progress.totalSize && startPosition > 0) {
            if (loadFile.exists() && startPosition == loadFile.length()) {
                postOnFinish(progress, loadFile);
                return;
            } else {
                postOnError(progress, new Exception());
                return;
            }
        }

        //start downloading
        RandomAccessFile randomAccessFile;
        try {
            randomAccessFile = new RandomAccessFile(loadFile, "rw");
            randomAccessFile.seek(startPosition);
            progress.currentSize = startPosition;
        } catch (Exception e) {
            postOnError(progress, new Exception());
            return;
        }
        try {
            DownloadDBDao.getInstance().replace(progress);
            download(body.byteStream(), randomAccessFile, progress);
        } catch (IOException e) {
            postOnError(progress, new Exception());
            return;
        }

        //check finish status
        if (progress.status == Progress.PAUSE) {
            postPause(progress);
        } else if (progress.status == Progress.LOADING) {
            if (loadFile.length() == progress.totalSize) {
                postOnFinish(progress, loadFile);
            } else {
                postOnError(progress, new Exception());
            }
        } else {
            postOnError(progress, new Exception());
        }

    }

    /**
     * 执行文件下载
     */
    private void download(InputStream input, RandomAccessFile out, Progress progress) throws IOException {
        if (input == null || out == null) return;

        progress.status = Progress.LOADING;
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        int len;
        try {
            while ((len = in.read(buffer, 0, BUFFER_SIZE)) != -1 && progress.status == Progress.LOADING) {
                out.write(buffer, 0, len);

                Progress.changeProgress(progress, len, progress.totalSize, new Progress.Action() {
                    @Override
                    public void call(Progress progress) {
                        postLoading(progress);
                    }
                });
            }
        } finally {
            if (out != null)
                out.close();
            if (out != null)
                in.close();
            if (out != null)
                input.close();
        }
    }

    private void postEvent(Progress progress) {
        String uuid = UUIDGenerator.getUUID();
        ProgressEvent event = new ProgressEvent(uuid);
        EventBus.getDefault().post(event.setEvent(200, progress));
    }


    private void postOnStart(final Progress progress) {
        progress.speed = 0;
        progress.status = Progress.NONE;
        updateDatabase(progress);
    }

    private void postWaiting(final Progress progress) {
        progress.speed = 0;
        progress.status = Progress.WAITING;
        updateDatabase(progress);
        postEvent(progress);
    }

    private void postPause(final Progress progress) {
        progress.speed = 0;
        progress.status = Progress.PAUSE;
        updateDatabase(progress);
        postEvent(progress);
    }

    private void postLoading(final Progress progress) {
        updateDatabase(progress);
        postEvent(progress);
    }

    private void postOnError(final Progress progress, Exception e) {
        progress.speed = 0;
        progress.status = Progress.ERROR;
        progress.exception = e;
        updateDatabase(progress);
        postEvent(progress);
    }

    private void postOnFinish(final Progress progress, final File file) {
        progress.speed = 0;
        progress.fraction = 1.0f;
        progress.status = Progress.FINISH;
        updateDatabase(progress);
        postEvent(progress);
    }

    private void postOnRemove(final Progress progress) {
        updateDatabase(progress);
        postEvent(progress);
    }

    private void updateDatabase(Progress progress) {
        ContentValues contentValues = Progress.buildUpdateContentValues(progress);
        DownloadDBDao.getInstance().update(contentValues, progress.url);
    }


    public void start() {
        if (DownloadManager.getInstance().getTask(progress.url) == null || DownloadDBDao.getInstance().get(progress.url) == null) {
            throw new IllegalStateException("you must call DownloadTask#save() before DownloadTask#start()！");
        }
        if (progress.status == Progress.NONE || progress.status == Progress.PAUSE || progress.status == Progress.ERROR) {
            postOnStart(progress);
            postWaiting(progress);
            priorityRunnable = new PriorityRunnable(this);
            executor.execute(priorityRunnable);
        } else if (progress.status == Progress.FINISH) {

            File file = new File(progress.folder, progress.fileName);
            if (file.exists() && file.length() == progress.totalSize) {
                postOnFinish(progress, file);
            } else {
                postOnError(progress, new Exception());
            }
        } else if (progress.status == Progress.WAITING || progress.status == Progress.LOADING) {
            restart();
        }
    }

    /**
     * 暂停的方法
     */
    public void pause() {
        executor.remove(priorityRunnable);
        if (progress.status == Progress.WAITING) {
            postPause(progress);
        } else if (progress.status == Progress.LOADING) {
            progress.speed = 0;
            progress.status = Progress.PAUSE;
            postEvent(progress);
        }
    }

    /**
     * 删除一个任务,会删除下载文件
     */
    public void remove() {
        remove(false);
    }

    /**
     * 删除一个任务,会删除下载文件
     */
    public DownloadRunnable remove(boolean isDeleteFile) {
        pause();
        File file = new File(progress.folder, progress.fileName);
        if (isDeleteFile) {
            FileUtil.delFileOrFolder(file);
        }
        DownloadDBDao.getInstance().delete(progress.url);
        DownloadRunnable task = DownloadManager.getInstance().removeTask(progress.url);
        postOnRemove(progress);
        return task;
    }

    public void restart() {
        pause();
        FileUtil.delFileOrFolder(new File(progress.folder, progress.fileName));
        progress.status = Progress.NONE;
        progress.currentSize = 0;
        progress.fraction = 0;
        progress.speed = 0;
        if (progress.request == null) {
            progress.request = new Request(progress.url);
        }
        DownloadDBDao.getInstance().replace(progress);
        start();
    }
}
