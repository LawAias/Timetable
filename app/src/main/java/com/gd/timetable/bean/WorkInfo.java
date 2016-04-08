package com.gd.timetable.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;


/**
 * 作业数据结构
 */
@AVClassName(WorkInfo.MY_CLASS)
public class WorkInfo extends AVObject {

    static final String MY_CLASS = "WorkInfo";

    // 该作业的发布者
    public static final String USER = "manager";
    // 该作业的课程logo(图标)
    public static final String LOGO = "logo";
    // 作业标题
    private static final String TITLE ="title";
    // 作业所属的课程名称
    private static final String COURESE ="course";
    // 作业的详情（要求等）
    private static final String WORKDETAIL ="work_detail";
    // 作业完成的截止时间
    private static final String DEADLINE ="dead_line";



    public UserInfo getUserInfo() {
        try {
            return this.getAVObject(USER, UserInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setUserInfo(UserInfo userInfo) {
        this.put(USER, userInfo);
    }

    public AVFile getLogo() {
        return this.getAVFile(LOGO);
    }

    public void setLogo(AVFile fileContent) {
        this.put(LOGO, fileContent);
    }


    public String getTitle() {
        return this.getString(TITLE);
    }

    public void setTitle(String title) {
        this.put(TITLE, title);
    }

    public String getCourse() {
        return this.getString(COURESE);
    }

    public void setCourse(String type) {
        this.put(COURESE, type);
    }

    public String getWorkDetail() {
        return this.getString(WORKDETAIL);
    }

    public void setWorkDetail(String content) {
        this.put(WORKDETAIL, content);
    }

    public String getDeadline() {
        return this.getString(DEADLINE);
    }

    public void setDeadline(String time) {
        this.put(DEADLINE, time);
    }



}
