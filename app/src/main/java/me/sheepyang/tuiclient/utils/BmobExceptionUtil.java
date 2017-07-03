package me.sheepyang.tuiclient.utils;

import com.blankj.utilcode.util.ToastUtils;
import com.socks.library.KLog;

import cn.bmob.v3.exception.BmobException;

/**
 * Created by SheepYang on 2017-06-27.
 */

public class BmobExceptionUtil {
    public static void handler(BmobException e) {
        switch (e.getErrorCode()) {
            case 207://验证码错误
                KLog.i("bmob", "207,验证码错误");
                ToastUtils.showShortToast("验证码错误");
                break;
            default:
                KLog.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                ToastUtils.showShortToast("失败：" + e.getMessage() + "," + e.getErrorCode());
                break;
        }
    }
}
