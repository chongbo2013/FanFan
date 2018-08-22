package com.fanfan.robot.db.upgrade;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.fanfan.robot.db.DaoMaster;
import com.fanfan.robot.db.VoiceBeanDao;

import org.greenrobot.greendao.database.Database;

/**
 * Created by android on 2018/1/24.
 */

public class MyOpenHelper extends DaoMaster.OpenHelper {

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    /**
     * 数据库升级
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //操作数据库的更新 有几个表升级都可以传入到下面
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, VoiceBeanDao.class);
    }

}
