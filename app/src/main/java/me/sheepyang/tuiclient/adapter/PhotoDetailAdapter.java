package me.sheepyang.tuiclient.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.model.bmobentity.PhotoDetailEntity;

/**
 * Created by SheepYang on 2017-07-14.
 */

public class PhotoDetailAdapter extends BaseQuickAdapter<PhotoDetailEntity, BaseViewHolder> {
    public PhotoDetailAdapter(List<PhotoDetailEntity> data) {
        super(R.layout.item_sort, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoDetailEntity item) {

    }
}
