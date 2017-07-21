package me.sheepyang.tuiclient.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.math.BigDecimal;
import java.util.List;

import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.model.bmobentity.VIPDetailEntity;

/**
 * Created by SheepYang on 2017-07-14.
 */

public class VIPAdapter extends BaseQuickAdapter<VIPDetailEntity, BaseViewHolder> {

    public VIPAdapter(List<VIPDetailEntity> data) {
        super(R.layout.item_vip_detail, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, VIPDetailEntity item) {

        if (helper.getLayoutPosition() == 0) {
            helper.setVisible(R.id.line, false);
        }
        String vipInFo = item.getName();
        BigDecimal bg = new BigDecimal(item.getRebate());
        double rebate = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (item.getRebate() > 0) {
            vipInFo += " (" + rebate + "折)";
        }
        helper.setText(R.id.tv_vip_druation, vipInFo);
        helper.setVisible(R.id.tv_recommend_state, item.getRecommend());
        helper.setText(R.id.tv_price, "¥" + item.getPrice());
    }
}