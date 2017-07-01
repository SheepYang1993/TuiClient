package me.sheepyang.tuiclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.SPUtils;
import com.socks.library.KLog;

import butterknife.BindView;
import butterknife.OnClick;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.app.Constants;
import me.sheepyang.tuiclient.utils.AppUtil;

public class SexGuideActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.ll_man)
    LinearLayout mLlMan;
    @BindView(R.id.ll_woman)
    LinearLayout mLlWoman;
    @BindView(R.id.tv_open)
    TextView mTvOpen;
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_desc)
    TextView mTvDesc;
    @BindView(R.id.ll_select)
    LinearLayout mLlSelect;
    @BindView(R.id.tv_select_hint)
    TextView mTvSelectHint;
    private long mCurrentTime;

    @Override
    public int setLayoutId() {
        return R.layout.activity_sex_guide;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startAnim();
    }

    private void startAnim() {
        AnimationSet textAnimSet = new AnimationSet(true);
        Animation a = AnimationUtils.loadAnimation(this, R.anim.anim_text_up);
        Animation b = AnimationUtils.loadAnimation(this, R.anim.anim_text_alpha);
        textAnimSet.addAnimation(a);
        textAnimSet.addAnimation(b);
        mTvTitle.startAnimation(textAnimSet);
        mTvDesc.startAnimation(textAnimSet);
        mLlSelect.startAnimation(b);
        mTvSelectHint.startAnimation(b);
        mTvOpen.startAnimation(b);
    }

    @Override
    public void onBackPressed() {
        KLog.i(Constants.TAG, System.currentTimeMillis() - mCurrentTime);
        if (System.currentTimeMillis() - mCurrentTime < 2000) {
            mCurrentTime = 0;
            AppUtil.exit(mActivity);
        } else {
            mCurrentTime = System.currentTimeMillis();
            showMessage("再次点击退出APP");
        }
    }

    @Override
    @OnClick({R.id.ll_man, R.id.ll_woman, R.id.tv_open})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_man:
                mLlMan.setSelected(!mLlMan.isSelected());
                mLlWoman.setSelected(false);
                if (mLlMan.isSelected() || mLlWoman.isSelected()) {
                    mTvOpen.setText("开启社区");
                } else {
                    mTvOpen.setText("跳过");
                }
                break;
            case R.id.ll_woman:
                mLlWoman.setSelected(!mLlWoman.isSelected());
                mLlMan.setSelected(false);
                if (mLlMan.isSelected() || mLlWoman.isSelected()) {
                    mTvOpen.setText("开启社区");
                } else {
                    mTvOpen.setText("跳过");
                }
                break;
            case R.id.tv_open:
                saveSex();
                break;
            default:
                break;
        }
    }

    private void saveSex() {
        int type = 3;
        if (mLlMan.isSelected()) {
            type = 1;
        } else if (mLlWoman.isSelected()) {
            type = 2;
        }
        new SPUtils(Constants.SP_NAME).put(Constants.SP_SELECT_SEX, type);
        startActivity(new Intent(SexGuideActivity.this, HomePageActivity.class));
        overridePendingTransition(R.anim.scale_anim_in, R.anim.scale_anim_out);
        finish();
    }
}
