package com.gd.timetable.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gd.timetable.util.C;
import com.gd.timetable.util.SingleToast;


/**
 * 提醒的广播接收器
 */
public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		//闹钟时间到，播放闹钟
		SingleToast.showToast(context, "课程提醒时间到了", 2000);
	
		Intent it = new Intent();
		it.putExtra(C.INTENT_TYPE.DATA_INFO,intent.getParcelableExtra(C.INTENT_TYPE.DATA_INFO));
		it.setAction("myaction.intent.alarmactivity");
		it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(it);

		abortBroadcast();   //Receiver接收到广播后中断广播

		
	}

}
