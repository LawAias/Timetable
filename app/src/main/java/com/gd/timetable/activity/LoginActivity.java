package com.gd.timetable.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.gd.timetable.R;
import com.gd.timetable.base.BaseSwitchFragment;
import com.gd.timetable.fragment.LoginFgtPwdFragment;
import com.gd.timetable.fragment.LoginFragment;
import com.gd.timetable.fragment.LoginRegFragment;


/**
 * 登陆界面: 包括登陆、注册、找回密码等子界面
 */
public class LoginActivity extends FragmentActivity implements BaseSwitchFragment.FragmentCallBack {

	@SuppressWarnings("unused")
	private static final String TAG = LoginActivity.class.getSimpleName();

	// 跳转到登录
	public static final int JUMP_2_LOGIN = 0X988;
	// 跳转到注册
	public static final int JUMP_2_REG = JUMP_2_LOGIN + 1;
	// 跳转到忘记密码
	public static final int JUMP_2_FRG = JUMP_2_REG + 1;
	
	//当前所在的Fragment
	private BaseSwitchFragment mSwitchFragment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		if (savedInstanceState == null) {
			
			mSwitchFragment = new LoginFragment().newInstance(this);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, mSwitchFragment)
					.commit();
		}
	}

	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void doRegister(View view) {
		changeFragment(new LoginFragment().newInstance(this),
				R.anim.push_left_in, R.anim.fade_out);
	}

	/**
	 * 找回密码
	 * 
	 * @param view
	 */
	public void doFindPwd(View view) {
		changeFragment(
				new LoginFgtPwdFragment().newInstance(this),
				R.anim.push_left_in, R.anim.fade_out);
	}

	private void changeFragment(BaseSwitchFragment targetFragment,
			int showAnim, int fadeAnim) {
		getSupportFragmentManager()
				.beginTransaction()
				.setCustomAnimations(showAnim, fadeAnim, R.anim.push_left_in,
						R.anim.push_left_out)
				.replace(R.id.container, targetFragment, "fragment")
				.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
				.commit();
		mSwitchFragment = targetFragment;
	}

	@Override
	public void fragmentCallBack(int targetID, Bundle data) {
		switch (targetID) {
		case JUMP_2_LOGIN:
			changeFragment(new LoginFragment().newInstance(this), R.anim.fade_in,
					R.anim.push_left_out);
			break;
		case JUMP_2_REG:
			changeFragment(
					new LoginRegFragment().newInstance(this), R.anim.push_left_in,
					R.anim.fade_out);
			break;
		case JUMP_2_FRG:
			changeFragment(
					new LoginFgtPwdFragment().newInstance(this), R.anim.push_left_in,
					R.anim.fade_out);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		mSwitchFragment.doFragmentBackPressed();
	}

}
