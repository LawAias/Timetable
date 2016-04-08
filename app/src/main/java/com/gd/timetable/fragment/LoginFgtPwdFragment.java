package com.gd.timetable.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.RequestPasswordResetCallback;
import com.gd.timetable.R;
import com.gd.timetable.activity.LoginActivity;
import com.gd.timetable.base.BaseSwitchFragment;
import com.gd.timetable.service.AVService;
import com.gd.timetable.util.SingleToast;


/**
 * 忘记密码界面
 * @author sjy
 *
 */
public class LoginFgtPwdFragment extends BaseSwitchFragment implements
		OnClickListener {

	private TextView mTxBack;
	private EditText emailText;
	private Button findPasswordButton;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_findpwd, container,
				false);

		mTxBack = (TextView)rootView
				.findViewById(R.id.rl_top_btn_back);
		emailText = (EditText) rootView
				.findViewById(R.id.editText_forget_email);
		findPasswordButton = (Button) rootView
				.findViewById(R.id.button_find_password);
		findPasswordButton.setOnClickListener(this);
		mTxBack.setOnClickListener(this);
		return rootView;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.rl_top_btn_back:
			mFragmentCallBack.fragmentCallBack(LoginActivity.JUMP_2_LOGIN, null);
			break;
		case R.id.button_find_password:
			String email = emailText.getText().toString();
			if (email != null) {
				RequestPasswordResetCallback callback = new RequestPasswordResetCallback() {
					public void done(AVException e) {
						if (e == null) {
							SingleToast.showToast(mAct,
									R.string.forget_password_send_email, 2000);
						} else {
							showError(mAct
									.getString(R.string.forget_password_email_error));
						}
					}
				};
				AVService.requestPasswordReset(email, callback);
			} else {
				showError(mAct.getResources().getString(
						R.string.error_register_email_address_null));
			}
			break;

		default:
			break;
		}

	}

	public void doFragmentBackPressed() {
		mFragmentCallBack.fragmentCallBack(LoginActivity.JUMP_2_LOGIN, null);
	};

}
