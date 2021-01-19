package com.naruto.templateproject.example.activity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.naruto.templateproject.R;
import com.naruto.templateproject.common.base.DataBindingActivity;
import com.naruto.templateproject.common.utils.CameraHelper;
import com.naruto.templateproject.databinding.ActivityTakePhotoBinding;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Purpose 拍照
 * @Author Naruto Yang
 * @CreateDate 2021/1/4 0004
 * @Note
 */
public class TakePhotoActivity extends DataBindingActivity<ActivityTakePhotoBinding> {
    private static final int REQUEST_CODE_TAKE_PHOTO = 10;
    private Uri photoURI;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_take_photo;
    }

    @Override
    protected void init() {
        setTitleBarTitle("拍照");
    }

    public void takePhoto(View view) {
        RequestPermissionsCallBack callBack = new RequestPermissionsCallBack(
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_TAKE_PHOTO) {
            @Override
            public void onGranted() {
                CameraHelper.CameraOptions options = new CameraHelper.CameraOptions()
                        .folderName("templateProject")
//                        .isPrivate(true)
                        ;
                photoURI = CameraHelper.openCamera666(TakePhotoActivity.this, options, REQUEST_CODE_TAKE_PHOTO);
            }
        };

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            checkAndRequestPermissions(callBack);
        } else {
            callBack.onGranted();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_TAKE_PHOTO && resultCode == RESULT_OK) {
            if (photoURI != null) {
                dataBinding.image.setImageURI(photoURI);
            }
        }
    }
}