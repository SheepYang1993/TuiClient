package me.sheepyang.tuiclient.fragment;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

import butterknife.BindView;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.fragment.base.BaseFragment;

/**
 * Created by SheepYang on 2017/4/26.
 */

public class ImageDetailFragment extends BaseFragment {
    private static final String IMAGE_PATH = "image_path";
    @BindView(R.id.photo_view)
    PhotoView mPhotoView;
    private String mPath;
    private OnPhotoTapListener mListener;

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_image_detail;
    }

    @Override
    protected void init() {
        initListener();
        initData();
    }

    private void initListener() {
        mPhotoView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                if (mListener != null) {
                    mListener.onPhotoTap(view, x, y);
                }
                getActivity().onBackPressed();
            }
        });
    }

    private void initData() {
        Glide.with(mContext)
                .load(mPath)
//                .placeholder(R.drawable.anim_loading_view)
//                .error(R.drawable.ico_error_avatar_white)
                .into(mPhotoView);
    }

    public ImageDetailFragment() {

    }

    public static ImageDetailFragment newInstance(String path) {
        ImageDetailFragment fragment = new ImageDetailFragment();
        Bundle args = new Bundle();
        args.putString(IMAGE_PATH, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPath = getArguments().getString(IMAGE_PATH);
        }
    }

    public void setOnPhotoTapListener(OnPhotoTapListener listener) {
        mListener = listener;
    }
}
