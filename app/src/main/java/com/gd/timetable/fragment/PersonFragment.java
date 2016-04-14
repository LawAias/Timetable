package com.gd.timetable.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.SaveCallback;
import com.bumptech.glide.Glide;
import com.gd.timetable.R;
import com.gd.timetable.base.BaseSwitchFragment;
import com.gd.timetable.bean.UserInfo;
import com.gd.timetable.util.ActivityForResultUtil;
import com.gd.timetable.util.C;
import com.gd.timetable.util.LogTrace;
import com.gd.timetable.util.PhotoUtil;
import com.gd.timetable.util.SingleToast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;


/**
 * 用户信息页面
 *
 */
public class PersonFragment extends BaseSwitchFragment implements OnClickListener {

    private static final String TAG = PersonFragment.class.getSimpleName();

    private View rootView;
    private TextView mTxUserName;
    private TextView mTxNickName;
    private TextView mTxPhone;

    private ImageView mImgNickName;
    private ImageView mImgTel;
    private ImageView mImgAvatar;

    private UserInfo mUserInfo;

    private static PersonFragment instance;

    public PersonFragment() {
    }

    public static PersonFragment getInstance() {
        if (instance == null) instance = new PersonFragment();
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_personinfo, container,
                false);
        mTxUserName = (TextView) rootView.findViewById(R.id.tx_username);
        mTxNickName = (TextView) rootView.findViewById(R.id.tx_nickname);
        mTxPhone = (TextView) rootView.findViewById(R.id.tx_phone);
        mImgNickName = (ImageView) rootView.findViewById(R.id.img_nickname);
        mImgTel = (ImageView) rootView.findViewById(R.id.img_phone);
        mImgAvatar = (ImageView) rootView.findViewById(R.id.img_avatar_view);

        mImgNickName.setOnClickListener(this);
        mImgTel.setOnClickListener(this);
        mImgAvatar.setOnClickListener(this);

        mUserInfo = mApp.getUserInfo();

