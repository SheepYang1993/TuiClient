package me.sheepyang.tuiclient.fragment.modellist;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.footer.LoadingView;
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.HomePageActivity;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.activity.photo.ModelDetailActivity;
import me.sheepyang.tuiclient.activity.photo.PhotoDetailActivity;
import me.sheepyang.tuiclient.adapter.PhotoBagAdapter;
import me.sheepyang.tuiclient.app.Constants;
import me.sheepyang.tuiclient.fragment.base.BaseLazyFragment;
import me.sheepyang.tuiclient.model.bmobentity.PhotoBagEntity;
import me.sheepyang.tuiclient.model.bmobentity.UserEntity;
import me.sheepyang.tuiclient.utils.AppUtil;
import me.sheepyang.tuiclient.utils.BmobExceptionUtil;
import me.sheepyang.tuiclient.widget.recyclerview.NoAlphaItemAnimator;

import static me.sheepyang.tuiclient.utils.AppUtil.TO_LOGIN;


/**
 * 最热
 */
public class HottestFragment extends BaseLazyFragment {
    private static final String PARAM_IS_IV_AVATAR_CLICKABLE = "param_is_iv_avatar_clickable";
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.refresh_layout)
    TwinklingRefreshLayout mRefreshLayout;
    private PhotoBagAdapter mAdapter;
    private List<PhotoBagEntity> mData = new ArrayList<>();
    private SinaRefreshView mHeadView;
    private boolean mIsIvAvatarClickable = true;
    private int mCurrentPage = 1;
    private int mPageSize = 5;
    private View mEmptyView;


    public HottestFragment() {

    }

    public static HottestFragment newInstance(boolean isIvAvatarClickable) {
        HottestFragment fragment = new HottestFragment();
        Bundle args = new Bundle();
        args.putBoolean(PARAM_IS_IV_AVATAR_CLICKABLE, isIvAvatarClickable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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
        BmobQuery<PhotoBagEntity> query = new BmobQuery<PhotoBagEntity>();
        //返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(mPageSize);
        if (isPullRefresh) {//下拉刷新
            mCurrentPage = 0;
        }
        int habit = new SPUtils(Constants.SP_NAME).getInt(Constants.SP_SELECT_SEX, -1);
        if (habit != -1 && habit != 0) {
            query.addWhereEqualTo("habit", habit);
        }
        query.setSkip(mCurrentPage * mPageSize);
        query.include("model");
        query.order("collectedNum,seeNum");
        ((BaseActivity) mContext).showDialog("加载模特套图...");
        //执行查询方法
        query.findObjects(new FindListener<PhotoBagEntity>() {

            @Override
            public void done(List<PhotoBagEntity> object, BmobException e) {
                if (e == null) {
                    if (object != null && object.size() > 0) {
                        if (isPullRefresh) {//下拉刷新
                            mData = object;
                        } else {//上拉加载更多
                            mData.addAll(object);
                        }
                        mAdapter.setNewData(mData);
                        mCurrentPage++;
                    } else {
                        if (isPullRefresh) {//下拉刷新
                            mData.clear();
                            mAdapter.setNewData(mData);
                            showMessage(getString(R.string.no_data));
                        } else {//上拉加载更多
                            showMessage(getString(R.string.no_more_data));
                        }
                    }
                } else {
                    ((BaseActivity) mContext).closeDialog();
                    BmobExceptionUtil.handler(e);
                }

                KLog.i();
                ((BaseActivity) mContext).closeDialog();
                if (isPullRefresh) {//下拉刷新
                    mRefreshLayout.finishRefreshing();
                } else {//上拉加载更多
                    mRefreshLayout.finishLoadmore();
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
        getModelList(true);
    }

    @Override
    protected boolean lazyLoad() {
        if (!super.lazyLoad()) {
            if (!isFirst && mContext != null && mContext instanceof HomePageActivity && ((HomePageActivity) mContext).mHottestNeedRefresh) {
                ((HomePageActivity) mContext).mHottestNeedRefresh = false;
                initData();
            }
        }
        return true;
    }

    private void initView() {
        mHeadView = new SinaRefreshView(mContext);
        mHeadView.setArrowResource(R.drawable.ico_pink_arrow);
        mRefreshLayout.setHeaderView(mHeadView);
        mRefreshLayout.setBottomView(new LoadingView(mContext));

        mAdapter = new PhotoBagAdapter(mData);
        mAdapter.isFirstOnly(true);
//        mAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new NoAlphaItemAnimator());

        mEmptyView = LayoutInflater.from(mContext).inflate(R.layout.layout_empty, mRecyclerView, false);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListener() {
        mAdapter.setOnItemChildClickListener((BaseQuickAdapter adapter, View view, int position) -> {
            switch (view.getId()) {
                case R.id.iv_avatar:
                    if (mIsIvAvatarClickable) {
                        Intent intent = new Intent(mContext, ModelDetailActivity.class);
                        intent.putExtra("id", mData.get(position).getModel().getObjectId());
                        startActivity(intent);
                    }
                    break;
                case R.id.ll_collection:
                    if (AppUtil.isUserLogin(mContext, true)) {
                        if (view.isSelected()) {
                            toCancelCollectPhotoBag(view, mData.get(position));
                        } else {
                            toCollectPhotoBag(view, mData.get(position));
                        }
                    }
                    break;
                default:
                    break;
            }
        });
        mAdapter.setOnItemClickListener((BaseQuickAdapter adapter, View view, int position) -> {
            Intent intent = new Intent(mContext, PhotoDetailActivity.class);
            intent.putExtra("id", mData.get(position).getObjectId());
            startActivity(intent);
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

    private void toCancelCollectPhotoBag(View view, PhotoBagEntity photoBagEntity) {
        ((BaseActivity) mContext).showDialog("正在取消收藏...");
        view.setEnabled(false);
        UserEntity user = BmobUser.getCurrentUser(UserEntity.class);
        if (photoBagEntity.getCollectorIdList().contains(user.getObjectId())) {
            photoBagEntity.getCollectorIdList().remove(user.getObjectId());
        }
        photoBagEntity.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
//                    ((BaseActivity) mContext).setDialogMessage("正在收藏2...");
                    //将当前用户添加到Post表中的likes字段值中，表明当前用户喜欢该帖子
                    BmobRelation relation = new BmobRelation();
                    //将当前用户添加到多对多关联中
                    relation.remove(user);
                    //多对多关联指向`post`的`likes`字段
                    photoBagEntity.setCollector(relation);
                    photoBagEntity.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            ((BaseActivity) mContext).closeDialog();
                            view.setEnabled(true);
                            if (e == null) {
                                mAdapter.notifyDataSetChanged();
                            } else {
                                BmobExceptionUtil.handler(e);
                            }
                        }

                    });
                } else {
                    ((BaseActivity) mContext).closeDialog();
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }

    /**
     * 收藏套图
     *
     * @param view
     * @param photoBagEntity
     */
    private void toCollectPhotoBag(View view, PhotoBagEntity photoBagEntity) {
        ((BaseActivity) mContext).showDialog("正在收藏套图...");
        view.setEnabled(false);
        UserEntity user = BmobUser.getCurrentUser(UserEntity.class);
        if (!photoBagEntity.getCollectorIdList().contains(user.getObjectId())) {
            photoBagEntity.getCollectorIdList().add(user.getObjectId());
        }
        photoBagEntity.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
//                    ((BaseActivity) mContext).setDialogMessage("正在收藏2...");
                    //将当前用户添加到Post表中的likes字段值中，表明当前用户喜欢该帖子
                    BmobRelation relation = new BmobRelation();
                    //将当前用户添加到多对多关联中
                    relation.add(user);
                    //多对多关联指向`post`的`likes`字段
                    photoBagEntity.setCollector(relation);
                    photoBagEntity.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            ((BaseActivity) mContext).closeDialog();
                            view.setEnabled(true);
                            if (e == null) {
                                mAdapter.notifyDataSetChanged();
                            } else {
                                BmobExceptionUtil.handler(e);
                            }
                        }

                    });
                } else {
                    ((BaseActivity) mContext).closeDialog();
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TO_LOGIN:
                if (resultCode == Activity.RESULT_OK) {
                    getModelList(true);
                }
                break;
            default:
                break;
        }
    }
}
