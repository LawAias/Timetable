package com.gd.timetable.bean;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;

import java.io.IOException;

/**
 * 用户信息
 * 
 * @author sjy
 * 
 */
@AVClassName(UserInfo.MY_CLASS)
public class UserInfo extends AVObject{

	static final String MY_CLASS = "UserInfo";

	public static final String AVATAR = "avatar";
	// 用户名(注册名)
	public static final String USERNAME = "user_name";
	// 用户昵称
	private static final String NICKNAME = "user_nickname";
	// 联系电话
	public static final String PHONE = "user_phone";
	// 用户类型 0: 普通用户  1：管理者
	public static final String TYPE = "user_type";

	//存储密码，密保找回用
	public static final String PWD = "password";

	public static final String ANSWER = "answer";

	public static final String QUESTION = "question";


	public AVFile getAvatar(){
		return this.getAVFile(AVATAR);
	}

	public void setAvatar(AVFile content) {
		this.put(AVATAR, content);
	}


	public String getAvatarUrl() {
		if (getAvatar() != null) {
			return getAvatar().getUrl();
		} else {
			return null;
		}
	}


	public void saveAvatar(String path, final SaveCallback saveCallback) {
		final AVFile file;
		try {
			file = AVFile.withAbsoluteLocalPath(this.getUserName(), path);
			put(AVATAR, file);
			file.saveInBackground(new SaveCallback() {
				@Override
				public void done(AVException e) {
					if (null == e) {
						saveInBackground(saveCallback);
					} else {
						if (null != saveCallback) {
							saveCallback.done(e);
						}
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}




	public String getUserName() {
		return this.getString(USERNAME);
	}

	public void setUserName(String content) {
		this.put(USERNAME, content);
	}

	public String getNickName() {
		return this.getString(NICKNAME);
	}

	public void setNickName(String content) {
		this.put(NICKNAME, content);
	}
	
	public String getPhone() {
		return this.getString(PHONE);
	}

	public void setPhone(String content) {
		this.put(PHONE, content);
	}


	public String getType() {
		return this.getString(TYPE);
	}

	public void setType(String content) {
		this.put(TYPE, content);
	}

	public String getPassword() {
		return this.getString(PWD);
	}

	public void setPassword(String content) {
		this.put(PWD, content);
	}

	public String getAnswer() {
		return this.getString(ANSWER);
	}

	public void setAnswer(String content) {
		this.put(ANSWER, content);
	}

	public String getQuestion() {
		return this.getString(QUESTION);
	}

	public void setQuestion(String content) {
		this.put(QUESTION, content);
	}

}