        mTxUserName.setText(String.format(
                getString(R.string.account_myusername), mApp.getUserName()));
        mTxNickName
                .setText(String.format(getString(R.string.account_mynickname),
                        mUserInfo.getNickName()));
        mTxPhone.setText(String.format(getString(R.string.account_phone),
                mUserInfo.getPhone()));

//        ImageLoader.getInstance().displayImage(mUserInfo.getAvatarUrl(), mImgAvatar, PhotoUtil.normalImageOptions);
        Glide.with(this).load(mUserInfo.getAvatarUrl()).into(mImgAvatar);
        return rootView;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_nickname:
                // 修改昵称
                showNameInputDialog();
                break;
            case R.id.img_phone:
                // 修改用户电话号码
                showPhoneInputDialog();
                break;
            case R.id.img_avatar_view:
                //从相册选取
                showAvatarList();
                break;
            default:
                break;
        }
    }


    public void showAvatarList() {
        new MaterialDialog.Builder(mAct)
                .title(R.string.pic_handle_title)
                .items(R.array.photo_handle)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        Intent intent = null;
                        switch (which) {
                            case 0:
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
                                        ActivityForResultUtil.REQUESTCODE_UPLOADAVATAR_CAMERA);
                                break;
                            case 1:
                                //从相册选取
                                intent = new Intent(Intent.ACTION_PICK, null);
                                intent.setDataAndType(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        "image/*");
                                startActivityForResult(
                                        intent,
                                        ActivityForResultUtil.REQUESTCODE_UPLOADAVATAR_LOCATION);
                                break;
                        }
                    }
                })
                .show();
    }


    private void showNameInputDialog() {
        new MaterialDialog.Builder(mAct)
                .title(R.string.account_mynickname_title)
                .content(R.string.account_mynickname_hint)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2, 8)
                .positiveText(R.string.acount_submit)
                .input(R.string.account_mynickname_hint, R.string.empty_str, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        mTxNickName.setText(String.format(
                                getString(R.string.account_mynickname),
                                input.toString()));
                        mUserInfo.setNickName(input.toString());
                        saveUserInfo();
                    }
                }).show();
    }


    private void showPhoneInputDialog() {
        new MaterialDialog.Builder(mAct)
                .title(R.string.account_phone_title)
                .content(R.string.account_phone_hint)
                .inputType(InputType.TYPE_CLASS_PHONE)
                .inputRange(11, 11)
                .positiveText(R.string.acount_submit)
                .input(R.string.account_phone_hint, R.string.empty_str, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        mTxPhone.setText(String.format(
                                getString(R.string.account_phone), input.toString()));
                        mUserInfo.setPhone(input.toString());
                        saveUserInfo();
                    }
                }).show();
    }

    private void saveUserInfo() {
        // 将数据存储到云端
        mUserInfo.saveInBackground(new SaveCallback() {

            @Override
            public void done(AVException arg0) {
                if (arg0 == null) {
                    SingleToast.showToast(mAct,
                            R.string.acount_modify_succ, 2000);
                } else {
                    SingleToast.showToast(mAct,
                            R.string.acount_modify_failed, 2000);
                }
            }
        });
    }

    // 临时文件路径
    private String mtempPhotoPath = "";

 /*  LogTrace.d(TAG, "onPhotoCropped", "Crop Uri in path: " + uri.getPath());
        mtempPhotoPath = uri.getPath();
        LogTrace.d("person", "onPhotoCropped", "mtempPhotoPath:" + mtempPhotoPath);
        ImageLoader.getInstance().displayImage("file://" + mtempPhotoPath, mImgAvatar);
        mUserInfo.saveAvatar(mtempPhotoPath, null);
        saveUserInfo();*/


    private Bitmap mPhotoBitmap;// 上传的图片
    private String mPhotoPath;// 上传的图片路径

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        LogTrace.d(TAG, "onActivityResult", "requestCode:" + requestCode + "  resultCode:" + resultCode + " data:" + data);

        switch (requestCode) {
            case ActivityForResultUtil.REQUESTCODE_UPLOADAVATAR_CAMERA:
                //调用相机拍照
                if (resultCode == Activity.RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        Toast.makeText(mAct, R.string.sd_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mPhotoBitmap = BitmapFactory.decodeFile(mPhotoPath);
                    mImgAvatar.setImageBitmap(mPhotoBitmap);
                    File file = new File(mPhotoPath);
                    startPhotoZoom(Uri.fromFile(file));
                } else {
                    Toast.makeText(mAct, R.string.pic_data_error, Toast.LENGTH_SHORT).show();
                }
                break;
            case ActivityForResultUtil.REQUESTCODE_UPLOADAVATAR_LOCATION:
                //从本地获取图片
                Uri uri = null;
                if (data == null) {
                    Toast.makeText(mAct, R.string.pic_data_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (resultCode == Activity.RESULT_OK) {
                    if (!Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        Toast.makeText(mAct, R.string.sd_error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    uri = data.getData();
                    startPhotoZoom(uri);
                } else {
                    Toast.makeText(mAct, R.string.pic_data_error, Toast.LENGTH_SHORT).show();
                }
                break;

            case ActivityForResultUtil.REQUESTCODE_UPLOADAVATAR_CROP:
                if (data == null) {
                    Toast.makeText(mAct, R.string.pic_data_error, Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    saveCropPhoto(data);
                }
                break;
        }
    }


    /**
     * 系统裁剪照片
     *
     * @param uri
     */
    private void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", true);
        startActivityForResult(intent,
                ActivityForResultUtil.REQUESTCODE_UPLOADAVATAR_CROP);
    }

    /**
     * 保存裁剪的照片
     *
     * @param data
     */
    private void saveCropPhoto(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap bitmap = extras.getParcelable("data");
            bitmap = PhotoUtil.toRoundCorner(bitmap, 15);
            if (bitmap != null) {
                mImgAvatar.setImageBitmap(bitmap);
                try {
                    AVFile picFile = AVFile.withAbsoluteLocalPath(C.AVFILE_NAME.PIC, PhotoUtil.saveCurrWorkToLocal(bitmap));
                    getUserInfo().setAvatar(picFile);
                    saveUserInfo();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }


        } else {
            Toast.makeText(mAct, "获取裁剪照片错误", Toast.LENGTH_SHORT).show();
        }
    }


}
