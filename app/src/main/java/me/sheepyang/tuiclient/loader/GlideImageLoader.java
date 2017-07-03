package me.sheepyang.tuiclient.loader;

import android.content.Context;
import android.widget.ImageView;

import com.youth.banner.loader.ImageLoader;

import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.utils.GlideApp;


public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //具体方法内容自己去选择，次方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择
        GlideApp.with(context.getApplicationContext())
                .load(path)
                .centerCrop()
                .placeholder(R.drawable.ico_loading2)
                .error(R.drawable.ico_error_avatar_white)
                .into(imageView);
    }
}
