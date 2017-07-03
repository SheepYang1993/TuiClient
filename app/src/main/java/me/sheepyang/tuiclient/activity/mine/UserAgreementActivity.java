package me.sheepyang.tuiclient.activity.mine;

import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;


public class UserAgreementActivity extends BaseActivity {

    @BindView(R.id.tv_desc)
    TextView mTvDesc;
    private final String mDesc = "·推倒APP提供的所有服务不含有令人反感或讨厌的内容。\n\n" +
            "·鉴于推倒APP是一个面向服务提供者和服务接受者的O2O服务平台，平台客服团队会对所有提供的服务进行审核。\n\n" +
            "·推倒APP本身不对服务提供者在平台上提供的服务负责，亦不对服务提供者在推倒APP上提供的服务作任何明示或默示的担保。\n\n" +
            "·服务接受者明确同意其使用本网站服务所存在的风险将完全由其自己承担；因其使用本网站服务而产生的一切后果也由其自己承担，推倒APP不对服务接受者承担任何责任。\n\n" +
            "·对由于服务提供者不正当使用推倒APP服务、或依据其在推倒APP中提供的信息进行交易引起的对任何第三方的损害不承担任何赔偿责任。服务提供者应对前述对第三方的损害进行完全的赔偿。\n\n" +
            "·推倒APP中包含的内容及提供的服务不含任何明示性或暗示性的声明、保证或条件，包括但不限于关于真实性、适销性或适用于某一特定用途的明示或暗示性的声明、保证或条件，或者对其使用不会被中断或无误。\n\n" +
            "·不声明或保证推倒APP或可从推倒APP下载的内容不带有计算机病毒或类似垃圾或破坏功能。\n\n" +
            "·不担保推倒APP中提供的平台服务一定能满足服务提供者或服务接受者的要求，亦不对服务提供者所发布信息的删除或储存失败负责。\n\n" +
            "·若已经明示推倒APP运营方式发生变更并提醒服务提供者或服务接受者应当注意事项，服务提供者或服务接受者未按要求操作所产生的一切后果由服务提供者或服务接受者自行承担。\n\n" +
            "·服务提供者及服务接受者确认其知悉，在使用推倒APP提供的平台服务中存在有来自任何其他人的包括威胁性的、诽谤性的、令人反感的或非法的内容或行为或对他人权利的侵犯(包括肖像权和知识产权)的匿名或冒名的信息的风险，服务提供者及服务接受者须承担以上风险，对因此导致任何因服务提供者或服务接受者不正当或非法使用服务产生的直接、间接、偶然、惩罚性的损害，不承担任何责任。\n\n" +
            "·因服务提供者上传到推倒APP中的内容违法或侵犯第三人的合法权益而导致推倒APP对第三方承担任何性质的赔偿、补偿或罚款而遭受损失(直接的、间接的、偶然的、惩罚性的损失)，服务提供者对于上述损失承担完全的赔偿责任。\n\n" +
            "·如果用户在推倒APP注册、发表图片和言论等活动时存在违反中华人民共和国法律或者网站政策的情况，经核实后有权终止向其服务并追究相关法律责任。\n\n" +
            "·服务接受者同意接收来自推倒APP或委托的第三方发送的推荐信息（包括短信、微信的推送）。\n\n";

    @Override
    public int setLayoutId() {
        return R.layout.activity_user_agreement;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTvDesc.setText(mDesc);
    }
}
