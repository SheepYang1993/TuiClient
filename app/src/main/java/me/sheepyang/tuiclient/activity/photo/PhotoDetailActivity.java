package me.sheepyang.tuiclient.activity.photo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
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
import me.sheepyang.tuiclient.activity.mine.BuyVIPActivity;
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
    private int mPageSize = 14;
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
                    if (mData != null && mData.size() > 0) {
                        if (position > 5) {
                            mHintDialog.show();
                            return;
                        }
                        ArrayList<String> imageList = new ArrayList<String>();
                        String imgPath;
                        PhotoDetailEntity entity;
                        int size = mData.size() < 6 ? mData.size() : 6;
                        for (int i = 0; i < size; i++) {
                            entity = mData.get(i);
                            if (entity != null && entity.getPic() != null && (entity.getBlur() == null || !entity.getBlur())) {
                                imgPath = entity.getPic().getFileUrl();
                                if (!TextUtils.isEmpty(imgPath)) {
                                    imageList.add(imgPath);
                                }
                            }
                        }
                        Intent intent1 = new Intent(mActivity, ImageBrowserActivity.class);
                        intent1.putExtra(ImageBrowserActivity.IMAGE_LIST, imageList);
                        intent1.putExtra(ImageBrowserActivity.POSITION, position);
                        //主要的语句
                        //通过makeSceneTransitionAnimation传入多个Pair
                        //每个Pair将一个当前Activity的View和目标Activity中的一个Key绑定起来
                        //在目标Activity中会调用这个Key
                        ActivityOptionsCompat activityOptions = ActivityOptionsCompat
                                .makeSceneTransitionAnimation(mActivity, view, "image");

                        // ActivityCompat是android支持库中用来适应不同android版本的
                        ActivityCompat.startActivity(mActivity, intent1, activityOptions.toBundle());
                    }
                }
            }
        });
        mHintDialog.setOnRightClickListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(mActivity, BuyVIPActivity.class));
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
