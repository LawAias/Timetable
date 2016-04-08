package com.gd.timetable.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 课程信息（存储在本地数据库）
 * 
 * @author Harlan
 * 
 */
public class ScheduleInfo implements Parcelable{

	private String id;//uuid 唯一索引

	private String name;//课程名称

	private String place;//上课地点

	private String teacher;//任课教师

	private String content;//课程内容

	private String time;//课程时间（HH:MM）

	private String date;//课程日期

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getTeacher() {
		return teacher;
	}

	public void setTeacher(String teacher) {
		this.teacher = teacher;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public ScheduleInfo(){

	}

	protected ScheduleInfo(Parcel in) {
		id = in.readString();
		name = in.readString();
		place = in.readString();
		teacher = in.readString();
		content = in.readString();
		time = in.readString();
		date = in.readString();

	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(name);
		dest.writeString(place);
		dest.writeString(teacher);
		dest.writeString(content);
		dest.writeString(time);
		dest.writeString(date);

	}

	public static final Creator<ScheduleInfo> CREATOR = new Creator<ScheduleInfo>() {
		@Override
		public ScheduleInfo createFromParcel(Parcel in) {
			return new ScheduleInfo(in);
		}

		@Override
		public ScheduleInfo[] newArray(int size) {
			return new ScheduleInfo[size];
		}
	};
}
