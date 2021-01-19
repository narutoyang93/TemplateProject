package com.naruto.templateproject.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.naruto.templateproject.MyApplication;


/**
 * @Purpose
 * @Author Naruto Yang
 * @CreateDate 2019/6/5
 * @Note
 */
public class SharedPreferencesHelper {
    private static SharedPreferences sp;
    private static final String KEY_TOKEN = "token";
    private static final String KEY_TASK_ID = "task_id";

    /**
     * 读取token
     *
     * @param token
     */
    public static void setToken(String token) {
        setStringValue(KEY_TOKEN, token);
    }

    /**
     * 存储token
     *
     * @return
     */
    public static String getToken() {
        return getSharedPreferences().getString(KEY_TOKEN, "");
    }

    /**
     * 保存主任务栈的TaskId
     *
     * @param taskId
     */
    public static void setTaskId(int taskId) {
        setIntValue(KEY_TASK_ID, taskId);
    }

    /**
     * 获取主任务栈的TaskId
     *
     * @return
     */
    public static int getTaskId() {
        return getSharedPreferences().getInt(KEY_TASK_ID, -1);
    }

    /**
     * 清空所有数据
     */
    public static void clear() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear();
        editor.commit();
    }


////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //以下为内部方法


    private static SharedPreferences getSharedPreferences() {
        if (sp == null) {
            Context context = MyApplication.getContext();
            String SP_NAME = context.getPackageName();
            sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        }
        return sp;
    }

    private static void setStringValue(final String key, final String value) {
        setValue(new SetValueHelper() {
            @Override
            public void setValue(SharedPreferences.Editor editor) {
                editor.putString(key, value);
            }
        });
    }

    private static void setBooleanValue(final String key, final boolean value) {
        setValue(new SetValueHelper() {
            @Override
            public void setValue(SharedPreferences.Editor editor) {
                editor.putBoolean(key, value);
            }
        });
    }

    private static void setFloatValue(final String key, final float value) {
        setValue(new SetValueHelper() {
            @Override
            public void setValue(SharedPreferences.Editor editor) {
                editor.putFloat(key, value);
            }
        });
    }

    private static void setIntValue(final String key, final int value) {
        setValue(new SetValueHelper() {
            @Override
            public void setValue(SharedPreferences.Editor editor) {
                editor.putInt(key, value);
            }
        });
    }

    private static void setLongValue(final String key, final long value) {
        setValue(new SetValueHelper() {
            @Override
            public void setValue(SharedPreferences.Editor editor) {
                editor.putLong(key, value);
            }
        });
    }


    private static void setValue(SetValueHelper helper) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        helper.setValue(editor);
        editor.commit();
    }


    /**
     * @Purpose
     * @Author Naruto Yang
     * @CreateDate 2020/5/21 0021
     * @Note
     */
    public interface SetValueHelper {
        void setValue(SharedPreferences.Editor editor);
    }
}
