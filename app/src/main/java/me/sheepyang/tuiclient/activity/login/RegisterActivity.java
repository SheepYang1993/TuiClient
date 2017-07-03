package me.sheepyang.tuiclient.activity.login;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.blankj.utilcode.util.KeyboardUtils;

import butterknife.BindView;
import butterknife.OnClick;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
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
    private String mVerifyCode;
    private String mTempPhone;
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
                resetPassword();
                break;
            case R.id.tv_login:
            case R.id.tv_has_account:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    private void resetPassword() {
        KeyboardUtils.hideSoftInput(this);
        if (TextUtils.isEmpty(mEdtPhone.getText().toString().trim())) {
            showMessage("请输入手机号码");
            return;
        }
        if (TextUtils.isEmpty(mVerifyCode)) {
            showMessage("请等待获取验证码");
            return;
        }
        if (TextUtils.isEmpty(mEdtGetCode.getText().toString())) {
            showMessage("请输入验证码");
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
        if (!mTempPhone.equals(mEdtPhone.getText().toString().trim())) {
            showMessage("手机号码已修改，请重新获取验证码");
            return;
        }
        if (!mVerifyCode.equals(mEdtGetCode.getText().toString())) {
            showMessage("验证码不正确");
            return;
        }
//        OkGo.post(Api.REGISTER)
//                .tag(this)
//                .params("e.phone", mEdtPhone.getText().toString().trim())
//                .params("e.acode", mEdtGetCode.getText().toString())
//                .params("e.userlike", AppUtil.getUserLike())
//                .params("e.password", EncryptUtils.encryptMD5ToString(mEdtPassword.getText().toString().trim()).toLowerCase())
//                .execute(new JsonCallback<BaseResponse>() {
//                    @Override
//                    public void onSuccess(BaseResponse baseResponse, Call call, Response response) {
//                        if (baseResponse.isTrue(mContext)) {
//                            showToast("注册成功！");
//                            setResult(RESULT_OK);
//                            onBackPressed();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        super.onError(call, response, e);
//                        ExceptionUtil.handleException(mContext, response, e);
//                    }
//                });
    }

    private void getVerifyCode() {
        if (TextUtils.isEmpty(mEdtPhone.getText().toString().trim())) {
            showMessage("请输入手机号码");
            return;
        }
        mTvGetVerifyCode.setEnabled(false);
        mCurrentTime = 10;
        mTvGetVerifyCode.setText(mCurrentTime + "s");
        mHandler.sendEmptyMessageDelayed(0, 1000);
        showMessage("验证码已发送");
        mEdtGetCode.setText("");
        mVerifyCode = "";
        mTempPhone = mEdtPhone.getText().toString().trim();
//        OkGo.post(Api.GET_VERIFY_CODE)
//                .tag(this)
//                .params("e.phone", mTempPhone)
//                .execute(new StringCallback() {
//                    @Override
//                    public void onSuccess(String s, Call call, Response response) {
//                        mVerifyCode = s;
//                        showToast("验证码：" + mVerifyCode);
//                    }
//                });
    }
}
