package me.sheepyang.tuiclient.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import butterknife.ButterKnife;
import butterknife.OnClick;
import me.sheepyang.tuiclient.R;

/**
 * Created by SheepYang on 2017/4/26.
 */

public class SelectPhotoDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private OnSelectClickListener mOnSelectClickListener;

    public SelectPhotoDialog(@NonNull Context context) {
        super(context, R.style.q_dialog);
        mContext = context;
    }

    public SelectPhotoDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected SelectPhotoDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_select_photo);
        setCanceledOnTouchOutside(true);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.AnimBottom);
        ButterKnife.bind(this);
    }

    @Override
    public void show() {
        super.show();
        WindowManager windowManager = ((Activity) mContext).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = display.getWidth(); // 设置宽度
        this.getWindow().setAttributes(lp);
    }

    @Override
    @OnClick({R.id.btn_camera, R.id.btn_album, R.id.btn_cancel})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera:
                dismiss();
                if (mOnSelectClickListener != null) {
                    mOnSelectClickListener.onCameraClick(this, v.getId());
                }
                break;
            case R.id.btn_album:
                dismiss();
                if (mOnSelectClickListener != null) {
                    mOnSelectClickListener.onAlbumClick(this, v.getId());
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                if (mOnSelectClickListener != null) {
                    mOnSelectClickListener.onCancelClick(this, v.getId());
                }
                break;
            default:
                break;
        }
    }

    public void setOnSelectClickListener(OnSelectClickListener onSelectClickListener) {
        mOnSelectClickListener = onSelectClickListener;
    }

    public interface OnSelectClickListener {
        void onCameraClick(DialogInterface dialog, int which);

        void onAlbumClick(DialogInterface dialog, int which);

        void onCancelClick(DialogInterface dialog, int which);
    }
}
