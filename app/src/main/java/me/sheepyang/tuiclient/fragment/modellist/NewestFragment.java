package me.sheepyang.tuiclient.fragment.modellist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.adapter.PhotoPackageAdapter;
import me.sheepyang.tuiclient.app.Constants;
import me.sheepyang.tuiclient.fragment.base.BaseLazyFragment;
import me.sheepyang.tuiclient.loader.GlideImageLoader;
import me.sheepyang.tuiclient.model.bmobentity.AdvEntity;
import me.sheepyang.tuiclient.utils.AppUtil;
import me.sheepyang.tuiclient.utils.BmobExceptionUtil;
import me.sheepyang.tuiclient.widget.recyclerview.NoAlphaItemAnimator;

import static me.sheepyang.tuiclient.utils.AppUtil.TO_LOGIN;

/**
 * 最新
 */
public class NewestFragment extends BaseLazyFragment {
    private static final String PARAM_TYPE_ID = "param_type_id";
    private static final String PARAM_MODEL_ID = "param_model_id";
    private static final String PARAM_IS_SHOW_BANNAR = "param_is_show_bannar";
    private static final String PARAM_IS_IV_AVATAR_CLICKABLE = "param_is_iv_avatar_clickable";
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    TwinklingRefreshLayout mRefreshLayout;
    private PhotoPackageAdapter mAdapter;
    private List<String> mData = new ArrayList<>();
    private SinaRefreshView mHeadView;
    private Banner mBannar;
    private List<AdvEntity> mBannerList = new ArrayList<>();
    private boolean mIsShowBannar;
    private boolean mIsIvAvatarClickable = true;
    private int mCurrentPage = 1;
    private int mPageSize = Constants.PAGE_SIZE;
    private View mEmptyView;
    private View mBannerHeadView;
    private String mTypeId;
    private String mModelId;


    public NewestFragment() {

    }

    public static NewestFragment newInstance(String typeId, String modelId, boolean isShowBannar, boolean isIvAvatarClickable) {
        NewestFragment fragment = new NewestFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_TYPE_ID, typeId);
        args.putString(PARAM_MODEL_ID, modelId);
        args.putBoolean(PARAM_IS_SHOW_BANNAR, isShowBannar);
        args.putBoolean(PARAM_IS_IV_AVATAR_CLICKABLE, isIvAvatarClickable);
        fragment.setArguments(args);
        return fragment;
    }

    public static NewestFragment newInstance(String typeId, boolean isShowBannar, boolean isIvAvatarClickable) {
        return newInstance(typeId, "", isShowBannar, isIvAvatarClickable);
    }

    public static NewestFragment newInstance(boolean isShowBannar, boolean isIvAvatarClickable) {
        return newInstance("", isShowBannar, isIvAvatarClickable);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTypeId = getArguments().getString(PARAM_TYPE_ID, "");
            mModelId = getArguments().getString(PARAM_MODEL_ID, "");
            mIsShowBannar = getArguments().getBoolean(PARAM_IS_SHOW_BANNAR, false);
            mIsIvAvatarClickable = getArguments().getBoolean(PARAM_IS_IV_AVATAR_CLICKABLE, true);
        }
    }

    @Override
    protected int setLayoutId() {
        return R.layout.layout_refresh_rv;
    }

    @Override
    protected void init() {

    }

    private void getModelList(final boolean isPullRefresh) {
        if (isPullRefresh) {//下拉刷新
            mCurrentPage = 1;
        } else {//加载更多
            mCurrentPage++;
        }
//        OkGo.post(Api.GET_MODEL_LIST_NEWEST)
//                .tag(this)
//                .params("page", mCurrentPage)
//                .params("rows", mPageSize)
//                .params("e.userlike", new SPUtils(QContacts.SP_NAME).getInt(SPContants.SELECT_SEX, 0))
//                .params("e.id", mTypeId)
//                .params("e.modelid", mModelId)
//                .execute(new JsonCallback<ModelListResponse>() {
//
//                    @Override
//                    public void onSuccess(ModelListResponse imageTypeResponse, Call call, Response response) {
//                        if (imageTypeResponse != null && imageTypeResponse.isTrue(mContext)) {
//                            if (imageTypeResponse.getData().getRows() != null && imageTypeResponse.getData().getRows().size() > 0) {
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
//                                    mCurrentPage--;
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
//                        if (isPullRefresh) {//下拉刷新
//                            mCurrentPage = 1;
//                        } else {//加载更多
//                            mCurrentPage--;
//                        }
//                        ExceptionUtil.handleException(mContext, response, e);
//                    }
//                });
    }

    private void getBannerList() {

        BmobQuery<AdvEntity> query = new BmobQuery<AdvEntity>();
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(100);
        query.addWhereEqualTo("isShow", Boolean.TRUE);

        int habit = new SPUtils(Constants.SP_NAME).getInt(Constants.SP_SELECT_SEX, 0);
        if (habit == 1 || habit == 2) {
            query.addWhereEqualTo("habit", habit);
        }

        query.order("-updatedAt");
        ((BaseActivity) mContext).showDialog("正在加载广告...");
        //执行查询方法
        query.findObjects(new FindListener<AdvEntity>() {

            @Override
            public void done(List<AdvEntity> object, BmobException e) {
                ((BaseActivity) mContext).closeDialog();
                if (e == null) {
                    if (object != null && object.size() > 0) {
                        if (mIsShowBannar) {
                            mBannerList = object;
                            List<String> bannerList = new ArrayList();
                            for (AdvEntity advData :
                                    mBannerList) {
                                if (advData.getPic() != null && !TextUtils.isEmpty(advData.getPic().getFileUrl())) {
                                    bannerList.add(advData.getPic().getFileUrl());
                                }
                            }
                            mBannar.update(bannerList);
                        }
                    } else {
                        mBannar.update(new ArrayList<>());
                    }
                } else {
                    BmobExceptionUtil.handler(e);
                }
                getModelList(true);
            }
        });
    }

    @Override
    protected void initPrepare() {
        initView();
        initListener();
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        if (mIsShowBannar) {
            //开始轮播
            mBannar.startAutoPlay();
        }
    }

    @Override
    protected void onInvisible() {
        if (mIsShowBannar) {
            //结束轮播
            mBannar.stopAutoPlay();
        }
    }

    @Override
    protected void initData() {
        if (mIsShowBannar) {
            getBannerList();
        } else {
            getModelList(true);
        }
    }

    private void initView() {
        mHeadView = new SinaRefreshView(mContext);
        mHeadView.setArrowResource(R.drawable.ico_pink_arrow);
        mRefreshLayout.setHeaderView(mHeadView);
        mRefreshLayout.setBottomView(new LoadingView(mContext));

        mAdapter = new PhotoPackageAdapter(mData);
        mAdapter.isFirstOnly(true);
//        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new NoAlphaItemAnimator());

        mEmptyView = LayoutInflater.from(mContext).inflate(R.layout.layout_empty, null, false);

        if (mIsShowBannar) {
            mBannerHeadView = LayoutInflater.from(mContext).inflate(R.layout.header_banner, (ViewGroup) mRecyclerView.getParent(), false);
            mBannar = (Banner) mBannerHeadView.findViewById(R.id.banner);
            mBannar.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtils.getScreenWidth() / 3));
            int height = ScreenUtils.getScreenHeight() - mBannerHeadView.getLayoutParams().height - BarUtils.getActionBarHeight((Activity) mContext);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            mEmptyView.setLayoutParams(lp);
            mAdapter.addHeaderView(mBannar);
            //简单使用
            mBannar.setImages(mBannerList)
                    .setDelayTime(3000)
