package com.gd.timetable.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.gd.timetable.R;
import com.gd.timetable.activity.LoginActivity;
import com.gd.timetable.activity.MenuActivity;
import com.gd.timetable.base.BaseSwitchFragment;
import com.gd.timetable.bean.UserInfo;
import com.gd.timetable.service.AVService;
import com.gd.timetable.util.C;
import com.gd.timetable.util.SPUtils;
import com.gd.timetable.util.SingleToast;


/**
 * 登录界面
 *
 */
public class LoginFragment extends BaseSwitchFragment implements OnClickListener {

    private Button mBtnLogin;
    private TextView mBtnReg;
    private TextView mBtnFrgpwd;
    private EditText mEtUserName;
    private EditText mEtPwd;
    private ProgressDialog mPrgDialog;
    private CheckBox mCkRemberPwd;

    private int back = 0;// 判断按几次back

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container,
                false);

        mBtnLogin = (Button) rootView.findViewById(R.id.button_login);
        mBtnReg = (TextView) rootView.findViewById(R.id.button_register);
        mBtnFrgpwd = (TextView) rootView
                .findViewById(R.id.button_forget_password);
        mEtUserName = (EditText) rootView
                .findViewById(R.id.editText_userName);
        mEtPwd = (EditText) rootView
                .findViewById(R.id.editText_userPassword);
        mCkRemberPwd = (CheckBox) rootView.findViewById(R.id.cb_rememberpwd);

        mCkRemberPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                SPUtils.setParam(mAct, C.SP.remember_login, arg1);
            }
        });

        Boolean isRemember = (Boolean) SPUtils.getParam(mAct,
                C.SP.remember_login, true);
        mCkRemberPwd.setChecked(isRemember);
        if (isRemember) {
            String userAccount = (String) SPUtils.getParam(mAct,
                    C.SP.account, "");
            String userPassword = (String) SPUtils.getParam(mAct, C.SP.pwd,
                    "");

            if (!TextUtils.isEmpty(userAccount)
                    && !TextUtils.isEmpty(userPassword)) {
                mEtUserName.setText(userAccount);
                mEtPwd.setText(userPassword);
            }
        }
        mBtnLogin.setOnClickListener(this);
        mBtnReg.setOnClickListener(this);
        mBtnFrgpwd.setOnClickListener(this);
        mAct.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        return rootView;

    }


    // 隐藏进度框
    private void progressDialogDismiss() {
        if (mPrgDialog != null)
            mPrgDialog.dismiss();
    }

    // 展示进度框
    private void progressDialogShow() {
        mPrgDialog = ProgressDialog
                .show(mAct,
                        mAct.getResources().getText(
                                R.string.dialog_message_title),
                        mAct.getResources().getText(
                                R.string.dialog_text_wait), true, false);
    }

    // 显示登陆错误对话框
    private void showLoginError() {
        new AlertDialog.Builder(mAct)
                .setTitle(
                        mAct.getResources().getString(
                                R.string.dialog_error_title))
                .setMessage(
                        mAct.getResources().getString(
                                R.string.error_login_error))
                .setNegativeButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    // 显示密码为空对话框
    private void showUserPasswordEmptyError() {
        new AlertDialog.Builder(mAct)
                .setTitle(
                        mAct.getResources().getString(
                                R.string.dialog_error_title))
                .setMessage(
                        mAct.getResources().getString(
                                R.string.error_register_password_null))
                .setNegativeButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    // 显示用户名为空对话框
    private void showUserNameEmptyError() {
        new AlertDialog.Builder(mAct)
                .setTitle(
                        mAct.getResources().getString(
                                R.string.dialog_error_title))
                .setMessage(
                        mAct.getResources().getString(
                                R.string.error_register_user_name_null))
                .setNegativeButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    public void doFragmentBackPressed() {
        back++;
        switch (back) {
            case 1:
                SingleToast.showToast(mAct, R.string.exit_again, 3000);
                // 3秒内都可以退出应用
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        back = 0;
                    }
                }, 3000);
                break;
            case 2:
                back = 0;
                if (TextUtils.isEmpty(mEtUserName.getText())
                        || TextUtils.isEmpty(mEtPwd.getText())) {
                    SPUtils.setParam(mApp, C.SP.remember_login, false);
                }
                mAct.finish();
                android.os.Process.killProcess(android.os.Process.myPid());// 关闭进程
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_login:
                String username = mEtUserName.getText().toString();
                if (username.isEmpty()) {
                    showUserNameEmptyError();
                    return;
                }
                String pwd = mEtPwd.getText().toString();
                if (pwd.isEmpty()) {
                    showUserPasswordEmptyError();
                    return;
                }

                progressDialogShow();
                // 进行登录验证
                AVUser.logInInBackground(username, pwd, new LogInCallback<AVUser>() {
                    public void done(AVUser user, AVException e) {
                        if (user != null) {
                            mApp.refCurrUser();
                            // 验证成功获取下用户信息
                            new getUserInfoTask().execute(mEtUserName.getText().toString());

                        } else {
                            // 提示错误
                            progressDialogDismiss();
                            showLoginError();
                        }
                    }
                });
                break;
            case R.id.button_register:
                mFragmentCallBack.fragmentCallBack(LoginActivity.JUMP_2_REG, null);
                break;
            case R.id.button_forget_password:
                mFragmentCallBack.fragmentCallBack(LoginActivity.JUMP_2_FRG, null);
                break;
            default:
                break;
        }

    }


    // 远程获取用户数据
    private class getUserInfoTask extends AsyncTask<String, Void, UserInfo> {
        @Override
        protected UserInfo doInBackground(String... params) {
            UserInfo info = AVService.getUserInfo(params[0]);
            return info;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {

            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(UserInfo info) {
            // 说明认证成功,则校验账号格式是否正确
            if (info != null) {
                mApp.refCurrUser();
                mApp.setUserInfo(info, AVUser.getCurrentUser());
                //进入主界面
                if (mCkRemberPwd.isChecked()) {
                    SPUtils.setParam(mAct, C.SP.account,
                            mEtUserName.getText().toString());
                    SPUtils.setParam(mAct, C.SP.pwd,
                            mEtPwd.getText().toString());
                } else {
                    SPUtils.setParam(mAct, C.SP.account, "");
                    SPUtils.setParam(mAct, C.SP.pwd, "");
                }
                progressDialogDismiss();
                Intent  mainIntent = new Intent(mAct,
                        MenuActivity.class);
                startActivity(mainIntent);
                mAct.finish();

            } else {
                // 说明认证失败
            }
        }
    }
}
