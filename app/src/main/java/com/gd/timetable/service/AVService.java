package com.gd.timetable.service;


import android.text.TextUtils;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SignUpCallback;
import com.gd.timetable.bean.UserInfo;

import java.util.List;


/**
 * 第三方服务器的服务
 */
public class AVService {

    // 重置密码
    public static void requestPasswordReset(String email,
                                            RequestPasswordResetCallback callback) {
        AVUser.requestPasswordResetInBackground(email, callback);
    }

    // 注册账号
    public static void signUp(String stuid, String name, String password,
                              String email,String mobileNumber,
                              SignUpCallback signUpCallback) {
        AVUser user = new AVUser();
        user.setUsername(stuid);
        user.setPassword(password);
        user.setMobilePhoneNumber(mobileNumber);
        user.setEmail(email);
        user.signUpInBackground(signUpCallback);
    }

    // 注册账号
    public static void signUp(String stuid, String password,
                              String email, SignUpCallback signUpCallback) {
        AVUser user = new AVUser();
        user.setUsername(stuid);
        user.setPassword(password);
        user.setEmail(email);
        user.signUpInBackground(signUpCallback);
    }

    // 登出
    public static void logout() {
        AVUser.logOut();
    }


    // 将用户名和pushid相互关联
    /*public static void updateInstallation(Context ctx,String username) {
		
		PushService.setDefaultPushCallback(ctx, MenuActivity.class);
		PushService.subscribe(ctx, "public", MenuActivity.class);
		AVInstallation mAVInstallation = AVInstallation
				.getCurrentInstallation();
		// 设置更新对象标识
		mAVInstallation.put(C.USER.USER_NAME, username);
		// 异步保存
		mAVInstallation.saveInBackground(new SaveCallback() {
			@Override
			public void done(AVException e) {
				AVInstallation.getCurrentInstallation().saveInBackground();
			}
		});
	}*/



    /**
     * 获取用户资料
     *
     * @return
     */
    public static UserInfo getUserInfo(String username) {
        // 查询当前子菜单列表
        AVQuery<UserInfo> query = AVQuery.getQuery(UserInfo.class);
        query.whereEqualTo("user_name", username);
        // 按照更新时间降序排序
        query.orderByDescending("updatedAt");
        try {
            List<UserInfo> list = query.find();
            if (list != null && list.size() > 0) {
                return list.get(0);
            } else {
                return new UserInfo();
            }
        } catch (AVException exception) {
            return new UserInfo();
        }
    }



    /*
     * 获取用户资料
     *
     * @return
     */
    public static List<UserInfo> searchUserInfo(String content, int type) {

        // 查询当前子菜单列表
        AVQuery<UserInfo> query = AVQuery.getQuery(UserInfo.class);
        switch (type) {
            case 0:
                query.whereContains(UserInfo.USERNAME, content);
                break;
            case 1:
                query.whereContains(UserInfo.PHONE, content);
                break;
            default:
                break;
        }

        // 按照更新时间降序排序
        try {
            List<UserInfo> list = query.find();
            return list;
        } catch (AVException exception) {
            return null;
        }
    }


    /**
     * 创建或者更新用户信息
     *
     * @param objectId
     * @param userName
     * @param nickName
     * @param phone
     * @param saveCallback
     */
    public static void createOrUpdateUserInfo(String objectId, String userName,
                                              String nickName, String userType,String phone,
                                              String question,String answer,String pwd,
        SaveCallback saveCallback) {
        final UserInfo userInfo = new UserInfo();
        if (!TextUtils.isEmpty(objectId)) {
            // 如果存在objectId，保存会变成更新操作。
            userInfo.setObjectId(objectId);
        }
        userInfo.setPhone(phone);
        userInfo.setNickName(nickName);
        userInfo.setType(userType);
        userInfo.setUserName(userName);
        userInfo.setQuestion(question);
        userInfo.setAnswer(answer);
        userInfo.setPassword(pwd);

        // 异步保存
        userInfo.saveInBackground(saveCallback);
    }




}
