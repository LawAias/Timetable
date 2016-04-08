package com.gd.timetable.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.gd.timetable.GdApp;
import com.gd.timetable.R;
import com.gd.timetable.bean.UserInfo;


/**
 * 用于Activity内部切换的fragment
 * 定义了一个回调用来通知宿主FragmentActivity执行相关处理
 * @author sjy
 *
 */
public class BaseSwitchFragment extends Fragment {

	protected GdApp mApp;
	protected Activity mAct;
	protected String TAG = BaseSwitchFragment.class.getSimpleName();


	/** Called when the mAct is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAct= getActivity();
		mApp = (GdApp) mAct.getApplication();
	}

	public UserInfo getUserInfo(){
		return mApp.getUserInfo();
	}

	/**
	 * 是否是管理者
	 * @return
	 */
	public boolean isManager(){
		return getUserInfo().getType().equals("1");
	}


	/**
	 * 处理在Fragment上的事件回调
	 * 分发到宿主FragmentActivity进行处理
	 * @author sjy
	 *
	 */
	public interface FragmentCallBack {
		void fragmentCallBack(int targetID, Bundle data);
	}
	
	protected FragmentCallBack mFragmentCallBack;
	
	public BaseSwitchFragment newInstance(FragmentCallBack mFragmentCallBack){
		this.mFragmentCallBack =mFragmentCallBack;
		return this;
	}
	
	public void doFragmentBackPressed(){
		
	}

	protected void showError(String errorMessage) {
		showError(errorMessage, mAct);
	}

	public void showError(String errorMessage, Activity activity) {
		new AlertDialog.Builder(activity)
				.setTitle(
						activity.getResources().getString(
								R.string.dialog_message_title))
				.setMessage(errorMessage)
				.setNegativeButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
												int which) {
								dialog.dismiss();
							}
						}).show();
	}

}
