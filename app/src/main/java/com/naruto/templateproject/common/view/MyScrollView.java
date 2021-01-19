package com.naruto.templateproject.common.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.Space;

import com.naruto.templateproject.common.InterfaceFactory;
import com.naruto.templateproject.common.utils.statusbar.StatusBarUtil;
import com.naruto.templateproject.databinding.TitleBarDetailBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * @Purpose 解决滑动冲突并扩展功能
 * @Author Naruto Yang
 * @CreateDate 2020/3/14
 * @Note
 */
public class MyScrollView extends ScrollView {
    private OnScrollChangeListener onScrollChangeListener;
    private TouchInterceptor touchInterceptor;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (onScrollChangeListener != null) {
            onScrollChangeListener.onScrollChanged(this, l, t, oldl, oldt);
            if (t + getHeight() >= computeVerticalScrollRange()) {
                onScrollChangeListener.onScrollToBottom();
            }
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (touchInterceptor != null && !touchInterceptor.onInterceptTouchEvent(ev)) return false;
        return super.onInterceptTouchEvent(ev);
    }

    public void setTouchInterceptor(TouchInterceptor touchInterceptor) {
        this.touchInterceptor = touchInterceptor;
    }

    /**
     * 解决水平滑动冲突（水平滑动事件交由子view处理）
     */
    public void solveHorizontalSlipConflict() {
        touchInterceptor = new TouchInterceptor() {
            float xDistance, yDistance, xLast, yLast;

            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        xDistance = yDistance = 0f;
                        xLast = ev.getX();
                        yLast = ev.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        final float curX = ev.getX();
                        final float curY = ev.getY();

                        xDistance += Math.abs(curX - xLast);
                        yDistance += Math.abs(curY - yLast);
                        xLast = curX;
                        yLast = curY;

                        if (xDistance > yDistance) {
                            return false;
                        }
                        return true;
                }
                return true;
            }

        };
    }


    /**
     * 关联标题栏
     * 常态时标题栏背景透明，按钮白色且有浅灰色圆形背景
     * 上滑时标题栏背景逐渐显示白色，按钮背景逐渐透明，按钮颜色逐渐变黑，直到headerView完全移出屏幕
     *
     * @param activity
     * @param headerView       标题栏下的视图，当其完全移出屏幕时为变化临界点
     * @param titleBarBinding
     * @param onScrollToBottom
     */
    public void correlateTitleBar(Activity activity, View headerView, TitleBarDetailBinding titleBarBinding, InterfaceFactory.SimpleOperation onScrollToBottom) {
        setOnScrollChangeListener(new MyScrollView.OnScrollChangeListener() {
            int titleBarHeight;
            float alpha0 = 0;//标题栏背景上一次的透明度
            int a = 0;
            List<View> buttons = new ArrayList<>();

            @Override
            public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy) {
                //滚动距离大于标题栏高度才开始改变标题栏颜色，当header滑出屏幕顶部时，标题栏背景颜色100%
                if (a == 0) {
                    titleBarHeight = titleBarBinding.vBgTitle.getHeight();
                    a = headerView.getHeight() - titleBarHeight;
                }
                int offset = y - titleBarHeight;
                if (offset < 0) offset = 0;
                float alpha = offset > a ? 1 : (float) offset / a;
                if (alpha0 == alpha) return;
                //设置标题背景透明度
                titleBarBinding.vBgTitle.setAlpha(alpha);
                //获取按钮
                if (buttons.isEmpty()) {
                    View view;
                    for (int i = 0; i < titleBarBinding.llButton.getChildCount(); i++) {
                        view = titleBarBinding.llButton.getChildAt(i);
                        if (!(view instanceof Space)) buttons.add(view);
                    }
                }
                //设置按钮透明度
                if (alpha <= 0.5) {//按钮逐渐隐藏
                    float alpha2 = 1 - alpha * 2;
                    for (View v : buttons) {
                        v.getBackground().setAlpha((int) (255 * alpha2));//按钮背景逐渐隐藏
                        v.setAlpha(alpha2);//按钮逐渐隐藏
                    }
                    if (alpha0 > 0.5) {//刚越界
                        titleBarBinding.tvTitleBarTitle.setAlpha(0);//标题文本完全隐藏
                        for (View v : buttons) {
                            v.setSelected(false);//按钮变色
                        }
                        StatusBarUtil.setStatusBarColor(activity, 0x55ffffff);//设置状态栏背景半透明
//                        StatusBarUtil.setStatusBarDarkTheme(activity, false);//设置状态栏字体白色
                    }
                } else {//按钮逐渐显示
                    float alpha2 = (alpha - 0.5f) * 2;
                    titleBarBinding.tvTitleBarTitle.setAlpha(alpha2);//标题文本逐渐显示
                    for (View v : buttons) {
                        v.setAlpha(alpha2);//按钮逐渐显示
                    }
                    if (alpha0 <= 0.5) {//刚越界
                        for (View v : buttons) {
                            v.getBackground().setAlpha(0);//按钮背景完全隐藏
                            v.setSelected(true);//按钮变色
                        }
                        StatusBarUtil.setStatusBarColor(activity, 0x00ffffff);;//设置状态栏透明
//                        StatusBarUtil.setStatusBarDarkTheme(activity, true);//设置状态栏字体黑色
                    }
                }
                alpha0 = alpha;//记录
            }

            @Override
            public void onScrollToBottom() {
                if (onScrollToBottom != null) onScrollToBottom.done();
            }
        });

        titleBarBinding.tvTitleBarTitle.setAlpha(0);//标题文本完全隐藏
    }

    /**
     * @Purpose 滑动监听接口
     * @Author Naruto Yang
     * @CreateDate 2020/3/14
     * @Note
     */
    public interface OnScrollChangeListener {
        void onScrollChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy);

        /**
         * 滚动到底部
         */
        void onScrollToBottom();
    }

    /**
     * @Purpose 用于处理滑动冲突
     * @Author Naruto Yang
     * @CreateDate 2019/9/25 0025
     * @Note
     */
    public interface TouchInterceptor {
        boolean onInterceptTouchEvent(MotionEvent ev);
    }
}
