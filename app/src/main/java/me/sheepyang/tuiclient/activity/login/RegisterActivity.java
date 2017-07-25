package me.sheepyang.tuiclient.activity.login;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.EncryptUtils;
import com.blankj.utilcode.util.KeyboardUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SPUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.app.Constants;
import me.sheepyang.tuiclient.model.bmobentity.UserEntity;
import me.sheepyang.tuiclient.utils.AppUtil;
import me.sheepyang.tuiclient.utils.BmobExceptionUtil;
import me.sheepyang.tuiclient.widget.ClearEditText;


public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.edt_get_code)
    ClearEditText mEdtGetCode;
    @BindView(R.id.edt_phone)
    ClearEditText mEdtPhone;
    @BindView(R.id.edt_password)
    ClearEditText mEdtPassword;
    @BindView(R.id.tv_get_verify_code)
    TextView mTvGetVerifyCode;
    private int mCurrentTime;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mCurrentTime--;
            mTvGetVerifyCode.setText(mCurrentTime + "s");
            if (mCurrentTime > 0) {
                mHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
                mTvGetVerifyCode.setEnabled(true);
                mTvGetVerifyCode.setText("获取验证码");
            }
        }
    };

    @Override
    public int setLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @OnClick({R.id.tv_get_verify_code, R.id.btn_register, R.id.tv_has_account, R.id.tv_login})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_get_verify_code:
                getVerifyCode();
                break;
            case R.id.btn_register:
                register();
                break;
            case R.id.tv_login:
            case R.id.tv_has_account:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private void register() {
        KeyboardUtils.hideSoftInput(this);
        String phone = mEdtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            showMessage("请输入手机号码");
            return;
        }
        if (!RegexUtils.isMobileExact(phone)) {
            showMessage("手机号码格式不正确");
            return;
        }
        if (TextUtils.isEmpty(mEdtGetCode.getText().toString())) {
            showMessage("请输入验证码");
            return;
        }
        if (mEdtGetCode.getText().toString().trim().length() < 6) {
            showMessage("验证码不能少于6位");
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
        BmobQuery<UserEntity> query = new BmobQuery<UserEntity>();
        query.addWhereEqualTo("mobilePhoneNumber", phone);
        showDialog("正在注册账号...");
        query.findObjects(new FindListener<UserEntity>() {
            @Override
            public void done(List<UserEntity> object, BmobException e) {
                if (e == null) {
                    if (object.size() > 0) {
                        closeDialog();
                        showMessage("该手机号已经被注册啦~");
                        return;
                    }
                    UserEntity user = new UserEntity();
                    user.setMobilePhoneNumber(phone);//设置手机号码（必填）
                    user.setPassword(password);                  //设置用户密码
                    user.setHabit(new SPUtils(Constants.SP_NAME).getInt(Constants.SP_SELECT_SEX, 0));
                    user.setNick(phone);
                    user.setLevel(0);
                    user.signOrLogin(mEdtGetCode.getText().toString(), new SaveListener<UserEntity>() {

                        @Override
                        public void done(UserEntity user, BmobException e) {
                            closeDialog();
                            if (e == null) {
                                //注册成功
                                AppUtil.logout();
                                setResult(RESULT_OK);
                                showMessage("注册成功");
                                onBackPressed();
                            } else {
                                BmobExceptionUtil.handler(e);
                            }
                        }
                    });
                } else {
                    closeDialog();
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }

    private void getVerifyCode() {
        if (TextUtils.isEmpty(mEdtPhone.getText().toString().trim())) {
            showMessage("请输入手机号码");
            return;
        }
        if (!RegexUtils.isMobileExact(mEdtPhone.getText().toString().trim())) {
            showMessage("手机号码格式不正确");
            return;
        }
        mTvGetVerifyCode.setEnabled(false);
        showDialog("正在获取验证码...");
        BmobSMS.requestSMSCode(mEdtPhone.getText().toString().trim(), "短信验证", new QueryListener<Integer>() {

            @Override
            public void done(Integer smsId, BmobException ex) {
                closeDialog();
                if (ex == null) {//验证码发送成功
                    Log.i("smile", "短信id：" + smsId);//用于查询本次短信发送详情
                    mCurrentTime = 60;
                    mTvGetVerifyCode.setText(mCurrentTime + "s");
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                    mEdtGetCode.setText("");
                    showMessage("验证码已发送");
                    mTvGetVerifyCode.setEnabled(true);
                } else {
                    BmobExceptionUtil.handler(ex);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        super.onDestroy();
    }
}
