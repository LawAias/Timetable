package com.gd.timetable.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gd.timetable.R;
import com.gd.timetable.bean.ScheduleInfo;
import com.gd.timetable.service.AlarmService;
import com.gd.timetable.util.C;
import com.gd.timetable.util.LogTrace;
import com.gd.timetable.util.SingleToast;


/**
 * 闹钟提醒界面
 */
public class AlarmActivity extends Activity {

	private static final String TAG = AlarmActivity.class.getSimpleName();

	Button mCfmBtn;
	TextView mTextContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activit_alarm);
		startMyService();



		Intent intent = getIntent();
		ScheduleInfo scheduleInfo = intent.getParcelableExtra(C.INTENT_TYPE.DATA_INFO);

		StringBuilder sb = new StringBuilder();
		sb.append("课程名称:").append(scheduleInfo.getName()).append("\n")
				.append("时间:").append(scheduleInfo.getDate()).append("\n")
				.append("地址:").append(scheduleInfo.getPlace()).append("\n")
				.append("任课教师:").append(scheduleInfo.getTeacher()).append("\n");
		String hintTx = sb.toString();
		LogTrace.d(TAG, "onCreate","hintTx:" + hintTx);
		mCfmBtn = (Button) findViewById(R.id.notify_btn_cfm);
		mTextContent = (TextView) findViewById(R.id.notify_txt);

		mTextContent.setText(hintTx);
		mCfmBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				/* 终止service */
				stopMyService(1);
				AlarmActivity.this.finish();
			}
		});

	}
	
	public void startMyService() {
		try {
			/* 先终止之前可能还在运行的service */
			stopMyService(0);
			/* 启动MyService */
			Intent intent = new Intent(AlarmActivity.this, AlarmService.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startService(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void stopMyService(int flag) {
		try {
			/* 停止MyService */
			Intent intent = new Intent(AlarmActivity.this, AlarmService.class);
			stopService(intent);
			if (flag == 1) {
				SingleToast.showToast(AlarmActivity.this, "已停止提醒", 1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

}
