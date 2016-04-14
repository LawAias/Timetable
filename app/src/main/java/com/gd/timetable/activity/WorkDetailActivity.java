package com.gd.timetable.activity;

import android.animation.Animator;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.gd.timetable.R;
import com.gd.timetable.base.BaseCompatActivity;
import com.gd.timetable.bean.WorkInfo;
import com.gd.timetable.util.ActivityForResultUtil;
import com.gd.timetable.util.C;
import com.gd.timetable.util.SingleToast;
import com.github.clans.fab.FloatingActionButton;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.UUID;


/**
 * 作业详情页面，增改查都在这页面
 */
@SuppressWarnings("ALL")
public class WorkDetailActivity extends BaseCompatActivity {

    private static final String TAG = WorkDetailActivity.class.getSimpleName();

    private static final int SCALE_DELAY = 30;

    private LinearLayout mRowContainer;
    private CoordinatorLayout mCoordinatorLayout;
    Toolbar toolbar;


    private WorkInfo mWorkInfo = null;
    //传输过来的数据处理类型
    private int dataType;

    FloatingActionButton mFabBtn;


    private ImageView mImgLogo;//课标logo
    private TextView mTxTitle;//作业标题
    private TextView mTxUpdateTime;//创建时间（以服务器数据为准）


    private View mViewTime;//作业截止时间
    private TextView mTxTimeTitle;//截止时间标题
    private TextView mTxDdlContent;//截止时间具体


    private View mViewCourse;//作业所属课程
    private TextView mTxCourseTitle;//作业所属课程标题
    private TextView mTxCourseName;//作业所属课程名称


    private View mViewDetail;//作业详情
    private TextView mTxWorkDetailTitle;//作业详情标题
    private TextView mTxWorkDetail;//作业详情内容


