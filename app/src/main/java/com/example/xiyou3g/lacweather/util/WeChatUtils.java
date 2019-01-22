package com.example.xiyou3g.lacweather.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by Lance
 * on 2019/1/22.
 */

public class WeChatUtils {
    private static final String APP_ID = "wx02e8114c29ce3765";
    private static IWXAPI iwxapi;
    public static final int WECHAT_FRIENDS = 0;
    public static final int WECHAT_TIMELINE = 1;

    private WeChatUtils() {}

    public static IWXAPI getIWXAPIInstance(Context context) {
        if (iwxapi == null) {
            synchronized (WeChatUtils.class) {
                if (iwxapi == null) {
                    iwxapi = WXAPIFactory.createWXAPI(context, APP_ID, true);
                    iwxapi.registerApp(APP_ID);
                }
            }
        }
        return iwxapi;
    }

    // 将图片转换成二进制流；
    private static byte[] getByteArrayByBitmap(Bitmap bitmap, boolean isRecycle) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bitmap.getByteCount());
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (isRecycle) bitmap.recycle();
        return baos.toByteArray();
    }

    // 分享图片到微信；
    public static void sendImageToWeChat(String filePath, int type) {
        WXImageObject imgObj = new WXImageObject();
        imgObj.setImagePath(filePath);
        WXMediaMessage imgMes = new WXMediaMessage();
        imgMes.mediaObject = imgObj;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        Bitmap thumBmp = Bitmap.createScaledBitmap(bitmap, 180, 360, true);
        bitmap.recycle();
        imgMes.thumbData = getByteArrayByBitmap(thumBmp, true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("img");
        req.message = imgMes;
        if (type == 0) {
            req.scene = SendMessageToWX.Req.WXSceneSession;
        } else {
            req.scene = SendMessageToWX.Req.WXSceneTimeline;
        }
        iwxapi.sendReq(req);
    }

    private static String buildTransaction(String type) {
        return type == null ? String.valueOf(System.currentTimeMillis())
                : type + System.currentTimeMillis();
    }
}
