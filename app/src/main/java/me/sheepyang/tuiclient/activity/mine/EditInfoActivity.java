package me.sheepyang.tuiclient.activity.mine;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.socks.library.KLog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import me.sheepyang.tuiclient.R;
import me.sheepyang.tuiclient.activity.base.BaseActivity;
import me.sheepyang.tuiclient.activity.photo.ImageBrowserActivity;
import me.sheepyang.tuiclient.loader.GlideImageLoader;
import me.sheepyang.tuiclient.utils.GlideApp;
import me.sheepyang.tuiclient.widget.QBar;
import me.sheepyang.tuiclient.widget.dialog.SelectPhotoDialog;

import static com.lzy.imagepicker.ImagePicker.REQUEST_CODE_PREVIEW;
import static me.sheepyang.tuiclient.activity.mine.MineActivity.USER_AVATAR;
import static me.sheepyang.tuiclient.activity.mine.MineActivity.USER_NICK_NAME;
import static me.sheepyang.tuiclient.activity.photo.ImageBrowserActivity.IMAGE_LIST;

public class EditInfoActivity extends BaseActivity implements View.OnClickListener {

    private static final int REQUEST_TAKE_PICKERS = 1;
    private static final int TO_EDIT_NAME = 0x0015;
    @BindView(R.id.q_bar)
    QBar mQBar;
    @BindView(R.id.iv_avatar)
    ImageView mIvAvatar;
    @BindView(R.id.tv_name)
    TextView mTvName;
    private SelectPhotoDialog mSelectPhotoDialog;
    private ArrayList<String> mImageList = new ArrayList<>();
    private String mUserNickName;
    private String mAvatarPath;

    @Override
    public int setLayoutId() {
        return R.layout.activity_edit_info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserNickName = getIntent().getStringExtra(USER_NICK_NAME);
        mAvatarPath = getIntent().getStringExtra(USER_AVATAR);
        initView();
        initImagePicker();
        initListener();
        initData();
    }

    private void initData() {
        if (!TextUtils.isEmpty(mAvatarPath)) {
            GlideApp.with(mActivity)
                    .load(mAvatarPath)
                    .circleCrop()
                    .placeholder(R.drawable.anim_loading_view)
                    .into(mIvAvatar);
        }
        if (!TextUtils.isEmpty(mUserNickName)) {
            mTvName.setText(mUserNickName);
        }
    }

    private void initView() {
        mSelectPhotoDialog = new SelectPhotoDialog(mActivity);
    }

    private void initListener() {
        mQBar.setOnRightClickListener((View v) -> {
            onBackPressed();
        });
        mSelectPhotoDialog.setOnSelectClickListener(new SelectPhotoDialog.OnSelectClickListener() {
            @Override
            public void onCameraClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(mActivity, ImageGridActivity.class);
                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                startActivityForResult(intent, REQUEST_TAKE_PICKERS);
            }

            @Override
            public void onAlbumClick(DialogInterface dialog, int which) {
                Intent intent1 = new Intent(mActivity, ImageGridActivity.class);
                startActivityForResult(intent1, REQUEST_TAKE_PICKERS);
            }

            @Override
            public void onCancelClick(DialogInterface dialog, int which) {

            }
        });
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(false);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(1);              //选中数量限制
        imagePicker.setMultiMode(false);
        imagePicker.setStyle(CropImageView.Style.CIRCLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    @Override
    @OnClick({R.id.iv_avatar, R.id.rl_edit_avatar, R.id.ll_edit_password, R.id.ll_edit_name})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_edit_avatar:
                mSelectPhotoDialog.show();
                break;
            case R.id.ll_edit_password:
                startActivity(new Intent(mActivity, EditPasswordActivity.class));
                break;
            case R.id.ll_edit_name://修改昵称
                Intent intent = new Intent(mActivity, EditNameActivity.class);
                if (!TextUtils.isEmpty(mUserNickName)) {
                    intent.putExtra(USER_NICK_NAME, mUserNickName);
                }
                startActivityForResult(intent, TO_EDIT_NAME);
                break;
            case R.id.iv_avatar:
                if (mImageList != null && mImageList.size() > 0) {
                    //打开预览
                    Intent intentPreview = new Intent(this, ImageBrowserActivity.class);
                    intentPreview.putExtra(IMAGE_LIST, mImageList);
                    startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                } else {
                    mSelectPhotoDialog.show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        KLog.i();
        switch (requestCode) {
            case TO_EDIT_NAME:
                if (resultCode == RESULT_OK && data != null) {
                    String name = data.getStringExtra(EditNameActivity.KEY_NAME);
                    if (!TextUtils.isEmpty(name)) {
                        mUserNickName = name;
                        mTvName.setText(name);
                        setResult(RESULT_OK);
                    }
                }
                break;
            case REQUEST_TAKE_PICKERS:
//                //添加图片返回
//                if (data != null && resultCode == ImagePicker.RESULT_CODE_ITEMS) {
//                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
//                    if (images != null && images.size() > 0) {
//                        mImageList.clear();
//                        for (ImageItem item : images) {
//                            mImageList.add(item.path);
//                        }
//                        Luban.compress(mActivity, new File(mImageList.get(0)))
//                                .setMaxSize(300)
//                                .setMaxWidth(300)
//                                .setMaxHeight(300)
//                                .launch(new OnCompressListener() {
//                                    @Override
//                                    public void onStart() {
//                                        showDialog("正在上传头像...");
//                                    }
//
//                                    @Override
//                                    public void onSuccess(File file) {
//                                        BmobFile bmobFile = new BmobFile(new File(picPath));
//                                        bmobFile.uploadblock(new UploadFileListener() {
//
//                                            @Override
//                                            public void done(BmobException e) {
//                                                if(e==null){
//                                                    //bmobFile.getFileUrl()--返回的上传文件的完整地址
//                                                    toast("上传文件成功:" + bmobFile.getFileUrl());
//                                                }else{
//                                                    toast("上传文件失败：" + e.getMessage());
//                                                }
//
//                                            }
//
//                                            @Override
//                                            public void onProgress(Integer value) {
//                                                // 返回的上传进度（百分比）
//                                            }
//                                        });
//                                        OkGo.post(Api.UPLOAD_USER_LOGO)
//                                                .tag(this)
//                                                .params("myFile", file)
//                                                .params("fileName", "这是我的头像")
//                                                .execute(new JsonCallback<UploadFileResponse>() {
//                                                    @Override
//                                                    public void onSuccess(UploadFileResponse uploadFileResponse, Call call, Response response) {
//                                                        if (uploadFileResponse != null && uploadFileResponse.isTrue(mContext) && !TextUtils.isEmpty(uploadFileResponse.getData())) {
//                                                            KLog.i("SheepYang", Api.LOAD_IMAGE + uploadFileResponse.getData());
//                                                            Glide.with(mContext)
//                                                                    .load(Api.LOAD_IMAGE + uploadFileResponse.getData())
//                                                                    .transform(new GlideCircleTransform(mContext))
//                                                                    .placeholder(R.drawable.anim_loading_view)
//                                                                    .into(mIvAvatar);
//                                                            setResult(RESULT_OK);
//                                                        }
//                                                    }
//                                                });
//                                    }
//
//                                    @Override
//                                    public void onError(Throwable e) {
//                                        showMessage("图片压缩失败...");
//                                    }
//                                });
//                    }
//                }
                break;
            default:
                break;
        }
    }
}
