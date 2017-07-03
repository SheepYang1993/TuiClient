package me.sheepyang.tuiclient.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.socks.library.KLog;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import me.sheepyang.tuiclient.activity.login.LoginActivity;
import me.sheepyang.tuiclient.model.bmobentity.UserEntity;


/**
 * Created by SheepYang on 2017-06-20.
 */

public class AppUtil {
    public static final int TO_LOGIN = 0x0123;

    public static void initBmob(Context context, String appkey, String channel) {
        Bmob.initialize(context, appkey, channel);
        //第二：自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
        //BmobConfig config =new BmobConfig.Builder(this)
        ////设置appkey
        //.setApplicationId("Your Application ID")
        ////请求超时时间（单位为秒）：默认15s
        //.setConnectTimeout(30)
        ////文件分片上传时每片的大小（单位字节），默认512*1024
        //.setUploadBlockSize(1024*1024)
        ////文件的过期时间(单位为秒)：默认1800s
        //.setFileExpiration(2500)
        //.build();
        //Bmob.initialize(config);
    }

    public static void exit(Context context) {
        AppManager.getAppManager().AppExit(context.getApplicationContext());
    }

    public static Boolean isUserVip() {
        UserEntity user = BmobUser.getCurrentUser(UserEntity.class);
        return user != null && user.getVip() != null && user.getVip();
    }

    public static Boolean isUserLogin(Context context, boolean isToLogin) {
        UserEntity user = BmobUser.getCurrentUser(UserEntity.class);
        if (user == null && isToLogin) {
            KLog.i();
            if (context instanceof Activity) {
                ((Activity) context).startActivityForResult(new Intent(context, LoginActivity.class), TO_LOGIN);
            }
        }
        return user != null;
    }
}
