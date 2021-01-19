package com.naruto.templateproject.common.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 相机相关功能辅助类
 */
public class CameraHelper {

    public static Uri openCamera(String filePath, String fileName, Activity context, int requestCode) {
        return openCamera0(filePath, fileName, context, requestCode);
    }

    public static Uri openCamera(String filePath, String fileName, Fragment fragment, int requestCode) {
        return openCamera0(filePath, fileName, fragment, requestCode);
    }

    /**
     * 打开相机(注意，这里要使用此方法返回的Uri获取照片，onActivityResult中的data为null)
     *
     * @param filePath      照片保存路径
     * @param fileName      照片名称
     * @param contextObject activity上下文或fragment
     * @param requestCode   请求码，用于返回数据识别
     * @return
     */
    private static Uri openCamera0(String filePath, String fileName, Object contextObject, int requestCode) {
        Uri imageUri = null;
        File tempFile = getFile(filePath, fileName);
        if (tempFile == null) {
            return null;
        }

        // 激活相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //兼容android7.0 使用共享文件的形式
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
        if (contextObject instanceof Activity) {
            Activity activity = (Activity) contextObject;
            imageUri = activity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            activity.startActivityForResult(intent, requestCode);
        } else if (contextObject instanceof Fragment) {
            Fragment fragment = (Fragment) contextObject;
            imageUri = fragment.getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            fragment.startActivityForResult(intent, requestCode);
        }
        return imageUri;
    }

    /**
     * 判断是否有存储设备
     *
     * @return
     */
    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 打开相册界面
     *
     * @param context     activity上下文
     * @param requestCode 请求码，用于返回数据识别
     * @return
     */
    public static void openAlbum(Activity context, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        context.startActivityForResult(intent, requestCode);
    }

    /**
     * 打开相册界面
     *
     * @param fragment
     * @param requestCode
     */
    public static void openAlbum(Fragment fragment, int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        fragment.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取相册照片路径
     *
     * @param context 上下文
     * @param data    onActivityResult中返回的数据
     * @return
     */
    public static String getAlbumPhotoPath(Context context, Intent data) {
        //获取系统返回的照片的Uri
        Uri selectedImageUri = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImageUri,
                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String path = cursor.getString(columnIndex);  //获取照片路径
        cursor.close();
        return path;
    }

    /**
     * 获取相册照片路径
     *
     * @param context 上下文
     * @param uri     uri
     * @return
     */
    public static String getAlbumPhotoPath(Context context, Uri uri) {
        //获取系统返回的照片的Uri
        Uri selectedImageUri = uri;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(selectedImageUri,
                filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String path = cursor.getString(columnIndex);  //获取照片路径
        cursor.close();
        return path;
    }

    /**
     * 剪裁照片 Tips:千万要注意，不能在onActivityResult方法中使用data.getData()获取数据，而是要用此方法返回的uri获取裁剪后的数据
     *
     * @param context     activity
     * @param imageUri    原图片
     * @param outFilePath 输出路径
     * @param outFileName 输出文件名
     * @param aspectX     宽度比例
     * @param aspectY     高度比例
     * @param requestCode 请求码
     * @return
     */
    public static Uri cropPhoto(Activity context, Uri imageUri, String outFilePath, String outFileName, int aspectX, int aspectY, int requestCode) {
        if (imageUri == null) {
            return null;
        }
        Uri outputUri;
        File tempFile = getFile(outFilePath, outFileName);
        if (tempFile == null) {
            return null;
        }
        // 裁剪图片
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");//显示裁剪
        intent.putExtra("aspectX", aspectX);//裁剪的比例
        intent.putExtra("aspectY", aspectY);//裁剪的比例
//        intent.putExtra("outputX", 600);//裁剪的尺寸
//        intent.putExtra("outputY", 500);//裁剪的尺寸
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);//true如果图片太大会导致oom，所以这里设置为false
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//压缩
        intent.putExtra("noFaceDetection", false); // no face detection无人脸检测

        //兼容android7.0 使用共享文件的形式
        ContentValues contentValues = new ContentValues(1);
        contentValues.put(MediaStore.Images.Media.DATA, tempFile.getAbsolutePath());
        outputUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        context.startActivityForResult(intent, requestCode);

        return outputUri;
    }

    /**
     * 获取文件
     *
     * @param path 路径
     * @param name 名称
     * @return
     */
    public static File getFile(String path, String name) {
        //判断是否有存储卡
        if (!hasSdcard()) {
            return null;
        }

        //文件夹操作
        File dirFile = new File(path);
        //创建文件夹
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        //创建文件
        File tempFile = new File(dirFile, name);

        if (tempFile.exists()) {
            tempFile.delete();
        }
        try {
            tempFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempFile;
    }

    public static Uri openCamera666(Activity activity,CameraOptions options, int requestCode) {
        Uri photoURI = null;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI = createFile(activity,options));
            activity.startActivityForResult(takePictureIntent, requestCode);
        }
        return photoURI;
    }

    private static Uri createFile(Context context,CameraOptions options) {
        Uri uri = null;
        String folderName = options.folderName;
        String fileName = options.fileNamePrefix + new SimpleDateFormat(options.timeStampFormat).format(new Date());
        if (options.isPrivate) {//应用私有目录
            uri = createFileOld(context,folderName, fileName, options);
        } else {//系统公共目录
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                uri = createFileOld(context,folderName, fileName, options);
            } else {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, options.directoryType + "/" + folderName);
                uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            }
        }
        return uri;
    }


    private static Uri createFileOld(Context context, String folderName, String fileName, CameraOptions options) {
        File folder = options.isPrivate ? context.getExternalFilesDir(options.directoryType)
                : Environment.getExternalStoragePublicDirectory(options.directoryType);
        String folderPath = folder.getAbsolutePath();
        File storageDir = new File(folderPath += "/" + folderName);
        if (!storageDir.exists()) storageDir.mkdirs();
        File file = new File(folderPath + "/" + fileName + options.fileNameSuffix);
/*        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return FileProvider.getUriForFile(context, context.getPackageName() + ".fileProvider", file);
    }


    /**
     * @Purpose 照片存储设置
     * @Author Naruto Yang
     * @CreateDate 2021/1/4 0004
     * @Note
     */
    public static class CameraOptions {
        boolean isPrivate = false;//存储在系统公共目录还是应用私有目录
        String directoryType = Environment.DIRECTORY_PICTURES;
        String folderName = "";//文件夹名称
        String timeStampFormat = "yyyyMMdd_HHmmss";//时间戳格式
        String fileNamePrefix = "IMG_";//文件名前缀
        String fileNameSuffix = ".jpg";//文件名后缀

        public CameraOptions isPrivate(boolean aPrivate) {
            isPrivate = aPrivate;
            return this;
        }

        public CameraOptions folderName(String folderName) {
            this.folderName = folderName;
            return this;
        }

        public CameraOptions timeStampFormat(String timeStampFormat) {
            this.timeStampFormat = timeStampFormat;
            return this;
        }

        public CameraOptions fileNamePrefix(String fileNamePrefix) {
            this.fileNamePrefix = fileNamePrefix;
            return this;
        }

        public CameraOptions fileNameSuffix(String fileNameSuffix) {
            this.fileNameSuffix = fileNameSuffix;
            return this;
        }
    }
}
