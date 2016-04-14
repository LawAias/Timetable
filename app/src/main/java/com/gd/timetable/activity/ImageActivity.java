package com.gd.timetable.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bumptech.glide.Glide;
import com.gd.timetable.GdApp;
import com.gd.timetable.R;
import com.gd.timetable.util.PhotoUtil;
import com.gd.timetable.util.SingleToast;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by aa on 2016/4/8.
 */
public class ImageActivity extends Activity {

    private PhotoView iv;
    private PhotoViewAttacher photoViewAttacher;
    private Button save;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        findViews();
        registerListener();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("url");
//            ImageLoader.getInstance().displayImage(url, iv,
//                    PhotoUtil.normalImageOptions);
            Glide.with(this).load(url).into(iv);
            photoViewAttacher = new PhotoViewAttacher(iv,true);
        }


    }

    private void registerListener() {

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = PhotoUtil.saveCurrWorkToLocal(PhotoUtil.getBitmapFromView(iv));
                Log.e("sihuan", s);
                SingleToast.showToast(GdApp.ctx, "图片已保存到目录" + s + "下", 2000);
//                Toast.makeText(GdApp.ctx, "图片已保存到目录" + s + "下", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void findViews() {
        iv = (PhotoView) findViewById(R.id.iv);
        save = (Button) findViewById(R.id.save);
    }


}
