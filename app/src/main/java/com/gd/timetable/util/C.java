package com.gd.timetable.util;

import android.os.Environment;


public final class C {

	public static final class SP {
	// 记住密码
	public static final String remember_login = "shared_remember_login";
	// 记住密码：账号
	public static final String account = "shared_key_accout";
	// 记住密码：密码
	public static final String pwd = "shared_key_pwd";
	}
	
	public static final class INTENT_TYPE {
		//数据修改模式(int) 0:增加 1:删除 2:修改 3：查看
		public static final String DATA_DATATYPE = "data_datatype";
		//更新的对象信息
		public static final String DATA_OBJ = "data_obj";

		public static final String DATA_INFO = "data_info";
	}
	
	public static final class NORMAL {

		public static final String PIC_PATH_DIR = Environment.getExternalStorageDirectory()
				+ "/GD/Camera/";// 图片存储路径
	}

	public static final class AVFILE_NAME {

		public static final String PIC = "Pic";

	}
	
	

	public static final class Push {
		//通知消息
		public static final String ACTION_NOTE = "com.gd.note.action";
	}


}
