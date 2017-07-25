package me.sheepyang.tuiclient.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.model.bmobentity.UserEntity;
import me.sheepyang.tuiclient.utils.AppUtil;
import me.sheepyang.tuiclient.utils.GlideApp;
import me.sheepyang.tuiclient.utils.transformation.GlideCircleTransform;
import me.sheepyang.tuiclient.widget.dialog.QDialog;

import static me.sheepyang.tuiclient.utils.AppUtil.TO_LOGIN;


/**
 * 个人中心
 */
public class MineActivity extends BaseActivity implements View.OnClickListener {
    public static final String USER_AVATAR = "user_avatar";
    public static final String USER_NICK_NAME = "user_nick_name";
    private static final int TO_SELECT_SEX = 0x0012;
    public static final int RESULT_REFRESH = 0x0013;
    private static final int TO_EDIT_INFO = 0x0014;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.iv_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.tv_nick_name)
    TextView mTvNickName;
    @BindView(R.id.tv_logout)
    TextView mTvLogout;
    private QDialog mHintDialog;

    @Override
    public int setLayoutId() {
        return R.layout.activity_mine;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        if (AppUtil.isUserLogin(mActivity, false)) {
            getUserInfo();
        } else {
            mTvNickName.setText("未登录");
            mTvLogout.setText("立即登录");
        }
    }

    private void getUserInfo() {
        UserEntity user = BmobUser.getCurrentUser(UserEntity.class);
        if (user != null) {
            if (!TextUtils.isEmpty(user.getNick())) {
                mTvNickName.setText(user.getNick());
            } else {
                mTvNickName.setText("");
            }
            String avatarPath = "";
            if (user.getAvatar() != null && !TextUtils.isEmpty(user.getAvatar().getFileUrl())) {
                avatarPath = user.getAvatar().getFileUrl();
            }
            GlideApp.with(mActivity)
                    .load(avatarPath)
                    .transform(new GlideCircleTransform(mActivity))
                    .placeholder(R.drawable.ico_user_avatar)
                    .into(mIvAvatar);
        }
        if (AppUtil.isUserLogin(mActivity, false)) {
            mTvLogout.setText("退出登录");
        } else {
            mTvLogout.setText("立即登录");
        }
        mRefreshLayout.setRefreshing(false);
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(() -> {
            getUserInfo();
        });
        mHintDialog.setOnRightClickListener((DialogInterface dialog, int which) -> {
            AppUtil.logout();
            onBackPressed();
        });
    }

    private void initView() {
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        mHintDialog = new QDialog(mActivity);
        mHintDialog.setTitle("退出当前账号？");
        mHintDialog.setMessage("退出后将不能享受更多服务哦~");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRefreshLayout.setEnabled(AppUtil.isUserLogin(mActivity, false));
    }

    @Override
    @OnClick({R.id.tv_check_update, R.id.tv_select_sex, R.id.tv_about, R.id.tv_feed_back, R.id.iv_avatar, R.id.tv_nick_name,
            R.id.tv_edit_info, R.id.rl_open_vip, R.id.rl_collection, R.id.tv_clear_memory,
            R.id.tv_user_agreement, R.id.tv_logout})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_select_sex:
                startActivityForResult(new Intent(mActivity, SelectSexActivity.class), TO_SELECT_SEX);
                break;
            case R.id.rl_open_vip:
                startActivity(new Intent(mActivity, BuyVIPActivity.class));
                break;
            case R.id.rl_collection:
                if (AppUtil.isUserLogin(mActivity, true)) {
                    startActivity(new Intent(mActivity, CollectionActivity.class));
                }
                break;
            case R.id.tv_clear_memory:
                showMessage("缓存已清除");
                break;
            case R.id.tv_check_update:
                showMessage("当前已经是最新版本");
                break;
            case R.id.tv_user_agreement:
                startActivity(new Intent(mActivity, UserAgreementActivity.class));
                break;
            case R.id.tv_feed_back:
                startActivity(new Intent(mActivity, FeedbackActivity.class));
                break;
            case R.id.tv_about:
                startActivity(new Intent(mActivity, AboutActivity.class));
                break;
            case R.id.tv_logout:
                if (AppUtil.isUserLogin(mActivity, true)) {
                    mHintDialog.show();
                }
                break;
            case R.id.iv_avatar://修改个人昵称、头像、密码
            case R.id.tv_nick_name:
            case R.id.tv_edit_info:
                if (AppUtil.isUserLogin(mActivity, true)) {
                    Intent intent = new Intent(mActivity, EditInfoActivity.class);
                    UserEntity user = AppUtil.getUser();
                    if (user != null) {
                        if (!TextUtils.isEmpty(user.getNick())) {
                            intent.putExtra(USER_NICK_NAME, user.getNick());
                        }
                        if (user.getAvatar() != null && !TextUtils.isEmpty(user.getAvatar().getFileUrl())) {
                            intent.putExtra(USER_AVATAR, user.getAvatar().getFileUrl());
                        }
                    }
                    startActivityForResult(intent, TO_EDIT_INFO);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TO_LOGIN:
            case TO_EDIT_INFO:
                if (resultCode == RESULT_OK) {
                    if (AppUtil.isUserLogin(mActivity, false)) {
                        getUserInfo();
                    } else {
                        mTvNickName.setText("未登录");
                    }
                }
                break;
            case TO_SELECT_SEX:
                if (resultCode == RESULT_OK) {
                    setResult(RESULT_REFRESH);
                }
                break;
            default:
                break;
        }
    }
}
