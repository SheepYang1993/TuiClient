package me.sheepyang.tuiclient.activity.mine;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.datatype.BmobFile;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.adapter.VIPAdapter;
import me.sheepyang.tuiclient.model.bmobentity.VIPDetailEntity;
import me.sheepyang.tuiclient.model.bmobentity.VIPEntity;
import me.sheepyang.tuiclient.utils.GlideApp;
import me.sheepyang.tuiclient.widget.recyclerview.NoAlphaItemAnimator;

/**
 * 购买VIP会员
 */
public class BuyVIPActivity extends BaseActivity {

    @BindView(R.id.iv_photo)
    ImageView mIvPhoto;
    @BindView(R.id.tv_opening_process)
    TextView mTvOpeningProcess;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout mRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.ll_main)
    LinearLayout mLlMain;
    private VIPEntity mVIPEntity;
    private View mEmptyView;
    private List<VIPDetailEntity> mVipDetailData = new ArrayList<>();
    private VIPAdapter mAdapter;

    @Override
    public int setLayoutId() {
        return R.layout.activity_buy_vip;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initListener();
        initTestData();
        getVIPInfo();
    }

    private void initTestData() {
        mVIPEntity = new VIPEntity();
        BmobFile pic = new BmobFile();
        pic.setUrl("http://img0.imgtn.bdimg.com/it/u=3158845311,1085054829&fm=26&gp=0.jpg");
        mVIPEntity.setPic(pic);
        mVIPEntity.setShow(true);
        StringBuffer openDesc = new StringBuffer();
        openDesc.append("1.开通流程第一步");
        openDesc.append("\n2.开通流程第二步");
        openDesc.append("\n3.开通流程第三步");
        openDesc.append("\n4.开通流程第四步");
        openDesc.append("\n5.开通流程第五步");
        openDesc.append("\n6.开通流程第六步");
        openDesc.append("\n\n请联系13069786878");
        mVIPEntity.setOpenDesc(openDesc.toString());

        List<VIPDetailEntity> vipList = new ArrayList<>();
        VIPDetailEntity vipDetailEntity;
        for (int i = 0; i < 9; i++) {
            vipDetailEntity = new VIPDetailEntity();
            vipDetailEntity.setName((i + 1) + "个月会员");
            vipDetailEntity.setPrice((double) ((i + 1) * 30));
            vipDetailEntity.setRebate(1 - (i + 1) / 10d);
            vipDetailEntity.setShow(true);
            if (i % 3 == 0) {
                vipDetailEntity.setRecommend(true);
            }
            vipList.add(vipDetailEntity);
        }
        mVIPEntity.setVipList(vipList);
    }

    private void initListener() {
        mRefreshLayout.setOnRefreshListener(() -> {
            getVIPInfo();
        });
    }

    private void initData() {
        if (mVIPEntity != null) {
            mLlMain.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(mVIPEntity.getPic().getFileUrl())) {
                GlideApp.with(mActivity)
                        .load(mVIPEntity.getPic().getFileUrl())
                        .centerCrop()
                        .into(mIvPhoto);
            }
            if (!TextUtils.isEmpty(mVIPEntity.getOpenDesc())) {
                mTvOpeningProcess.setText(mVIPEntity.getOpenDesc());
            }
            if (mVIPEntity.getVipList() != null && mVIPEntity.getVipList().size() > 0) {
                mVipDetailData = mVIPEntity.getVipList();
                mAdapter.setNewData(mVipDetailData);
            }
        } else {
            mLlMain.setVisibility(View.GONE);
        }
    }

    private void getVIPInfo() {
//        OkGo.post(Api.VIP_INFO)
//                .tag(this)
//                .params("", "")
//                .execute(new JsonCallback<VIPResponse>() {
//                    @Override
//                    public void onSuccess(VIPResponse vipResponse, Call call, Response response) {
//                        if (vipResponse != null && vipResponse.isTrue(mContext)) {
//                            mVIPEntity = vipResponse.getData();
        initData();
//                        }
//                    }
//
//                    @Override
//                    public void onAfter(VIPResponse vipResponse, Exception e) {
//                        super.onAfter(vipResponse, e);
        mRefreshLayout.setRefreshing(false);
//                    }
//                });
    }

    private void initView() {
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getScreenWidth() / 3);
        mIvPhoto.setLayoutParams(params);

        mAdapter = new VIPAdapter(mVipDetailData);
        mAdapter.isFirstOnly(true);
//        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setItemAnimator(new NoAlphaItemAnimator());

        mEmptyView = LayoutInflater.from(mActivity).inflate(R.layout.layout_empty, mRecyclerView, false);
        mRecyclerView.setAdapter(mAdapter);
    }
}
