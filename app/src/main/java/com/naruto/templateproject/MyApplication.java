package com.naruto.templateproject;

import android.app.Application;
import android.content.Context;

import androidx.annotation.DimenRes;


/**
 * @Purpose
 * @Author Naruto Yang
 * @CreateDate 2019/8/26 0026
 * @Note
 */
public class MyApplication extends Application {
    private static Context context;
    public static boolean hasMainPageLaunched = false;//首页是否已启动

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }

    /**
     * 获取dimen资源
     *
     * @param dimenId
     * @return
     */
    public static int getDimension(@DimenRes int dimenId) {
        return context.getResources().getDimensionPixelSize(dimenId);
    }
}
