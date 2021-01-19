package com.naruto.templateproject.example.activity;

import com.naruto.templateproject.R;
import com.naruto.templateproject.common.base.DataBindingActivity;
import com.naruto.templateproject.common.utils.statusbar.StatusBarUtil;
import com.naruto.templateproject.databinding.ActivityDetailsBinding;

/**
 * @Purpose 详情页面
 * @Author Naruto Yang
 * @CreateDate 2020/12/26 0026
 * @Note
 */
public class DetailsActivity extends DataBindingActivity<ActivityDetailsBinding> {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_details;
    }

    @Override
    protected void init() {
        StatusBarUtil.setStatusBarColor(this, 0x55ffffff);//设置状态栏背景灰色，防止文字与header融合
        setTitleBarTitle("详情");

        //滑动监听（控制标题栏背景透明度）
        dataBinding.scrollView.correlateTitleBar(this, dataBinding.ivHeader, dataBinding.titleBar, null);
    }
/*    @Override
    protected boolean isNeedSetStatusBarDarkTheme() {
        return false;//header会延伸到状态栏，有可能导致状态栏文字无法看清，故初始化时需设置状态栏半透明灰色并设置状态栏文字白色
    }*/
}