package com.naruto.templateproject.common.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.naruto.templateproject.R;
import com.naruto.templateproject.common.utils.statusbar.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Purpose
 * @Author Naruto Yang
 * @CreateDate 2020/11/13 0013
 * @Note
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected final static int REQUEST_CODE_OPEN_PERMISSION = -20;//前往手动开启权限
    public ProgressDialog progressDialog;//加载弹窗
    protected View rootView;//根布局，即getLayoutRes()返回的布局
    protected View titleBar;//标题栏
    protected Toast toast;
    RequestPermissionsCallBack requestPermissionsCallBack;//权限申请回调

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (!isScreenRotatable())
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 禁用横屏

        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());

        rootView = ((ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content)).getChildAt(0);
        getTitleBar();
        setBackBtnClickListener(null);

        //设置沉浸式状态栏
        if (isNeedSetFitsSystemWindow()) {
            View fitsSystemWindowView = getFitsSystemWindowView();
            if (fitsSystemWindowView != null)
                fitsSystemWindowView.setFitsSystemWindows(true);//设置FitsSystemWindows，使顶部留出状态栏高度的padding
            //设置状态栏透明
            StatusBarUtil.setTranslucentStatus(this);
            //一般的手机的状态栏文字和图标都是白色的, 可如果应用也是纯白色的, 或导致状态栏文字看不清
            if (isNeedSetStatusBarDarkTheme() && !StatusBarUtil.setStatusBarDarkTheme(this, true)) {
                //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
                //这样半透明+白=灰, 状态栏的文字能看得清
                StatusBarUtil.setStatusBarColor(this, 0x55000000);
            }
        }

        init();
    }


    /**
     * 是否需要设置状态栏字体颜色为深色
     *
     * @return
     */
    protected boolean isNeedSetStatusBarDarkTheme() {
        return true;
    }

    /**
     * 是否需要设置沉浸式状态栏
     *
     * @return
     */
    protected boolean isNeedSetFitsSystemWindow() {
        return true;
    }

    /**
     * 是否允许旋转屏幕
     *
     * @return
     */
    protected boolean isScreenRotatable() {
        return false;
    }

    /**
     * 设置标题
     *
     * @param title
     * @param backBtnClickListener
     */
    protected void setTitleBarTitle(String title, View.OnClickListener backBtnClickListener) {
        TextView tvTitleBarTitle = findViewById(R.id.tv_titleBar_title);
        if (tvTitleBarTitle != null) tvTitleBarTitle.setText(title);
        View backBtn = findViewById(R.id.iv_titleBar_back);
        if (backBtn != null) backBtn.setOnClickListener(backBtnClickListener);
    }

    protected void setTitleBarTitle(String title) {
        setTitleBarTitle(title, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 获取标题栏
     */
    protected View getTitleBar() {
        if (titleBar == null)
            titleBar = rootView.findViewWithTag(getString(R.string.title_bar_tag));
        return titleBar;
    }

    /**
     * 获取“沉浸式状态栏”模式下需要顶到状态栏上的view（也就是与状态栏重叠的view）
     *
     * @return
     */
    protected View getFitsSystemWindowView() {
        View fitsSystemWindowView = titleBar == null ? rootView.findViewWithTag(getString(R.string.fits_system_windows_tag)) : titleBar;//需要顶到状态栏位置上的view
        if (fitsSystemWindowView == null) fitsSystemWindowView = rootView;
        return fitsSystemWindowView;
    }

    /**
     * 设置返回按钮点击事件
     *
     * @param clickListener
     */
    protected void setBackBtnClickListener(View.OnClickListener clickListener) {
        View v = titleBar == null ? rootView : titleBar;
        View backBtn = v.findViewWithTag("back_btn");
        if (backBtn != null) {
            if (clickListener == null) {//默认关闭当前页面
                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            } else {
                backBtn.setOnClickListener(clickListener);
            }
        }
    }

    protected void showUnimplemented() {
        toast("暂未实现");
    }

    protected void toast(String message) {
        if (toast == null) {
            toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    /**
     * 展示等待对话框
     */
    public void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 展示等待对话框
     */
    public void showProgressDialog(String msg) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(msg);
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    /**
     * 隐藏等待对话框
     */
    public void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void startActivity(Class<? extends Activity> activityClass) {
        startActivity(new Intent(this, activityClass));
    }

    /**
     * 检查并申请权限
     *
     * @param callBack
     */
    public void checkAndRequestPermissions(RequestPermissionsCallBack callBack) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {//6.0以下系统无需动态申请权限
            if (callBack != null) callBack.onGranted();
            return;
        }

        List<String> requestPermissionsList = new ArrayList<>();//记录需要申请的权限
        for (String p : callBack.permissions) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {//未授权，记录下来
                requestPermissionsList.add(p);
            }
        }
        if (requestPermissionsList.isEmpty()) {//已授权
            if (callBack != null) callBack.onGranted();
        } else {//申请
            String[] requestPermissionsArray = requestPermissionsList.toArray(new String[requestPermissionsList.size()]);
            requestPermissionsCallBack = callBack;
            ActivityCompat.requestPermissions(this, requestPermissionsArray, callBack.requestCode);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isAllGranted = true;//是否全部已授权
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
                break;
            }
        }
        if (isAllGranted) {//全部已授权
            if (requestPermissionsCallBack != null) {
                requestPermissionsCallBack.onGranted();
                requestPermissionsCallBack = null;
            }
        } else {//被拒绝
            if (requestPermissionsCallBack == null) {
                toast("授权失败");
            } else {
                requestPermissionsCallBack.onDenied(this);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // TODO: 2020/12/15 0015 这里没走 
        Log.d("BaseActivity", "--->onActivityResult: ");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_PERMISSION && requestPermissionsCallBack != null) {
            if (checkPermissions(requestPermissionsCallBack.permissions).isEmpty()) {
                requestPermissionsCallBack.onGranted();
            } else {
                requestPermissionsCallBack.onDenied(this);
            }
            requestPermissionsCallBack = null;
        }
    }


    /**
     * 显示引导设置权限弹窗
     *
     * @param permissionNames 需要设置的权限名
     */
    protected void showPermissionDeniedDialog(String... permissionNames) {
        if (permissionNames.length == 0) return;
        String message = "该功能需要开启如下权限才能使用，是否现在设置？";
        for (String name : permissionNames) {
            message += ("\n" + name);
        }
        AlertDialog permissionDialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("前往设置", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int id) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);//注意就是"package",不用改成自己的包名
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_CODE_OPEN_PERMISSION);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        toast("授权失败");
                    }
                }).create();
        permissionDialog.show();
    }


    /**
     * 检查权限
     *
     * @param permissions
     * @return
     */
    protected List<String> checkPermissions(String... permissions) {
        List<String> list = new ArrayList<>();
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {//未授权，记录下来
                list.add(p);
            }
        }
        return list;
    }

    /**
     * 页面布局文件
     *
     * @return
     */
    protected abstract int getLayoutRes();

    /**
     * 页面初始化
     */
    protected abstract void init();

    /**
     * @Purpose 申请权限后处理接口
     * @Author Naruto Yang
     * @CreateDate 2019/12/19
     * @Note
     */
    public static abstract class RequestPermissionsCallBack {
        public String[] permissions;
        public String[] permissionNames;
        public int requestCode;

        public RequestPermissionsCallBack(String[] permissions, int requestCode) {
            this.permissions = permissions;
            this.requestCode = requestCode;
        }

        public RequestPermissionsCallBack(String[] permissions, String[] permissionNames, int requestCode) {
            this.permissions = permissions;
            this.permissionNames = permissionNames;
            this.requestCode = requestCode;
        }

        /**
         * 已授权
         */
        public abstract void onGranted();

        /**
         * 被拒绝
         */
        public void onDenied(Context context) {
            if (!(context instanceof BaseActivity) || permissionNames == null || permissionNames.length == 0) {
                Toast.makeText(context, "授权失败", Toast.LENGTH_SHORT).show();
            } else {//引导前往设置权限
                BaseActivity activity = (BaseActivity) context;
                activity.showPermissionDeniedDialog(permissionNames);
            }
        }
    }
}
