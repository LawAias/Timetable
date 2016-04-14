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
import com.gd.timetable.activity.NoteDetailActivity;
import com.gd.timetable.adapter.NoteAdapter;
import com.gd.timetable.base.BaseSwitchFragment;
import com.gd.timetable.bean.NoteInfo;
import com.gd.timetable.util.C;
import com.gd.timetable.util.LogTrace;
import com.gd.timetable.util.SingleToast;
import com.github.clans.fab.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.util.List;

import anim.CustomItemAnimator;


/**
 * 笔记页面
 *
 */
public class NoteFragment extends BaseSwitchFragment implements NoteAdapter.OnNoteItemClick {

    private static final String TAG = NoteFragment.class.getSimpleName();


    private NoteAdapter mAdapter;
    private static NoteFragment instance;
    private MenuActivity menuActivity;

    private View rootView;
    private FloatingActionButton mFabActButton;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static NoteFragment getInstance() {
        if (instance == null) instance = new NoteFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_note, container, false);
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
        mAdapter = new NoteAdapter(mApp.getNoteList(), mAct, this);
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
        mFabActButton = (FloatingActionButton) rootView.findViewById(R.id.fab_normal);
        mFabActButton.setImageDrawable(new IconicsDrawable(mAct, GoogleMaterial.Icon.gmd_add).color(Color.WHITE).actionBar());
        mFabActButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                creatNewNote();
            }
        });
    }

    /**
     * 创建新的笔记
     */
    private void creatNewNote() {

        mFabActButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                NoteInfo noteInfo = new NoteInfo();
                noteInfo.setUserInfo(getUserInfo());
                Intent intent = new Intent(menuActivity, NoteDetailActivity.class);
                intent.putExtra(C.INTENT_TYPE.DATA_DATATYPE, 0);
                intent.putExtra(C.INTENT_TYPE.DATA_OBJ, noteInfo);
                menuActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                menuActivity.startActivity(intent);
            }
        }, 300);

    }

    @Override
    public void OnClickNoteItem(NoteInfo mNoteInfo, View animView) {
        menuActivity.animateNoteActivity(mNoteInfo, 3, mFabActButton, animView);
    }

    @Override
    public void OnLongClickNoteItem(NoteInfo mNoteInfo, View animView) {
        showHandleList(mNoteInfo, animView);
    }


    /**
     * 展示操作选款
     *
     * @param mNoteInfo
     * @param animView
     */
    private void showHandleList(final NoteInfo mNoteInfo, final View animView) {
        new MaterialDialog.Builder(mAct)
                .items(R.array.note_handle)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                //查看笔记
                                menuActivity.animateNoteActivity(mNoteInfo, 3, mFabActButton, animView);
                                break;
                            case 1:
                                //修改笔记
                                menuActivity.animateNoteActivity(mNoteInfo, 2, mFabActButton, animView);
                                break;
                            case 2:
                                //删除笔记
                                new MaterialDialog.Builder(mAct).content(R.string.dialog_del_title)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(final MaterialDialog dialog, DialogAction which) {

                                                mNoteInfo.deleteInBackground(new DeleteCallback() {
                                                    @Override
                                                    public void done(AVException e) {
                                                        if (e == null) {
                                                            SingleToast.showToast(mAct, R.string.note_del_succ, 2000);
                                                            mApp.getNoteList().remove(mNoteInfo);
                                                            mAdapter.notifyDataSetChanged();
                                                        } else {
                                                            SingleToast.showToast(mAct, R.string.note_del_faile, 2000);
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
        AVQuery<AVObject> query = new AVQuery<AVObject>("NoteInfo");
        query.whereEqualTo(NoteInfo.USER, getUserInfo());
        query.include("note_user");
        //跳过已有的数据
        LogTrace.d(TAG, "doRefreshSchedule", "mApp.getNoteList().size():" + mApp.getNoteList().size());
        query.skip(mApp.getNoteList().size());
        //每次刷新十条
        query.setLimit(10);
        query.addAscendingOrder("updatedAt");
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
                            NoteInfo noteInfo = (NoteInfo) info;
                            LogTrace.d(TAG,"doRefreshSchedule","noteInfo:"+noteInfo.getTitle());
                            mApp.getNoteList().add(noteInfo);
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
