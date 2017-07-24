package me.sheepyang.tuiclient.activity.mine;

import android.os.Bundle;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.socks.library.KLog;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseRefreshActivity;
import me.sheepyang.tuiclient.adapter.PhotoBagAdapter;
import me.sheepyang.tuiclient.model.bmobentity.PhotoBagEntity;
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

                KLog.i();
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
}
