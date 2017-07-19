package me.sheepyang.tuiclient.activity.mine;

import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.SPUtils;
import com.suke.widget.SwitchButton;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.app.Constants;
import me.sheepyang.tuiclient.model.bmobentity.UserEntity;
import me.sheepyang.tuiclient.utils.AppUtil;
import me.sheepyang.tuiclient.utils.BmobExceptionUtil;
import me.sheepyang.tuiclient.widget.QBar;

public class SelectSexActivity extends BaseActivity implements View.OnClickListener, SwitchButton.OnCheckedChangeListener {

    @BindView(R.id.sb_all)
    SwitchButton mSbAll;
    @BindView(R.id.sb_woman)
    SwitchButton mSbWoman;
    @BindView(R.id.sb_man)
    SwitchButton mSbMan;
    @BindView(R.id.q_bar)
    QBar mQBar;

    @Override
    public int setLayoutId() {
        return R.layout.activity_select_sex;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        initData();
    }

    private void initData() {
        int type = new SPUtils(Constants.SP_NAME).getInt(Constants.SP_SELECT_SEX, 0);
        switch (type) {
            case 0://0全部;
                mSbAll.setChecked(true);
                break;
            case 1://1只看女生;
                mSbWoman.setChecked(true);
                break;
            case 2://2只看男生;
                mSbMan.setChecked(true);
                break;
            default:
                mSbAll.setChecked(true);
                break;
        }
    }

    private void initView() {
        mQBar.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSex();
            }
        });
    }

    private void saveSex() {
        if (!mSbAll.isChecked() && !mSbWoman.isChecked() && !mSbMan.isChecked()) {
            showMessage("请至少选择一项~");
            return;
        }
        int type = 0;
        if (mSbAll.isChecked()) {
            type = 0;
        }
        if (mSbWoman.isChecked()) {
            type = 1;
        }
        if (mSbMan.isChecked()) {
            type = 2;
        }
        new SPUtils(Constants.SP_NAME).put(Constants.SP_SELECT_SEX, type);
        if (AppUtil.isUserLogin(mActivity, false)) {
            UserEntity newUser = new UserEntity();
            newUser.setHabit(type);
            showDialog("正在保存喜好...");
            newUser.update(AppUtil.getUser().getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    closeDialog();
                    if (e == null) {
                        showMessage("保存成功");
                        setResult(RESULT_OK);
                        onBackPressed();
                    } else {
                        BmobExceptionUtil.handler(e);
                    }
                }
            });
        } else {
            closeDialog();
            showMessage("保存成功");
            setResult(RESULT_OK);
            onBackPressed();
        }
    }

    private void initListener() {
        mSbAll.setOnCheckedChangeListener(this);
        mSbWoman.setOnCheckedChangeListener(this);
        mSbMan.setOnCheckedChangeListener(this);
        mSbAll.setEnableEffect(false);
        mSbWoman.setEnableEffect(false);
        mSbMan.setEnableEffect(false);
    }

    @Override
    @OnClick({R.id.rl_check_all, R.id.rl_check_woman, R.id.rl_check_man})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_check_all:
                mSbAll.toggle(false);
                break;
            case R.id.rl_check_woman:
                mSbWoman.toggle(false);
                break;
            case R.id.rl_check_man:
                mSbMan.toggle(false);
                break;
            default:
                break;
        }
    }

    @Override
    public void onCheckedChanged(SwitchButton view, boolean isChecked) {
        try {
            if (isChecked) {
                switch (view.getId()) {
                    case R.id.sb_all:
                        mSbMan.setChecked(false);
                        mSbWoman.setChecked(false);
                        break;
                    case R.id.sb_woman:
                        mSbMan.setChecked(false);
                        mSbAll.setChecked(false);
                        break;
                    case R.id.sb_man:
                        mSbAll.setChecked(false);
                        mSbWoman.setChecked(false);
                        break;
                    default:
                        break;
                }
            } else {
                if (!mSbAll.isChecked() && !mSbWoman.isChecked() && !mSbMan.isChecked()) {
                    showMessage("请至少选择一项~");
                }
            }
        } catch (RuntimeException e) {
            showMessage("点击太快啦~");
        }
    }
}
