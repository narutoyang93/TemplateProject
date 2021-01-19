package com.naruto.templateproject.example.activity;

import com.naruto.templateproject.R;
import com.naruto.templateproject.common.base.DataBindingActivity;
import com.naruto.templateproject.databinding.ActivityExampleBinding;

/**
 * @Purpose 例子
 * @Author Naruto Yang
 * @CreateDate 2020/12/25 0025
 * @Note
 */
public class ExampleActivity extends DataBindingActivity<ActivityExampleBinding> {

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_example;
    }

    @Override
    protected void init() {
        setTitleBarTitle("例子");
    }

}