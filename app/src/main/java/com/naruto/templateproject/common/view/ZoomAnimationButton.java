package com.naruto.templateproject.common.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.naruto.templateproject.R;

/**
 * @Purpose 带文字和图片的按钮，点击有缩放动画
 * @Author Naruto Yang
 * @CreateDate 2019/12/24
 * @Note
 */
public class ZoomAnimationButton extends LinearLayout {
    private ScaleAnimation animation;
    private ImageView imageView;
    private TextView textView;

    public ZoomAnimationButton(Context context) {
        this(context, null);
    }

    public ZoomAnimationButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomAnimationButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ZoomAnimationButton);
        Drawable drawable = ta.getDrawable(R.styleable.ZoomAnimationButton_drawable);
        int drawableWidth = ta.getDimensionPixelSize(R.styleable.ZoomAnimationButton_drawableWidth, 0);
        int drawableHeight = ta.getDimensionPixelSize(R.styleable.ZoomAnimationButton_drawableHeight, 0);
        String text = ta.getString(R.styleable.ZoomAnimationButton_text);
        ColorStateList textColor = ta.getColorStateList(R.styleable.ZoomAnimationButton_textColor);
        ColorStateList drawableTint = ta.getColorStateList(R.styleable.ZoomAnimationButton_drawableTint);
        float textSize = ta.getDimension(R.styleable.ZoomAnimationButton_textSize, -1);
        int drawablePadding = ta.getDimensionPixelSize(R.styleable.ZoomAnimationButton_drawablePadding, 0);
        boolean startWithImage = ta.getBoolean(R.styleable.ZoomAnimationButton_startWithImage, true);//决定图片和文字哪个先放进容器

        //图片
        if (drawable != null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new LayoutParams(drawableWidth, drawableHeight));
            imageView.setImageDrawable(drawable);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                imageView.setImageTintList(drawableTint);
            }
        }
        //文字
        if (!TextUtils.isEmpty(text)) {
            textView = new TextView(context);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //设置图片和文字间距
            if (startWithImage) {
                if (getOrientation() == HORIZONTAL) {
                    layoutParams.leftMargin = drawablePadding;
                } else {
                    layoutParams.topMargin = drawablePadding;
                }
            } else {
                if (getOrientation() == HORIZONTAL) {
                    layoutParams.rightMargin = drawablePadding;
                } else {
                    layoutParams.bottomMargin = drawablePadding;
                }
            }
            textView.setLayoutParams(layoutParams);
            textView.setText(text);
            if (textColor != null) textView.setTextColor(textColor);
            if (textSize != -1) textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
        //添加子view
        if (startWithImage) {
            if (imageView != null) addView(imageView);
            if (textView != null) addView(textView);
        } else {
            if (textView != null) addView(textView);
            if (imageView != null) addView(imageView);
        }
        ta.recycle();

        //点击动画
        animation = new ScaleAnimation(1, 0.7f, 1, 0.7f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(75);
        animation.setRepeatCount(1);
        animation.setRepeatMode(Animation.REVERSE);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            imageView.startAnimation(animation);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 设置文本
     *
     * @param text
     */
    public void setText(String text) {
        textView.setText(text);
    }

}
