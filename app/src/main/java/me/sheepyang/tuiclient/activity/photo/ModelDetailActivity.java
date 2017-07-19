package me.sheepyang.tuiclient.activity.photo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.fragment.modellist.NewestFragment;
import me.sheepyang.tuiclient.model.bmobentity.ModelEntity;
import me.sheepyang.tuiclient.utils.AppUtil;
import me.sheepyang.tuiclient.utils.BmobExceptionUtil;
import me.sheepyang.tuiclient.utils.DateUtil;
import me.sheepyang.tuiclient.utils.GlideApp;
import me.sheepyang.tuiclient.widget.dialog.QDialog;

/**
 * 模特的所有套图列表
 */
public class ModelDetailActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_tz_sw)
    TextView mTvTzSw;
    @BindView(R.id.tv_birthday)
    TextView mTvBirthday;
    private QDialog mHintialog;
    private String mId;
    private NewestFragment mNewestFragment;
    private ModelEntity mData;

    @Override
    public int setLayoutId() {
        return R.layout.activity_model_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = getIntent().getStringExtra("id");
        initView();
        initListener();
        initData();
    }

    private void initListener() {
        mHintialog.setOnRightClickListener((DialogInterface dialog, int which) -> {
//            startActivity(new Intent(mContext, BuyVIPActivity.class));
            showMessage("跳转到购买会员");
        });
    }

    private void initData() {
        BmobQuery<ModelEntity> query = new BmobQuery<ModelEntity>();
        query.getObject(mId, new QueryListener<ModelEntity>() {

            @Override
            public void done(ModelEntity object, BmobException e) {
                closeDialog();
                if (e == null) {
                    mData = object;
                    GlideApp.with(mActivity)
                            .load(mData.getAvatar().getFileUrl())
                            .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                            .placeholder(R.drawable.ico_user_avatar)
                            .error(R.drawable.ico_user_avatar)
                            .into(mIvAvatar);
                    mTvName.setText(mData.getNick());
                    mTvTzSw.setText(mData.getWeight() + "KG/" + mData.getBustSize() + "-" + mData.getWaistSize() + "-" + mData.getHipSize());
                    mTvBirthday.setText(DateUtil.getStringByFormat(mData.getBirthday().getDate(), DateUtil.dateFormatYMD));
                } else {
                    BmobExceptionUtil.handler(e);
                }
            }

        });
    }

    private void initView() {
        mHintialog = new QDialog(mActivity);
        mHintialog.setTitle("获取联系方式");
        mHintialog.setMessage("开通会员才能查看我的联系方式哦~");
        mNewestFragment = NewestFragment.newInstance("", mId, false, false);
        //必需继承FragmentActivity,嵌套fragment只需要这行代码
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, mNewestFragment).commitAllowingStateLoss();
    }

    @Override
    @OnClick({R.id.tv_get_contact})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_get_contact:
                if (AppUtil.isUserLogin(mActivity, true)) {
                    mHintialog.show();
                }
                break;
            default:
                break;
        }
    }
}
