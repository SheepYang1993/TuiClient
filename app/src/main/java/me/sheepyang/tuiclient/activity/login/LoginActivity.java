package me.sheepyang.tuiclient.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.RegexUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import me.sheepyang.tuiclient.BuildConfig;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.model.bmobentity.UserEntity;
import me.sheepyang.tuiclient.utils.BmobExceptionUtil;
import me.sheepyang.tuiclient.widget.ClearEditText;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.edt_phone)
    ClearEditText mEdtPhone;
    @BindView(R.id.edt_password)
    ClearEditText mEdtPassword;

    @Override
    public int setLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.LOG_DEBUG) {
//            mEdtPhone.setText("13055253351");
//            mEdtPhone.setText("18649795408");
//            mEdtPassword.setText("123456");
        }
    }

    @Override
    @OnClick({R.id.btn_login, R.id.tv_no_account, R.id.tv_register_now, R.id.tv_forgot_password})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                toLogin();
                break;
            case R.id.tv_no_account:
            case R.id.tv_register_now:
                startActivity(new Intent(mActivity, RegisterActivity.class));
                break;
            case R.id.tv_forgot_password:
                startActivity(new Intent(mActivity, ForgotPasswordActivity.class));
                break;
            default:
                break;
        }
    }

    private void toLogin() {
        KeyboardUtils.hideSoftInput(this);
        if (TextUtils.isEmpty(mEdtPhone.getText().toString().trim())) {
            showMessage("请输入手机号码");
            return;
        }
        if (!RegexUtils.isMobileExact(mEdtPhone.getText().toString().trim())) {
            showMessage("手机号码格式不正确");
            return;
        }
        if (TextUtils.isEmpty(mEdtPassword.getText().toString().trim())) {
            showMessage("请输入密码");
            return;
        }
        if (mEdtPassword.getText().toString().trim().length() < 6) {
            showMessage("密码不能少于6位");
            return;
        }
        String password = EncryptUtils.encryptMD5ToString(mEdtPassword.getText().toString().trim()).toLowerCase();
        showDialog("正在登陆...");
        BmobUser.loginByAccount(mEdtPhone.getText().toString().trim(), password, new LogInListener<UserEntity>() {

            @Override
            public void done(UserEntity user, BmobException e) {
                closeDialog();
                if (e != null) {
                    UserEntity myUser = BmobUser.getCurrentUser(UserEntity.class);
                    if (myUser != null) {
                        showMessage("登陆成功");
                        setResult(RESULT_OK);
                        onBackPressed();
                    } else {
                        showMessage("登陆失败，账号或密码错误");
                    }
                } else {
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }
}