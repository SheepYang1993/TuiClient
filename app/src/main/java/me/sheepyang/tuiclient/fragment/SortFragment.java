package me.sheepyang.tuiclient.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.HomePageActivity;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.activity.photo.PhotoBagListActivity;
import me.sheepyang.tuiclient.adapter.SortAdapter;
import me.sheepyang.tuiclient.app.Constants;
import me.sheepyang.tuiclient.fragment.base.BaseLazyFragment;
import me.sheepyang.tuiclient.model.bmobentity.SortEntity;
import me.sheepyang.tuiclient.utils.AppUtil;
import me.sheepyang.tuiclient.utils.BmobExceptionUtil;
import me.sheepyang.tuiclient.widget.dialog.QDialog;
import me.sheepyang.tuiclient.widget.recyclerview.NoAlphaItemAnimator;

import static me.sheepyang.tuiclient.utils.AppUtil.TO_LOGIN;

/**
 * 分类
 */
public class SortFragment extends BaseLazyFragment {
    private static final String ARG_PARAM1 = "param1";
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    TwinklingRefreshLayout mRefreshLayout;
    //    private String mParam1;
    private SortAdapter mAdapter;
    private List<SortEntity> mDatas = new ArrayList<>();
    private SinaRefreshView mHeadView;
    private QDialog mHintDialog;
    private int mCurrentPage = 1;
    private int mPageSize = 10;
    private View mEmptyView;

    public SortFragment() {

    }

    public static SortFragment newInstance(String param1) {
        SortFragment fragment = new SortFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    protected int setLayoutId() {
        return R.layout.layout_refresh_rv;
    }

    @Override
    protected void init() {

    }

    private void initListener() {
        mHintDialog.setOnRightClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showMessage("购买vip");
//                startActivity(new Intent(mContext, BuyVIPActivity.class));
            }
        });
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                SortEntity item = (SortEntity) adapter.getData().get(position);
                if (item.getVip()) {
                    if (AppUtil.isUserLogin(mContext, true)) {
                        if (AppUtil.isUserVip()) {
                            Intent intent = new Intent(mContext, PhotoBagListActivity.class);
                            intent.putExtra("id", mDatas.get(position).getObjectId());
                            intent.putExtra("title", mDatas.get(position).getName());
                            startActivity(intent);
                        } else {
                            mHintDialog.show();
                        }
                    }
                } else {
                    Intent intent = new Intent(mContext, PhotoBagListActivity.class);
                    intent.putExtra("id", mDatas.get(position).getObjectId());
                    intent.putExtra("title", mDatas.get(position).getName());
                    startActivity(intent);
                }
            }
        });
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                getTypeData(0, refreshLayout);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                getTypeData(1, refreshLayout);
            }
        });
    }

    @Override
    protected boolean lazyLoad() {
        if (!super.lazyLoad()) {
            if (!isFirst && mContext != null && mContext instanceof HomePageActivity && ((HomePageActivity) mContext).mSortNeedRefresh) {
                ((HomePageActivity) mContext).mSortNeedRefresh = false;
                initData();
            }
        }
        return true;
    }

    private void getTypeData(int type, TwinklingRefreshLayout refreshLayout) {
        BmobQuery<SortEntity> query = new BmobQuery<SortEntity>();
        query.addWhereEqualTo("isShow", Boolean.TRUE);
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(mPageSize);
        switch (type) {
            case 0://下拉刷新
                mCurrentPage = 0;
                break;
        }
        int habit = new SPUtils(Constants.SP_NAME).getInt(Constants.SP_SELECT_SEX, -1);
        if (habit != -1 && habit != 0) {
            query.addWhereEqualTo("habit", habit);
        }
        query.setSkip(mCurrentPage * mPageSize);
//        query.order("-updatedAt");
        query.order("-createdAt");
        ((BaseActivity) mContext).showDialog("玩命加载中...");
        //执行查询方法
        query.findObjects(new FindListener<SortEntity>() {

            @Override
            public void done(List<SortEntity> object, BmobException e) {
                if (e == null) {
                    if (object != null && object.size() > 0) {
                        switch (type) {
                            case 0://下拉刷新
                                mDatas = object;
                                break;
                            case 1://上拉加载更多
                                mDatas.addAll(object);
                                break;
                        }
                        mAdapter.setNewData(mDatas);
                        mCurrentPage++;
                    } else {
                        switch (type) {
                            case 0://下拉刷新
                                mDatas.clear();
                                mAdapter.setNewData(mDatas);
                                showMessage(getString(R.string.no_data));
                                break;
                            case 1://上拉加载更多
                                showMessage(getString(R.string.no_more_data));
                                break;
                        }
                    }
                } else {
                    ((BaseActivity) mContext).closeDialog();
                    BmobExceptionUtil.handler(e);
                }

                ((BaseActivity) mContext).closeDialog();
                switch (type) {
                    case 0:
                        refreshLayout.finishRefreshing();
                        break;
                    case 1:
                        refreshLayout.finishLoadmore();
                        break;
                }
            }
        });
    }

    @Override
    protected void initPrepare() {
        initView();
        initListener();
    }

    @Override
    protected void onInvisible() {

    }

    @Override
    protected void initData() {
        getTypeData(0, mRefreshLayout);
    }

    private void initView() {
        mHeadView = new SinaRefreshView(mContext);
        mHeadView.setArrowResource(R.drawable.ico_pink_arrow);
        mRefreshLayout.setHeaderView(mHeadView);
        mRefreshLayout.setBottomView(new LoadingView(mContext));

        mAdapter = new SortAdapter(mDatas);
        mAdapter.isFirstOnly(true);
//        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        mRecyclerView.setItemAnimator(new NoAlphaItemAnimator());
        mRecyclerView.setAdapter(mAdapter);

        mEmptyView = LayoutInflater.from(mContext).inflate(R.layout.layout_empty, mRecyclerView, false);

        mHintDialog = new QDialog(mContext);
        mHintDialog.setTitle("开通会员");
        mHintDialog.setMessage("现在开通会员，即可解锁所有照片，并可获取模特联系方式哦~");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TO_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
                    mRefreshLayout.startRefresh();
                }
                break;
            default:
                break;
        }
    }
}
