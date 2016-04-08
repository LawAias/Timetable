package com.gd.timetable.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.gd.timetable.GdApp;
import com.gd.timetable.R;
import com.gd.timetable.base.BaseCompatActivity;
import com.gd.timetable.bean.ScheduleInfo;
import com.gd.timetable.db.DBProvider;
import com.gd.timetable.receiver.AlarmReceiver;
import com.gd.timetable.util.C;
import com.gd.timetable.util.LogTrace;
import com.gd.timetable.util.SingleToast;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 当日课程详情界面
 */
public class ScheduleDetailActivity extends BaseCompatActivity implements View.OnClickListener,
         RadialTimePickerDialogFragment.OnTimeSetListener{

    private static final String TAG = ScheduleDetailActivity.class.getSimpleName();

    private TextView mTvTitle;
    //标题
    private EditText mEtTitle;
    //时间
    private EditText mEtTime;
    //地址
    private EditText mEtAddress;
    //教师
    private EditText mEtTeacher;
    //内容
    private EditText mEtContent;

    private ScheduleInfo mInfo = null;
    //传输过来的数据处理类型
    private int dataType;

    GdApp mApp;

    FloatingActionButton mFab;

    FloatingActionMenu mFabMenu;

    AlarmManager mAlarmManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_schedule_detail);
        mApp = (GdApp)getApplication();
        initViews();
        initStatus();
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Handle Back Navigation :D
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScheduleDetailActivity.this.onBackPressed();
            }
        });


        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mEtTitle = (EditText) findViewById(R.id.et_title);
        mEtTime = (EditText) findViewById(R.id.et_time);
        mEtAddress = (EditText) findViewById(R.id.et_address);
        mEtTeacher = (EditText) findViewById(R.id.et_teacher);
        mEtContent = (EditText) findViewById(R.id.et_content);
        // Fab Button
        mFab = (FloatingActionButton) findViewById(R.id.fab_normal);
        mFabMenu  = (FloatingActionMenu) findViewById(R.id.fab_menu);


    }

    private void initStatus() {

        Intent it = getIntent();
        //增删改查
        dataType = it.getIntExtra(C.INTENT_TYPE.DATA_DATATYPE, 0);
        mInfo = it.getParcelableExtra(C.INTENT_TYPE.DATA_OBJ);
        switch (dataType) {
            case 0://增
                LogTrace.d(TAG, "initStatus", "id:" + mInfo.getId());
                getSupportActionBar().setTitle(R.string.schedule_add);
                mEtTitle.setEnabled(true);
                mEtTime.setEnabled(true);
                mEtTitle.setEnabled(true);

                mEtAddress.setEnabled(true);

                mEtContent.setEnabled(true);
                mEtTime.setOnClickListener(this);

                mEtTime.setFocusable(false);

                if(getUserInfo().getType().equals("0")){
                    //普通用户
                    mEtTeacher.setEnabled(true);
                }else{
                    //教师
                    mEtTeacher.setText(getUserInfo().getNickName());
                    mEtTeacher.setEnabled(false);
                }


                mFab.setImageDrawable(new IconicsDrawable(this,
                        GoogleMaterial.Icon.gmd_file_upload).color(Color.WHITE).actionBar());
                mFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        mInfo.setName(mEtTitle.getEditableText().toString());
                        mInfo.setContent(mEtContent.getEditableText().toString());
                        mInfo.setPlace(mEtAddress.getEditableText().toString());
                        mInfo.setTeacher(mEtTeacher.getEditableText().toString());

                        if (TextUtils.isEmpty(mInfo.getName()) ||
                                TextUtils.isEmpty(mInfo.getTime()) ||
                                TextUtils.isEmpty(mInfo.getPlace()) ||
                                TextUtils.isEmpty(mInfo.getTeacher()) ||
                                TextUtils.isEmpty(mInfo.getContent())) {
                            SingleToast.showToast(getApplicationContext(), "请补全课程信息!",
                                    2000);
                        } else {
                            DBProvider.getInstance(getApplicationContext()).insertScheduleInfo(mInfo);
                            SingleToast.showToast(getApplicationContext(), "添加课程成功!",
                                    2000);
                            //添加日程后  记录一则闹钟提醒
                            Intent intent = new Intent(ScheduleDetailActivity.this, AlarmReceiver.class);
                            intent.putExtra(C.INTENT_TYPE.DATA_INFO, mInfo);

                            PendingIntent sender = PendingIntent.getBroadcast(ScheduleDetailActivity.this, 0, intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT);

                            Calendar calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(System.currentTimeMillis());
                            calendar.setTimeZone(TimeZone.getTimeZone("GMT+8")); // 这里时区需要设置一下，不然会有8个小时的时间差
                            calendar.set(Calendar.HOUR_OF_DAY, mHour);
                            calendar.set(Calendar.MINUTE, mMinute);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                                    calendar.getTimeInMillis(), sender);


                        }
                    }
                });
                break;
            case 1://删
                break;
            case 2://改
                getSupportActionBar().setTitle(R.string.schedule_update);
                mEtTitle.setEnabled(true);
                mEtTime.setEnabled(true);
                mEtTitle.setEnabled(true);
                mEtContent.setEnabled(true);
                mEtTitle.setText(mInfo.getName());
                mEtTime.setText(mInfo.getTime());
                mEtTeacher.setText(mInfo.getTeacher());
                mEtAddress.setText(mInfo.getPlace());
                mEtContent.setText(mInfo.getContent());
                mEtTime.setOnClickListener(this);
                mEtTime.setFocusable(false);

                if(getUserInfo().getType().equals("0")){
                    //普通用户
                    mEtTeacher.setEnabled(true);
                }else{
                    //教师
                    mEtTeacher.setEnabled(false);
                }


                mFab.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_mode_edit).color(Color.WHITE).actionBar());
                mFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DBProvider.getInstance(getApplicationContext()).updateNoteInfo(mInfo);
                        SingleToast.showToast(getApplicationContext(), "更新课程成功!",
                                2000);
                        mApp.doRefreshSchedule();

                        LogTrace.d(TAG,"update ","mHour:"+mHour+"  mMinute:"+mMinute);


                        if(mHour==-1||mMinute==-1){
                            return;
                        }
                        //更新日程后  记录一则闹钟提醒
                        Intent intent = new Intent(ScheduleDetailActivity.this, AlarmReceiver.class);
                        intent.putExtra(C.INTENT_TYPE.DATA_INFO, mInfo);

                        PendingIntent sender = PendingIntent.getBroadcast(ScheduleDetailActivity.this, 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8")); // 这里时区需要设置一下，不然会有8个小时的时间差
                        calendar.set(Calendar.HOUR_OF_DAY, mHour);
                        calendar.set(Calendar.MINUTE, mMinute);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                                calendar.getTimeInMillis(), sender);
                    }
                });
                break;
            case 3://查
                getSupportActionBar().setTitle(R.string.schedule_qry);
                mFab.setVisibility(View.GONE);
                initFabMenu();
                mEtTitle.setText(mInfo.getName());
                mEtTime.setText(mInfo.getTime());
                mEtTeacher.setText(mInfo.getTeacher());
                mEtAddress.setText(mInfo.getPlace());
                mEtContent.setText(mInfo.getContent());
                mEtTitle.setEnabled(false);
                mEtTime.setEnabled(false);
                mEtTeacher.setEnabled(false);
                mEtAddress.setEnabled(false);
                mEtContent.setEnabled(false);
                break;
        }
    }

    private void initFabMenu(){
        mFabMenu.setVisibility(View.VISIBLE);
        mFab.setVisibility(View.GONE);
        FloatingActionButton fbQry = new FloatingActionButton(this);
        fbQry.setButtonSize(FloatingActionButton.SIZE_MINI);
        fbQry.setLabelText(getString(R.string.schedule_update));
        fbQry.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_edit).color(Color.WHITE).actionBar());
        fbQry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleDetailActivity.this, ScheduleDetailActivity.class);
                intent.putExtra(C.INTENT_TYPE.DATA_DATATYPE, dataType);
                intent.putExtra(C.INTENT_TYPE.DATA_OBJ, mInfo);
                startActivity(intent);
                ScheduleDetailActivity.this.finish();
            }
        });
        mFabMenu.addMenuButton(fbQry);

        FloatingActionButton fbDel = new FloatingActionButton(this);
        fbDel.setButtonSize(FloatingActionButton.SIZE_MINI);
        fbDel.setLabelText(getString(R.string.schedule_del));
        fbDel.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_delete).color(Color.WHITE).actionBar());
        fbDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除笔记
                new MaterialDialog.Builder(ScheduleDetailActivity.this).content(R.string.dialog_del_title)
                        .positiveText(android.R.string.ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(final MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                DBProvider.getInstance(ScheduleDetailActivity.this).delScheduleInfo(mInfo.getId());
                                mApp.doRefreshSchedule();
                                SingleToast.showToast(ScheduleDetailActivity.this,"删除成功",2000);
                                ScheduleDetailActivity.this.finish();
                            }
                        }).negativeText(android.R.string.cancel).show();
            }
        });
        mFabMenu.addMenuButton(fbDel);
        mFabMenu.setClosedOnTouchOutside(true);
    }

    private static final String FRAG_TAG_TIME_PICKER = "fragment_time_picker_name";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_time:
                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                        .setOnTimeSetListener(ScheduleDetailActivity.this)
                        .setForced24hFormat();
                rtpd.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
                break;
            default:
                break;
        }

    }

    int mHour =-1;//闹钟时间
    int mMinute = -1;//闹钟分钟

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        String date ="";
        if(minute>9&&hourOfDay>9){
             date = getString(R.string.calendar_time_picker_result_values, hourOfDay, minute);
        }else if(minute>9&&hourOfDay<9){
            date ="0"+ hourOfDay+":"+ minute;
        }else if(minute<9&&hourOfDay>9){
            date =hourOfDay+":"+"0"+ minute;
        }else{
            date ="0"+ hourOfDay+":"+"0"+ minute;
        }
        mEtTime.setText(date);
        mInfo.setTime(date);

        //设置小时：分钟的时候同时指定今日
        Date dateDay = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        mInfo.setDate(sdf.format(dateDay)); ;

        //设置提醒时间，提前10分钟
        if(minute-10>0){
            mHour = hourOfDay;
            mMinute = minute-10;
        }else{
            mHour = hourOfDay-1;
            mMinute = minute+50;
        }

    }
}

