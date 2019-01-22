package com.example.xiyou3g.lacweather.util;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.xiyou3g.lacweather.R;

/**
 * Created by Lance
 * on 2019/1/22.
 */

public class DialogUtils {

    public static void showShareDialog(final Context context, final String filePath) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.share_wechat_dialog);
        LinearLayout wechatFriends = (LinearLayout) dialog.findViewById(R.id.share_wechat_friends);
        LinearLayout wechatTimeline = (LinearLayout) dialog.findViewById(R.id.share_wechat_timeline);
        ImageView closeDialog = (ImageView) dialog.findViewById(R.id.share_close_btn);
        wechatFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeChatUtils.getIWXAPIInstance(context);
                WeChatUtils.sendImageToWeChat(filePath, WeChatUtils.WECHAT_FRIENDS);
            }
        });
        wechatTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeChatUtils.getIWXAPIInstance(context);
                WeChatUtils.sendImageToWeChat(filePath, WeChatUtils.WECHAT_TIMELINE);
            }
        });
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
