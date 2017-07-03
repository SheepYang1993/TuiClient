package me.sheepyang.tuiclient.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.sheepyang.tuiclient.R;


/**
 * Created by SheepYang on 2017-07-03.
 */

public class PhotoPackageAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public PhotoPackageAdapter(List<String> data) {
        super(R.layout.item_photo_package, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {

    }
}
