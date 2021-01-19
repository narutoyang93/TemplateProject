package com.naruto.templateproject.common.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import com.naruto.templateproject.R;


/**
 * @Purpose 支持设置drawable尺寸
 * @Author Naruto Yang
 * @CreateDate 2019/6/5
 * @Note
 */
public class ConstraintDrawableEditText extends AppCompatEditText {
    private int drawableWidth;
    private int drawableHeight;

    public ConstraintDrawableEditText(Context context) {
        super(context);
    }

    public ConstraintDrawableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ConstraintDrawableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context
     * @param attrs
     */
    public void init(Context context, AttributeSet attrs) {
        //获取自定义属性并赋值
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ConstraintDrawableEditText);
        drawableWidth = ta.getDimensionPixelSize(R.styleable.ConstraintDrawableEditText_drawableWidth, 0);
        drawableHeight = ta.getDimensionPixelSize(R.styleable.ConstraintDrawableEditText_drawableHeight, 0);
        Rect rect = new Rect(0, 0, drawableWidth, drawableHeight);
        if (drawableWidth > 0 || drawableHeight > 0) {
            Drawable[] drawables = getCompoundDrawables();
            int count = drawables.length;
            for (int i = 0; i < count; i++) {
                Drawable drawable = drawables[i];
                if (drawable != null) {
                    drawable.setBounds(rect);
                }
            }
            setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
        }
        ta.recycle();
    }
}
