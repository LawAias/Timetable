package com.gd.timetable.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * 对数据的sql操作
 */
public class DBAdapter extends SQLiteOpenHelper {

    private static final String TAG = DBAdapter.class.getSimpleName();

    private static final String DB_NAME = "schedule.db";

    private static final int DB_VERSION = 1;

    private static final String schedule_table = "schedule_table";


    //创建日程数据表
    private static final String create_schedule_tbl =
            "create table if not exists schedule_table("
                    + "id varchar, "
                    + "name varchar, "
                    + "place varchar, "
                    + "teacher varchar, "
                    + "content varchar, "
                    + "time varchar, "
                    + "date varchar "
                    + ")";


    private SQLiteDatabase db;

    public DBAdapter(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.beginTransaction();
        try {
            db.execSQL(create_schedule_tbl);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    public void closeDb() {
        if (db != null) {
            db.close();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS schedule_table");
            onCreate(db);
        } catch (Exception e) {
            Log.d(TAG, "onUpgrade Exception" + e.toString());
        }
    }


    /*****************
     * 对课程数据的相关操作
     *******************/
    public Cursor queryDataFromDB() {
        Cursor queryAllData = null;
        db = getWritableDatabase();
        try {
            queryAllData = db.query(schedule_table, null, null, null, null, null
                    , "time desc");
        } catch (Exception e) {
            Log.d(TAG, "queryDB Exception " + e.toString());
        }
        return queryAllData;
    }


    /**
     * 查询指定时间的课表信息
     * @param dateStr
     * @return
     */
    public Cursor queryDataFromDB(String dateStr) {
        Cursor queryAllData = null;
        db = getWritableDatabase();
        try {
            queryAllData = db.query(schedule_table, null, "date=" +DatabaseUtils.sqlEscapeString(dateStr) , null, null, null
                    , "time desc");
        } catch (Exception e) {
            Log.d(TAG, "queryDB Exception " + e.toString());
        }
        return queryAllData;
    }

    public void insertData2DB(ContentValues values) {
        try {
            db = getWritableDatabase();
            if (values != null) {
                db.insert(schedule_table, null, values);
            }
        } finally {
            closeDb();
        }
    }

    public void updateDataInfo(ContentValues values) {
        try {
            db = getWritableDatabase();
            if (values != null) {
                db.update(schedule_table, values, "id=?", new String[]{values.getAsString("id")});
            }
        } finally {
            closeDb();
        }
    }


    /**
     * 删除指定信息
     *
     * @param id
     * @return
     */
    public void deleteData(String id) {
        try {
            db = getWritableDatabase();
            db.delete(schedule_table, "id=?", new String[]{id});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeDb();
        }
    }


    public void deleteAllData() {
        try {
            db = getWritableDatabase();
            db.delete(schedule_table, null, null);
        } finally {
            closeDb();
        }
    }
    /*****************对数据的相关操作*******************/

}
