package me.sheepyang.tuiclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.activity.mine.MineActivity;
import me.sheepyang.tuiclient.adapter.HomePageAdapter;
import me.sheepyang.tuiclient.fragment.SortFragment;
import me.sheepyang.tuiclient.fragment.modellist.NewestFragment;
import me.sheepyang.tuiclient.model.entity.TabEntity;
import me.sheepyang.tuiclient.utils.AppUtil;

public class HomePageActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tab_layout)
    CommonTabLayout mTabLayout;
    @BindView(R.id.view_pager)
    ViewPager mViewPager;
    private List<String> mTitleList = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private long mCurrentTime;

    @Override
    public int setLayoutId() {
        return R.layout.activity_home_page;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        initListener();
    }

    private void initListener() {
        mTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void init() {
        mTitleList.add("最新");
        mTitleList.add("分类");
        mTitleList.add("最热");
        mFragmentList.add(NewestFragment.newInstance(true, true));
        mFragmentList.add(SortFragment.newInstance("分类"));
        mFragmentList.add(SortFragment.newInstance("分类"));
//        mFragmentList.add(HottestFragment.newInstance(true));
        mTabEntities.add(new TabEntity("最新", 0, 0));
        mTabEntities.add(new TabEntity("分类", 0, 0));
        mTabEntities.add(new TabEntity("最热", 0, 0));
        mViewPager.setOffscreenPageLimit(3);//设置缓存view 的个数
        mViewPager.setAdapter(new HomePageAdapter(getSupportFragmentManager(), mFragmentList, mTitleList));
        mTabLayout.setTabData(mTabEntities);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mCurrentTime < 2000) {
            mCurrentTime = 0;
            AppUtil.exit(mActivity);
        } else {
            mCurrentTime = System.currentTimeMillis();
            showMessage("再次点击退出APP");
        }
    }

    @Override
    @OnClick({R.id.iv_search, R.id.iv_mine})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_mine:
                startActivity(new Intent(mActivity, MineActivity.class));
                break;
            case R.id.iv_search:
                showMessage("搜索");
//                startActivity(new Intent(mActivity, SearchActivity.class));
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFragmentList.get(mViewPager.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
    }
}
