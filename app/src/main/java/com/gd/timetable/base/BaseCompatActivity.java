package com.gd.timetable.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gd.timetable.GdApp;
import com.gd.timetable.bean.UserInfo;

/**
 * Created by sjy on 2016/3/12.
 */
public class BaseCompatActivity extends AppCompatActivity {

    protected GdApp mApp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApp = (GdApp) getApplication();
    }

    public UserInfo getUserInfo(){
        return mApp.getUserInfo();
    }

    /**
     * 是否是管理者
     * @return
     */
    public boolean isManager(){
        return getUserInfo().getType().equals("1");
    }

}
