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
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.adapter.PhotoDetailAdapter;
import me.sheepyang.tuiclient.model.bmobentity.PhotoDetailEntity;
import me.sheepyang.tuiclient.utils.AppUtil;
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
        getModelList(true);
    }

    private void initView() {
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
                getModelList(true);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                getModelList(false);
            }
        });
    }

    private void getModelList(final boolean isPullRefresh) {
        if (isPullRefresh) {//下拉刷新
            mCurrentPage = 1;
        } else {//加载更多
            mCurrentPage++;
        }
//        OkGo.post(Api.MODEL_IMG_DETAL)
//                .tag(this)
//                .params("page", mCurrentPage)
//                .params("rows", mPageSize)
//                .params("e.id", mId)
//                .execute(new JsonCallback<ModelPhotoListResponse>() {
//
//                    @Override
//                    public void onSuccess(ModelPhotoListResponse imageTypeResponse, Call call, Response response) {
//                        if (imageTypeResponse != null && imageTypeResponse.isTrue(mContext)) {
//                            if (imageTypeResponse.getData().getRows() != null && imageTypeResponse.getData().getRows().size() > 0) {
//                                mAdapter.setSeeCount(imageTypeResponse.getData().getSeecount());
//                                if (isPullRefresh) {//下拉刷新
//                                    mData = imageTypeResponse.getData().getRows();
//                                    mAdapter.setNewData(mData);
//                                } else {//加载更多
//                                    mData.addAll(imageTypeResponse.getData().getRows());
//                                    mAdapter.setNewData(mData);
//                                }
//                            } else {
//                                if (isPullRefresh) {//下拉刷新
//                                    mData.clear();
//                                    mAdapter.setNewData(mData);
//                                    mAdapter.setEmptyView(mEmptyView);
//                                    showToast(getString(R.string.no_data));
//                                } else {//加载更多
//                                    mCurrentPage--;
//                                    showToast(getString(R.string.no_more_data));
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onAfter(ModelPhotoListResponse imageTypeResponse, Exception e) {
//                        super.onAfter(imageTypeResponse, e);
//                        if (isPullRefresh) {//下拉刷新
//                            mRefreshLayout.finishRefreshing();
//                        } else {//加载更多
//                            mRefreshLayout.finishLoadmore();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Call call, Response response, Exception e) {
//                        super.onError(call, response, e);
//                        ExceptionUtil.handleException(mContext, response, e);
//                        if (isPullRefresh) {//下拉刷新
//                            mCurrentPage = 1;
//                        } else {//加载更多
//                            mCurrentPage--;
//                        }
//                    }
//                });
    }
}
