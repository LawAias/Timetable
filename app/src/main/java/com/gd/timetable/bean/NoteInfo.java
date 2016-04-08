package com.gd.timetable.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

@AVClassName(NoteInfo.MY_CLASS)
public class NoteInfo extends AVObject {

    static final String MY_CLASS = "NoteInfo";

    // 该笔记所属的用户
    public static final String USER = "note_user";

    // 日记的标题
    private static final String TITLE ="title";

    // 日记的文本内容
    private static final String CONTENT="content";


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


    public String getTitle() {
        return this.getString(TITLE);
    }

    public void setTitle(String title) {
        this.put(TITLE, title);
    }

    public String getContent() {
        return this.getString(CONTENT);
    }

    public void setContent(String content) {
        this.put(CONTENT, content);
    }

}
