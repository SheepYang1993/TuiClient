package me.sheepyang.tuiclient.widget.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.sheepyang.tuiclient.R;


/**
 * Created by SheepYang on 2017/4/24.
 */

public class QDialog extends Dialog implements View.OnClickListener {
    @BindView(R.id.tv_title)
    TextView mTvTitle;
    @BindView(R.id.tv_message)
    TextView mTvMessage;
    private Context mContext;
    private String mTitle;
    private String mMessage;
    private OnClickListener mOnLeftClickListener;
    private OnClickListener mOnRightClickListener;

    public QDialog(@NonNull Context context) {
        super(context, R.style.q_dialog);
        mContext = context;
    }

    public QDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected QDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_message);
        setCanceledOnTouchOutside(true);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTvTitle.setText(mTitle);
        mTvMessage.setText(mMessage);
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

    public void setMessage(String msg) {
        mMessage = msg;
        if (mTvMessage != null) {
            mTvMessage.setText(mMessage);
        }
    }

    public void setTitle(String title) {
        mTitle = title;
        if (mTvTitle != null) {
            mTvTitle.setText(mTitle);
        }
    }

    public void setOnLeftClickListener(OnClickListener onLeftClickListener) {
        mOnLeftClickListener = onLeftClickListener;
    }

    public void setOnRightClickListener(OnClickListener onRightClickListener) {
        mOnRightClickListener = onRightClickListener;
    }

    @Override
    @OnClick({R.id.btn_left, R.id.btn_right})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_left:
                dismiss();
                if (mOnLeftClickListener != null) {
                    dismiss();
                    mOnLeftClickListener.onClick(this, v.getId());
                }
                break;
            case R.id.btn_right:
                dismiss();
                if (mOnRightClickListener != null) {
                    mOnRightClickListener.onClick(this, v.getId());
                }
                break;
            default:
                break;
        }
    }
}