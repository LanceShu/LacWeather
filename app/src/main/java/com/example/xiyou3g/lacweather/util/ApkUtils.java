package com.example.xiyou3g.lacweather.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Lance
 * on 2019/1/26.
 */

public class ApkUtils {
    // 获取apk的版本名称;
    public static String getAPKVersionName(Context context, String packageName) {
        if (packageName != null && packageName.length() > 0) {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(packageName, 0);
                return info.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    // 获取apk版本代码；
    public static int getAPKVersionCode(Context context, String packageName) {
        if (packageName != null && packageName.length() > 0) {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(packageName, 0);
                return info.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    // 下载APK;
    public static void downloadAPK(Context context, String url) {
        if (url == null) return;
        try {
            final DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.allowScanningByMediaScanner();
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType("application/vnd.android.package-archive");
            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/lacweather/",
                    "lacweather.apk");
            if (file.exists()) {
                file.delete();
            }
            request.setDestinationInExternalPublicDir(Environment.getExternalStorageDirectory().getAbsolutePath() + "/lacweather/",
                    "lacweather.apk");
            long apkId = downloadManager.enqueue(request);
            SharedPreferences manager = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = manager.edit();
            editor.putLong("apk_id", apkId);
            editor.apply();
        } catch (Exception e) {
            Toast.makeText(context, "下载更新失败", Toast.LENGTH_SHORT).show();
        }
    }
}
