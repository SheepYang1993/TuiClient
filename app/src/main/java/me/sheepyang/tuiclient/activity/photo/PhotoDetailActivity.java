package me.sheepyang.tuiclient.activity.photo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

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
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.adapter.PhotoDetailAdapter;
import me.sheepyang.tuiclient.model.bmobentity.PhotoBagEntity;
import me.sheepyang.tuiclient.model.bmobentity.PhotoDetailEntity;
import me.sheepyang.tuiclient.utils.AppUtil;
import me.sheepyang.tuiclient.utils.BmobExceptionUtil;
import me.sheepyang.tuiclient.widget.QBar;
import me.sheepyang.tuiclient.widget.dialog.QDialog;
import me.sheepyang.tuiclient.widget.recyclerview.NoAlphaItemAnimator;

/**
 * 模特单套图详情
 */
public class PhotoDetailActivity extends BaseActivity {
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    TwinklingRefreshLayout mRefreshLayout;
    @BindView(R.id.q_bar)
    QBar mQBar;
    private PhotoDetailAdapter mAdapter;
    private List<PhotoDetailEntity> mData = new ArrayList<>();
    private SinaRefreshView mHeadView;
    private QDialog mHintDialog;
    private int mCurrentPage;
    private View mEmptyView;
    private int mPageSize = 8;
    private String mId;

    @Override
    public int setLayoutId() {
        return R.layout.activity_model_photo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = getIntent().getStringExtra("id");
        if (TextUtils.isEmpty(mId)) {
            showMessage("找不到套图信息");
            finish();
            return;
        }
        initView();
        initListener();
        getModelList(0, mRefreshLayout);
    }

    private void initView() {
//        if (AppUtil.getUser() != null) {
//
//        } else {
//            mQBar.setRightText();
//        }

        mHeadView = new SinaRefreshView(mActivity);
        mHeadView.setArrowResource(R.drawable.ico_pink_arrow);
        mRefreshLayout.setHeaderView(mHeadView);
        mRefreshLayout.setBottomView(new LoadingView(mActivity));

        mAdapter = new PhotoDetailAdapter(mData);
        mAdapter.isFirstOnly(true);
//        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mActivity, 2));
        mRecyclerView.setItemAnimator(new NoAlphaItemAnimator());
        mEmptyView = LayoutInflater.from(mActivity).inflate(R.layout.layout_empty, null, false);
        mRecyclerView.setAdapter(mAdapter);

        mHintDialog = new QDialog(mActivity);
        mHintDialog.setTitle("开通会员");
        mHintDialog.setMessage("现在开通会员，即可解锁所有照片，并可获取模特联系方式哦~");
    }

    private void initListener() {
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (AppUtil.isUserLogin(mActivity, true)) {//已登录
                    showMessage("浏览图片");
//                    if (position >= mAdapter.getSeeCount()) {//图片已上锁
//                        mHintDialog.show();
//                    } else {
//                        boolean isMoreLock = false;
//                        ArrayList<String> modelPathList = new ArrayList<>();
//                        for (int i = 0; i < mData.size(); i++) {
//                            if (i >= mAdapter.getSeeCount()) {
//                                isMoreLock = true;
//                            } else {
//                                modelPathList.add(Api.LOAD_IMAGE + mData.get(i).getImgname());
//                            }
//                        }
//                        Intent intent = new Intent(mContext, ImageBrowserActivity.class);
//                        intent.putExtra(POSITION, position);
//                        intent.putExtra(IS_MORE_LOCK, isMoreLock);
//                        intent.putExtra(IMAGE_LIST, modelPathList);
//                        startActivity(intent);
//                    }
                }
            }
        });
        mHintDialog.setOnRightClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showMessage("购买VIP");
//                startActivity(new Intent(mActivity, BuyVIPActivity.class));
            }
        });
        mQBar.setOnRightClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtil.isUserLogin(mActivity, true)) {
                    mHintDialog.show();
                }
            }
        });
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                getModelList(0, mRefreshLayout);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                getModelList(1, mRefreshLayout);
            }
        });
    }

    private void getModelList(int type, TwinklingRefreshLayout refreshLayout) {
        showDialog("玩命加载中...");
        BmobQuery<PhotoDetailEntity> query = new BmobQuery<PhotoDetailEntity>();
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(mPageSize);
        switch (type) {
            case 0://下拉刷新
                mCurrentPage = 0;
                break;
        }
        PhotoBagEntity photoBag = new PhotoBagEntity();
        photoBag.setObjectId(mId);
        query.addWhereEqualTo("photoBag", photoBag);
        query.setSkip(mCurrentPage * mPageSize);
//        query.order("-updatedAt");
        query.order("-createdAt");
        //执行查询方法
        query.findObjects(new FindListener<PhotoDetailEntity>() {

            @Override
            public void done(List<PhotoDetailEntity> object, BmobException e) {
                if (e == null) {
                    if (object != null && object.size() > 0) {
                        switch (type) {
                            case 0://下拉刷新
                                mData = object;
                                break;
                            case 1://上拉加载更多
                                mData.addAll(object);
                                break;
                        }
                        mAdapter.setNewData(mData);
                        mCurrentPage++;
                    } else {
                        switch (type) {
                            case 0://下拉刷新
                                mData.clear();
                                mAdapter.setNewData(mData);
                                showMessage(getString(R.string.no_data));
                                break;
                            case 1://上拉加载更多
                                showMessage(getString(R.string.no_more_data));
                                break;
                        }
                    }
                } else {
                    closeDialog();
                    BmobExceptionUtil.handler(e);
                }

                closeDialog();
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
}