    private Bitmap mPhotoBitmap;// 上传的图片
    private String mPhotoPath;// 上传的图片路径

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_detail);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.container);
        mRowContainer = (LinearLayout) findViewById(R.id.row_container);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.work_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Handle Back Navigation :D
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkDetailActivity.this.onBackPressed();
            }
        });

        initView();


        Intent it = getIntent();
        //增删改查
        dataType = it.getIntExtra(C.INTENT_TYPE.DATA_DATATYPE, 0);
        mWorkInfo = it.getParcelableExtra(C.INTENT_TYPE.DATA_OBJ);

        switch (dataType) {
            case 0:
                //增加
                mFabBtn.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_file_upload).color(Color.WHITE).actionBar());
                mFabBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        upLoadInfo();
                    }
                });
                break;
            case 1:
                break;
            case 2:
                //改
                mFabBtn.setImageDrawable(new IconicsDrawable(this, GoogleMaterial.Icon.gmd_mode_edit).color(Color.WHITE).actionBar());
                mFabBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        upLoadInfo();
                    }
                });
                break;
            case 3:
                //查看没有按钮
                mFabBtn.setVisibility(View.GONE);
                break;
        }
        bindData2View();

    }


    private void initView() {
        // Fab Button
        mFabBtn = (FloatingActionButton) findViewById(R.id.fab_normal);

        for (int i = 1; i < mRowContainer.getChildCount(); i++) {
            View rowView = mRowContainer.getChildAt(i);
            rowView.animate().setStartDelay(100 + i * SCALE_DELAY).scaleX(1).scaleY(1);
        }

        //标题，logo和发布时间
        View viewTitle = mRowContainer.findViewById(R.id.row_title);
        mImgLogo = (ImageView) viewTitle.findViewById(R.id.icon);
        mTxTitle = (TextView) viewTitle.findViewById(R.id.name);
        mTxUpdateTime = (TextView) viewTitle.findViewById(R.id.description);

        //课程名称
        mViewCourse = mRowContainer.findViewById(R.id.row_course);
        mTxCourseTitle = (TextView) mViewCourse.findViewById(R.id.name);
        mTxCourseTitle.setText(R.string.work_course_title);
        mTxCourseName = (TextView) mViewCourse.findViewById(R.id.description);


        //作业详情介绍
        mViewDetail = mRowContainer.findViewById(R.id.row_detail);
        mTxWorkDetailTitle = (TextView) mViewDetail.findViewById(R.id.name);
        mTxWorkDetailTitle.setText(R.string.work_detail_title);
        mTxWorkDetail = (TextView) mViewDetail.findViewById(R.id.description);

        //作业截止时间
        mViewTime = mRowContainer.findViewById(R.id.row_time);
        mTxTimeTitle = (TextView) mViewTime.findViewById(R.id.name);
        mTxTimeTitle.setText(R.string.work_deadline_title);
        mTxDdlContent = (TextView) mViewTime.findViewById(R.id.description);

        viewTitle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!TextUtils.isEmpty(mWorkInfo.getLogo().getUrl())) {
                    Intent intent = new Intent(WorkDetailActivity.this, ImageActivity.class);
                    Bundle b = new Bundle();
                    b.putString("url", mWorkInfo.getLogo().getUrl());
                    intent.putExtras(b);
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    private void bindData2View() {
        int drawColor = getResources().getColor(R.color.primary);
        Drawable typeDrawable = null;

        toolbar.setTitle(R.string.work_detail);
        typeDrawable = new IconicsDrawable(this, GoogleMaterial.Icon.gmd_event_note).color(drawColor);

        mImgLogo.setImageDrawable(typeDrawable);
//        iv.setImageDrawable(typeDrawable);

        if (dataType == 0) {
            mTxTitle.setText(R.string.input_title_hint);
            //新建，服务器上还没有时间
            mTxUpdateTime.setText("");
            mTxCourseName.setText("");
            mTxWorkDetail.setText("");
            mTxDdlContent.setText("");
        } else {
//            ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(this));
//            ImageLoader.getInstance().displayImage(mWorkInfo.getLogo().getUrl(), mImgLogo,
//                    PhotoUtil.normalImageOptions);
            Glide.with(this).load(mWorkInfo.getLogo().getUrl()).into(mImgLogo);
            Log.i("sihuan", mWorkInfo.getLogo().getUrl());
            //            ImageLoader.getInstance().displayImage(mWorkInfo.getLogo().getUrl(), iv,
            //                    PhotoUtil.normalImageOptions);
            mTxTitle.setText(mWorkInfo.getTitle());
            SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
            mTxUpdateTime.setText(getString(R.string.update_time, time.format(mWorkInfo.getUpdatedAt())));

            mTxCourseName.setText(mWorkInfo.getCourse());
            mTxWorkDetail.setText(mWorkInfo.getWorkDetail());
            mTxDdlContent.setText(mWorkInfo.getDeadline());
        }

        if (dataType == 0 || dataType == 2) {
            //增加和修改
            mImgLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPicViews();
                }
            });
            //点击标题 修改
            mTxTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInputTitleDialog();
                }
            });

            //名称
            mViewCourse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInputCourseDialog();
                }
            });

            //详情
            mViewDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showInputDetailDialog();
                }
            });

            mViewTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                            .setOnDateSetListener(new CalendarDatePickerDialogFragment.OnDateSetListener() {

                                @Override
                                public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
                                    String date = getString(R.string.calendar_date_picker_result_values, year, monthOfYear + 1, dayOfMonth);
                                    mTxDdlContent.setText(date);
                                    mWorkInfo.setDeadline(date);
                                }
                            });
                    cdp.show(getSupportFragmentManager(), "");
                }
            });
        }
    }


    /**
     * 选择图片
     */
    private void showPicViews() {
        new MaterialDialog.Builder(this)
                .title(R.string.pic_handle_title)
                .items(R.array.photo_handle)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        Intent intent = null;
                        if (which == 0) {
                            //拍照
                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File dir = new File(C.NORMAL.PIC_PATH_DIR);
                            if (!dir.exists()) {
                                dir.mkdirs();
                            }
                            mPhotoPath = C.NORMAL.PIC_PATH_DIR
                                    + UUID.randomUUID().toString();
                            File file = new File(mPhotoPath);
                            if (!file.exists()) {
                                try {
                                    file.createNewFile();
                                } catch (IOException e) {

                                }
                            }
                            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(file));
                            startActivityForResult(
                                    intent,
                                    ActivityForResultUtil.REQUESTCODE_UPLOADPHOTO_CAMERA);
                        } else if (which == 1) {
                            //从本地选
                            intent = new Intent(Intent.ACTION_PICK, null);
                            intent.setDataAndType(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    "image/*");
                            startActivityForResult(
                                    intent,
                                    ActivityForResultUtil.REQUESTCODE_UPLOADPHOTO_LOCATION);
                        }
                    }
                })
                .show();
    }


    /**
     * 输入标题
     */
    private void showInputTitleDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.input_title_hint)
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
                        mWorkInfo.setTitle(str);
                    }
                }).show();
    }

    /**
     * 输入课程名称
     */
    private void showInputCourseDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.input_course_hint)
                .content(R.string.input_course_hint_msg)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2, 20)
                .positiveText(R.string.submit)
                .input(R.string.empty_str, R.string.empty_str, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String str = input.toString();
                        mTxCourseName.setText(str);
                        mWorkInfo.setCourse(str);
                    }
                }).show();
    }


    /**
     * 输入作业详情
     */
    private void showInputDetailDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.input_course_detail_hint)
                .content(R.string.input_course_detail_hint_msg)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                .contentLineSpacing(1.0f)
                .inputRange(1, 300)
                .positiveText(R.string.submit)
                .input(R.string.empty_str, R.string.empty_str, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        String str = input.toString();
                        mTxWorkDetail.setText(str);
                        mWorkInfo.setWorkDetail(str);
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
     * 处理图片相关数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ActivityForResultUtil.REQUESTCODE_UPLOADPHOTO_CAMERA:
                //调用相机拍照
                if (resultCode == RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        Toast.makeText(this, R.string.sd_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mPhotoBitmap = BitmapFactory.decodeFile(mPhotoPath);
                    mImgLogo.setImageBitmap(mPhotoBitmap);

                    try {
                        AVFile picFile = AVFile.withAbsoluteLocalPath(C.AVFILE_NAME.PIC, mPhotoPath);
                        mWorkInfo.setLogo(picFile);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(this, R.string.pic_data_error, Toast.LENGTH_SHORT).show();
                }
                break;
            case ActivityForResultUtil.REQUESTCODE_UPLOADPHOTO_LOCATION:
                //从本地获取图片
                Uri uri = null;
                if (data == null) {
                    Toast.makeText(this, R.string.pic_data_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (resultCode == RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        Toast.makeText(this, R.string.sd_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    uri = data.getData();
                    String[] proj = {MediaStore.Images.Media.DATA};
                    Cursor cursor = managedQuery(uri, proj, null, null, null);
                    if (cursor != null) {
                        int column_index = cursor
                                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                            mPhotoPath = cursor.getString(column_index);
                            mPhotoBitmap = BitmapFactory.decodeFile(mPhotoPath);
                            mImgLogo.setImageBitmap(mPhotoBitmap);

                            try {
                                AVFile picFile = AVFile.withAbsoluteLocalPath(C.AVFILE_NAME.PIC, mPhotoPath);
                                mWorkInfo.setLogo(picFile);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                } else {
                    Toast.makeText(this, R.string.pic_data_error, Toast.LENGTH_SHORT).show();
                }
                break;
            case ActivityForResultUtil.REQUESTCODE_VOICE_DELETE_PHOTO:
                if (resultCode == RESULT_OK) {
                    mImgLogo.setImageDrawable(new BitmapDrawable());
                    mWorkInfo.setLogo(null);
                }
                break;
        }

    }


    /**
     * 上传招聘信息
     */
    private void upLoadInfo() {
        if (TextUtils.isEmpty(mWorkInfo.getTitle())) {
            SingleToast.showToast(this,
                    R.string.error_title_empty, 2000);
            return;
        } else if (TextUtils.isEmpty(mWorkInfo.getCourse())) {
            SingleToast.showToast(this, R.string.error_course_empty, 2000);
            return;
        } else if (TextUtils.isEmpty(mWorkInfo.getWorkDetail())) {
            SingleToast.showToast(this, R.string.error_work_detail_empty, 2000);
            return;
        } else if (mWorkInfo.getLogo() == null) {
            SingleToast.showToast(this, R.string.error_logo_empty, 2000);
            return;
        } else if (TextUtils.isEmpty(mWorkInfo.getDeadline())) {
            SingleToast.showToast(this, R.string.error_rec_time_empty, 2000);
            return;
        }

        final MaterialDialog progress = new MaterialDialog.Builder(this)
                .title(R.string.info_upload_title)
                .content(R.string.info_upload_hard)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();

        mWorkInfo.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    progress.dismiss();
                    SingleToast.showToast(WorkDetailActivity.this, R.string.info_upload_succ, 2000);
                } else {
                    SingleToast.showToast(WorkDetailActivity.this, R.string.info_upload_failed, 2000);
                }
            }
        });


    }


}
