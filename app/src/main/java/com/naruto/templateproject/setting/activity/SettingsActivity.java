package com.naruto.templateproject.setting.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.naruto.templateproject.R;
import com.naruto.templateproject.common.base.BaseActivity;

/**
 * @Purpose 设置页面
 * @Author Naruto Yang
 * @CreateDate 2020/12/25 0025
 * @Note
 */
public class SettingsActivity extends BaseActivity {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_settings;
    }

    @Override
    protected void init() {
        setTitleBarTitle("设置");
    }

    public void about(View view) {
        AboutActivity.launch(this);
    }

    /**
     * 启动此页面
     * @param activity
     */
    public static void launch(Activity activity) {
        activity.startActivity(new Intent(activity, SettingsActivity.class));
    }
}