package me.sheepyang.tuiclient.activity.mine;

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

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.utils.BmobExceptionUtil;
import me.sheepyang.tuiclient.widget.ClearEditText;
import me.sheepyang.tuiclient.widget.QBar;


public class EditPasswordActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.q_bar)
    QBar mQBar;
    @BindView(R.id.edt_get_code)
    ClearEditText mEdtGetCode;
    @BindView(R.id.edt_phone)
    ClearEditText mEdtPhone;
    @BindView(R.id.edt_password)
    ClearEditText mEdtPassword;
    @BindView(R.id.tv_get_verify_code)
    TextView mTvGetVerifyCode;
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
        return R.layout.activity_edit_password;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
    }

    private void initListener() {
        mQBar.setOnRightClickListener((View v) -> {
            modifyPassword();
        });
    }

    private void modifyPassword() {
        KeyboardUtils.hideSoftInput(this);
        if (TextUtils.isEmpty(mEdtPhone.getText().toString().trim())) {
            showMessage("请输入手机号码");
            return;
        }
        if (!RegexUtils.isMobileExact(mEdtPhone.getText().toString().trim())) {
            showMessage("手机号码格式不正确");
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
        String password = EncryptUtils.encryptMD5ToString(mEdtPassword.getText().toString().trim()).toLowerCase();
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
                    BmobExceptionUtil.handler(ex);
                }
            }
        });
//        OkGo.post(Api.RESET_PASSWORD)
//                .tag(this)
//                .params("e.phone", mEdtPhone.getText().toString().trim())
//                .params("e.acode", mEdtGetCode.getText().toString())
//                .params("e.password", EncryptUtils.encryptMD5ToString(mEdtPassword.getText().toString().trim()).toLowerCase())
//                .execute(new JsonCallback<BaseResponse>() {
//                    @Override
//                    public void onSuccess(BaseResponse baseResponse, Call call, Response response) {
//                        if (baseResponse.isTrue(mContext)) {
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

    private void initView() {

    }

    @Override
    @OnClick({R.id.tv_get_verify_code})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_get_verify_code:
                getVerifyCode();
                break;
            default:
                break;
        }
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
                mTvGetVerifyCode.setEnabled(true);
                if (ex == null) {//验证码发送成功
                    Log.i("smile", "短信id：" + smsId);//用于查询本次短信发送详情
                    mCurrentTime = 60;
                    mTvGetVerifyCode.setText(mCurrentTime + "s");
                    mHandler.sendEmptyMessageDelayed(0, 1000);
                    showMessage("验证码已发送");
                    mEdtGetCode.setText("");
                    mTempPhone = mEdtPhone.getText().toString().trim();
                } else {
                    BmobExceptionUtil.handler(ex);
                }
            }
        });
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

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        super.onDestroy();
    }
}
