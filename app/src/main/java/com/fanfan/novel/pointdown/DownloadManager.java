package com.fanfan.novel.pointdown;

import com.fanfan.novel.pointdown.db.DownloadDBDao;
import com.fanfan.novel.pointdown.model.Progress;
import com.fanfan.novel.pointdown.model.Request;
import com.fanfan.novel.pointdown.run.DownloadThreadPool;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Administrator on 2018/3/31/031.
 */

public class DownloadManager {

    private DownloadThreadPool threadPool;                      //下载的线程池
    private ConcurrentHashMap<String, DownloadRunnable> taskMap;    //所有任务

    public static DownloadManager getInstance() {
        return OkDownloadHolder.instance;
    }

    private static class OkDownloadHolder {
        private static final DownloadManager instance = new DownloadManager();
    }


    private DownloadManager() {
        threadPool = new DownloadThreadPool();
        taskMap = new ConcurrentHashMap<>();

        //校验数据的有效性，防止下载过程中退出，第二次进入的时候，由于状态没有更新导致的状态错误
        List<Progress> taskList = DownloadDBDao.getInstance().getDownloading();
        for (Progress info : taskList) {
            if (info.status == Progress.WAITING || info.status == Progress.LOADING || info.status == Progress.PAUSE) {
                info.status = Progress.NONE;
            }
        }
        DownloadDBDao.getInstance().replace(taskList);
    }

    public static DownloadRunnable request(Progress progress) {
        Map<String, DownloadRunnable> taskMap = DownloadManager.getInstance().getTaskMap();
        DownloadRunnable task = taskMap.get(progress.url);
        if (task == null) {
            task = new DownloadRunnable(progress);
            taskMap.put(progress.url, task);
        }
        return task;
    }

    public static DownloadRunnable request(String url, String folder, String fileName) {
        Request request = new Request(url);

        Progress progress = new Progress();
        progress.url = url;
        progress.status = Progress.NONE;
        progress.totalSize = -1;
        progress.request = request;
        progress.folder = folder;
        progress.fileName = fileName;
        DownloadDBDao.getInstance().replace(progress);

        Map<String, DownloadRunnable> taskMap = DownloadManager.getInstance().getTaskMap();
        DownloadRunnable task = taskMap.get(url);
        if (task == null) {
            task = new DownloadRunnable(progress);
            taskMap.put(url, task);
        }
        return task;
    }


    public Map<String, DownloadRunnable> getTaskMap() {
        return taskMap;
    }

    public DownloadThreadPool getThreadPool() {
        return threadPool;
    }

    public DownloadRunnable getTask(String tag) {
        return taskMap.get(tag);
    }

    public DownloadRunnable removeTask(String tag) {
        return taskMap.remove(tag);
    }
}
