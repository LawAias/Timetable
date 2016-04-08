package com.gd.timetable.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SignUpCallback;
import com.gd.timetable.R;
import com.gd.timetable.activity.LoginActivity;
import com.gd.timetable.base.BaseSwitchFragment;
import com.gd.timetable.service.AVService;
import com.gd.timetable.util.LogTrace;


/**
 * 注册界面
 *
 * @author sjy
 */
public class LoginRegFragment extends BaseSwitchFragment implements
        OnClickListener {

    private Button mBtnReg;
    private EditText mEtUserName;
    private EditText mEtUserNickName;
    private EditText mEtUserEmail;
    private EditText mEtUserPhone;
    private EditText mEtUserPassword;
    private EditText mEtUserPasswordAgain;

    private EditText mEtUserQuestion;
    private EditText mEtUserAnswer;

    private TextView mTxBack;
    private ProgressDialog progressDialog;

    private String userName;
    private String userNickName;

    private String userType ="0";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_register, container,
                false);
        mBtnReg = (Button) rootView.findViewById(R.id.button_i_need_register);
        mEtUserName = (EditText) rootView
                .findViewById(R.id.editText_register_username);
        mEtUserNickName = (EditText) rootView
                .findViewById(R.id.editText_register_nickname);
        mEtUserEmail = (EditText) rootView
                .findViewById(R.id.editText_register_email);
        mEtUserPhone = (EditText) rootView
                .findViewById(R.id.editText_register_phone);
        mEtUserPassword = (EditText) rootView
                .findViewById(R.id.editText_register_userPassword);
        mEtUserPasswordAgain = (EditText) rootView
                .findViewById(R.id.editText_register_userPassword_again);
        mEtUserQuestion = (EditText) rootView
                .findViewById(R.id.editText_register_question);
        mEtUserAnswer = (EditText) rootView
                .findViewById(R.id.editText_register_answer);

        mTxBack = (TextView) rootView.findViewById(R.id.rl_top_btn_back);

        mBtnReg.setOnClickListener(this);
        mTxBack.setOnClickListener(this);

        RadioGroup group = (RadioGroup) rootView.findViewById(R.id.radioGroup);

        // 绑定一个匿名监听器
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup arg0, int arg1) {

                switch (arg0.getCheckedRadioButtonId()) {
                    case R.id.radioStud:
                        userType ="0";
                        break;
                    case R.id.radioManager:
                        userType = "1";
                        break;
                    default:
                        break;
                }
            }
        });



        return rootView;
    }

    // 开始注册
    public void register() {
        final String password = mEtUserPassword.getText().toString();
        final String email = mEtUserEmail.getText().toString();
        final String phone = mEtUserPhone.getText().toString();

        final String question = mEtUserQuestion.getText().toString();
        final String answer = mEtUserAnswer.getText().toString();

        SignUpCallback signUpCallback = new SignUpCallback() {
            public void done(AVException e) {
                progressDialogDismiss();
                if (e == null) {
                    // 注册成功
                    // 向服务器插入一条用户信息数据
                    AVService.createOrUpdateUserInfo("", userName,
                            userNickName, userType,phone,
                            question,answer,password,
                            null);
                    showRegisterSuccess();
                } else {

                    LogTrace.d(TAG,"error","e.getCode():"+e.getCode()+" "+e.getMessage());
                    switch (e.getCode()) {
                        case 202:
                            showError(mAct
                                    .getString(R.string.error_register_user_name_repeat));
                            break;
                        case 203:
                            showError(mAct
                                    .getString(R.string.error_register_email_repeat));
                            break;
                        case AVException.INVALID_EMAIL_ADDRESS:
                            showError(mAct
                                    .getString(R.string.error_register_email_invalid));
                            break;
                        case AVException.INVALID_PHONE_NUMBER:
                            showError(mAct
                                    .getString(R.string.error_register_phone_invalid));

                            break;
                        default:
                            showError(mAct.getString(R.string.network_error));
                            break;
                    }
                }
            }
        };


        userName = mEtUserName.getText().toString();
        userNickName = mEtUserNickName.getText().toString();

        AVService.signUp(userName, userNickName, password, email, phone,
                signUpCallback);
    }

    private void progressDialogDismiss() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    private void progressDialogShow() {
        progressDialog = ProgressDialog
                .show(mAct,
                        mAct.getResources().getText(
                                R.string.dialog_message_title),
                        mAct.getResources().getText(
                                R.string.dialog_text_wait), true, false);
    }

    private void showRegisterSuccess() {
        new AlertDialog.Builder(mAct)
                .setTitle(
                        mAct.getResources().getString(
                                R.string.dialog_message_title))
                .setMessage(
                        mAct.getResources().getString(
                                R.string.success_register_success))
                .setNegativeButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                                mFragmentCallBack.fragmentCallBack(
                                        LoginActivity.JUMP_2_LOGIN, null);
                            }
                        }).show();
    }

    // 进行注册前的格式验证
    private void doRegVerfy() {
        if (mEtUserPassword.getText().toString()
                .equals(mEtUserPasswordAgain.getText().toString())) {
            if (!mEtUserPassword.getText().toString().isEmpty()) {
                if (!mEtUserName.getText().toString().isEmpty()) {
                    if (!mEtUserEmail.getText().toString().isEmpty()) {
                        // 直接注册
                        progressDialogShow();
                        register();
                    } else {
                        showError(mAct
                                .getString(R.string.error_register_email_address_null));
                    }
                } else {
                    showError(mAct
                            .getString(R.string.error_register_user_name_null));
                }
            } else {
                showError(mAct
                        .getString(R.string.error_register_password_null));
            }
        } else {
            showError(mAct
                    .getString(R.string.error_register_password_not_equals));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_i_need_register:
                doRegVerfy();
                break;
            case R.id.rl_top_btn_back:
                AVService.logout();
                mFragmentCallBack
                        .fragmentCallBack(LoginActivity.JUMP_2_LOGIN, null);
                break;
            default:
                break;
        }
    }

    public void doFragmentBackPressed() {
        mFragmentCallBack.fragmentCallBack(LoginActivity.JUMP_2_LOGIN, null);
    }

    ;
}
