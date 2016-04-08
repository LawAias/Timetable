package com.gd.timetable.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.gd.timetable.bean.ScheduleInfo;

import java.util.ArrayList;
import java.util.List;


/**
 * 对数据库数据处理的封装类
 * 直接对javabean操作，方便简洁
 * @author sjy
 *
 */
public class DBProvider {
	private Context mContext;

	private DBAdapter mDbAdapter = null;

	private static DBProvider instance = null;

	private DBProvider(Context context) {
		this.mContext = context;
		this.mDbAdapter = new DBAdapter(mContext);

	}

	public static synchronized DBProvider getInstance(Context context) {
		if (instance == null)
			instance = new DBProvider(context);
		return instance;
	}

	
	/*****************对数据的相关操作*******************/
	public void insertScheduleInfo(ScheduleInfo info) {
		if (info == null)
			return;
		ContentValues cv = new ContentValues();
		cv.put("id", info.getId());
		cv.put("name", info.getName());
		cv.put("place", info.getPlace());
		cv.put("teacher", info.getTeacher());
		cv.put("content", info.getContent());
		cv.put("time", info.getTime());
		cv.put("date", info.getDate());
		mDbAdapter.insertData2DB(cv);
	}


	/**
	 * 查询所有
	 * @return
	 */
	public List<ScheduleInfo> getScheduleFromDB() {
		List<ScheduleInfo> infos = new ArrayList<ScheduleInfo>();
		Cursor data = mDbAdapter.queryDataFromDB();
		if (data != null) {
			try {
				while (data.moveToNext()) {
					ScheduleInfo item = new ScheduleInfo();
					item.setId(data.getString(data.getColumnIndex("id")));
					item.setName(data.getString(data.getColumnIndex("name")));
					item.setPlace(data.getString(data.getColumnIndex("place")));
					item.setTeacher(data.getString(data.getColumnIndex("teacher")));
					item.setContent(data.getString(data.getColumnIndex("content")));
					item.setTime(data.getString(data.getColumnIndex("time")));
					item.setDate(data.getString(data.getColumnIndex("date")));
					infos.add(item);
				}
			} finally {
				data.close();
				mDbAdapter.closeDb();
			}
		}
		return infos;
	}


	/**
	 * 查询指定日期（今天）
	 * @return
	 */
	public List<ScheduleInfo> getScheduleFromDB(String date) {
		List<ScheduleInfo> infos = new ArrayList<ScheduleInfo>();
		Cursor data = mDbAdapter.queryDataFromDB(date);
		if (data != null) {
			try {
				while (data.moveToNext()) {
					ScheduleInfo item = new ScheduleInfo();
					item.setId(data.getString(data.getColumnIndex("id")));
					item.setName(data.getString(data.getColumnIndex("name")));
					item.setPlace(data.getString(data.getColumnIndex("place")));
					item.setTeacher(data.getString(data.getColumnIndex("teacher")));
					item.setContent(data.getString(data.getColumnIndex("content")));
					item.setTime(data.getString(data.getColumnIndex("time")));
					item.setDate(data.getString(data.getColumnIndex("date")));
					infos.add(item);
				}
			} finally {
				data.close();
				mDbAdapter.closeDb();
			}
		}
		return infos;
	}


	/**
	 * 查询所有日期的，用来同步成今天的
	 * @return
	 */
	public List<ScheduleInfo> getAllScheduleFromDB() {
		List<ScheduleInfo> infos = new ArrayList<ScheduleInfo>();
		Cursor data = mDbAdapter.queryDataFromDB();
		if (data != null) {
			try {
				while (data.moveToNext()) {
					ScheduleInfo item = new ScheduleInfo();
					item.setId(data.getString(data.getColumnIndex("id")));
					item.setName(data.getString(data.getColumnIndex("name")));
					item.setPlace(data.getString(data.getColumnIndex("place")));
					item.setTeacher(data.getString(data.getColumnIndex("teacher")));
					item.setContent(data.getString(data.getColumnIndex("content")));
					item.setTime(data.getString(data.getColumnIndex("time")));
					item.setDate(data.getString(data.getColumnIndex("date")));
					infos.add(item);
				}
			} finally {
				data.close();
				mDbAdapter.closeDb();
			}
		}
		return infos;
	}


	public void updateNoteInfo(ScheduleInfo info) {
		if (info == null)
			return;
		ContentValues cv = new ContentValues();
		cv.put("id", info.getId());
		cv.put("name", info.getName());
		cv.put("place", info.getPlace());
		cv.put("teacher", info.getTeacher());
		cv.put("content", info.getContent());
		cv.put("time", info.getTime());
		cv.put("date", info.getDate());
		mDbAdapter.updateDataInfo(cv);
	}
	
	public void delScheduleInfo(String id) {
		if (TextUtils.isEmpty(id))
			return;
		mDbAdapter.deleteData(id);
	}
	
	public void emptyAllNoteFromDB() {
		mDbAdapter.deleteAllData();
	}
	
	/*****************对数据的相关操作*******************/
	


}