//                    .setBannerStyle(BannerConfig.NUM_INDICATOR)
                    .setImageLoader(new GlideImageLoader())
                    .setOnBannerListener((int position) -> {

                    })
                    .start();
        }
        mAdapter.setHeaderAndEmpty(true);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListener() {
        mAdapter.setOnItemChildClickListener((BaseQuickAdapter adapter, View view, int position) -> {
            switch (view.getId()) {
                case R.id.iv_avatar:
                    if (mIsIvAvatarClickable) {
//                        Intent intent = new Intent(mContext, ModelDetailActivity.class);
//                        intent.putExtra("id", mData.get(position).getModelid());
//                        startActivity(intent);
                        showMessage("模特详情");
                    }
                    break;
                case R.id.ll_collection:
                    if (AppUtil.isUserLogin(mContext, true)) {
                        view.setEnabled(false);
//                        if ("1".equals(mData.get(position).getSfsc())) {//已收藏
//                            cancelCollectImages(view, position);
//                        } else {//未收藏
//                            collectImages(view, position);
//                        }
                    }
                    break;
                default:
                    break;
            }
        });
        mAdapter.setOnItemClickListener((BaseQuickAdapter adapter, View view, int position) -> {
//            Intent intent = new Intent(mContext, ModelPhotoActivity.class);
//            intent.putExtra("id", mData.get(position).getId());
//            startActivity(intent);
            showMessage("套图详情");
        });
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                if (mIsShowBannar) {
                    getBannerList();
                } else {
                    getModelList(true);
                }
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                getModelList(false);
            }
        });
    }

    /**
     * 收藏套图
     */
    private void collectImages(View view, int position) {
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
    }

    /**
     * 取消收藏套
     */
    private void cancelCollectImages(View view, int position) {
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TO_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
                    if (mIsShowBannar) {
                        getBannerList();
                    } else {
                        getModelList(true);
                    }
                }
                break;
            default:
                break;
        }
    }
}
