package me.sheepyang.tuiclient.activity.photo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.adapter.ImageBrowserAdapter;
import me.sheepyang.tuiclient.fragment.ImageDetailFragment;


public class ImageBrowserActivity extends BaseActivity {
    public static final String IS_MORE_LOCK = "is_more_lock";
    public static final String POSITION = "position";
    public static final String IMAGE_LIST = "image_list";
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    @BindView(R.id.tv_current_page)
    TextView mTvCurrentPage;
    private boolean mIsMoreLock;
    private List<String> mImageList;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mTitleList = new ArrayList<>();
    private int mPosition = -1;

    @Override
    public int setLayoutId() {
        return R.layout.activity_image_browser;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    //主要的语句---将当前Activity的View和自己定义的Key绑定起来
        ViewCompat.setTransitionName(mViewPager, "image");
        mIsMoreLock = getIntent().getBooleanExtra(IS_MORE_LOCK, false);
        mImageList = getIntent().getStringArrayListExtra(IMAGE_LIST);
        mPosition = getIntent().getIntExtra(POSITION, -1);
        if (mImageList == null || mImageList.size() <= 0) {
            showMessage("图片列表为空");
            onBackPressed();
        }
        initView();
        initListener();
        if (mPosition != -1) {
            mViewPager.setCurrentItem(mPosition);
        }
    }

    private void initListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTvCurrentPage.setText((position + 1) + " / " + mImageList.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView() {
        mFragmentList.clear();
        for (String path :
                mImageList) {
            mFragmentList.add(ImageDetailFragment.newInstance(path));
        }
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new ImageBrowserAdapter(getSupportFragmentManager(), mFragmentList, mTitleList));
        if (mImageList.size() <= 1) {
            mTvCurrentPage.setVisibility(View.GONE);
        } else {
            mTvCurrentPage.setText("1 / " + mImageList.size());
        }
    }
}
