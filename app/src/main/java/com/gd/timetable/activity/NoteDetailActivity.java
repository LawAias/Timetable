package com.gd.timetable.activity;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.SaveCallback;
import com.gd.timetable.R;
import com.gd.timetable.bean.NoteInfo;
import com.gd.timetable.util.C;
import com.gd.timetable.util.SingleToast;
import com.github.clans.fab.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 笔记详情界面
 */
public class NoteDetailActivity extends AppCompatActivity {

    private static final String TAG= NoteDetailActivity.class.getSimpleName();

    private static final int SCALE_DELAY = 30;

    private LinearLayout mRowContainer;
    private CoordinatorLayout mCoordinatorLayout;
    Toolbar toolbar;


    private NoteInfo mNoteInfo = null;
    //传输过来的数据处理类型
    private int dataType;

    FloatingActionButton mFab;

    private ImageView mImgType;//笔记logo
    private TextView mTxTitle;//笔记标题
    private TextView mTxTime;//笔记创建时间（以服务器数据为准）

    private TextView mTxContentTitle;//笔记内容
    private TextView mTxContent;//笔记内容



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.container);
        mRowContainer = (LinearLayout) findViewById(R.id.row_container);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.note_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Handle Back Navigation :D
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteDetailActivity.this.onBackPressed();
            }
        });

        initView();


        Intent it = getIntent();
        //增删改查
        dataType = it.getIntExtra(C.INTENT_TYPE.DATA_DATATYPE, 0);
        mNoteInfo = it.getParcelableExtra(C.INTENT_TYPE.DATA_OBJ);

        switch (dataType) {
            case 0:
                //增加
                mFab.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_file_upload).color(Color.WHITE).actionBar());
                mFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        upLoadNoteInfo();
                    }
                });
                break;
            case 1:
                break;
            case 2:
                //改
                mFab.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_mode_edit).color(Color.WHITE).actionBar());
                mFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        upLoadNoteInfo();
                    }
                });
                break;
            case 3:
                //查看
                mFab.setVisibility(View.GONE);
                break;
        }
        bindData2View();

    }


    private void initView() {
        // Fab Button
        mFab = (FloatingActionButton) findViewById(R.id.fab_normal);

        for (int i = 1; i < mRowContainer.getChildCount(); i++) {
            View rowView = mRowContainer.getChildAt(i);
            rowView.animate().setStartDelay(100 + i * SCALE_DELAY).scaleX(1).scaleY(1);
        }

        //笔记标题，笔记类型和笔记时间
        View viewTitle = mRowContainer.findViewById(R.id.row_title);
        mImgType = (ImageView) viewTitle.findViewById(R.id.icon);
        mTxTitle = (TextView) viewTitle.findViewById(R.id.name);
        mTxTime = (TextView) viewTitle.findViewById(R.id.description);

        //笔记内容
        View viewContent = mRowContainer.findViewById(R.id.row_content);
        mTxContentTitle = (TextView) viewContent.findViewById(R.id.name);
        mTxContentTitle.setText(R.string.note_content);
        mTxContent = (TextView) viewContent.findViewById(R.id.description);

    }

    private void bindData2View() {
        int drawColor = getResources().getColor(R.color.primary);
        Drawable typeDrawable = null;
        typeDrawable = new IconicsDrawable(this, GoogleMaterial.Icon.gmd_event_note).color(drawColor);
        mImgType.setImageDrawable(typeDrawable);

        if (dataType == 0) {
            //新建笔记，服务器上还没有时间
            mTxTime.setText("");
            mTxTitle.setText(R.string.input_title_hint);
            mTxContentTitle.setText(R.string.input_content_hint);
        } else {
            mTxTitle.setText(mNoteInfo.getTitle());
            mTxContent.setText(mNoteInfo.getContent());
            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            mTxTime.setText(time.format(mNoteInfo.getUpdatedAt()));
        }

        if (dataType == 0 || dataType == 2) {
            //增加和修改笔记

            //点击标题 修改
            mTxTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTitleInputDialog();
                }
            });
            //点击内容 修改
            mTxContentTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showContentInputDialog();
                }
            });
        }

    }


    /**
     * 输入标题
     */
    private void showTitleInputDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.input_title_hint)
                .content(R.string.input_title_hint_note)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2, 12)
                .positiveText(R.string.submit)
                .input(R.string.empty_str, R.string.empty_str, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String str = input.toString();
                        mTxTitle.setText(str);
                        mNoteInfo.setTitle(str);
                    }
                }).show();
    }

    /**
     * 输入内容框
     */
    private void showContentInputDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.input_content_hint)
                .content(R.string.input_content_hint_note)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .contentLineSpacing(1.0f)
                .inputRange(1, 360)
                .positiveText(R.string.submit)
                .input(R.string.empty_str, R.string.empty_str, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String str = input.toString();
                        mTxContent.setText(str);
                        mNoteInfo.setContent(str);
                    }
                }).show();
    }



    /**
     * animate the views if we close the activity
     */
    @Override
    public void onBackPressed() {
        for (int i = mRowContainer.getChildCount() - 1; i > 0; i--) {
            View rowView = mRowContainer.getChildAt(i);
            ViewPropertyAnimator propertyAnimator = rowView.animate().setStartDelay((mRowContainer.getChildCount() - 1 - i) * SCALE_DELAY)
                    .scaleX(0).scaleY(0);

            propertyAnimator.setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        finishAfterTransition();
                    } else {
                        finish();
                    }
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        }
    }



    /**
     * 上传笔记
     */
    private void upLoadNoteInfo() {
        if (TextUtils.isEmpty(mNoteInfo.getTitle())) {
            SingleToast.showToast(this,
                    R.string.diary_title_empty, 2000);
            return;
        } else if (TextUtils.isEmpty(mNoteInfo.getContent())) {
            SingleToast.showToast(this, R.string.diary_content_empty, 2000);
            return;
        }

        final MaterialDialog progress = new MaterialDialog.Builder(this)
                .title(R.string.info_upload_title)
                .content(R.string.info_upload_hard)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();

        mNoteInfo.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    progress.dismiss();
                    SingleToast.showToast(NoteDetailActivity.this, R.string.info_upload_succ, 2000);
                } else {
                    SingleToast.showToast(NoteDetailActivity.this, R.string.info_upload_failed, 2000);
                }
            }
        });


    }


}
