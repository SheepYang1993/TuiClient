package me.sheepyang.tuiclient.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.socks.library.KLog;

import butterknife.BindView;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.model.bmobentity.UserEntity;
import me.sheepyang.tuiclient.utils.AppUtil;
import me.sheepyang.tuiclient.utils.BmobExceptionUtil;
import me.sheepyang.tuiclient.widget.QBar;

import static me.sheepyang.tuiclient.activity.mine.MineActivity.USER_NICK_NAME;

public class EditNameActivity extends BaseActivity {

    public static final String KEY_NAME = "key_name";
    @BindView(R.id.q_bar)
    QBar mQBar;
    @BindView(R.id.edt_name)
    EditText mEdtName;
    private String mUserNickName;

    @Override
    public int setLayoutId() {
        return R.layout.activity_edit_name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserNickName = getIntent().getStringExtra(USER_NICK_NAME);
        initListener();
        if (!TextUtils.isEmpty(mUserNickName)) {
            KLog.i(mUserNickName);
            mEdtName.setText(mUserNickName);
            mEdtName.setSelection(mUserNickName.length());
        }
    }

    private void initListener() {
        mQBar.setOnRightClickListener((View v) -> {
            saveNickName();
        });
    }

    private void saveNickName() {
        String name = mEdtName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            showMessage("昵称不能为空!");
            return;
        }
        UserEntity newUser = new UserEntity();
        newUser.setNick(name);
        UserEntity user = AppUtil.getUser();
        showDialog("正在修改昵称...");
        newUser.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                closeDialog();
                if (e == null) {
                    showMessage("修改成功！");
                    Intent data = new Intent();
                    data.putExtra(KEY_NAME, name);
                    setResult(RESULT_OK, data);
                    onBackPressed();
                } else {
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }
}
