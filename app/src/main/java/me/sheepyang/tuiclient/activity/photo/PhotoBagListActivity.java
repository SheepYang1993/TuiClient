package me.sheepyang.tuiclient.activity.photo;

import android.os.Bundle;
import android.text.TextUtils;

import butterknife.BindView;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.fragment.modellist.NewestFragment;
import me.sheepyang.tuiclient.widget.QBar;


/**
 * 模特所有套图列表
 */
public class PhotoBagListActivity extends BaseActivity {

    @BindView(R.id.q_bar)
    QBar mQBar;
    private String mId;
    private NewestFragment mNewestFragment;
    private String mTitle;

    @Override
    public int setLayoutId() {
        return R.layout.activity_photo_bag_list;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = getIntent().getStringExtra("id");
        mTitle = getIntent().getStringExtra("title");
        if (!TextUtils.isEmpty(mTitle)) {
            mQBar.setTitle(mTitle);
        }
        if (TextUtils.isEmpty(mId)) {
            showMessage("找不到模特信息");
            finish();
            return;
        }
        initView();
    }

    private void initView() {
        mNewestFragment = NewestFragment.newInstance(mId, false, true);
        //必需继承FragmentActivity,嵌套fragment只需要这行代码
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, mNewestFragment).commitAllowingStateLoss();
    }
}
