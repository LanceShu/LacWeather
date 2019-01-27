package com.example.xiyou3g.lacweather.receiver;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.webkit.MimeTypeMap;

import java.io.File;

/**
 * Created by Lance
 * on 2019/1/27.
 */

public class UpdataBroadcastRecevier extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        long apkId = sharedPreferences.getLong("apk_id", -2);
        if (downloadId == apkId) {
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadFileUri = manager.getUriForDownloadedFile(downloadId);
            installAPK(context, downloadFileUri);
        }
    }

    // 安装apk；
    private void installAPK(Context context, Uri downloadFileUri) {
        // Android 6.0之前安装方式;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setType("application/vnd.android.package-archive");
            intent.setData(downloadFileUri);
            intent.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else { // android 6.0之后安装方式;
            File file = queryDownloadedAPK(context);
            if (file.exists()) {
                openFile(context, file);
            }
        }
    }

    private File queryDownloadedAPK(Context context) {
        File targetFile = null;
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        long apkId = preferences.getLong("apk_id", -1);
        if (apkId != -1) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(apkId);
            query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
            Cursor cursor = downloadManager.query(query);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String uriString = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                    if (uriString != null) {
                        targetFile = new File(Uri.parse(uriString).getPath());
                    }
                }
                cursor.close();
            }
        }
        return targetFile;
    }

    @SuppressLint("WrongConstant")
    private void openFile(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(268435456);
        intent.setAction("android.intent.action.VIEW");
        String type = getMIMEType(file);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, "lacweather_provider", file);
            intent.setDataAndType(contentUri, type);
            context.startActivity(intent);
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
            context.startActivity(intent);
        }
    }

    private String getMIMEType(File file) {
        String var1 = "";
        String var2 = file.getName();
        String var3 = var2.substring(var2.lastIndexOf(".") + 1, var2.length()).toLowerCase();
        var1 = MimeTypeMap.getSingleton().getMimeTypeFromExtension(var3);
        return var1;
    }
}
