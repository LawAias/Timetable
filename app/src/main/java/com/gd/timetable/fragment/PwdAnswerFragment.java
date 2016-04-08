package com.gd.timetable.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.UpdatePasswordCallback;
import com.gd.timetable.R;
import com.gd.timetable.base.BaseSwitchFragment;
import com.gd.timetable.util.SingleToast;


/**
 * 根据密保问题
 * 修改密码页面
 * @author sjy
 *
 */
public class PwdAnswerFragment extends BaseSwitchFragment {

//	private static final String TAG = PwdAnswerFragment.class.getSimpleName();

	private View rootView;

	private TextView mTxQuestion;
	private EditText mEdAnswer;
	private EditText mEdNew;
	private EditText mEdNewR;
	private Button mBtnModify;

	private static PwdAnswerFragment instance;

	public static PwdAnswerFragment getInstance() {
		if (instance == null) instance = new PwdAnswerFragment();
		return instance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_answer, container, false);

		mTxQuestion = (TextView)rootView.findViewById(R.id.tx_question_hint);

		mEdAnswer = (EditText)rootView.findViewById(R.id.et_pwd_answer);
		mEdNew = (EditText)rootView.findViewById(R.id.et_pwd_new);
		mEdNewR = (EditText)rootView.findViewById(R.id.et_pwd_new_repeat);
		mBtnModify = (Button)rootView.findViewById(R.id.btn_pwd_modify);

		mTxQuestion.setText("密保问题:"+"\n"+getUserInfo().getQuestion());

		mBtnModify.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String answerPwd = mEdAnswer.getEditableText().toString();
				String newPwd = mEdNew.getEditableText().toString();
				String newPwdR = mEdNewR.getEditableText().toString();

				if (!answerPwd.equals(getUserInfo().getAnswer())) {
					SingleToast.showToast(mAct, "密保问题回答错误!", 2000);
					return;
				}

				if (!newPwd.equals(newPwdR)) {
					SingleToast.showToast(mAct, "两次新密码不一致", 2000);
					return;
				}

					AVUser.getCurrentUser().updatePasswordInBackground(getUserInfo().getPassword(), newPwd, new UpdatePasswordCallback() {

						@Override
						public void done(AVException e) {
							if (e == null) {
								SingleToast.showToast(mAct, "密码修改成功", 2000);
								mEdAnswer.setText("");
								mEdNew.setText("");
								mEdNewR.setText("");
							} else {
								SingleToast.showToast(mAct, "密码修改失败:" + e.getMessage(), 2000);
							}
						}
					});
				}

		});

		return rootView;
	}

}
