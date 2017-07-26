package me.sheepyang.tuiclient.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseRefreshActivity;
import me.sheepyang.tuiclient.activity.photo.ModelDetailActivity;
import me.sheepyang.tuiclient.activity.photo.PhotoDetailActivity;
import me.sheepyang.tuiclient.adapter.PhotoBagAdapter;
import me.sheepyang.tuiclient.model.bmobentity.PhotoBagEntity;
import me.sheepyang.tuiclient.model.bmobentity.UserEntity;
import me.sheepyang.tuiclient.utils.AppUtil;
import me.sheepyang.tuiclient.utils.BmobExceptionUtil;

public class CollectionActivity extends BaseRefreshActivity {

    private List<PhotoBagEntity> mDatas = new ArrayList<>();
    private int mCurrentPage = 1;
    private int mPageSize = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBarTitle("我的收藏");
        getPhotoBagList(true, mRefreshLayout);
    }

    @Override
    protected void startLoadMore(TwinklingRefreshLayout refreshLayout) {
        getPhotoBagList(false, refreshLayout);
    }

    @Override
    protected void startRefresh(TwinklingRefreshLayout refreshLayout) {
        getPhotoBagList(true, refreshLayout);
    }

    private void getPhotoBagList(boolean isPullRefresh, TwinklingRefreshLayout refreshLayout) {
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
        query.addWhereEqualTo("collector", AppUtil.getUser().getObjectId());
        query.setSkip(mCurrentPage * mPageSize);
        query.include("model");
        query.order("collectedNum,seeNum");
        showDialog("玩命加载中...");
        //执行查询方法
        query.findObjects(new FindListener<PhotoBagEntity>() {

            @Override
            public void done(List<PhotoBagEntity> object, BmobException e) {
                closeDialog();
                if (e == null) {
                    if (object != null && object.size() > 0) {
                        if (isPullRefresh) {//下拉刷新
                            mDatas = object;
                        } else {//上拉加载更多
                            mDatas.addAll(object);
                        }
                        mAdapter.setNewData(mDatas);
                        mCurrentPage++;
                    } else {
                        if (isPullRefresh) {//下拉刷新
                            mDatas.clear();
                            mAdapter.setNewData(mDatas);
                            showMessage(getString(R.string.no_data));
                        } else {//上拉加载更多
                            showMessage(getString(R.string.no_more_data));
                        }
                    }
                } else {
                    closeDialog();
                    BmobExceptionUtil.handler(e);
                }

                closeDialog();
                if (isPullRefresh) {//下拉刷新
                    mRefreshLayout.finishRefreshing();
                } else {//上拉加载更多
                    mRefreshLayout.finishLoadmore();
                }
            }
        });
    }

    @Override
    public BaseQuickAdapter initAdapter() {
        return new PhotoBagAdapter(mDatas);
    }

    @Override
    public void initListener() {
        super.initListener();
        mAdapter.setOnItemChildClickListener((BaseQuickAdapter adapter, View view, int position) -> {
            switch (view.getId()) {
                case R.id.iv_avatar:
                    Intent intent = new Intent(mActivity, ModelDetailActivity.class);
                    intent.putExtra("id", mDatas.get(position).getModel().getObjectId());
                    startActivity(intent);
                    break;
                case R.id.ll_collection:
                    if (AppUtil.isUserLogin(mActivity, true)) {
                        if (view.isSelected()) {
                            toCancelCollectPhotoBag(view, mDatas.get(position));
                        } else {
                            toCollectPhotoBag(view, mDatas.get(position));
                        }
                    }
                    break;
                default:
                    break;
            }
        });
        mAdapter.setOnItemClickListener((BaseQuickAdapter adapter, View view, int position) -> {
            Intent intent = new Intent(mActivity, PhotoDetailActivity.class);
            intent.putExtra("id", mDatas.get(position).getObjectId());
            startActivity(intent);
        });
    }

    private void toCancelCollectPhotoBag(View view, PhotoBagEntity photoBagEntity) {
        showDialog("正在取消收藏...");
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
                            closeDialog();
                            view.setEnabled(true);
                            if (e == null) {
                                mAdapter.notifyDataSetChanged();
                            } else {
                                BmobExceptionUtil.handler(e);
                            }
                        }

                    });
                } else {
                    closeDialog();
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
        showDialog("正在收藏套图...");
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
                            closeDialog();
                            view.setEnabled(true);
                            if (e == null) {
                                mAdapter.notifyDataSetChanged();
                            } else {
                                BmobExceptionUtil.handler(e);
                            }
                        }

                    });
                } else {
                    closeDialog();
                    BmobExceptionUtil.handler(e);
                }
            }
        });
    }
}
