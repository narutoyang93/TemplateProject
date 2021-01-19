package com.naruto.templateproject.common.base;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.naruto.templateproject.MyApplication;
import com.naruto.templateproject.R;
import com.naruto.templateproject.common.view.ZoomAnimationButton;

import java.util.List;

/**
 * @Purpose 主页基类
 * @Author Naruto Yang
 * @CreateDate 2020/5/06 0006
 * @Note
 */
public abstract class BaseMainActivity extends BaseActivity {
    protected LinearLayout bottomTab;//底部栏

    private FragmentManager manager;
    private static final String FRAGMENT_TAG_PREFIX = "Main_Activity_Fragment_";
    private int currentCheckId = -1;//当前选中的tab的id
    private static final String SAVE_STATE_KEY_POSITION = "Save_State_Key_Position";
    private long lastPressBackTime = 0;//上次点击返回按钮的时间，用于连续点击返回键退出应用

    /**
     * 创建Fragment
     *
     * @param checkedId
     * @return
     */
    protected abstract Fragment createFragment(int checkedId);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {//activity被重新创建，必须先给currentPosition赋值再执行init方法
            currentCheckId = savedInstanceState.getInt(SAVE_STATE_KEY_POSITION, currentCheckId);
        }
        super.onCreate(savedInstanceState);

        MyApplication.hasMainPageLaunched = true;
    }

    @Override
    protected View getFitsSystemWindowView() {
        return null;
    }

    @Override
    protected void init() {
        bottomTab = findViewById(R.id.ll_bottom_tab);

        manager = getSupportFragmentManager();
        if (currentCheckId == -1) {
            //初始化时自动选中第一个tab
            currentCheckId = bottomTab.getChildAt(0).getId();
        } else {
            List<Fragment> list = manager.getFragments();
            FragmentTransaction transaction = manager.beginTransaction();
            for (Fragment fragment : list) {
                transaction.hide(fragment);
            }
        }

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                if (currentCheckId == id) return;
                if (onInterceptClickBottomTab(id)) return;//拦截点击事件
                if (currentCheckId != -1) {//隐藏Fragment
                    bottomTab.findViewById(currentCheckId).setSelected(false);
                    hideFragment(currentCheckId);
                }
                //显示Fragment
                v.setSelected(true);
                currentCheckId = id;
                showFragment(currentCheckId);
            }
        };

        //遍历设置点击监听
        View v;
        for (int i = 0; i < bottomTab.getChildCount(); i++) {
            v = bottomTab.getChildAt(i);
            if (v instanceof ZoomAnimationButton)
                v.setOnClickListener(clickListener);
        }

        bottomTab.findViewById(currentCheckId).setSelected(true);
        showFragment(currentCheckId);
    }


    /**
     * 展示Fragment
     */
    private void showFragment(int checkedId) {
        FragmentTransaction transaction = manager.beginTransaction();
        String tag = FRAGMENT_TAG_PREFIX + checkedId;
        Fragment fragment = manager.findFragmentByTag(tag);
        if (fragment == null) {
            fragment = createFragment(checkedId);
            transaction.add(R.id.frameLayout, fragment, tag);
        }
        transaction.show(fragment).commit();
        currentCheckId = checkedId;
    }

    /**
     * 隐藏Fragment
     *
     * @param checkedId
     */
    private void hideFragment(int checkedId) {
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag(FRAGMENT_TAG_PREFIX + checkedId);
        if (fragment != null) {
            transaction.hide(fragment).commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPressBackTime < 2000) {//两秒内连续点击返回键则退出应用
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(10);
            } else {
                lastPressBackTime = currentTime;
                Toast.makeText(this, "再按一次返回键退出应用", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Activity异常销毁，保存当前tab位置
        outState.putInt(SAVE_STATE_KEY_POSITION, currentCheckId);
    }

    /**
     * 点击某个tab可能需要满足某些条件才允许打开对应页面，否则拦截此次点击（例如需要登录才能打开“消息”页面）
     *
     * @param TabId
     * @return
     */
    protected boolean onInterceptClickBottomTab(int TabId) {
        return false;
    }
}
