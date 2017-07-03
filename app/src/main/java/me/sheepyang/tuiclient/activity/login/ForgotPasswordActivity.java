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

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.model.bmobentity.UserEntity;
import me.sheepyang.tuiclient.utils.BmobExceptionUtil;
import me.sheepyang.tuiclient.widget.ClearEditText;


public class ForgotPasswordActivity extends BaseActivity implements View.OnClickListener {

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
        return R.layout.activity_forgot_password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    @OnClick({R.id.iv_left, R.id.tv_get_verify_code, R.id.btn_reset_password})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                onBackPressed();
                break;
            case R.id.tv_get_verify_code:
                getVerifyCode();
                break;
            case R.id.btn_reset_password:
                resetPassword();
                break;
            default:
                break;
        }
    }

    private void resetPassword() {
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
            showMessage("密码太短，至少6个字符");
            return;
        }
        String password = EncryptUtils.encryptMD5ToString(mEdtPassword.getText().toString().trim()).toLowerCase();
        BmobQuery<UserEntity> query = new BmobQuery<UserEntity>();
        query.addWhereEqualTo("mobilePhoneNumber", phone);
        showDialog("正在重置密码...");
        query.findObjects(new FindListener<UserEntity>() {
            @Override
            public void done(List<UserEntity> object, BmobException e) {
                if (e == null) {
                    if (object.size() <= 0) {
                        closeDialog();
                        showMessage("手机号不存在哦~");
                        return;
                    }
                    BmobUser.resetPasswordBySMSCode(mEdtGetCode.getText().toString().trim(), password, new UpdateListener() {

                        @Override
                        public void done(BmobException ex) {
                            closeDialog();
                            if (ex == null) {
                                Log.i("smile", "密码重置成功");
                                showMessage("密码重置成功啦~");
                                setResult(RESULT_OK);
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
        mCurrentTime = 60;
        mTvGetVerifyCode.setText(mCurrentTime + "s");
        mHandler.sendEmptyMessageDelayed(0, 1000);
        mEdtGetCode.setText("");
        showDialog("正在获取验证码...");
        BmobSMS.requestSMSCode(mEdtPhone.getText().toString().trim(), "短信验证", new QueryListener<Integer>() {

            @Override
            public void done(Integer smsId, BmobException ex) {
                closeDialog();
                if (ex == null) {//验证码发送成功
                    Log.i("smile", "短信id：" + smsId);//用于查询本次短信发送详情
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
