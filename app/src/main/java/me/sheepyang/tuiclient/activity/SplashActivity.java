package me.sheepyang.tuiclient.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.blankj.utilcode.util.SPUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.app.Constants;
import me.sheepyang.tuiclient.utils.AppUtil;

public class SplashActivity extends BaseActivity {
    private static final int REQUEST_READ_PHONE_STATE = 200;//请求读取手机状态权限
    private Handler mHandler;

    @Override
    public int setLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
    }

    private void initData() {
        AndPermission.with(mActivity)
                .requestCode(REQUEST_READ_PHONE_STATE)
                .permission(Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .rationale((requestCode, rationale) -> {
                            // 此对话框可以自定义，调用rationale.resume()就可以继续申请。
                            AndPermission.rationaleDialog(mActivity, rationale).show();
                        }
                )
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, List<String> grantedPermissions) {
                        // 权限申请成功回调。
                        // 这里的requestCode就是申请时设置的requestCode。
                        // 和onActivityResult()的requestCode一样，用来区分多个不同的请求。
                        if (requestCode == REQUEST_READ_PHONE_STATE) {
                            getPermissionSuccess();
                        }
                    }

                    @Override
                    public void onFailed(int requestCode, List<String> deniedPermissions) {
                        // 权限申请失败回调。
                        if (requestCode == REQUEST_READ_PHONE_STATE) {
                            // 是否有不再提示并拒绝的权限。
                            if (AndPermission.hasAlwaysDeniedPermission(mActivity, deniedPermissions)) {
                                // 第二种：用自定义的提示语。
                                AndPermission.defaultSettingDialog(mActivity, REQUEST_READ_PHONE_STATE)
                                        .setTitle("权限申请失败")
                                        .setMessage("您拒绝了我们必要的一些权限，已经没法愉快的玩耍了，请在设置中开启权限！")
                                        .setPositiveButton("好，去设置")
                                        .setNegativeButton("不了", (DialogInterface dialog, int which) -> {
                                            dialog.dismiss();
                                            AppUtil.exit(mActivity);
                                        })
                                        .show();
                            }
                        }
                    }
                })
                .start();
    }


    @Override
    public void onBackPressed() {

    }

    private void getPermissionSuccess() {
        AppUtil.initBmob(mActivity, Constants.BMOB_APP_ID, "bmob");
        mHandler = new Handler();
        mHandler.postDelayed(() -> {
            if (new SPUtils(Constants.SP_NAME).getInt(Constants.SP_SELECT_SEX, -1) == -1) {
                startActivity(new Intent(SplashActivity.this, SexGuideActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, HomePageActivity.class));
            }
            overridePendingTransition(R.anim.scale_anim_in, R.anim.scale_anim_out);
            finish();
        }, 1000);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE: {
                // 你可以在这里检查你需要的权限是否被允许，并做相应的操作。
                initData();
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        super.onDestroy();
    }
}
