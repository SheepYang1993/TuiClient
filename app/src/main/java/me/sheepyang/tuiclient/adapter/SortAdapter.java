package me.sheepyang.tuiclient.adapter;


import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.model.bmobentity.ImageTypeEntity;

/**
 * Created by Administrator on 2017/4/19.
 */

public class SortAdapter extends BaseQuickAdapter<ImageTypeEntity, BaseViewHolder> {
    private int mScreenWidth;
    private RequestOptions mOptions;

    public SortAdapter(List<ImageTypeEntity> data) {
        super(R.layout.item_sort, data);
        mOptions = new RequestOptions()
                .placeholder(R.drawable.ico_loading2)
                .error(R.drawable.ico_error_avatar_white)
                .centerCrop();
        mScreenWidth = ScreenUtils.getScreenWidth();
    }

    @Override
    protected void convert(BaseViewHolder helper, ImageTypeEntity item) {
        ViewGroup.LayoutParams lp = helper.getView(R.id.iv_photo).getLayoutParams();
        lp.width = mScreenWidth / 2;
        lp.height = (int) (lp.width * 0.75);
        helper.getView(R.id.iv_photo).setLayoutParams(lp);

        helper.setText(R.id.tv_name, item.getName());
        helper.setText(R.id.tv_photo_num, item.getNum() + "");

        if (item.getPic() != null && !TextUtils.isEmpty(item.getPic().getFileUrl())) {
            Glide.with(mContext)
                    .load(item.getPic().getFileUrl())
                    .apply(mOptions)
                    .into((ImageView) helper.getView(R.id.iv_photo));
        } else {
            Glide.with(mContext)
                    .load("")
                    .apply(mOptions)
                    .into((ImageView) helper.getView(R.id.iv_photo));
        }
    }
}
