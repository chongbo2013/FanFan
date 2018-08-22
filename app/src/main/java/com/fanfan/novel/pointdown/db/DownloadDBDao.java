package com.fanfan.novel.pointdown.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.fanfan.novel.pointdown.model.Progress;

import java.util.List;

/**
 * Created by Administrator on 2018/3/31/031.
 */

public class DownloadDBDao extends BaseDao<Progress> {

    private DownloadDBDao() {
        super(new DBHelper());
    }

    public static DownloadDBDao getInstance() {
        return DownloadManagerHolder.instance;
    }

    private static class DownloadManagerHolder {
        private static final DownloadDBDao instance = new DownloadDBDao();
    }

    @Override
    public Progress parseCursorToBean(Cursor cursor) {
        return Progress.parseCursorToBean(cursor);
    }

    @Override
    public ContentValues getContentValues(Progress progress) {
        return Progress.buildContentValues(progress);
    }

    @Override
    public String getTableName() {
        return DBHelper.TABLE_DOWNLOAD;
    }

    @Override
    public void unInit() {
    }

    /**
     * 获取下载任务
     */
    public Progress get(String url) {
        return queryOne(Progress.URL + "=?", new String[]{url});
    }

    /**
     * 移除下载任务
     */
    public void delete(String taskKey) {
        delete(Progress.URL + "=?", new String[]{taskKey});
    }

    /**
     * 更新下载任务
     */
    public boolean update(Progress progress) {
        return update(progress, Progress.URL + "=?", new String[]{progress.url});
    }

    /**
     * 更新下载任务
     */
    public boolean update(ContentValues contentValues, String tag) {
        return update(contentValues, Progress.URL + "=?", new String[]{tag});
    }

    /**
     * 获取所有下载信息
     */
    public List<Progress> getAll() {
        return query(null, null, null, null, null, Progress.DATE + " ASC", null);
    }

    /**
     * 获取所有下载信息
     */
    public List<Progress> getFinished() {
        return query(null, "status=?", new String[]{Progress.FINISH + ""}, null, null, Progress.DATE + " ASC", null);
    }

    /**
     * 获取所有下载信息
     */
    public List<Progress> getDownloading() {
        return query(null, "status not in(?)", new String[]{Progress.FINISH + ""}, null, null, Progress.DATE + " ASC", null);
    }

    /**
     * 清空下载任务
     */
    public boolean clear() {
        return deleteAll();
    }
}
