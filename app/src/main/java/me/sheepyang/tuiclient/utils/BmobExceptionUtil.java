package me.sheepyang.tuiclient.utils;

import com.blankj.utilcode.util.ToastUtils;
import com.socks.library.KLog;

import cn.bmob.v3.exception.BmobException;

/**
 * Created by SheepYang on 2017-06-27.
 */

public class BmobExceptionUtil {
    public static void handler(BmobException e) {
        if (e == null) {
            ToastUtils.showShortToast("出错啦~");
            return;
        }
        switch (e.getErrorCode()) {
            case 100://something wrong with your code
            case 502://查询失败
            case 9015://其他错误均返回此code
                KLog.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                ToastUtils.showShortToast("服务器又炸啦~再刷新下试试~");
                break;
            case 101://没有找到表
                KLog.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                if ("username or password incorrect.".equals(e.getMessage())) {
                    ToastUtils.showShortToast("用户名或密码错误~");
                } else {
                    ToastUtils.showShortToast("没有查询到数据表，请联系管理员~");
                }
                break;
            case 202://用户名已存在
                KLog.i("bmob", "202, 用户名已存在");
                ToastUtils.showShortToast("用户名已存在");
                break;
            case 207://验证码错误
                KLog.i("bmob", "207, 验证码错误");
                ToastUtils.showShortToast("验证码错误");
                break;
            case 9016://验证码错误
                KLog.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                ToastUtils.showShortToast("网络链接异常，请检查网络");
                break;
            default:
                KLog.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                ToastUtils.showShortToast("失败：" + e.getMessage() + "," + e.getErrorCode());
                break;
        }
    }
}
