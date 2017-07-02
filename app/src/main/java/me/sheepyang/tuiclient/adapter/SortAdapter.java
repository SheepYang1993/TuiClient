package me.sheepyang.tuiclient.adapter;


import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.blankj.utilcode.util.ScreenUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.app.TApp;
import me.sheepyang.tuiclient.model.bmobentity.ImageTypeEntity;
import me.sheepyang.tuiclient.utils.GlideApp;
import me.sheepyang.tuiclient.utils.transformation.MyBlurTransformation;

/**
 * Created by Administrator on 2017/4/19.
 */

public class SortAdapter extends BaseQuickAdapter<ImageTypeEntity, BaseViewHolder> {
    private int mScreenWidth;
    private MultiTransformation transformation;

    public SortAdapter(List<ImageTypeEntity> data) {
        super(R.layout.item_sort, data);
        mScreenWidth = ScreenUtils.getScreenWidth();
    }

    @Override
    protected void convert(BaseViewHolder helper, ImageTypeEntity item) {
        ViewGroup.LayoutParams lp = helper.getView(R.id.iv_photo).getLayoutParams();
        lp.width = mScreenWidth / 2;
        lp.height = (int) (lp.width * 0.75);
        helper.getView(R.id.iv_photo).setLayoutParams(lp);

        helper.setText(R.id.tv_name, item.getName());
        if (item.getNum() != null) {
            helper.setText(R.id.tv_photo_num, item.getNum() + "");
        } else {
            helper.setText(R.id.tv_photo_num, "0");
        }

        if (item.getBlur()) {
            transformation = new MultiTransformation(new CenterCrop(), new MyBlurTransformation(mContext, 10, 8));
        } else {
            transformation = new MultiTransformation(new CenterCrop());
        }
        if (item.getPic() != null && !TextUtils.isEmpty(item.getPic().getFileUrl())) {
            GlideApp.with(mContext)
                    .load(item.getPic().getFileUrl())
                    .placeholder(R.drawable.ico_loading2)
                    .error(R.drawable.ico_error_avatar_white)
                    .transform(transformation)
                    .into((ImageView) helper.getView(R.id.iv_photo));
        } else {
            GlideApp.with(mContext)
                    .load("")
                    .placeholder(R.drawable.ico_loading2)
                    .error(R.drawable.ico_error_avatar_white)
                    .transform(transformation)
                    .into((ImageView) helper.getView(R.id.iv_photo));
        }
    }
}
