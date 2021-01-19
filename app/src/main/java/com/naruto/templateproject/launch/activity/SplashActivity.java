package com.naruto.templateproject.launch.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;

import com.naruto.templateproject.MyApplication;
import com.naruto.templateproject.R;
import com.naruto.templateproject.common.SharedPreferencesHelper;
import com.naruto.templateproject.common.base.BaseActivity;

/**
 * @Purpose 启动页
 * @Author Naruto Yang
 * @CreateDate 2020/12/25 0025
 * @Note
 */
public class SplashActivity extends BaseActivity {
    private boolean flag = false;//获取到token且页面已经打开超过1s（至少让用户看得到启动页）才允许进入其他页面

    @Override
    public int getLayoutRes() {
        return R.layout.activity_splash;
    }

    @Override
    public void init() {
//        SharedPreferencesHelper.setLogout();
        SharedPreferencesHelper.setTaskId(getTaskId());//记录主任务栈id

/*        //检查是否有网络
        if (DeviceUtils.isNetworkConnected(this)) {
            if (SharedPreferencesHelper.getToken().length() == 0) {//需要获取token
                CommonModel.getTempToken(this, new InterfaceFactory.Operation<String>() {
                    @Override
                    public void done(String data) {
                        if (!TextUtils.isEmpty(data)) {
                            checkUpdate();//检查更新
                        }
                    }
                });
            } else {//检查更新
                checkUpdate();
            }
        } else {
            flag = true;
        }*/
        flag = true;// TODO: 2020/12/25 0025 处理token获取逻辑
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                OpenFirstActivity();
            }
        }, 1000);
    }


    /**
     * 打开第一个页面
     */
    private void OpenFirstActivity() {
        if (!flag) {
            flag = true;
            return;
        }

        enterMainPage(this);

        finish();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {//不允许用户手动退出此页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    /**
     * 进入首页
     *
     * @param activity
     */
    public static void enterMainPage(BaseActivity activity) {
        activity.startActivity(new Intent(activity, MainActivity.class));
    }

}
