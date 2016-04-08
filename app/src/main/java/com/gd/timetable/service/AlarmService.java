package com.gd.timetable.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import com.gd.timetable.R;


public class AlarmService extends Service {
	private MediaPlayer myPlayer;

	@Override
	public void onCreate() {
		
		try {
			/* Create MediaPlayer */
			myPlayer = new MediaPlayer();
			myPlayer = MediaPlayer.create(AlarmService.this, R.raw.music);
				/* 播放铃声 */
				if (!myPlayer.isPlaying()) {
					myPlayer.seekTo(0);
					myPlayer.setLooping(true);
					myPlayer.start();
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		try {
			/* Service关闭时释放MediaPlayer */
			myPlayer.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
