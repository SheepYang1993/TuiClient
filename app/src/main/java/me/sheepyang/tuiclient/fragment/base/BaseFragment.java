package me.sheepyang.tuiclient.fragment.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ToastUtils;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import me.sheepyang.tuiclient.app.TApp;

/**
 * Created by Administrator on 2017/4/19.
 */

public abstract class BaseFragment extends Fragment {
    public Unbinder unbinder;
    public View mRootView;
    public Context mContext;
    public TApp mTApp;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mTApp = (TApp) ((Activity) context).getApplication();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(setLayoutId(), container, false);
            unbinder = ButterKnife.bind(this, mRootView);
            init();
        }
        ViewGroup parent = (ViewGroup) mRootView.getParent();
        if (parent != null) {
            parent.removeView(mRootView);
        }
        return mRootView;
    }

    protected abstract
    @LayoutRes
    int
    setLayoutId();

    protected abstract void init();

    public void showToast(String msg) {
        ToastUtils.showShortToast(msg);
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
