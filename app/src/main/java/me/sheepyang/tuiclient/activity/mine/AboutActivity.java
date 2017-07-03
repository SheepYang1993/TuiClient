package me.sheepyang.tuiclient.activity.mine;

import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;


public class AboutActivity extends BaseActivity {

    @BindView(R.id.tv_desc)
    TextView mTvDesc;
    private final String mDesc = "关于我们：\n\n" +
            "推倒—专注于打造美女帅哥的精美写真分享平台，提供用户与模特的100种沟通姿势，推倒心仪的TA。\n\n" +
            "我们有高清美图，二次元、萌宠、宅男福利、腐女最爱，每日更新。\n\n" +
            "客服微信号：si918136。";

    @Override
    public int setLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTvDesc.setText(mDesc);
    }
}
