package com.fanfan.novel.pointdown.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.fanfan.novel.pointdown.model.Progress;
import com.fanfan.robot.app.NovelApp;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_CACHE_NAME = "okgo.db";
    private static final int DB_CACHE_VERSION = 1;
    public static final String TABLE_DOWNLOAD = "download";

    static final Lock lock = new ReentrantLock();

    private TableEntity downloadTableEntity = new TableEntity(TABLE_DOWNLOAD);

    public DBHelper() {
        this(NovelApp.getInstance());
    }

    DBHelper(Context context) {
        super(context, DB_CACHE_NAME, null, DB_CACHE_VERSION);


        downloadTableEntity
                .addColumn(new ColumnEntity(Progress.URL, "VARCHAR", true, true))//
                .addColumn(new ColumnEntity(Progress.FOLDER, "VARCHAR"))//
                .addColumn(new ColumnEntity(Progress.FILE_NAME, "VARCHAR"))//
                .addColumn(new ColumnEntity(Progress.FRACTION, "VARCHAR"))//
                .addColumn(new ColumnEntity(Progress.TOTAL_SIZE, "INTEGER"))//
                .addColumn(new ColumnEntity(Progress.CURRENT_SIZE, "INTEGER"))//
                .addColumn(new ColumnEntity(Progress.STATUS, "INTEGER"))//
                .addColumn(new ColumnEntity(Progress.DATE, "INTEGER"))//
                .addColumn(new ColumnEntity(Progress.REQUEST, "BLOB"));//

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(downloadTableEntity.buildTableString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String tableName = downloadTableEntity.tableName;
        if (tableName == null || db == null || !db.isOpen())
            return;

        int count = 0;

        Cursor cursor = null;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? AND name = ?", new String[]{"table", tableName});
            if (!cursor.moveToFirst()) {
                return;
            }
            count = cursor.getInt(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (count <= 0)
            return;

        Cursor cursor1 = db.rawQuery("select * from " + tableName, null);
        if (cursor1 == null)
            return;
        try {
            int columnCount = downloadTableEntity.getColumnCount();
            if (columnCount == cursor.getColumnCount()) {
                for (int i = 0; i < columnCount; i++) {
                    if (downloadTableEntity.getColumnIndex(cursor.getColumnName(i)) == -1) {
                        return;
                    }
                }
            } else {
                return;
            }

        } finally {
            cursor1.close();
        }

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOAD);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
