package com.gd.timetable;


import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.PushService;
import com.gd.timetable.activity.LoginActivity;
import com.gd.timetable.bean.NoteInfo;
import com.gd.timetable.bean.ScheduleInfo;
import com.gd.timetable.bean.UserInfo;
import com.gd.timetable.bean.WorkInfo;
import com.gd.timetable.db.DBProvider;
import com.gd.timetable.service.AVService;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 应用的入口 注册服务器信息
 * 
 * @author sjy
 * 
 */
public class GdApp extends Application {

	private static final String TAG = GdApp.class.getSimpleName();

	private AVUser currentUser;
	private String userId;
	private String userName;
	private UserInfo userInfo = null;

	public static Context ctx;

	/**
	 * 缓存部分数据
	 */
	List<WorkInfo> mWorkList = new ArrayList<>();

	List<NoteInfo> mNoteList= new ArrayList<>();

	/**
	 * 缓存部分数据
	 */
	List<ScheduleInfo> mScheduleList = new ArrayList<>();



	public List<WorkInfo> getWorkInfoList() {
		return mWorkList;
	}

	public List<NoteInfo> getNoteList() {
		return mNoteList;
	}

	public List<ScheduleInfo> getScheduleList() {
		return mScheduleList;
	}


	@Override
	public void onCreate() {
		super.onCreate();
		ctx = GdApp.this;

		// U need your AVOS key and so on to run the code.
		AVOSCloud.initialize(this,
				"NMbegCLLk2P2utrK8Rf84clJ-gzGzoHsz",
				"fGT4SLch3roePlJ8BJovIja5");

		// 注册子类
		AVObject.registerSubclass(UserInfo.class);
		AVObject.registerSubclass(WorkInfo.class);
		AVObject.registerSubclass(NoteInfo.class);

		refCurrUser();
		initImageLoader(GdApp.this);

		// 设置默认打开的 Activity
		PushService.setDefaultPushCallback(this, LoginActivity.class);
		// 订阅频道，当该频道消息到来的时候，打开对应的 Activity
		PushService.subscribe(this, "public", LoginActivity.class);

	}


	/**
	 * 初始化ImageLoader
	 */
	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
						//.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.build();
		ImageLoader.getInstance().init(config);
	}


	/**
	 * 更新当前用户信息
	 */
	public void refCurrUser() {
		currentUser = AVUser.getCurrentUser();
		if (currentUser != null) {
			userId = currentUser.getObjectId();
			userName = currentUser.getUsername();
			new getUserInfoTask().execute(userName);
		}
	}

	public AVUser getAVUser() {
		return currentUser;
	}
	
	public void setAVUser(AVUser avuser) {
		 currentUser = avuser ;
		 userId = currentUser.getObjectId();
		 userName = currentUser.getUsername();
	}

	/**
	 * 获取用户账号ObjectId
	 * 
	 * @return
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * 获取用户名
	 * 
	 * @return
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * 获取用户账号名字
	 * 
	 * @return
	 */
	public UserInfo getUserInfo() {
		return userInfo;
	}

	public void setUserInfo(UserInfo info,AVUser avuser) {
		userInfo = info;
		setAVUser(avuser);
	}

	// 远程获取用户数据
	private class getUserInfoTask extends AsyncTask<String, Void, UserInfo> {
		@Override
		protected UserInfo doInBackground(String... params) {
			UserInfo info = AVService.getUserInfo(params[0]);
			return info;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {

			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(UserInfo info) {
			// 说明认证成功,则校验账号格式是否正确
			if (info != null) {
				userInfo = info;
				setUserInfo(info, AVUser.getCurrentUser());
			} else {
				// 说明认证失败
			}
		}
	}


	/**
	 * 从数据库重新获取数据
	 */
	public void doRefreshSchedule() {
		Date dateDay = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		String dateStr = sdf.format(dateDay);
		List<ScheduleInfo> infos = DBProvider.getInstance(this)
				.getScheduleFromDB(dateStr);
		getScheduleList().clear();
		getScheduleList().addAll(infos);
	}


}
