package com.gd.timetable.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gd.timetable.R;
import com.gd.timetable.activity.MenuActivity;
import com.gd.timetable.adapter.ScheduleAdapter;
import com.gd.timetable.base.BaseSwitchFragment;
import com.gd.timetable.bean.ScheduleInfo;
import com.gd.timetable.db.DBProvider;
import com.gd.timetable.receiver.AlarmReceiver;
import com.gd.timetable.util.C;
import com.gd.timetable.util.LogTrace;
import com.github.clans.fab.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import anim.CustomItemAnimator;


/**
 * 日程页面
 *
 */
public class ScheduleFragment extends BaseSwitchFragment implements ScheduleAdapter.OnScheduleClick{

    private static final String TAG = ScheduleFragment.class.getSimpleName();

    private ScheduleAdapter mAdapter;
    private static ScheduleFragment instance;
    private MenuActivity menuActivity;

    private View rootView;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private FloatingActionButton mFabActBtn;

    public static ScheduleFragment getInstance() {
        if (instance == null) instance = new ScheduleFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_schedule, container, false);
        initViews();
        menuActivity = (MenuActivity) mAct;
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private void initViews() {
        initFloatBtn();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mAct));
        mRecyclerView.setItemAnimator(new CustomItemAnimator());
        mAdapter = new ScheduleAdapter(mApp.getScheduleList(), mAct, this);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_accent));

        //下拉刷新
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);
        doRefresh();
    }

    private void initFloatBtn() {
        mFabActBtn = (FloatingActionButton) rootView.findViewById(R.id.fab_normal);
        mFabActBtn.setImageDrawable(new IconicsDrawable(mAct,
                GoogleMaterial.Icon.gmd_add).color(Color.WHITE).actionBar());
        mFabActBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //增加
                ScheduleInfo scheduleInfo = new ScheduleInfo();
                scheduleInfo.setId(UUID.randomUUID().toString());
                LogTrace.d(TAG, "initFloatBtn", "id:" + scheduleInfo.getId());
                menuActivity.animateDetailActivity(scheduleInfo, 0);
            }
        });
    }



    /**
     * 下拉刷新数据(今日数据)
     */
    private void doRefresh() {

        Date dateDay = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String dateStr = sdf.format(dateDay);

        List<ScheduleInfo> infos = DBProvider.getInstance(getActivity())
                .getScheduleFromDB(dateStr);

        if(infos.isEmpty()){
            //没有数据，把服务器的课程数据更新成今天的(额，虽然这样有点不合理--！)
            List<ScheduleInfo> allInfos = DBProvider.getInstance(getActivity())
                    .getScheduleFromDB();
            AlarmManager mAlarmManager = (AlarmManager) mAct.getSystemService(Context.ALARM_SERVICE);

            boolean isUpdate = false;

            for(ScheduleInfo info:allInfos){
                if(!info.getDate().equals(dateStr)){
                    isUpdate = true;
                    info.setDate(dateStr);
                    //更新为今日的
                    DBProvider.getInstance(getActivity()).updateNoteInfo(info);
                    //更新日程后  记录一则闹钟提醒
                    Intent intent = new Intent(mAct, AlarmReceiver.class);
                    intent.putExtra(C.INTENT_TYPE.DATA_INFO, info);
                    PendingIntent sender = PendingIntent.getBroadcast(mAct, 0, intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    String[] timeArr = info.getTime().split(":");
                    int hourOfDay =Integer.parseInt(timeArr[0]);
                    int minute = Integer.parseInt(timeArr[1]);
                    int mHour =-1;//闹钟时间
                    int mMinute = -1;//闹钟分钟
                    if(minute-10>0){
                        mHour = hourOfDay;
                        mMinute = minute-10;
                    }else{
                        mHour = hourOfDay-1;
                        mMinute = minute+50;
                    }
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

            if(isUpdate) {
                //重新获取次
                doRefresh();
            }else{
                mApp.getScheduleList().clear();
                mApp.getScheduleList().addAll(infos);
                mAdapter.notifyDataSetChanged();
                //此次刷新结束
                mSwipeRefreshLayout.setRefreshing(false);
            }

        }else{
            mApp.getScheduleList().clear();
            mApp.getScheduleList().addAll(infos);
            mAdapter.notifyDataSetChanged();
            //此次刷新结束
            mSwipeRefreshLayout.setRefreshing(false);
        }


    }


    @Override
    public void OnClickSchedule(ScheduleInfo scheduleInfo) {
        showHandleList(scheduleInfo);
    }

    @Override
    public void OnLongClickSchedule(ScheduleInfo scheduleInfo) {
        showHandleList(scheduleInfo);
    }


    /**
     * 展示操作选款
     *
     * @param scheduleInfo
     */
    private void showHandleList(final ScheduleInfo scheduleInfo) {
        new MaterialDialog.Builder(mAct)
                .title(scheduleInfo.getName())
                .items(R.array.schedule_handle)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                //查看笔记
                                menuActivity.animateDetailActivity(scheduleInfo, 3);
                                break;
                            case 1:
                                //修改笔记
                                menuActivity.animateDetailActivity(scheduleInfo, 2);
                                break;
                            case 2:
                                //删除笔记
                                new MaterialDialog.Builder(mAct).content(R.string.dialog_del_title)
                                        .positiveText(android.R.string.ok)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(final MaterialDialog dialog, DialogAction which) {
                                                dialog.dismiss();
                                                DBProvider.getInstance(menuActivity).delScheduleInfo(scheduleInfo.getId());
                                                doRefresh();
                                            }
                                        })
                                        .positiveText(android.R.string.ok)
                                        .negativeText(android.R.string.cancel).show();
                                break;
                        }
                    }
                })
                .show();
    }



}
