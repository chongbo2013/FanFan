package com.fanfan.robot.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.fanfan.robot.model.Dance;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DANCE".
*/
public class DanceDao extends AbstractDao<Dance, Long> {

    public static final String TABLENAME = "DANCE";

    /**
     * Properties of entity Dance.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Title = new Property(1, String.class, "title", false, "title");
        public final static Property Time = new Property(2, long.class, "time", false, "time");
        public final static Property Path = new Property(3, String.class, "path", false, "path");
        public final static Property CoverPath = new Property(4, String.class, "coverPath", false, "coverPath");
        public final static Property Duration = new Property(5, long.class, "duration", false, "duration");
        public final static Property Order = new Property(6, String.class, "order", false, "order");
        public final static Property OrderData = new Property(7, String.class, "orderData", false, "orderData");
    }


    public DanceDao(DaoConfig config) {
        super(config);
    }
    
    public DanceDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DANCE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"title\" TEXT," + // 1: title
                "\"time\" INTEGER NOT NULL ," + // 2: time
                "\"path\" TEXT," + // 3: path
                "\"coverPath\" TEXT," + // 4: coverPath
                "\"duration\" INTEGER NOT NULL ," + // 5: duration
                "\"order\" TEXT," + // 6: order
                "\"orderData\" TEXT);"); // 7: orderData
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DANCE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Dance entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
        stmt.bindLong(3, entity.getTime());
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(4, path);
        }
 
        String coverPath = entity.getCoverPath();
        if (coverPath != null) {
            stmt.bindString(5, coverPath);
        }
        stmt.bindLong(6, entity.getDuration());
 
        String order = entity.getOrder();
        if (order != null) {
            stmt.bindString(7, order);
        }
 
        String orderData = entity.getOrderData();
        if (orderData != null) {
            stmt.bindString(8, orderData);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Dance entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(2, title);
        }
        stmt.bindLong(3, entity.getTime());
 
        String path = entity.getPath();
        if (path != null) {
            stmt.bindString(4, path);
        }
 
        String coverPath = entity.getCoverPath();
        if (coverPath != null) {
            stmt.bindString(5, coverPath);
        }
        stmt.bindLong(6, entity.getDuration());
 
        String order = entity.getOrder();
        if (order != null) {
            stmt.bindString(7, order);
        }
 
        String orderData = entity.getOrderData();
        if (orderData != null) {
            stmt.bindString(8, orderData);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Dance readEntity(Cursor cursor, int offset) {
        Dance entity = new Dance( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // title
            cursor.getLong(offset + 2), // time
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // path
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // coverPath
            cursor.getLong(offset + 5), // duration
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // order
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // orderData
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Dance entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTitle(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTime(cursor.getLong(offset + 2));
        entity.setPath(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setCoverPath(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDuration(cursor.getLong(offset + 5));
        entity.setOrder(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setOrderData(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Dance entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Dance entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Dance entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
