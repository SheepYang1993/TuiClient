package me.sheepyang.tuiclient.activity.mine;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.blankj.utilcode.util.AppUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.model.bmobentity.FeedbackEntity;
import me.sheepyang.tuiclient.utils.AppUtil;
import me.sheepyang.tuiclient.utils.BmobExceptionUtil;


/**
 * 意见反馈
 */
public class FeedbackActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.edt_feedback)
    EditText mEdtFeedback;

    @Override
    public int setLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {

    }

    @Override
    @OnClick({R.id.btn_send})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                toSendFeedback();
                break;
            default:
                break;
        }
    }

    private void toSendFeedback() {
        if (TextUtils.isEmpty(mEdtFeedback.getText().toString().trim())) {
            showMessage("请填写意见反馈~");
            return;
        }

        FeedbackEntity feedbackEntity = new FeedbackEntity();
        feedbackEntity.setVersionName(AppUtils.getAppVersionName(mActivity));
        feedbackEntity.setVersionCode(AppUtils.getAppVersionCode(mActivity));
        feedbackEntity.setUser(AppUtil.getUser());
        feedbackEntity.setContent(mEdtFeedback.getText().toString().trim());
        showDialog("正在传递圣旨...");
        feedbackEntity.save(new SaveListener<String>() {

            @Override
            public void done(String objectId, BmobException e) {
                closeDialog();
                if (e == null) {
                    showMessage("反馈成功");
                    setResult(RESULT_OK);
                    onBackPressed();
                } else {
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }
}
