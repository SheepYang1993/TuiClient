package me.sheepyang.tuiclient.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.model.bmobentity.PhotoDetailEntity;
import me.sheepyang.tuiclient.utils.GlideApp;
import me.sheepyang.tuiclient.utils.transformation.MyBlurTransformation;

/**
 * Created by SheepYang on 2017-07-14.
 */

public class PhotoDetailAdapter extends BaseQuickAdapter<PhotoDetailEntity, BaseViewHolder> {
    private LinearLayout.LayoutParams mParams;

    public PhotoDetailAdapter(List<PhotoDetailEntity> data) {
        super(R.layout.item_photo_detail, data);
        mParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (ScreenUtils.getScreenWidth() / 2.0));
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoDetailEntity item) {
        helper.getView(R.id.iv_photo).setLayoutParams(mParams);
        if (helper.getLayoutPosition() >= 6) {
            //图片已上锁，模糊图片
            GlideApp.with(mContext)
                    .load(item.getPic().getFileUrl())
                    .transform(new MultiTransformation<>(new CenterCrop(), new MyBlurTransformation(mContext, 10, 10)))
                    .placeholder(R.drawable.ico_loading2)
                    .error(R.drawable.ico_error_avatar_white)
                    .into((ImageView) helper.getView(R.id.iv_photo));
        } else {
            //加载图片
            GlideApp.with(mContext)
                    .load(item.getPic().getFileUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ico_loading2)
                    .error(R.drawable.ico_error_avatar_white)
                    .into((ImageView) helper.getView(R.id.iv_photo));
        }
    }
}