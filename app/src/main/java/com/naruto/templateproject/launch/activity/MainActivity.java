package com.naruto.templateproject.launch.activity;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.naruto.templateproject.R;
import com.naruto.templateproject.common.base.BaseMainActivity;
import com.naruto.templateproject.home.fragment.HomeFragment;
import com.naruto.templateproject.message.fragment.MessageFragment;
import com.naruto.templateproject.mine.fragment.MineFragment;

/**
 * @Purpose 首页
 * @Author Naruto Yang
 * @CreateDate 2020/12/25 0025
 * @Note
 */
public class MainActivity extends BaseMainActivity {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }


    @Override
    protected Fragment createFragment(int checkedId) {
        switch (checkedId) {
            case R.id.btn_message://消息
                return MessageFragment.newInstance(null, null);
            case R.id.btn_mine://我的
                return MineFragment.newInstance(null, null);
            default://首页
                return HomeFragment.newInstance(null, null);
        }
    }

    /**
     * 启动此页面并清除之前的所有页面
     *
     * @param activity
     */
    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.finishAffinity();
        activity.startActivity(intent);
    }
}