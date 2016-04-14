package com.gd.timetable.fragment;

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
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.gd.timetable.R;
import com.gd.timetable.activity.MenuActivity;
import com.gd.timetable.activity.WorkDetailActivity;
import com.gd.timetable.adapter.WorkAdapter;
import com.gd.timetable.base.BaseSwitchFragment;
import com.gd.timetable.bean.WorkInfo;
import com.gd.timetable.util.C;
import com.gd.timetable.util.LogTrace;
import com.gd.timetable.util.SingleToast;
import com.github.clans.fab.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import anim.CustomItemAnimator;


/**
 * 作业信息页面
 *
 */
public class WorkFragment extends BaseSwitchFragment implements WorkAdapter.OnRecItemClick {

    private static final String TAG = WorkFragment.class.getSimpleName();


    private WorkAdapter mAdapter;
    private static WorkFragment instance;
    private MenuActivity menuActivity;

    private View rootView;
    private FloatingActionButton mFab;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static WorkFragment getInstance() {
        if (instance == null) instance = new WorkFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_work, container, false);

        menuActivity = (MenuActivity) mAct;
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }


    private void initViews() {
        initFloatBtn();
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mAct));
        mRecyclerView.setItemAnimator(new CustomItemAnimator());
        mAdapter = new WorkAdapter(mApp.getWorkInfoList(), mAct, this);
        mRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.theme_accent));

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
        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab_normal);
        if(isManager()) {
            //管理员可以增加作业
            mFab.setVisibility(View.VISIBLE);
            mFab.setImageDrawable(new IconicsDrawable(mAct, GoogleMaterial.Icon.gmd_add).color(Color.WHITE).actionBar());
            mFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //增加作业
                    WorkInfo workInfo = new WorkInfo();
                    workInfo.setUserInfo(getUserInfo());
                    Intent intent = new Intent(menuActivity, WorkDetailActivity.class);
                    intent.putExtra(C.INTENT_TYPE.DATA_DATATYPE, 0);
                    intent.putExtra(C.INTENT_TYPE.DATA_OBJ, workInfo);
                    menuActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    menuActivity.startActivity(intent);
                }
            });
        }else{
            //学生只可以查看作业，没有增加作业按钮
            mFab.setVisibility(View.GONE);
        }

    }


    @Override
    public void OnClickRecItem(WorkInfo mWorkInfo, View animView) {
        if(isManager()) {
            //管理者可以使用动画
            menuActivity.animateWorkActivity(mWorkInfo, 3, mFab, animView);
        }else{
            menuActivity.animateWorkActivity(mWorkInfo, 3);
        }

    }

    @Override
    public void OnLongClickRecItem(WorkInfo mWorkInfo, View animView) {
        if(isManager()) {
            //管理者长按出菜单
            showManagerHandleList(mWorkInfo, animView);
        }
    }


    /**
     * 展示操作选款
     *
     * @param mWorkInfo
     * @param animView
     */
    private void showManagerHandleList(final WorkInfo mWorkInfo, final View animView) {
        new MaterialDialog.Builder(mAct)
                .items(R.array.work_handle)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                //查看
                                menuActivity.animateWorkActivity(mWorkInfo, 3, mFab, animView);
                                break;
                            case 1:
                                //修改
                                menuActivity.animateWorkActivity(mWorkInfo, 2, mFab, animView);
                                break;
                            case 2:
                                //删除
                                new MaterialDialog.Builder(mAct).content(R.string.dialog_del_title)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(final MaterialDialog dialog, DialogAction which) {

                                                mWorkInfo.deleteInBackground(new DeleteCallback() {
                                                    @Override
                                                    public void done(AVException e) {
                                                        if (e == null) {
                                                            SingleToast.showToast(mAct, R.string.work_del_succ, 2000);
                                                            mApp.getWorkInfoList().remove(mWorkInfo);
                                                            mAdapter.notifyDataSetChanged();
                                                        } else {
                                                            SingleToast.showToast(mAct, R.string.work_del_faile, 2000);

                                                        }
                                                        dialog.dismiss();
                                                    }
                                                });

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


    /**
     * 获取服务器数据
     */
    private void doRefresh() {
        AVQuery<AVObject> query = new AVQuery<AVObject>("WorkInfo");
        query.addAscendingOrder("updatedAt");//升序排列
        query.include("note_user");
        //每次刷新十条
        query.setLimit(10);
        //跳过已有的数据
        LogTrace.d(TAG, "doQryRelative", "mApp.getWorkInfoList().size():" + mApp.getWorkInfoList().size());
        query.skip(mApp.getWorkInfoList().size());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e != null) {
                    // 查询出错，没有拿到数据
                    SingleToast.showToast(mApp, "查询失败:" + e.getMessage(), 2000);
                } else {
                    // 当前用户喜欢的所有 物品 都保存在 list 里面了.
                    if (list != null && list.size() > 0) {
                        for (AVObject info : list) {
                            WorkInfo workInfo = (WorkInfo) info;
                            LogTrace.d(TAG,"doQryRelative","workInfo:"+ workInfo.getTitle());
                            mApp.getWorkInfoList().add(workInfo);
                        }
                        mAdapter.notifyDataSetChanged();

                    } else {
                        SingleToast.showToast(mApp, R.string.data_no_more, 2000);
                    }
                }
                //此次刷新结束
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }


}
