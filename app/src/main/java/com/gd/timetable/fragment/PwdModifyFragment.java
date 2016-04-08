package com.gd.timetable.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.gd.timetable.R;
import com.gd.timetable.base.BaseSwitchFragment;
import com.gd.timetable.util.SingleToast;


/**
 * 修改密码页面
 * @author sjy
 *
 */
public class PwdModifyFragment extends BaseSwitchFragment {

//	private static final String TAG = PwdModifyFragment.class.getSimpleName();

	private View rootView;

	private EditText mEdOld;
	private EditText mEdNew;
	private EditText mEdNewR;
	private Button mBtnModify;

	private static PwdModifyFragment instance;

	public static PwdModifyFragment getInstance() {
		if (instance == null) instance = new PwdModifyFragment();
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_modify_pwd, container, false);

		mEdOld = (EditText)rootView.findViewById(R.id.et_pwd_old);
		mEdNew = (EditText)rootView.findViewById(R.id.et_pwd_new);
		mEdNewR = (EditText)rootView.findViewById(R.id.et_pwd_new_repeat);
		mBtnModify = (Button)rootView.findViewById(R.id.btn_pwd_modify);

		mBtnModify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String oldPwd = mEdOld.getEditableText().toString();
				String newPwd = mEdNew.getEditableText().toString();
				String newPwdR = mEdNewR.getEditableText().toString();
				if(!newPwd.equals(newPwdR)){
					SingleToast.showToast(mAct,"两次新密码不一致",2000);
				}else{

					AVUser.getCurrentUser().updatePasswordInBackground(oldPwd, newPwd, new UpdatePasswordCallback() {

						@Override
						public void done(AVException e) {
							if(e ==null){
								SingleToast.showToast(mAct,"密码修改成功",2000);
								mEdOld.setText("");
								mEdNew.setText("");
								mEdNewR.setText("");
							}else{
								SingleToast.showToast(mAct,"密码修改失败:"+e.getMessage(),2000);
							}
						}
					});
				}
			}
		});

		return rootView;
	}

}
