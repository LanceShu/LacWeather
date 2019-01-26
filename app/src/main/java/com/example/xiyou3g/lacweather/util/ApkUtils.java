package com.example.xiyou3g.lacweather.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Lance
 * on 2019/1/26.
 */

public class ApkUtils {
    public static void downloadAPK() {

    }

    public static String getAPKVersionCode(Context context, String packageName) {
        if (packageName != null && packageName.length() > 0) {
            try {
                PackageManager manager = context.getPackageManager();
                PackageInfo info = manager.getPackageInfo(packageName, 0);
                return String.valueOf(info.versionCode);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
