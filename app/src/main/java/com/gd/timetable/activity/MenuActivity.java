package com.gd.timetable.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gd.timetable.R;
import com.gd.timetable.base.BaseCompatActivity;
import com.gd.timetable.bean.NoteInfo;
import com.gd.timetable.bean.ScheduleInfo;
import com.gd.timetable.bean.WorkInfo;
import com.gd.timetable.fragment.AboutFragment;
import com.gd.timetable.fragment.NoteFragment;
import com.gd.timetable.fragment.PersonFragment;
import com.gd.timetable.fragment.PwdAnswerFragment;
import com.gd.timetable.fragment.PwdModifyFragment;
import com.gd.timetable.fragment.ScheduleFragment;
import com.gd.timetable.fragment.WorkFragment;
import com.gd.timetable.service.AVService;
import com.gd.timetable.util.C;
import com.gd.timetable.util.LogTrace;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MenuActivity extends BaseCompatActivity {

    private static final String TAG = MenuActivity.class.getSimpleName();

    //save our header or result
    private Drawer result = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Handle Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);


        PrimaryDrawerItem orderItem;
        AccountHeader headerResult;



        IProfile profile;

        if(getUserInfo().getType().equals("0")){
            //普通用户
            profile = new ProfileDrawerItem().withName("学生用户:" + mApp.getUserInfo().getUserName());
            if(TextUtils.isEmpty(getUserInfo().getAvatarUrl())){
                profile.withIcon(R.drawable.default_user_avatar);
            }else{
                LogTrace.d(TAG,"avatar","url:"+getUserInfo().getAvatarUrl());
                profile.withIcon(ImageLoader.getInstance().loadImageSync(getUserInfo().getAvatarUrl()));
            }
            // Create the AccountHeader
            headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.header)
                    .addProfiles(profile)
                    .build();
            result = new DrawerBuilder(this)
                    //this layout have to contain child layouts
                    .withRootView(R.id.drawer_container)
                    .withToolbar(toolbar)
                    .withDisplayBelowStatusBar(false)
                    .withActionBarDrawerToggleAnimated(true)
                    .withAccountHeader(headerResult)
                    .addDrawerItems(
                            new PrimaryDrawerItem().withName(R.string.drawer_timetable).withIcon(FontAwesome.Icon.faw_table).withIdentifier(0),
                            new PrimaryDrawerItem().withName(R.string.drawer_user_job).withIcon(FontAwesome.Icon.faw_file_word_o).withIdentifier(4),
                            new PrimaryDrawerItem().withName(R.string.drawer_user_note).withIcon(FontAwesome.Icon.faw_sticky_note).withIdentifier(2),
                            new PrimaryDrawerItem().withName(R.string.drawer_personal).withIcon(FontAwesome.Icon.faw_info).withIdentifier(6),
                            new PrimaryDrawerItem().withName(R.string.drawer_modify_pwd).withIcon(FontAwesome.Icon.faw_eye).withIdentifier(7),
                            new PrimaryDrawerItem().withName(R.string.drawer_answer_pwd).withIcon(FontAwesome.Icon.faw_question).withIdentifier(3),
            new PrimaryDrawerItem().withName(R.string.drawer_app_about).withIcon(FontAwesome.Icon.faw_star).withIdentifier(8),
                            new PrimaryDrawerItem().withName(R.string.drawer_item_loginout).withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(9)
                    ).withOnDrawerItemClickListener(mOnDrawerItemClickListener)
                    .withSavedInstance(savedInstanceState)
                    .withSelectedItem(4)
                    .build();


        }else{
            //管理者
            profile = new ProfileDrawerItem().withName("教师用户:" + mApp.getUserInfo().getUserName());
            if(TextUtils.isEmpty(getUserInfo().getAvatarUrl())){
                profile.withIcon(R.drawable.default_user_avatar);
            }else{
                LogTrace.d(TAG,"avatar","url:"+getUserInfo().getAvatarUrl());
                profile.withIcon(ImageLoader.getInstance().loadImageSync(getUserInfo().getAvatarUrl()));
            }

            // Create the AccountHeader
            headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.header)
                    .addProfiles(profile)
                    .build();
            //Create the drawer
            result = new DrawerBuilder(this)
                    //this layout have to contain child layouts
                    .withRootView(R.id.drawer_container)
                    .withToolbar(toolbar)
                    .withDisplayBelowStatusBar(false)
                    .withActionBarDrawerToggleAnimated(true)
                    .withAccountHeader(headerResult)
                    .addDrawerItems(
                            new PrimaryDrawerItem().withName(R.string.drawer_timetable).withIcon(FontAwesome.Icon.faw_table).withIdentifier(0),
                            new PrimaryDrawerItem().withName(R.string.drawer_user_job).withIcon(FontAwesome.Icon.faw_hand_o_up).withIdentifier(4),
                            new PrimaryDrawerItem().withName(R.string.drawer_user_note).withIcon(FontAwesome.Icon.faw_sticky_note).withIdentifier(2),
                            new PrimaryDrawerItem().withName(R.string.drawer_personal).withIcon(FontAwesome.Icon.faw_info).withIdentifier(6),
                            new PrimaryDrawerItem().withName(R.string.drawer_modify_pwd).withIcon(FontAwesome.Icon.faw_eye).withIdentifier(7),
                            new PrimaryDrawerItem().withName(R.string.drawer_answer_pwd).withIcon(FontAwesome.Icon.faw_question).withIdentifier(3),
                            new PrimaryDrawerItem().withName(R.string.drawer_app_about).withIcon(FontAwesome.Icon.faw_star).withIdentifier(8),
                            new PrimaryDrawerItem().withName(R.string.drawer_item_loginout).withIcon(FontAwesome.Icon.faw_sign_out).withIdentifier(9)
                    ).withOnDrawerItemClickListener(mOnDrawerItemClickListener)
                    .withSavedInstance(savedInstanceState)
                    .withSelectedItem(4)
                    .build();

        }

        getSupportActionBar().setTitle(R.string.drawer_user_job);
        changeFragment(WorkFragment.getInstance());
    }

    Drawer.OnDrawerItemClickListener mOnDrawerItemClickListener = new Drawer.OnDrawerItemClickListener() {
        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

            switch ((int) drawerItem.getIdentifier()) {
                case 0:
                    //今日课表（本地数据库）
                    getSupportActionBar().setTitle(R.string.drawer_timetable);
                    changeFragment(ScheduleFragment.getInstance());
                    break;
                case 2:
                    //用户笔记
                    getSupportActionBar().setTitle(R.string.drawer_user_note);
                    changeFragment(NoteFragment.getInstance());
                    break;
                case 4:
                    //作业模块(云端数据库)
                    getSupportActionBar().setTitle(R.string.drawer_user_job);
                    changeFragment(WorkFragment.getInstance());
                    break;
                case 6:
                    //修改个人资料
                    getSupportActionBar().setTitle(R.string.drawer_personal);
                    changeFragment(PersonFragment.getInstance());
                    break;
                case 7:
                    //修改密码
                    getSupportActionBar().setTitle(R.string.drawer_modify_pwd);
                    changeFragment(PwdModifyFragment.getInstance());
                    break;
                case 3:
                    //密保找回密码
                    getSupportActionBar().setTitle(R.string.drawer_answer_pwd);
                    changeFragment(PwdAnswerFragment.getInstance());
                    break;
                case 8:
                    //关于
                    getSupportActionBar().setTitle(R.string.drawer_app_about);
                    changeFragment(AboutFragment.getInstance());
                    break;
                case 9:
                    //退出应用
                    showExitDialog();
                    break;

            }

            return false;
        }
    };


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            exit();
            return;
        }
    }

    /**
     * 退出时间
     */
    private long mExitTime;
    /**
     * 退出间隔
     */
    private static final int INTERVAL = 2000;
    /**
     * 判断两次返回时间间隔,小于两秒则退出程序
     */
    private void exit() {
        if (System.currentTimeMillis() - mExitTime > INTERVAL) {
            Toast.makeText(this, "再按一次返回键,可直接退出程序", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(0);
        }
    }

    /**
     * 切换Fragment
     *
     * @param targetFragment
     */
    private void changeFragment(Fragment targetFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public void showExitDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.loginout_info)
                .positiveText(android.R.string.ok).onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(MaterialDialog dialog, DialogAction which) {
                logout();
            }
        }).negativeText(android.R.string.cancel).show();
    }

    /**
     * 登出
     */
    private void logout() {
        AVService.logout();
        Intent loginIntent = new Intent(this, LoginActivity.class);
        loginIntent.putExtra("type", "loginout");
        startActivity(loginIntent);
        finish();
        overridePendingTransition(R.anim.slide_in_bottom,
                R.anim.slide_out_top);
    }

    /**
     * helper class to start the new detailActivity animated
     *
     * @param workInfo
     * @param appIcon
     */
    public void animateWorkActivity(WorkInfo workInfo, int dataType, View menu, View appIcon) {
        Intent intent = new Intent(this, WorkDetailActivity.class);
        intent.putExtra(C.INTENT_TYPE.DATA_DATATYPE, dataType);
        intent.putExtra(C.INTENT_TYPE.DATA_OBJ, workInfo);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create(menu, "fab_normal"), Pair.create(appIcon, "notetypeImg"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            startActivity(intent, transitionActivityOptions.toBundle());
        }
    }

    public void animateWorkActivity(WorkInfo workInfo, int dataType) {
        Intent intent = new Intent(this, WorkDetailActivity.class);
        intent.putExtra(C.INTENT_TYPE.DATA_DATATYPE, dataType);
        intent.putExtra(C.INTENT_TYPE.DATA_OBJ, workInfo);
        startActivity(intent);

    }

    public void animateNoteActivity(NoteInfo noteInfo, int dataType, View menu, View appIcon) {
        Intent intent = new Intent(this, NoteDetailActivity.class);
        intent.putExtra(C.INTENT_TYPE.DATA_DATATYPE, dataType);
        intent.putExtra(C.INTENT_TYPE.DATA_OBJ, noteInfo);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this, Pair.create(menu, "fab_normal"), Pair.create(appIcon, "notetypeImg"));
        startActivity(intent, transitionActivityOptions.toBundle());
    }

    /**
     * 跳转到详情界面
     * @param info
     * @param dataType
     */
    public void animateDetailActivity(ScheduleInfo info, int dataType) {
        Intent intent = new Intent(this, ScheduleDetailActivity.class);
        intent.putExtra(C.INTENT_TYPE.DATA_DATATYPE, dataType);
        intent.putExtra(C.INTENT_TYPE.DATA_OBJ, info);
        startActivity(intent);
    }

}
