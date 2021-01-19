package com.naruto.templateproject.common.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;

/**
 * @Purpose 解决“一个activity add多个fragment只有第一个fragment的fitsSystemWindows生效”的问题
 * @Author Naruto Yang
 * @CreateDate 2019/8/30 0030
 * @Note
 */
public class FitSystemWindowFrameLayout extends FrameLayout {
    public FitSystemWindowFrameLayout(Context context) {
        super(context);
    }

    public FitSystemWindowFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FitSystemWindowFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        WindowInsets result = super.dispatchApplyWindowInsets(insets);
        if (!insets.isConsumed()) {
            final int count = getChildCount();
            for (int i = 0; i < count; i++)
                result = getChildAt(i).dispatchApplyWindowInsets(insets);
        }
        return result;
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        ViewCompat.requestApplyInsets(child);
    }
}
