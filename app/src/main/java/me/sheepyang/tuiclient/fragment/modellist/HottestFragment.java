//package me.sheepyang.tuiclient.fragment.modellist;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//
//import com.blankj.utilcode.util.SPUtils;
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
//import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
//import com.lcodecore.tkrefreshlayout.footer.LoadingView;
//import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
//import com.lzy.okgo.OkGo;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import me.sheepyang.qlady.QContacts;
//import me.sheepyang.qlady.R;
//import me.sheepyang.qlady.activity.detail.ModelDetailActivity;
//import me.sheepyang.qlady.activity.detail.ModelPhotoActivity;
//import me.sheepyang.qlady.adapter.ModelDetailAdapter;
//import me.sheepyang.qlady.fragment.base.BaseLazyFragment;
//import me.sheepyang.qlady.http.Api;
//import me.sheepyang.qlady.http.callback.JsonCallback;
//import me.sheepyang.qlady.http.response.BaseResponse;
//import me.sheepyang.qlady.http.response.ModelListResponse;
//import me.sheepyang.qlady.utils.AppUtil;
//import me.sheepyang.qlady.utils.ExceptionUtil;
//import me.sheepyang.qlady.utils.SPContants;
//import me.sheepyang.qlady.widget.recyclerview.NoAlphaItemAnimator;
//import okhttp3.Call;
//import okhttp3.Response;
//
//import static u.aly.au.R;
//
///**
// * 最热
// */
//public class HottestFragment extends BaseLazyFragment {
//    private static final String PARAM_IS_IV_AVATAR_CLICKABLE = "param_is_iv_avatar_clickable";
//    @BindView(R.id.recycler_view)
//    RecyclerView mRecyclerView;
//    @BindView(R.id.refresh_layout)
//    TwinklingRefreshLayout mRefreshLayout;
//    private ModelDetailAdapter mAdapter;
//    private List<ModelListResponse.DataBean.RowsBean> mData = new ArrayList<>();
//    private SinaRefreshView mHeadView;
//    private boolean mIsIvAvatarClickable = true;
//    private int mCurrentPage = 1;
//    private int mPageSize = 5;
//    private View mEmptyView;
//
//
//    public HottestFragment() {
//
//    }
//
//    public static HottestFragment newInstance(boolean isIvAvatarClickable) {
//        HottestFragment fragment = new HottestFragment();
//        Bundle args = new Bundle();
//        args.putBoolean(PARAM_IS_IV_AVATAR_CLICKABLE, isIvAvatarClickable);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mIsIvAvatarClickable = getArguments().getBoolean(PARAM_IS_IV_AVATAR_CLICKABLE, true);
//        }
//    }
//
//    @Override
//    protected int setLayoutId() {
//        return R.layout.layout_refresh_rv;
//    }
//
//    @Override
//    protected void init() {
//
//    }
//
//    private void getModelList(final boolean isPullRefresh) {
//        if (isPullRefresh) {//下拉刷新
//            mCurrentPage = 1;
//        } else {//加载更多
//            mCurrentPage++;
//        }
//        OkGo.post(Api.GET_MODEL_LIST_HOTTEST)
//                .tag(this)
//                .params("page", mCurrentPage)
//                .params("rows", mPageSize)
//                .params("e.userlike", new SPUtils(QContacts.SP_NAME).getInt(SPContants.SELECT_SEX, 0))
//                .execute(new JsonCallback<ModelListResponse>() {
//
//                    @Override
//                    public void onSuccess(ModelListResponse imageTypeResponse, Call call, Response response) {
//                        if (imageTypeResponse != null && imageTypeResponse.isTrue(mContext)) {
//                            if (imageTypeResponse.getData().getTotal() > 0 && imageTypeResponse.getData().getRows() != null && imageTypeResponse.getData().getRows().size() > 0) {
//                                if (isPullRefresh) {//下拉刷新
//                                    mData = imageTypeResponse.getData().getRows();
//                                    mAdapter.updata(mData);
//                                } else {//加载更多
//                                    mData.addAll(imageTypeResponse.getData().getRows());
//                                    mAdapter.updata(mData);
//                                }
//                            } else {
//                                if (isPullRefresh) {//下拉刷新
//                                    mData.clear();
//                                    mAdapter.updata(mData);
//                                    mAdapter.setEmptyView(mEmptyView);
//                                    showMessage(getString(R.string.no_data));
//                                } else {//加载更多
//                                    showMessage(getString(R.string.no_more_data));
//                                }
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onAfter(ModelListResponse imageTypeResponse, Exception e) {
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
//    }
//
//    @Override
//    protected void initPrepare() {
//        initView();
//        initListener();
//    }
//
//    @Override
//    protected void onInvisible() {
//
//    }
//
//    @Override
//    protected void initData() {
//        getModelList(true);
//    }
//
//    private void initView() {
//        mHeadView = new SinaRefreshView(mContext);
//        mHeadView.setArrowResource(R.drawable.ico_pink_arrow);
//        mRefreshLayout.setHeaderView(mHeadView);
//        mRefreshLayout.setBottomView(new LoadingView(mContext));
//
//        mAdapter = new ModelDetailAdapter(mData);
//        mAdapter.isFirstOnly(true);
////        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
//
//        mRecyclerView.setHasFixedSize(true);
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
//        mRecyclerView.setItemAnimator(new NoAlphaItemAnimator());
//
//        mEmptyView = LayoutInflater.from(mContext).inflate(R.layout.layout_empty, mRecyclerView, false);
//        mRecyclerView.setAdapter(mAdapter);
//    }
//
//    private void initListener() {
//        mAdapter.setOnItemChildClickListener((BaseQuickAdapter adapter, View view, int position) -> {
//            switch (view.getId()) {
//                case R.id.iv_avatar:
//                    if (mIsIvAvatarClickable) {
//                        Intent intent = new Intent(mContext, ModelDetailActivity.class);
//                        intent.putExtra("id", mData.get(position).getModelid());
//                        startActivity(intent);
//                    }
//                    break;
//                case R.id.ll_collection:
//                    if (AppUtil.isLogin()) {
//                        view.setEnabled(false);
//                        if ("1".equals(mData.get(position).getSfsc())) {//已收藏
//                            cancelCollectImages(view, position);
//                        } else {//未收藏
//                            collectImages(view, position);
//                        }
//                    } else {
//                        AppUtil.toLogin(mContext);
//                    }
//                    break;
//                default:
//                    break;
//            }
//        });
//        mAdapter.setOnItemClickListener((BaseQuickAdapter adapter, View view, int position) -> {
//            Intent intent = new Intent(mContext, ModelPhotoActivity.class);
//            intent.putExtra("id", mData.get(position).getId());
//            startActivity(intent);
//        });
//        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
//            @Override
//            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
//                super.onRefresh(refreshLayout);
//                getModelList(true);
//            }
//
//            @Override
//            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
//                super.onLoadMore(refreshLayout);
//                getModelList(false);
//            }
//        });
//    }
//
//    /**
//     * 收藏套图
//     */
//    private void collectImages(View view, int position) {
//        OkGo.post(Api.COLLECT_IMAGES)
//                .tag(this)
//                .params("e.imggroupid", mData.get(position).getId())
//                .execute(new JsonCallback<BaseResponse>() {
//                    @Override
//                    public void onSuccess(BaseResponse baseResponse, Call call, Response response) {
//                        if (baseResponse != null && baseResponse.isTrue(mContext)) {
//                            mData.get(position).setImggroupsee(mData.get(position).getImggroupsee() + 1);
//                            mData.get(position).setSfsc("1");
//                            mAdapter.setNewData(mData);
//                        }
//                    }
//
//                    @Override
//                    public void onAfter(BaseResponse baseResponse, Exception e) {
//                        super.onAfter(baseResponse, e);
//                        view.setEnabled(true);
//                    }
//                });
//    }
//
//    /**
//     * 取消收藏套
//     */
//    private void cancelCollectImages(View view, int position) {
//        OkGo.post(Api.CANCEL_COLLECT_IMAGES)
//                .tag(this)
//                .params("e.imggroupid", mData.get(position).getId())
//                .execute(new JsonCallback<BaseResponse>() {
//                    @Override
//                    public void onSuccess(BaseResponse baseResponse, Call call, Response response) {
//                        if (baseResponse != null && baseResponse.isTrue(mContext)) {
//                            mData.get(position).setImggroupsee(mData.get(position).getImggroupsee() - 1);
//                            mData.get(position).setSfsc("");
//                            mAdapter.setNewData(mData);
//                        }
//                    }
//
//                    @Override
//                    public void onAfter(BaseResponse baseResponse, Exception e) {
//                        super.onAfter(baseResponse, e);
//                        view.setEnabled(true);
//                    }
//                });
//    }
//}
