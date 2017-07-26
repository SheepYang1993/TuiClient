package me.sheepyang.tuiclient.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import butterknife.OnClick;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.mine.BuyVIPActivity;
import me.sheepyang.tuiclient.fragment.base.BaseFragment;

/**
 * Created by SheepYang on 2017/4/26.
 */

public class OpenVIPFragment extends BaseFragment implements View.OnClickListener {

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_open_vip;
    }

    @Override
    protected void init() {

    }

    public OpenVIPFragment() {

    }

    public static OpenVIPFragment newInstance() {
        OpenVIPFragment fragment = new OpenVIPFragment();
        Bundle args = new Bundle();
//        args.putString(IMAGE_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            mPath = getArguments().getString(IMAGE_PATH);
        }
    }

    @Override
    @OnClick({R.id.rl_open_vip})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_open_vip:
                startActivity(new Intent(mContext, BuyVIPActivity.class));
                break;
            default:
                break;
        }
    }
}
